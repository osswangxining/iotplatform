package org.iotp.analytics.ruleengine.core.plugin.messaging;

import java.util.UUID;

import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Data;

/**
 */
@Data
class PendingRpcRequestMetadata {
  private final UUID uid;
  private final int requestId;
  private final TenantId tenantId;
  private final RuleId ruleId;
  private final CustomerId customerId;
  private final DeviceId deviceId;
}
