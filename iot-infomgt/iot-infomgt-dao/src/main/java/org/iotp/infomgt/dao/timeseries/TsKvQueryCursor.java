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
package org.iotp.infomgt.dao.timeseries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.iotp.infomgt.data.kv.TsKvEntry;
import org.iotp.infomgt.data.kv.TsKvQuery;

import lombok.Getter;

/**
 * Created by ashvayka on 21.02.17.
 */
public class TsKvQueryCursor {
    @Getter
    private final String entityType;
    @Getter
    private final UUID entityId;
    @Getter
    private final String key;
    @Getter
    private final long startTs;
    @Getter
    private final long endTs;
    private final List<Long> partitions;
    @Getter
    private final List<TsKvEntry> data;

    private int partitionIndex;
    private int currentLimit;

    public TsKvQueryCursor(String entityType, UUID entityId, TsKvQuery baseQuery, List<Long> partitions) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.key = baseQuery.getKey();
        this.startTs = baseQuery.getStartTs();
        this.endTs = baseQuery.getEndTs();
        this.partitions = partitions;
        this.partitionIndex = partitions.size() - 1;
        this.data = new ArrayList<>();
        this.currentLimit = baseQuery.getLimit();
    }

    public boolean hasNextPartition() {
        return partitionIndex >= 0;
    }

    public boolean isFull() {
        return currentLimit <= 0;
    }

    public long getNextPartition() {
        long partition = partitions.get(partitionIndex);
        partitionIndex--;
        return partition;
    }

    public int getCurrentLimit() {
        return currentLimit;
    }

    public void addData(List<TsKvEntry> newData) {
        currentLimit -= newData.size();
        data.addAll(newData);
    }
}
