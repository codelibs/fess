/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.service;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.db.exbhv.ClickLogBhv;
import org.codelibs.fess.db.exbhv.SearchLogBhv;
import org.codelibs.fess.db.exentity.ClickLog;
import org.codelibs.fess.db.exentity.SearchLog;
import org.dbflute.bhv.readable.EntityRowHandler;

import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvReader;
import jp.sf.orangesignal.csv.CsvWriter;

public class ClickLogService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(ClickLogService.class);

    @Resource
    protected SearchLogBhv searchLogBhv;

    @Resource
    protected ClickLogBhv clickLogBhv;

    public void importCsv(final Reader reader) {
        final CsvReader csvReader = new CsvReader(reader, new CsvConfig());
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
        try {
            List<String> list;
            csvReader.readValues(); // ignore header
            while ((list = csvReader.readValues()) != null) {
                try {
                    final String dateStr = list.get(3);
                    final String userSessionId = list.get(4);
                    final SearchLog searchLog = searchLogBhv.selectEntity(cb -> {
                        cb.query().setRequestedTime_Equal(LocalDateTime.parse(dateStr, formatter));
                        cb.query().setUserSessionId_Equal(userSessionId);
                    }).orElse(null);//TODO
                    if (searchLog != null) {
                        final ClickLog entity = new ClickLog();
                        entity.setId(Long.parseLong(list.get(0)));
                        entity.setSearchId(searchLog.getId());
                        entity.setUrl(list.get(1));
                        entity.setRequestedTime(LocalDateTime.parse(list.get(2), formatter));
                        clickLogBhv.insert(entity);
                    } else {
                        log.warn("The search log is not found: " + list);
                    }
                } catch (final Exception e) {
                    log.warn("Failed to read a click log: " + list, e);
                }
            }
        } catch (final IOException e) {
            log.warn("Failed to read a click log.", e);
        }
    }

    public void exportCsv(final Writer writer) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        final CsvWriter csvWriter = new CsvWriter(writer, cfg);
        try {
            final List<String> list = new ArrayList<String>();
            list.add("SearchId");
            list.add("URL");
            list.add("RequestedTime");
            list.add("QueryRequestedTime");
            list.add("UserSessionId");
            csvWriter.writeValues(list);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            clickLogBhv.selectCursor(cb -> {
                cb.setupSelect_SearchLog();
                cb.specify().specifySearchLog().columnUserSessionId();
                cb.specify().specifySearchLog().columnRequestedTime();
            }, new EntityRowHandler<ClickLog>() {
                @Override
                public void handle(final ClickLog entity) {
                    final List<String> list = new ArrayList<String>();
                    addToList(list, entity.getSearchId());
                    addToList(list, entity.getUrl());
                    addToList(list, entity.getRequestedTime());
                    entity.getSearchLog().ifPresent(searchLog -> {
                        addToList(list, searchLog.getRequestedTime());
                        addToList(list, searchLog.getUserSessionId());
                    });
                    try {
                        csvWriter.writeValues(list);
                    } catch (final IOException e) {
                        log.warn("Failed to write a search log: " + entity, e);
                    }
                }

                private void addToList(final List<String> list, final Object value) {
                    if (value == null) {
                        list.add(StringUtil.EMPTY);
                    } else if (value instanceof LocalDateTime) {
                        list.add(((LocalDateTime) value).format(formatter));
                    } else {
                        list.add(value.toString());
                    }
                }
            });
            csvWriter.flush();
        } catch (final IOException e) {
            log.warn("Failed to write a search log.", e);
        }
    }
}
