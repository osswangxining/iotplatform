package org.iotp.iothub.server;

import java.io.Serializable;

public class ThingsKVData implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -937006849924670282L;
  private String key;
  private Object value;

  public ThingsKVData(String key, Object value) {
    this.key = key;
    this.value = value;
  }
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
