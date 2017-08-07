package org.iotp.analytics.ruleengine.common.msg.session;

public interface SessionActorToAdaptorMsg extends SessionMsg {

  ToDeviceMsg getMsg();

}
