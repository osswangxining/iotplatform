package org.iotp.analytics.ruleengine.common.msg.cluster;

import lombok.Data;

/**
 */
@Data
public final class ClusterEventMsg {

  private final ServerAddress serverAddress;
  private final boolean added;

}
