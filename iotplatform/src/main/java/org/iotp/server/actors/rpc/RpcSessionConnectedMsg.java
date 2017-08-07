package org.iotp.server.actors.rpc;

import java.util.UUID;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;

import lombok.Data;

/**
 */
@Data
public final class RpcSessionConnectedMsg {

    private final ServerAddress remoteAddress;
    private final UUID id;
}
