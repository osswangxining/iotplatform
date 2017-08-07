package org.iotp.analytics.ruleengine.action.plugins.webhook.action;

import java.util.Optional;

import org.iotp.analytics.ruleengine.annotation.Action;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceRequestMsg;
import org.iotp.analytics.ruleengine.core.action.template.AbstractTemplatePluginAction;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Action(name = "Webhook Plugin Action",
        descriptor = "WebhookActionDescriptor.json", configuration = WebhookPluginActionConfiguration.class)
@Slf4j
public class WebhookPluginAction extends AbstractTemplatePluginAction<WebhookPluginActionConfiguration> {

    @Override
    protected Optional<RuleToPluginMsg<?>> buildRuleToPluginMsg(RuleContext ctx, ToDeviceActorMsg msg, FromDeviceRequestMsg payload) {
        WebhookActionPayload.WebhookActionPayloadBuilder builder = WebhookActionPayload.builder();
        builder.msgType(payload.getMsgType());
        builder.requestId(payload.getRequestId());
        builder.sync(configuration.isSync());
        builder.actionPath(configuration.getActionPath());
        builder.httpMethod(HttpMethod.valueOf(configuration.getRequestMethod()));
        builder.expectedResultCode(HttpStatus.valueOf(configuration.getExpectedResultCode()));
        builder.msgBody(getMsgBody(ctx, msg));
        return Optional.of(new WebhookActionMsg(msg.getTenantId(),
                msg.getCustomerId(),
                msg.getDeviceId(),
                builder.build()));
    }

}
