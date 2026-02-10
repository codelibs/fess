/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.timer.TimeoutTarget;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.action.admin.cluster.node.stats.NodeStats;
import org.opensearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.opensearch.monitor.os.OsStats;

/**
 * Timeout target that periodically monitors search engine CPU usage.
 */
public class LoadControlMonitorTarget implements TimeoutTarget {

    private static final Logger logger = LogManager.getLogger(LoadControlMonitorTarget.class);

    private final SystemHelper systemHelper;

    private int consecutiveFailures = 0;

    /**
     * Constructs a new load control monitor target.
     *
     * @param systemHelper the system helper to update with CPU usage
     */
    public LoadControlMonitorTarget(final SystemHelper systemHelper) {
        this.systemHelper = systemHelper;
    }

    @Override
    public void expired() {
        try {
            final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
            final NodesStatsResponse response = client.admin().cluster().prepareNodesStats().addMetric("os").execute().actionGet(10000L);

            short maxCpu = 0;
            for (final NodeStats nodeStats : response.getNodes()) {
                final OsStats os = nodeStats.getOs();
                if (os != null && os.getCpu() != null) {
                    final short percent = os.getCpu().getPercent();
                    if (percent > maxCpu) {
                        maxCpu = percent;
                    }
                }
            }
            systemHelper.setSearchEngineCpuPercent(maxCpu);
            consecutiveFailures = 0;
            if (logger.isDebugEnabled()) {
                logger.debug("Search Engine CPU: {}%", maxCpu);
            }
        } catch (final Exception e) {
            systemHelper.setSearchEngineCpuPercent((short) 0);
            consecutiveFailures++;
            if (consecutiveFailures <= 3) {
                logger.warn("Failed to get search engine CPU stats.", e);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Failed to get search engine CPU stats.", e);
            }
        }
    }
}
