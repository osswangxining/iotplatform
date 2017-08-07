package org.iotp.infomgt.data.kv;

import java.util.Objects;
import java.util.Optional;

public class LongDataEntry extends BasicKvEntry {

    /**
   * 
   */
  private static final long serialVersionUID = 6022625395298774462L;
    private final Long value;

    public LongDataEntry(String key, Long value) {
        super(key);
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.LONG;
    }

    @Override
    public Optional<Long> getLongValue() {
        return Optional.of(value);
    }

    @Override
    public Object getValue() {
        return value;
    }

    public Long getAdd(long b) {
      return value.longValue() + b;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongDataEntry)) return false;
        if (!super.equals(o)) return false;
        LongDataEntry that = (LongDataEntry) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return "LongDataEntry{" +
                "value=" + value +
                "} " + super.toString();
    }
    
    @Override
    public String getValueAsString() {
        return Long.toString(value);
    }
}
