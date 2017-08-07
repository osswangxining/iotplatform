package org.iotp.server.service.cluster.discovery;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.server.gen.discovery.ServerInstanceProtos.ServerInfo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 */
@ToString
@EqualsAndHashCode(exclude = {"serverInfo", "serverAddress"})
public final class ServerInstance implements Comparable<ServerInstance> {

    @Getter(AccessLevel.PACKAGE)
    private final ServerInfo serverInfo;
    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final ServerAddress serverAddress;

    public ServerInstance(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        this.host = serverInfo.getHost();
        this.port = serverInfo.getPort();
        this.serverAddress = new ServerAddress(host, port);
    }

    @Override
    public int compareTo(ServerInstance o) {
        return this.serverAddress.compareTo(o.serverAddress);
    }
}
