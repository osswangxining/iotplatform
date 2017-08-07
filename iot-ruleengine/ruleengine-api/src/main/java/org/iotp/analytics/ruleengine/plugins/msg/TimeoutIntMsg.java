package org.iotp.analytics.ruleengine.plugins.msg;

/**
 */
public final class TimeoutIntMsg extends TimeoutMsg<Integer> {

  public TimeoutIntMsg(Integer id, long timeout) {
    super(id, timeout);
  }

}
