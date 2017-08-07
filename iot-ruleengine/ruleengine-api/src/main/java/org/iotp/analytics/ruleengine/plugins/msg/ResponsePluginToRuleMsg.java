package org.iotp.analytics.ruleengine.plugins.msg;

import java.util.UUID;

import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

public class ResponsePluginToRuleMsg extends AbstractPluginToRuleMsg<ToDeviceMsg> {

  private static final long serialVersionUID = 1L;

  public ResponsePluginToRuleMsg(UUID uid, TenantId tenantId, RuleId ruleId, ToDeviceMsg payload) {
    super(uid, tenantId, ruleId, payload);
  }

}
