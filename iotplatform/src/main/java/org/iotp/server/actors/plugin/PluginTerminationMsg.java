package org.iotp.server.actors.plugin;

import org.iotp.infomgt.data.id.PluginId;
import org.iotp.server.actors.shared.ActorTerminationMsg;

/**
 */
public class PluginTerminationMsg extends ActorTerminationMsg<PluginId> {

  public PluginTerminationMsg(PluginId id) {
    super(id);
  }
}
