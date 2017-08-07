package org.iotp.server.config;

import java.util.Map;

import org.iotp.server.controller.plugin.PluginWebSocketHandler;
import org.iotp.server.exception.IoTPErrorCode;
import org.iotp.server.exception.IoTPException;
import org.iotp.server.service.security.model.SecurityUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

  public static final String WS_PLUGIN_PREFIX = "/api/ws/plugins/";
  public static final String WS_SECURITY_USER_ATTRIBUTE = "SECURITY_USER";
  private static final String WS_PLUGIN_MAPPING = WS_PLUGIN_PREFIX + "**";

  @Bean
  public ServletServerContainerFactoryBean createWebSocketContainer() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxTextMessageBufferSize(8192);
    container.setMaxBinaryMessageBufferSize(8192);
    return container;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(pluginWsHandler(), WS_PLUGIN_MAPPING).setAllowedOrigins("*")
        .addInterceptors(new HttpSessionHandshakeInterceptor(), new HandshakeInterceptor() {

          @Override
          public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
              WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            SecurityUser user = null;
            try {
              user = getCurrentUser();
            } catch (IoTPException ex) {
            }
            if (user == null) {
              response.setStatusCode(HttpStatus.UNAUTHORIZED);
              return false;
            } else {
              attributes.put(WS_SECURITY_USER_ATTRIBUTE, user);
              return true;
            }
          }

          @Override
          public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
              Exception exception) {
          }
        });
  }

  @Bean
  public WebSocketHandler pluginWsHandler() {
    return new PluginWebSocketHandler();
  }

  protected SecurityUser getCurrentUser() throws IoTPException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
      return (SecurityUser) authentication.getPrincipal();
    } else {
      throw new IoTPException("You aren't authorized to perform this operation!", IoTPErrorCode.AUTHENTICATION);
    }
  }
}
