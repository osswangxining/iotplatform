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
package org.iotp.infomgt.dao.model.nosql;

import static org.iotp.infomgt.dao.model.ModelConstants.ASSET_CREDENTIALS_ASSET_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ASSET_CREDENTIALS_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.ASSET_CREDENTIALS_CREDENTIALS_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ASSET_CREDENTIALS_CREDENTIALS_TYPE_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ASSET_CREDENTIALS_CREDENTIALS_VALUE_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ID_PROPERTY;

import java.util.UUID;

import org.iotp.infomgt.dao.model.BaseEntity;
import org.iotp.infomgt.dao.model.type.AssetCredentialsTypeCodec;
import org.iotp.infomgt.data.id.AssetCredentialsId;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.security.AssetCredentials;
import org.iotp.infomgt.data.security.AssetCredentialsType;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

@Table(name = ASSET_CREDENTIALS_COLUMN_FAMILY_NAME)
public final class AssetCredentialsEntity implements BaseEntity<AssetCredentials> {

    @Transient
    private static final long serialVersionUID = -2667310560260621252L;
    
    @PartitionKey(value = 0)
    @Column(name = ID_PROPERTY)
    private UUID id;
    
    @Column(name = ASSET_CREDENTIALS_ASSET_ID_PROPERTY)
    private UUID assetId;
    
    @Column(name = ASSET_CREDENTIALS_CREDENTIALS_TYPE_PROPERTY, codec = AssetCredentialsTypeCodec.class)
    private AssetCredentialsType credentialsType;

    @Column(name = ASSET_CREDENTIALS_CREDENTIALS_ID_PROPERTY)
    private String credentialsId;

    @Column(name = ASSET_CREDENTIALS_CREDENTIALS_VALUE_PROPERTY)
    private String credentialsValue;

    public AssetCredentialsEntity() {
        super();
    }

    public AssetCredentialsEntity(AssetCredentials assetCredentials) {
        if (assetCredentials.getId() != null) {
            this.id = assetCredentials.getId().getId();
        }
        if (assetCredentials.getAssetId() != null) {
            this.assetId = assetCredentials.getAssetId().getId();
        }
        this.credentialsType = assetCredentials.getCredentialsType();
        this.credentialsId = assetCredentials.getCredentialsId();
        this.credentialsValue = assetCredentials.getCredentialsValue(); 
    }
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public AssetCredentialsType getCredentialsType() {
        return credentialsType;
    }

    public void setCredentialsType(AssetCredentialsType credentialsType) {
        this.credentialsType = credentialsType;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    public String getCredentialsValue() {
        return credentialsValue;
    }

    public void setCredentialsValue(String credentialsValue) {
        this.credentialsValue = credentialsValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((credentialsId == null) ? 0 : credentialsId.hashCode());
        result = prime * result + ((credentialsType == null) ? 0 : credentialsType.hashCode());
        result = prime * result + ((credentialsValue == null) ? 0 : credentialsValue.hashCode());
        result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssetCredentialsEntity other = (AssetCredentialsEntity) obj;
        if (credentialsId == null) {
            if (other.credentialsId != null)
                return false;
        } else if (!credentialsId.equals(other.credentialsId))
            return false;
        if (credentialsType != other.credentialsType)
            return false;
        if (credentialsValue == null) {
            if (other.credentialsValue != null)
                return false;
        } else if (!credentialsValue.equals(other.credentialsValue))
            return false;
        if (assetId == null) {
            if (other.assetId != null)
                return false;
        } else if (!assetId.equals(other.assetId))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AssetCredentialsEntity [id=");
        builder.append(id);
        builder.append(", assetId=");
        builder.append(assetId);
        builder.append(", credentialsType=");
        builder.append(credentialsType);
        builder.append(", credentialsId=");
        builder.append(credentialsId);
        builder.append(", credentialsValue=");
        builder.append(credentialsValue);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public AssetCredentials toData() {
        AssetCredentials assetCredentials = new AssetCredentials(new AssetCredentialsId(id));
        assetCredentials.setCreatedTime(UUIDs.unixTimestamp(id));
        if (assetId != null) {
            assetCredentials.setAssetId(new AssetId(assetId));
        }
        assetCredentials.setCredentialsType(credentialsType);
        assetCredentials.setCredentialsId(credentialsId);
        assetCredentials.setCredentialsValue(credentialsValue);
        return assetCredentials;
    }

}
