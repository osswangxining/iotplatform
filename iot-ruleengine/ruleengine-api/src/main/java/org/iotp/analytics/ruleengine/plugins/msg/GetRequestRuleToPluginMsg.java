package org.iotp.analytics.ruleengine.plugins.msg;

import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

public class GetRequestRuleToPluginMsg extends AbstractRuleToPluginMsg<String[]> {

  private static final long serialVersionUID = 1L;

  public GetRequestRuleToPluginMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId, String[] payload) {
    super(tenantId, customerId, deviceId, payload);
  }

}
