package org.iotp.analytics.ruleengine.plugins.msg;

import java.io.Serializable;
import java.util.UUID;

import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

public class AbstractPluginToRuleMsg<T extends Serializable> implements PluginToRuleMsg<T> {

  private static final long serialVersionUID = 1L;

  private final UUID uid;
  private final TenantId tenantId;
  private final RuleId ruleId;
  private final T payload;

  public AbstractPluginToRuleMsg(UUID uid, TenantId tenantId, RuleId ruleId, T payload) {
    super();
    this.uid = uid;
    this.tenantId = tenantId;
    this.ruleId = ruleId;
    this.payload = payload;
  }

  @Override
  public UUID getUid() {
    return uid;
  }

  @Override
  public TenantId getTenantId() {
    return tenantId;
  }

  @Override
  public T getPayload() {
    return payload;
  }

  @Override
  public RuleId getRuleId() {
    return ruleId;
  }

}
