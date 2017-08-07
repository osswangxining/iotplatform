package org.iotp.analytics.ruleengine.api.plugins.handlers;

import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketMsg;

/**
 */
public interface WebsocketMsgHandler {

  void process(PluginContext ctx, PluginWebsocketMsg<?> wsMsg);

}
