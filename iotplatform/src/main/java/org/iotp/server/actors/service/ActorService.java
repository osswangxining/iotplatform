package org.iotp.server.actors.service;

import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.plugin.ComponentLifecycleEvent;
import org.iotp.server.service.cluster.discovery.DiscoveryServiceListener;
import org.iotp.server.service.cluster.rpc.RpcMsgListener;
import org.iotp.server.transport.SessionMsgProcessor;

public interface ActorService
    extends SessionMsgProcessor, WebSocketMsgProcessor, RestMsgProcessor, RpcMsgListener, DiscoveryServiceListener {

  void onPluginStateChange(TenantId tenantId, PluginId pluginId, ComponentLifecycleEvent state);

  void onRuleStateChange(TenantId tenantId, RuleId ruleId, ComponentLifecycleEvent state);

  void onCredentialsUpdate(TenantId tenantId, DeviceId deviceId);

  void onCredentialsUpdate(TenantId tenantId, AssetId assetId);

  void onDeviceNameOrTypeUpdate(TenantId tenantId, DeviceId deviceId, String deviceName, String deviceType);
}
