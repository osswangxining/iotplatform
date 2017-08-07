package org.iotp.server.service.cluster.discovery;

import java.util.List;

/**
 */
public interface DiscoveryService {

  void publishCurrentServer();

  void unpublishCurrentServer();

  ServerInstance getCurrentServer();

  List<ServerInstance> getOtherServers();

  boolean addListener(DiscoveryServiceListener listener);

  boolean removeListener(DiscoveryServiceListener listener);

}
