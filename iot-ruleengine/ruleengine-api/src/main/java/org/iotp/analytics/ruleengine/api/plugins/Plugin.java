package org.iotp.analytics.ruleengine.api.plugins;

import org.iotp.analytics.ruleengine.annotation.ConfigurableComponent;
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

public interface Plugin<T> extends ConfigurableComponent<T> {

  void process(PluginContext ctx, PluginWebsocketMsg<?> wsMsg);

  void process(PluginContext ctx, TenantId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException;

  void process(PluginContext ctx, PluginRestMsg msg);

  void process(PluginContext ctx, RpcMsg msg);

  void process(PluginContext ctx, FromDeviceRpcResponse msg);

  void process(PluginContext ctx, TimeoutMsg<?> msg);

  void onServerAdded(PluginContext ctx, ServerAddress server);

  void onServerRemoved(PluginContext ctx, ServerAddress server);

  void resume(PluginContext ctx);

  void suspend(PluginContext ctx);

  void stop(PluginContext ctx);

}
