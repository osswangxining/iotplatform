package org.iotp.analytics.ruleengine.configuration;

public class ConfigurationValidationException extends Exception {

  private static final long serialVersionUID = 1L;

  public ConfigurationValidationException(String msg, Exception cause) {
    super(msg, cause);
  }

}
