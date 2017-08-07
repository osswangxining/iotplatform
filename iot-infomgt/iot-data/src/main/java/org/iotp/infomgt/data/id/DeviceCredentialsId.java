package org.iotp.infomgt.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceCredentialsId extends UUIDBased {

  /**
   * 
   */
  private static final long serialVersionUID = 8608844130587784917L;

  @JsonCreator
  public DeviceCredentialsId(@JsonProperty("id") UUID id) {
    super(id);
  }
}
