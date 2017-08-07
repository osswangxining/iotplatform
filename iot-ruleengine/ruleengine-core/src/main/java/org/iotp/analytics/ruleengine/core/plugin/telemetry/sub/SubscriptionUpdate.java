package org.iotp.analytics.ruleengine.core.plugin.telemetry.sub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.iotp.infomgt.data.kv.TsKvEntry;

public class SubscriptionUpdate {

  private int subscriptionId;
  private int errorCode;
  private String errorMsg;
  private Map<String, List<Object>> data;

  public SubscriptionUpdate(int subscriptionId, List<TsKvEntry> data) {
    super();
    this.subscriptionId = subscriptionId;
    this.data = new TreeMap<>();
    for (TsKvEntry tsEntry : data) {
      List<Object> values = this.data.computeIfAbsent(tsEntry.getKey(), k -> new ArrayList<>());
      Object[] value = new Object[2];
      value[0] = tsEntry.getTs();
      value[1] = tsEntry.getValueAsString();
      values.add(value);
    }
  }

  public SubscriptionUpdate(int subscriptionId, Map<String, List<Object>> data) {
    super();
    this.subscriptionId = subscriptionId;
    this.data = data;
  }

  public SubscriptionUpdate(int subscriptionId, SubscriptionErrorCode errorCode) {
    this(subscriptionId, errorCode, null);
  }

  public SubscriptionUpdate(int subscriptionId, SubscriptionErrorCode errorCode, String errorMsg) {
    super();
    this.subscriptionId = subscriptionId;
    this.errorCode = errorCode.getCode();
    this.errorMsg = errorMsg != null ? errorMsg : errorCode.getDefaultMsg();
  }

  public int getSubscriptionId() {
    return subscriptionId;
  }

  public Map<String, List<Object>> getData() {
    return data;
  }

  public Map<String, Long> getLatestValues() {
    if (data == null) {
      return Collections.emptyMap();
    } else {
      return data.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
        List<Object> data = e.getValue();
        Object[] latest = (Object[]) data.get(data.size() - 1);
        return (long) latest[0];
      }));
    }
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  @Override
  public String toString() {
    return "SubscriptionUpdate [subscriptionId=" + subscriptionId + ", errorCode=" + errorCode + ", errorMsg="
        + errorMsg + ", data=" + data + "]";
  }
}
