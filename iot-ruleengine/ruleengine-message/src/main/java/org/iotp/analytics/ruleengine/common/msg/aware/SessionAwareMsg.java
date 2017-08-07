package org.iotp.analytics.ruleengine.common.msg.aware;

import org.iotp.infomgt.data.id.SessionId;

public interface SessionAwareMsg {

  SessionId getSessionId();

}
