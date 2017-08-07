package org.iotp.analytics.ruleengine.core.plugin.messaging;

import lombok.Data;

/**
 */
@Data
public class DeviceMessagingPluginConfiguration {

  private int maxDeviceCountPerCustomer;
  private long defaultTimeout;
  private long maxTimeout;

}
