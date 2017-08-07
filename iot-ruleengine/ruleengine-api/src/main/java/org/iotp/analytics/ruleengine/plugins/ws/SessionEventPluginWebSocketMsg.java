package org.iotp.analytics.ruleengine.plugins.ws;

public class SessionEventPluginWebSocketMsg extends AbstractPluginWebSocketMsg<SessionEvent> {

  private static final long serialVersionUID = 1L;

  public SessionEventPluginWebSocketMsg(PluginWebsocketSessionRef sessionRef, SessionEvent payload) {
    super(sessionRef, payload);
  }

}
