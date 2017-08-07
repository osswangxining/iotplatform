package org.iotp.analytics.ruleengine.action.plugins.kafka.plugin;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.iotp.analytics.ruleengine.action.plugins.kafka.action.KafkaActionMsg;
import org.iotp.analytics.ruleengine.action.plugins.kafka.action.KafkaActionPayload;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.common.msg.core.BasicStatusCodeResponse;
import org.iotp.analytics.ruleengine.plugins.msg.ResponsePluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class KafkaMsgHandler implements RuleMsgHandler {

  private final Producer<?, String> producer;

  @Override
  public void process(PluginContext ctx, TenantId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg)
      throws RuleException {
    if (!(msg instanceof KafkaActionMsg)) {
      throw new RuleException("Unsupported message type " + msg.getClass().getName() + "!");
    }
    KafkaActionPayload payload = ((KafkaActionMsg) msg).getPayload();
    log.debug("Processing kafka payload: {}", payload);
    try {
      producer.send(new ProducerRecord<>(payload.getTopic(), payload.getMsgBody()), (metadata, e) -> {
        if (payload.isSync()) {
          if (metadata != null) {
            ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
                BasicStatusCodeResponse.onSuccess(payload.getMsgType(), payload.getRequestId())));
          } else {
            ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
                BasicStatusCodeResponse.onError(payload.getMsgType(), payload.getRequestId(), e)));
          }
        }
      });
    } catch (Exception e) {
      throw new RuleException(e.getMessage(), e);
    }
  }
}
