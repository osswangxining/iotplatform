package org.iotp.analytics.ruleengine.core.plugin.telemetry.cmd;

import org.iotp.analytics.ruleengine.core.plugin.telemetry.sub.SubscriptionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeseriesSubscriptionCmd extends SubscriptionCmd {

  private long startTs;
  private long timeWindow;
  private long interval;
  private int limit;
  private String agg;

  @Override
  public SubscriptionType getType() {
    return SubscriptionType.TIMESERIES;
  }
}
