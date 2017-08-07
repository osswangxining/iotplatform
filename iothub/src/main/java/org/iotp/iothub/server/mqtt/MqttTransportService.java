package org.iotp.iothub.server.mqtt;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.iotp.infomgt.dao.asset.AssetService;
import org.iotp.infomgt.dao.device.DeviceService;
import org.iotp.infomgt.dao.relation.RelationService;
import org.iotp.iothub.server.outbound.kafka.MsgProducer;
import org.iotp.iothub.server.security.AssetAuthService;
import org.iotp.iothub.server.security.DeviceAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Service("MqttTransportService")
@Slf4j
public class MqttTransportService {

  private static final String V1 = "v1";
  private static final String DEVICE = "device";

  @Autowired(required = false)
  private MsgProducer msgProducer;

  @Autowired(required = false)
  private ApplicationContext appContext;

  @Autowired(required = false)
  private DeviceService deviceService;

  @Autowired(required = false)
  private DeviceAuthService authService;

  @Autowired(required = false)
  private AssetService assetService;

  @Autowired(required = false)
  private AssetAuthService assetAuthService;

  @Autowired(required = false)
  private RelationService relationService;

  @Autowired(required = false)
  private MqttSslHandlerProvider sslHandlerProvider;

  @Value("${mqtt.bind_address}")
  private String host;
  @Value("${mqtt.bind_port}")
  private Integer port;
  @Value("${mqtt.adaptor}")
  private String adaptorName;

  @Value("${mqtt.netty.leak_detector_level}")
  private String leakDetectorLevel;
  @Value("${mqtt.netty.boss_group_thread_count}")
  private Integer bossGroupThreadCount;
  @Value("${mqtt.netty.worker_group_thread_count}")
  private Integer workerGroupThreadCount;

  private Channel serverChannel;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;

  @PostConstruct
  public void init() throws Exception {
    log.info("Setting resource leak detector level to {}", leakDetectorLevel);
    ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));

    log.info("Starting MQTT transport...");
    log.info("Lookup MQTT transport adaptor {}", adaptorName);
    // this.adaptor = (MqttTransportAdaptor) appContext.getBean(adaptorName);

    log.info("Starting MQTT transport server");
    bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
    workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
    ServerBootstrap b = new ServerBootstrap();
    b.group(bossGroup, workerGroup).option(ChannelOption.SO_BACKLOG, 1000).option(ChannelOption.TCP_NODELAY, true)
        .childOption(ChannelOption.SO_KEEPALIVE, true).channel(NioServerSocketChannel.class)
        .childHandler(new MqttTransportServerInitializer(msgProducer, deviceService, authService, assetService,
            assetAuthService, relationService, sslHandlerProvider));

    serverChannel = b.bind(host, port).sync().channel();
    log.info("Mqtt transport started: {}:{}!", host, port);
  }

  @PreDestroy
  public void shutdown() throws InterruptedException {
    log.info("Stopping MQTT transport!");
    try {
      serverChannel.close().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
    log.info("MQTT transport stopped!");
  }
}
