package org.iotp.infomgt.data.kv;

import java.util.Optional;

public class BaseAttributeKvEntry implements AttributeKvEntry {

  /**
  * 
  */
  private static final long serialVersionUID = 4716091761795009303L;
  private final long lastUpdateTs;
  private final KvEntry kv;

  public BaseAttributeKvEntry(KvEntry kv, long lastUpdateTs) {
    this.kv = kv;
    this.lastUpdateTs = lastUpdateTs;
  }

  @Override
  public long getLastUpdateTs() {
    return lastUpdateTs;
  }

  @Override
  public String getKey() {
    return kv.getKey();
  }

  @Override
  public DataType getDataType() {
    return kv.getDataType();
  }

  @Override
  public Optional<String> getStrValue() {
    return kv.getStrValue();
  }

  @Override
  public Optional<Long> getLongValue() {
    return kv.getLongValue();
  }

  @Override
  public Optional<Boolean> getBooleanValue() {
    return kv.getBooleanValue();
  }

  @Override
  public Optional<Double> getDoubleValue() {
    return kv.getDoubleValue();
  }

  @Override
  public String getValueAsString() {
    return kv.getValueAsString();
  }

  @Override
  public Object getValue() {
    return kv.getValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    BaseAttributeKvEntry that = (BaseAttributeKvEntry) o;

    if (lastUpdateTs != that.lastUpdateTs)
      return false;
    return kv.equals(that.kv);

  }

  @Override
  public int hashCode() {
    int result = (int) (lastUpdateTs ^ (lastUpdateTs >>> 32));
    result = 31 * result + kv.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "BaseAttributeKvEntry{" + "lastUpdateTs=" + lastUpdateTs + ", kv=" + kv + '}';
  }
}
