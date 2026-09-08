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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.function.Function;

import org.codelibs.curl.CurlResponse;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.xcontent.DeprecationHandler;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.XContentParser;

/**
 * Parses search engine responses fetched with curl4j.
 *
 * <p>Replaces {@code org.codelibs.opensearch.runner.net.OpenSearchCurl}, which lived in the
 * opensearch-runner jar the embedded search engine needed. The parsing itself only uses
 * opensearch-x-content, which Fess depends on however the search engine is deployed, so
 * dropping the runner does not cost anything here.</p>
 */
public final class SearchEngineCurl {

    private static final Function<CurlResponse, Map<String, Object>> JSON_PARSER = response -> {
        try (InputStream in = response.getContentAsStream();
                XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
                        DeprecationHandler.THROW_UNSUPPORTED_OPERATION, in)) {
            return parser.map();
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read the search engine response.", e);
        }
    };

    private SearchEngineCurl() {
    }

    /**
     * Returns a parser that turns a JSON response body into a map.
     *
     * @return the parser
     */
    public static Function<CurlResponse, Map<String, Object>> jsonParser() {
        return JSON_PARSER;
    }
}
