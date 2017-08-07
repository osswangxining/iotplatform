package org.iotp.analytics.ruleengine.core.plugin.rpc;

import org.iotp.analytics.ruleengine.annotation.Plugin;
import org.iotp.analytics.ruleengine.api.plugins.AbstractPlugin;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RestMsgHandler;
import org.iotp.analytics.ruleengine.core.plugin.rpc.handlers.RpcRestMsgHandler;
import org.iotp.analytics.ruleengine.plugins.msg.FromDeviceRpcResponse;
import org.iotp.analytics.ruleengine.plugins.msg.TimeoutMsg;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Plugin(name = "RPC Plugin", actions = {}, descriptor = "RpcPluginDescriptor.json", configuration = RpcPluginConfiguration.class)
@Slf4j
public class RpcPlugin extends AbstractPlugin<RpcPluginConfiguration> {

  private final RpcManager rpcManager;
  private final RpcRestMsgHandler restMsgHandler;

  public RpcPlugin() {
    this.rpcManager = new RpcManager();
    this.restMsgHandler = new RpcRestMsgHandler(rpcManager);
    this.rpcManager.setRestHandler(restMsgHandler);
  }

  @Override
  public void process(PluginContext ctx, FromDeviceRpcResponse msg) {
    rpcManager.process(ctx, msg);
  }

  @Override
  public void process(PluginContext ctx, TimeoutMsg<?> msg) {
    rpcManager.process(ctx, msg);
  }

  @Override
  protected RestMsgHandler getRestMsgHandler() {
    return restMsgHandler;
  }

  @Override
  public void init(RpcPluginConfiguration configuration) {
    restMsgHandler.setDefaultTimeout(configuration.getDefaultTimeout());
  }

  @Override
  public void resume(PluginContext ctx) {

  }

  @Override
  public void suspend(PluginContext ctx) {

  }

  @Override
  public void stop(PluginContext ctx) {

  }
}
