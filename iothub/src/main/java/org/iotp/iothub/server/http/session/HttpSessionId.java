package org.iotp.iothub.server.http.session;

import java.util.UUID;

import org.iotp.infomgt.data.id.SessionId;

/**
 */
public class HttpSessionId implements SessionId {

  /**
   * 
   */
  private static final long serialVersionUID = -303616153334001399L;
  private final UUID id;

  public HttpSessionId() {
    this.id = UUID.randomUUID();
  }

  @Override
  public String toUidStr() {
    return id.toString();
  }
}
