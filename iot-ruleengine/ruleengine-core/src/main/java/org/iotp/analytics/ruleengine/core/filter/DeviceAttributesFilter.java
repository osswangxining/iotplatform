package org.iotp.analytics.ruleengine.core.filter;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.iotp.analytics.ruleengine.annotation.Filter;
import org.iotp.analytics.ruleengine.api.device.DeviceAttributes;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.common.msg.core.UpdateAttributesRequest;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Filter(name = "Device Attributes Filter", descriptor = "JsFilterDescriptor.json", configuration = JsFilterConfiguration.class)
@Slf4j
public class DeviceAttributesFilter extends BasicJsFilter {

  @Override
  protected boolean doFilter(RuleContext ctx, ToDeviceActorMsg msg) throws ScriptException {
    return evaluator
        .execute(toBindings(ctx.getDeviceMetaData().getDeviceAttributes(), msg != null ? msg.getPayload() : null));
  }

  private Bindings toBindings(DeviceAttributes attributes, FromDeviceMsg msg) {
    Bindings bindings = NashornJsEvaluator.getAttributeBindings(attributes);

    if (msg != null) {
      switch (msg.getMsgType()) {
      case POST_ATTRIBUTES_REQUEST:
        bindings = NashornJsEvaluator.updateBindings(bindings, (UpdateAttributesRequest) msg);
        break;
      }
    }

    return bindings;
  }

}
