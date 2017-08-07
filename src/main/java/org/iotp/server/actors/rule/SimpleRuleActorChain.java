package org.iotp.server.actors.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimpleRuleActorChain implements RuleActorChain {

  private final List<RuleActorMetaData> rules;

  public SimpleRuleActorChain(Set<RuleActorMetaData> ruleSet) {
    rules = new ArrayList<>(ruleSet);
    rules.sort(RuleActorMetaData.RULE_ACTOR_MD_COMPARATOR);
  }

  public int size() {
    return rules.size();
  }

  public RuleActorMetaData getRuleActorMd(int index) {
    return rules.get(index);
  }

}
