package org.iotp.server.actors.shared.rule;

import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.PageDataIterable.FetchFunction;
import org.iotp.infomgt.data.rule.RuleMetaData;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.DefaultActorService;

public class SystemRuleManager extends RuleManager {

  public SystemRuleManager(ActorSystemContext systemContext) {
    super(systemContext, new TenantId(ModelConstants.NULL_UUID));
  }

  @Override
  FetchFunction<RuleMetaData> getFetchRulesFunction() {
    return ruleService::findSystemRules;
  }

  @Override
  String getDispatcherName() {
    return DefaultActorService.SYSTEM_RULE_DISPATCHER_NAME;
  }
}
