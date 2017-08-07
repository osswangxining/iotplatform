package org.iotp.analytics.ruleengine.api.device;

import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceNameOrTypeUpdateMsg implements ToDeviceActorNotificationMsg {
  /**
  * 
  */
  private static final long serialVersionUID = 1408169883828954946L;
  private final TenantId tenantId;
  private final DeviceId deviceId;
  private final String deviceName;
  private final String deviceType;
}
