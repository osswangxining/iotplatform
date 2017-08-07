package org.iotp.analytics.ruleengine.action.plugins.webhook.action;

import org.iotp.analytics.ruleengine.plugins.msg.AbstractRuleToPluginMsg;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;

public class WebhookActionMsg extends AbstractRuleToPluginMsg<WebhookActionPayload> {

    public WebhookActionMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId, WebhookActionPayload payload) {
        super(tenantId, customerId, deviceId, payload);
    }
}
