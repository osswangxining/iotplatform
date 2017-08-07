package org.iotp.analytics.ruleengine.plugins.msg;

import org.iotp.analytics.ruleengine.common.msg.core.ToServerRpcRequestMsg;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

public class RpcRequestRuleToPluginMsg extends AbstractRuleToPluginMsg<ToServerRpcRequestMsg> {

  private static final long serialVersionUID = 1L;

  public RpcRequestRuleToPluginMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId,
      ToServerRpcRequestMsg payload) {
    super(tenantId, customerId, deviceId, payload);
  }

}
