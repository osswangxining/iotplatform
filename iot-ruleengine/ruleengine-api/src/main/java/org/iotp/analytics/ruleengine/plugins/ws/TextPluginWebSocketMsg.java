package org.iotp.analytics.ruleengine.plugins.ws;

public class TextPluginWebSocketMsg extends AbstractPluginWebSocketMsg<String> {

  private static final long serialVersionUID = 1L;

  public TextPluginWebSocketMsg(PluginWebsocketSessionRef sessionRef, String payload) {
    super(sessionRef, payload);
  }

}
