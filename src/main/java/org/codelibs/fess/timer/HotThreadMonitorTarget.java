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

import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.action.admin.cluster.node.hotthreads.NodesHotThreadsResponse;
import org.opensearch.common.unit.TimeValue;

/**
 * Monitor target for tracking hot threads in the OpenSearch cluster.
 * This class extends MonitorTarget to provide monitoring functionality for
 * hot threads, which helps identify performance bottlenecks and resource
 * usage issues in the search engine cluster.
 */
public class HotThreadMonitorTarget extends MonitorTarget {
    private static final Logger logger = LogManager.getLogger(HotThreadMonitorTarget.class);

    /**
     * Default constructor for HotThreadMonitorTarget.
     */
    public HotThreadMonitorTarget() {
        // Default constructor
    }

    @Override
    public void expired() {
        final StringBuilder buf = new StringBuilder(1000);

        buf.append("[HOTTHREAD MONITOR] ");

        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        buf.append('{');

        final boolean ignoreIdleThreads = Constants.TRUE.equalsIgnoreCase(fessConfig.getCrawlerHotthreadIgnoreIdleThreads());
        final TimeValue interval = TimeValue.parseTimeValue(fessConfig.getCrawlerHotthreadInterval(), "crawler.hotthread.interval");
        final int threads = fessConfig.getCrawlerHotthreadThreadsAsInteger();
        final String timeout = fessConfig.getCrawlerHotthreadTimeout();
        final String type = fessConfig.getCrawlerHotthreadType();
        try {
            final SearchEngineClient esClient = ComponentUtil.getSearchEngineClient();
            final NodesHotThreadsResponse response =
                    esClient.admin().cluster().prepareNodesHotThreads().setIgnoreIdleThreads(ignoreIdleThreads).setInterval(interval)
                            .setThreads(threads).setTimeout(timeout).setType(type).execute().actionGet(timeout);
            append(buf, "cluster_name", () -> response.getClusterName().value()).append(',');
            final String hotThreads = response.getNodesMap().entrySet().stream().map(e -> {
                final StringBuilder tempBuf = new StringBuilder();
                append(tempBuf, StringEscapeUtils.escapeJson(e.getKey()), () -> StringEscapeUtils.escapeJson(e.getValue().getHotThreads()));
                return tempBuf.toString();
            }).collect(Collectors.joining(","));
            buf.append(hotThreads).append(',');
        } catch (final Exception e) {
            appendException(buf, e).append(',');
        }

        appendTimestamp(buf);
        buf.append('}');

        if (logger.isInfoEnabled()) {
            logger.info(buf.toString());
        }
    }

}
