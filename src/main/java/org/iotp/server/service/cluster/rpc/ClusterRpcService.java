package org.iotp.server.service.cluster.rpc;

import java.util.UUID;

import org.iotp.analytics.ruleengine.api.asset.ToAssetActorNotificationMsg;
import org.iotp.analytics.ruleengine.api.device.ToDeviceActorNotificationMsg;
import org.iotp.analytics.ruleengine.common.msg.asset.ToAssetActorMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.cluster.ToAllNodesMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequestPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginRpcResponseDeviceMsg;
import org.iotp.analytics.ruleengine.plugins.rpc.PluginRpcMsg;
import org.iotp.server.gen.cluster.ClusterAPIProtos;

import io.grpc.stub.StreamObserver;

/**
 */
public interface ClusterRpcService {

  void init(RpcMsgListener listener);

  void tell(ServerAddress serverAddress, ToDeviceActorMsg toForward);

  void tell(ServerAddress serverAddress, ToAssetActorMsg toForward);

  void tell(ServerAddress serverAddress, ToDeviceSessionActorMsg toForward);

  void tell(ServerAddress serverAddress, ToDeviceActorNotificationMsg toForward);

  void tell(ServerAddress serverAddress, ToAssetActorNotificationMsg toForward);

  void tell(ServerAddress serverAddress, ToDeviceRpcRequestPluginMsg toForward);

  void tell(ServerAddress serverAddress, ToPluginRpcResponseDeviceMsg toForward);

  void tell(PluginRpcMsg toForward);

  void broadcast(ToAllNodesMsg msg);

  void onSessionCreated(UUID msgUid, StreamObserver<ClusterAPIProtos.ToRpcServerMessage> inputStream);
}
