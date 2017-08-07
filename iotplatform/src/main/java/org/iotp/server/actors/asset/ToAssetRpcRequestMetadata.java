package org.iotp.server.actors.asset;

import org.iotp.analytics.ruleengine.plugins.msg.ToAssetRpcRequestPluginMsg;

import lombok.Data;

/**
 */
@Data
public class ToAssetRpcRequestMetadata {
  private final ToAssetRpcRequestPluginMsg msg;
  private final boolean sent;
}
