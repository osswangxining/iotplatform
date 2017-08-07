package org.iotp.analytics.ruleengine.common.msg.cluster;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 */
@Data
@EqualsAndHashCode
public class ServerAddress implements Comparable<ServerAddress>, Serializable {

  private final String host;
  private final int port;

  @Override
  public int compareTo(ServerAddress o) {
    int result = this.host.compareTo(o.host);
    if (result == 0) {
      result = this.port - o.port;
    }
    return result;
  }

  @Override
  public String toString() {
    return '[' + host + ':' + port + ']';
  }
}
