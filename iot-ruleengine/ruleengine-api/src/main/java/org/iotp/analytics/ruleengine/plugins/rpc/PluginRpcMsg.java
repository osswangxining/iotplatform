package org.iotp.analytics.ruleengine.plugins.rpc;

import org.iotp.analytics.ruleengine.plugins.msg.ToPluginActorMsg;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class PluginRpcMsg implements ToPluginActorMsg {

  private final TenantId tenantId;
  private final PluginId pluginId;
  @Getter
  private final RpcMsg rpcMsg;

  @Override
  public TenantId getPluginTenantId() {
    return tenantId;
  }

  @Override
  public PluginId getPluginId() {
    return pluginId;
  }

}
