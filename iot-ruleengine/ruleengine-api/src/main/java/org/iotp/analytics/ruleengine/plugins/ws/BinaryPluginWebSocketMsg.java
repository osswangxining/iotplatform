package org.iotp.analytics.ruleengine.plugins.ws;

import java.nio.ByteBuffer;

public class BinaryPluginWebSocketMsg extends AbstractPluginWebSocketMsg<ByteBuffer> {

  private static final long serialVersionUID = 1L;

  public BinaryPluginWebSocketMsg(PluginWebsocketSessionRef sessionRef, ByteBuffer payload) {
    super(sessionRef, payload);
  }
}
