package org.iotp.server.actors.rule;

import java.util.Comparator;

import org.iotp.infomgt.data.id.RuleId;

import akka.actor.ActorRef;

public class RuleActorMetaData {

  private final RuleId ruleId;
  private final boolean systemRule;
  private final int weight;
  private final ActorRef actorRef;

  public static final Comparator<RuleActorMetaData> RULE_ACTOR_MD_COMPARATOR = new Comparator<RuleActorMetaData>() {

    @Override
    public int compare(RuleActorMetaData r1, RuleActorMetaData r2) {
      if (r1.isSystemRule() && !r2.isSystemRule()) {
        return 1;
      } else if (!r1.isSystemRule() && r2.isSystemRule()) {
        return -1;
      } else {
        return Integer.compare(r2.getWeight(), r1.getWeight());
      }
    }
  };

  public static RuleActorMetaData systemRule(RuleId ruleId, int weight, ActorRef actorRef) {
    return new RuleActorMetaData(ruleId, true, weight, actorRef);
  }

  public static RuleActorMetaData tenantRule(RuleId ruleId, int weight, ActorRef actorRef) {
    return new RuleActorMetaData(ruleId, false, weight, actorRef);
  }

  private RuleActorMetaData(RuleId ruleId, boolean systemRule, int weight, ActorRef actorRef) {
    super();
    this.ruleId = ruleId;
    this.systemRule = systemRule;
    this.weight = weight;
    this.actorRef = actorRef;
  }

  public RuleId getRuleId() {
    return ruleId;
  }

  public boolean isSystemRule() {
    return systemRule;
  }

  public int getWeight() {
    return weight;
  }

  public ActorRef getActorRef() {
    return actorRef;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ruleId == null) ? 0 : ruleId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RuleActorMetaData other = (RuleActorMetaData) obj;
    if (ruleId == null) {
      if (other.ruleId != null)
        return false;
    } else if (!ruleId.equals(other.ruleId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "RuleActorMetaData [ruleId=" + ruleId + ", systemRule=" + systemRule + ", weight=" + weight + ", actorRef="
        + actorRef + "]";
  }

}
