package org.iotp.server.actors.rpc;

import java.io.Serializable;
import java.util.UUID;

import org.iotp.analytics.ruleengine.api.device.ToDeviceActorNotificationMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.cluster.ToAllNodesMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.plugins.msg.FromDeviceRpcResponse;
import org.iotp.analytics.ruleengine.plugins.msg.RpcError;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequest;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequestBody;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequestPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginRpcResponseDeviceMsg;
import org.iotp.analytics.ruleengine.plugins.rpc.PluginRpcMsg;
import org.iotp.analytics.ruleengine.plugins.rpc.RpcMsg;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.ActorService;
import org.iotp.server.gen.cluster.ClusterAPIProtos;
import org.iotp.server.service.cluster.rpc.GrpcSession;
import org.iotp.server.service.cluster.rpc.GrpcSessionListener;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import akka.actor.ActorRef;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class BasicRpcSessionListener implements GrpcSessionListener {

  private final ActorSystemContext context;
  private final ActorService service;
  private final ActorRef manager;
  private final ActorRef self;

  public BasicRpcSessionListener(ActorSystemContext context, ActorRef manager, ActorRef self) {
    this.context = context;
    this.service = context.getActorService();
    this.manager = manager;
    this.self = self;
  }

  @Override
  public void onConnected(GrpcSession session) {
    log.info("{} session started -> {}", getType(session), session.getRemoteServer());
    if (!session.isClient()) {
      manager.tell(new RpcSessionConnectedMsg(session.getRemoteServer(), session.getSessionId()), self);
    }
  }

  @Override
  public void onDisconnected(GrpcSession session) {
    log.info("{} session closed -> {}", getType(session), session.getRemoteServer());
    manager.tell(new RpcSessionDisconnectedMsg(session.isClient(), session.getRemoteServer()), self);
  }

  @Override
  public void onToPluginRpcMsg(GrpcSession session, ClusterAPIProtos.ToPluginRpcMessage msg) {
    if (log.isTraceEnabled()) {
      log.trace("{} session [{}] received plugin msg {}", getType(session), session.getRemoteServer(), msg);
    }
    service.onMsg(convert(session.getRemoteServer(), msg));
  }

  @Override
  public void onToDeviceActorRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceActorRpcMessage msg) {
    log.trace("{} session [{}] received device actor msg {}", getType(session), session.getRemoteServer(), msg);
    service.onMsg((ToDeviceActorMsg) deserialize(msg.getData().toByteArray()));
  }

  @Override
  public void onToDeviceActorNotificationRpcMsg(GrpcSession session,
      ClusterAPIProtos.ToDeviceActorNotificationRpcMessage msg) {
    log.trace("{} session [{}] received device actor notification msg {}", getType(session), session.getRemoteServer(),
        msg);
    service.onMsg((ToDeviceActorNotificationMsg) deserialize(msg.getData().toByteArray()));
  }

  @Override
  public void onToDeviceSessionActorRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceSessionActorRpcMessage msg) {
    log.trace("{} session [{}] received session actor msg {}", getType(session), session.getRemoteServer(), msg);
    service.onMsg((ToDeviceSessionActorMsg) deserialize(msg.getData().toByteArray()));
  }

  @Override
  public void onToDeviceRpcRequestRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceRpcRequestRpcMessage msg) {
    log.trace("{} session [{}] received session actor msg {}", getType(session), session.getRemoteServer(), msg);
    service.onMsg(deserialize(session.getRemoteServer(), msg));
  }

  @Override
  public void onFromDeviceRpcResponseRpcMsg(GrpcSession session, ClusterAPIProtos.ToPluginRpcResponseRpcMessage msg) {
    log.trace("{} session [{}] received session actor msg {}", getType(session), session.getRemoteServer(), msg);
    service.onMsg(deserialize(session.getRemoteServer(), msg));
  }

  @Override
  public void onToAllNodesRpcMessage(GrpcSession session, ClusterAPIProtos.ToAllNodesRpcMessage msg) {
    log.trace("{} session [{}] received session actor msg {}", getType(session), session.getRemoteServer(), msg);
    service.onMsg((ToAllNodesMsg) deserialize(msg.getData().toByteArray()));
  }

  @Override
  public void onError(GrpcSession session, Throwable t) {
    log.warn("{} session got error -> {}", getType(session), session.getRemoteServer(), t);
    manager.tell(new RpcSessionClosedMsg(session.isClient(), session.getRemoteServer()), self);
    session.close();
  }

  private static String getType(GrpcSession session) {
    return session.isClient() ? "Client" : "Server";
  }

  private static PluginRpcMsg convert(ServerAddress serverAddress, ClusterAPIProtos.ToPluginRpcMessage msg) {
    ClusterAPIProtos.PluginAddress address = msg.getAddress();
    TenantId tenantId = new TenantId(toUUID(address.getTenantId()));
    PluginId pluginId = new PluginId(toUUID(address.getPluginId()));
    RpcMsg rpcMsg = new RpcMsg(serverAddress, msg.getClazz(), msg.getData().toByteArray());
    return new PluginRpcMsg(tenantId, pluginId, rpcMsg);
  }

  private static UUID toUUID(ClusterAPIProtos.Uid uid) {
    return new UUID(uid.getPluginUuidMsb(), uid.getPluginUuidLsb());
  }

  private static ToDeviceRpcRequestPluginMsg deserialize(ServerAddress serverAddress,
      ClusterAPIProtos.ToDeviceRpcRequestRpcMessage msg) {
    ClusterAPIProtos.PluginAddress address = msg.getAddress();
    TenantId pluginTenantId = new TenantId(toUUID(address.getTenantId()));
    PluginId pluginId = new PluginId(toUUID(address.getPluginId()));

    TenantId deviceTenantId = new TenantId(toUUID(msg.getDeviceTenantId()));
    DeviceId deviceId = new DeviceId(toUUID(msg.getDeviceId()));

    ToDeviceRpcRequestBody requestBody = new ToDeviceRpcRequestBody(msg.getMethod(), msg.getParams());
    ToDeviceRpcRequest request = new ToDeviceRpcRequest(toUUID(msg.getMsgId()), deviceTenantId, deviceId,
        msg.getOneway(), msg.getExpTime(), requestBody);

    return new ToDeviceRpcRequestPluginMsg(serverAddress, pluginId, pluginTenantId, request);
  }

  private static ToPluginRpcResponseDeviceMsg deserialize(ServerAddress serverAddress,
      ClusterAPIProtos.ToPluginRpcResponseRpcMessage msg) {
    ClusterAPIProtos.PluginAddress address = msg.getAddress();
    TenantId pluginTenantId = new TenantId(toUUID(address.getTenantId()));
    PluginId pluginId = new PluginId(toUUID(address.getPluginId()));

    RpcError error = !StringUtils.isEmpty(msg.getError()) ? RpcError.valueOf(msg.getError()) : null;
    FromDeviceRpcResponse response = new FromDeviceRpcResponse(toUUID(msg.getMsgId()), msg.getResponse(), error);
    return new ToPluginRpcResponseDeviceMsg(pluginId, pluginTenantId, response);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Serializable> T deserialize(byte[] data) {
    return (T) SerializationUtils.deserialize(data);
  }

}
