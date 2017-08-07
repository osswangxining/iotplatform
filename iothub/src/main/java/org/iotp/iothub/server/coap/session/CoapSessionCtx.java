package org.iotp.iothub.server.coap.session;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.infomgt.data.security.DeviceCredentialsFilter;
import org.iotp.iothub.server.security.DeviceAuthResult;
import org.iotp.iothub.server.security.DeviceAuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoapSessionCtx {
  protected final DeviceAuthService authService;
  protected volatile Device device;
  private final SessionId sessionId;
  private final CoapExchange exchange;
  // private final CoapTransportAdaptor adaptor;
  private final String token;
  private final long timeout;
  // private SessionType sessionType;
  private final AtomicInteger seqNumber = new AtomicInteger(2);

  public CoapSessionCtx(CoapExchange exchange, DeviceAuthService authService, long timeout) {
    this.authService = authService;
    Request request = exchange.advanced().getRequest();
    this.token = request.getTokenString();
    this.sessionId = new CoapSessionId(request.getSource().getHostAddress(), request.getSourcePort(), this.token);
    this.exchange = exchange;
    this.timeout = timeout;
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
  public Device getDevice() {
    return device;
  }
  private void pushToNetwork(Response response) {
    exchange.respond(response);
  }

  public SessionId getSessionId() {
    return sessionId;
  }

  @Override
  public String toString() {
    return "CoapSessionCtx [sessionId=" + sessionId + "]";
  }

  public boolean isClosed() {
    return exchange.advanced().isComplete() || exchange.advanced().isTimedOut();
  }

  public long getTimeout() {
    return timeout;
  }

  public int nextSeqNumber() {
    return seqNumber.getAndIncrement();
  }
}
