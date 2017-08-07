package org.iotp.analytics.ruleengine.action.plugins.kafka.plugin;

import java.util.List;

import org.iotp.analytics.ruleengine.core.plugin.KeyValuePluginProperties;

import lombok.Data;

@Data
public class KafkaPluginConfiguration {
  private String bootstrapServers;
  private int retries;
  private int batchSize;
  private int linger;
  private int bufferMemory;
  private int acks;
  private String keySerializer;
  private String valueSerializer;
  private List<KeyValuePluginProperties> otherProperties;
}