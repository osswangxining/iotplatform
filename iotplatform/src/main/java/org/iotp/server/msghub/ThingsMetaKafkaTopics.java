package org.iotp.server.msghub;

public class ThingsMetaKafkaTopics {
  public static final String TOPIC_KEY_SPLIT_TOKEN = "\0";
  
  public static final String TELEMETRY_TOPIC = "telemetry";
  
  public static final String METADATA_ASSET_TOPIC = "metadata_asset";
  public static final String METADATA_DEVICE_TOPIC = "metadata_device";
  public static final String METADATA_PLUGIN_TOPIC = "metadata_plugin";
  public static final String METADATA_RULE_TOPIC = "metadata_rule";
  
  public static final String TENANT_ID = "tenantId";
  public static final String ASSET_ID = "assetId";
  public static final String DEVICE_ID = "deviceId";
  public static final String PLUGIN_ID = "pluginId";
  public static final String RULE_ID = "ruleId";
  public static final String DEVICE_NAME = "deviceName";
  public static final String DEVICE_TYPE = "deviceType";
  
  public static final String EVENT = "event";
  
  public static final String DEVICE_CREDENTIALS_TOPIC = "device-credentials";
  public static final String ASSET_CREDENTIALS_TOPIC = "asset-credentials";
  
  public static final String EVENT_CREDENTIALS_UPDATE = "onCredentialsUpdate";
  public static final String EVENT_DEVICENAMEORTYPE_UPDATE = "onDeviceNameOrTypeUpdate";

 
}
