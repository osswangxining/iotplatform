package org.iotp.infomgt.data.kv;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents attribute or any other KV data entry
 *
 */
public interface KvEntry extends Serializable {

    String getKey();

    DataType getDataType();

    Optional<String> getStrValue();

    Optional<Long> getLongValue();

    Optional<Boolean> getBooleanValue();

    Optional<Double> getDoubleValue();

    String getValueAsString();

    Object getValue();
}
