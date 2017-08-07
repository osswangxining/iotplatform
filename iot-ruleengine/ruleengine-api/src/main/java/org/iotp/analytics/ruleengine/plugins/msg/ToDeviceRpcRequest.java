package org.iotp.analytics.ruleengine.plugins.msg;

import java.io.Serializable;
import java.util.UUID;

import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Data;

/**
 */
@Data
public class ToDeviceRpcRequest implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 8094490243720551366L;
  private final UUID id;
  private final TenantId tenantId;
  private final DeviceId deviceId;
  private final boolean oneway;
  private final long expirationTime;
  private final ToDeviceRpcRequestBody body;
}
