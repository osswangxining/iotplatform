package org.iotp.infomgt.data.kv;

public interface TsKvQuery {

  String getKey();

  long getStartTs();

  long getEndTs();

  long getInterval();

  int getLimit();

  Aggregation getAggregation();

}
