package org.iotp.analytics.ruleengine.core.plugin.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.core.plugin.rpc.handlers.RpcRestMsgHandler;
import org.iotp.analytics.ruleengine.plugins.msg.FromDeviceRpcResponse;
import org.iotp.analytics.ruleengine.plugins.msg.RpcError;
import org.iotp.analytics.ruleengine.plugins.msg.TimeoutMsg;
import org.iotp.analytics.ruleengine.plugins.msg.TimeoutUUIDMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequest;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class RpcManager {

  @Setter
  private RpcRestMsgHandler restHandler;

  private Map<UUID, LocalRequestMetaData> localRpcRequests = new HashMap<>();

  public void process(PluginContext ctx, LocalRequestMetaData requestMd) {
    ToDeviceRpcRequest request = requestMd.getRequest();
    log.trace("[{}] Processing local rpc call for device [{}]", request.getId(), request.getDeviceId());
    ctx.sendRpcRequest(request);
    localRpcRequests.put(request.getId(), requestMd);
    ctx.scheduleTimeoutMsg(
        new TimeoutUUIDMsg(request.getId(), request.getExpirationTime() - System.currentTimeMillis()));
  }

  public void process(PluginContext ctx, FromDeviceRpcResponse response) {
    UUID requestId = response.getId();
    LocalRequestMetaData md = localRpcRequests.remove(requestId);
    if (md != null) {
      log.trace("[{}] Processing local rpc response from device [{}]", requestId, md.getRequest().getDeviceId());
      restHandler.reply(ctx, md.getResponseWriter(), response);
    } else {
      log.trace("[{}] Unknown or stale rpc response received [{}]", requestId, response);
    }
  }

  public void process(PluginContext ctx, TimeoutMsg msg) {
    if (msg instanceof TimeoutUUIDMsg) {
      UUID requestId = ((TimeoutUUIDMsg) msg).getId();
      FromDeviceRpcResponse timeoutReponse = new FromDeviceRpcResponse(requestId, null, RpcError.TIMEOUT);
      LocalRequestMetaData md = localRpcRequests.remove(requestId);
      if (md != null) {
        log.trace("[{}] Processing rpc timeout for local device [{}]", requestId, md.getRequest().getDeviceId());
        restHandler.reply(ctx, md.getResponseWriter(), timeoutReponse);
      }
    }
  }
}
