package org.iotp.analytics.ruleengine.action.plugins.kafka.action;

import java.util.Optional;

import org.iotp.analytics.ruleengine.annotation.Action;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceRequestMsg;
import org.iotp.analytics.ruleengine.core.action.template.AbstractTemplatePluginAction;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;

import lombok.extern.slf4j.Slf4j;

@Action(name = "Kafka Plugin Action", descriptor = "KafkaActionDescriptor.json", configuration = KafkaPluginActionConfiguration.class)
@Slf4j
public class KafkaPluginAction extends AbstractTemplatePluginAction<KafkaPluginActionConfiguration> {

  @Override
  protected Optional<RuleToPluginMsg<?>> buildRuleToPluginMsg(RuleContext ctx, ToDeviceActorMsg msg,
      FromDeviceRequestMsg payload) {
    KafkaActionPayload.KafkaActionPayloadBuilder builder = KafkaActionPayload.builder();
    builder.msgType(payload.getMsgType());
    builder.requestId(payload.getRequestId());
    builder.sync(configuration.isSync());
    builder.topic(configuration.getTopic());
    builder.msgBody(getMsgBody(ctx, msg));
    return Optional.of(new KafkaActionMsg(msg.getTenantId(), msg.getCustomerId(), msg.getDeviceId(), builder.build()));
  }
}
