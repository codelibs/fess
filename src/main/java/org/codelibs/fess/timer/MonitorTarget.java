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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.function.Supplier;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.text.StringEscapeUtils;
import org.codelibs.core.timer.TimeoutTarget;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.ComponentUtil;

public abstract class MonitorTarget implements TimeoutTarget {

    protected StringBuilder append(final StringBuilder buf, final String key, final Supplier<Object> supplier) {
        final StringBuilder tempBuf = new StringBuilder();
        tempBuf.append('"').append(key).append("\":");
        try {
            final Object value = supplier.get();
            if (value == null) {
                tempBuf.append("null");
            } else if ((value instanceof Integer) || (value instanceof Long)) {
                tempBuf.append((value));
            } else if (value instanceof Short) {
                tempBuf.append(((Short) value).shortValue());
            } else if (value instanceof double[]) {
                tempBuf.append(Arrays.toString((double[]) value));
            } else {
                tempBuf.append('"').append(StringEscapeUtils.escapeJson(value.toString())).append('"');
            }
        } catch (final Exception e) {
            tempBuf.append("null");
        }
        buf.append(tempBuf.toString());
        return buf;
    }

    protected StringBuilder appendTimestamp(final StringBuilder buf) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        append(buf, "timestamp", () -> FessFunctions.formatDate(systemHelper.getCurrentTime()));
        return buf;
    }

    protected StringBuilder appendException(final StringBuilder buf, final Exception exception) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintWriter writer = new PrintWriter(baos, false, Constants.CHARSET_UTF_8)) {
            exception.printStackTrace(writer);
            writer.flush();
            append(buf, "exception", () -> StringEscapeUtils.escapeJson(new String(baos.toByteArray(), Constants.CHARSET_UTF_8)));
        } catch (final IOException e) {
            append(buf, "exception", () -> StringEscapeUtils.escapeJson(e.getMessage()));
        }
        return buf;
    }
}