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
package org.iotp.infomgt.dao.model.sql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.iotp.infomgt.dao.model.BaseEntity;
import org.iotp.infomgt.dao.model.BaseSqlEntity;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.data.id.DeviceCredentialsId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.security.DeviceCredentials;
import org.iotp.infomgt.data.security.DeviceCredentialsType;

import com.datastax.driver.core.utils.UUIDs;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = ModelConstants.DEVICE_CREDENTIALS_COLUMN_FAMILY_NAME)
public final class DeviceCredentialsEntity extends BaseSqlEntity<DeviceCredentials> implements BaseEntity<DeviceCredentials> {

    @Transient
    private static final long serialVersionUID = -2512362753385470464L;

    @Column(name = ModelConstants.DEVICE_CREDENTIALS_DEVICE_ID_PROPERTY)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.DEVICE_CREDENTIALS_CREDENTIALS_TYPE_PROPERTY)
    private DeviceCredentialsType credentialsType;

    @Column(name = ModelConstants.DEVICE_CREDENTIALS_CREDENTIALS_ID_PROPERTY)
    private String credentialsId;

    @Column(name = ModelConstants.DEVICE_CREDENTIALS_CREDENTIALS_VALUE_PROPERTY)
    private String credentialsValue;

    public DeviceCredentialsEntity() {
        super();
    }

    public DeviceCredentialsEntity(DeviceCredentials deviceCredentials) {
        if (deviceCredentials.getId() != null) {
            this.setId(deviceCredentials.getId().getId());
        }
        if (deviceCredentials.getDeviceId() != null) {
            this.deviceId = toString(deviceCredentials.getDeviceId().getId());
        }
        this.credentialsType = deviceCredentials.getCredentialsType();
        this.credentialsId = deviceCredentials.getCredentialsId();
        this.credentialsValue = deviceCredentials.getCredentialsValue();
    }

    @Override
    public DeviceCredentials toData() {
        DeviceCredentials deviceCredentials = new DeviceCredentials(new DeviceCredentialsId(getId()));
        deviceCredentials.setCreatedTime(UUIDs.unixTimestamp(getId()));
        if (deviceId != null) {
            deviceCredentials.setDeviceId(new DeviceId(toUUID(deviceId)));
        }
        deviceCredentials.setCredentialsType(credentialsType);
        deviceCredentials.setCredentialsId(credentialsId);
        deviceCredentials.setCredentialsValue(credentialsValue);
        return deviceCredentials;
    }

}
