package org.iotp.server.actors.app;

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
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.dao.tenant.TenantService;
import org.iotp.infomgt.data.Tenant;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.PageDataIterable;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.plugin.PluginTerminationMsg;
import org.iotp.server.actors.service.ContextAwareActor;
import org.iotp.server.actors.service.ContextBasedCreator;
import org.iotp.server.actors.service.DefaultActorService;
import org.iotp.server.actors.shared.plugin.PluginManager;
import org.iotp.server.actors.shared.plugin.SystemPluginManager;
import org.iotp.server.actors.shared.rule.RuleManager;
import org.iotp.server.actors.shared.rule.SystemRuleManager;
import org.iotp.server.actors.tenant.RuleChainDeviceMsg;
import org.iotp.server.actors.tenant.TenantActor;

import akka.actor.ActorRef;
import akka.actor.LocalActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import scala.concurrent.duration.Duration;

public class AppActor extends ContextAwareActor {

  private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

  public static final TenantId SYSTEM_TENANT = new TenantId(ModelConstants.NULL_UUID);
  private final RuleManager ruleManager;
  private final PluginManager pluginManager;
  private final TenantService tenantService;
  private final Map<TenantId, ActorRef> tenantActors;

  private AppActor(ActorSystemContext systemContext) {
    super(systemContext);
    this.ruleManager = new SystemRuleManager(systemContext);
    this.pluginManager = new SystemPluginManager(systemContext);
    this.tenantService = systemContext.getTenantService();
    this.tenantActors = new HashMap<>();
  }

  @Override
  public SupervisorStrategy supervisorStrategy() {
    return strategy;
  }

  @Override
  public void preStart() {
    logger.info("Starting main system actor.");
    try {
      ruleManager.init(this.context());
      pluginManager.init(this.context());

      PageDataIterable<Tenant> tenantIterator = new PageDataIterable<>(link -> tenantService.findTenants(link),
          ENTITY_PACK_LIMIT);
      for (Tenant tenant : tenantIterator) {
        logger.debug("[{}] Creating tenant actor", tenant.getId());
        getOrCreateTenantActor(tenant.getId());
        logger.debug("Tenant actor created.");
      }

      logger.info("Main system actor started.");
    } catch (Exception e) {
      logger.error(e, "Unknown failure");
    }
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    logger.debug("Received message: {}", msg);
    if (msg instanceof ToDeviceActorMsg) {
      processDeviceMsg((ToDeviceActorMsg) msg);
    } else if (msg instanceof ToAssetActorMsg) {
      processAssetMsg((ToAssetActorMsg) msg);
    } else if (msg instanceof ToPluginActorMsg) {
      onToPluginMsg((ToPluginActorMsg) msg);
    } else if (msg instanceof ToRuleActorMsg) {
      onToRuleMsg((ToRuleActorMsg) msg);
    } else if (msg instanceof ToDeviceActorNotificationMsg) {
      onToDeviceActorMsg((ToDeviceActorNotificationMsg) msg);
    } else if (msg instanceof Terminated) {
      processTermination((Terminated) msg);
    } else if (msg instanceof ClusterEventMsg) {
      broadcast(msg);
    } else if (msg instanceof ComponentLifecycleMsg) {
      onComponentLifecycleMsg((ComponentLifecycleMsg) msg);
    } else if (msg instanceof PluginTerminationMsg) {
      onPluginTerminated((PluginTerminationMsg) msg);
    } else {
      logger.warning("Unknown message: {}!", msg);
    }
  }

  private void onPluginTerminated(PluginTerminationMsg msg) {
    pluginManager.remove(msg.getId());
  }

  private void broadcast(Object msg) {
    pluginManager.broadcast(msg);
    tenantActors.values().forEach(actorRef -> actorRef.tell(msg, ActorRef.noSender()));
  }

  private void onToRuleMsg(ToRuleActorMsg msg) {
    ActorRef target;
    if (SYSTEM_TENANT.equals(msg.getTenantId())) {
      target = ruleManager.getOrCreateRuleActor(this.context(), msg.getRuleId());
    } else {
      target = getOrCreateTenantActor(msg.getTenantId());
    }
    target.tell(msg, ActorRef.noSender());
  }

  private void onToPluginMsg(ToPluginActorMsg msg) {
    ActorRef target;
    if (SYSTEM_TENANT.equals(msg.getPluginTenantId())) {
      target = pluginManager.getOrCreatePluginActor(this.context(), msg.getPluginId());
    } else {
      target = getOrCreateTenantActor(msg.getPluginTenantId());
    }
    target.tell(msg, ActorRef.noSender());
  }

  private void onComponentLifecycleMsg(ComponentLifecycleMsg msg) {
    ActorRef target = null;
    if (SYSTEM_TENANT.equals(msg.getTenantId())) {
      if (msg.getPluginId().isPresent()) {
        target = pluginManager.getOrCreatePluginActor(this.context(), msg.getPluginId().get());
      } else if (msg.getRuleId().isPresent()) {
        Optional<ActorRef> ref = ruleManager.update(this.context(), msg.getRuleId().get(), msg.getEvent());
        if (ref.isPresent()) {
          target = ref.get();
        } else {
          logger.debug("Failed to find actor for rule: [{}]", msg.getRuleId());
          return;
        }
      }
    } else {
      target = getOrCreateTenantActor(msg.getTenantId());
    }
    if (target != null) {
      target.tell(msg, ActorRef.noSender());
    }
  }

  private void onToDeviceActorMsg(ToDeviceActorNotificationMsg msg) {
    getOrCreateTenantActor(msg.getTenantId()).tell(msg, ActorRef.noSender());
  }

  private void processDeviceMsg(ToDeviceActorMsg toDeviceActorMsg) {
    TenantId tenantId = toDeviceActorMsg.getTenantId();
    ActorRef tenantActor = getOrCreateTenantActor(tenantId);
    if (toDeviceActorMsg.getPayload().getMsgType().requiresRulesProcessing()) {
      tenantActor.tell(new RuleChainDeviceMsg(toDeviceActorMsg, null, ruleManager.getRuleChain()), context().self());
    } else {
      tenantActor.tell(toDeviceActorMsg, context().self());
    }
  }

  private void processAssetMsg(ToAssetActorMsg toAssetActorMsg) {
    TenantId tenantId = toAssetActorMsg.getTenantId();
    ActorRef tenantActor = getOrCreateTenantActor(tenantId);
    if (toAssetActorMsg.getPayload().getMsgType().requiresRulesProcessing()) {
      tenantActor.tell(new RuleChainDeviceMsg(null, toAssetActorMsg, ruleManager.getRuleChain()), context().self());
    } else {
      tenantActor.tell(toAssetActorMsg, context().self());
    }
  }

  private ActorRef getOrCreateTenantActor(TenantId tenantId) {
    ActorRef tenantActor = tenantActors.get(tenantId);
    if (tenantActor == null) {
      tenantActor = context().actorOf(Props.create(new TenantActor.ActorCreator(systemContext, tenantId))
          .withDispatcher(DefaultActorService.CORE_DISPATCHER_NAME), tenantId.toString());
      tenantActors.put(tenantId, tenantActor);
    }
    return tenantActor;
  }

  private void processTermination(Terminated message) {
    ActorRef terminated = message.actor();
    if (terminated instanceof LocalActorRef) {
      logger.debug("Removed actor: {}", terminated);
    } else {
      throw new IllegalStateException("Remote actors are not supported!");
    }
  }

  public static class ActorCreator extends ContextBasedCreator<AppActor> {
    private static final long serialVersionUID = 1L;

    public ActorCreator(ActorSystemContext context) {
      super(context);
    }

    @Override
    public AppActor create() throws Exception {
      return new AppActor(context);
    }
  }

  private final SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create("1 minute"),
      new Function<Throwable, Directive>() {
        @Override
        public Directive apply(Throwable t) {
          logger.error(t, "Unknown failure");
          if (t instanceof RuntimeException) {
            return SupervisorStrategy.restart();
          } else {
            return SupervisorStrategy.stop();
          }
        }
      });
}
