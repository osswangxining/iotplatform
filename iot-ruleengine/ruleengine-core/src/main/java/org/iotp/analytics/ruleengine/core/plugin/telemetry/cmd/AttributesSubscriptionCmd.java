package org.iotp.analytics.ruleengine.core.plugin.telemetry.cmd;

import org.iotp.analytics.ruleengine.core.plugin.telemetry.sub.SubscriptionType;

import lombok.NoArgsConstructor;

/**
 */
@NoArgsConstructor
public class AttributesSubscriptionCmd extends SubscriptionCmd {

  @Override
  public SubscriptionType getType() {
    return SubscriptionType.ATTRIBUTES;
  }

}
