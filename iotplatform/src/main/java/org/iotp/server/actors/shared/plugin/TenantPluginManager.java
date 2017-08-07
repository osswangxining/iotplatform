package org.iotp.server.actors.shared.plugin;

import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.PageDataIterable.FetchFunction;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.DefaultActorService;

public class TenantPluginManager extends PluginManager {

  private final TenantId tenantId;

  public TenantPluginManager(ActorSystemContext systemContext, TenantId tenantId) {
    super(systemContext);
    this.tenantId = tenantId;
  }

  @Override
  FetchFunction<PluginMetaData> getFetchPluginsFunction() {
    return link -> pluginService.findTenantPlugins(tenantId, link);
  }

  @Override
  TenantId getTenantId() {
    return tenantId;
  }

  @Override
  protected String getDispatcherName() {
    return DefaultActorService.TENANT_PLUGIN_DISPATCHER_NAME;
  }

}
