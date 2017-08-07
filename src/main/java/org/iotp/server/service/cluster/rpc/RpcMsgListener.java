package org.iotp.server.service.cluster.rpc;

import org.iotp.analytics.ruleengine.api.asset.ToAssetActorNotificationMsg;
import org.iotp.analytics.ruleengine.api.device.ToDeviceActorNotificationMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ToAllNodesMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginActorMsg;
import org.iotp.server.actors.rpc.RpcBroadcastMsg;
import org.iotp.server.actors.rpc.RpcSessionCreateRequestMsg;
import org.iotp.server.actors.rpc.RpcSessionTellMsg;

/**
 */
public interface RpcMsgListener {

  void onMsg(ToDeviceActorMsg msg);

  void onMsg(ToDeviceActorNotificationMsg msg);

  void onMsg(ToAssetActorNotificationMsg msg);

  void onMsg(ToDeviceSessionActorMsg msg);

  void onMsg(ToAllNodesMsg nodeMsg);

  void onMsg(ToPluginActorMsg msg);

  void onMsg(RpcSessionCreateRequestMsg msg);

  void onMsg(RpcSessionTellMsg rpcSessionTellMsg);

  void onMsg(RpcBroadcastMsg rpcBroadcastMsg);

}
