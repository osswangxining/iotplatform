package org.iotp.server.service.cluster.routing;

import java.util.Optional;
import java.util.UUID;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.infomgt.data.id.EntityId;

/**
 */
public interface ClusterRoutingService {

    ServerAddress getCurrentServer();

    Optional<ServerAddress> resolveByUuid(UUID uuid);

    Optional<ServerAddress> resolveById(EntityId entityId);

}
