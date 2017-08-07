package org.iotp.analytics.ruleengine.common.msg.core;

import java.util.List;
import java.util.Map;

import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceRequestMsg;
import org.iotp.infomgt.data.kv.KvEntry;

public interface TelemetryUploadRequest extends FromDeviceRequestMsg {

  Map<Long, List<KvEntry>> getData();

}
