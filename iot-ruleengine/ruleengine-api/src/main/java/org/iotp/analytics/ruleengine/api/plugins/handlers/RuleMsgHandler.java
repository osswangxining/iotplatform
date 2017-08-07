package org.iotp.analytics.ruleengine.api.plugins.handlers;

import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

/**
 */
public interface RuleMsgHandler {

  void process(PluginContext ctx, TenantId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException;

}
