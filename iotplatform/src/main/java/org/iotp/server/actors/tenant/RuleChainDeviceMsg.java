package org.iotp.server.actors.tenant;

import org.iotp.analytics.ruleengine.common.msg.asset.ToAssetActorMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.server.actors.rule.RuleActorChain;

public class RuleChainDeviceMsg {

  private final ToDeviceActorMsg toDeviceActorMsg;
  private final ToAssetActorMsg toAssetActorMsg;
  private final RuleActorChain ruleChain;

  public RuleChainDeviceMsg(ToDeviceActorMsg toDeviceActorMsg, ToAssetActorMsg toAssetActorMsg,
      RuleActorChain ruleChain) {
    super();
    this.toDeviceActorMsg = toDeviceActorMsg;
    this.toAssetActorMsg = toAssetActorMsg;
    this.ruleChain = ruleChain;
  }

  public ToDeviceActorMsg getToDeviceActorMsg() {
    return toDeviceActorMsg;
  }

  public ToAssetActorMsg getToAssetActorMsg() {
    return toAssetActorMsg;
  }

  public RuleActorChain getRuleChain() {
    return ruleChain;
  }

}
