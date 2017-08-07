package org.iotp.analytics.ruleengine.core.action.mail;

import lombok.Data;

/**
 */
@Data
public class SendMailActionConfiguration {

  private String sendFlag;

  private String fromTemplate;
  private String toTemplate;
  private String ccTemplate;
  private String bccTemplate;
  private String subjectTemplate;
  private String bodyTemplate;
}
