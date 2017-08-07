package org.iotp.iothub.server.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.iotp.infomgt.dao.attributes.AttributesService;
import org.iotp.infomgt.dao.timeseries.TimeseriesService;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.common.DataConstants;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.kv.AttributeKvEntry;
import org.iotp.infomgt.data.kv.TsKvEntry;
import org.iotp.infomgt.data.security.DeviceTokenCredentials;
import org.iotp.iothub.server.ThingsKVData;
import org.iotp.iothub.server.http.session.HttpSessionCtx;
import org.iotp.iothub.server.outbound.kafka.KafkaTopics;
import org.iotp.iothub.server.outbound.kafka.MsgProducer;
import org.iotp.iothub.server.security.DeviceAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.extern.slf4j.Slf4j;

/**
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DeviceApiController {
  public static final Integer DEFAULT_REQUEST_ID = 0;

  @Value("${http.request_timeout}")
  private long defaultTimeout;

  // @Autowired(required = false)
  // private SessionMsgProcessor processor;

  @Autowired(required = false)
  private MsgProducer msgProducer;

  @Autowired(required = false)
  private DeviceAuthService authService;

  @Autowired(required = false)
  private TimeseriesService timeseriesService;

  @Autowired(required = false)
  private AttributesService attributesService;

  @RequestMapping(value = "/{deviceToken}/attributes", method = RequestMethod.GET, produces = "application/json")
  public DeferredResult<ResponseEntity> getDeviceAttributes(@PathVariable("deviceToken") String deviceToken,
      @RequestParam(value = "clientKeys", required = false, defaultValue = "") String clientKeys,
      @RequestParam(value = "sharedKeys", required = false, defaultValue = "") String sharedKeys,
      @RequestParam(value = "serverKeys", required = false, defaultValue = "") String serverKeys) {
    DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
    HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
    if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
      DeviceId _deviceId = ctx.getDevice().getId();
      try {
        List<ListenableFuture<List<AttributeKvEntry>>> futures = new ArrayList<>();
        if (StringUtils.isEmpty(clientKeys) && StringUtils.isEmpty(sharedKeys) && StringUtils.isEmpty(serverKeys)) {
          Arrays.asList(DataConstants.ALL_SCOPES)
              .forEach(attributeType -> futures.add(attributesService.findAll(_deviceId, attributeType)));
        } else {
          Set<String> clientKeySet = !StringUtils.isEmpty(clientKeys)
              ? new HashSet<>(Arrays.asList(clientKeys.split(","))) : new HashSet<>();
          Set<String> sharedKeySet = !StringUtils.isEmpty(sharedKeys)
              ? new HashSet<>(Arrays.asList(sharedKeys.split(","))) : new HashSet<>();
          Set<String> serverKeySet = !StringUtils.isEmpty(serverKeys)
              ? new HashSet<>(Arrays.asList(serverKeys.split(","))) : new HashSet<>();
          clientKeySet.addAll(sharedKeySet);
          clientKeySet.addAll(serverKeySet);
          Arrays.asList(DataConstants.ALL_SCOPES)
              .forEach(attributeType -> futures.add(attributesService.find(_deviceId, attributeType, clientKeySet)));
        }
        ListenableFuture<List<List<AttributeKvEntry>>> successfulAsList = Futures.successfulAsList(futures);
        List<AttributeKvEntry> result = new ArrayList<>();
        successfulAsList.get().forEach(r -> result.addAll(r));
        List<ThingsKVData> collect = result.stream().map(attribute -> new ThingsKVData(attribute.getKey(), attribute.getValue())).collect(Collectors.toList());
        responseWriter.setResult(new ResponseEntity<>(collect, HttpStatus.OK));

      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
      }

      // if (StringUtils.isEmpty(clientKeys) && StringUtils.isEmpty(sharedKeys))
      // {
      // request = new BasicGetAttributesRequest(0);
      // } else {
      // Set<String> clientKeySet = !StringUtils.isEmpty(clientKeys)
      // ? new HashSet<>(Arrays.asList(clientKeys.split(","))) : null;
      // Set<String> sharedKeySet = !StringUtils.isEmpty(sharedKeys)
      // ? new HashSet<>(Arrays.asList(sharedKeys.split(","))) : null;
      // request = new BasicGetAttributesRequest(0, clientKeySet, sharedKeySet);
      // }
      // process(ctx, request);
    } else {
      responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    return responseWriter;
  }

  @RequestMapping(value = "/device/token/{deviceToken}/attributes/shadow", method = RequestMethod.GET, produces = "application/json")
  public DeferredResult<ResponseEntity> getDeviceAttributesShadow(@PathVariable("deviceToken") String deviceToken) {
    DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
    HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
    if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
      DeviceId _deviceId = ctx.getDevice().getId();
      try {
        List<ListenableFuture<List<AttributeKvEntry>>> futures = new ArrayList<>();
        Arrays.asList(DataConstants.ALL_SCOPES)
            .forEach(attributeType -> futures.add(attributesService.findAll(_deviceId, attributeType)));
        ListenableFuture<List<List<AttributeKvEntry>>> successfulAsList = Futures.successfulAsList(futures);
        List<AttributeKvEntry> result = new ArrayList<>();
        successfulAsList.get().forEach(r -> result.addAll(r));
        List<ThingsKVData> collect = result.stream().map(attribute -> new ThingsKVData(attribute.getKey(), attribute.getValue())).collect(Collectors.toList());
        responseWriter.setResult(new ResponseEntity<>(collect, HttpStatus.OK));

      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
      }
    } else {
      responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    return responseWriter;
  }

  @RequestMapping(value = "/device/token/{deviceToken}/telemetry/shadow", method = RequestMethod.GET, produces = "application/json")
  public DeferredResult<ResponseEntity> getDeviceTelemetryShadow(@PathVariable("deviceToken") String deviceToken) {
    DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
    HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
    if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
      DeviceId _deviceId = ctx.getDevice().getId();
      try {
        List<TsKvEntry> tsList = timeseriesService.findAllLatest(_deviceId).get();
//        if (tsList != null) {
//          for (TsKvEntry tsKvEntry : tsList) {
//            String key = tsKvEntry.getKey();
//            Object value = tsKvEntry.getValue();
//            log.info("key:{}, value:{}", key, value);
//          }
//        }
        List<ThingsKVData> collect = tsList.stream().map(attribute -> new ThingsKVData(attribute.getKey(), attribute.getValue())).collect(Collectors.toList());
        responseWriter.setResult(new ResponseEntity<>(collect, HttpStatus.OK));

      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
      }
    } else {
      responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    return responseWriter;
  }

  @RequestMapping(value = "/{deviceToken}/attributes", method = RequestMethod.POST)
  public DeferredResult<ResponseEntity> postDeviceAttributes(@PathVariable("deviceToken") String deviceToken,
      @RequestBody String json) {
    DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
    HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
    if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
      try {
        String kafkaOutboundTopic = KafkaTopics.DEVICE_ATTRIBUTES_TOPIC;
        Device device = ctx.getDevice();
        if (device != null && device.getId() != null) {
          // BasicToDeviceActorSessionMsg basicToDeviceActorSessionMsg = new
          // BasicToDeviceActorSessionMsg(
          // device, msg);
          JsonObject root = new JsonObject();
          JsonElement jsonElement = new JsonParser().parse(json);
          root.add("d", jsonElement);
          root.addProperty("messageId", DEFAULT_REQUEST_ID);
          log.info("msg: {}", root.toString());

          this.msgProducer.send(kafkaOutboundTopic, device.getId().toString(), root.toString());
          responseWriter.setResult(new ResponseEntity<>(HttpStatus.OK));
        } else {
          responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        // process(ctx, JsonConverter.convertToAttributes(new
        // JsonParser().parse(json)));
      } catch (IllegalStateException | JsonSyntaxException ex) {
        responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
      }
    } else {
      responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
    return responseWriter;
  }

  @RequestMapping(value = "/{deviceToken}/telemetry", method = RequestMethod.POST)
  public DeferredResult<ResponseEntity> postTelemetry(@PathVariable("deviceToken") String deviceToken,
      @RequestBody String json) {
    DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
    HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
    if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
      try {
        String kafkaOutboundTopic = KafkaTopics.DEVICE_TELEMETRY_TOPIC;
        Device device = ctx.getDevice();
        if (device != null && device.getId() != null) {
          // BasicToDeviceActorSessionMsg basicToDeviceActorSessionMsg = new
          // BasicToDeviceActorSessionMsg(
          // device, msg);
          JsonObject root = new JsonObject();
          JsonElement jsonElement = new JsonParser().parse(json);
          root.add("d", jsonElement);
          root.addProperty("messageId", DEFAULT_REQUEST_ID);
          log.info("msg: {}", root.toString());

          this.msgProducer.send(kafkaOutboundTopic, device.getId().toString(), root.toString());
          responseWriter.setResult(new ResponseEntity<>(HttpStatus.OK));
        } else {
          responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        // process(ctx, JsonConverter.convertToTelemetry(new
        // JsonParser().parse(json)));
      } catch (IllegalStateException | JsonSyntaxException ex) {
        responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
      }
    } else {
      responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
    return responseWriter;
  }

  // @RequestMapping(value = "/{deviceToken}/rpc", method = RequestMethod.GET,
  // produces = "application/json")
  // public DeferredResult<ResponseEntity>
  // subscribeToCommands(@PathVariable("deviceToken") String deviceToken,
  // @RequestParam(value = "timeout", required = false, defaultValue = "0") long
  // timeout) {
  // return subscribe(deviceToken, timeout, new RpcSubscribeMsg());
  // }
  //
  // @RequestMapping(value = "/{deviceToken}/rpc/{requestId}", method =
  // RequestMethod.POST)
  // public DeferredResult<ResponseEntity>
  // replyToCommand(@PathVariable("deviceToken") String deviceToken,
  // @PathVariable("requestId") Integer requestId, @RequestBody String json) {
  // DeferredResult<ResponseEntity> responseWriter = new
  // DeferredResult<ResponseEntity>();
  // HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
  // if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
  // try {
  // JsonObject response = new JsonParser().parse(json).getAsJsonObject();
  // process(ctx, new ToDeviceRpcResponseMsg(requestId, response.toString()));
  // } catch (IllegalStateException | JsonSyntaxException ex) {
  // responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  // }
  // } else {
  // responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
  // }
  // return responseWriter;
  // }
  //
  // @RequestMapping(value = "/{deviceToken}/rpc", method = RequestMethod.POST)
  // public DeferredResult<ResponseEntity>
  // postRpcRequest(@PathVariable("deviceToken") String deviceToken,
  // @RequestBody String json) {
  // DeferredResult<ResponseEntity> responseWriter = new
  // DeferredResult<ResponseEntity>();
  // HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
  // if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
  // try {
  // JsonObject request = new JsonParser().parse(json).getAsJsonObject();
  // process(ctx,
  // new ToServerRpcRequestMsg(0, request.get("method").getAsString(),
  // request.get("params").toString()));
  // } catch (IllegalStateException | JsonSyntaxException ex) {
  // responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  // }
  // } else {
  // responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
  // }
  // return responseWriter;
  // }
  //
  // @RequestMapping(value = "/{deviceToken}/attributes/updates", method =
  // RequestMethod.GET, produces = "application/json")
  // public DeferredResult<ResponseEntity>
  // subscribeToAttributes(@PathVariable("deviceToken") String deviceToken,
  // @RequestParam(value = "timeout", required = false, defaultValue = "0") long
  // timeout) {
  // return subscribe(deviceToken, timeout, new AttributesSubscribeMsg());
  // }

  // private DeferredResult<ResponseEntity> subscribe(String deviceToken, long
  // timeout, FromDeviceMsg msg) {
  // DeferredResult<ResponseEntity> responseWriter = new
  // DeferredResult<ResponseEntity>();
  // HttpSessionCtx ctx = getHttpSessionCtx(responseWriter, timeout);
  // if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
  // try {
  // process(ctx, msg);
  // } catch (IllegalStateException | JsonSyntaxException ex) {
  // responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  // }
  // } else {
  // responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
  // }
  // return responseWriter;
  // }

  private HttpSessionCtx getHttpSessionCtx(DeferredResult<ResponseEntity> responseWriter) {
    return getHttpSessionCtx(responseWriter, defaultTimeout);
  }

  private HttpSessionCtx getHttpSessionCtx(DeferredResult<ResponseEntity> responseWriter, long timeout) {
    return new HttpSessionCtx(authService, responseWriter, timeout != 0 ? timeout : defaultTimeout);
  }
  //
  // private void process(HttpSessionCtx ctx, FromDeviceMsg request) {
  // AdaptorToSessionActorMsg msg = new BasicAdaptorToSessionActorMsg(ctx,
  // request);
  // processor.process(new BasicToDeviceActorSessionMsg(ctx.getDevice(), msg));
  // }

}
