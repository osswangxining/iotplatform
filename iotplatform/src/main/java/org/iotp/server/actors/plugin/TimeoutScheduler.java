package org.iotp.server.actors.plugin;

/**
 */
public interface TimeoutScheduler {

  void scheduleMsgWithDelay(Object msg, long delayInMs);

}
