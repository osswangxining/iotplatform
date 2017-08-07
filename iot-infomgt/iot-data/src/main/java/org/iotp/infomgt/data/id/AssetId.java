package org.iotp.infomgt.data.id;

import java.util.UUID;

import org.iotp.infomgt.data.common.ThingType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetId extends DeviceId {

  /**
  * 
  */
  private static final long serialVersionUID = 2103713328154494886L;

  @JsonCreator
  public AssetId(@JsonProperty("id") UUID id) {
    super(id);
  }

  public static AssetId fromString(String assetId) {
    return new AssetId(UUID.fromString(assetId));
  }

  @JsonIgnore
  @Override
  public ThingType getEntityType() {
    return ThingType.ASSET;
  }
}
