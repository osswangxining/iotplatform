package org.iotp.analytics.ruleengine.core.action.rpc;

import java.util.Optional;

import org.iotp.analytics.ruleengine.annotation.Action;
import org.iotp.analytics.ruleengine.annotation.EmptyComponentConfiguration;
import org.iotp.analytics.ruleengine.api.plugins.PluginAction;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.api.rules.RuleProcessingMetaData;
import org.iotp.analytics.ruleengine.api.rules.SimpleRuleLifecycleComponent;
import org.iotp.analytics.ruleengine.common.msg.core.ToServerRpcRequestMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.analytics.ruleengine.plugins.msg.PluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RpcRequestRuleToPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RpcResponsePluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;

@Action(name = "RPC Plugin Action")
public class RpcPluginAction extends SimpleRuleLifecycleComponent implements PluginAction<EmptyComponentConfiguration> {

  public void init(EmptyComponentConfiguration configuration) {
  }

  @Override
  public Optional<RuleToPluginMsg<?>> convert(RuleContext ctx, ToDeviceActorMsg toDeviceActorMsg,
      RuleProcessingMetaData deviceMsgMd) {
    FromDeviceMsg msg = toDeviceActorMsg.getPayload();
    if (msg.getMsgType() == MsgType.TO_SERVER_RPC_REQUEST) {
      ToServerRpcRequestMsg payload = (ToServerRpcRequestMsg) msg;
      return Optional.of(new RpcRequestRuleToPluginMsg(toDeviceActorMsg.getTenantId(), toDeviceActorMsg.getCustomerId(),
          toDeviceActorMsg.getDeviceId(), payload));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<ToDeviceMsg> convert(PluginToRuleMsg<?> response) {
    if (response instanceof RpcResponsePluginToRuleMsg) {
      return Optional.of(((RpcResponsePluginToRuleMsg) response).getPayload());
    }
    return Optional.empty();
  }

  @Override
  public boolean isOneWayAction() {
    return false;
  }

}
