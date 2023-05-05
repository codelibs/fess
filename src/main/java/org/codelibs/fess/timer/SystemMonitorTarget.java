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
package org.codelibs.fess.timer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.monitor.jvm.JvmStats;
import org.opensearch.monitor.jvm.JvmStats.BufferPool;
import org.opensearch.monitor.jvm.JvmStats.Classes;
import org.opensearch.monitor.jvm.JvmStats.GarbageCollectors;
import org.opensearch.monitor.jvm.JvmStats.Mem;
import org.opensearch.monitor.jvm.JvmStats.Threads;
import org.opensearch.monitor.os.OsProbe;
import org.opensearch.monitor.os.OsStats;
import org.opensearch.monitor.process.ProcessProbe;

public class SystemMonitorTarget extends MonitorTarget {
    private static final Logger logger = LogManager.getLogger(SystemMonitorTarget.class);

    @Override
    public void expired() {
        final StringBuilder buf = new StringBuilder(1000);

        buf.append("[SYSTEM MONITOR] ");
        buf.append('{');

        appendOsStats(buf);
        appendProcessStats(buf);
        appendJvmStats(buf);
        appendFesenStats(buf);

        appendTimestamp(buf);
        buf.append('}');

        if (logger.isInfoEnabled()) {
            logger.info(buf.toString());
        }
    }

    private void appendJvmStats(final StringBuilder buf) {
        buf.append("\"jvm\":{");
        final JvmStats jvmStats = JvmStats.jvmStats();
        final Mem mem = jvmStats.getMem();
        buf.append("\"memory\":{");
        buf.append("\"heap\":{");
        append(buf, "used", () -> mem.getHeapUsed().getBytes()).append(',');
        append(buf, "committed", () -> mem.getHeapCommitted().getBytes()).append(',');
        append(buf, "max", () -> mem.getHeapMax().getBytes()).append(',');
        append(buf, "percent", () -> mem.getHeapUsedPercent());
        buf.append("},");
        buf.append("\"non_heap\":{");
        append(buf, "used", () -> mem.getNonHeapUsed().getBytes()).append(',');
        append(buf, "committed", () -> mem.getNonHeapCommitted().getBytes());
        buf.append('}');
        buf.append("},");
        final List<BufferPool> bufferPools = jvmStats.getBufferPools();
        buf.append("\"pools\":{");
        buf.append(bufferPools.stream().map(p -> {
            final StringBuilder b = new StringBuilder();
            b.append('"').append(StringEscapeUtils.escapeJson(p.getName())).append("\":{");
            append(b, "count", () -> p.getCount()).append(',');
            append(b, "used", () -> p.getUsed().getBytes()).append(',');
            append(b, "capacity", () -> p.getTotalCapacity().getBytes()).append('}');
            return b.toString();
        }).collect(Collectors.joining(",")));
        buf.append("},");
        final GarbageCollectors gc = jvmStats.getGc();
        buf.append("\"gc\":{");
        buf.append(Arrays.stream(gc.getCollectors()).map(c -> {
            final StringBuilder b = new StringBuilder();
            b.append('"').append(StringEscapeUtils.escapeJson(c.getName())).append("\":{");
            append(b, "count", () -> c.getCollectionCount()).append(',');
            append(b, "time", () -> c.getCollectionTime().getMillis()).append('}');
            return b.toString();
        }).collect(Collectors.joining(",")));
        buf.append("},");
        final Threads threads = jvmStats.getThreads();
        buf.append("\"threads\":{");
        append(buf, "count", () -> threads.getCount()).append(',');
        append(buf, "peak", () -> threads.getPeakCount());
        buf.append("},");
        final Classes classes = jvmStats.getClasses();
        buf.append("\"classes\":{");
        append(buf, "loaded", () -> classes.getLoadedClassCount()).append(',');
        append(buf, "total_loaded", () -> classes.getTotalLoadedClassCount()).append(',');
        append(buf, "unloaded", () -> classes.getUnloadedClassCount());
        buf.append("},");
        append(buf, "uptime", () -> jvmStats.getUptime().getMillis());
        buf.append("},");
    }

    private void appendProcessStats(final StringBuilder buf) {
        buf.append("\"process\":{");
        final ProcessProbe processProbe = ProcessProbe.getInstance();
        buf.append("\"file_descriptor\":{");
        append(buf, "open", () -> processProbe.getOpenFileDescriptorCount()).append(',');
        append(buf, "max", () -> processProbe.getMaxFileDescriptorCount());
        buf.append("},");
        buf.append("\"cpu\":{");
        append(buf, "percent", () -> processProbe.getProcessCpuPercent()).append(',');
        append(buf, "total", () -> processProbe.getProcessCpuTotalTime());
        buf.append("},");
        buf.append("\"virtual_memory\":{");
        append(buf, "total", () -> processProbe.getTotalVirtualMemorySize());
        buf.append('}');
        buf.append("},");
    }

    private void appendOsStats(final StringBuilder buf) {
        buf.append("\"os\":{");
        final OsProbe osProbe = OsProbe.getInstance();
        buf.append("\"memory\":{");
        buf.append("\"physical\":{");
        append(buf, "free", () -> osProbe.getFreePhysicalMemorySize()).append(',');
        append(buf, "total", () -> osProbe.getTotalPhysicalMemorySize());
        buf.append("},");
        buf.append("\"swap_space\":{");
        append(buf, "free", () -> osProbe.getFreeSwapSpaceSize()).append(',');
        append(buf, "total", () -> osProbe.getTotalSwapSpaceSize());
        buf.append('}');
        buf.append("},");
        buf.append("\"cpu\":{");
        append(buf, "percent", () -> osProbe.getSystemCpuPercent());
        final OsStats osStats = osProbe.osStats();
        buf.append("},");
        append(buf, "load_averages", () -> osStats.getCpu().getLoadAverage());
        buf.append("},");
    }

    private void appendFesenStats(final StringBuilder buf) {
        String stats = null;
        try {
            final SearchEngineClient esClient = ComponentUtil.getSearchEngineClient();
            final NodesStatsResponse response = esClient.admin().cluster().prepareNodesStats().all().execute().actionGet(10000L);
            final XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            builder.endObject();
            builder.flush();
            try (OutputStream out = builder.getOutputStream()) {
                stats = ((ByteArrayOutputStream) out).toString(Constants.UTF_8);
            }
        } catch (final Exception e) {
            appendException(buf, e).append(',');
        }
        buf.append("\"search_engine\":").append(stats).append(',');
    }
}
