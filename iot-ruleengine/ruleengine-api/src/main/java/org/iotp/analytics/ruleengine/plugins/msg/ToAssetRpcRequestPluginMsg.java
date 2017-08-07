package org.iotp.analytics.ruleengine.plugins.msg;

import java.util.Optional;

import org.iotp.analytics.ruleengine.api.device.ToDeviceActorNotificationMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 */
@ToString
@RequiredArgsConstructor
public class ToAssetRpcRequestPluginMsg implements ToDeviceActorNotificationMsg {

  /**
  * 
  */
  private static final long serialVersionUID = -3503911769658995166L;
  private final ServerAddress serverAddress;
  @Getter
  private final PluginId pluginId;
  @Getter
  private final TenantId pluginTenantId;
  @Getter
  private final ToAssetRpcRequest msg;

  public ToAssetRpcRequestPluginMsg(PluginId pluginId, TenantId pluginTenantId, ToAssetRpcRequest msg) {
    this(null, pluginId, pluginTenantId, msg);
  }

  public Optional<ServerAddress> getServerAddress() {
    return Optional.ofNullable(serverAddress);
  }

  @Override
  public DeviceId getDeviceId() {
    return msg.getDeviceId();
  }

  @Override
  public TenantId getTenantId() {
    return msg.getTenantId();
  }
}
