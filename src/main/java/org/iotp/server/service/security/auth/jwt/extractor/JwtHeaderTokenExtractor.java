package org.iotp.server.service.security.auth.jwt.extractor;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.iotp.server.config.IoTPSecurityConfiguration;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component(value = "jwtHeaderTokenExtractor")
public class JwtHeaderTokenExtractor implements TokenExtractor {
  public static String HEADER_PREFIX = "Bearer ";

  @Override
  public String extract(HttpServletRequest request) {
    String header = request.getHeader(IoTPSecurityConfiguration.JWT_TOKEN_HEADER_PARAM);
    if (StringUtils.isBlank(header)) {
      throw new AuthenticationServiceException("Authorization header cannot be blank!");
    }

    if (header.length() < HEADER_PREFIX.length()) {
      throw new AuthenticationServiceException("Invalid authorization header size.");
    }

    return header.substring(HEADER_PREFIX.length(), header.length());
  }
}
