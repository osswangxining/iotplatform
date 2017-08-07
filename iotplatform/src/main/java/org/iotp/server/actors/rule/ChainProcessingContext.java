package org.iotp.server.actors.rule;

import org.iotp.analytics.ruleengine.api.device.DeviceAttributes;
import org.iotp.analytics.ruleengine.api.device.DeviceMetaData;
import org.iotp.analytics.ruleengine.common.msg.core.RuleEngineError;
import org.iotp.analytics.ruleengine.common.msg.core.RuleEngineErrorMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;

import akka.actor.ActorRef;

public class ChainProcessingContext {

  private final ChainProcessingMetaData md;
  private final int index;
  private final RuleEngineError error;
  private ToDeviceMsg response;

  public ChainProcessingContext(ChainProcessingMetaData md) {
    super();
    this.md = md;
    this.index = 0;
    this.error = RuleEngineError.NO_RULES;
  }

  private ChainProcessingContext(ChainProcessingContext other, int indexOffset, RuleEngineError error) {
    super();
    this.md = other.md;
    this.index = other.index + indexOffset;
    this.error = error;
    this.response = other.response;

    if (this.index < 0 || this.index >= this.md.chain.size()) {
      throw new IllegalArgumentException("Can't apply offset " + indexOffset + " to the chain!");
    }
  }

  public ActorRef getDeviceActor() {
    return md.originator;
  }

  public ActorRef getCurrentActor() {
    return md.chain.getRuleActorMd(index).getActorRef();
  }

  public boolean hasNext() {
    return (getChainLength() - 1) > index;
  }

  public boolean isFailure() {
    return (error != null && error.isCritical()) || (response != null && !response.isSuccess());
  }

  public ChainProcessingContext getNext() {
    return new ChainProcessingContext(this, 1, this.error);
  }

  public ChainProcessingContext withError(RuleEngineError error) {
    if (error != null && (this.error == null || this.error.getPriority() < error.getPriority())) {
      return new ChainProcessingContext(this, 0, error);
    } else {
      return this;
    }
  }

  public int getChainLength() {
    return md.chain.size();
  }

  public ToDeviceActorMsg getInMsg() {
    return md.inMsg;
  }

  public DeviceMetaData getDeviceMetaData() {
    return md.deviceMetaData;
  }

  public String getDeviceName() {
    return md.deviceMetaData.getDeviceName();
  }

  public String getDeviceType() {
    return md.deviceMetaData.getDeviceType();
  }

  public DeviceAttributes getAttributes() {
    return md.deviceMetaData.getDeviceAttributes();
  }

  public ToDeviceMsg getResponse() {
    return response;
  }

  public void mergeResponse(ToDeviceMsg response) {
    // TODO add merge logic
    this.response = response;
  }

  public RuleEngineErrorMsg getError() {
    return new RuleEngineErrorMsg(md.inMsg.getPayload().getMsgType(), error);
  }
}
