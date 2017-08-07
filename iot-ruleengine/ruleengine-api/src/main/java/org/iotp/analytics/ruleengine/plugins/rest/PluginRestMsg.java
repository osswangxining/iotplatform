package org.iotp.analytics.ruleengine.plugins.rest;

import org.iotp.analytics.ruleengine.api.plugins.PluginApiCallSecurityContext;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginActorMsg;
import org.iotp.infomgt.data.id.PluginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

@SuppressWarnings("rawtypes")
public interface PluginRestMsg extends ToPluginActorMsg {

  RestRequest getRequest();

  DeferredResult<ResponseEntity> getResponseHolder();

  PluginApiCallSecurityContext getSecurityCtx();

  @Override
  default PluginId getPluginId() {
    return getSecurityCtx().getPluginId();
  }

}
