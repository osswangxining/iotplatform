package org.iotp.analytics.ruleengine.plugins.msg;

import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Data;

/**
 */
@Data
public class ToPluginRpcResponseDeviceMsg implements ToPluginActorMsg {
  private final PluginId pluginId;
  private final TenantId pluginTenantId;
  private final FromDeviceRpcResponse response;
}
