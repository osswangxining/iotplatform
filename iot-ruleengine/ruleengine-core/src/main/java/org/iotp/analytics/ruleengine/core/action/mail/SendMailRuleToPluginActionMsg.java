package org.iotp.analytics.ruleengine.core.action.mail;

import org.iotp.analytics.ruleengine.plugins.msg.AbstractRuleToPluginMsg;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Data;

/**
 */
@Data
public class SendMailRuleToPluginActionMsg extends AbstractRuleToPluginMsg<SendMailActionMsg> {

  public SendMailRuleToPluginActionMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId,
      SendMailActionMsg payload) {
    super(tenantId, customerId, deviceId, payload);
  }

}
