package org.iotp.server.controller.plugin;

import java.io.IOException;

import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketMsg;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketSessionRef;

public interface PluginWebSocketMsgEndpoint {

    void send(PluginWebsocketMsg<?> wsMsg) throws IOException;

    void close(PluginWebsocketSessionRef sessionRef) throws IOException;
}
