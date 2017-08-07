package org.iotp.analytics.ruleengine.plugins.msg;

import lombok.Data;

/**
 */
@Data
public class TimeoutMsg<T> {
  private final T id;
  private final long timeout;
}
