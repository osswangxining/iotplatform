package org.iotp.infomgt.data.security;

public interface DeviceCredentialsFilter {

  String getCredentialsId();

  DeviceCredentialsType getCredentialsType();

}
