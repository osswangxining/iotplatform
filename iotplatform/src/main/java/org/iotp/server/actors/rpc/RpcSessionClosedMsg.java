package org.iotp.server.actors.rpc;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;

import lombok.Data;

/**
 */
@Data
public final class RpcSessionClosedMsg {

  private final boolean client;
  private final ServerAddress remoteAddress;
}
