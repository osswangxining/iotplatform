package org.iotp.analytics.ruleengine.core.plugin.telemetry.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.iotp.analytics.ruleengine.api.plugins.PluginCallback;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.DefaultRuleMsgHandler;
import org.iotp.analytics.ruleengine.common.msg.core.BasicGetAttributesResponse;
import org.iotp.analytics.ruleengine.common.msg.core.BasicStatusCodeResponse;
import org.iotp.analytics.ruleengine.common.msg.core.GetAttributesRequest;
import org.iotp.analytics.ruleengine.common.msg.core.TelemetryUploadRequest;
import org.iotp.analytics.ruleengine.common.msg.core.UpdateAttributesRequest;
import org.iotp.analytics.ruleengine.common.msg.kv.BasicAttributeKVMsg;
import org.iotp.analytics.ruleengine.core.plugin.telemetry.SubscriptionManager;
import org.iotp.analytics.ruleengine.core.plugin.telemetry.sub.SubscriptionType;
import org.iotp.analytics.ruleengine.plugins.msg.GetAttributesRequestRuleToPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ResponsePluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.TelemetryUploadRequestRuleToPluginMsg;
import org.iotp.analytics.ruleengine.plugins.msg.UpdateAttributesRequestRuleToPluginMsg;
import org.iotp.infomgt.data.common.DataConstants;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.kv.AttributeKvEntry;
import org.iotp.infomgt.data.kv.BasicTsKvEntry;
import org.iotp.infomgt.data.kv.KvEntry;
import org.iotp.infomgt.data.kv.TsKvEntry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelemetryRuleMsgHandler extends DefaultRuleMsgHandler {
  private final SubscriptionManager subscriptionManager;

  public TelemetryRuleMsgHandler(SubscriptionManager subscriptionManager) {
    this.subscriptionManager = subscriptionManager;
  }

  @Override
  public void handleGetAttributesRequest(PluginContext ctx, TenantId tenantId, RuleId ruleId,
      GetAttributesRequestRuleToPluginMsg msg) {
    GetAttributesRequest request = msg.getPayload();

    BiPluginCallBack<List<AttributeKvEntry>, List<AttributeKvEntry>> callback = new BiPluginCallBack<List<AttributeKvEntry>, List<AttributeKvEntry>>() {

      @Override
      public void onSuccess(PluginContext ctx, List<AttributeKvEntry> clientAttributes,
          List<AttributeKvEntry> sharedAttributes) {
        BasicGetAttributesResponse response = BasicGetAttributesResponse.onSuccess(request.getMsgType(),
            request.getRequestId(), BasicAttributeKVMsg.from(clientAttributes, sharedAttributes));
        ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId, response));
      }

      @Override
      public void onFailure(PluginContext ctx, Exception e) {
        log.error("Failed to process get attributes request", e);
        ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
            BasicStatusCodeResponse.onError(request.getMsgType(), request.getRequestId(), e)));
      }
    };

    getAttributeKvEntries(ctx, msg.getDeviceId(), DataConstants.CLIENT_SCOPE, request.getClientAttributeNames(),
        callback.getV1Callback());
    getAttributeKvEntries(ctx, msg.getDeviceId(), DataConstants.SHARED_SCOPE, request.getSharedAttributeNames(),
        callback.getV2Callback());
  }

  private void getAttributeKvEntries(PluginContext ctx, DeviceId deviceId, String scope, Optional<Set<String>> names,
      PluginCallback<List<AttributeKvEntry>> callback) {
    if (names.isPresent()) {
      if (!names.get().isEmpty()) {
        ctx.loadAttributes(deviceId, scope, new ArrayList<>(names.get()), callback);
      } else {
        ctx.loadAttributes(deviceId, scope, callback);
      }
    } else {
      callback.onSuccess(ctx, Collections.emptyList());
    }
  }

  @Override
  public void handleTelemetryUploadRequest(PluginContext ctx, TenantId tenantId, RuleId ruleId,
      TelemetryUploadRequestRuleToPluginMsg msg) {
    TelemetryUploadRequest request = msg.getPayload();
    List<TsKvEntry> tsKvEntries = new ArrayList<>();
    for (Map.Entry<Long, List<KvEntry>> entry : request.getData().entrySet()) {
      for (KvEntry kv : entry.getValue()) {
        tsKvEntries.add(new BasicTsKvEntry(entry.getKey(), kv));
      }
    }
    ctx.saveTsData(msg.getDeviceId(), tsKvEntries, msg.getTtl(), new PluginCallback<Void>() {
      @Override
      public void onSuccess(PluginContext ctx, Void data) {
        ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
            BasicStatusCodeResponse.onSuccess(request.getMsgType(), request.getRequestId())));
        subscriptionManager.onLocalSubscriptionUpdate(ctx, msg.getDeviceId(), SubscriptionType.TIMESERIES, s -> {
          List<TsKvEntry> subscriptionUpdate = new ArrayList<TsKvEntry>();
          for (Map.Entry<Long, List<KvEntry>> entry : request.getData().entrySet()) {
            for (KvEntry kv : entry.getValue()) {
              if (s.isAllKeys() || s.getKeyStates().containsKey((kv.getKey()))) {
                subscriptionUpdate.add(new BasicTsKvEntry(entry.getKey(), kv));
              }
            }
          }
          return subscriptionUpdate;
        });
      }

      @Override
      public void onFailure(PluginContext ctx, Exception e) {
        log.error("Failed to process telemetry upload request", e);
        ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
            BasicStatusCodeResponse.onError(request.getMsgType(), request.getRequestId(), e)));
      }
    });
  }

  @Override
  public void handleUpdateAttributesRequest(PluginContext ctx, TenantId tenantId, RuleId ruleId,
      UpdateAttributesRequestRuleToPluginMsg msg) {
    UpdateAttributesRequest request = msg.getPayload();
    ctx.saveAttributes(msg.getTenantId(), msg.getDeviceId(), DataConstants.CLIENT_SCOPE,
        request.getAttributes().stream().collect(Collectors.toList()), new PluginCallback<Void>() {
          @Override
          public void onSuccess(PluginContext ctx, Void value) {
            ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
                BasicStatusCodeResponse.onSuccess(request.getMsgType(), request.getRequestId())));

            subscriptionManager.onLocalSubscriptionUpdate(ctx, msg.getDeviceId(), SubscriptionType.ATTRIBUTES, s -> {
              List<TsKvEntry> subscriptionUpdate = new ArrayList<TsKvEntry>();
              for (AttributeKvEntry kv : request.getAttributes()) {
                if (s.isAllKeys() || s.getKeyStates().containsKey(kv.getKey())) {
                  subscriptionUpdate.add(new BasicTsKvEntry(kv.getLastUpdateTs(), kv));
                }
              }
              return subscriptionUpdate;
            });
          }

          @Override
          public void onFailure(PluginContext ctx, Exception e) {
            log.error("Failed to process attributes update request", e);
            ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
                BasicStatusCodeResponse.onError(request.getMsgType(), request.getRequestId(), e)));
          }
        });
  }
}