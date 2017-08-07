package org.iotp.server.actors.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.iotp.analytics.ruleengine.common.msg.session.SessionActorToAdaptorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionCtrlMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.analytics.ruleengine.common.msg.session.ctrl.SessionCloseMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ex.SessionException;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.server.actors.service.adaptor.MqttTransportAdaptor;
import org.iotp.server.service.security.device.DeviceAuthService;
import org.iotp.server.transport.AdaptorException;
import org.iotp.server.transport.SessionMsgProcessor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class DeviceSessionCtx extends DeviceAwareSessionContext {

  private final MqttTransportAdaptor adaptor;
  private final MqttSessionId sessionId;
  private ChannelHandlerContext channel;
  private volatile boolean allowAttributeResponses;
  private AtomicInteger msgIdSeq = new AtomicInteger(0);

  public DeviceSessionCtx(SessionMsgProcessor processor, DeviceAuthService authService, MqttTransportAdaptor adaptor) {
    super(processor, authService);
    this.adaptor = adaptor;
    this.sessionId = new MqttSessionId();
  }

  @Override
  public SessionType getSessionType() {
    return SessionType.ASYNC;
  }

  @Override
  public void onMsg(SessionActorToAdaptorMsg msg) throws SessionException {
    try {
      adaptor.convertToAdaptorMsg(this, msg).ifPresent(this::pushToNetwork);
    } catch (AdaptorException e) {
      // TODO: close channel with disconnect;
      logAndWrap(e);
    }
  }

  private void logAndWrap(AdaptorException e) throws SessionException {
    log.warn("Failed to convert msg: {}", e.getMessage(), e);
    throw new SessionException(e);
  }

  private void pushToNetwork(MqttMessage msg) {
    if (channel == null) {
      log.warn("channel is null:" + DeviceSessionCtx.class);
    } else {
      channel.writeAndFlush(msg);
    }
  }

  @Override
  public void onMsg(SessionCtrlMsg msg) throws SessionException {
    if (msg instanceof SessionCloseMsg) {
      pushToNetwork(
          new MqttMessage(new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0)));
      channel.close();
    }
  }

  @Override
  public boolean isClosed() {
    return false;
  }

  @Override
  public long getTimeout() {
    return 0;
  }

  @Override
  public SessionId getSessionId() {
    return sessionId;
  }

  public void setChannel(ChannelHandlerContext channel) {
    this.channel = channel;
  }

  public void setAllowAttributeResponses() {
    allowAttributeResponses = true;
  }

  public void setDisallowAttributeResponses() {
    allowAttributeResponses = false;
  }

  public int nextMsgId() {
    return msgIdSeq.incrementAndGet();
  }
}
