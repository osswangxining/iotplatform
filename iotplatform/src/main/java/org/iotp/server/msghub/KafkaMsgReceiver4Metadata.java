package org.iotp.server.msghub;

import java.util.Arrays;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.plugin.ComponentLifecycleEvent;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaMsgReceiver4Metadata extends BaseKafkaMsgReceiver {
  public KafkaMsgReceiver4Metadata() {
    topicNames = Arrays.asList(ThingsMetaKafkaTopics.METADATA_ASSET_TOPIC, ThingsMetaKafkaTopics.METADATA_DEVICE_TOPIC,
        ThingsMetaKafkaTopics.METADATA_PLUGIN_TOPIC, ThingsMetaKafkaTopics.METADATA_RULE_TOPIC);
  }

  public boolean consume(ConsumerRecords<String, String> records) {
    for (ConsumerRecord<String, String> consumerRecord : records) {
      String topic = consumerRecord.topic();
      String key = consumerRecord.key();
      String payload = consumerRecord.value();
      log.info("topic:{}, key:{}, value:{}", topic, key, payload);

      if (topic == null) {
        log.error("topic should be not null.");
        return true;
      }
      if (payload == null) {
        log.error("payload should be not null.");
        return true;
      }

      JsonParser jsonParser = new JsonParser();
      JsonElement jsonElement = null;
      try {
        jsonElement = jsonParser.parse(payload);
      } catch (Exception e) {
        log.error("payload should be valid Json format: {}.", payload);
        return true;
      }
      if (jsonElement == null || !(jsonElement instanceof JsonObject)) {
        log.error("payload should be valid Json object: {}.", payload);
        return true;
      }
      JsonObject json = (JsonObject) jsonElement;
      switch (topic) {
      case ThingsMetaKafkaTopics.METADATA_ASSET_TOPIC:
        JsonPrimitive eventAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.EVENT);
        String event = (eventAsJsonPrimitive == null) ? null : eventAsJsonPrimitive.toString();
        if (ThingsMetaKafkaTopics.EVENT_CREDENTIALS_UPDATE.equals(event)) {
          JsonPrimitive tenantIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.TENANT_ID);
          String tenantId = (tenantIdAsJsonPrimitive == null) ? null : tenantIdAsJsonPrimitive.toString();
          JsonPrimitive assetIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.ASSET_ID);
          String assetId = (assetIdAsJsonPrimitive == null) ? null : assetIdAsJsonPrimitive.toString();
          actorService.onCredentialsUpdate(new TenantId(UUID.fromString(tenantId)),
              new AssetId(UUID.fromString(assetId)));
        }

        break;
      case ThingsMetaKafkaTopics.METADATA_DEVICE_TOPIC:
        eventAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.EVENT);
        event = (eventAsJsonPrimitive == null) ? null : eventAsJsonPrimitive.toString();
        if (ThingsMetaKafkaTopics.EVENT_DEVICENAMEORTYPE_UPDATE.equals(event)) {
          JsonPrimitive tenantIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.TENANT_ID);
          String tenantId = (tenantIdAsJsonPrimitive == null) ? null : tenantIdAsJsonPrimitive.toString();
          JsonPrimitive deviceIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.DEVICE_ID);
          String deviceId = (deviceIdAsJsonPrimitive == null) ? null : deviceIdAsJsonPrimitive.toString();
          JsonPrimitive deviceNameAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.DEVICE_NAME);
          String deviceName = (deviceNameAsJsonPrimitive == null) ? null : deviceNameAsJsonPrimitive.toString();
          JsonPrimitive deviceTypeAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.DEVICE_TYPE);
          String deviceType = (deviceTypeAsJsonPrimitive == null) ? null : deviceTypeAsJsonPrimitive.toString();
          actorService.onDeviceNameOrTypeUpdate(new TenantId(UUID.fromString(tenantId)),
              new DeviceId(UUID.fromString(deviceId)), deviceName, deviceType);
        } else if (ThingsMetaKafkaTopics.EVENT_CREDENTIALS_UPDATE.equals(event)) {
          JsonPrimitive tenantIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.TENANT_ID);
          String tenantId = (tenantIdAsJsonPrimitive == null) ? null : tenantIdAsJsonPrimitive.toString();
          JsonPrimitive deviceIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.DEVICE_ID);
          String deviceId = (deviceIdAsJsonPrimitive == null) ? null : deviceIdAsJsonPrimitive.toString();
          actorService.onCredentialsUpdate(new TenantId(UUID.fromString(tenantId)),
              new DeviceId(UUID.fromString(deviceId)));
        }

        break;
      case ThingsMetaKafkaTopics.METADATA_PLUGIN_TOPIC:
        eventAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.EVENT);
        event = (eventAsJsonPrimitive == null) ? null : eventAsJsonPrimitive.toString();
        JsonPrimitive tenantIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.TENANT_ID);
        String tenantId = (tenantIdAsJsonPrimitive == null) ? null : tenantIdAsJsonPrimitive.toString();
        JsonPrimitive pluginIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.PLUGIN_ID);
        String pluginId = (pluginIdAsJsonPrimitive == null) ? null : pluginIdAsJsonPrimitive.toString();
        if (ComponentLifecycleEvent.ACTIVATED.name().equals(event)) {
          actorService.onPluginStateChange(new TenantId(UUID.fromString(tenantId)),
              new PluginId(UUID.fromString(pluginId)), ComponentLifecycleEvent.ACTIVATED);
        } else if (ComponentLifecycleEvent.DELETED.name().equals(event)) {
          actorService.onPluginStateChange(new TenantId(UUID.fromString(tenantId)),
              new PluginId(UUID.fromString(pluginId)), ComponentLifecycleEvent.DELETED);
        } else if (ComponentLifecycleEvent.CREATED.name().equals(event)) {
          actorService.onPluginStateChange(new TenantId(UUID.fromString(tenantId)),
              new PluginId(UUID.fromString(pluginId)), ComponentLifecycleEvent.CREATED);
        } else if (ComponentLifecycleEvent.UPDATED.name().equals(event)) {
          actorService.onPluginStateChange(new TenantId(UUID.fromString(tenantId)),
              new PluginId(UUID.fromString(pluginId)), ComponentLifecycleEvent.UPDATED);
        } else if (ComponentLifecycleEvent.SUSPENDED.name().equals(event)) {
          actorService.onPluginStateChange(new TenantId(UUID.fromString(tenantId)),
              new PluginId(UUID.fromString(pluginId)), ComponentLifecycleEvent.SUSPENDED);
        }

        break;
      case ThingsMetaKafkaTopics.METADATA_RULE_TOPIC:
        eventAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.EVENT);
        event = (eventAsJsonPrimitive == null) ? null : eventAsJsonPrimitive.toString();
        tenantIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.TENANT_ID);
        tenantId = (tenantIdAsJsonPrimitive == null) ? null : tenantIdAsJsonPrimitive.toString();
        JsonPrimitive ruleIdAsJsonPrimitive = json.getAsJsonPrimitive(ThingsMetaKafkaTopics.RULE_ID);
        String ruleId = (ruleIdAsJsonPrimitive == null) ? null : ruleIdAsJsonPrimitive.toString();
        if (ComponentLifecycleEvent.ACTIVATED.name().equals(event)) {
          actorService.onRuleStateChange(new TenantId(UUID.fromString(tenantId)),
              new RuleId(UUID.fromString(ruleId)), ComponentLifecycleEvent.ACTIVATED);
        } else if (ComponentLifecycleEvent.DELETED.name().equals(event)) {
          actorService.onRuleStateChange(new TenantId(UUID.fromString(tenantId)),
              new RuleId(UUID.fromString(ruleId)), ComponentLifecycleEvent.DELETED);
        } else if (ComponentLifecycleEvent.CREATED.name().equals(event)) {
          actorService.onRuleStateChange(new TenantId(UUID.fromString(tenantId)),
              new RuleId(UUID.fromString(ruleId)), ComponentLifecycleEvent.CREATED);
        } else if (ComponentLifecycleEvent.UPDATED.name().equals(event)) {
          actorService.onRuleStateChange(new TenantId(UUID.fromString(tenantId)),
              new RuleId(UUID.fromString(ruleId)), ComponentLifecycleEvent.UPDATED);
        } else if (ComponentLifecycleEvent.SUSPENDED.name().equals(event)) {
          actorService.onRuleStateChange(new TenantId(UUID.fromString(tenantId)),
              new RuleId(UUID.fromString(ruleId)), ComponentLifecycleEvent.SUSPENDED);
        }

        break;
      default:
        break;
      }

    }
    return true;
  }
}
