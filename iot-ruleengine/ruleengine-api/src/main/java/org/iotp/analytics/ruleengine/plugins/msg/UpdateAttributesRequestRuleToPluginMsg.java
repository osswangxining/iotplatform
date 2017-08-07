package org.iotp.analytics.ruleengine.plugins.msg;

import org.iotp.analytics.ruleengine.common.msg.core.UpdateAttributesRequest;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

public class UpdateAttributesRequestRuleToPluginMsg extends AbstractRuleToPluginMsg<UpdateAttributesRequest> {

  private static final long serialVersionUID = 1L;

  public UpdateAttributesRequestRuleToPluginMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId,
      UpdateAttributesRequest payload) {
    super(tenantId, customerId, deviceId, payload);
  }

}
