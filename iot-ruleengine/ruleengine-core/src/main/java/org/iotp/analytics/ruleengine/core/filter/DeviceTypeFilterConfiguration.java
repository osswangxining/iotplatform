package org.iotp.analytics.ruleengine.core.filter;

import lombok.Data;

/**
 */
@Data
public class DeviceTypeFilterConfiguration {

  private DeviceTypeName[] deviceTypes;

  @Data
  public static class DeviceTypeName {
    private String name;
  }

}
