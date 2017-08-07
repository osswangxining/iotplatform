package org.iotp.server.actors.service;

import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketMsg;

public interface WebSocketMsgProcessor {

  void process(PluginWebsocketMsg<?> msg);

}
