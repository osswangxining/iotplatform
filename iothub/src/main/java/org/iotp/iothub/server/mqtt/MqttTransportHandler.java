package org.iotp.iothub.server.mqtt;

import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_ACCEPTED;
import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD;
import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED;
import static io.netty.handler.codec.mqtt.MqttMessageType.CONNACK;
import static io.netty.handler.codec.mqtt.MqttMessageType.PINGRESP;
import static io.netty.handler.codec.mqtt.MqttMessageType.PUBACK;
import static io.netty.handler.codec.mqtt.MqttMessageType.SUBACK;
import static io.netty.handler.codec.mqtt.MqttMessageType.UNSUBACK;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_LEAST_ONCE;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;
import static io.netty.handler.codec.mqtt.MqttQoS.FAILURE;
import static org.iotp.iothub.server.mqtt.MqttTopics.BASE_GATEWAY_API_TOPIC;
import static org.iotp.iothub.server.mqtt.MqttTopics.DEVICE_ATTRIBUTES_REQUEST_TOPIC_PREFIX;
import static org.iotp.iothub.server.mqtt.MqttTopics.DEVICE_ATTRIBUTES_RESPONSES_TOPIC;
import static org.iotp.iothub.server.mqtt.MqttTopics.DEVICE_ATTRIBUTES_TOPIC;
import static org.iotp.iothub.server.mqtt.MqttTopics.DEVICE_RPC_REQUESTS_SUB_TOPIC;
import static org.iotp.iothub.server.mqtt.MqttTopics.DEVICE_RPC_REQUESTS_TOPIC;
import static org.iotp.iothub.server.mqtt.MqttTopics.DEVICE_RPC_RESPONSE_SUB_TOPIC;
import static org.iotp.iothub.server.mqtt.MqttTopics.DEVICE_RPC_RESPONSE_TOPIC;
import static org.iotp.iothub.server.mqtt.MqttTopics.DEVICE_TELEMETRY_TOPIC;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

import org.iotp.infomgt.dao.EncryptionUtil;
import org.iotp.infomgt.dao.asset.AssetService;
import org.iotp.infomgt.dao.device.DeviceService;
import org.iotp.infomgt.dao.relation.RelationService;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.security.DeviceTokenCredentials;
import org.iotp.infomgt.data.security.DeviceX509Credentials;
import org.iotp.iothub.server.mqtt.session.DeviceSessionCtx;
import org.iotp.iothub.server.outbound.kafka.KafkaTopics;
import org.iotp.iothub.server.outbound.kafka.MsgProducer;
import org.iotp.iothub.server.security.AssetAuthService;
import org.iotp.iothub.server.security.DeviceAuthService;
import org.iotp.iothub.server.security.SslUtil;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class MqttTransportHandler extends ChannelInboundHandlerAdapter
/* implements GenericFutureListener<Future<? super Void>> */ {

  public static final MqttQoS MAX_SUPPORTED_QOS_LVL = AT_LEAST_ONCE;

  private final MsgProducer msgProducer;
  private final DeviceSessionCtx deviceSessionCtx;
  private final String sessionId;
  // private final MqttTransportAdaptor adaptor;
  private final DeviceService deviceService;
  private final DeviceAuthService authService;
  private final AssetService assetService;
  private final AssetAuthService assetAuthService;
  private final RelationService relationService;
  private final SslHandler sslHandler;
  private volatile boolean connected;
  // private volatile GatewaySessionCtx gatewaySessionCtx;

  public MqttTransportHandler(MsgProducer msgProducer, DeviceService deviceService, DeviceAuthService authService,
      AssetService assetService, AssetAuthService assetAuthService, RelationService relationService,
      SslHandler sslHandler) {
    this.msgProducer = msgProducer;
    this.deviceService = deviceService;
    this.relationService = relationService;
    this.authService = authService;
    this.assetService = assetService;
    this.assetAuthService = assetAuthService;
    // this.adaptor = adaptor;
    this.deviceSessionCtx = new DeviceSessionCtx(authService);
    this.sessionId = deviceSessionCtx.getSessionId().toUidStr();
    this.sslHandler = sslHandler;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    log.info("[{}] Processing msg: {}", sessionId, msg);
    if (msg instanceof MqttMessage) {
      MqttMessage mqttMessage = (MqttMessage) msg;
      MqttFixedHeader fixedHeader = mqttMessage.fixedHeader();
      if (fixedHeader != null) {
        processMqttMsg(ctx, (MqttMessage) msg);
      } else {
        //xtx
      }
    }

  }

  private void processMqttMsg(ChannelHandlerContext ctx, MqttMessage msg) {
    // deviceSessionCtx.setChannel(ctx);
    // assetSessionCtx.setChannel(ctx);

    switch (msg.fixedHeader().messageType()) {
    case CONNECT:
      processConnect(ctx, (MqttConnectMessage) msg);
      break;
    case PUBLISH:
      processPublish(ctx, (MqttPublishMessage) msg);
      // System.out.println("write...");
      // ctx.write("just for test");
      break;
    case SUBSCRIBE:
      processSubscribe(ctx, (MqttSubscribeMessage) msg);
      break;
    case UNSUBSCRIBE:
      processUnsubscribe(ctx, (MqttUnsubscribeMessage) msg);
      break;
    case PINGREQ:
      if (checkConnected(ctx)) {
        ctx.writeAndFlush(new MqttMessage(new MqttFixedHeader(PINGRESP, false, AT_MOST_ONCE, false, 0)));
      }
      break;
    case DISCONNECT:
      if (checkConnected(ctx)) {
        processDisconnect(ctx);
      }
      break;
    }
  }

  private void processPublish(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg) {
    if (!checkConnected(ctx)) {
      return;
    }
    String topicName = mqttMsg.variableHeader().topicName();
    int msgId = mqttMsg.variableHeader().messageId();
    log.info("[{}] Processing publish msg [{}][{}]!", sessionId, topicName, msgId);

    if (topicName.startsWith(BASE_GATEWAY_API_TOPIC)) {
      // if (gatewaySessionCtx != null) {
      // gatewaySessionCtx.setChannel(ctx);
      // try {
      // if (topicName.equals(GATEWAY_TELEMETRY_TOPIC)) {
      // gatewaySessionCtx.onDeviceTelemetry(mqttMsg);
      // } else if (topicName.equals(GATEWAY_ATTRIBUTES_TOPIC)) {
      // gatewaySessionCtx.onDeviceAttributes(mqttMsg);
      // } else if (topicName.equals(GATEWAY_ATTRIBUTES_REQUEST_TOPIC)) {
      // gatewaySessionCtx.onDeviceAttributesRequest(mqttMsg);
      // } else if (topicName.equals(GATEWAY_RPC_TOPIC)) {
      // gatewaySessionCtx.onDeviceRpcResponse(mqttMsg);
      // } else if (topicName.equals(GATEWAY_CONNECT_TOPIC)) {
      // gatewaySessionCtx.onDeviceConnect(mqttMsg);
      // } else if (topicName.equals(GATEWAY_DISCONNECT_TOPIC)) {
      // gatewaySessionCtx.onDeviceDisconnect(mqttMsg);
      // }
      // } catch (RuntimeException | AdaptorException e) {
      // log.warn("[{}] Failed to process publish msg [{}][{}]", sessionId,
      // topicName, msgId, e);
      // }
      // }
    } else {
      processDevicePublish(ctx, mqttMsg, topicName, msgId);
    }
  }

  private void processDevicePublish(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg, String topicName,
      int msgId) {
    // AdaptorToSessionActorMsg msg = null;
    int refCnt = mqttMsg.refCnt();
    int messageId = mqttMsg.variableHeader().messageId();
    log.info("[{}] refCnt: [{}], messageId: [{}]", sessionId, refCnt, messageId);
    MqttPublishMessage retainedDuplicate = mqttMsg.retainedDuplicate();
    String kafkaOutboundTopic = null;
    try {
      if (topicName.equals(DEVICE_TELEMETRY_TOPIC)) {
        // msg = adaptor.convertToActorMsg(deviceSessionCtx,
        // POST_TELEMETRY_REQUEST, mqttMsg);
        kafkaOutboundTopic = KafkaTopics.DEVICE_TELEMETRY_TOPIC;
      } else if (topicName.equals(DEVICE_ATTRIBUTES_TOPIC)) {
        kafkaOutboundTopic = KafkaTopics.DEVICE_ATTRIBUTES_TOPIC;
        // msg = adaptor.convertToActorMsg(deviceSessionCtx,
        // POST_ATTRIBUTES_REQUEST, mqttMsg);

        // MqttMessage createSubscribeResponseMessage =
        // createSubscribeResponseMessage(msgId);
        //// System.out.println(createSubscribeResponseMessage.payload());
        // ctx.writeAndFlush(createSubscribeResponseMessage);

      } else if (topicName.startsWith(DEVICE_ATTRIBUTES_REQUEST_TOPIC_PREFIX)) {
        // msg = adaptor.convertToActorMsg(deviceSessionCtx,
        // GET_ATTRIBUTES_REQUEST, mqttMsg);
        kafkaOutboundTopic = KafkaTopics.DEVICE_ATTRIBUTES_REQUEST_TOPIC_PREFIX;
        if (msgId >= 0) {
          ctx.writeAndFlush(createMqttPubAckMsg(msgId));
        }
      } else if (topicName.startsWith(DEVICE_RPC_RESPONSE_TOPIC)) {
        // msg = adaptor.convertToActorMsg(deviceSessionCtx,
        // TO_DEVICE_RPC_RESPONSE, mqttMsg);
        kafkaOutboundTopic = KafkaTopics.DEVICE_ATTRIBUTES_RESPONSE_TOPIC_PREFIX;
        if (msgId >= 0) {
          ctx.writeAndFlush(createMqttPubAckMsg(msgId));
        }
      } else if (topicName.startsWith(DEVICE_RPC_REQUESTS_TOPIC)) {
        // msg = adaptor.convertToActorMsg(deviceSessionCtx,
        // TO_SERVER_RPC_REQUEST, mqttMsg);
        kafkaOutboundTopic = KafkaTopics.DEVICE_RPC_REQUESTS_TOPIC;
        if (msgId >= 0) {
          ctx.writeAndFlush(createMqttPubAckMsg(msgId));
        }
      }
    } catch (Exception e) {
      log.warn("[{}] Failed to process publish msg [{}][{}]", sessionId, topicName, msgId, e);
    }
    if (kafkaOutboundTopic != null) {
      String payload = new String(ByteBufUtil.getBytes(retainedDuplicate.payload()));

      Set<ChannelEntity> channelEntitys = MemoryMetaPool.getChannelByTopics(topicName);
      if (channelEntitys != null) {
        for (ChannelEntity channelEntity : channelEntitys) {
          log.info("PUBLISH to ChannelEntity topic = " + topicName + " payload = " + payload);
          channelEntity.write(retainedDuplicate);
        }
      }

      Device device = deviceSessionCtx.getDevice();
      if (device != null && device.getId() != null) {
        // BasicToDeviceActorSessionMsg basicToDeviceActorSessionMsg = new
        // BasicToDeviceActorSessionMsg(
        // device, msg);
        JsonObject root = new JsonObject();
        JsonElement jsonElement = new JsonParser().parse(payload);
        root.add("d", jsonElement);
        root.addProperty("messageId", messageId);
        log.info("[{}] msg: {}", sessionId, root.toString());

        this.msgProducer.send(kafkaOutboundTopic, device.getId().toString(), root.toString());
      }
      // processor.process(basicToDeviceActorSessionMsg);
    } else {
      log.info("[{}] Closing current session due to invalid publish msg [{}][{}]", sessionId, topicName, msgId);
      ctx.close();
    }
  }

  private void processSubscribe(ChannelHandlerContext ctx, MqttSubscribeMessage mqttMsg) {
    if (!checkConnected(ctx)) {
      return;
    }
    log.trace("[{}] Processing subscription [{}]!", sessionId, mqttMsg.variableHeader().messageId());
    List<Integer> grantedQoSList = new ArrayList<>();
    for (MqttTopicSubscription subscription : mqttMsg.payload().topicSubscriptions()) {
      String topicName = subscription.topicName();
      // TODO: handle this qos level.
      MqttQoS reqQoS = subscription.qualityOfService();
      try {
        if (topicName.equals(DEVICE_ATTRIBUTES_TOPIC)) {
          // AdaptorToSessionActorMsg msg =
          // adaptor.convertToActorMsg(deviceSessionCtx,
          // SUBSCRIBE_ATTRIBUTES_REQUEST,
          // mqttMsg);
          // BasicToDeviceActorSessionMsg basicToDeviceActorSessionMsg = new
          // BasicToDeviceActorSessionMsg(
          // deviceSessionCtx.getDevice(), msg);
          // processor.process(basicToDeviceActorSessionMsg);
          grantedQoSList.add(getMinSupportedQos(reqQoS));
        } else if (topicName.equals(DEVICE_RPC_REQUESTS_SUB_TOPIC)) {
          // AdaptorToSessionActorMsg msg =
          // adaptor.convertToActorMsg(deviceSessionCtx,
          // SUBSCRIBE_RPC_COMMANDS_REQUEST,
          // mqttMsg);
          // processor.process(new
          // BasicToDeviceActorSessionMsg(deviceSessionCtx.getDevice(), msg));
          grantedQoSList.add(getMinSupportedQos(reqQoS));
        } else if (topicName.equals(DEVICE_RPC_RESPONSE_SUB_TOPIC)) {
          grantedQoSList.add(getMinSupportedQos(reqQoS));
        } else if (topicName.equals(DEVICE_ATTRIBUTES_RESPONSES_TOPIC)) {
          deviceSessionCtx.setAllowAttributeResponses();
          grantedQoSList.add(getMinSupportedQos(reqQoS));
        } else if (topicName.equals(DEVICE_TELEMETRY_TOPIC)) {
          grantedQoSList.add(getMinSupportedQos(reqQoS));
        } else {
          log.warn("[{}] Failed to subscribe to [{}][{}]", sessionId, topicName, reqQoS);
          grantedQoSList.add(FAILURE.value());
        }
        ChannelEntity channelEntity = new TcpChannelEntity(ctx.channel());
        MemoryMetaPool.registerTopic(channelEntity, topicName);
      } catch (Exception e) {
        e.printStackTrace();
        log.warn("[{}] Failed to subscribe to [{}][{}]", sessionId, topicName, reqQoS);
        grantedQoSList.add(FAILURE.value());
      }
    }

    ctx.writeAndFlush(createSubAckMessage(mqttMsg.variableHeader().messageId(), grantedQoSList));
  }

  private void processUnsubscribe(ChannelHandlerContext ctx, MqttUnsubscribeMessage mqttMsg) {
    if (!checkConnected(ctx)) {
      return;
    }
    if (MemoryMetaPool.getClientId(ctx.channel()) == null) {
      ctx.channel().close();
    }
    log.trace("[{}] Processing subscription [{}]!", sessionId, mqttMsg.variableHeader().messageId());
    for (String topicName : mqttMsg.payload().topics()) {
      try {
        if (topicName.equals(DEVICE_ATTRIBUTES_TOPIC)) {
          // AdaptorToSessionActorMsg msg =
          // adaptor.convertToActorMsg(deviceSessionCtx,
          // UNSUBSCRIBE_ATTRIBUTES_REQUEST,
          // mqttMsg);
          // processor.process(new
          // BasicToDeviceActorSessionMsg(deviceSessionCtx.getDevice(), msg));
        } else if (topicName.equals(DEVICE_RPC_REQUESTS_SUB_TOPIC)) {
          // AdaptorToSessionActorMsg msg =
          // adaptor.convertToActorMsg(deviceSessionCtx,
          // UNSUBSCRIBE_RPC_COMMANDS_REQUEST,
          // mqttMsg);
          // processor.process(new
          // BasicToDeviceActorSessionMsg(deviceSessionCtx.getDevice(), msg));
        } else if (topicName.equals(DEVICE_ATTRIBUTES_RESPONSES_TOPIC)) {
          deviceSessionCtx.setDisallowAttributeResponses();
        }
        MemoryMetaPool.unregisterTopic(ctx.channel(), topicName);
      } catch (Exception e) {
        log.warn("[{}] Failed to process unsubscription [{}] to [{}]", sessionId, mqttMsg.variableHeader().messageId(),
            topicName);
      }
    }
    ctx.writeAndFlush(createUnSubAckMessage(mqttMsg.variableHeader().messageId()));
  }

  private MqttMessage createUnSubAckMessage(int msgId) {
    MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(UNSUBACK, false, AT_LEAST_ONCE, false, 0);
    MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(msgId);
    return new MqttMessage(mqttFixedHeader, mqttMessageIdVariableHeader);
  }

  private void processConnect(ChannelHandlerContext ctx, MqttConnectMessage msg) {
    log.info("[{}] Processing connect msg for client: {}!", sessionId, msg.payload().clientIdentifier());
    X509Certificate cert;
    if (sslHandler != null && (cert = getX509Certificate()) != null) {
      String clientIdentifier = msg.payload().clientIdentifier();
      processX509CertConnect(ctx, cert, clientIdentifier);
    } else {
      processAuthTokenConnect(ctx, msg);
    }
  }

  private void processAuthTokenConnect(ChannelHandlerContext ctx, MqttConnectMessage msg) {
    String userName = msg.payload().userName();
    String clientIdentifier = msg.payload().clientIdentifier();
    if (StringUtils.isEmpty(userName)) {
      // ctx.writeAndFlush(createMqttConnAckMsg(CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
      // ctx.close();
      ctx.writeAndFlush(createMqttConnAckMsg(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
      connected = false;
    } else {
      boolean login = deviceSessionCtx.login(new DeviceTokenCredentials(userName));
      if (!login) {
        ctx.writeAndFlush(createMqttConnAckMsg(CONNECTION_REFUSED_NOT_AUTHORIZED));
        connected = false;
      } else {
        MemoryMetaPool.registerClienId(clientIdentifier, ctx.channel());

        ctx.writeAndFlush(createMqttConnAckMsg(CONNECTION_ACCEPTED));
        connected = true;
        checkGatewaySession();
      }
      // }
    }

  }

  private void processX509CertConnect(ChannelHandlerContext ctx, X509Certificate cert, String clientIdentifier) {
    try {
      String strCert = SslUtil.getX509CertificateString(cert);
      String sha3Hash = EncryptionUtil.getSha3Hash(strCert);
      boolean login = deviceSessionCtx.login(new DeviceX509Credentials(sha3Hash));

      if (login) {
        MemoryMetaPool.registerClienId(clientIdentifier, ctx.channel());
        ctx.writeAndFlush(createMqttConnAckMsg(CONNECTION_ACCEPTED));
        connected = true;
        checkGatewaySession();
      } else {
        ctx.writeAndFlush(createMqttConnAckMsg(CONNECTION_REFUSED_NOT_AUTHORIZED));
        ctx.close();
      }
    } catch (Exception e) {
      ctx.writeAndFlush(createMqttConnAckMsg(CONNECTION_REFUSED_NOT_AUTHORIZED));
      ctx.close();
    }
  }

  private X509Certificate getX509Certificate() {
    try {
      X509Certificate[] certChain = sslHandler.engine().getSession().getPeerCertificateChain();
      if (certChain.length > 0) {
        return certChain[0];
      }
    } catch (SSLPeerUnverifiedException e) {
      log.warn(e.getMessage());
      return null;
    }
    return null;
  }

  private void processDisconnect(ChannelHandlerContext ctx) {
    ctx.close();
    // processor.process(SessionCloseMsg.onDisconnect(deviceSessionCtx.getSessionId()));
    // processor.process(SessionCloseMsg.onDisconnect(assetSessionCtx.getSessionId()));
    // if (gatewaySessionCtx != null) {
    // gatewaySessionCtx.onGatewayDisconnect();
    // }
  }

  private MqttConnAckMessage createMqttConnAckMsg(MqttConnectReturnCode returnCode) {
    MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(CONNACK, false, AT_MOST_ONCE, false, 0);
    MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(returnCode, true);
    return new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error("[{}] Unexpected Exception: {}", sessionId, cause);
    try {
      if (cause.getCause() instanceof ReadTimeoutException) {
        ctx.write(PINGRESP).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      } else {
        ctx.close();
      }
    } catch (Throwable t) {
      t.printStackTrace();
      ctx.close();
    }

  }

  // private static MqttMessage createSubscribeResponseMessage(Integer msgId) {
  // MqttFixedHeader mqttFixedHeader = new
  // MqttFixedHeader(MqttMessageType.PUBLISH, false, AT_LEAST_ONCE, false, 0);
  // MqttPublishVariableHeader mqttMessageIdVariableHeader = new
  // MqttPublishVariableHeader("v1/devices/me/attributes",
  // msgId);
  // byte[] payload = "test".getBytes();
  // return new MqttMessage(mqttFixedHeader, mqttMessageIdVariableHeader,
  // payload);
  // }

  private static MqttSubAckMessage createSubAckMessage(Integer msgId, List<Integer> grantedQoSList) {
    MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(SUBACK, false, AT_LEAST_ONCE, false, 0);
    MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(msgId);
    MqttSubAckPayload mqttSubAckPayload = new MqttSubAckPayload(grantedQoSList);
    return new MqttSubAckMessage(mqttFixedHeader, mqttMessageIdVariableHeader, mqttSubAckPayload);
  }

  private static int getMinSupportedQos(MqttQoS reqQoS) {
    return Math.min(reqQoS.value(), MAX_SUPPORTED_QOS_LVL.value());
  }

  public static MqttPubAckMessage createMqttPubAckMsg(int requestId) {
    MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(PUBACK, false, AT_LEAST_ONCE, false, 0);
    MqttMessageIdVariableHeader mqttMsgIdVariableHeader = MqttMessageIdVariableHeader.from(requestId);
    return new MqttPubAckMessage(mqttFixedHeader, mqttMsgIdVariableHeader);
  }

  private boolean checkConnected(ChannelHandlerContext ctx) {
    if (connected) {
      return true;
    } else {
      log.info("[{}] Closing current session due to invalid msg order [{}][{}]", sessionId);
      ctx.close();
      return false;
    }
  }

  private void checkGatewaySession() {
    Device device = deviceSessionCtx.getDevice();
    if (device != null) {
      JsonNode infoNode = device.getAdditionalInfo();
      if (infoNode != null) {
        JsonNode gatewayNode = infoNode.get("gateway");
        if (gatewayNode != null && gatewayNode.asBoolean()) {
          // gatewaySessionCtx = new GatewaySessionCtx(processor, deviceService,
          // authService, relationService,
          // deviceSessionCtx);
        }
      }
    }
  }

  // @Override
  // public void operationComplete(Future<? super Void> future) throws Exception
  // {
  // log.warn("operationComplete.....{}", future.get());
  // }
}
