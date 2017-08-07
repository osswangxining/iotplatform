package org.iotp.server.actors.service.adaptor;

import org.iotp.server.actors.service.DeviceSessionCtx;
import org.iotp.server.transport.TransportAdaptor;

import io.netty.handler.codec.mqtt.MqttMessage;

/**
 */
public interface MqttTransportAdaptor extends TransportAdaptor<DeviceSessionCtx, MqttMessage, MqttMessage> {
}
