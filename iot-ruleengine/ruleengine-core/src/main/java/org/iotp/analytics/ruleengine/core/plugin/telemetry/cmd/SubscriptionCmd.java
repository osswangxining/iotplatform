package org.iotp.analytics.ruleengine.core.plugin.telemetry.cmd;

import org.iotp.analytics.ruleengine.core.plugin.telemetry.sub.SubscriptionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class SubscriptionCmd implements TelemetryPluginCmd {

  private int cmdId;
  private String entityType;
  private String entityId;
  private String keys;
  private String scope;
  private boolean unsubscribe;

  public abstract SubscriptionType getType();

  @Override
  public String toString() {
    return "SubscriptionCmd [entityType=" + entityType + ", entityId=" + entityId + ", tags=" + keys + ", unsubscribe="
        + unsubscribe + "]";
  }

}
