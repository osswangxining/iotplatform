package org.iotp.infomgt.data.id;

import java.util.UUID;

import org.iotp.infomgt.data.common.ThingType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserId extends UUIDBased implements EntityId {

  /**
   * 
   */
  private static final long serialVersionUID = 2547504885530951872L;

  @JsonCreator
  public UserId(@JsonProperty("id") UUID id) {
    super(id);
  }

  public static UserId fromString(String userId) {
    return new UserId(UUID.fromString(userId));
  }

  @JsonIgnore
  @Override
  public ThingType getEntityType() {
    return ThingType.USER;
  }

}
