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
package org.iotp.infomgt.dao.nosql;

import org.iotp.infomgt.dao.cassandra.CassandraCluster;
import org.iotp.infomgt.dao.model.type.AuthorityCodec;
import org.iotp.infomgt.dao.model.type.ComponentLifecycleStateCodec;
import org.iotp.infomgt.dao.model.type.ComponentScopeCodec;
import org.iotp.infomgt.dao.model.type.ComponentTypeCodec;
import org.iotp.infomgt.dao.model.type.DeviceCredentialsTypeCodec;
import org.iotp.infomgt.dao.model.type.EntityTypeCodec;
import org.iotp.infomgt.dao.model.type.JsonCodec;
import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.core.CodecRegistry;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.core.exceptions.CodecNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CassandraAbstractDao {

    @Autowired
    protected CassandraCluster cluster;

    private Session session;

    private ConsistencyLevel defaultReadLevel;
    private ConsistencyLevel defaultWriteLevel;

    protected Session getSession() {
        if (session == null) {
            session = cluster.getSession();
            defaultReadLevel = cluster.getDefaultReadConsistencyLevel();
            defaultWriteLevel = cluster.getDefaultWriteConsistencyLevel();
            CodecRegistry registry = session.getCluster().getConfiguration().getCodecRegistry();
            registerCodecIfNotFound(registry, new JsonCodec());
            registerCodecIfNotFound(registry, new DeviceCredentialsTypeCodec());
            registerCodecIfNotFound(registry, new AuthorityCodec());
            registerCodecIfNotFound(registry, new ComponentLifecycleStateCodec());
            registerCodecIfNotFound(registry, new ComponentTypeCodec());
            registerCodecIfNotFound(registry, new ComponentScopeCodec());
            registerCodecIfNotFound(registry, new EntityTypeCodec());
        }
        return session;
    }

    private void registerCodecIfNotFound(CodecRegistry registry, TypeCodec<?> codec) {
        try {
            registry.codecFor(codec.getCqlType(), codec.getJavaType());
        } catch (CodecNotFoundException e) {
            registry.register(codec);
        }
    }

    protected ResultSet executeRead(Statement statement) {
        return execute(statement, defaultReadLevel);
    }

    protected ResultSet executeWrite(Statement statement) {
        return execute(statement, defaultWriteLevel);
    }

    protected ResultSetFuture executeAsyncRead(Statement statement) {
        return executeAsync(statement, defaultReadLevel);
    }

    protected ResultSetFuture executeAsyncWrite(Statement statement) {
        return executeAsync(statement, defaultWriteLevel);
    }

    private ResultSet execute(Statement statement, ConsistencyLevel level) {
        log.debug("Execute cassandra statement {}", statement);
        if (statement.getConsistencyLevel() == null) {
            statement.setConsistencyLevel(level);
        }
        return getSession().execute(statement);
    }

    private ResultSetFuture executeAsync(Statement statement, ConsistencyLevel level) {
        log.debug("Execute cassandra async statement {}", statement);
        if (statement.getConsistencyLevel() == null) {
            statement.setConsistencyLevel(level);
        }
        return getSession().executeAsync(statement);
    }
}
