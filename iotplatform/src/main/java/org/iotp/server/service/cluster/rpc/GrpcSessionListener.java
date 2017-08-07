package org.iotp.server.service.cluster.rpc;

import org.iotp.server.gen.cluster.ClusterAPIProtos;

/**
 */
public interface GrpcSessionListener {

  void onConnected(GrpcSession session);

  void onDisconnected(GrpcSession session);

  void onToPluginRpcMsg(GrpcSession session, ClusterAPIProtos.ToPluginRpcMessage msg);

  void onToDeviceActorRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceActorRpcMessage msg);

  void onToDeviceActorNotificationRpcMsg(GrpcSession grpcSession,
      ClusterAPIProtos.ToDeviceActorNotificationRpcMessage msg);

  void onToDeviceSessionActorRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceSessionActorRpcMessage msg);

  void onToAllNodesRpcMessage(GrpcSession grpcSession, ClusterAPIProtos.ToAllNodesRpcMessage toAllNodesRpcMessage);

  void onToDeviceRpcRequestRpcMsg(GrpcSession grpcSession,
      ClusterAPIProtos.ToDeviceRpcRequestRpcMessage toDeviceRpcRequestRpcMsg);

  void onFromDeviceRpcResponseRpcMsg(GrpcSession grpcSession,
      ClusterAPIProtos.ToPluginRpcResponseRpcMessage toPluginRpcResponseRpcMsg);

  void onError(GrpcSession session, Throwable t);

}
