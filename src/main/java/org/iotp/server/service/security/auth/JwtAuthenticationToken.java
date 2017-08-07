package org.iotp.server.service.security.auth;

import org.iotp.server.service.security.model.SecurityUser;
import org.iotp.server.service.security.model.token.RawAccessJwtToken;

public class JwtAuthenticationToken extends AbstractJwtAuthenticationToken {


  /**
   * 
   */
  private static final long serialVersionUID = 3492711332758479879L;

  public JwtAuthenticationToken(RawAccessJwtToken unsafeToken) {
    super(unsafeToken);
  }

  public JwtAuthenticationToken(SecurityUser securityUser) {
    super(securityUser);
  }
}
