package org.iotp.analytics.ruleengine.plugins.ws;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

import org.iotp.analytics.ruleengine.api.plugins.PluginApiCallSecurityContext;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;

public interface PluginWebsocketSessionRef extends Serializable {

  String getSessionId();

  TenantId getPluginTenantId();

  PluginId getPluginId();

  URI getUri();

  Map<String, Object> getAttributes();

  InetSocketAddress getLocalAddress();

  InetSocketAddress getRemoteAddress();

  PluginApiCallSecurityContext getSecurityCtx();

}
