package org.iotp.analytics.ruleengine.api.rules;

import org.iotp.analytics.ruleengine.annotation.ConfigurableComponent;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;

/**
 */
public interface RuleFilter<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

  boolean filter(RuleContext ctx, ToDeviceActorMsg msg);

}
