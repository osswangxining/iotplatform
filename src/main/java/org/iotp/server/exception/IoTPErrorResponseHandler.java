package org.iotp.server.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iotp.server.service.security.exception.AuthMethodNotSupportedException;
import org.iotp.server.service.security.exception.JwtExpiredTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IoTPErrorResponseHandler implements AccessDeniedHandler {

  @Autowired
  private ObjectMapper mapper;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    if (!response.isCommitted()) {
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpStatus.FORBIDDEN.value());
      mapper.writeValue(response.getWriter(),
          IoTPErrorResponse.of("You don't have permission to perform this operation!",
              IoTPErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN));
    }
  }

  public void handle(Exception exception, HttpServletResponse response) {
    log.debug("Processing exception {}", exception.getMessage(), exception);
    if (!response.isCommitted()) {
      try {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (exception instanceof IoTPException) {
          handleThingsboardException((IoTPException) exception, response);
        } else if (exception instanceof AccessDeniedException) {
          handleAccessDeniedException(response);
        } else if (exception instanceof AuthenticationException) {
          handleAuthenticationException((AuthenticationException) exception, response);
        } else {
          response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
          mapper.writeValue(response.getWriter(), IoTPErrorResponse.of(exception.getMessage(),
              IoTPErrorCode.GENERAL, HttpStatus.INTERNAL_SERVER_ERROR));
        }
      } catch (IOException e) {
        log.error("Can't handle exception", e);
      }
    }
  }

  private void handleThingsboardException(IoTPException thingsboardException, HttpServletResponse response)
      throws IOException {

    IoTPErrorCode errorCode = thingsboardException.getErrorCode();
    HttpStatus status;

    switch (errorCode) {
    case AUTHENTICATION:
      status = HttpStatus.UNAUTHORIZED;
      break;
    case PERMISSION_DENIED:
      status = HttpStatus.FORBIDDEN;
      break;
    case INVALID_ARGUMENTS:
      status = HttpStatus.BAD_REQUEST;
      break;
    case ITEM_NOT_FOUND:
      status = HttpStatus.NOT_FOUND;
      break;
    case BAD_REQUEST_PARAMS:
      status = HttpStatus.BAD_REQUEST;
      break;
    case GENERAL:
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      break;
    default:
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      break;
    }

    response.setStatus(status.value());
    mapper.writeValue(response.getWriter(),
        IoTPErrorResponse.of(thingsboardException.getMessage(), errorCode, status));
  }

  private void handleAccessDeniedException(HttpServletResponse response) throws IOException {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    mapper.writeValue(response.getWriter(),
        IoTPErrorResponse.of("You don't have permission to perform this operation!",
            IoTPErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN));

  }

  private void handleAuthenticationException(AuthenticationException authenticationException,
      HttpServletResponse response) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    if (authenticationException instanceof BadCredentialsException) {
      mapper.writeValue(response.getWriter(), IoTPErrorResponse.of("Invalid username or password",
          IoTPErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
    } else if (authenticationException instanceof JwtExpiredTokenException) {
      mapper.writeValue(response.getWriter(), IoTPErrorResponse.of("Token has expired",
          IoTPErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
    } else if (authenticationException instanceof AuthMethodNotSupportedException) {
      mapper.writeValue(response.getWriter(), IoTPErrorResponse.of(authenticationException.getMessage(),
          IoTPErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
    }
    mapper.writeValue(response.getWriter(), IoTPErrorResponse.of("Authentication failed",
        IoTPErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
  }

}
