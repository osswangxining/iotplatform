package org.iotp.analytics.ruleengine.plugins.msg;

import java.util.UUID;

/**
 */
public final class TimeoutUUIDMsg extends TimeoutMsg<UUID> {

  public TimeoutUUIDMsg(UUID id, long timeout) {
    super(id, timeout);
  }

}
