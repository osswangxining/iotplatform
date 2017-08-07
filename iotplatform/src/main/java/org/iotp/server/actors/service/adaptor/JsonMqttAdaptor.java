package org.iotp.server.actors.service.adaptor;

import static io.netty.handler.codec.mqtt.MqttMessageType.PUBACK;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_LEAST_ONCE;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.iotp.analytics.ruleengine.common.msg.core.AttributesSubscribeMsg;
import org.iotp.analytics.ruleengine.common.msg.core.AttributesUnsubscribeMsg;
import org.iotp.analytics.ruleengine.common.msg.core.AttributesUpdateNotification;
import org.iotp.analytics.ruleengine.common.msg.core.BasicGetAttributesRequest;
import org.iotp.analytics.ruleengine.common.msg.core.GetAttributesResponse;
import org.iotp.analytics.ruleengine.common.msg.core.ResponseMsg;
import org.iotp.analytics.ruleengine.common.msg.core.RpcSubscribeMsg;
import org.iotp.analytics.ruleengine.common.msg.core.RpcUnsubscribeMsg;
import org.iotp.analytics.ruleengine.common.msg.core.RuleEngineErrorMsg;
import org.iotp.analytics.ruleengine.common.msg.core.TelemetryUploadRequest;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceRpcRequestMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceRpcResponseMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToServerRpcResponseMsg;
import org.iotp.analytics.ruleengine.common.msg.core.UpdateAttributesRequest;
import org.iotp.analytics.ruleengine.common.msg.kv.AttributesKVMsg;
import org.iotp.analytics.ruleengine.common.msg.session.AdaptorToSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.BasicAdaptorToSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.SessionActorToAdaptorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionContext;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.server.actors.service.DeviceSessionCtx;
import org.iotp.server.transport.AdaptorException;
import org.iotp.server.transport.JsonConverter;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Component("JsonMqttAdaptor")
@Slf4j
public class JsonMqttAdaptor implements MqttTransportAdaptor {

  private static final Gson GSON = new Gson();
  private static final Charset UTF8 = Charset.forName("UTF-8");
  private static final ByteBufAllocator ALLOCATOR = new UnpooledByteBufAllocator(false);

  @Override
  public AdaptorToSessionActorMsg convertToActorMsg(DeviceSessionCtx ctx, MsgType type, MqttMessage inbound)
      throws AdaptorException {
    FromDeviceMsg msg;
    switch (type) {
    case POST_TELEMETRY_REQUEST:
      msg = convertToTelemetryUploadRequest(ctx, (MqttPublishMessage) inbound);
      break;
    case POST_ATTRIBUTES_REQUEST:
      msg = convertToUpdateAttributesRequest(ctx, (MqttPublishMessage) inbound);
      break;
    case SUBSCRIBE_ATTRIBUTES_REQUEST:
      msg = new AttributesSubscribeMsg();
      break;
    case UNSUBSCRIBE_ATTRIBUTES_REQUEST:
      msg = new AttributesUnsubscribeMsg();
      break;
    case SUBSCRIBE_RPC_COMMANDS_REQUEST:
      msg = new RpcSubscribeMsg();
      break;
    case UNSUBSCRIBE_RPC_COMMANDS_REQUEST:
      msg = new RpcUnsubscribeMsg();
      break;
    case GET_ATTRIBUTES_REQUEST:
      msg = convertToGetAttributesRequest(ctx, (MqttPublishMessage) inbound);
      break;
    case TO_DEVICE_RPC_RESPONSE:
      msg = convertToRpcCommandResponse(ctx, (MqttPublishMessage) inbound);
      break;
    case TO_SERVER_RPC_REQUEST:
      msg = convertToServerRpcRequest(ctx, (MqttPublishMessage) inbound);
      break;
    default:
      log.warn("[{}] Unsupported msg type: {}!", ctx.getSessionId(), type);
      throw new AdaptorException(new IllegalArgumentException("Unsupported msg type: " + type + "!"));
    }
    return new BasicAdaptorToSessionActorMsg(ctx, msg);
  }

  @Override
  public Optional<MqttMessage> convertToAdaptorMsg(DeviceSessionCtx ctx, SessionActorToAdaptorMsg sessionMsg)
      throws AdaptorException {
    MqttMessage result = null;
    ToDeviceMsg msg = sessionMsg.getMsg();
    switch (msg.getMsgType()) {
    case STATUS_CODE_RESPONSE:
    case GET_ATTRIBUTES_RESPONSE:
      ResponseMsg<?> responseMsg = (ResponseMsg) msg;
      if (responseMsg.isSuccess()) {
        MsgType requestMsgType = responseMsg.getRequestMsgType();
        Integer requestId = responseMsg.getRequestId();
        if (requestId >= 0) {
          if (requestMsgType == MsgType.POST_ATTRIBUTES_REQUEST || requestMsgType == MsgType.POST_TELEMETRY_REQUEST) {
            result = createMqttPubAckMsg(requestId);
          } else if (requestMsgType == MsgType.GET_ATTRIBUTES_REQUEST) {
            GetAttributesResponse response = (GetAttributesResponse) msg;
            if (response.isSuccess()) {
              result = createMqttPublishMsg(ctx, MqttTopics.DEVICE_ATTRIBUTES_RESPONSE_TOPIC_PREFIX + requestId,
                  response.getData().get(), true);
            } else {
              throw new AdaptorException(response.getError().get());
            }
          }
        }
      } else {
        if (responseMsg.getError().isPresent()) {
          throw new AdaptorException(responseMsg.getError().get());
        }
      }
      break;
    case ATTRIBUTES_UPDATE_NOTIFICATION:
      AttributesUpdateNotification notification = (AttributesUpdateNotification) msg;
      result = createMqttPublishMsg(ctx, MqttTopics.DEVICE_ATTRIBUTES_TOPIC, notification.getData(), false);
      break;
    case TO_DEVICE_RPC_REQUEST:
      ToDeviceRpcRequestMsg rpcRequest = (ToDeviceRpcRequestMsg) msg;
      result = createMqttPublishMsg(ctx, MqttTopics.DEVICE_RPC_REQUESTS_TOPIC + rpcRequest.getRequestId(), rpcRequest);
      break;
    case TO_SERVER_RPC_RESPONSE:
      ToServerRpcResponseMsg rpcResponse = (ToServerRpcResponseMsg) msg;
      result = createMqttPublishMsg(ctx, MqttTopics.DEVICE_RPC_RESPONSE_TOPIC + rpcResponse.getRequestId(),
          rpcResponse);
      break;
    case RULE_ENGINE_ERROR:
      RuleEngineErrorMsg errorMsg = (RuleEngineErrorMsg) msg;
      result = createMqttPublishMsg(ctx, "errors", JsonConverter.toErrorJson(errorMsg.getErrorMsg()));
      break;
    }
    return Optional.ofNullable(result);
  }

  private static MqttPubAckMessage createMqttPubAckMsg(int requestId) {
    MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(PUBACK, false, AT_LEAST_ONCE, false, 0);
    MqttMessageIdVariableHeader mqttMsgIdVariableHeader = MqttMessageIdVariableHeader.from(requestId);
    return new MqttPubAckMessage(mqttFixedHeader, mqttMsgIdVariableHeader);
  }

  private MqttPublishMessage createMqttPublishMsg(DeviceSessionCtx ctx, String topic, AttributesKVMsg msg,
      boolean asMap) {
    return createMqttPublishMsg(ctx, topic, JsonConverter.toJson(msg, asMap));
  }

  private MqttPublishMessage createMqttPublishMsg(DeviceSessionCtx ctx, String topic, ToDeviceRpcRequestMsg msg) {
    return createMqttPublishMsg(ctx, topic, JsonConverter.toJson(msg, false));
  }

  private MqttPublishMessage createMqttPublishMsg(DeviceSessionCtx ctx, String topic, ToServerRpcResponseMsg msg) {
    return createMqttPublishMsg(ctx, topic, JsonConverter.toJson(msg));
  }

  private MqttPublishMessage createMqttPublishMsg(DeviceSessionCtx ctx, String topic, JsonElement json) {
    MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_LEAST_ONCE, false,
        0);
    MqttPublishVariableHeader header = new MqttPublishVariableHeader(topic, ctx.nextMsgId());
    ByteBuf payload = ALLOCATOR.buffer();
    payload.writeBytes(GSON.toJson(json).getBytes(UTF8));
    return new MqttPublishMessage(mqttFixedHeader, header, payload);
  }

  private FromDeviceMsg convertToGetAttributesRequest(DeviceSessionCtx ctx, MqttPublishMessage inbound)
      throws AdaptorException {
    String topicName = inbound.variableHeader().topicName();
    try {
      Integer requestId = Integer
          .valueOf(topicName.substring(MqttTopics.DEVICE_ATTRIBUTES_REQUEST_TOPIC_PREFIX.length()));
      String payload = inbound.payload().toString(UTF8);
      JsonElement requestBody = new JsonParser().parse(payload);
      Set<String> clientKeys = toStringSet(requestBody, "clientKeys");
      Set<String> sharedKeys = toStringSet(requestBody, "sharedKeys");
      if (clientKeys == null && sharedKeys == null) {
        return new BasicGetAttributesRequest(requestId);
      } else {
        return new BasicGetAttributesRequest(requestId, clientKeys, sharedKeys);
      }
    } catch (RuntimeException e) {
      log.warn("Failed to decode get attributes request", e);
      throw new AdaptorException(e);
    }
  }

  private FromDeviceMsg convertToRpcCommandResponse(DeviceSessionCtx ctx, MqttPublishMessage inbound)
      throws AdaptorException {
    String topicName = inbound.variableHeader().topicName();
    try {
      Integer requestId = Integer.valueOf(topicName.substring(MqttTopics.DEVICE_RPC_RESPONSE_TOPIC.length()));
      String payload = inbound.payload().toString(UTF8);
      return new ToDeviceRpcResponseMsg(requestId, payload);
    } catch (RuntimeException e) {
      log.warn("Failed to decode get attributes request", e);
      throw new AdaptorException(e);
    }
  }

  private Set<String> toStringSet(JsonElement requestBody, String name) {
    JsonElement element = requestBody.getAsJsonObject().get(name);
    if (element != null) {
      return new HashSet<>(Arrays.asList(element.getAsString().split(",")));
    } else {
      return null;
    }
  }

  private UpdateAttributesRequest convertToUpdateAttributesRequest(SessionContext ctx, MqttPublishMessage inbound)
      throws AdaptorException {
    String payload = validatePayload(ctx.getSessionId(), inbound.payload());
    try {
      return JsonConverter.convertToAttributes(new JsonParser().parse(payload), inbound.variableHeader().messageId());
    } catch (IllegalStateException | JsonSyntaxException ex) {
      throw new AdaptorException(ex);
    }
  }

  private TelemetryUploadRequest convertToTelemetryUploadRequest(SessionContext ctx, MqttPublishMessage inbound)
      throws AdaptorException {
    String payload = validatePayload(ctx.getSessionId(), inbound.payload());
    try {
      return JsonConverter.convertToTelemetry(new JsonParser().parse(payload), inbound.variableHeader().messageId());
    } catch (IllegalStateException | JsonSyntaxException ex) {
      throw new AdaptorException(ex);
    }
  }

  private FromDeviceMsg convertToServerRpcRequest(DeviceSessionCtx ctx, MqttPublishMessage inbound)
      throws AdaptorException {
    String topicName = inbound.variableHeader().topicName();
    String payload = validatePayload(ctx.getSessionId(), inbound.payload());
    try {
      Integer requestId = Integer.valueOf(topicName.substring(MqttTopics.DEVICE_RPC_REQUESTS_TOPIC.length()));
      return JsonConverter.convertToServerRpcRequest(new JsonParser().parse(payload), requestId);
    } catch (IllegalStateException | JsonSyntaxException ex) {
      throw new AdaptorException(ex);
    }
  }

  public static JsonElement validateJsonPayload(SessionId sessionId, ByteBuf payloadData) throws AdaptorException {
    String payload = validatePayload(sessionId, payloadData);
    try {
      return new JsonParser().parse(payload);
    } catch (JsonSyntaxException ex) {
      throw new AdaptorException(ex);
    }
  }

  public static String validatePayload(SessionId sessionId, ByteBuf payloadData) throws AdaptorException {
    try {
      String payload = payloadData.toString(UTF8);
      if (payload == null) {
        log.warn("[{}] Payload is empty!", sessionId.toUidStr());
        throw new AdaptorException(new IllegalArgumentException("Payload is empty!"));
      }
      return payload;
    } finally {
      payloadData.release();
    }
  }

}
