/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EsUtil {

    private static final Logger logger = LoggerFactory.getLogger(EsUtil.class);

    private EsUtil() {
    }

    public static OutputStream getXContentOutputStream(final ToXContent xContent, final XContentType xContentType) {
        try (final XContentBuilder builder = xContent.toXContent(XContentFactory.contentBuilder(xContentType), ToXContent.EMPTY_PARAMS)) {
            builder.flush();
            return builder.getOutputStream();
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to print the output.", e);
            }
            return new ByteArrayOutputStream();
        }
    }
}
