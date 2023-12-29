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
package org.codelibs.fess.util;

import static org.apache.commons.io.FileUtils.ONE_EB_BI;
import static org.apache.commons.io.FileUtils.ONE_GB_BI;
import static org.apache.commons.io.FileUtils.ONE_KB_BI;
import static org.apache.commons.io.FileUtils.ONE_MB_BI;
import static org.apache.commons.io.FileUtils.ONE_PB_BI;
import static org.apache.commons.io.FileUtils.ONE_TB_BI;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

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
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }

    private static String byteCountToDisplaySize(final BigInteger size) {
        Objects.requireNonNull(size, "size");
        final String displaySize;

        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = new BigDecimal(size.divide(ONE_PB_BI)).divide(BigDecimal.valueOf(1000)) + "EB";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = new BigDecimal(size.divide(ONE_TB_BI)).divide(BigDecimal.valueOf(1000)) + "PB";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = new BigDecimal(size.divide(ONE_GB_BI)).divide(BigDecimal.valueOf(1000)) + "TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = new BigDecimal(size.divide(ONE_MB_BI)).divide(BigDecimal.valueOf(1000)) + "GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = new BigDecimal(size.divide(ONE_KB_BI)).divide(BigDecimal.valueOf(1000)) + "MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = new BigDecimal(size).divide(BigDecimal.valueOf(1000)) + "KB";
        } else {
            displaySize = size + "bytes";
        }
        return displaySize;
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
