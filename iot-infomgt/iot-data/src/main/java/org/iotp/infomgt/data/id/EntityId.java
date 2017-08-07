package org.iotp.infomgt.data.id;

import java.util.UUID;

import org.iotp.infomgt.data.common.ThingType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = EntityIdDeserializer.class)
@JsonSerialize(using = EntityIdSerializer.class)
public interface EntityId {

  UUID NULL_UUID = UUID.fromString("13814000-1dd2-11b2-8080-808080808080");

  UUID getId();

  ThingType getEntityType();

  @JsonIgnore
  default boolean isNullUid() {
    return NULL_UUID.equals(getId());
  }

}
