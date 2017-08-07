package org.iotp.analytics.ruleengine.common.msg.session;

import org.iotp.analytics.ruleengine.common.msg.aware.SessionAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ex.SessionException;

public interface SessionContext extends SessionAwareMsg {

  SessionType getSessionType();

  void onMsg(SessionActorToAdaptorMsg msg) throws SessionException;

  void onMsg(SessionCtrlMsg msg) throws SessionException;

  boolean isClosed();

  long getTimeout();

}
