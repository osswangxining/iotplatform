/*******************************************************************************
 * Copyright 2017 osswangxining@163.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iotp.infomgt.dao.entity;

import org.iotp.infomgt.dao.alarm.AlarmService;
import org.iotp.infomgt.dao.asset.AssetService;
import org.iotp.infomgt.dao.customer.CustomerService;
import org.iotp.infomgt.dao.dashboard.DashboardService;
import org.iotp.infomgt.dao.device.DeviceService;
import org.iotp.infomgt.dao.plugin.PluginService;
import org.iotp.infomgt.dao.rule.RuleService;
import org.iotp.infomgt.dao.tenant.TenantService;
import org.iotp.infomgt.dao.user.UserService;
import org.iotp.infomgt.data.common.NamingThing;
import org.iotp.infomgt.data.id.AlarmId;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DashboardId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.id.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by ashvayka on 04.05.17.
 */
@Service
@Slf4j
public class BaseEntityService extends AbstractEntityService implements EntityService {

    @Autowired
    private AssetService assetService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AlarmService alarmService;

    @Override
    public void deleteEntityRelations(EntityId entityId) {
        super.deleteEntityRelations(entityId);
    }

    @Override
    public ListenableFuture<String> fetchEntityNameAsync(EntityId entityId) {
        log.trace("Executing fetchEntityNameAsync [{}]", entityId);
        ListenableFuture<String> entityName;
        ListenableFuture<? extends NamingThing> hasName;
        switch (entityId.getEntityType()) {
            case ASSET:
                hasName = assetService.findAssetByIdAsync(new AssetId(entityId.getId()));
                break;
            case DEVICE:
                hasName = deviceService.findDeviceByIdAsync(new DeviceId(entityId.getId()));
                break;
            case RULE:
                hasName = ruleService.findRuleByIdAsync(new RuleId(entityId.getId()));
                break;
            case PLUGIN:
                hasName = pluginService.findPluginByIdAsync(new PluginId(entityId.getId()));
                break;
            case TENANT:
                hasName = tenantService.findTenantByIdAsync(new TenantId(entityId.getId()));
                break;
            case CUSTOMER:
                hasName = customerService.findCustomerByIdAsync(new CustomerId(entityId.getId()));
                break;
            case USER:
                hasName = userService.findUserByIdAsync(new UserId(entityId.getId()));
                break;
            case DASHBOARD:
                hasName = dashboardService.findDashboardInfoByIdAsync(new DashboardId(entityId.getId()));
                break;
            case ALARM:
                hasName = alarmService.findAlarmByIdAsync(new AlarmId(entityId.getId()));
                break;
            default:
                throw new IllegalStateException("Not Implemented!");
        }
        entityName = Futures.transform(hasName, (Function<NamingThing, String>) hasName1 -> hasName1 != null ? hasName1.getName() : null );
        return entityName;
    }

}
