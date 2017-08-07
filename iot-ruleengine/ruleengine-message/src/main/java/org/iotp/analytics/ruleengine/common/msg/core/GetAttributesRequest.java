package org.iotp.analytics.ruleengine.common.msg.core;

import java.util.Optional;
import java.util.Set;

import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceRequestMsg;

public interface GetAttributesRequest extends FromDeviceRequestMsg {

  Optional<Set<String>> getClientAttributeNames();

  Optional<Set<String>> getSharedAttributeNames();

}
