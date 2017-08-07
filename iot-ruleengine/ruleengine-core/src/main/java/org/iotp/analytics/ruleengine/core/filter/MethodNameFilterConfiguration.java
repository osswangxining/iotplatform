package org.iotp.analytics.ruleengine.core.filter;

import lombok.Data;

/**
 */
@Data
public class MethodNameFilterConfiguration {

  private MethodName[] methodNames;

  @Data
  public static class MethodName {
    private String name;
  }

}
