package org.iotp.analytics.ruleengine.core.plugin.telemetry.cmd;

/**
 */
public interface TelemetryPluginCmd {

  int getCmdId();

  void setCmdId(int cmdId);

  String getKeys();

}
