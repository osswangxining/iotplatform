package org.iotp.analytics.ruleengine.api.plugins;

public class PluginException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PluginException(String msg, Exception e) {
    super(msg, e);
  }

}
