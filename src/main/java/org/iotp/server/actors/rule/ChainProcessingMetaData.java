package org.iotp.server.actors.rule;

import org.iotp.analytics.ruleengine.api.device.DeviceMetaData;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;

import akka.actor.ActorRef;

/**
 * Immutable part of chain processing data;
 *
 */
public final class ChainProcessingMetaData {

  final RuleActorChain chain;
  final ToDeviceActorMsg inMsg;
  final ActorRef originator;
  final DeviceMetaData deviceMetaData;

  public ChainProcessingMetaData(RuleActorChain chain, ToDeviceActorMsg inMsg, DeviceMetaData deviceMetaData,
      ActorRef originator) {
    super();
    this.chain = chain;
    this.inMsg = inMsg;
    this.originator = originator;
    this.deviceMetaData = deviceMetaData;
  }
}
