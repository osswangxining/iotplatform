package org.iotp.analytics.ruleengine.core.filter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.iotp.analytics.ruleengine.annotation.Filter;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.api.rules.RuleFilter;
import org.iotp.analytics.ruleengine.api.rules.SimpleRuleLifecycleComponent;
import org.iotp.analytics.ruleengine.common.msg.core.ToServerRpcRequestMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Filter(name = "Method Name Filter", descriptor = "MethodNameFilterDescriptor.json", configuration = MethodNameFilterConfiguration.class)
@Slf4j
public class MethodNameFilter extends SimpleRuleLifecycleComponent
    implements RuleFilter<MethodNameFilterConfiguration> {

  private Set<String> methods;

  @Override
  public void init(MethodNameFilterConfiguration configuration) {
    methods = Arrays.stream(configuration.getMethodNames()).map(m -> m.getName()).collect(Collectors.toSet());
  }

  @Override
  public boolean filter(RuleContext ctx, ToDeviceActorMsg msg) {
    if (msg.getPayload().getMsgType() == MsgType.TO_SERVER_RPC_REQUEST) {
      return methods.contains(((ToServerRpcRequestMsg) msg.getPayload()).getMethod());
    } else {
      return false;
    }
  }
}
