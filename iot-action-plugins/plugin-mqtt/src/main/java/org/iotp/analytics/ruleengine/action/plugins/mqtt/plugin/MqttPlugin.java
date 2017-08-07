package org.iotp.analytics.ruleengine.action.plugins.mqtt.plugin;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.iotp.analytics.ruleengine.action.plugins.mqtt.action.MqttPluginAction;
import org.iotp.analytics.ruleengine.annotation.Plugin;
import org.iotp.analytics.ruleengine.api.plugins.AbstractPlugin;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;

import lombok.extern.slf4j.Slf4j;

@Plugin(name = "Mqtt Plugin", actions = {
    MqttPluginAction.class }, descriptor = "MqttPluginDescriptor.json", configuration = MqttPluginConfiguration.class)
@Slf4j
public class MqttPlugin extends AbstractPlugin<MqttPluginConfiguration> {

  private MqttMsgHandler handler;

  private MqttAsyncClient mqttClient;
  private MqttConnectOptions mqttClientOptions;

  private int retryInterval;

  private final Object connectLock = new Object();

  @Override
  public void init(MqttPluginConfiguration configuration) {
    retryInterval = configuration.getRetryInterval();

    mqttClientOptions = new MqttConnectOptions();
    mqttClientOptions.setCleanSession(false);
    mqttClientOptions.setMaxInflight(configuration.getMaxInFlight());
    mqttClientOptions.setAutomaticReconnect(true);
    String clientId = configuration.getClientId();
    if (StringUtils.isEmpty(clientId)) {
      clientId = UUID.randomUUID().toString();
    }
    if (!StringUtils.isEmpty(configuration.getAccessToken())) {
      mqttClientOptions.setUserName(configuration.getAccessToken());
    }
    try {
      mqttClient = new MqttAsyncClient("tcp://" + configuration.getHost() + ":" + configuration.getPort(), clientId);
    } catch (Exception e) {
      log.error("Failed to create mqtt client", e);
      throw new RuntimeException(e);
    }
    // connect();
  }

  private void connect() {
    if (!mqttClient.isConnected()) {
      synchronized (connectLock) {
        while (!mqttClient.isConnected()) {
          log.debug("Attempt to connect to requested mqtt host [{}]!", mqttClient.getServerURI());
          try {
            mqttClient.connect(mqttClientOptions, null, new IMqttActionListener() {
              @Override
              public void onSuccess(IMqttToken iMqttToken) {
                log.info("Connected to requested mqtt host [{}]!", mqttClient.getServerURI());
              }

              @Override
              public void onFailure(IMqttToken iMqttToken, Throwable e) {
              }
            }).waitForCompletion();
          } catch (MqttException e) {
            log.warn("Failed to connect to requested mqtt host  [{}]!", mqttClient.getServerURI(), e);
            if (!mqttClient.isConnected()) {
              try {
                Thread.sleep(retryInterval);
              } catch (InterruptedException e1) {
                log.trace("Failed to wait for retry interval!", e);
              }
            }
          }
        }
      }
    }
    this.handler = new MqttMsgHandler(mqttClient);
  }

  private void destroy() {
    try {
      this.handler = null;
      this.mqttClient.disconnect();
    } catch (MqttException e) {
      log.error("Failed to close mqtt client connection during destroy()", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  protected RuleMsgHandler getRuleMsgHandler() {
    return handler;
  }

  @Override
  public void resume(PluginContext ctx) {
    connect();
  }

  @Override
  public void suspend(PluginContext ctx) {
    destroy();
  }

  @Override
  public void stop(PluginContext ctx) {
    destroy();
  }
}
