package org.iotp.server.actors.shared.plugin;

import java.util.HashMap;
import java.util.Map;

import org.iotp.infomgt.dao.plugin.PluginService;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.PageDataIterable;
import org.iotp.infomgt.data.page.PageDataIterable.FetchFunction;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.plugin.PluginActor;
import org.iotp.server.actors.service.ContextAwareActor;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PluginManager {

  protected final ActorSystemContext systemContext;
  protected final PluginService pluginService;
  protected final Map<PluginId, ActorRef> pluginActors;

  public PluginManager(ActorSystemContext systemContext) {
    this.systemContext = systemContext;
    this.pluginService = systemContext.getPluginService();
    this.pluginActors = new HashMap<>();
  }

  public void init(ActorContext context) {
    PageDataIterable<PluginMetaData> pluginIterator = new PageDataIterable<>(getFetchPluginsFunction(),
        ContextAwareActor.ENTITY_PACK_LIMIT);
    for (PluginMetaData plugin : pluginIterator) {
      log.debug("[{}] Creating plugin actor", plugin.getId());
      getOrCreatePluginActor(context, plugin.getId());
      log.debug("Plugin actor created.");
    }
  }

  abstract FetchFunction<PluginMetaData> getFetchPluginsFunction();

  abstract TenantId getTenantId();

  abstract String getDispatcherName();

  public ActorRef getOrCreatePluginActor(ActorContext context, PluginId pluginId) {
    return pluginActors.computeIfAbsent(pluginId,
        pId -> context.actorOf(Props.create(new PluginActor.ActorCreator(systemContext, getTenantId(), pId))
            .withDispatcher(getDispatcherName()), pId.toString()));
  }

  public void broadcast(Object msg) {
    pluginActors.values().forEach(actorRef -> actorRef.tell(msg, ActorRef.noSender()));
  }

  public void remove(PluginId id) {
    pluginActors.remove(id);
  }
}
