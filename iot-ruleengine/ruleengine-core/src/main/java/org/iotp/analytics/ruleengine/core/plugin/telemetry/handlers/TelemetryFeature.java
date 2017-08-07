package org.iotp.analytics.ruleengine.core.plugin.telemetry.handlers;

/**
 */
public enum TelemetryFeature {

  ATTRIBUTES, TIMESERIES;

  public static TelemetryFeature forName(String name) {
    return TelemetryFeature.valueOf(name.toUpperCase());
  }

}
