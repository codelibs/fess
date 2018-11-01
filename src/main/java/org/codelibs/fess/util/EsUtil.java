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
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to print the output.", e);
            }
            return new ByteArrayOutputStream();
        }
    }
}
