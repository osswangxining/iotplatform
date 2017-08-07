package org.iotp.analytics.ruleengine.api.plugins;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.plugins.msg.PluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.TimeoutMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequest;
import org.iotp.analytics.ruleengine.plugins.rpc.RpcMsg;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketMsg;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketSessionRef;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.kv.AttributeKvEntry;
import org.iotp.infomgt.data.kv.TsKvEntry;
import org.iotp.infomgt.data.kv.TsKvQuery;

public interface PluginContext {

  PluginId getPluginId();

  void reply(PluginToRuleMsg<?> msg);

  void checkAccess(DeviceId deviceId, PluginCallback<Void> callback);

  Optional<PluginApiCallSecurityContext> getSecurityCtx();

  void persistError(String method, Exception e);

  /*
   * Device RPC API
   */

  Optional<ServerAddress> resolve(EntityId entityId);

  void getDevice(DeviceId deviceId, PluginCallback<Device> pluginCallback);

  void sendRpcRequest(ToDeviceRpcRequest msg);

  void scheduleTimeoutMsg(TimeoutMsg<?> timeoutMsg);

  /*
   * Websocket API
   */

  void send(PluginWebsocketMsg<?> wsMsg) throws IOException;

  void close(PluginWebsocketSessionRef sessionRef) throws IOException;

  /*
   * Plugin RPC API
   */

  void sendPluginRpcMsg(RpcMsg msg);

  /*
   * Timeseries API
   */

  void saveTsData(EntityId entityId, TsKvEntry entry, PluginCallback<Void> callback);

  void saveTsData(EntityId entityId, List<TsKvEntry> entries, PluginCallback<Void> callback);

  void saveTsData(EntityId deviceId, List<TsKvEntry> entries, long ttl, PluginCallback<Void> pluginCallback);

  void loadTimeseries(EntityId entityId, List<TsKvQuery> queries, PluginCallback<List<TsKvEntry>> callback);

  void loadLatestTimeseries(EntityId entityId, Collection<String> keys, PluginCallback<List<TsKvEntry>> callback);

  void loadLatestTimeseries(EntityId entityId, PluginCallback<List<TsKvEntry>> callback);

  /*
   * Attributes API
   */

  void saveAttributes(TenantId tenantId, EntityId entityId, String attributeType, List<AttributeKvEntry> attributes,
      PluginCallback<Void> callback);

  void removeAttributes(TenantId tenantId, EntityId entityId, String scope, List<String> attributeKeys,
      PluginCallback<Void> callback);

  void loadAttribute(EntityId entityId, String attributeType, String attributeKey,
      PluginCallback<Optional<AttributeKvEntry>> callback);

  void loadAttributes(EntityId entityId, String attributeType, Collection<String> attributeKeys,
      PluginCallback<List<AttributeKvEntry>> callback);

  void loadAttributes(EntityId entityId, String attributeType, PluginCallback<List<AttributeKvEntry>> callback);

  void loadAttributes(EntityId entityId, Collection<String> attributeTypes,
      PluginCallback<List<AttributeKvEntry>> callback);

  void loadAttributes(EntityId entityId, Collection<String> attributeTypes, Collection<String> attributeKeys,
      PluginCallback<List<AttributeKvEntry>> callback);

  void getCustomerDevices(TenantId tenantId, CustomerId customerId, int limit, PluginCallback<List<Device>> callback);

}
