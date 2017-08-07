package org.iotp.analytics.ruleengine.action.plugins.mqtt.action;

import java.io.Serializable;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MqttActionPayload implements Serializable {

  private final boolean sync;
  private final String topic;
  private final String msgBody;

  private final Integer requestId;
  private final MsgType msgType;
}
