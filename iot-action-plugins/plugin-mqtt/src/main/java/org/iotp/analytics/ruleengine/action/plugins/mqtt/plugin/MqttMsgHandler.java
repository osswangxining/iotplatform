package org.iotp.analytics.ruleengine.action.plugins.mqtt.plugin;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.iotp.analytics.ruleengine.action.plugins.mqtt.action.MqttActionMsg;
import org.iotp.analytics.ruleengine.action.plugins.mqtt.action.MqttActionPayload;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.common.msg.core.BasicStatusCodeResponse;
import org.iotp.analytics.ruleengine.plugins.msg.ResponsePluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MqttMsgHandler implements RuleMsgHandler {

  private final MqttAsyncClient mqttClient;

  @Override
  public void process(PluginContext ctx, TenantId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg)
      throws RuleException {
    if (!(msg instanceof MqttActionMsg)) {
      throw new RuleException("Unsupported message type " + msg.getClass().getName() + "!");
    }
    MqttActionPayload payload = ((MqttActionMsg) msg).getPayload();

    // validate the payload format
    String msgBody = payload.getMsgBody();
    boolean isValid = false;
    try {
      new JsonParser().parse(msgBody);
      isValid = true;
    } catch (Exception ex) {
      log.error("The message format is not valid: {} with the exception: {}", msgBody, ex);
    }

    if (isValid) {
      MqttMessage mqttMsg = new MqttMessage(msgBody.getBytes(StandardCharsets.UTF_8));
      try {
        mqttClient.publish(payload.getTopic(), mqttMsg, null, new IMqttActionListener() {
          @Override
          public void onSuccess(IMqttToken asyncActionToken) {
            log.debug("Message [{}] was successfully delivered to topic [{}]!", msg.toString(), payload.getTopic());
            if (payload.isSync()) {
              ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
                  BasicStatusCodeResponse.onSuccess(payload.getMsgType(), payload.getRequestId())));
            }
          }

          @Override
          public void onFailure(IMqttToken asyncActionToken, Throwable e) {
            log.warn("Failed to deliver message [{}] to topic [{}]!", msg.toString(), payload.getTopic());
            if (payload.isSync()) {
              ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
                  BasicStatusCodeResponse.onError(payload.getMsgType(), payload.getRequestId(), new Exception(e))));
            }
          }
        });
      } catch (MqttException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }
}
