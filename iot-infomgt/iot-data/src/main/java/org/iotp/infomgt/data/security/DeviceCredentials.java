package org.iotp.infomgt.data.security;

import org.iotp.infomgt.data.common.BaseData;
import org.iotp.infomgt.data.id.DeviceCredentialsId;
import org.iotp.infomgt.data.id.DeviceId;

public class DeviceCredentials extends BaseData<DeviceCredentialsId> implements DeviceCredentialsFilter {

    private static final long serialVersionUID = -7869261127032877765L;
    
    private DeviceId deviceId;
    private DeviceCredentialsType credentialsType;
    private String credentialsId;
    private String credentialsValue;
    
    public DeviceCredentials() {
        super();
    }

    public DeviceCredentials(DeviceCredentialsId id) {
        super(id);
    }

    public DeviceCredentials(DeviceCredentials deviceCredentials) {
        super(deviceCredentials);
        this.deviceId = deviceCredentials.getDeviceId();
        this.credentialsType = deviceCredentials.getCredentialsType();
        this.credentialsId = deviceCredentials.getCredentialsId();
        this.credentialsValue = deviceCredentials.getCredentialsValue();
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(DeviceId deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public DeviceCredentialsType getCredentialsType() {
        return credentialsType;
    }

    public void setCredentialsType(DeviceCredentialsType credentialsType) {
        this.credentialsType = credentialsType;
    }

    @Override
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
        int result = super.hashCode();
        result = prime * result + ((credentialsId == null) ? 0 : credentialsId.hashCode());
        result = prime * result + ((credentialsType == null) ? 0 : credentialsType.hashCode());
        result = prime * result + ((credentialsValue == null) ? 0 : credentialsValue.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceCredentials other = (DeviceCredentials) obj;
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
        if (deviceId == null) {
            if (other.deviceId != null)
                return false;
        } else if (!deviceId.equals(other.deviceId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DeviceCredentials [deviceId=" + deviceId + ", credentialsType=" + credentialsType + ", credentialsId="
                + credentialsId + ", credentialsValue=" + credentialsValue + ", createdTime=" + createdTime + ", id="
                + id + "]";
    }

}
