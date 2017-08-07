package org.iotp.infomgt.data.rule;

import org.iotp.infomgt.data.SearchTextBased;
import org.iotp.infomgt.data.common.NamingThing;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.plugin.ComponentLifecycleState;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RuleMetaData extends SearchTextBased<RuleId> implements NamingThing {

  private static final long serialVersionUID = -5656679015122935465L;

  private TenantId tenantId;
  private String name;
  private ComponentLifecycleState state;
  private int weight;
  private String pluginToken;
  private JsonNode filters;
  private JsonNode processor;
  private JsonNode action;
  private JsonNode additionalInfo;

  public RuleMetaData() {
    super();
  }

  public RuleMetaData(RuleId id) {
    super(id);
  }

  public RuleMetaData(RuleMetaData rule) {
    super(rule);
    this.tenantId = rule.getTenantId();
    this.name = rule.getName();
    this.state = rule.getState();
    this.weight = rule.getWeight();
    this.pluginToken = rule.getPluginToken();
    this.filters = rule.getFilters();
    this.processor = rule.getProcessor();
    this.action = rule.getAction();
    this.additionalInfo = rule.getAdditionalInfo();
  }

  @Override
  public String getSearchText() {
    return name;
  }

  @Override
  public String getName() {
    return name;
  }

}
