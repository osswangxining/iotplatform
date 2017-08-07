package org.iotp.analytics.ruleengine.api.device;

import org.iotp.infomgt.data.id.DeviceId;

import lombok.Data;

/**
 * Contains basic device metadata;
 *
 */
@Data
public final class DeviceMetaData {

  final DeviceId deviceId;
  final String deviceName;
  final String deviceType;
  final DeviceAttributes deviceAttributes;

}
