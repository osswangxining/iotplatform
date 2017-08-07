package org.iotp.analytics.ruleengine.common.msg.plugin;

import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.aware.TenantAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ToAllNodesMsg;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.plugin.ComponentLifecycleEvent;

import lombok.Getter;
import lombok.ToString;

/**
 */
@ToString
public class ComponentLifecycleMsg implements TenantAwareMsg, ToAllNodesMsg {
  @Getter
  private final TenantId tenantId;
  private final PluginId pluginId;
  private final RuleId ruleId;
  @Getter
  private final ComponentLifecycleEvent event;

  public static ComponentLifecycleMsg forPlugin(TenantId tenantId, PluginId pluginId, ComponentLifecycleEvent event) {
    return new ComponentLifecycleMsg(tenantId, pluginId, null, event);
  }

  public static ComponentLifecycleMsg forRule(TenantId tenantId, RuleId ruleId, ComponentLifecycleEvent event) {
    return new ComponentLifecycleMsg(tenantId, null, ruleId, event);
  }

  private ComponentLifecycleMsg(TenantId tenantId, PluginId pluginId, RuleId ruleId, ComponentLifecycleEvent event) {
    this.tenantId = tenantId;
    this.pluginId = pluginId;
    this.ruleId = ruleId;
    this.event = event;
  }

  public Optional<PluginId> getPluginId() {
    return Optional.ofNullable(pluginId);
  }

  public Optional<RuleId> getRuleId() {
    return Optional.ofNullable(ruleId);
  }
}
