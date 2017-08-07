package org.iotp.server.actors.device;

import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;

import lombok.Data;

/**
 */
@Data
public class SessionInfo {
  private final SessionType type;
  private final Optional<ServerAddress> server;
}
