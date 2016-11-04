/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.util.List;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.cluster.health.ClusterHealthStatus;

public class PingResponse {
    private final int status;

    private final List<String> failures;

    private final String clusterName;

    private final String clusterStatus;

    public PingResponse(final ClusterHealthResponse response) {
        status = response.getStatus() == ClusterHealthStatus.RED ? 1 : 0;
        failures = response.getValidationFailures();
        clusterName = response.getClusterName();
        clusterStatus = response.getStatus().toString();
    }

    public int getStatus() {
        return status;
    }

    public String[] getFailures() {
        return failures.stream().toArray(n -> new String[n]);
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getClusterStatus() {
        return clusterStatus;
    }
}
