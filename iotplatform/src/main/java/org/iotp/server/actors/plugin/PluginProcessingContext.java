package org.iotp.server.actors.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.iotp.analytics.ruleengine.api.device.DeviceAttributesEventNotificationMsg;
import org.iotp.analytics.ruleengine.api.plugins.PluginApiCallSecurityContext;
import org.iotp.analytics.ruleengine.api.plugins.PluginCallback;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.plugins.msg.PluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.TimeoutMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ToDeviceRpcRequest;
import org.iotp.analytics.ruleengine.plugins.rpc.PluginRpcMsg;
import org.iotp.analytics.ruleengine.plugins.rpc.RpcMsg;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketMsg;
import org.iotp.analytics.ruleengine.plugins.ws.PluginWebsocketSessionRef;
import org.iotp.infomgt.data.Asset;
import org.iotp.infomgt.data.Customer;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.Tenant;
import org.iotp.infomgt.data.common.ThingType;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.kv.AttributeKey;
import org.iotp.infomgt.data.kv.AttributeKvEntry;
import org.iotp.infomgt.data.kv.TsKvEntry;
import org.iotp.infomgt.data.kv.TsKvQuery;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.infomgt.data.rule.RuleMetaData;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import akka.actor.ActorRef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PluginProcessingContext implements PluginContext {

  private static final Executor executor = Executors.newSingleThreadExecutor();

  private final SharedPluginProcessingContext pluginCtx;
  private final Optional<PluginApiCallSecurityContext> securityCtx;

  public PluginProcessingContext(SharedPluginProcessingContext pluginCtx, PluginApiCallSecurityContext securityCtx) {
    super();
    this.pluginCtx = pluginCtx;
    this.securityCtx = Optional.ofNullable(securityCtx);
  }

  public void persistError(String method, Exception e) {
    pluginCtx.persistError(method, e);
  }

  @Override
  public void sendPluginRpcMsg(RpcMsg msg) {
    this.pluginCtx.rpcService.tell(new PluginRpcMsg(pluginCtx.tenantId, pluginCtx.pluginId, msg));
  }

  @Override
  public void send(PluginWebsocketMsg<?> wsMsg) throws IOException {
    pluginCtx.msgEndpoint.send(wsMsg);
  }

  @Override
  public void close(PluginWebsocketSessionRef sessionRef) throws IOException {
    pluginCtx.msgEndpoint.close(sessionRef);
  }

  @Override
  public void saveAttributes(final TenantId tenantId, final EntityId entityId, final String scope,
      final List<AttributeKvEntry> attributes, final PluginCallback<Void> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<Void>> futures = pluginCtx.attributesService.save(entityId, scope, attributes);
      Futures.addCallback(futures, getListCallback(callback, v -> {
        if (entityId.getEntityType() == ThingType.DEVICE) {
          onDeviceAttributesChanged(tenantId, new DeviceId(entityId.getId()), scope, attributes);
        }
        return null;
      }), executor);
    }));
  }

  @Override
  public void removeAttributes(final TenantId tenantId, final EntityId entityId, final String scope,
      final List<String> keys, final PluginCallback<Void> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<Void>> futures = pluginCtx.attributesService.removeAll(entityId, scope, keys);
      Futures.addCallback(futures, getCallback(callback, v -> null), executor);
      if (entityId.getEntityType() == ThingType.DEVICE) {
        onDeviceAttributesDeleted(tenantId, new DeviceId(entityId.getId()),
            keys.stream().map(key -> new AttributeKey(scope, key)).collect(Collectors.toSet()));
      }
    }));
  }

  @Override
  public void loadAttribute(EntityId entityId, String attributeType, String attributeKey,
      final PluginCallback<Optional<AttributeKvEntry>> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<Optional<AttributeKvEntry>> future = pluginCtx.attributesService.find(entityId, attributeType,
          attributeKey);
      Futures.addCallback(future, getCallback(callback, v -> v), executor);
    }));
  }

  @Override
  public void loadAttributes(EntityId entityId, String attributeType, Collection<String> attributeKeys,
      final PluginCallback<List<AttributeKvEntry>> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<AttributeKvEntry>> future = pluginCtx.attributesService.find(entityId, attributeType,
          attributeKeys);
      Futures.addCallback(future, getCallback(callback, v -> v), executor);
    }));
  }

  @Override
  public void loadAttributes(EntityId entityId, String attributeType, PluginCallback<List<AttributeKvEntry>> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<AttributeKvEntry>> future = pluginCtx.attributesService.findAll(entityId, attributeType);
      Futures.addCallback(future, getCallback(callback, v -> v), executor);
    }));
  }

  @Override
  public void loadAttributes(final EntityId entityId, final Collection<String> attributeTypes,
      final PluginCallback<List<AttributeKvEntry>> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      List<ListenableFuture<List<AttributeKvEntry>>> futures = new ArrayList<>();
      attributeTypes
          .forEach(attributeType -> futures.add(pluginCtx.attributesService.findAll(entityId, attributeType)));
      convertFuturesAndAddCallback(callback, futures);
    }));
  }

  @Override
  public void loadAttributes(final EntityId entityId, final Collection<String> attributeTypes,
      final Collection<String> attributeKeys, final PluginCallback<List<AttributeKvEntry>> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      List<ListenableFuture<List<AttributeKvEntry>>> futures = new ArrayList<>();
      attributeTypes.forEach(
          attributeType -> futures.add(pluginCtx.attributesService.find(entityId, attributeType, attributeKeys)));
      convertFuturesAndAddCallback(callback, futures);
    }));
  }

  @Override
  public void saveTsData(final EntityId entityId, final TsKvEntry entry, final PluginCallback<Void> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<Void>> rsListFuture = pluginCtx.tsService.save(entityId, entry);
      Futures.addCallback(rsListFuture, getListCallback(callback, v -> null), executor);
    }));
  }

  @Override
  public void saveTsData(final EntityId entityId, final List<TsKvEntry> entries, final PluginCallback<Void> callback) {
    saveTsData(entityId, entries, 0L, callback);
  }

  @Override
  public void saveTsData(final EntityId entityId, final List<TsKvEntry> entries, long ttl,
      final PluginCallback<Void> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<Void>> rsListFuture = pluginCtx.tsService.save(entityId, entries, ttl);
      Futures.addCallback(rsListFuture, getListCallback(callback, v -> null), executor);
    }));
  }

  @Override
  public void loadTimeseries(final EntityId entityId, final List<TsKvQuery> queries,
      final PluginCallback<List<TsKvEntry>> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<TsKvEntry>> future = pluginCtx.tsService.findAll(entityId, queries);
      Futures.addCallback(future, getCallback(callback, v -> v), executor);
    }));
  }

  @Override
  public void loadLatestTimeseries(final EntityId entityId, final PluginCallback<List<TsKvEntry>> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<TsKvEntry>> future = pluginCtx.tsService.findAllLatest(entityId);
      Futures.addCallback(future, getCallback(callback, v -> v), executor);
    }));
  }

  @Override
  public void loadLatestTimeseries(final EntityId entityId, final Collection<String> keys,
      final PluginCallback<List<TsKvEntry>> callback) {
    validate(entityId, new ValidationCallback(callback, ctx -> {
      ListenableFuture<List<TsKvEntry>> rsListFuture = pluginCtx.tsService.findLatest(entityId, keys);
      Futures.addCallback(rsListFuture, getCallback(callback, v -> v), executor);
    }));
  }

  @Override
  public void reply(PluginToRuleMsg<?> msg) {
    pluginCtx.parentActor.tell(msg, ActorRef.noSender());
  }

  @Override
  public PluginId getPluginId() {
    return pluginCtx.pluginId;
  }

  @Override
  public Optional<PluginApiCallSecurityContext> getSecurityCtx() {
    return securityCtx;
  }

  private void onDeviceAttributesDeleted(TenantId tenantId, DeviceId deviceId, Set<AttributeKey> keys) {
    pluginCtx.toDeviceActor(DeviceAttributesEventNotificationMsg.onDelete(tenantId, deviceId, keys));
  }

  private void onDeviceAttributesChanged(TenantId tenantId, DeviceId deviceId, String scope,
      List<AttributeKvEntry> values) {
    pluginCtx.toDeviceActor(DeviceAttributesEventNotificationMsg.onUpdate(tenantId, deviceId, scope, values));
  }

  private <T, R> FutureCallback<List<T>> getListCallback(final PluginCallback<R> callback,
      Function<List<T>, R> transformer) {
    return new FutureCallback<List<T>>() {
      @Override
      public void onSuccess(@Nullable List<T> result) {
        pluginCtx.self().tell(PluginCallbackMessage.onSuccess(callback, transformer.apply(result)),
            ActorRef.noSender());
      }

      @Override
      public void onFailure(Throwable t) {
        if (t instanceof Exception) {
          pluginCtx.self().tell(PluginCallbackMessage.onError(callback, (Exception) t), ActorRef.noSender());
        } else {
          log.error("Critical error: {}", t.getMessage(), t);
        }
      }
    };
  }

  private <T, R> FutureCallback<R> getCallback(final PluginCallback<T> callback, Function<R, T> transformer) {
    return new FutureCallback<R>() {
      @Override
      public void onSuccess(@Nullable R result) {
        try {
          pluginCtx.self().tell(PluginCallbackMessage.onSuccess(callback, transformer.apply(result)),
              ActorRef.noSender());
        } catch (Exception e) {
          pluginCtx.self().tell(PluginCallbackMessage.onError(callback, e), ActorRef.noSender());
        }
      }

      @Override
      public void onFailure(Throwable t) {
        if (t instanceof Exception) {
          pluginCtx.self().tell(PluginCallbackMessage.onError(callback, (Exception) t), ActorRef.noSender());
        } else {
          log.error("Critical error: {}", t.getMessage(), t);
        }
      }
    };
  }

  @Override
  public void checkAccess(DeviceId deviceId, PluginCallback<Void> callback) {
    validate(deviceId, new ValidationCallback(callback, ctx -> callback.onSuccess(ctx, null)));
  }

  private void validate(EntityId entityId, ValidationCallback callback) {
    if (securityCtx.isPresent()) {
      final PluginApiCallSecurityContext ctx = securityCtx.get();
      if (ctx.isTenantAdmin() || ctx.isCustomerUser() || ctx.isSystemAdmin()) {
        switch (entityId.getEntityType()) {
        case DEVICE:
          if (ctx.isSystemAdmin()) {
            callback.onSuccess(this, Boolean.FALSE);
          } else {
            ListenableFuture<Device> deviceFuture = pluginCtx.deviceService
                .findDeviceByIdAsync(new DeviceId(entityId.getId()));
            Futures.addCallback(deviceFuture, getCallback(callback, device -> {
              if (device == null) {
                return Boolean.FALSE;
              } else {
                if (!device.getTenantId().equals(ctx.getTenantId())) {
                  return Boolean.FALSE;
                } else if (ctx.isCustomerUser() && !device.getCustomerId().equals(ctx.getCustomerId())) {
                  return Boolean.FALSE;
                } else {
                  return Boolean.TRUE;
                }
              }
            }));
          }
          return;
        case ASSET:
          if (ctx.isSystemAdmin()) {
            callback.onSuccess(this, Boolean.FALSE);
          } else {
            ListenableFuture<Asset> assetFuture = pluginCtx.assetService
                .findAssetByIdAsync(new AssetId(entityId.getId()));
            Futures.addCallback(assetFuture, getCallback(callback, asset -> {
              if (asset == null) {
                return Boolean.FALSE;
              } else {
                if (!asset.getTenantId().equals(ctx.getTenantId())) {
                  return Boolean.FALSE;
                } else if (ctx.isCustomerUser() && !asset.getCustomerId().equals(ctx.getCustomerId())) {
                  return Boolean.FALSE;
                } else {
                  return Boolean.TRUE;
                }
              }
            }));
          }
          return;
        case RULE:
          if (ctx.isCustomerUser()) {
            callback.onSuccess(this, Boolean.FALSE);
          } else {
            ListenableFuture<RuleMetaData> ruleFuture = pluginCtx.ruleService
                .findRuleByIdAsync(new RuleId(entityId.getId()));
            Futures.addCallback(ruleFuture, getCallback(callback, rule -> {
              if (rule == null) {
                return Boolean.FALSE;
              } else {
                if (ctx.isTenantAdmin() && !rule.getTenantId().equals(ctx.getTenantId())) {
                  return Boolean.FALSE;
                } else if (ctx.isSystemAdmin() && !rule.getTenantId().isNullUid()) {
                  return Boolean.FALSE;
                } else {
                  return Boolean.TRUE;
                }
              }
            }));
          }
          return;
        case PLUGIN:
          if (ctx.isCustomerUser()) {
            callback.onSuccess(this, Boolean.FALSE);
          } else {
            ListenableFuture<PluginMetaData> pluginFuture = pluginCtx.pluginService
                .findPluginByIdAsync(new PluginId(entityId.getId()));
            Futures.addCallback(pluginFuture, getCallback(callback, plugin -> {
              if (plugin == null) {
                return Boolean.FALSE;
              } else {
                if (ctx.isTenantAdmin() && !plugin.getTenantId().equals(ctx.getTenantId())) {
                  return Boolean.FALSE;
                } else if (ctx.isSystemAdmin() && !plugin.getTenantId().isNullUid()) {
                  return Boolean.FALSE;
                } else {
                  return Boolean.TRUE;
                }
              }
            }));
          }
          return;
        case CUSTOMER:
          if (ctx.isSystemAdmin()) {
            callback.onSuccess(this, Boolean.FALSE);
          } else {
            ListenableFuture<Customer> customerFuture = pluginCtx.customerService
                .findCustomerByIdAsync(new CustomerId(entityId.getId()));
            Futures.addCallback(customerFuture, getCallback(callback, customer -> {
              if (customer == null) {
                return Boolean.FALSE;
              } else {
                if (!customer.getTenantId().equals(ctx.getTenantId())) {
                  return Boolean.FALSE;
                } else if (ctx.isCustomerUser() && !customer.getId().equals(ctx.getCustomerId())) {
                  return Boolean.FALSE;
                } else {
                  return Boolean.TRUE;
                }
              }
            }));
          }
          return;
        case TENANT:
          if (ctx.isCustomerUser()) {
            callback.onSuccess(this, Boolean.FALSE);
          } else if (ctx.isSystemAdmin()) {
            callback.onSuccess(this, Boolean.TRUE);
          } else {
            ListenableFuture<Tenant> tenantFuture = pluginCtx.tenantService
                .findTenantByIdAsync(new TenantId(entityId.getId()));
            Futures.addCallback(tenantFuture,
                getCallback(callback, tenant -> tenant != null && tenant.getId().equals(ctx.getTenantId())));
          }
          return;
        default:
          // TODO: add support of other entities
          throw new IllegalStateException("Not Implemented!");
        }
      } else {
        callback.onSuccess(this, Boolean.FALSE);
      }
    } else {
      callback.onSuccess(this, Boolean.TRUE);
    }
  }

  @Override
  public Optional<ServerAddress> resolve(EntityId entityId) {
    return pluginCtx.routingService.resolveById(entityId);
  }

  @Override
  public void getDevice(DeviceId deviceId, PluginCallback<Device> callback) {
    ListenableFuture<Device> deviceFuture = pluginCtx.deviceService.findDeviceByIdAsync(deviceId);
    Futures.addCallback(deviceFuture, getCallback(callback, v -> v));
  }

  @Override
  public void getCustomerDevices(TenantId tenantId, CustomerId customerId, int limit,
      PluginCallback<List<Device>> callback) {
    // TODO: add caching here with async api.
    List<Device> devices = pluginCtx.deviceService
        .findDevicesByTenantIdAndCustomerId(tenantId, customerId, new TextPageLink(limit)).getData();
    pluginCtx.self().tell(PluginCallbackMessage.onSuccess(callback, devices), ActorRef.noSender());
  }

  @Override
  public void sendRpcRequest(ToDeviceRpcRequest msg) {
    pluginCtx.sendRpcRequest(msg);
  }

  @Override
  public void scheduleTimeoutMsg(TimeoutMsg msg) {
    pluginCtx.scheduleTimeoutMsg(msg);
  }

  private void convertFuturesAndAddCallback(PluginCallback<List<AttributeKvEntry>> callback,
      List<ListenableFuture<List<AttributeKvEntry>>> futures) {
    ListenableFuture<List<AttributeKvEntry>> future = Futures.transform(Futures.successfulAsList(futures),
        (Function<? super List<List<AttributeKvEntry>>, ? extends List<AttributeKvEntry>>) input -> {
          List<AttributeKvEntry> result = new ArrayList<>();
          input.forEach(r -> result.addAll(r));
          return result;
        }, executor);
    Futures.addCallback(future, getCallback(callback, v -> v), executor);
  }
}
