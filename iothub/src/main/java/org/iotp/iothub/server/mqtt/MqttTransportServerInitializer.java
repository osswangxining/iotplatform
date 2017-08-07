package org.iotp.iothub.server.mqtt;

import org.iotp.infomgt.dao.asset.AssetService;
import org.iotp.infomgt.dao.device.DeviceService;
import org.iotp.infomgt.dao.relation.RelationService;
import org.iotp.iothub.server.outbound.kafka.MsgProducer;
import org.iotp.iothub.server.security.AssetAuthService;
import org.iotp.iothub.server.security.DeviceAuthService;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslHandler;

/**
 */
public class MqttTransportServerInitializer extends ChannelInitializer<SocketChannel> {

  private static final int MAX_PAYLOAD_SIZE = 64 * 1024 * 1024;

  private final MsgProducer msgProducer;
  private final DeviceService deviceService;
  private final DeviceAuthService authService;
  private final AssetService assetService;
  private final AssetAuthService assetAuthService;
  private final RelationService relationService;
  // private final MqttTransportAdaptor adaptor;
  private final MqttSslHandlerProvider sslHandlerProvider;

  public MqttTransportServerInitializer(MsgProducer msgProducer, DeviceService deviceService,
      DeviceAuthService authService, AssetService assetService, AssetAuthService assetAuthService,
      RelationService relationService, MqttSslHandlerProvider sslHandlerProvider) {
    this.msgProducer = msgProducer;
    this.deviceService = deviceService;
    this.authService = authService;
    this.assetService = assetService;
    this.assetAuthService = assetAuthService;
    this.relationService = relationService;
    // this.adaptor = adaptor;
    this.sslHandlerProvider = sslHandlerProvider;
  }

  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline pipeline = ch.pipeline();
    SslHandler sslHandler = null;
    if (sslHandlerProvider != null) {
      sslHandler = sslHandlerProvider.getSslHandler();
      pipeline.addLast(sslHandler);
    }
    pipeline.addLast("decoder", new MqttDecoder(MAX_PAYLOAD_SIZE));
    pipeline.addLast("encoder", MqttEncoder.INSTANCE);

    MqttTransportHandler handler = new MqttTransportHandler(msgProducer, deviceService, authService, assetService,
        assetAuthService, relationService, sslHandler);
    pipeline.addLast(handler);

//    ch.closeFuture().addListener(handler);

  }

}
