package org.iotp.analytics.ruleengine.action.plugins.kafka.action;

import java.io.Serializable;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaActionPayload implements Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = -8216429380096648655L;
  private final String topic;
  private final String msgBody;
  private final boolean sync;

  private final Integer requestId;
  private final MsgType msgType;
}
