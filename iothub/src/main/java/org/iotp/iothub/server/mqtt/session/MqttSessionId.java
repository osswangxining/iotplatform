package org.iotp.iothub.server.mqtt.session;

import java.util.concurrent.atomic.AtomicLong;

import org.iotp.infomgt.data.id.SessionId;

/**
 */
public class MqttSessionId implements SessionId {

  /**
   * 
   */
  private static final long serialVersionUID = -3878779050444861731L;

  private static final AtomicLong idSeq = new AtomicLong();

  private final long id;

  public MqttSessionId() {
    this.id = idSeq.incrementAndGet();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    MqttSessionId that = (MqttSessionId) o;

    return id == that.id;

  }

  @Override
  public String toString() {
    return "MqttSessionId{" + "id=" + id + '}';
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toUidStr() {
    return "mqtt" + id;
  }
}
