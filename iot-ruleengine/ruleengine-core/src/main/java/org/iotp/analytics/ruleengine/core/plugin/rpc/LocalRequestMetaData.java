package org.iotp.analytics.ruleengine.core.plugin.rpc;

import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.Data;

/**
 */
@Data
public class LocalRequestMetaData {
  private final ToDeviceRpcRequest request;
  private final DeferredResult<ResponseEntity> responseWriter;
}
