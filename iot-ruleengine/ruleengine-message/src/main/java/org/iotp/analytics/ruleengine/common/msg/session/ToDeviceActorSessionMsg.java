package org.iotp.analytics.ruleengine.common.msg.session;

import org.iotp.analytics.ruleengine.common.msg.aware.CustomerAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.DeviceAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.SessionAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.TenantAwareMsg;

public interface ToDeviceActorSessionMsg extends DeviceAwareMsg, CustomerAwareMsg, TenantAwareMsg, SessionAwareMsg {

  AdaptorToSessionActorMsg getSessionMsg();

}
