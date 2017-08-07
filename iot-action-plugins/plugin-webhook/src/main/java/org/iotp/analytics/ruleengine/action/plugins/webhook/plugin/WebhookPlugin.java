package org.iotp.analytics.ruleengine.action.plugins.webhook.plugin;

import java.util.Base64;

import org.iotp.analytics.ruleengine.action.plugins.webhook.action.WebhookPluginAction;
import org.iotp.analytics.ruleengine.annotation.Plugin;
import org.iotp.analytics.ruleengine.api.plugins.AbstractPlugin;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;
import org.springframework.http.HttpHeaders;

import lombok.extern.slf4j.Slf4j;

@Plugin(name = "Webhook Plugin", actions = {WebhookPluginAction.class},
        descriptor = "WebhookPluginDescriptor.json", configuration = WebhookPluginConfiguration.class)
@Slf4j
public class WebhookPlugin extends AbstractPlugin<WebhookPluginConfiguration> {

    private static final String BASIC_AUTH_METHOD = "BASIC_AUTH";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_FORMAT = "Basic %s";
    private static final String CREDENTIALS_TEMPLATE = "%s:%s";
//    private static final String BASE_URL_TEMPLATE = "http://%s:%d%s";
    private WebhookMsgHandler handler;
    private String baseUrl;
    private HttpHeaders headers = new HttpHeaders();

    @Override
    public void init(WebhookPluginConfiguration configuration) {
        this.baseUrl = configuration.getUrl();

        if (configuration.getAuthMethod().equals(BASIC_AUTH_METHOD)) {
            String userName = configuration.getUserName();
            String password = configuration.getPassword();
            String credentials = String.format(CREDENTIALS_TEMPLATE, userName, password);
            byte[] token = Base64.getEncoder().encode(credentials.getBytes());
            this.headers.add(AUTHORIZATION_HEADER_NAME, String.format(AUTHORIZATION_HEADER_FORMAT, new String(token)));
        }

        if (configuration.getHeaders() != null) {
            configuration.getHeaders().forEach(h -> {
                log.debug("Adding header to request object. Key = {}, Value = {}", h.getKey(), h.getValue());
                this.headers.add(h.getKey(), h.getValue());
            });
        }

        init();
    }

    private void init() {
        this.handler = new WebhookMsgHandler(baseUrl, headers);
    }

    @Override
    protected RuleMsgHandler getRuleMsgHandler() {
        return handler;
    }

    @Override
    public void resume(PluginContext ctx) {
        init();
    }

    @Override
    public void suspend(PluginContext ctx) {
        log.debug("Suspend method was called, but no impl provided!");
    }

    @Override
    public void stop(PluginContext ctx) {
        log.debug("Stop method was called, but no impl provided!");
    }
}
