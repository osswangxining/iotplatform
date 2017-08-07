package org.iotp.server.service.security.auth.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.iotp.server.service.security.auth.jwt.RefreshTokenRepository;
import org.iotp.server.service.security.model.SecurityUser;
import org.iotp.server.service.security.model.token.JwtToken;
import org.iotp.server.service.security.model.token.JwtTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  private final ObjectMapper mapper;
  private final JwtTokenFactory tokenFactory;
  private final RefreshTokenRepository refreshTokenRepository;

  @Autowired
  public RestAwareAuthenticationSuccessHandler(final ObjectMapper mapper, final JwtTokenFactory tokenFactory,
      final RefreshTokenRepository refreshTokenRepository) {
    this.mapper = mapper;
    this.tokenFactory = tokenFactory;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

    JwtToken accessToken = tokenFactory.createAccessJwtToken(securityUser);
    JwtToken refreshToken = refreshTokenRepository.requestRefreshToken(securityUser);

    Map<String, String> tokenMap = new HashMap<String, String>();
    tokenMap.put("token", accessToken.getToken());
    tokenMap.put("refreshToken", refreshToken.getToken());

    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    mapper.writeValue(response.getWriter(), tokenMap);

    clearAuthenticationAttributes(request);
  }

  /**
   * Removes temporary authentication-related data which may have been stored in
   * the session during the authentication process..
   *
   */
  protected final void clearAuthenticationAttributes(HttpServletRequest request) {
    HttpSession session = request.getSession(false);

    if (session == null) {
      return;
    }

    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
  }
}
