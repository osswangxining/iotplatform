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

import java.util.UUID;

import org.iotp.infomgt.dao.Dao;
import org.iotp.infomgt.data.security.AssetCredentials;

/**
 * The Interface AssetCredentialsDao.
 */
public interface AssetCredentialsDao extends Dao<AssetCredentials> {

    /**
     * Save or update asset credentials object
     *
     * @param assetCredentials the asset credentials object
     * @return saved asset credentials object
     */
    AssetCredentials save(AssetCredentials assetCredentials);

    /**
     * Find asset credentials by asset id.
     *
     * @param assetId the asset id
     * @return the asset credentials object
     */
    AssetCredentials findByAssetId(UUID assetId);

    /**
     * Find asset credentials by credentials id.
     *
     * @param credentialsId the credentials id
     * @return the asset credentials object
     */
    AssetCredentials findByCredentialsId(String credentialsId);

}
