package org.iotp.infomgt.data.kv;

/**
 * Represents time series KV data entry
 * 
 */
public interface TsKvEntry extends KvEntry {

  long getTs();

}
