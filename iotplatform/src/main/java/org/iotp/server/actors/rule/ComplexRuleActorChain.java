package org.iotp.server.actors.rule;

public class ComplexRuleActorChain implements RuleActorChain {

  private final RuleActorChain systemChain;
  private final RuleActorChain tenantChain;

  public ComplexRuleActorChain(RuleActorChain systemChain, RuleActorChain tenantChain) {
    super();
    this.systemChain = systemChain;
    this.tenantChain = tenantChain;
  }

  @Override
  public int size() {
    return systemChain.size() + tenantChain.size();
  }

  @Override
  public RuleActorMetaData getRuleActorMd(int index) {
    if (index < systemChain.size()) {
      return systemChain.getRuleActorMd(index);
    } else {
      return tenantChain.getRuleActorMd(index - systemChain.size());
    }
  }

}
