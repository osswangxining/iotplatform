package org.iotp.analytics.ruleengine.api.plugins.handlers;

import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.plugins.rpc.RpcMsg;

/**
 */
public interface RpcMsgHandler {

  void process(PluginContext ctx, RpcMsg msg);

}
