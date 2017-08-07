package org.iotp.analytics.ruleengine.plugins.msg;

import java.util.Optional;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 */
@RequiredArgsConstructor
@ToString
public class FromDeviceRpcResponse {
  @Getter
  private final UUID id;
  private final String response;
  private final RpcError error;

  public Optional<String> getResponse() {
    return Optional.ofNullable(response);
  }

  public Optional<RpcError> getError() {
    return Optional.ofNullable(error);
  }

}
