package org.iotp.analytics.ruleengine.core.plugin.mail;

import java.util.List;

import org.iotp.analytics.ruleengine.core.plugin.KeyValuePluginProperties;

import lombok.Data;

/**
 */
@Data
public class MailPluginConfiguration {
  private String host;
  private Integer port;
  private String username;
  private String password;
  private List<KeyValuePluginProperties> otherProperties;
}
