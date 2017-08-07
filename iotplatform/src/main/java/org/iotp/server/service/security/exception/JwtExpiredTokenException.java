package org.iotp.server.service.security.exception;

import org.iotp.server.service.security.model.token.JwtToken;
import org.springframework.security.core.AuthenticationException;

public class JwtExpiredTokenException extends AuthenticationException {

  /**
   * 
   */
  private static final long serialVersionUID = 4305891026007880651L;
  private JwtToken token;

  public JwtExpiredTokenException(String msg) {
    super(msg);
  }

  public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
    super(msg, t);
    this.token = token;
  }

  public String token() {
    return this.token.getToken();
  }
}
