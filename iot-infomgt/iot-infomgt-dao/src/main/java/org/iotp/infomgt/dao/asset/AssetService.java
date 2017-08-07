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
package org.iotp.infomgt.dao.asset;

import java.util.List;
import java.util.Optional;

import org.iotp.infomgt.data.Asset;
import org.iotp.infomgt.data.TenantAssetType;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;

import com.google.common.util.concurrent.ListenableFuture;

public interface AssetService {

    Asset findAssetById(AssetId assetId);

    ListenableFuture<Asset> findAssetByIdAsync(AssetId assetId);

    Optional<Asset> findAssetByTenantIdAndName(TenantId tenantId, String name);

    Asset saveAsset(Asset asset);

    Asset assignAssetToCustomer(AssetId assetId, CustomerId customerId);

    Asset unassignAssetFromCustomer(AssetId assetId);

    void deleteAsset(AssetId assetId);

    TextPageData<Asset> findAssetsByTenantId(TenantId tenantId, TextPageLink pageLink);

    TextPageData<Asset> findAssetsByTenantIdAndType(TenantId tenantId, String type, TextPageLink pageLink);

    ListenableFuture<List<Asset>> findAssetsByTenantIdAndIdsAsync(TenantId tenantId, List<AssetId> assetIds);

    void deleteAssetsByTenantId(TenantId tenantId);

    TextPageData<Asset> findAssetsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, TextPageLink pageLink);

    TextPageData<Asset> findAssetsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, TextPageLink pageLink);

    ListenableFuture<List<Asset>> findAssetsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<AssetId> assetIds);

    void unassignCustomerAssets(TenantId tenantId, CustomerId customerId);

    ListenableFuture<List<Asset>> findAssetsByQuery(AssetSearchQuery query);

    ListenableFuture<List<TenantAssetType>> findAssetTypesByTenantId(TenantId tenantId);
}
