package org.iotp.analytics.ruleengine.core.plugin.telemetry;

public class TsData implements Comparable<TsData> {

  private final long ts;
  private final String value;

  public TsData(long ts, String value) {
    super();
    this.ts = ts;
    this.value = value;
  }

  public long getTs() {
    return ts;
  }

  public String getValue() {
    return value;
  }

  @Override
  public int compareTo(TsData o) {
    return Long.compare(ts, o.ts);
  }

}
