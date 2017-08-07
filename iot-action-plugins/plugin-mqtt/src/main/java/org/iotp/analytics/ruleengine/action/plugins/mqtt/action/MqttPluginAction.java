package org.iotp.analytics.ruleengine.action.plugins.mqtt.action;

import java.util.Optional;

import org.iotp.analytics.ruleengine.annotation.Action;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceRequestMsg;
import org.iotp.analytics.ruleengine.core.action.template.AbstractTemplatePluginAction;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;

@Action(name = "Mqtt Plugin Action", descriptor = "MqttActionDescriptor.json", configuration = MqttPluginActionConfiguration.class)
public class MqttPluginAction extends AbstractTemplatePluginAction<MqttPluginActionConfiguration> {

  @Override
  protected Optional<RuleToPluginMsg<?>> buildRuleToPluginMsg(RuleContext ctx, ToDeviceActorMsg msg,
      FromDeviceRequestMsg payload) {
    MqttActionPayload.MqttActionPayloadBuilder builder = MqttActionPayload.builder();
    builder.sync(configuration.isSync());
    builder.msgType(payload.getMsgType());
    builder.requestId(payload.getRequestId());
    builder.topic(configuration.getTopic());
    builder.msgBody(getMsgBody(ctx, msg));
    return Optional.of(new MqttActionMsg(msg.getTenantId(), msg.getCustomerId(), msg.getDeviceId(), builder.build()));
  }
}
