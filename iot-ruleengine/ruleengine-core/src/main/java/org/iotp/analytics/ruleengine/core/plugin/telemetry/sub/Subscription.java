package org.iotp.analytics.ruleengine.core.plugin.telemetry.sub;

import java.util.Map;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.infomgt.data.id.EntityId;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Subscription {

  private final SubscriptionState sub;
  private final boolean local;
  private ServerAddress server;

  public Subscription(SubscriptionState sub, boolean local) {
    this(sub, local, null);
  }

  public String getWsSessionId() {
    return getSub().getWsSessionId();
  }

  public int getSubscriptionId() {
    return getSub().getSubscriptionId();
  }

  public EntityId getEntityId() {
    return getSub().getEntityId();
  }

  public SubscriptionType getType() {
    return getSub().getType();
  }

  public boolean isAllKeys() {
    return getSub().isAllKeys();
  }

  public Map<String, Long> getKeyStates() {
    return getSub().getKeyStates();
  }

  public void setKeyState(String key, long ts) {
    getSub().getKeyStates().put(key, ts);
  }

  @Override
  public String toString() {
    return "Subscription{" + "sub=" + sub + ", local=" + local + ", server=" + server + '}';
  }
}
