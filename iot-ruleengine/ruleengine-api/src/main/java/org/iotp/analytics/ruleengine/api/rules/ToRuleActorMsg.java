package org.iotp.analytics.ruleengine.api.rules;

import org.iotp.analytics.ruleengine.common.msg.aware.TenantAwareMsg;
import org.iotp.infomgt.data.id.RuleId;

public interface ToRuleActorMsg extends TenantAwareMsg {

  RuleId getRuleId();

}
