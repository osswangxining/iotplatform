package org.iotp.analytics.ruleengine.common.msg.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.infomgt.data.kv.KvEntry;

public class BasicTelemetryUploadRequest extends BasicRequest implements TelemetryUploadRequest {

  private static final long serialVersionUID = 1L;

  private final Map<Long, List<KvEntry>> data;

  public BasicTelemetryUploadRequest() {
    this(DEFAULT_REQUEST_ID);
  }

  public BasicTelemetryUploadRequest(Integer requestId) {
    super(requestId);
    this.data = new HashMap<>();
  }

  public void add(long ts, KvEntry entry) {
    List<KvEntry> tsEntries = data.get(ts);
    if (tsEntries == null) {
      tsEntries = new ArrayList<>();
      data.put(ts, tsEntries);
    }
    tsEntries.add(entry);
  }

  @Override
  public MsgType getMsgType() {
    return MsgType.POST_TELEMETRY_REQUEST;
  }

  @Override
  public Map<Long, List<KvEntry>> getData() {
    return data;
  }

  @Override
  public String toString() {
    return "BasicTelemetryUploadRequest [data=" + data + "]";
  }

}
