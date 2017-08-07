package org.iotp.server.service.security.auth.rest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.iotp.server.service.security.exception.AuthMethodNotSupportedException;
import org.iotp.server.service.security.model.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {
  private static Logger logger = LoggerFactory.getLogger(RestLoginProcessingFilter.class);

  private final AuthenticationSuccessHandler successHandler;
  private final AuthenticationFailureHandler failureHandler;

  private final ObjectMapper objectMapper;

  public RestLoginProcessingFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler,
      AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
    super(defaultProcessUrl);
    this.successHandler = successHandler;
    this.failureHandler = failureHandler;
    this.objectMapper = mapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {
    if (!HttpMethod.POST.name().equals(request.getMethod())) {
      if (logger.isDebugEnabled()) {
        logger.debug("Authentication method not supported. Request method: " + request.getMethod());
      }
      throw new AuthMethodNotSupportedException("Authentication method not supported");
    }

    LoginRequest loginRequest;
    try {
      loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
    } catch (Exception e) {
      throw new AuthenticationServiceException("Invalid login request payload");
    }

    if (StringUtils.isBlank(loginRequest.getUsername()) || StringUtils.isBlank(loginRequest.getPassword())) {
      throw new AuthenticationServiceException("Username or Password not provided");
    }

    UserPrincipal principal = new UserPrincipal(UserPrincipal.Type.USER_NAME, loginRequest.getUsername());

    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal,
        loginRequest.getPassword());

    return this.getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {
    successHandler.onAuthenticationSuccess(request, response, authResult);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {
    SecurityContextHolder.clearContext();
    failureHandler.onAuthenticationFailure(request, response, failed);
  }
}
