package org.iotp.analytics.ruleengine.common.msg.session;

public interface AdaptorToSessionActorMsg extends SessionMsg {

  FromDeviceMsg getMsg();

}
