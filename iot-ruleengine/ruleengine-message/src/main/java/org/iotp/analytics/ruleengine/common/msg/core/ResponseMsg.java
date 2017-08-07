package org.iotp.analytics.ruleengine.common.msg.core;

import java.io.Serializable;
import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;

public interface ResponseMsg<T extends Serializable> extends ToDeviceMsg {

  MsgType getRequestMsgType();

  Integer getRequestId();

  Optional<Exception> getError();

  Optional<T> getData();
}
