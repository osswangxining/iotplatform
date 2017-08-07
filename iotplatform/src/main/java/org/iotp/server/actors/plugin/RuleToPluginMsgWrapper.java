package org.iotp.server.actors.plugin;

import org.iotp.analytics.ruleengine.common.msg.aware.RuleAwareMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginActorMsg;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

public class RuleToPluginMsgWrapper implements ToPluginActorMsg, RuleAwareMsg {

  private final TenantId pluginTenantId;
  private final PluginId pluginId;
  private final TenantId ruleTenantId;
  private final RuleId ruleId;
  private final RuleToPluginMsg<?> msg;

  public RuleToPluginMsgWrapper(TenantId pluginTenantId, PluginId pluginId, TenantId ruleTenantId, RuleId ruleId,
      RuleToPluginMsg<?> msg) {
    super();
    this.pluginTenantId = pluginTenantId;
    this.pluginId = pluginId;
    this.ruleTenantId = ruleTenantId;
    this.ruleId = ruleId;
    this.msg = msg;
  }

  @Override
  public TenantId getPluginTenantId() {
    return pluginTenantId;
  }

  @Override
  public PluginId getPluginId() {
    return pluginId;
  }

  public TenantId getRuleTenantId() {
    return ruleTenantId;
  }

  @Override
  public RuleId getRuleId() {
    return ruleId;
  }

  public RuleToPluginMsg<?> getMsg() {
    return msg;
  }

}
