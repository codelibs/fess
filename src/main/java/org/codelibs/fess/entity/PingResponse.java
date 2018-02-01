/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import java.io.IOException;

import org.codelibs.core.lang.StringUtil;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class PingResponse {
    private final int status;

    private final String clusterName;

    private final String clusterStatus;

    private String message = StringUtil.EMPTY;

    public PingResponse(final ClusterHealthResponse response) {
        status = response.getStatus() == ClusterHealthStatus.RED ? 1 : 0;
        clusterName = response.getClusterName();
        clusterStatus = response.getStatus().toString();
        try {
            final XContentBuilder builder = XContentFactory.jsonBuilder();
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            message = builder.string();
        } catch (final IOException e) {
            message = "{ \"error\" : \"" + e.getMessage() + "\"}";
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
