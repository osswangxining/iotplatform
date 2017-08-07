package org.iotp.infomgt.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventId extends UUIDBased {

  private static final long serialVersionUID = 1L;

  @JsonCreator
  public EventId(@JsonProperty("id") UUID id) {
    super(id);
  }

  public static EventId fromString(String eventId) {
    return new EventId(UUID.fromString(eventId));
  }
}
