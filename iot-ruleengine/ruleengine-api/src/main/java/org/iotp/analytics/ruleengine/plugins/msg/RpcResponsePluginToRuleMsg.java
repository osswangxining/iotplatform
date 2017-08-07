package org.iotp.analytics.ruleengine.plugins.msg;

import java.util.UUID;

import org.iotp.analytics.ruleengine.common.msg.core.ToServerRpcResponseMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

public class RpcResponsePluginToRuleMsg extends AbstractPluginToRuleMsg<ToServerRpcResponseMsg> {

  private static final long serialVersionUID = 1L;

  public RpcResponsePluginToRuleMsg(UUID uid, TenantId tenantId, RuleId ruleId, ToServerRpcResponseMsg payload) {
    super(uid, tenantId, ruleId, payload);
  }

}
