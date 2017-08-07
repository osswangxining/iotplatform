package org.iotp.analytics.ruleengine.core.plugin.time;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.iotp.analytics.ruleengine.annotation.Plugin;
import org.iotp.analytics.ruleengine.api.plugins.AbstractPlugin;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.common.msg.core.ToServerRpcRequestMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToServerRpcResponseMsg;
import org.iotp.analytics.ruleengine.core.action.rpc.RpcPluginAction;
import org.iotp.analytics.ruleengine.plugins.msg.RpcResponsePluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Plugin(name = "Time Plugin", actions = {
    RpcPluginAction.class }, descriptor = "TimePluginDescriptor.json", configuration = TimePluginConfiguration.class)
@Slf4j
public class TimePlugin extends AbstractPlugin<TimePluginConfiguration> implements RuleMsgHandler {

  private DateTimeFormatter formatter;
  private String format;

  @Override
  public void process(PluginContext ctx, TenantId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg)
      throws RuleException {
    if (msg.getPayload() instanceof ToServerRpcRequestMsg) {
      ToServerRpcRequestMsg request = (ToServerRpcRequestMsg) msg.getPayload();

      String reply;
      if (!StringUtils.isEmpty(format)) {
        reply = "\"" + formatter.format(ZonedDateTime.now()) + "\"";
      } else {
        reply = Long.toString(System.currentTimeMillis());
      }
      ToServerRpcResponseMsg response = new ToServerRpcResponseMsg(request.getRequestId(), "{\"time\":" + reply + "}");
      ctx.reply(new RpcResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId, response));
    } else {
      throw new RuntimeException("Not supported msg type: " + msg.getPayload().getClass() + "!");
    }
  }

  @Override
  public void init(TimePluginConfiguration configuration) {
    format = configuration.getTimeFormat();
    if (!StringUtils.isEmpty(format)) {
      formatter = DateTimeFormatter.ofPattern(format);
    }
  }

  @Override
  public void resume(PluginContext ctx) {

  }

  @Override
  public void suspend(PluginContext ctx) {

  }

  @Override
  public void stop(PluginContext ctx) {

  }

  @Override
  protected RuleMsgHandler getRuleMsgHandler() {
    return this;
  }
}
