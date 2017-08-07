package org.iotp.analytics.ruleengine.core.processor;

import lombok.Data;

/**
 */
@Data
public class AlarmProcessorConfiguration {

  private String newAlarmExpression;
  private String clearAlarmExpression;

  private String alarmTypeTemplate;
  private String alarmSeverity;
  private String alarmStatus;
  private boolean alarmPropagateFlag;

  private String alarmDetailsTemplate;

}