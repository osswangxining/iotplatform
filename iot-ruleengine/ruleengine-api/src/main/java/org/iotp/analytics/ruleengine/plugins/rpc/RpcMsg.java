package org.iotp.analytics.ruleengine.plugins.rpc;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 */
@ToString
@RequiredArgsConstructor
public class RpcMsg {
  @Getter
  private final ServerAddress serverAddress;
  @Getter
  private final int msgClazz;
  @Getter
  private final byte[] msgData;
}
