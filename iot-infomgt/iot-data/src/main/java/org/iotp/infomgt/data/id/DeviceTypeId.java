package org.iotp.infomgt.data.id;

import java.util.UUID;

import org.iotp.infomgt.data.common.ThingType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceTypeId extends UUIDBased implements EntityId {

  private static final long serialVersionUID = 1L;

  @JsonCreator
  public DeviceTypeId(@JsonProperty("id") UUID id) {
    super(id);
  }

  public static DeviceTypeId fromString(String deviceTypeId) {
    return new DeviceTypeId(UUID.fromString(deviceTypeId));
  }

  @JsonIgnore
  @Override
  public ThingType getEntityType() {
    return ThingType.DEVICETYPE;
  }
}
