package org.iotp.analytics.ruleengine.core.filter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.iotp.analytics.ruleengine.annotation.Filter;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.api.rules.RuleFilter;
import org.iotp.analytics.ruleengine.api.rules.SimpleRuleLifecycleComponent;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Filter(name = "Device Type Filter", descriptor = "DeviceTypeFilterDescriptor.json", configuration = DeviceTypeFilterConfiguration.class)
@Slf4j
public class DeviceTypeFilter extends SimpleRuleLifecycleComponent
    implements RuleFilter<DeviceTypeFilterConfiguration> {

  private Set<String> deviceTypes;

  @Override
  public void init(DeviceTypeFilterConfiguration configuration) {
    deviceTypes = Arrays.stream(configuration.getDeviceTypes()).map(m -> m.getName()).collect(Collectors.toSet());
  }

  @Override
  public boolean filter(RuleContext ctx, ToDeviceActorMsg msg) {
    return deviceTypes.contains(ctx.getDeviceMetaData().getDeviceType());
  }
}
