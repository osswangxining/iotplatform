package org.iotp.server.actors.shared;

import java.io.Serializable;

import org.iotp.infomgt.data.id.SessionId;

import lombok.Data;

@Data
public class SessionTimeoutMsg implements Serializable {

  private static final long serialVersionUID = 1L;

  private final SessionId sessionId;
}
