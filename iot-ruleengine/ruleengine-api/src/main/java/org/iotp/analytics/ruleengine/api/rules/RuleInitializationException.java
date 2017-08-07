package org.iotp.analytics.ruleengine.api.rules;

public class RuleInitializationException extends RuleException {

  private static final long serialVersionUID = 1L;

  public RuleInitializationException(String msg, Exception e) {
    super(msg, e);
  }

  public RuleInitializationException(String msg) {
    super(msg, null);
  }
}
