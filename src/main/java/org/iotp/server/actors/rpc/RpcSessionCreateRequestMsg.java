package org.iotp.server.actors.rpc;

import java.util.UUID;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.server.gen.cluster.ClusterAPIProtos;

import io.grpc.stub.StreamObserver;
import lombok.Data;

/**
 */
@Data
public final class RpcSessionCreateRequestMsg {

  private final UUID msgUid;
  private final ServerAddress remoteAddress;
  private final StreamObserver<ClusterAPIProtos.ToRpcServerMessage> responseObserver;

}
