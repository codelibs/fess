/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import java.util.Arrays;
import java.util.function.Supplier;

import org.apache.commons.text.StringEscapeUtils;
import org.codelibs.core.timer.TimeoutTarget;
import org.elasticsearch.monitor.os.OsProbe;
import org.elasticsearch.monitor.os.OsStats;
import org.elasticsearch.monitor.process.ProcessProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemMonitorTarget implements TimeoutTarget {
    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorTarget.class);

    protected StringBuilder append(final StringBuilder buf, final String key, final Supplier<Object> supplier) {
        buf.append('"').append(key).append("\":");
        try {
            final Object value = supplier.get();
            if (value == null) {
                buf.append("null");
            } else if (value instanceof Long) {
                buf.append(((Long) value).longValue());
            } else if (value instanceof Short) {
                buf.append(((Short) value).shortValue());
            } else if (value instanceof double[]) {
                buf.append(Arrays.toString((double[]) value));
            } else {
                buf.append('"').append(StringEscapeUtils.escapeJson(value.toString())).append('"');
            }
        } catch (final Exception e) {
            buf.append("null");
        }
        return buf;
    }

    @Override
    public void expired() {
        StringBuilder buf = new StringBuilder();

        buf.append("[SYSTEM MONITOR] ");
        buf.append('{');

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
        OsStats osStats = osProbe.osStats();
        buf.append("},");
        append(buf, "load_averages", () -> osStats.getCpu().getLoadAverage());
        buf.append("},");

        buf.append("\"process\":{");
        final ProcessProbe processProbe = ProcessProbe.getInstance();
        buf.append("\"file_descriptor\":{");
        append(buf, "open", () -> processProbe.getOpenFileDescriptorCount()).append(',');
        append(buf, "max", () -> processProbe.getMaxFileDescriptorCount());
        buf.append("},");
        buf.append("\"cpu\":{");
        append(buf, "percent", () -> processProbe.getProcessCpuPercent()).append(',');
        append(buf, "time", () -> processProbe.getProcessCpuTotalTime());
        buf.append("},");
        buf.append("\"virtual_memory\":{");
        append(buf, "total", () -> processProbe.getTotalVirtualMemorySize());
        buf.append('}');
        buf.append("},");

        buf.append("\"jvm\":{");
        final Runtime runtime = Runtime.getRuntime();
        buf.append("\"memory\":{");
        append(buf, "free", () -> runtime.freeMemory()).append(',');
        append(buf, "max", () -> runtime.maxMemory()).append(',');
        append(buf, "total", () -> runtime.totalMemory());
        buf.append('}');
        buf.append('}');
        buf.append('}');

        logger.info(buf.toString());
    }
}
