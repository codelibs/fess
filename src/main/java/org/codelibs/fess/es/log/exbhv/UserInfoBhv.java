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
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.es.log.bsbhv.BsUserInfoBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.util.DfTypeUtil;

/**
 * @author FreeGen
 */
public class UserInfoBhv extends BsUserInfoBhv {
    private static final Logger logger = LogManager.getLogger(UserInfoBhv.class);

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
}
