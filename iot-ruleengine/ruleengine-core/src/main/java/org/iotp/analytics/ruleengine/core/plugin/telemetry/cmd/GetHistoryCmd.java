package org.iotp.analytics.ruleengine.core.plugin.telemetry.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetHistoryCmd implements TelemetryPluginCmd {

  private int cmdId;
  private String entityType;
  private String entityId;
  private String keys;
  private long startTs;
  private long endTs;
  private long interval;
  private int limit;
  private String agg;

}
