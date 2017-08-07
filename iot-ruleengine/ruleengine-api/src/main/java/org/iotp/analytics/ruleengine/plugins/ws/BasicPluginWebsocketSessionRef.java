package org.iotp.analytics.ruleengine.plugins.ws;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

import org.iotp.analytics.ruleengine.api.plugins.PluginApiCallSecurityContext;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;

public class BasicPluginWebsocketSessionRef implements PluginWebsocketSessionRef {

  private static final long serialVersionUID = 1L;

  private final String sessionId;
  private final PluginApiCallSecurityContext securityCtx;
  private final URI uri;
  private final Map<String, Object> attributes;
  private final InetSocketAddress localAddress;
  private final InetSocketAddress remoteAddress;

  public BasicPluginWebsocketSessionRef(String sessionId, PluginApiCallSecurityContext securityCtx, URI uri,
      Map<String, Object> attributes, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
    super();
    this.sessionId = sessionId;
    this.securityCtx = securityCtx;
    this.uri = uri;
    this.attributes = attributes;
    this.localAddress = localAddress;
    this.remoteAddress = remoteAddress;
  }

  public String getSessionId() {
    return sessionId;
  }

  public TenantId getPluginTenantId() {
    return securityCtx.getPluginTenantId();
  }

  public PluginId getPluginId() {
    return securityCtx.getPluginId();
  }

  public URI getUri() {
    return uri;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public InetSocketAddress getLocalAddress() {
    return localAddress;
  }

  public InetSocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BasicPluginWebsocketSessionRef other = (BasicPluginWebsocketSessionRef) obj;
    if (sessionId == null) {
      if (other.sessionId != null)
        return false;
    } else if (!sessionId.equals(other.sessionId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "BasicPluginWebsocketSessionRef [sessionId=" + sessionId + ", pluginId=" + getPluginId() + "]";
  }

  @Override
  public PluginApiCallSecurityContext getSecurityCtx() {
    return securityCtx;
  }

}
