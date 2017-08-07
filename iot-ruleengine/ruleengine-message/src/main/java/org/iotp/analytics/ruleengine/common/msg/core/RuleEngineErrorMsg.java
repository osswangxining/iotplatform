package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;

import lombok.Data;

/**
 */
@Data
public class RuleEngineErrorMsg implements ToDeviceMsg {

  private final MsgType inMsgType;
  private final RuleEngineError error;

  @Override
  public boolean isSuccess() {
    return false;
  }

  @Override
  public MsgType getMsgType() {
    return MsgType.RULE_ENGINE_ERROR;
  }

  public String getErrorMsg() {
    switch (error) {
    case NO_RULES:
      return "No rules configured!";
    case NO_ACTIVE_RULES:
      return "No active rules!";
    case NO_FILTERS_MATCHED:
      return "No rules that match current message!";
    case NO_REQUEST_FROM_ACTIONS:
      return "Rule filters match, but no plugin message produced by rule action!";
    case NO_TWO_WAY_ACTIONS:
      return "Rule filters match, but no rule with two-way action configured!";
    case NO_RESPONSE_FROM_ACTIONS:
      return "Rule filters match, message processed by plugin, but no response produced by rule action!";
    case PLUGIN_TIMEOUT:
      return "Timeout during processing of message by plugin!";
    default:
      throw new RuntimeException("Error " + error + " is not supported!");
    }
  }
}
