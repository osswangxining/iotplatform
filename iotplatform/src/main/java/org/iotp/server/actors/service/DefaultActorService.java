package org.iotp.server.actors.service;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.iotp.analytics.ruleengine.api.asset.AssetCredentialsUpdateNotificationMsg;
import org.iotp.analytics.ruleengine.api.asset.ToAssetActorNotificationMsg;
import org.iotp.analytics.ruleengine.api.device.DeviceCredentialsUpdateNotificationMsg;
import org.iotp.analytics.ruleengine.api.device.DeviceNameOrTypeUpdateMsg;
import org.iotp.analytics.ruleengine.api.device.ToDeviceActorNotificationMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.SessionAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.cluster.ToAllNodesMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.plugin.ComponentLifecycleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToPluginActorMsg;
import org.iotp.analytics.ruleengine.plugins.rest.PluginRestMsg;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketMsg;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.plugin.ComponentLifecycleEvent;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.app.AppActor;
import org.iotp.server.actors.rpc.RpcBroadcastMsg;
import org.iotp.server.actors.rpc.RpcManagerActor;
import org.iotp.server.actors.rpc.RpcSessionCreateRequestMsg;
import org.iotp.server.actors.rpc.RpcSessionTellMsg;
import org.iotp.server.actors.session.SessionManagerActor;
import org.iotp.server.actors.stats.StatsActor;
import org.iotp.server.service.cluster.discovery.DiscoveryService;
import org.iotp.server.service.cluster.discovery.ServerInstance;
import org.iotp.server.service.cluster.rpc.ClusterRpcService;
import org.iotp.server.transport.SessionMsgProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

@Service
@Slf4j
public class DefaultActorService implements ActorService {

  private static final String ACTOR_SYSTEM_NAME = "Akka";

  public static final String APP_DISPATCHER_NAME = "app-dispatcher";
  public static final String CORE_DISPATCHER_NAME = "core-dispatcher";
  public static final String SYSTEM_RULE_DISPATCHER_NAME = "system-rule-dispatcher";
  public static final String SYSTEM_PLUGIN_DISPATCHER_NAME = "system-plugin-dispatcher";
  public static final String TENANT_RULE_DISPATCHER_NAME = "rule-dispatcher";
  public static final String TENANT_PLUGIN_DISPATCHER_NAME = "plugin-dispatcher";
  public static final String SESSION_DISPATCHER_NAME = "session-dispatcher";
  public static final String RPC_DISPATCHER_NAME = "rpc-dispatcher";

  @Autowired
  private ActorSystemContext actorContext;

  @Autowired
  private ClusterRpcService rpcService;

  @Autowired
  private DiscoveryService discoveryService;

  @Autowired(required = false)
  private SessionMsgProcessor processor;

  private ActorSystem system;

  private ActorRef appActor;

  private ActorRef sessionManagerActor;

  private ActorRef rpcManagerActor;

  @PostConstruct
  public void initActorSystem() {
    log.info("Initializing Actor system. {}", actorContext.getRuleService());
    actorContext.setActorService(this);
    system = ActorSystem.create(ACTOR_SYSTEM_NAME, actorContext.getConfig());
    actorContext.setActorSystem(system);

    appActor = system.actorOf(Props.create(new AppActor.ActorCreator(actorContext)).withDispatcher(APP_DISPATCHER_NAME),
        "appActor");
    actorContext.setAppActor(appActor);

    sessionManagerActor = system.actorOf(
        Props.create(new SessionManagerActor.ActorCreator(actorContext)).withDispatcher(CORE_DISPATCHER_NAME),
        "sessionManagerActor");
    actorContext.setSessionManagerActor(sessionManagerActor);

    rpcManagerActor = system.actorOf(
        Props.create(new RpcManagerActor.ActorCreator(actorContext)).withDispatcher(CORE_DISPATCHER_NAME),
        "rpcManagerActor");

    ActorRef statsActor = system.actorOf(
        Props.create(new StatsActor.ActorCreator(actorContext)).withDispatcher(CORE_DISPATCHER_NAME), "statsActor");
    actorContext.setStatsActor(statsActor);

    rpcService.init(this);

    discoveryService.addListener(this);
    log.info("Actor system initialized.");
  }

  @PreDestroy
  public void stopActorSystem() {
    Future<Terminated> status = system.terminate();
    try {
      Terminated terminated = Await.result(status, Duration.Inf());
      log.info("Actor system terminated: {}", terminated);
    } catch (Exception e) {
      log.error("Failed to terminate actor system.", e);
    }
  }

  @Override
  public void process(SessionAwareMsg msg) {
    if (msg instanceof SessionAwareMsg) {
      log.debug("Processing session aware msg: {}", msg);
      sessionManagerActor.tell(msg, ActorRef.noSender());
    }
  }

  @Override
  public void process(PluginWebsocketMsg<?> msg) {
    log.debug("Processing websocket msg: {}", msg);
    appActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void process(PluginRestMsg msg) {
    log.debug("Processing rest msg: {}", msg);
    appActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(ToPluginActorMsg msg) {
    log.trace("Processing plugin rpc msg: {}", msg);
    appActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(ToDeviceActorMsg msg) {
    log.trace("Processing device rpc msg: {}", msg);
    appActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(ToDeviceActorNotificationMsg msg) {
    log.trace("Processing notification rpc msg: {}", msg);
    appActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(ToAssetActorNotificationMsg msg) {
    log.trace("Processing notification rpc msg: {}", msg);
    appActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(ToDeviceSessionActorMsg msg) {
    log.trace("Processing session rpc msg: {}", msg);
    sessionManagerActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(ToAllNodesMsg msg) {
    log.trace("Processing broadcast rpc msg: {}", msg);
    appActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(RpcSessionCreateRequestMsg msg) {
    log.trace("Processing session create msg: {}", msg);
    rpcManagerActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(RpcSessionTellMsg msg) {
    log.trace("Processing session rpc msg: {}", msg);
    rpcManagerActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onMsg(RpcBroadcastMsg msg) {
    log.trace("Processing broadcast rpc msg: {}", msg);
    rpcManagerActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public void onServerAdded(ServerInstance server) {
    log.trace("Processing onServerAdded msg: {}", server);
    broadcast(new ClusterEventMsg(server.getServerAddress(), true));
  }

  @Override
  public void onServerUpdated(ServerInstance server) {

  }

  @Override
  public void onServerRemoved(ServerInstance server) {
    log.trace("Processing onServerRemoved msg: {}", server);
    broadcast(new ClusterEventMsg(server.getServerAddress(), false));
  }

  @Override
  public void onPluginStateChange(TenantId tenantId, PluginId pluginId, ComponentLifecycleEvent state) {
    log.trace("[{}] Processing onPluginStateChange event: {}", pluginId, state);
    broadcast(ComponentLifecycleMsg.forPlugin(tenantId, pluginId, state));
  }

  @Override
  public void onRuleStateChange(TenantId tenantId, RuleId ruleId, ComponentLifecycleEvent state) {
    log.trace("[{}] Processing onRuleStateChange event: {}", ruleId, state);
    broadcast(ComponentLifecycleMsg.forRule(tenantId, ruleId, state));
  }

  @Override
  public void onCredentialsUpdate(TenantId tenantId, DeviceId deviceId) {
    DeviceCredentialsUpdateNotificationMsg msg = new DeviceCredentialsUpdateNotificationMsg(tenantId, deviceId);
    Optional<ServerAddress> address = actorContext.getRoutingService().resolveById(deviceId);
    if (address.isPresent()) {
      rpcService.tell(address.get(), msg);
    } else {
      onMsg(msg);
    }
  }

  @Override
  public void onCredentialsUpdate(TenantId tenantId, AssetId assetId) {
    AssetCredentialsUpdateNotificationMsg msg = new AssetCredentialsUpdateNotificationMsg(tenantId, assetId);
    Optional<ServerAddress> address = actorContext.getRoutingService().resolveById(assetId);
    if (address.isPresent()) {
      rpcService.tell(address.get(), msg);
    } else {
      onMsg(msg);
    }
  }

  @Override
  public void onDeviceNameOrTypeUpdate(TenantId tenantId, DeviceId deviceId, String deviceName, String deviceType) {
    log.trace("[{}] Processing onDeviceNameOrTypeUpdate event, deviceName: {}, deviceType: {}", deviceId, deviceName,
        deviceType);
    DeviceNameOrTypeUpdateMsg msg = new DeviceNameOrTypeUpdateMsg(tenantId, deviceId, deviceName, deviceType);
    Optional<ServerAddress> address = actorContext.getRoutingService().resolveById(deviceId);
    if (address.isPresent()) {
      rpcService.tell(address.get(), msg);
    } else {
      onMsg(msg);
    }
  }

  public void broadcast(ToAllNodesMsg msg) {
    rpcService.broadcast(msg);
    appActor.tell(msg, ActorRef.noSender());
  }

  private void broadcast(ClusterEventMsg msg) {
    this.appActor.tell(msg, ActorRef.noSender());
    this.sessionManagerActor.tell(msg, ActorRef.noSender());
    this.rpcManagerActor.tell(msg, ActorRef.noSender());
  }
}
