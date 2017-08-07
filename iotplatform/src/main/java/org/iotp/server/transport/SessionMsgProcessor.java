package org.iotp.server.transport;

import org.iotp.analytics.ruleengine.common.msg.aware.SessionAwareMsg;

public interface SessionMsgProcessor {

  void process(SessionAwareMsg msg);

}
