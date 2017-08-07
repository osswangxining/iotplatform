package org.iotp.analytics.ruleengine.api.plugins;

import java.util.Optional;

import org.iotp.analytics.ruleengine.annotation.ConfigurableComponent;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.api.rules.RuleLifecycleComponent;
import org.iotp.analytics.ruleengine.api.rules.RuleProcessingMetaData;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.analytics.ruleengine.plugins.msg.PluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;

public interface PluginAction<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

  Optional<RuleToPluginMsg<?>> convert(RuleContext ctx, ToDeviceActorMsg toDeviceActorMsg,
      RuleProcessingMetaData deviceMsgMd);

  Optional<ToDeviceMsg> convert(PluginToRuleMsg<?> response);

  boolean isOneWayAction();

}
