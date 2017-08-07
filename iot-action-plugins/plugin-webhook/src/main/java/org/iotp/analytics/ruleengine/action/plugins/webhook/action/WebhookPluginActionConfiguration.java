package org.iotp.analytics.ruleengine.action.plugins.webhook.action;

import org.iotp.analytics.ruleengine.core.action.template.TemplateActionConfiguration;

import lombok.Data;

@Data
public class WebhookPluginActionConfiguration implements TemplateActionConfiguration {
  private String actionPath;
  private String requestMethod;
  private String contentType;
  private String template;
  private boolean sync;
  private int expectedResultCode;
}
