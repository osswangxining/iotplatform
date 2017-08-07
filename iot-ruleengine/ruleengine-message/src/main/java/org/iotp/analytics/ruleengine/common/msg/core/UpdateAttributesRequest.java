package org.iotp.analytics.ruleengine.common.msg.core;

import java.util.Set;

import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceRequestMsg;
import org.iotp.infomgt.data.kv.AttributeKvEntry;

public interface UpdateAttributesRequest extends FromDeviceRequestMsg {

    Set<AttributeKvEntry> getAttributes();

}
