package org.iotp.analytics.ruleengine.api.device;

import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Data;

/**
 */
@Data
public class DeviceCredentialsUpdateNotificationMsg implements ToDeviceActorNotificationMsg {

  /**
  * 
  */
  private static final long serialVersionUID = -5319801717971879871L;
  private final TenantId tenantId;
  private final DeviceId deviceId;

}
