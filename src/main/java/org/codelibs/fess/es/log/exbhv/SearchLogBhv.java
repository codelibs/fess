/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.codelibs.fess.es.log.bsbhv.BsSearchLogBhv;
import org.dbflute.util.DfTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FreeGen
 */
public class SearchLogBhv extends BsSearchLogBhv {
    private static final Logger logger = LoggerFactory.getLogger(SearchLogBhv.class);

    @Override
    protected LocalDateTime toLocalDateTime(Object value) {
        if (value != null) {
            try {
                Instant instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value.toString()));
                LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                return date;
            } catch (final DateTimeParseException e) {
                logger.debug("Invalid date format: " + value, e);
            }
        }
        return DfTypeUtil.toLocalDateTime(value);
    }

}
