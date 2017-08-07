package org.iotp.server.actors.tenant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.iotp.analytics.ruleengine.api.device.ToDeviceActorNotificationMsg;
import org.iotp.analytics.ruleengine.api.rules.ToRuleActorMsg;
import org.iotp.analytics.ruleengine.common.msg.asset.ToAssetActorMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.plugin.ComponentLifecycleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginActorMsg;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.device.DeviceActor;
import org.iotp.server.actors.plugin.PluginTerminationMsg;
import org.iotp.server.actors.rule.ComplexRuleActorChain;
import org.iotp.server.actors.rule.RuleActorChain;
import org.iotp.server.actors.service.ContextAwareActor;
import org.iotp.server.actors.service.ContextBasedCreator;
import org.iotp.server.actors.service.DefaultActorService;
import org.iotp.server.actors.shared.plugin.PluginManager;
import org.iotp.server.actors.shared.plugin.TenantPluginManager;
import org.iotp.server.actors.shared.rule.RuleManager;
import org.iotp.server.actors.shared.rule.TenantRuleManager;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TenantActor extends ContextAwareActor {

  private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

  private final TenantId tenantId;
  private final RuleManager ruleManager;
  private final PluginManager pluginManager;
  private final Map<DeviceId, ActorRef> deviceActors;

  private TenantActor(ActorSystemContext systemContext, TenantId tenantId) {
    super(systemContext);
    this.tenantId = tenantId;
    this.ruleManager = new TenantRuleManager(systemContext, tenantId);
    this.pluginManager = new TenantPluginManager(systemContext, tenantId);
    this.deviceActors = new HashMap<>();
  }

  @Override
  public void preStart() {
    logger.info("[{}] Starting tenant actor.", tenantId);
    try {
      ruleManager.init(this.context());
      pluginManager.init(this.context());
      logger.info("[{}] Tenant actor started.", tenantId);
    } catch (Exception e) {
      logger.error(e, "[{}] Unknown failure", tenantId);
    }
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    logger.debug("[{}] Received message: {}", tenantId, msg);
    if (msg instanceof RuleChainDeviceMsg) {
      process((RuleChainDeviceMsg) msg);
    } else if (msg instanceof ToDeviceActorMsg) {
      onToDeviceActorMsg((ToDeviceActorMsg) msg);
    } else if (msg instanceof ToAssetActorMsg) {
      onToAssetActorMsg((ToAssetActorMsg) msg);
    } else if (msg instanceof ToPluginActorMsg) {
      onToPluginMsg((ToPluginActorMsg) msg);
    } else if (msg instanceof ToRuleActorMsg) {
      onToRuleMsg((ToRuleActorMsg) msg);
    } else if (msg instanceof ToDeviceActorNotificationMsg) {
      onToDeviceActorMsg((ToDeviceActorNotificationMsg) msg);
    } else if (msg instanceof ClusterEventMsg) {
      broadcast(msg);
    } else if (msg instanceof ComponentLifecycleMsg) {
      onComponentLifecycleMsg((ComponentLifecycleMsg) msg);
    } else if (msg instanceof PluginTerminationMsg) {
      onPluginTerminated((PluginTerminationMsg) msg);
    } else {
      logger.warning("[{}] Unknown message: {}!", tenantId, msg);
    }
  }

  private void broadcast(Object msg) {
    pluginManager.broadcast(msg);
    deviceActors.values().forEach(actorRef -> actorRef.tell(msg, ActorRef.noSender()));
  }

  private void onToDeviceActorMsg(ToDeviceActorMsg msg) {
    getOrCreateDeviceActor(msg.getDeviceId()).tell(msg, ActorRef.noSender());
  }

  private void onToAssetActorMsg(ToAssetActorMsg msg) {
    getOrCreateDeviceActor(msg.getAssetId()).tell(msg, ActorRef.noSender());
  }

  private void onToDeviceActorMsg(ToDeviceActorNotificationMsg msg) {
    getOrCreateDeviceActor(msg.getDeviceId()).tell(msg, ActorRef.noSender());
  }

  private void onToRuleMsg(ToRuleActorMsg msg) {
    ActorRef target = ruleManager.getOrCreateRuleActor(this.context(), msg.getRuleId());
    target.tell(msg, ActorRef.noSender());
  }

  private void onToPluginMsg(ToPluginActorMsg msg) {
    if (msg.getPluginTenantId().equals(tenantId)) {
      ActorRef pluginActor = pluginManager.getOrCreatePluginActor(this.context(), msg.getPluginId());
      pluginActor.tell(msg, ActorRef.noSender());
    } else {
      context().parent().tell(msg, ActorRef.noSender());
    }
  }

  private void onComponentLifecycleMsg(ComponentLifecycleMsg msg) {
    if (msg.getPluginId().isPresent()) {
      ActorRef pluginActor = pluginManager.getOrCreatePluginActor(this.context(), msg.getPluginId().get());
      pluginActor.tell(msg, ActorRef.noSender());
    } else if (msg.getRuleId().isPresent()) {
      ActorRef target;
      Optional<ActorRef> ref = ruleManager.update(this.context(), msg.getRuleId().get(), msg.getEvent());
      if (ref.isPresent()) {
        target = ref.get();
      } else {
        logger.debug("Failed to find actor for rule: [{}]", msg.getRuleId());
        return;
      }
      target.tell(msg, ActorRef.noSender());
    } else {
      logger.debug("[{}] Invalid component lifecycle msg.", tenantId);
    }
  }

  private void onPluginTerminated(PluginTerminationMsg msg) {
    pluginManager.remove(msg.getId());
  }

  private void process(RuleChainDeviceMsg msg) {
    ToDeviceActorMsg toDeviceActorMsg = msg.getToDeviceActorMsg();
    if (toDeviceActorMsg == null) {
      return;
    }
    ActorRef deviceActor = getOrCreateDeviceActor(toDeviceActorMsg.getDeviceId());
    RuleActorChain chain = new ComplexRuleActorChain(msg.getRuleChain(), ruleManager.getRuleChain());
    deviceActor.tell(new RuleChainDeviceMsg(toDeviceActorMsg, msg.getToAssetActorMsg(), chain), context().self());
  }

  private ActorRef getOrCreateDeviceActor(DeviceId deviceId) {
    ActorRef deviceActor = deviceActors.get(deviceId);
    if (deviceActor == null) {
      deviceActor = context().actorOf(Props.create(new DeviceActor.ActorCreator(systemContext, tenantId, deviceId))
          .withDispatcher(DefaultActorService.CORE_DISPATCHER_NAME), deviceId.toString());
      deviceActors.put(deviceId, deviceActor);
    }
    return deviceActor;
  }

  public static class ActorCreator extends ContextBasedCreator<TenantActor> {
    private static final long serialVersionUID = 1L;

    private final TenantId tenantId;

    public ActorCreator(ActorSystemContext context, TenantId tenantId) {
      super(context);
      this.tenantId = tenantId;
    }

    @Override
    public TenantActor create() throws Exception {
      return new TenantActor(context, tenantId);
    }
  }

}
