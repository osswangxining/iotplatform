package org.iotp.analytics.ruleengine.common.msg.device;

import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceActorSessionMsg;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.ToString;

@ToString
public class BasicToDeviceActorMsg implements ToDeviceActorMsg {

  private static final long serialVersionUID = -1866795134993115408L;

  private final TenantId tenantId;
  private final CustomerId customerId;
  private final DeviceId deviceId;
  private final SessionId sessionId;
  private final SessionType sessionType;
  private final ServerAddress serverAddress;
  private final FromDeviceMsg msg;

  public BasicToDeviceActorMsg(ToDeviceActorMsg other, FromDeviceMsg msg) {
    this(null, other.getTenantId(), other.getCustomerId(), other.getDeviceId(), other.getSessionId(),
        other.getSessionType(), msg);
  }

  public BasicToDeviceActorMsg(ToDeviceActorSessionMsg msg, SessionType sessionType) {
    this(null, msg.getTenantId(), msg.getCustomerId(), msg.getDeviceId(), msg.getSessionId(), sessionType,
        msg.getSessionMsg().getMsg());
  }

  private BasicToDeviceActorMsg(ServerAddress serverAddress, TenantId tenantId, CustomerId customerId,
      DeviceId deviceId, SessionId sessionId, SessionType sessionType, FromDeviceMsg msg) {
    super();
    this.serverAddress = serverAddress;
    this.tenantId = tenantId;
    this.customerId = customerId;
    this.deviceId = deviceId;
    this.sessionId = sessionId;
    this.sessionType = sessionType;
    this.msg = msg;
  }

  @Override
  public DeviceId getDeviceId() {
    return deviceId;
  }

  @Override
  public CustomerId getCustomerId() {
    return customerId;
  }

  public TenantId getTenantId() {
    return tenantId;
  }

  @Override
  public SessionId getSessionId() {
    return sessionId;
  }

  @Override
  public SessionType getSessionType() {
    return sessionType;
  }

  @Override
  public Optional<ServerAddress> getServerAddress() {
    return Optional.ofNullable(serverAddress);
  }

  @Override
  public FromDeviceMsg getPayload() {
    return msg;
  }

  @Override
  public ToDeviceActorMsg toOtherAddress(ServerAddress otherAddress) {
    return new BasicToDeviceActorMsg(otherAddress, tenantId, customerId, deviceId, sessionId, sessionType, msg);
  }
}
