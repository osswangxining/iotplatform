package org.iotp.analytics.ruleengine.core.plugin.telemetry;

public class AttributeData implements Comparable<AttributeData> {

  private final long lastUpdateTs;
  private final String key;
  private final Object value;

  public AttributeData(long lastUpdateTs, String key, Object value) {
    super();
    this.lastUpdateTs = lastUpdateTs;
    this.key = key;
    this.value = value;
  }

  public long getLastUpdateTs() {
    return lastUpdateTs;
  }

  public String getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public int compareTo(AttributeData o) {
    return Long.compare(lastUpdateTs, o.lastUpdateTs);
  }

}
