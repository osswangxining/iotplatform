package org.iotp.server.service.security.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class AuthMethodNotSupportedException extends AuthenticationServiceException {

  /**
  * 
  */
  private static final long serialVersionUID = -3381004077779621715L;

  public AuthMethodNotSupportedException(String msg) {
    super(msg);
  }
}
