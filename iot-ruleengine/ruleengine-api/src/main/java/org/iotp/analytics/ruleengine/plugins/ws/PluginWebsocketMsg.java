package org.iotp.analytics.ruleengine.plugins.ws;

import java.io.Serializable;

import org.iotp.analytics.ruleengine.api.plugins.PluginApiCallSecurityContext;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginActorMsg;

public interface PluginWebsocketMsg<T> extends ToPluginActorMsg, Serializable {

  PluginWebsocketSessionRef getSessionRef();

  T getPayload();

  PluginApiCallSecurityContext getSecurityCtx();
}
