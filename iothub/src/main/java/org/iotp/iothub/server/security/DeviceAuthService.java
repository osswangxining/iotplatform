package org.iotp.iothub.server.security;

import java.util.Optional;

import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.security.DeviceCredentialsFilter;

public interface DeviceAuthService {

  DeviceAuthResult process(DeviceCredentialsFilter credentials);

  Optional<Device> findDeviceById(DeviceId deviceId);

}
