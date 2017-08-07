package org.iotp.server.actors.device;

import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequestPluginMsg;

import lombok.Data;

/**
 */
@Data
public class ToDeviceRpcRequestMetadata {
  private final ToDeviceRpcRequestPluginMsg msg;
  private final boolean sent;
}
