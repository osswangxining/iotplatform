package org.iotp.infomgt.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class WidgetTypeId extends UUIDBased {

  private static final long serialVersionUID = 1L;

  @JsonCreator
  public WidgetTypeId(@JsonProperty("id") UUID id) {
    super(id);
  }

}
