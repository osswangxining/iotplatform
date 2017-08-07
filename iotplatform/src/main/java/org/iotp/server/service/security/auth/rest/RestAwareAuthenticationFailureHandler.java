package org.iotp.server.service.security.auth.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iotp.server.exception.IoTPErrorResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class RestAwareAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private final IoTPErrorResponseHandler errorResponseHandler;

  @Autowired
  public RestAwareAuthenticationFailureHandler(IoTPErrorResponseHandler errorResponseHandler) {
    this.errorResponseHandler = errorResponseHandler;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException e) throws IOException, ServletException {
    errorResponseHandler.handle(e, response);
  }
}
