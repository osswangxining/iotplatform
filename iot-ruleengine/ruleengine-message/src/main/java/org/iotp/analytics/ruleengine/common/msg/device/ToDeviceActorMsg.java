package org.iotp.analytics.ruleengine.common.msg.device;

import java.io.Serializable;
import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.aware.CustomerAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.DeviceAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.TenantAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.infomgt.data.id.SessionId;

public interface ToDeviceActorMsg extends DeviceAwareMsg, CustomerAwareMsg, TenantAwareMsg, Serializable {

  SessionId getSessionId();

  SessionType getSessionType();

  Optional<ServerAddress> getServerAddress();

  FromDeviceMsg getPayload();

  ToDeviceActorMsg toOtherAddress(ServerAddress otherAddress);
}
