package org.iotp.infomgt.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetCredentialsId extends UUIDBased {

  /**
   * 
   */
  private static final long serialVersionUID = 7581796336024302056L;

  @JsonCreator
  public AssetCredentialsId(@JsonProperty("id") UUID id) {
    super(id);
  }
}
