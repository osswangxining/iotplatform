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
package org.iotp.infomgt.dao.device;

import java.util.List;
import java.util.Optional;

import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.TenantDeviceType;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;

import com.google.common.util.concurrent.ListenableFuture;

public interface DeviceService {
    
    Device findDeviceById(DeviceId deviceId);

    ListenableFuture<Device> findDeviceByIdAsync(DeviceId deviceId);

    Optional<Device> findDeviceByTenantIdAndName(TenantId tenantId, String name);

    Device saveDevice(Device device);

    Device assignDeviceToCustomer(DeviceId deviceId, CustomerId customerId);

    Device unassignDeviceFromCustomer(DeviceId deviceId);

    void deleteDevice(DeviceId deviceId);

    TextPageData<Device> findDevicesByTenantId(TenantId tenantId, TextPageLink pageLink);

    TextPageData<Device> findDevicesByTenantIdAndType(TenantId tenantId, String type, TextPageLink pageLink);

    ListenableFuture<List<Device>> findDevicesByTenantIdAndIdsAsync(TenantId tenantId, List<DeviceId> deviceIds);

    void deleteDevicesByTenantId(TenantId tenantId);

    TextPageData<Device> findDevicesByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, TextPageLink pageLink);

    TextPageData<Device> findDevicesByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, TextPageLink pageLink);

    ListenableFuture<List<Device>> findDevicesByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<DeviceId> deviceIds);

    void unassignCustomerDevices(TenantId tenantId, CustomerId customerId);

    ListenableFuture<List<Device>> findDevicesByQuery(DeviceSearchQuery query);

    ListenableFuture<List<TenantDeviceType>> findDeviceTypesByTenantId(TenantId tenantId);

}
