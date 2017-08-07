package org.iotp.infomgt.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import org.iotp.infomgt.data.common.ThingType;

public class AlarmId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public AlarmId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static AlarmId fromString(String alarmId) {
        return new AlarmId(UUID.fromString(alarmId));
    }

    @JsonIgnore
    @Override
    public ThingType getEntityType() {
        return ThingType.ALARM;
    }
}
