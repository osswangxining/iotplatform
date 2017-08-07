package org.iotp.analytics.ruleengine.api.device;

import java.util.List;
import java.util.Set;

import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.kv.AttributeKey;
import org.iotp.infomgt.data.kv.AttributeKvEntry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 */
@ToString
@AllArgsConstructor
public class DeviceAttributesEventNotificationMsg implements ToDeviceActorNotificationMsg {

    /**
   * 
   */
  private static final long serialVersionUID = 8605682062889233238L;
    @Getter
    private final TenantId tenantId;
    @Getter
    private final DeviceId deviceId;
    @Getter
    private final Set<AttributeKey> deletedKeys;
    @Getter
    private final String scope;
    @Getter
    private final List<AttributeKvEntry> values;
    @Getter
    private final boolean deleted;

    public static DeviceAttributesEventNotificationMsg onUpdate(TenantId tenantId, DeviceId deviceId, String scope, List<AttributeKvEntry> values) {
        return new DeviceAttributesEventNotificationMsg(tenantId, deviceId, null, scope, values, false);
    }

    public static DeviceAttributesEventNotificationMsg onDelete(TenantId tenantId, DeviceId deviceId, Set<AttributeKey> keys) {
        return new DeviceAttributesEventNotificationMsg(tenantId, deviceId, keys, null, null, true);
    }

}
