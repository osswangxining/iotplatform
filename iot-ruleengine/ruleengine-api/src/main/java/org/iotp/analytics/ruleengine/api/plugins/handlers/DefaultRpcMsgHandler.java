package org.iotp.analytics.ruleengine.api.plugins.handlers;

import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.plugins.rpc.RpcMsg;

/**
 */
public class DefaultRpcMsgHandler implements RpcMsgHandler {

  @Override
  public void process(PluginContext ctx, RpcMsg msg) {
    throw new RuntimeException("Not registered msg type: " + msg.getMsgClazz() + "!");
  }
}
