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
package org.codelibs.fess.es.log.exbhv;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.es.log.bsbhv.BsSearchLogBhv;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.util.DfTypeUtil;

/**
 * @author FreeGen
 */
public class SearchLogBhv extends BsSearchLogBhv {
    private static final Logger logger = LogManager.getLogger(SearchLogBhv.class);

    private String indexName = null;

    @Override
    protected String asEsIndex() {
        if (indexName == null) {
            final String name = ComponentUtil.getFessConfig().getIndexLogIndex();
            indexName = super.asEsIndex().replaceFirst(Pattern.quote("fess_log"), name);
        }
        return indexName;
    }

    @Override
    protected LocalDateTime toLocalDateTime(final Object value) {
        if (value != null) {
            try {
                final Instant instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value.toString()));
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            } catch (final DateTimeParseException e) {
                logger.debug("Invalid date format: {}", value, e);
            }
        }
        return DfTypeUtil.toLocalDateTime(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <RESULT extends SearchLog> RESULT createEntity(final Map<String, Object> source, final Class<? extends RESULT> entityType) {
        try {
            final RESULT result = super.createEntity(source, entityType);
            final Object searchFieldObj = source.get("searchField");
            if (searchFieldObj instanceof Map) {
                ((Map<String, ?>) searchFieldObj).entrySet().stream().forEach(e -> {
                    if (e.getValue() instanceof String[]) {
                        final String[] values = (String[]) e.getValue();
                        for (final String v : values) {
                            result.getSearchFieldLogList().add(new Pair<>(e.getKey(), v));
                        }
                    } else if (e.getValue() instanceof List) {
                        final List<String> values = (List<String>) e.getValue();
                        for (final String v : values) {
                            result.getSearchFieldLogList().add(new Pair<>(e.getKey(), v));
                        }
                    } else if (e.getValue() != null) {
                        result.getSearchFieldLogList().add(new Pair<>(e.getKey(), e.getValue().toString()));
                    }
                });
            }
            final Object headersObj = source.get("headers");
            if (headersObj instanceof Map) {
                ((Map<String, ?>) headersObj).entrySet().stream().forEach(e -> {
                    if (e.getValue() instanceof String[]) {
                        final String[] values = (String[]) e.getValue();
                        for (final String v : values) {
                            result.getRequestHeaderList().add(new Pair<>(e.getKey(), v));
                        }
                    } else if (e.getValue() instanceof List) {
                        final List<String> values = (List<String>) e.getValue();
                        for (final String v : values) {
                            result.getRequestHeaderList().add(new Pair<>(e.getKey(), v));
                        }
                    } else if (e.getValue() != null) {
                        result.getRequestHeaderList().add(new Pair<>(e.getKey(), e.getValue().toString()));
                    }
                });
            }
            return result;
        } catch (final Exception e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

}
