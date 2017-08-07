package org.iotp.analytics.ruleengine.core.processor;

import lombok.Data;

/**
 */
@Data
public class AlarmDeduplicationProcessorConfiguration {

  private String alarmIdTemplate;
  private String alarmBodyTemplate;

}
