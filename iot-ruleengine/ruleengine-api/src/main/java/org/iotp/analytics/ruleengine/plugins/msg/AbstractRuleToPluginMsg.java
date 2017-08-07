package org.iotp.analytics.ruleengine.plugins.msg;

import java.io.Serializable;
import java.util.UUID;

import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

public abstract class AbstractRuleToPluginMsg<T extends Serializable> implements RuleToPluginMsg<T> {

  private static final long serialVersionUID = 1L;

  private final UUID uid;
  private final TenantId tenantId;
  private final CustomerId customerId;
  private final DeviceId deviceId;
  private final T payload;

  public AbstractRuleToPluginMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId, T payload) {
    super();
    this.uid = UUID.randomUUID();
    this.tenantId = tenantId;
    this.customerId = customerId;
    this.deviceId = deviceId;
    this.payload = payload;
  }

  @Override
  public UUID getUid() {
    return uid;
  }

  public TenantId getTenantId() {
    return tenantId;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  @Override
  public DeviceId getDeviceId() {
    return deviceId;
  }

  public T getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return "AbstractRuleToPluginMsg [uid=" + uid + ", tenantId=" + tenantId + ", customerId=" + customerId
        + ", deviceId=" + deviceId + ", payload=" + payload + "]";
  }

}
