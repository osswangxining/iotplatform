package org.iotp.server.actors.rpc;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.server.gen.cluster.ClusterAPIProtos;

import lombok.Data;

/**
 */
@Data
public final class RpcSessionTellMsg {
  private final ServerAddress serverAddress;
  private final ClusterAPIProtos.ToRpcServerMessage msg;
}
