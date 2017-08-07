package org.iotp.analytics.ruleengine.plugins.ws;

import java.nio.ByteBuffer;

public class EmptyPluginWebsocketMsg extends AbstractPluginWebSocketMsg<ByteBuffer> {

  private static final long serialVersionUID = 1L;
  private static ByteBuffer EMPTY = ByteBuffer.wrap(new byte[0]);

  protected EmptyPluginWebsocketMsg(PluginWebsocketSessionRef sessionRef) {
    super(sessionRef, EMPTY);
  }
}
