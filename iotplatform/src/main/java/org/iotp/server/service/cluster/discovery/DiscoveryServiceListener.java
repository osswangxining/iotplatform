package org.iotp.server.service.cluster.discovery;

/**
 */
public interface DiscoveryServiceListener {

  void onServerAdded(ServerInstance server);

  void onServerUpdated(ServerInstance server);

  void onServerRemoved(ServerInstance server);
}
