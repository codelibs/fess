/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.SearchEngineUtil;
import org.lastaflute.di.exception.IORuntimeException;
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.opensearch.cluster.health.ClusterHealthStatus;
import org.opensearch.common.xcontent.XContentType;

public class PingResponse {
    private static final String CLUSTER_NAME = "cluster_name";
    private static final String STATUS = "status";
    private static final String TIMED_OUT = "timed_out";
    private static final String NUMBER_OF_NODES = "number_of_nodes";
    private static final String NUMBER_OF_DATA_NODES = "number_of_data_nodes";
    private static final String NUMBER_OF_PENDING_TASKS = "number_of_pending_tasks";
    private static final String NUMBER_OF_IN_FLIGHT_FETCH = "number_of_in_flight_fetch";
    private static final String DELAYED_UNASSIGNED_SHARDS = "delayed_unassigned_shards";
    private static final String TASK_MAX_WAIT_TIME_IN_QUEUE_IN_MILLIS = "task_max_waiting_in_queue_millis";
    private static final String ACTIVE_SHARDS_PERCENT_AS_NUMBER = "active_shards_percent_as_number";
    private static final String ACTIVE_PRIMARY_SHARDS = "active_primary_shards";
    private static final String ACTIVE_SHARDS = "active_shards";
    private static final String RELOCATING_SHARDS = "relocating_shards";
    private static final String INITIALIZING_SHARDS = "initializing_shards";
    private static final String UNASSIGNED_SHARDS = "unassigned_shards";

    private final int status;

    private final String clusterName;

    private final String clusterStatus;

    private String message = StringUtil.EMPTY;

    public PingResponse(final ClusterHealthResponse response) {
        status = response.getStatus() == ClusterHealthStatus.RED ? 1 : 0;
        clusterName = response.getClusterName();
        clusterStatus = response.getStatus().toString();
        final Set<String> fieldSet = ComponentUtil.getFessConfig().getApiPingEsFieldSet();
        try (OutputStream out = SearchEngineUtil.getXContentBuilderOutputStream((builder, params) -> {
            builder.startObject();
            if (fieldSet.contains(CLUSTER_NAME)) {
                builder.field(CLUSTER_NAME, response.getClusterName());
            }
            if (fieldSet.contains(STATUS)) {
                builder.field(STATUS, response.getStatus().name().toLowerCase(Locale.ROOT));
            }
            if (fieldSet.contains(TIMED_OUT)) {
                builder.field(TIMED_OUT, response.isTimedOut());
            }
            if (fieldSet.contains(NUMBER_OF_NODES)) {
                builder.field(NUMBER_OF_NODES, response.getNumberOfNodes());
            }
            if (fieldSet.contains(NUMBER_OF_DATA_NODES)) {
                builder.field(NUMBER_OF_DATA_NODES, response.getNumberOfDataNodes());
            }
            if (fieldSet.contains(ACTIVE_PRIMARY_SHARDS)) {
                builder.field(ACTIVE_PRIMARY_SHARDS, response.getActivePrimaryShards());
            }
            if (fieldSet.contains(ACTIVE_SHARDS)) {
                builder.field(ACTIVE_SHARDS, response.getActiveShards());
            }
            if (fieldSet.contains(RELOCATING_SHARDS)) {
                builder.field(RELOCATING_SHARDS, response.getRelocatingShards());
            }
            if (fieldSet.contains(INITIALIZING_SHARDS)) {
                builder.field(INITIALIZING_SHARDS, response.getInitializingShards());
            }
            if (fieldSet.contains(UNASSIGNED_SHARDS)) {
                builder.field(UNASSIGNED_SHARDS, response.getUnassignedShards());
            }
            if (fieldSet.contains(DELAYED_UNASSIGNED_SHARDS)) {
                builder.field(DELAYED_UNASSIGNED_SHARDS, response.getDelayedUnassignedShards());
            }
            if (fieldSet.contains(NUMBER_OF_PENDING_TASKS)) {
                builder.field(NUMBER_OF_PENDING_TASKS, response.getNumberOfPendingTasks());
            }
            if (fieldSet.contains(NUMBER_OF_IN_FLIGHT_FETCH)) {
                builder.field(NUMBER_OF_IN_FLIGHT_FETCH, response.getNumberOfInFlightFetch());
            }
            if (fieldSet.contains(TASK_MAX_WAIT_TIME_IN_QUEUE_IN_MILLIS)) {
                builder.field(TASK_MAX_WAIT_TIME_IN_QUEUE_IN_MILLIS, response.getTaskMaxWaitingTime().getMillis());
            }
            if (fieldSet.contains(ACTIVE_SHARDS_PERCENT_AS_NUMBER)) {
                builder.field(ACTIVE_SHARDS_PERCENT_AS_NUMBER, response.getActiveShardsPercent());
            }
            builder.endObject();
            return builder;
        }, XContentType.JSON)) {
            message = ((ByteArrayOutputStream) out).toString(Constants.UTF_8);
            if (StringUtil.isBlank(message)) {
                message = "{}";
            }
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public int getStatus() {
        return status;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getClusterStatus() {
        return clusterStatus;
    }

    public String getMessage() {
        return message;
    }

}
