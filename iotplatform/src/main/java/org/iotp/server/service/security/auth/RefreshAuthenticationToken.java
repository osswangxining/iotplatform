package org.iotp.server.service.security.auth;

import org.iotp.server.service.security.model.SecurityUser;
import org.iotp.server.service.security.model.token.RawAccessJwtToken;

public class RefreshAuthenticationToken extends AbstractJwtAuthenticationToken {

  /**
   * 
   */
  private static final long serialVersionUID = -206631383172956771L;

  public RefreshAuthenticationToken(RawAccessJwtToken unsafeToken) {
    super(unsafeToken);
  }

  public RefreshAuthenticationToken(SecurityUser securityUser) {
    super(securityUser);
  }
}
