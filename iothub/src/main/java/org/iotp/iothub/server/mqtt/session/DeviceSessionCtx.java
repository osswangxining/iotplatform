package org.iotp.iothub.server.mqtt.session;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.infomgt.data.security.DeviceCredentialsFilter;
import org.iotp.iothub.server.security.DeviceAuthResult;
import org.iotp.iothub.server.security.DeviceAuthService;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class DeviceSessionCtx {
  protected final DeviceAuthService authService;

  protected volatile Device device;
  // private final MqttTransportAdaptor adaptor;
  private final MqttSessionId sessionId;
  private ChannelHandlerContext channel;
  private volatile boolean allowAttributeResponses;
  private AtomicInteger msgIdSeq = new AtomicInteger(0);

  public DeviceSessionCtx(DeviceAuthService authService) {
    this.authService = authService;
    // this.adaptor = adaptor;
    this.sessionId = new MqttSessionId();
  }

  public boolean isClosed() {
    return false;
  }

  public long getTimeout() {
    return 0;
  }

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

  public Device getDevice() {
    return device;
  }

  public DeviceAuthService getAuthService() {
    return authService;
  }

  public boolean login(DeviceCredentialsFilter credentials) {
    DeviceAuthResult result = authService.process(credentials);
    if (result.isSuccess()) {
      Optional<Device> deviceOpt = authService.findDeviceById(result.getDeviceId());
      if (deviceOpt.isPresent()) {
        device = deviceOpt.get();
      }
      return true;
    } else {
      log.info("Can't find device using credentials [{}] due to {}", credentials, result.getErrorMsg());
      return false;
    }
  }
}
