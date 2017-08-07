package org.iotp.analytics.ruleengine.core.plugin.telemetry.sub;

import java.util.Map;

import org.iotp.infomgt.data.id.EntityId;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 */
@AllArgsConstructor
public class SubscriptionState {

  @Getter
  private final String wsSessionId;
  @Getter
  private final int subscriptionId;
  @Getter
  private final EntityId entityId;
  @Getter
  private final SubscriptionType type;
  @Getter
  private final boolean allKeys;
  @Getter
  private final Map<String, Long> keyStates;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    SubscriptionState that = (SubscriptionState) o;

    if (subscriptionId != that.subscriptionId)
      return false;
    if (wsSessionId != null ? !wsSessionId.equals(that.wsSessionId) : that.wsSessionId != null)
      return false;
    if (entityId != null ? !entityId.equals(that.entityId) : that.entityId != null)
      return false;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    int result = wsSessionId != null ? wsSessionId.hashCode() : 0;
    result = 31 * result + subscriptionId;
    result = 31 * result + (entityId != null ? entityId.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SubscriptionState{" + "type=" + type + ", entityId=" + entityId + ", subscriptionId=" + subscriptionId
        + ", wsSessionId='" + wsSessionId + '\'' + '}';
  }
}
