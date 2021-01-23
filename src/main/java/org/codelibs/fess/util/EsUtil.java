/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fesen.common.xcontent.ToXContent;
import org.codelibs.fesen.common.xcontent.XContentBuilder;
import org.codelibs.fesen.common.xcontent.XContentFactory;
import org.codelibs.fesen.common.xcontent.XContentType;

public final class EsUtil {

    private static final Logger logger = LogManager.getLogger(EsUtil.class);

    private EsUtil() {
    }

    public static OutputStream getXContentBuilderOutputStream(final XContentBuilderCallback func, final XContentType xContentType) {
        try (final XContentBuilder builder = func.apply(XContentFactory.contentBuilder(xContentType), ToXContent.EMPTY_PARAMS)) {
            builder.flush();
            return builder.getOutputStream();
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to print the output.", e);
            }
            return new ByteArrayOutputStream();
        }
    }

    public static OutputStream getXContentOutputStream(final ToXContent xContent, final XContentType xContentType) {
        return getXContentBuilderOutputStream((builder, params) -> xContent.toXContent(builder, params), xContentType);
    }

    public interface XContentBuilderCallback {
        XContentBuilder apply(XContentBuilder builder, ToXContent.Params params) throws IOException;
    }
}
