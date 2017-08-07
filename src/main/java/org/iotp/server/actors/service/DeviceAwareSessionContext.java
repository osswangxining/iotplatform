package org.iotp.server.actors.service;

import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.session.SessionContext;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.security.DeviceCredentialsFilter;
import org.iotp.server.service.security.device.DeviceAuthResult;
import org.iotp.server.service.security.device.DeviceAuthService;
import org.iotp.server.transport.SessionMsgProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public abstract class DeviceAwareSessionContext implements SessionContext {

  protected final DeviceAuthService authService;
  protected final SessionMsgProcessor processor;

  protected volatile Device device;

  public DeviceAwareSessionContext(SessionMsgProcessor processor, DeviceAuthService authService) {
    this.processor = processor;
    this.authService = authService;
  }

  public DeviceAwareSessionContext(SessionMsgProcessor processor, DeviceAuthService authService, Device device) {
    this(processor, authService);
    this.device = device;
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
      log.debug("Can't find device using credentials [{}] due to {}", credentials, result.getErrorMsg());
      return false;
    }
  }

  public DeviceAuthService getAuthService() {
    return authService;
  }

  public SessionMsgProcessor getProcessor() {
    return processor;
  }

  public Device getDevice() {
    return device;
  }
}
