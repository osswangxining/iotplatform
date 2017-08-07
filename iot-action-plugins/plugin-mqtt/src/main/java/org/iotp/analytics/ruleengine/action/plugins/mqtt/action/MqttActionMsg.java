package org.iotp.analytics.ruleengine.action.plugins.mqtt.action;

import org.iotp.analytics.ruleengine.plugins.msg.AbstractRuleToPluginMsg;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

public class MqttActionMsg extends AbstractRuleToPluginMsg<MqttActionPayload> {

  public MqttActionMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId, MqttActionPayload payload) {
    super(tenantId, customerId, deviceId, payload);
  }
}
