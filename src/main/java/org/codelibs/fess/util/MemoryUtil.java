/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.util;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codelibs.core.lang.StringUtil;

public final class MemoryUtil {
    private MemoryUtil() {
    }

    public static String getMemoryUsageLog() {
        final Runtime runtime = Runtime.getRuntime();
        final long freeBytes = runtime.freeMemory();
        final long maxBytes = runtime.maxMemory();
        final long totalBytes = runtime.totalMemory();
        final long usedBytes = totalBytes - freeBytes;
        return "Mem:{used " + byteCountToDisplaySize(usedBytes) + ", heap " + byteCountToDisplaySize(totalBytes) + ", max "
                + byteCountToDisplaySize(maxBytes) + "}";
    }

    public static String byteCountToDisplaySize(final long size) {
        return FileUtils.byteCountToDisplaySize(size).replace(" ", StringUtil.EMPTY);
    }

    public static long getUsedMemory() {
        final Runtime runtime = Runtime.getRuntime();
        final long freeBytes = runtime.freeMemory();
        final long totalBytes = runtime.totalMemory();
        return totalBytes - freeBytes;
    }

    public static long sizeOf(final Object obj) {
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof String) {
            return ((String) obj).length() + 56L;
        }
        if (obj instanceof Number) {
            return 24L;
        }
        if (obj instanceof Date) {
            return 32L;
        }
        if (obj instanceof LocalDateTime) {
            return 80L;
        }
        if (obj instanceof ZonedDateTime) {
            return 2128L;
        }
        if (obj instanceof Object[]) {
            long size = 0;
            for (final Object value : (Object[]) obj) {
                size += sizeOf(value);
            }
            return size;
        }
        if (obj instanceof Collection<?>) {
            long size = 0;
            for (final Object value : (Collection<?>) obj) {
                size += sizeOf(value);
            }
            return size;
        }
        if (obj instanceof Map<?, ?>) {
            long size = 0;
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                size += sizeOf(entry.getKey());
                size += sizeOf(entry.getValue());
            }
            return size;
        }
        return 16L;
    }
}
