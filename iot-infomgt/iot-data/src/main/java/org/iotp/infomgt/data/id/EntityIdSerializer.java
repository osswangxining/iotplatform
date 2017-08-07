package org.iotp.infomgt.data.id;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EntityIdSerializer extends JsonSerializer<EntityId> {

  @Override
  public void serialize(EntityId value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException, JsonProcessingException {
    gen.writeStartObject();
    gen.writeStringField("entityType", value.getEntityType().name());
    gen.writeStringField("id", value.getId().toString());
    gen.writeEndObject();
  }
}
