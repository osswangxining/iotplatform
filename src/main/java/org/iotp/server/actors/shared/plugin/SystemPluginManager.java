package org.iotp.server.actors.shared.plugin;

import org.iotp.infomgt.dao.plugin.BasePluginService;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.PageDataIterable.FetchFunction;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.DefaultActorService;

public class SystemPluginManager extends PluginManager {

  public SystemPluginManager(ActorSystemContext systemContext) {
    super(systemContext);
  }

  @Override
  FetchFunction<PluginMetaData> getFetchPluginsFunction() {
    return pluginService::findSystemPlugins;
  }

  @Override
  TenantId getTenantId() {
    return BasePluginService.SYSTEM_TENANT;
  }

  @Override
  protected String getDispatcherName() {
    return DefaultActorService.SYSTEM_PLUGIN_DISPATCHER_NAME;
  }
}
