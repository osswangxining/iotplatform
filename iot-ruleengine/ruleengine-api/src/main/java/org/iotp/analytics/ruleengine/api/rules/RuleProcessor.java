package org.iotp.analytics.ruleengine.api.rules;

import org.iotp.analytics.ruleengine.annotation.ConfigurableComponent;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;

/**
 */
public interface RuleProcessor<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

  RuleProcessingMetaData process(RuleContext ctx, ToDeviceActorMsg msg) throws RuleException;
}
