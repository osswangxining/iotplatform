package org.iotp.infomgt.data.id;

import java.util.UUID;

import org.iotp.infomgt.data.common.ThingType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceId extends UUIDBased implements EntityId {

  private static final long serialVersionUID = 1L;

  @JsonCreator
  public DeviceId(@JsonProperty("id") UUID id) {
    super(id);
  }

  public static DeviceId fromString(String deviceId) {
    return new DeviceId(UUID.fromString(deviceId));
  }

  @JsonIgnore
  @Override
  public ThingType getEntityType() {
    return ThingType.DEVICE;
  }
}
