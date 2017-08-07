package org.iotp.analytics.ruleengine.api.device;

import java.io.Serializable;

import org.iotp.analytics.ruleengine.common.msg.aware.DeviceAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.TenantAwareMsg;

/**
 */
public interface ToDeviceActorNotificationMsg extends TenantAwareMsg, DeviceAwareMsg, Serializable {

}
