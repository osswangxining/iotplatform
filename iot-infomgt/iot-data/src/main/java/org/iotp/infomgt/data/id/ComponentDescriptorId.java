package org.iotp.infomgt.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ComponentDescriptorId extends UUIDBased {

  private static final long serialVersionUID = 1L;

  @JsonCreator
  public ComponentDescriptorId(@JsonProperty("id") UUID id) {
    super(id);
  }
}
