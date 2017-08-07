package org.iotp.analytics.ruleengine.core.action.telemetry;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelemetryPluginActionConfiguration {

  private String timeUnit;
  private int ttlValue;

}
