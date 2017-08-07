package org.iotp.server.service.cluster.routing;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.annotation.PostConstruct;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.server.service.cluster.discovery.DiscoveryService;
import org.iotp.server.service.cluster.discovery.DiscoveryServiceListener;
import org.iotp.server.service.cluster.discovery.ServerInstance;
import org.iotp.server.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

import lombok.extern.slf4j.Slf4j;

/**
 * Cluster service implementation based on consistent hash ring
 */

@Service
@Slf4j
public class ConsistentClusterRoutingService implements ClusterRoutingService, DiscoveryServiceListener {

  @Autowired
  private DiscoveryService discoveryService;

  @Value("${cluster.hash_function_name}")
  private String hashFunctionName;
  @Value("${cluster.vitrual_nodes_size}")
  private Integer virtualNodesSize;

  private ServerInstance currentServer;

  private HashFunction hashFunction;

  private final ConcurrentNavigableMap<Long, ServerInstance> circle = new ConcurrentSkipListMap<>();

  @PostConstruct
  public void init() {
    log.info("Initializing Cluster routing service!");
    hashFunction = MiscUtils.forName(hashFunctionName);
    discoveryService.addListener(this);
    this.currentServer = discoveryService.getCurrentServer();
    addNode(discoveryService.getCurrentServer());
    for (ServerInstance instance : discoveryService.getOtherServers()) {
      addNode(instance);
    }
    logCircle();
    log.info("Cluster routing service initialized!");
  }

  @Override
  public ServerAddress getCurrentServer() {
    return discoveryService.getCurrentServer().getServerAddress();
  }

  @Override
  public Optional<ServerAddress> resolveById(EntityId entityId) {
    return resolveByUuid(entityId.getId());
  }

  @Override
  public Optional<ServerAddress> resolveByUuid(UUID uuid) {
    Assert.notNull(uuid);
    if (circle.isEmpty()) {
      return Optional.empty();
    }
    Long hash = hashFunction.newHasher().putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits())
        .hash().asLong();
    if (!circle.containsKey(hash)) {
      ConcurrentNavigableMap<Long, ServerInstance> tailMap = circle.tailMap(hash);
      hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
    }
    ServerInstance result = circle.get(hash);
    if (!currentServer.equals(result)) {
      return Optional.of(result.getServerAddress());
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void onServerAdded(ServerInstance server) {
    log.debug("On server added event: {}", server);
    addNode(server);
    logCircle();
  }

  @Override
  public void onServerUpdated(ServerInstance server) {
    log.debug("Ignoring server onUpdate event: {}", server);
  }

  @Override
  public void onServerRemoved(ServerInstance server) {
    log.debug("On server removed event: {}", server);
    removeNode(server);
    logCircle();
  }

  private void addNode(ServerInstance instance) {
    for (int i = 0; i < virtualNodesSize; i++) {
      circle.put(hash(instance, i).asLong(), instance);
    }
  }

  private void removeNode(ServerInstance instance) {
    for (int i = 0; i < virtualNodesSize; i++) {
      circle.remove(hash(instance, i).asLong());
    }
  }

  private HashCode hash(ServerInstance instance, int i) {
    return hashFunction.newHasher().putString(instance.getHost(), MiscUtils.UTF8).putInt(instance.getPort()).putInt(i)
        .hash();
  }

  private void logCircle() {
    log.trace("Consistent Hash Circle Start");
    circle.entrySet().forEach((e) -> log.debug("{} -> {}", e.getKey(), e.getValue().getServerAddress()));
    log.trace("Consistent Hash Circle End");
  }

}
