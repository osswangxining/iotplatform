package org.iotp.analytics.ruleengine.plugins.msg;

import org.iotp.analytics.ruleengine.common.msg.core.GetAttributesRequest;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

/**
 */
public class GetAttributesRequestRuleToPluginMsg extends AbstractRuleToPluginMsg<GetAttributesRequest> {

  private static final long serialVersionUID = 1L;

  public GetAttributesRequestRuleToPluginMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId,
      GetAttributesRequest payload) {
    super(tenantId, customerId, deviceId, payload);
  }
}
