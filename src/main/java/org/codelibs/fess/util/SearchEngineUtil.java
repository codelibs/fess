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
package org.codelibs.fess.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.lastaflute.di.exception.IORuntimeException;
import org.opensearch.core.xcontent.MediaType;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentHelper;
import org.opensearch.search.SearchHit;

/**
 * Utility class for search engine operations and content formatting.
 * Provides helper methods for working with XContent builders, scrolling through search results,
 * and converting XContent objects to different output formats.
 */
public final class SearchEngineUtil {

    private static final Logger logger = LogManager.getLogger(SearchEngineUtil.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SearchEngineUtil() {
    }

    /**
     * Creates an OutputStream from an XContentBuilder using the provided callback function.
     *
     * @param func the callback function to build XContent
     * @param mediaType the media type for the content builder
     * @return an OutputStream containing the built content, or an empty ByteArrayOutputStream if an error occurs
     */
    public static OutputStream getXContentBuilderOutputStream(final XContentBuilderCallback func, final MediaType mediaType) {
        try (final XContentBuilder builder = func.apply(mediaType.contentBuilder(), ToXContent.EMPTY_PARAMS)) {
            builder.flush();
            return builder.getOutputStream();
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to print the output.", e);
            }
            return new ByteArrayOutputStream();
        }
    }

    /**
     * Creates an OutputStream from a ToXContent object with the specified media type.
     *
     * @param xContent the content object to convert
     * @param mediaType the media type for the output
     * @return an OutputStream containing the converted content
     */
    public static OutputStream getXContentOutputStream(final ToXContent xContent, final MediaType mediaType) {
        return getXContentBuilderOutputStream((builder, params) -> xContent.toXContent(builder, params), mediaType);
    }

    /**
     * Scrolls through all documents in the specified index and applies the callback function to each hit.
     *
     * @param index the name of the index to scroll through
     * @param callback the function to apply to each search hit, returning true to continue or false to stop
     * @return the number of documents processed
     */
    public static long scroll(final String index, final Function<SearchHit, Boolean> callback) {
        final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
        return client.<SearchHit> scrollSearch(index, searchRequestBuilder -> true, (searchResponse, hit) -> hit,
                hit -> callback.apply(hit));
    }

    /**
     * Converts a ToXContent object to its string representation using the specified media type.
     *
     * @param xContent the content object to convert
     * @param mediaType the media type for the conversion
     * @return the string representation of the content
     * @throws IORuntimeException if an IO error occurs during conversion
     */
    public static String getXContentString(final ToXContent xContent, final MediaType mediaType) {
        try {
            return XContentHelper.toXContent(xContent, mediaType, ToXContent.EMPTY_PARAMS, false).utf8ToString();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Functional interface for building XContent with custom logic.
     * Allows clients to provide custom content building implementations.
     */
    public interface XContentBuilderCallback {
        /**
         * Applies custom logic to build XContent using the provided builder and parameters.
         *
         * @param builder the XContentBuilder to use for building content
         * @param params the parameters to use during content building
         * @return the modified XContentBuilder
         * @throws IOException if an IO error occurs during building
         */
        XContentBuilder apply(XContentBuilder builder, ToXContent.Params params) throws IOException;
    }

}
