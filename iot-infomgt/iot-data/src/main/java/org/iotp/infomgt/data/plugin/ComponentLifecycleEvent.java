package org.iotp.infomgt.data.plugin;

import java.io.Serializable;

public enum ComponentLifecycleEvent implements Serializable {
  CREATED, STARTED, ACTIVATED, SUSPENDED, UPDATED, STOPPED, DELETED
}