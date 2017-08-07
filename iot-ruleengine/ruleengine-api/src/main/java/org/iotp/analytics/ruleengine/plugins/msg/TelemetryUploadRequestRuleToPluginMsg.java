package org.iotp.analytics.ruleengine.plugins.msg;

import org.iotp.analytics.ruleengine.common.msg.core.TelemetryUploadRequest;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

public class TelemetryUploadRequestRuleToPluginMsg extends AbstractRuleToPluginMsg<TelemetryUploadRequest> {

  private static final long serialVersionUID = 1L;
  private final long ttl;

  public TelemetryUploadRequestRuleToPluginMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId,
      TelemetryUploadRequest payload, long ttl) {
    super(tenantId, customerId, deviceId, payload);
    this.ttl = ttl;
  }

  public long getTtl() {
    return ttl;
  }
}
