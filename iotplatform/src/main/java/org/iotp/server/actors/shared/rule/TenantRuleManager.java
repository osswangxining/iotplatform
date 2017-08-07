package org.iotp.server.actors.shared.rule;

import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.PageDataIterable.FetchFunction;
import org.iotp.infomgt.data.rule.RuleMetaData;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.DefaultActorService;

public class TenantRuleManager extends RuleManager {

  public TenantRuleManager(ActorSystemContext systemContext, TenantId tenantId) {
    super(systemContext, tenantId);
  }

  @Override
  FetchFunction<RuleMetaData> getFetchRulesFunction() {
    return link -> ruleService.findTenantRules(tenantId, link);
  }

  @Override
  String getDispatcherName() {
    return DefaultActorService.TENANT_RULE_DISPATCHER_NAME;
  }

}
