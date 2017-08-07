package org.iotp.analytics.ruleengine.core.filter;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.iotp.analytics.ruleengine.annotation.Filter;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.api.rules.RuleFilter;
import org.iotp.analytics.ruleengine.api.rules.SimpleRuleLifecycleComponent;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Filter(name = "Message Type Filter", descriptor = "MsgTypeFilterDescriptor.json", configuration = MsgTypeFilterConfiguration.class)
@Slf4j
public class MsgTypeFilter extends SimpleRuleLifecycleComponent implements RuleFilter<MsgTypeFilterConfiguration> {

  private List<MsgType> msgTypes;

  @Override
  public void init(MsgTypeFilterConfiguration configuration) {
    msgTypes = Arrays.stream(configuration.getMessageTypes()).map(type -> {
      switch (type) {
      case "GET_ATTRIBUTES":
        return MsgType.GET_ATTRIBUTES_REQUEST;
      case "POST_ATTRIBUTES":
        return MsgType.POST_ATTRIBUTES_REQUEST;
      case "POST_TELEMETRY":
        return MsgType.POST_TELEMETRY_REQUEST;
      case "RPC_REQUEST":
        return MsgType.TO_SERVER_RPC_REQUEST;
      default:
        throw new InvalidParameterException("Can't map " + type + " to " + MsgType.class.getName() + "!");
      }
    }).collect(Collectors.toList());
  }

  @Override
  public boolean filter(RuleContext ctx, ToDeviceActorMsg msg) {
    for (MsgType msgType : msgTypes) {
      if (msgType == msg.getPayload().getMsgType()) {
        return true;
      }
    }
    return false;
  }
}
