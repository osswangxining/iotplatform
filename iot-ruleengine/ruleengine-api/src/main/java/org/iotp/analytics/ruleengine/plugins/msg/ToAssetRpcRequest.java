package org.iotp.analytics.ruleengine.plugins.msg;

import java.io.Serializable;
import java.util.UUID;

import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Data;

/**
 */
@Data
public class ToAssetRpcRequest implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 4808030606746583547L;
  private final UUID id;
  private final TenantId tenantId;
  private final DeviceId deviceId;
  private final boolean oneway;
  private final long expirationTime;
  private final ToDeviceRpcRequestBody body;
}
