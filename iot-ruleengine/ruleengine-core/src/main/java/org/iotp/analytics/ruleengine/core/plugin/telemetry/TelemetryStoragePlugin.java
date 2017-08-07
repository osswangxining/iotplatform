package org.iotp.analytics.ruleengine.core.plugin.telemetry;

import org.iotp.analytics.ruleengine.annotation.EmptyComponentConfiguration;
import org.iotp.analytics.ruleengine.annotation.Plugin;
import org.iotp.analytics.ruleengine.api.plugins.AbstractPlugin;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RestMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RpcMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.WebsocketMsgHandler;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.core.action.telemetry.TelemetryPluginAction;
import org.iotp.analytics.ruleengine.core.plugin.telemetry.handlers.TelemetryRestMsgHandler;
import org.iotp.analytics.ruleengine.core.plugin.telemetry.handlers.TelemetryRpcMsgHandler;
import org.iotp.analytics.ruleengine.core.plugin.telemetry.handlers.TelemetryRuleMsgHandler;
import org.iotp.analytics.ruleengine.core.plugin.telemetry.handlers.TelemetryWebsocketMsgHandler;

import lombok.extern.slf4j.Slf4j;

@Plugin(name = "Telemetry Plugin", actions = { TelemetryPluginAction.class })
@Slf4j
public class TelemetryStoragePlugin extends AbstractPlugin<EmptyComponentConfiguration> {

  private final TelemetryRestMsgHandler restMsgHandler;
  private final TelemetryRuleMsgHandler ruleMsgHandler;
  private final TelemetryWebsocketMsgHandler websocketMsgHandler;
  private final TelemetryRpcMsgHandler rpcMsgHandler;
  private final SubscriptionManager subscriptionManager;

  public TelemetryStoragePlugin() {
    this.subscriptionManager = new SubscriptionManager();
    this.restMsgHandler = new TelemetryRestMsgHandler(subscriptionManager);
    this.ruleMsgHandler = new TelemetryRuleMsgHandler(subscriptionManager);
    this.websocketMsgHandler = new TelemetryWebsocketMsgHandler(subscriptionManager);
    this.rpcMsgHandler = new TelemetryRpcMsgHandler(subscriptionManager);
    this.subscriptionManager.setWebsocketHandler(this.websocketMsgHandler);
    this.subscriptionManager.setRpcHandler(this.rpcMsgHandler);
  }

  @Override
  public void init(EmptyComponentConfiguration configuration) {

  }

  @Override
  protected RestMsgHandler getRestMsgHandler() {
    return restMsgHandler;
  }

  @Override
  protected RuleMsgHandler getRuleMsgHandler() {
    return ruleMsgHandler;
  }

  @Override
  protected WebsocketMsgHandler getWebsocketMsgHandler() {
    return websocketMsgHandler;
  }

  @Override
  protected RpcMsgHandler getRpcMsgHandler() {
    return rpcMsgHandler;
  }

  @Override
  public void onServerAdded(PluginContext ctx, ServerAddress server) {
    subscriptionManager.onClusterUpdate(ctx);
  }

  @Override
  public void onServerRemoved(PluginContext ctx, ServerAddress server) {
    subscriptionManager.onClusterUpdate(ctx);
  }

  @Override
  public void resume(PluginContext ctx) {
    log.info("Plugin activated!");
  }

  @Override
  public void suspend(PluginContext ctx) {
    log.info("Plugin suspended!");
  }

  @Override
  public void stop(PluginContext ctx) {
    subscriptionManager.clear();
    websocketMsgHandler.clear(ctx);
    log.info("Plugin stopped!");
  }
}
