/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.response.SolrPingResponse;

public class PingResponse {
    private final int status = 0;

    private final Target[] targets;

    public PingResponse(final Collection<SolrPingResponse> responses) {
        final List<Target> targetList = new ArrayList<>();
        for (final SolrPingResponse response : responses) {
            int status = response.getStatus();
            if (status != 0) {
                status = 1;
            }
            targetList.add(new Target(status, response.getRequestUrl(),
                    response.getElapsedTime(), response.getQTime()));
        }
        targets = targetList.toArray(new Target[targetList.size()]);
    }

    public static class Target {

        private final int status;

        private final String url;

        private final long searchTime;

        private final int queryTime;

        public Target(final int status, final String url,
                final long elapsedTime, final int qTime) {
            this.status = status;
            this.url = url;
            searchTime = elapsedTime;
            queryTime = qTime;
        }

        public int getStatus() {
            return status;
        }

        public String getUrl() {
            return url;
        }

        public long getSearchTime() {
            return searchTime;
        }

        public int getQueryTime() {
            return queryTime;
        }

    }

    public int getStatus() {
        return status;
    }

    public Target[] getTargets() {
        return targets;
    }

}
