package org.iotp.server.actors.shared.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.iotp.infomgt.dao.rule.RuleService;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.PageDataIterable;
import org.iotp.infomgt.data.page.PageDataIterable.FetchFunction;
import org.iotp.infomgt.data.plugin.ComponentLifecycleEvent;
import org.iotp.infomgt.data.plugin.ComponentLifecycleState;
import org.iotp.infomgt.data.rule.RuleMetaData;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.rule.RuleActor;
import org.iotp.server.actors.rule.RuleActorChain;
import org.iotp.server.actors.rule.RuleActorMetaData;
import org.iotp.server.actors.rule.SimpleRuleActorChain;
import org.iotp.server.actors.service.ContextAwareActor;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class RuleManager {

  protected final ActorSystemContext systemContext;
  protected final RuleService ruleService;
  protected final Map<RuleId, ActorRef> ruleActors;
  protected final TenantId tenantId;

  Map<RuleMetaData, RuleActorMetaData> ruleMap = new HashMap<>();
  private RuleActorChain ruleChain;

  public RuleManager(ActorSystemContext systemContext, TenantId tenantId) {
    this.systemContext = systemContext;
    this.ruleService = systemContext.getRuleService();
    this.ruleActors = new HashMap<>();
    this.tenantId = tenantId;
  }

  public void init(ActorContext context) {
    PageDataIterable<RuleMetaData> ruleIterator = new PageDataIterable<>(getFetchRulesFunction(),
        ContextAwareActor.ENTITY_PACK_LIMIT);
    ruleMap = new HashMap<>();

    for (RuleMetaData rule : ruleIterator) {
      log.debug("[{}] Creating rule actor {}", rule.getId(), rule);
      ActorRef ref = getOrCreateRuleActor(context, rule.getId());
      RuleActorMetaData actorMd = RuleActorMetaData.systemRule(rule.getId(), rule.getWeight(), ref);
      ruleMap.put(rule, actorMd);
      log.debug("[{}] Rule actor created.", rule.getId());
    }

    refreshRuleChain();
  }

  public Optional<ActorRef> update(ActorContext context, RuleId ruleId, ComponentLifecycleEvent event) {
    RuleMetaData rule;
    if (event != ComponentLifecycleEvent.DELETED) {
      rule = systemContext.getRuleService().findRuleById(ruleId);
    } else {
      rule = ruleMap.keySet().stream().filter(r -> r.getId().equals(ruleId))
          .peek(r -> r.setState(ComponentLifecycleState.SUSPENDED)).findFirst().orElse(null);
      if (rule != null) {
        ruleMap.remove(rule);
        ruleActors.remove(ruleId);
      }
    }
    if (rule != null) {
      RuleActorMetaData actorMd = ruleMap.get(rule);
      if (actorMd == null) {
        ActorRef ref = getOrCreateRuleActor(context, rule.getId());
        actorMd = RuleActorMetaData.systemRule(rule.getId(), rule.getWeight(), ref);
        ruleMap.put(rule, actorMd);
      }
      refreshRuleChain();
      return Optional.of(actorMd.getActorRef());
    } else {
      log.warn("[{}] Can't process unknown rule!", ruleId);
      return Optional.empty();
    }
  }

  abstract FetchFunction<RuleMetaData> getFetchRulesFunction();

  abstract String getDispatcherName();

  public ActorRef getOrCreateRuleActor(ActorContext context, RuleId ruleId) {
    return ruleActors.computeIfAbsent(ruleId,
        rId -> context.actorOf(
            Props.create(new RuleActor.ActorCreator(systemContext, tenantId, rId)).withDispatcher(getDispatcherName()),
            rId.toString()));
  }

  public RuleActorChain getRuleChain() {
    return ruleChain;
  }

  private void refreshRuleChain() {
    Set<RuleActorMetaData> activeRuleSet = new HashSet<>();
    for (Map.Entry<RuleMetaData, RuleActorMetaData> rule : ruleMap.entrySet()) {
      if (rule.getKey().getState() == ComponentLifecycleState.ACTIVE) {
        activeRuleSet.add(rule.getValue());
      }
    }
    ruleChain = new SimpleRuleActorChain(activeRuleSet);
  }
}
