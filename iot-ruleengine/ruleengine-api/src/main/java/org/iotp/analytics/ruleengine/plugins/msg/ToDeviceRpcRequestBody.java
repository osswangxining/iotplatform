package org.iotp.analytics.ruleengine.plugins.msg;

import java.io.Serializable;

import lombok.Data;

/**
 */
@Data
public class ToDeviceRpcRequestBody implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -2057880626582692858L;
  private final String method;
  private final String params;
}
