package org.iotp.infomgt.data.id;

import java.util.UUID;

import org.iotp.infomgt.data.common.ThingType;

public class EntityIdFactory {

  public static EntityId getByTypeAndId(String type, String uuid) {
    return getByTypeAndUuid(ThingType.valueOf(type), UUID.fromString(uuid));
  }

  public static EntityId getByTypeAndUuid(String type, UUID uuid) {
    return getByTypeAndUuid(ThingType.valueOf(type), uuid);
  }

  public static EntityId getByTypeAndUuid(ThingType type, UUID uuid) {
    switch (type) {
    case TENANT:
      return new TenantId(uuid);
    case CUSTOMER:
      return new CustomerId(uuid);
    case USER:
      return new UserId(uuid);
    case RULE:
      return new RuleId(uuid);
    case PLUGIN:
      return new PluginId(uuid);
    case DASHBOARD:
      return new DashboardId(uuid);
    case DEVICE:
      return new DeviceId(uuid);
    case DEVICETYPE:
      return new DeviceTypeId(uuid);
    case ASSET:
      return new AssetId(uuid);
    case ALARM:
      return new AlarmId(uuid);
    }
    throw new IllegalArgumentException("EntityType " + type + " is not supported!");
  }
}
