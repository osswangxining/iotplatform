package org.iotp.iothub.server.coap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.network.ExchangeObserver;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.iotp.infomgt.dao.attributes.AttributesService;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.common.DataConstants;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.infomgt.data.kv.AttributeKvEntry;
import org.iotp.infomgt.data.security.DeviceCredentialsFilter;
import org.iotp.infomgt.data.security.DeviceTokenCredentials;
import org.iotp.iothub.server.ThingsKVData;
import org.iotp.iothub.server.coap.session.CoapExchangeObserverProxy;
import org.iotp.iothub.server.coap.session.CoapSessionCtx;
import org.iotp.iothub.server.outbound.kafka.KafkaTopics;
import org.iotp.iothub.server.outbound.kafka.MsgProducer;
import org.iotp.iothub.server.security.DeviceAuthService;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoapTransportResource extends CoapResource {
  // coap://localhost:port/api/v1/DEVICE_TOKEN/[attributes|telemetry|rpc[/requestId]]
  private static final int ACCESS_TOKEN_POSITION = 3;
  private static final int FEATURE_TYPE_POSITION = 4;
  private static final int REQUEST_ID_POSITION = 5;

  public static final Integer DEFAULT_REQUEST_ID = 0;

  private final MsgProducer msgProducer;
  private AttributesService attributesService;
  // private final CoapTransportAdaptor adaptor;
  private final DeviceAuthService authService;
  private final Field observerField;
  private final long timeout;
  
  private final ObjectMapper objectMapper = new ObjectMapper();

  public CoapTransportResource(MsgProducer msgProducer, AttributesService attributesService, DeviceAuthService authService, String name, long timeout) {
    super(name);
    this.msgProducer = msgProducer;
    this.attributesService = attributesService;
    this.authService = authService;
    this.timeout = timeout;
    // This is important to turn off existing observable logic in
    // CoapResource. We will have our own observe monitoring due to 1:1
    // observe relationship.
    this.setObservable(false);
    observerField = ReflectionUtils.findField(Exchange.class, "observer");
    observerField.setAccessible(true);
  }

  @Override
  public void handleGET(CoapExchange exchange) {
    Optional<FeatureType> featureType = getFeatureType(exchange.advanced().getRequest());
    if (!featureType.isPresent()) {
      log.trace("Missing feature type parameter");
      exchange.respond(ResponseCode.BAD_REQUEST);
    } else if (featureType.get() == FeatureType.TELEMETRY) {
      log.trace("Can't fetch/subscribe to timeseries updates");
      exchange.respond(ResponseCode.BAD_REQUEST);
    } else if (exchange.getRequestOptions().hasObserve()) {
      boolean unsubscribe = exchange.getRequestOptions().getObserve() == 1;
      MsgType msgType;
      if (featureType.get() == FeatureType.RPC) {
        msgType = unsubscribe ? MsgType.UNSUBSCRIBE_RPC_COMMANDS_REQUEST : MsgType.SUBSCRIBE_RPC_COMMANDS_REQUEST;
      } else {
        msgType = unsubscribe ? MsgType.UNSUBSCRIBE_ATTRIBUTES_REQUEST : MsgType.SUBSCRIBE_ATTRIBUTES_REQUEST;
      }
      Optional<SessionId> sessionId = processRequest(exchange, msgType);
      if (sessionId.isPresent()) {
        if (exchange.getRequestOptions().getObserve() == 1) {
          exchange.respond(ResponseCode.VALID);
        }
      }
    } else if (featureType.get() == FeatureType.ATTRIBUTES) {
      processRequest(exchange, MsgType.GET_ATTRIBUTES_REQUEST);
    } else {
      log.trace("Invalid feature type parameter");
      exchange.respond(ResponseCode.BAD_REQUEST);
    }
  }

  @Override
  public void handlePOST(CoapExchange exchange) {
    Optional<FeatureType> featureType = getFeatureType(exchange.advanced().getRequest());
    if (!featureType.isPresent()) {
      log.trace("Missing feature type parameter");
      exchange.respond(ResponseCode.BAD_REQUEST);
    } else {
      switch (featureType.get()) {
      case ATTRIBUTES:
        processRequest(exchange, MsgType.POST_ATTRIBUTES_REQUEST);
        break;
      case TELEMETRY:
        processRequest(exchange, MsgType.POST_TELEMETRY_REQUEST);
        break;
      case RPC:
        Optional<Integer> requestId = getRequestId(exchange.advanced().getRequest());
        if (requestId.isPresent()) {
          processRequest(exchange, MsgType.TO_DEVICE_RPC_RESPONSE);
        } else {
          processRequest(exchange, MsgType.TO_SERVER_RPC_REQUEST);
        }
        break;
      }
    }
  }

  private Optional<SessionId> processRequest(CoapExchange exchange, MsgType type) {
    log.trace("Processing {}", exchange.advanced().getRequest());
    exchange.accept();
    Exchange advanced = exchange.advanced();
    Request request = advanced.getRequest();

    Optional<DeviceCredentialsFilter> credentials = decodeCredentials(request);
    if (!credentials.isPresent()) {
      exchange.respond(ResponseCode.BAD_REQUEST);
      return Optional.empty();
    }

    CoapSessionCtx ctx = new CoapSessionCtx(exchange, authService, timeout);

    if (!ctx.login(credentials.get())) {
      exchange.respond(ResponseCode.UNAUTHORIZED);
      return Optional.empty();
    }
    String kafkaOutboundTopic = null;
    // AdaptorToSessionActorMsg msg;
    try {
      switch (type) {
      case GET_ATTRIBUTES_REQUEST:
        
        try {
          DeviceId _deviceId = ctx.getDevice().getId();
          List<ListenableFuture<List<AttributeKvEntry>>> futures = new ArrayList<>();
          Arrays.asList(DataConstants.ALL_SCOPES)
              .forEach(attributeType -> futures.add(attributesService.findAll(_deviceId, attributeType)));
          ListenableFuture<List<List<AttributeKvEntry>>> successfulAsList = Futures.successfulAsList(futures);
          List<AttributeKvEntry> result = new ArrayList<>();
          successfulAsList.get().forEach(r -> result.addAll(r));
          List<ThingsKVData> collect = result.stream().map(attribute -> new ThingsKVData(attribute.getKey(), attribute.getValue())).collect(Collectors.toList());
          String payload = objectMapper.writeValueAsString(collect);
          exchange.respond(ResponseCode.CONTENT);
          exchange.respond(payload);
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
          exchange.respond(ResponseCode.BAD_REQUEST);
        }
        exchange.respond("");
        kafkaOutboundTopic = null;
        break;
      case POST_ATTRIBUTES_REQUEST:
        kafkaOutboundTopic = KafkaTopics.DEVICE_ATTRIBUTES_TOPIC;
        break;
      case POST_TELEMETRY_REQUEST:
        kafkaOutboundTopic = KafkaTopics.DEVICE_TELEMETRY_TOPIC;
        break;
      case TO_DEVICE_RPC_RESPONSE:
        kafkaOutboundTopic = KafkaTopics.DEVICE_ATTRIBUTES_RESPONSE_TOPIC_PREFIX;
        break;
      case TO_SERVER_RPC_REQUEST:
        // ctx.setSessionType(SessionType.SYNC);
        // msg = adaptor.convertToActorMsg(ctx, type, request);
        kafkaOutboundTopic = KafkaTopics.DEVICE_RPC_REQUESTS_TOPIC;
        break;
      case SUBSCRIBE_ATTRIBUTES_REQUEST:
        kafkaOutboundTopic = KafkaTopics.DEVICE_ATTRIBUTES_REQUEST_TOPIC_PREFIX;
        break;
      case SUBSCRIBE_RPC_COMMANDS_REQUEST:
        ExchangeObserver systemObserver = (ExchangeObserver) observerField.get(advanced);
        advanced.setObserver(new CoapExchangeObserverProxy(systemObserver, ctx));
      case UNSUBSCRIBE_ATTRIBUTES_REQUEST:
      case UNSUBSCRIBE_RPC_COMMANDS_REQUEST:
        // ctx.setSessionType(SessionType.ASYNC);
        // msg = adaptor.convertToActorMsg(ctx, type, request);
        break;
      default:
        log.trace("[{}] Unsupported msg type: {}", ctx.getSessionId(), type);
        throw new IllegalArgumentException("Unsupported msg type: " + type);
      }
      log.trace("Processing msg payload: {}", request.getPayloadString());
      Device device = ctx.getDevice();
      if (kafkaOutboundTopic != null && device != null && device.getId() != null) {
        JsonObject root = new JsonObject();
        JsonElement jsonElement = new JsonParser().parse(request.getPayloadString());
        root.add("d", jsonElement);
        root.addProperty("messageId", DEFAULT_REQUEST_ID);
        log.info("msg: {}", root.toString());

        this.msgProducer.send(kafkaOutboundTopic, device.getId().toString(), root.toString());
        exchange.respond(ResponseCode.CREATED);
      }
      // processor.process(new BasicToDeviceActorSessionMsg(ctx.getDevice(),
      // msg));
    } catch (Exception e) {
      log.debug("Failed to decode payload {}", e);
      exchange.respond(ResponseCode.BAD_REQUEST, e.getMessage());
      return Optional.empty();
    }
    return Optional.of(ctx.getSessionId());
  }

  private Optional<DeviceCredentialsFilter> decodeCredentials(Request request) {
    List<String> uriPath = request.getOptions().getUriPath();
    DeviceCredentialsFilter credentials = null;
    if (uriPath.size() >= ACCESS_TOKEN_POSITION) {
      credentials = new DeviceTokenCredentials(uriPath.get(ACCESS_TOKEN_POSITION - 1));
    }
    return Optional.ofNullable(credentials);
  }

  private Optional<FeatureType> getFeatureType(Request request) {
    List<String> uriPath = request.getOptions().getUriPath();
    try {
      if (uriPath.size() >= FEATURE_TYPE_POSITION) {
        return Optional.of(FeatureType.valueOf(uriPath.get(FEATURE_TYPE_POSITION - 1).toUpperCase()));
      }
    } catch (RuntimeException e) {
      log.warn("Failed to decode feature type: {}", uriPath);
    }
    return Optional.empty();
  }

  public static Optional<Integer> getRequestId(Request request) {
    List<String> uriPath = request.getOptions().getUriPath();
    try {
      if (uriPath.size() >= REQUEST_ID_POSITION) {
        return Optional.of(Integer.valueOf(uriPath.get(REQUEST_ID_POSITION - 1)));
      }
    } catch (RuntimeException e) {
      log.warn("Failed to decode feature type: {}", uriPath);
    }
    return Optional.empty();
  }

  @Override
  public Resource getChild(String name) {
    return this;
  }

}
