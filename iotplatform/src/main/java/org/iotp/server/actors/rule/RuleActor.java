package org.iotp.server.actors.rule;

import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.plugin.ComponentLifecycleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.PluginToRuleMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.ComponentActor;
import org.iotp.server.actors.service.ContextBasedCreator;
import org.iotp.server.actors.stats.StatsPersistTick;

public class RuleActor extends ComponentActor<RuleId, RuleActorMessageProcessor> {

  private RuleActor(ActorSystemContext systemContext, TenantId tenantId, RuleId ruleId) {
    super(systemContext, tenantId, ruleId);
    setProcessor(new RuleActorMessageProcessor(tenantId, ruleId, systemContext, logger));
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    logger.debug("[{}] Received message: {}", id, msg);
    if (msg instanceof RuleProcessingMsg) {
      try {
        processor.onRuleProcessingMsg(context(), (RuleProcessingMsg) msg);
        increaseMessagesProcessedCount();
      } catch (Exception e) {
        logAndPersist("onDeviceMsg", e);
      }
    } else if (msg instanceof PluginToRuleMsg<?>) {
      try {
        processor.onPluginMsg(context(), (PluginToRuleMsg<?>) msg);
      } catch (Exception e) {
        logAndPersist("onPluginMsg", e);
      }
    } else if (msg instanceof ComponentLifecycleMsg) {
      onComponentLifecycleMsg((ComponentLifecycleMsg) msg);
    } else if (msg instanceof ClusterEventMsg) {
      onClusterEventMsg((ClusterEventMsg) msg);
    } else if (msg instanceof RuleToPluginTimeoutMsg) {
      try {
        processor.onTimeoutMsg(context(), (RuleToPluginTimeoutMsg) msg);
      } catch (Exception e) {
        logAndPersist("onTimeoutMsg", e);
      }
    } else if (msg instanceof StatsPersistTick) {
      onStatsPersistTick(id);
    } else {
      logger.debug("[{}][{}] Unknown msg type.", tenantId, id, msg.getClass().getName());
    }
  }

  public static class ActorCreator extends ContextBasedCreator<RuleActor> {
    private static final long serialVersionUID = 1L;

    private final TenantId tenantId;
    private final RuleId ruleId;

    public ActorCreator(ActorSystemContext context, TenantId tenantId, RuleId ruleId) {
      super(context);
      this.tenantId = tenantId;
      this.ruleId = ruleId;
    }

    @Override
    public RuleActor create() throws Exception {
      return new RuleActor(context, tenantId, ruleId);
    }
  }

  @Override
  protected long getErrorPersistFrequency() {
    return systemContext.getRuleErrorPersistFrequency();
  }
}
