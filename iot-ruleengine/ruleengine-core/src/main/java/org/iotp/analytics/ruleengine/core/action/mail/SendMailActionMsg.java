package org.iotp.analytics.ruleengine.core.action.mail;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 */
@Data
@Builder
public class SendMailActionMsg implements Serializable {

  private final String from;
  private final String to;
  private final String cc;
  private final String bcc;
  private final String subject;
  private final String body;
}
