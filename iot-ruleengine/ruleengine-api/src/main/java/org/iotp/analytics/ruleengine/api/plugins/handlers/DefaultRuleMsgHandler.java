package org.iotp.analytics.ruleengine.api.plugins.handlers;

import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.plugins.msg.GetAttributesRequestRuleToPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.TelemetryUploadRequestRuleToPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.UpdateAttributesRequestRuleToPluginMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class DefaultRuleMsgHandler implements RuleMsgHandler {

  @Override
  public void process(PluginContext ctx, TenantId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg)
      throws RuleException {
    if (msg instanceof TelemetryUploadRequestRuleToPluginMsg) {
      handleTelemetryUploadRequest(ctx, tenantId, ruleId, (TelemetryUploadRequestRuleToPluginMsg) msg);
    } else if (msg instanceof UpdateAttributesRequestRuleToPluginMsg) {
      handleUpdateAttributesRequest(ctx, tenantId, ruleId, (UpdateAttributesRequestRuleToPluginMsg) msg);
    } else if (msg instanceof GetAttributesRequestRuleToPluginMsg) {
      handleGetAttributesRequest(ctx, tenantId, ruleId, (GetAttributesRequestRuleToPluginMsg) msg);
    }
    // TODO: handle subscriptions to attribute updates.
  }

  protected void handleGetAttributesRequest(PluginContext ctx, TenantId tenantId, RuleId ruleId,
      GetAttributesRequestRuleToPluginMsg msg) {
    msgTypeNotSupported(msg.getPayload().getMsgType());
  }

  protected void handleUpdateAttributesRequest(PluginContext ctx, TenantId tenantId, RuleId ruleId,
      UpdateAttributesRequestRuleToPluginMsg msg) {
    msgTypeNotSupported(msg.getPayload().getMsgType());
  }

  protected void handleTelemetryUploadRequest(PluginContext ctx, TenantId tenantId, RuleId ruleId,
      TelemetryUploadRequestRuleToPluginMsg msg) {
    msgTypeNotSupported(msg.getPayload().getMsgType());
  }

  private void msgTypeNotSupported(MsgType msgType) {
    throw new RuntimeException("Not supported msg type: " + msgType + "!");
  }

}
