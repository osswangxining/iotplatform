package org.iotp.analytics.ruleengine.core.plugin.rpc.cmd;

import lombok.Data;

/**
 */
@Data
public class RpcRequest {
  private final String methodName;
  private final String requestData;
  private Long timeout;
}
