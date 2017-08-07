package org.iotp.analytics.ruleengine.plugins.msg;

import java.io.Serializable;
import java.util.UUID;

import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;

/**
 * The basic interface for messages that are sent from particular rule to plugin
 * instance
 * 
 *
 */
public interface RuleToPluginMsg<V extends Serializable> extends Serializable {

  /**
   * Returns the unique identifier of the message
   * 
   * @return unique identifier of the message.
   */
  UUID getUid();

  /**
   * Returns the unique identifier of the device that send the message
   * 
   * @return unique identifier of the device.
   */
  DeviceId getDeviceId();

  /**
   * Returns the unique identifier of the customer that owns the device
   *
   * @return unique identifier of the device.
   */
  CustomerId getCustomerId();

  /**
   * Returns the serializable message payload.
   * 
   * @return the serializable message payload.
   */
  V getPayload();
}
