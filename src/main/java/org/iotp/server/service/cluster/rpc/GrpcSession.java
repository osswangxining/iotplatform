package org.iotp.server.service.cluster.rpc;

import java.io.Closeable;
import java.util.UUID;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.server.gen.cluster.ClusterAPIProtos;

import io.grpc.stub.StreamObserver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Data
@Slf4j
final public class GrpcSession implements Closeable {
  private final UUID sessionId;
  private final boolean client;
  private final GrpcSessionListener listener;
  private StreamObserver<ClusterAPIProtos.ToRpcServerMessage> inputStream;
  private StreamObserver<ClusterAPIProtos.ToRpcServerMessage> outputStream;

  private boolean connected;
  private ServerAddress remoteServer;

  public GrpcSession(GrpcSessionListener listener) {
    this(null, listener);
  }

  public GrpcSession(ServerAddress remoteServer, GrpcSessionListener listener) {
    this.sessionId = UUID.randomUUID();
    this.listener = listener;
    if (remoteServer != null) {
      this.client = true;
      this.connected = true;
      this.remoteServer = remoteServer;
    } else {
      this.client = false;
    }
  }

  public void initInputStream() {
    this.inputStream = new StreamObserver<ClusterAPIProtos.ToRpcServerMessage>() {
      @Override
      public void onNext(ClusterAPIProtos.ToRpcServerMessage msg) {
        if (!connected) {
          if (msg.hasConnectMsg()) {
            connected = true;
            ClusterAPIProtos.ServerAddress rpcAddress = msg.getConnectMsg().getServerAddress();
            remoteServer = new ServerAddress(rpcAddress.getHost(), rpcAddress.getPort());
            listener.onConnected(GrpcSession.this);
          }
        }
        if (connected) {
          if (msg.hasToPluginRpcMsg()) {
            listener.onToPluginRpcMsg(GrpcSession.this, msg.getToPluginRpcMsg());
          }
          if (msg.hasToDeviceActorRpcMsg()) {
            listener.onToDeviceActorRpcMsg(GrpcSession.this, msg.getToDeviceActorRpcMsg());
          }
          if (msg.hasToDeviceSessionActorRpcMsg()) {
            listener.onToDeviceSessionActorRpcMsg(GrpcSession.this, msg.getToDeviceSessionActorRpcMsg());
          }
          if (msg.hasToDeviceActorNotificationRpcMsg()) {
            listener.onToDeviceActorNotificationRpcMsg(GrpcSession.this, msg.getToDeviceActorNotificationRpcMsg());
          }
          if (msg.hasToDeviceRpcRequestRpcMsg()) {
            listener.onToDeviceRpcRequestRpcMsg(GrpcSession.this, msg.getToDeviceRpcRequestRpcMsg());
          }
          if (msg.hasToPluginRpcResponseRpcMsg()) {
            listener.onFromDeviceRpcResponseRpcMsg(GrpcSession.this, msg.getToPluginRpcResponseRpcMsg());
          }
          if (msg.hasToAllNodesRpcMsg()) {
            listener.onToAllNodesRpcMessage(GrpcSession.this, msg.getToAllNodesRpcMsg());
          }
        }
      }

      @Override
      public void onError(Throwable t) {
        listener.onError(GrpcSession.this, t);
      }

      @Override
      public void onCompleted() {
        outputStream.onCompleted();
        listener.onDisconnected(GrpcSession.this);
      }
    };
  }

  public void initOutputStream() {
    if (client) {
      listener.onConnected(GrpcSession.this);
    }
  }

  public void sendMsg(ClusterAPIProtos.ToRpcServerMessage msg) {
    outputStream.onNext(msg);
  }

  public void onError(Throwable t) {
    outputStream.onError(t);
  }

  @Override
  public void close() {
    try {
      outputStream.onCompleted();
    } catch (IllegalStateException e) {
      log.debug("[{}] Failed to close output stream: {}", sessionId, e.getMessage());
    }
  }
}
