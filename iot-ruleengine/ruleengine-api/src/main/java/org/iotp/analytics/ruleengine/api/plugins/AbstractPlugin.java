package org.iotp.analytics.ruleengine.api.plugins;

import org.iotp.analytics.ruleengine.api.plugins.handlers.DefaultRestMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.DefaultRpcMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.DefaultRuleMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.DefaultWebsocketMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RestMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RpcMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;
import org.iotp.analytics.ruleengine.api.plugins.handlers.WebsocketMsgHandler;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.plugins.msg.FromDeviceRpcResponse;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.TimeoutMsg;
import org.iotp.analytics.ruleengine.plugins.rest.PluginRestMsg;
import org.iotp.analytics.ruleengine.plugins.rpc.RpcMsg;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

/**
 */
public abstract class AbstractPlugin<T> implements Plugin<T> {

  @Override
  public void process(PluginContext ctx, PluginWebsocketMsg<?> wsMsg) {
    getWebsocketMsgHandler().process(ctx, wsMsg);
  }

  @Override
  public void process(PluginContext ctx, PluginRestMsg msg) {
    getRestMsgHandler().process(ctx, msg);
  }

  @Override
  public void process(PluginContext ctx, TenantId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg)
      throws RuleException {
    getRuleMsgHandler().process(ctx, tenantId, ruleId, msg);
  }

  @Override
  public void process(PluginContext ctx, RpcMsg msg) {
    getRpcMsgHandler().process(ctx, msg);
  }

  @Override
  public void process(PluginContext ctx, FromDeviceRpcResponse msg) {
    throw new IllegalStateException("Device RPC messages is not handled in current plugin!");
  }

  @Override
  public void process(PluginContext ctx, TimeoutMsg<?> msg) {
    throw new IllegalStateException("Timeouts is not handled in current plugin!");
  }

  @Override
  public void onServerAdded(PluginContext ctx, ServerAddress server) {
  }

  @Override
  public void onServerRemoved(PluginContext ctx, ServerAddress server) {
  }

  protected RuleMsgHandler getRuleMsgHandler() {
    return new DefaultRuleMsgHandler();
  }

  protected RestMsgHandler getRestMsgHandler() {
    return new DefaultRestMsgHandler();
  }

  protected WebsocketMsgHandler getWebsocketMsgHandler() {
    return new DefaultWebsocketMsgHandler();
  }

  protected RpcMsgHandler getRpcMsgHandler() {
    return new DefaultRpcMsgHandler();
  }
}
