package org.iotp.server.actors.plugin;

import org.iotp.analytics.ruleengine.api.plugins.Plugin;
import org.iotp.analytics.ruleengine.api.plugins.PluginInitializationException;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.plugins.msg.FromDeviceRpcResponse;
import org.iotp.analytics.ruleengine.plugins.msg.TimeoutMsg;
import org.iotp.analytics.ruleengine.plugins.rest.PluginRestMsg;
import org.iotp.analytics.ruleengine.plugins.rpc.PluginRpcMsg;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketMsg;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.plugin.ComponentLifecycleState;
import org.iotp.infomgt.data.plugin.ComponentType;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.shared.ComponentMsgProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.event.LoggingAdapter;

/**
 */
public class PluginActorMessageProcessor extends ComponentMsgProcessor<PluginId> {

  private final SharedPluginProcessingContext pluginCtx;
  private final PluginProcessingContext trustedCtx;
  private PluginMetaData pluginMd;
  private Plugin pluginImpl;
  private ComponentLifecycleState state;

  protected PluginActorMessageProcessor(TenantId tenantId, PluginId pluginId, ActorSystemContext systemContext,
      LoggingAdapter logger, ActorRef parent, ActorRef self) {
    super(systemContext, logger, tenantId, pluginId);
    this.pluginCtx = new SharedPluginProcessingContext(systemContext, tenantId, pluginId, parent, self);
    this.trustedCtx = new PluginProcessingContext(pluginCtx, null);
  }

  @Override
  public void start() throws Exception {
    logger.info("[{}] Going to start plugin actor.", entityId);
    pluginMd = systemContext.getPluginService().findPluginById(entityId);
    if (pluginMd == null) {
      throw new PluginInitializationException("Plugin not found!");
    }
    if (pluginMd.getConfiguration() == null) {
      throw new PluginInitializationException("Plugin metadata is empty!");
    }
    state = pluginMd.getState();
    if (state == ComponentLifecycleState.ACTIVE) {
      logger.info("[{}] Plugin is active. Going to initialize plugin.", entityId);
      initComponent();
    } else {
      logger.info("[{}] Plugin is suspended. Skipping plugin initialization.", entityId);
    }
  }

  @Override
  public void stop() throws Exception {
    onStop();
  }

  private void initComponent() {
    try {
      pluginImpl = initComponent(pluginMd.getClazz(), ComponentType.PLUGIN,
          mapper.writeValueAsString(pluginMd.getConfiguration()));
    } catch (InstantiationException e) {
      throw new PluginInitializationException("No default constructor for plugin implementation!", e);
    } catch (IllegalAccessException e) {
      throw new PluginInitializationException("Illegal Access Exception during plugin initialization!", e);
    } catch (ClassNotFoundException e) {
      throw new PluginInitializationException("Plugin Class not found!", e);
    } catch (JsonProcessingException e) {
      throw new PluginInitializationException("Plugin Configuration is invalid!", e);
    } catch (Exception e) {
      throw new PluginInitializationException(e.getMessage(), e);
    }
  }

  public void onRuleToPluginMsg(RuleToPluginMsgWrapper msg) throws RuleException {
    if (state == ComponentLifecycleState.ACTIVE) {
      pluginImpl.process(trustedCtx, msg.getRuleTenantId(), msg.getRuleId(), msg.getMsg());
    } else {
      // TODO: reply with plugin suspended message
    }
  }

  public void onWebsocketMsg(PluginWebsocketMsg<?> msg) {
    if (state == ComponentLifecycleState.ACTIVE) {
      pluginImpl.process(new PluginProcessingContext(pluginCtx, msg.getSecurityCtx()), msg);
    } else {
      // TODO: reply with plugin suspended message
    }
  }

  public void onRestMsg(PluginRestMsg msg) {
    if (state == ComponentLifecycleState.ACTIVE) {
      pluginImpl.process(new PluginProcessingContext(pluginCtx, msg.getSecurityCtx()), msg);
    }
  }

  public void onRpcMsg(PluginRpcMsg msg) {
    if (state == ComponentLifecycleState.ACTIVE) {
      pluginImpl.process(trustedCtx, msg.getRpcMsg());
    } else {
      // TODO: reply with plugin suspended message
    }
  }

  public void onPluginCallbackMsg(PluginCallbackMessage msg) {
    if (state == ComponentLifecycleState.ACTIVE) {
      if (msg.isSuccess()) {
        msg.getCallback().onSuccess(trustedCtx, msg.getV());
      } else {
        msg.getCallback().onFailure(trustedCtx, msg.getE());
      }
    } else {
      // TODO: reply with plugin suspended message
    }
  }

  public void onTimeoutMsg(ActorContext context, TimeoutMsg<?> msg) {
    if (state == ComponentLifecycleState.ACTIVE) {
      pluginImpl.process(trustedCtx, msg);
    }
  }

  public void onDeviceRpcMsg(FromDeviceRpcResponse response) {
    if (state == ComponentLifecycleState.ACTIVE) {
      pluginImpl.process(trustedCtx, response);
    }
  }

  @Override
  public void onClusterEventMsg(ClusterEventMsg msg) {
    if (state == ComponentLifecycleState.ACTIVE) {
      ServerAddress address = msg.getServerAddress();
      if (msg.isAdded()) {
        logger.debug("[{}] Going to process server add msg: {}", entityId, address);
        pluginImpl.onServerAdded(trustedCtx, address);
      } else {
        logger.debug("[{}] Going to process server remove msg: {}", entityId, address);
        pluginImpl.onServerRemoved(trustedCtx, address);
      }
    }
  }

  @Override
  public void onCreated(ActorContext context) {
    logger.info("[{}] Going to process onCreated plugin.", entityId);
  }

  @Override
  public void onUpdate(ActorContext context) throws Exception {
    PluginMetaData oldPluginMd = pluginMd;
    pluginMd = systemContext.getPluginService().findPluginById(entityId);
    boolean requiresRestart = false;
    logger.info("[{}] Plugin configuration was updated from {} to {}.", entityId, oldPluginMd, pluginMd);
    if (!oldPluginMd.getClazz().equals(pluginMd.getClazz())) {
      logger.info("[{}] Plugin requires restart due to clazz change from {} to {}.", entityId, oldPluginMd.getClazz(),
          pluginMd.getClazz());
      requiresRestart = true;
    } else if (!oldPluginMd.getConfiguration().equals(pluginMd.getConfiguration())) {
      logger.info("[{}] Plugin requires restart due to configuration change from {} to {}.", entityId,
          oldPluginMd.getConfiguration(), pluginMd.getConfiguration());
      requiresRestart = true;
    }
    if (requiresRestart) {
      this.state = ComponentLifecycleState.SUSPENDED;
      if (pluginImpl != null) {
        pluginImpl.stop(trustedCtx);
      }
      start();
    }
  }

  @Override
  public void onStop(ActorContext context) {
    onStop();
    scheduleMsgWithDelay(context, new PluginTerminationMsg(entityId), systemContext.getPluginActorTerminationDelay());
  }

  private void onStop() {
    logger.info("[{}] Going to process onStop plugin.", entityId);
    this.state = ComponentLifecycleState.SUSPENDED;
    if (pluginImpl != null) {
      pluginImpl.stop(trustedCtx);
    }
  }

  @Override
  public void onActivate(ActorContext context) throws Exception {
    logger.info("[{}] Going to process onActivate plugin.", entityId);
    this.state = ComponentLifecycleState.ACTIVE;
    if (pluginImpl != null) {
      pluginImpl.resume(trustedCtx);
      logger.info("[{}] Plugin resumed.", entityId);
    } else {
      start();
    }
  }

  @Override
  public void onSuspend(ActorContext context) {
    logger.info("[{}] Going to process onSuspend plugin.", entityId);
    this.state = ComponentLifecycleState.SUSPENDED;
    if (pluginImpl != null) {
      pluginImpl.suspend(trustedCtx);
    }
  }

}
