package org.iotp.analytics.ruleengine.core.filter;

import java.util.List;

import javax.script.ScriptException;

import org.iotp.analytics.ruleengine.annotation.Filter;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.common.msg.core.TelemetryUploadRequest;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.infomgt.data.kv.KvEntry;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Filter(name = "Device Telemetry Filter", descriptor = "JsFilterDescriptor.json", configuration = JsFilterConfiguration.class)
@Slf4j
public class DeviceTelemetryFilter extends BasicJsFilter {

  @Override
  protected boolean doFilter(RuleContext ctx, ToDeviceActorMsg msg) throws ScriptException {
    FromDeviceMsg deviceMsg = msg.getPayload();
    if (deviceMsg instanceof TelemetryUploadRequest) {
      TelemetryUploadRequest telemetryMsg = (TelemetryUploadRequest) deviceMsg;
      for (List<KvEntry> entries : telemetryMsg.getData().values()) {
        if (evaluator.execute(NashornJsEvaluator.toBindings(entries))) {
          return true;
        }
      }
    }
    return false;
  }

}
