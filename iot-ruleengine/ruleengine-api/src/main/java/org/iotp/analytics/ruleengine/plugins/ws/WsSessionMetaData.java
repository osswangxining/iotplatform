package org.iotp.analytics.ruleengine.plugins.ws;

public class WsSessionMetaData {

  private PluginWebsocketSessionRef sessionRef;
  private long lastActivityTime;

  public WsSessionMetaData(PluginWebsocketSessionRef sessionRef) {
    super();
    this.sessionRef = sessionRef;
    this.lastActivityTime = System.currentTimeMillis();
  }

  public PluginWebsocketSessionRef getSessionRef() {
    return sessionRef;
  }

  public void setSessionRef(PluginWebsocketSessionRef sessionRef) {
    this.sessionRef = sessionRef;
  }

  public long getLastActivityTime() {
    return lastActivityTime;
  }

  public void setLastActivityTime(long lastActivityTime) {
    this.lastActivityTime = lastActivityTime;
  }

  @Override
  public String toString() {
    return "WsSessionMetaData [sessionRef=" + sessionRef + ", lastActivityTime=" + lastActivityTime + "]";
  }

}
