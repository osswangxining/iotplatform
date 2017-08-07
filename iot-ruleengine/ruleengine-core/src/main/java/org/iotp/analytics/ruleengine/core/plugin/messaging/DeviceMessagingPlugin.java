package org.iotp.analytics.ruleengine.core.plugin.messaging;

import org.iotp.analytics.ruleengine.annotation.Plugin;
import org.iotp.analytics.ruleengine.api.plugins.AbstractPlugin;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;
import org.iotp.analytics.ruleengine.core.action.rpc.RpcPluginAction;
import org.iotp.analytics.ruleengine.plugins.msg.FromDeviceRpcResponse;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Plugin(name = "Device Messaging Plugin", actions = {
    RpcPluginAction.class }, descriptor = "DeviceMessagingPluginDescriptor.json", configuration = DeviceMessagingPluginConfiguration.class)
@Slf4j
public class DeviceMessagingPlugin extends AbstractPlugin<DeviceMessagingPluginConfiguration> {

  private DeviceMessagingRuleMsgHandler ruleHandler;

  public DeviceMessagingPlugin() {
    ruleHandler = new DeviceMessagingRuleMsgHandler();
  }

  @Override
  public void init(DeviceMessagingPluginConfiguration configuration) {
    ruleHandler.setConfiguration(configuration);
  }

  @Override
  public void process(PluginContext ctx, FromDeviceRpcResponse msg) {
    ruleHandler.process(ctx, msg);
  }

  @Override
  protected RuleMsgHandler getRuleMsgHandler() {
    return ruleHandler;
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
