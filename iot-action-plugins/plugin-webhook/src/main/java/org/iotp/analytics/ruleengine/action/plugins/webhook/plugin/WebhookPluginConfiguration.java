package org.iotp.analytics.ruleengine.action.plugins.webhook.plugin;

import java.util.List;

import org.iotp.analytics.ruleengine.core.plugin.KeyValuePluginProperties;

import lombok.Data;

@Data
public class WebhookPluginConfiguration {
    private String url;

    private String authMethod;

    private String userName;
    private String password;

    private List<KeyValuePluginProperties> headers;
}
