package org.iotp.analytics.ruleengine.plugins.ws;

import org.iotp.analytics.ruleengine.api.plugins.PluginApiCallSecurityContext;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;

public abstract class AbstractPluginWebSocketMsg<T> implements PluginWebsocketMsg<T> {

  private static final long serialVersionUID = 1L;

  private final PluginWebsocketSessionRef sessionRef;
  private final T payload;

  AbstractPluginWebSocketMsg(PluginWebsocketSessionRef sessionRef, T payload) {
    this.sessionRef = sessionRef;
    this.payload = payload;
  }

  public PluginWebsocketSessionRef getSessionRef() {
    return sessionRef;
  }

  @Override
  public TenantId getPluginTenantId() {
    return sessionRef.getPluginTenantId();
  }

  @Override
  public PluginId getPluginId() {
    return sessionRef.getPluginId();
  }

  @Override
  public PluginApiCallSecurityContext getSecurityCtx() {
    return sessionRef.getSecurityCtx();
  }

  public T getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return "AbstractPluginWebSocketMsg [sessionRef=" + sessionRef + ", payload=" + payload + "]";
  }

}
