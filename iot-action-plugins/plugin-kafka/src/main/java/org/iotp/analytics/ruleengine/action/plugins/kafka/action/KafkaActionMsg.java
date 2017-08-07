package org.iotp.analytics.ruleengine.action.plugins.kafka.action;

import org.iotp.analytics.ruleengine.plugins.msg.AbstractRuleToPluginMsg;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

public class KafkaActionMsg extends AbstractRuleToPluginMsg<KafkaActionPayload> {

  /**
   * 
   */
  private static final long serialVersionUID = 8694628377154825078L;

  public KafkaActionMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId, KafkaActionPayload payload) {
    super(tenantId, customerId, deviceId, payload);
  }
}
