package org.iotp.server.actors.device;

import org.iotp.analytics.ruleengine.api.device.DeviceAttributesEventNotificationMsg;
import org.iotp.analytics.ruleengine.api.device.DeviceCredentialsUpdateNotificationMsg;
import org.iotp.analytics.ruleengine.api.device.DeviceNameOrTypeUpdateMsg;
import org.iotp.analytics.ruleengine.api.device.ToDeviceActorNotificationMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.plugins.msg.TimeoutMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequestPluginMsg;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.rule.RulesProcessedMsg;
import org.iotp.server.actors.service.ContextAwareActor;
import org.iotp.server.actors.service.ContextBasedCreator;
import org.iotp.server.actors.tenant.RuleChainDeviceMsg;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class DeviceActor extends ContextAwareActor {

  private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

  private final TenantId tenantId;
  private final DeviceId deviceId;
  private final DeviceActorMessageProcessor processor;

  private DeviceActor(ActorSystemContext systemContext, TenantId tenantId, DeviceId deviceId) {
    super(systemContext);
    this.tenantId = tenantId;
    this.deviceId = deviceId;
    this.processor = new DeviceActorMessageProcessor(systemContext, logger, deviceId);
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    if (msg instanceof RuleChainDeviceMsg) {
      processor.process(context(), (RuleChainDeviceMsg) msg);
    } else if (msg instanceof RulesProcessedMsg) {
      processor.onRulesProcessedMsg(context(), (RulesProcessedMsg) msg);
    } else if (msg instanceof ToDeviceActorMsg) {
      processor.process(context(), (ToDeviceActorMsg) msg);
    } else if (msg instanceof ToDeviceActorNotificationMsg) {
      if (msg instanceof DeviceAttributesEventNotificationMsg) {
        processor.processAttributesUpdate(context(), (DeviceAttributesEventNotificationMsg) msg);
      } else if (msg instanceof ToDeviceRpcRequestPluginMsg) {
        processor.processRpcRequest(context(), (ToDeviceRpcRequestPluginMsg) msg);
      } else if (msg instanceof DeviceCredentialsUpdateNotificationMsg) {
        processor.processCredentialsUpdate();
      } else if (msg instanceof DeviceNameOrTypeUpdateMsg) {
        processor.processNameOrTypeUpdate((DeviceNameOrTypeUpdateMsg) msg);
      }
    } else if (msg instanceof TimeoutMsg) {
      processor.processTimeout(context(), (TimeoutMsg) msg);
    } else if (msg instanceof ClusterEventMsg) {
      processor.processClusterEventMsg((ClusterEventMsg) msg);
    } else {
      logger.debug("[{}][{}] Unknown msg type.", tenantId, deviceId, msg.getClass().getName());
    }
  }

  public static class ActorCreator extends ContextBasedCreator<DeviceActor> {
    private static final long serialVersionUID = 1L;

    private final TenantId tenantId;
    private final DeviceId deviceId;

    public ActorCreator(ActorSystemContext context, TenantId tenantId, DeviceId deviceId) {
      super(context);
      this.tenantId = tenantId;
      this.deviceId = deviceId;
    }

    @Override
    public DeviceActor create() throws Exception {
      return new DeviceActor(context, tenantId, deviceId);
    }
  }

}
