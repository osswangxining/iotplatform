package org.iotp.server.service.cluster.rpc;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PreDestroy;

import org.iotp.analytics.ruleengine.api.asset.ToAssetActorNotificationMsg;
import org.iotp.analytics.ruleengine.api.device.ToDeviceActorNotificationMsg;
import org.iotp.analytics.ruleengine.common.msg.asset.ToAssetActorMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.cluster.ToAllNodesMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.plugins.msg.FromDeviceRpcResponse;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequest;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequestPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginRpcResponseDeviceMsg;
import org.iotp.analytics.ruleengine.plugins.rpc.PluginRpcMsg;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.server.actors.rpc.RpcBroadcastMsg;
import org.iotp.server.actors.rpc.RpcSessionCreateRequestMsg;
import org.iotp.server.actors.rpc.RpcSessionTellMsg;
import org.iotp.server.gen.cluster.ClusterAPIProtos;
import org.iotp.server.gen.cluster.ClusterRpcServiceGrpc;
import org.iotp.server.service.cluster.discovery.ServerInstance;
import org.iotp.server.service.cluster.discovery.ServerInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import com.google.protobuf.ByteString;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Service
@Slf4j
public class ClusterGrpcService extends ClusterRpcServiceGrpc.ClusterRpcServiceImplBase implements ClusterRpcService {

  @Autowired
  private ServerInstanceService instanceService;

  private RpcMsgListener listener;

  private Server server;

  private ServerInstance instance;

  private ConcurrentMap<UUID, RpcSessionCreationFuture> pendingSessionMap = new ConcurrentHashMap<>();

  public void init(RpcMsgListener listener) {
    this.listener = listener;
    log.info("Initializing RPC service!");
    instance = instanceService.getSelf();
    server = ServerBuilder.forPort(instance.getPort()).addService(this).build();
    log.info("Going to start RPC server using port: {}", instance.getPort());
    try {
      server.start();
    } catch (IOException e) {
      log.error("Failed to start RPC server!", e);
      throw new RuntimeException("Failed to start RPC server!");
    }
    log.info("RPC service initialized!");
  }

  @Override
  public void onSessionCreated(UUID msgUid, StreamObserver<ClusterAPIProtos.ToRpcServerMessage> msg) {
    RpcSessionCreationFuture future = pendingSessionMap.remove(msgUid);
    if (future != null) {
      try {
        future.onMsg(msg);
      } catch (InterruptedException e) {
        log.warn("Failed to report created session!");
      }
    } else {
      log.warn("Failed to lookup pending session!");
    }
  }

  @Override
  public StreamObserver<ClusterAPIProtos.ToRpcServerMessage> handlePluginMsgs(
      StreamObserver<ClusterAPIProtos.ToRpcServerMessage> responseObserver) {
    log.info("Processing new session.");
    return createSession(new RpcSessionCreateRequestMsg(UUID.randomUUID(), null, responseObserver));
  }

  @PreDestroy
  public void stop() {
    if (server != null) {
      log.info("Going to onStop RPC server");
      server.shutdownNow();
      try {
        server.awaitTermination();
        log.info("RPC server stopped!");
      } catch (InterruptedException e) {
        log.warn("Failed to onStop RPC server!");
      }
    }
  }

  @Override
  public void tell(ServerAddress serverAddress, ToDeviceActorMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToDeviceActorRpcMsg(toProtoMsg(toForward)).build();
    tell(serverAddress, msg);
  }

  @Override
  public void tell(ServerAddress serverAddress, ToAssetActorMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToAssetActorRpcMsg(toProtoMsg(toForward)).build();
    tell(serverAddress, msg);
  }

  @Override
  public void tell(ServerAddress serverAddress, ToDeviceActorNotificationMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToDeviceActorNotificationRpcMsg(toProtoMsg(toForward)).build();
    tell(serverAddress, msg);
  }

  @Override
  public void tell(ServerAddress serverAddress, ToAssetActorNotificationMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToAssetActorNotificationRpcMsg(toProtoMsg(toForward)).build();
    tell(serverAddress, msg);
  }

  @Override
  public void tell(ServerAddress serverAddress, ToDeviceRpcRequestPluginMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToDeviceRpcRequestRpcMsg(toProtoMsg(toForward)).build();
    tell(serverAddress, msg);
  }

  @Override
  public void tell(ServerAddress serverAddress, ToPluginRpcResponseDeviceMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToPluginRpcResponseRpcMsg(toProtoMsg(toForward)).build();
    tell(serverAddress, msg);
  }

  @Override
  public void tell(ServerAddress serverAddress, ToDeviceSessionActorMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToDeviceSessionActorRpcMsg(toProtoMsg(toForward)).build();
    tell(serverAddress, msg);
  }

  @Override
  public void tell(PluginRpcMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToPluginRpcMsg(toProtoMsg(toForward)).build();
    tell(toForward.getRpcMsg().getServerAddress(), msg);
  }

  @Override
  public void broadcast(ToAllNodesMsg toForward) {
    ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setToAllNodesRpcMsg(toProtoMsg(toForward)).build();
    listener.onMsg(new RpcBroadcastMsg(msg));
  }

  private void tell(ServerAddress serverAddress, ClusterAPIProtos.ToRpcServerMessage msg) {
    listener.onMsg(new RpcSessionTellMsg(serverAddress, msg));
  }

  private StreamObserver<ClusterAPIProtos.ToRpcServerMessage> createSession(RpcSessionCreateRequestMsg msg) {
    RpcSessionCreationFuture future = new RpcSessionCreationFuture();
    pendingSessionMap.put(msg.getMsgUid(), future);
    listener.onMsg(msg);
    try {
      StreamObserver<ClusterAPIProtos.ToRpcServerMessage> observer = future.get();
      log.info("Processed new session.");
      return observer;
    } catch (Exception e) {
      log.info("Failed to process session.", e);
      throw new RuntimeException(e);
    }
  }

  private static ClusterAPIProtos.ToDeviceActorRpcMessage toProtoMsg(ToDeviceActorMsg msg) {
    return ClusterAPIProtos.ToDeviceActorRpcMessage.newBuilder()
        .setData(ByteString.copyFrom(SerializationUtils.serialize(msg))).build();
  }

  private static ClusterAPIProtos.ToAssetActorRpcMessage toProtoMsg(ToAssetActorMsg msg) {
    return ClusterAPIProtos.ToAssetActorRpcMessage.newBuilder()
        .setData(ByteString.copyFrom(SerializationUtils.serialize(msg))).build();
  }

  private static ClusterAPIProtos.ToDeviceActorNotificationRpcMessage toProtoMsg(ToDeviceActorNotificationMsg msg) {
    return ClusterAPIProtos.ToDeviceActorNotificationRpcMessage.newBuilder()
        .setData(ByteString.copyFrom(SerializationUtils.serialize(msg))).build();
  }

  private static ClusterAPIProtos.ToAssetActorNotificationRpcMessage toProtoMsg(ToAssetActorNotificationMsg msg) {
    return ClusterAPIProtos.ToAssetActorNotificationRpcMessage.newBuilder()
        .setData(ByteString.copyFrom(SerializationUtils.serialize(msg))).build();
  }

  private static ClusterAPIProtos.ToDeviceRpcRequestRpcMessage toProtoMsg(ToDeviceRpcRequestPluginMsg msg) {
    ClusterAPIProtos.ToDeviceRpcRequestRpcMessage.Builder builder = ClusterAPIProtos.ToDeviceRpcRequestRpcMessage
        .newBuilder();
    ToDeviceRpcRequest request = msg.getMsg();

    builder.setAddress(ClusterAPIProtos.PluginAddress.newBuilder().setTenantId(toUid(msg.getPluginTenantId().getId()))
        .setPluginId(toUid(msg.getPluginId().getId())).build());

    builder.setDeviceTenantId(toUid(msg.getTenantId()));
    builder.setDeviceId(toUid(msg.getDeviceId()));

    builder.setMsgId(toUid(request.getId()));
    builder.setOneway(request.isOneway());
    builder.setExpTime(request.getExpirationTime());
    builder.setMethod(request.getBody().getMethod());
    builder.setParams(request.getBody().getParams());

    return builder.build();
  }

  private static ClusterAPIProtos.ToPluginRpcResponseRpcMessage toProtoMsg(ToPluginRpcResponseDeviceMsg msg) {
    ClusterAPIProtos.ToPluginRpcResponseRpcMessage.Builder builder = ClusterAPIProtos.ToPluginRpcResponseRpcMessage
        .newBuilder();
    FromDeviceRpcResponse request = msg.getResponse();

    builder.setAddress(ClusterAPIProtos.PluginAddress.newBuilder().setTenantId(toUid(msg.getPluginTenantId().getId()))
        .setPluginId(toUid(msg.getPluginId().getId())).build());

    builder.setMsgId(toUid(request.getId()));
    request.getResponse().ifPresent(builder::setResponse);
    request.getError().ifPresent(e -> builder.setError(e.name()));

    return builder.build();
  }

  private ClusterAPIProtos.ToAllNodesRpcMessage toProtoMsg(ToAllNodesMsg msg) {
    return ClusterAPIProtos.ToAllNodesRpcMessage.newBuilder()
        .setData(ByteString.copyFrom(SerializationUtils.serialize(msg))).build();
  }

  private ClusterAPIProtos.ToPluginRpcMessage toProtoMsg(PluginRpcMsg msg) {
    return ClusterAPIProtos.ToPluginRpcMessage.newBuilder().setClazz(msg.getRpcMsg().getMsgClazz())
        .setData(ByteString.copyFrom(msg.getRpcMsg().getMsgData()))
        .setAddress(ClusterAPIProtos.PluginAddress.newBuilder().setTenantId(toUid(msg.getPluginTenantId().getId()))
            .setPluginId(toUid(msg.getPluginId().getId())).build())
        .build();
  }

  private static ClusterAPIProtos.Uid toUid(EntityId uuid) {
    return toUid(uuid.getId());
  }

  private static ClusterAPIProtos.Uid toUid(UUID uuid) {
    return ClusterAPIProtos.Uid.newBuilder().setPluginUuidMsb(uuid.getMostSignificantBits())
        .setPluginUuidLsb(uuid.getLeastSignificantBits()).build();
  }

  private static ClusterAPIProtos.ToDeviceSessionActorRpcMessage toProtoMsg(ToDeviceSessionActorMsg msg) {
    return ClusterAPIProtos.ToDeviceSessionActorRpcMessage.newBuilder()
        .setData(ByteString.copyFrom(SerializationUtils.serialize(msg))).build();
  }

}
