package org.iotp.iothub.server.http.session;

import java.util.Optional;

import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.infomgt.data.security.DeviceCredentialsFilter;
import org.iotp.iothub.server.security.DeviceAuthResult;
import org.iotp.iothub.server.security.DeviceAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class HttpSessionCtx {
  protected final DeviceAuthService authService;

  protected volatile Device device;

  private final SessionId sessionId;
  private final long timeout;
  private final DeferredResult<ResponseEntity> responseWriter;

  public HttpSessionCtx(DeviceAuthService authService, DeferredResult<ResponseEntity> responseWriter, long timeout) {
    this.authService = authService;
    this.sessionId = new HttpSessionId();
    this.responseWriter = responseWriter;
    this.timeout = timeout;
  }

  public boolean login(DeviceCredentialsFilter credentials) {
    DeviceAuthResult result = authService.process(credentials);
    if (result.isSuccess()) {
      Optional<Device> deviceOpt = authService.findDeviceById(result.getDeviceId());
      if (deviceOpt.isPresent()) {
        device = deviceOpt.get();
      }
      return true;
    } else {
      log.info("Can't find device using credentials [{}] due to {}", credentials, result.getErrorMsg());
      return false;
    }
  }

  public Device getDevice() {
    return device;
  }
  // @Override
  // public SessionType getSessionType() {
  // return SessionType.SYNC;
  // }

  // @Override
  // public void onMsg(SessionActorToAdaptorMsg source) throws SessionException
  // {
  // ToDeviceMsg msg = source.getMsg();
  // switch (msg.getMsgType()) {
  // case GET_ATTRIBUTES_RESPONSE:
  // reply((GetAttributesResponse) msg);
  // return;
  // case STATUS_CODE_RESPONSE:
  // reply((StatusCodeResponse) msg);
  // return;
  // case ATTRIBUTES_UPDATE_NOTIFICATION:
  // reply((AttributesUpdateNotification) msg);
  // return;
  // case TO_DEVICE_RPC_REQUEST:
  // reply((ToDeviceRpcRequestMsg) msg);
  // return;
  // case TO_SERVER_RPC_RESPONSE:
  // reply((ToServerRpcResponseMsg) msg);
  // return;
  // case RULE_ENGINE_ERROR:
  // reply((RuleEngineErrorMsg) msg);
  // return;
  // }
  // }
  //
  // private void reply(RuleEngineErrorMsg msg) {
  // HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
  // switch (msg.getError()) {
  // case PLUGIN_TIMEOUT:
  // status = HttpStatus.REQUEST_TIMEOUT;
  // break;
  // default:
  // if (msg.getInMsgType() == MsgType.TO_SERVER_RPC_REQUEST) {
  // status = HttpStatus.BAD_REQUEST;
  // }
  // break;
  // }
  // responseWriter.setResult(new
  // ResponseEntity<>(JsonConverter.toErrorJson(msg.getErrorMsg()).toString(),
  // status));
  // }
  //
  // private <T> void reply(ResponseMsg<? extends T> msg, Consumer<T> f) {
  // if (!msg.getError().isPresent()) {
  // f.accept(msg.getData().get());
  // } else {
  // Exception e = msg.getError().get();
  // responseWriter.setResult(new ResponseEntity<>(e.getMessage(),
  // HttpStatus.INTERNAL_SERVER_ERROR));
  // }
  // }
  //
  // private void reply(ToDeviceRpcRequestMsg msg) {
  // responseWriter.setResult(new ResponseEntity<>(JsonConverter.toJson(msg,
  // true).toString(), HttpStatus.OK));
  // }
  //
  // private void reply(ToServerRpcResponseMsg msg) {
  // responseWriter.setResult(new
  // ResponseEntity<>(JsonConverter.toJson(msg).toString(), HttpStatus.OK));
  // }
  //
  // private void reply(AttributesUpdateNotification msg) {
  // responseWriter.setResult(new
  // ResponseEntity<>(JsonConverter.toJson(msg.getData(), false).toString(),
  // HttpStatus.OK));
  // }
  //
  // private void reply(GetAttributesResponse msg) {
  // reply(msg, payload -> {
  // if (payload.getClientAttributes().isEmpty() &&
  // payload.getSharedAttributes().isEmpty()) {
  // responseWriter.setResult(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  // } else {
  // JsonObject result = JsonConverter.toJson(payload, false);
  // responseWriter.setResult(new ResponseEntity<>(result.toString(),
  // HttpStatus.OK));
  // }
  // });
  // }
  //
  // private void reply(StatusCodeResponse msg) {
  // reply(msg, payload -> {
  // if (payload == 0) {
  // responseWriter.setResult(new ResponseEntity<>(HttpStatus.OK));
  // } else {
  // responseWriter.setResult(new
  // ResponseEntity<>(HttpStatus.valueOf(payload)));
  // }
  // });
  // }

  // public void onMsg(SessionCtrlMsg msg) throws SessionException {
  //
  // }

  public boolean isClosed() {
    return false;
  }

  public long getTimeout() {
    return timeout;
  }

  public SessionId getSessionId() {
    return sessionId;
  }
}
