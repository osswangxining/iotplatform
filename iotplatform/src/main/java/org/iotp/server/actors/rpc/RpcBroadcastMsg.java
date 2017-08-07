package org.iotp.server.actors.rpc;

import org.iotp.server.gen.cluster.ClusterAPIProtos;

import lombok.Data;

/**
 */
@Data
public final class RpcBroadcastMsg {
  private final ClusterAPIProtos.ToRpcServerMessage msg;
}
