package org.iotp.analytics.ruleengine.action.plugins.kafka.action;

import org.iotp.analytics.ruleengine.core.action.template.TemplateActionConfiguration;

import lombok.Data;

@Data
public class KafkaPluginActionConfiguration implements TemplateActionConfiguration {
  private boolean sync;
  private String topic;
  private String template;
}
