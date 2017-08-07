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
package org.iotp.infomgt.dao.devicetype;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.iotp.infomgt.dao.Dao;
import org.iotp.infomgt.data.DeviceType;
import org.iotp.infomgt.data.TenantDeviceType;
import org.iotp.infomgt.data.page.TextPageLink;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Interface DeviceDao.
 *
 */
public interface DeviceTypeDao extends Dao<DeviceType> {

    /**
     * Save or update device object
     *
     * @param device the device object
     * @return saved device object
     */
  DeviceType save(DeviceType deviceType);

    /**
     * Find devices by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of device objects
     */
    List<DeviceType> findDeviceTypesByTenantId(UUID tenantId, TextPageLink pageLink);

    /**
     * Find devices by tenantId, type and page link.
     *
     * @param tenantId the tenantId
     * @param type the type
     * @param pageLink the page link
     * @return the list of device objects
     */
    List<DeviceType> findDeviceTypesByTenantIdAndTypeName(UUID tenantId, String name, TextPageLink pageLink);

    /**
     * Find devices by tenantId and devices Ids.
     *
     * @param tenantId the tenantId
     * @param deviceIds the device Ids
     * @return the list of device objects
     */
    ListenableFuture<List<DeviceType>> findDeviceTypesByTenantIdAndIdsAsync(UUID tenantId, List<UUID> deviceTypeIds);

    /**
     * Find devices by tenantId, customerId and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param pageLink the page link
     * @return the list of device objects
     */
    List<DeviceType> findDeviceTypesByTenantIdAndCustomerId(UUID tenantId, UUID customerId, TextPageLink pageLink);

    /**
     * Find devices by tenantId, customerId, type and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param type the type
     * @param pageLink the page link
     * @return the list of device objects
     */
    List<DeviceType> findDeviceTypesByTenantIdAndCustomerIdAndTypeName(UUID tenantId, UUID customerId, String name, TextPageLink pageLink);


    /**
     * Find devices by tenantId, customerId and devices Ids.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param deviceIds the device Ids
     * @return the list of device objects
     */
    ListenableFuture<List<DeviceType>> findDeviceTypesByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> deviceTypeIds);

    /**
     * Find devices by tenantId and device name.
     *
     * @param tenantId the tenantId
     * @param name the device name
     * @return the optional device object
     */
    Optional<DeviceType> findDeviceTypeByTenantIdAndName(UUID tenantId, String name);

    /**
     * Find tenants device types.
     *
     * @return the list of tenant device type objects
     */
    ListenableFuture<List<TenantDeviceType>> findTenantDeviceTypesAsync();
}
