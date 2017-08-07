package org.iotp.analytics.ruleengine.api.plugins.handlers;

import javax.servlet.ServletException;

import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.plugins.rest.PluginRestMsg;
import org.springframework.http.HttpMethod;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class DefaultRestMsgHandler implements RestMsgHandler {

  protected final ObjectMapper jsonMapper = new ObjectMapper();

  @Override
  public void process(PluginContext ctx, PluginRestMsg msg) {
    try {
      log.debug("[{}] Processing REST msg: {}", ctx.getPluginId(), msg);
      HttpMethod method = msg.getRequest().getMethod();
      switch (method) {
      case GET:
        handleHttpGetRequest(ctx, msg);
        break;
      case POST:
        handleHttpPostRequest(ctx, msg);
        break;
      case DELETE:
        handleHttpDeleteRequest(ctx, msg);
        break;
      default:
        msg.getResponseHolder().setErrorResult(new HttpRequestMethodNotSupportedException(method.name()));
      }
      log.debug("[{}] Processed REST msg.", ctx.getPluginId());
    } catch (Exception e) {
      log.warn("[{}] Exception during REST msg processing: {}", ctx.getPluginId(), e.getMessage(), e);
      msg.getResponseHolder().setErrorResult(e);
    }
  }

  protected void handleHttpGetRequest(PluginContext ctx, PluginRestMsg msg) throws ServletException {
    msg.getResponseHolder().setErrorResult(new HttpRequestMethodNotSupportedException(HttpMethod.GET.name()));
  }

  protected void handleHttpPostRequest(PluginContext ctx, PluginRestMsg msg) throws ServletException {
    msg.getResponseHolder().setErrorResult(new HttpRequestMethodNotSupportedException(HttpMethod.POST.name()));
  }

  protected void handleHttpDeleteRequest(PluginContext ctx, PluginRestMsg msg) throws ServletException {
    msg.getResponseHolder().setErrorResult(new HttpRequestMethodNotSupportedException(HttpMethod.DELETE.name()));
  }

}
