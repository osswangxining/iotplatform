package org.iotp.analytics.ruleengine.common.msg.core;

import java.io.Serializable;

import org.iotp.analytics.ruleengine.common.msg.aware.SessionAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;

/**
 */
public interface ToDeviceSessionActorMsg extends SessionAwareMsg, Serializable {

  ToDeviceMsg getMsg();
}
