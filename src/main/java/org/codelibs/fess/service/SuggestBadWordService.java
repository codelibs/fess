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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvReader;
import jp.sf.orangesignal.csv.CsvWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.crud.service.BsSuggestBadWordService;
import org.codelibs.fess.db.cbean.SuggestBadWordCB;
import org.codelibs.fess.db.exentity.SuggestBadWord;
import org.codelibs.fess.pager.SuggestBadWordPager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;

public class SuggestBadWordService extends BsSuggestBadWordService implements Serializable {

    private static final String DELETE_PREFIX = "--";

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(SuggestBadWordService.class);

    @Override
    protected void setupListCondition(final SuggestBadWordCB cb, final SuggestBadWordPager suggestBadWordPager) {
        super.setupListCondition(cb, suggestBadWordPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SuggestWord_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final SuggestBadWordCB cb, final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(final SuggestBadWord suggestBadWord) {
        super.setupStoreCondition(suggestBadWord);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final SuggestBadWord suggestBadWord) {
        super.setupDeleteCondition(suggestBadWord);

        // setup condition

    }

    public void importCsv(final Reader reader) {
        final CsvReader csvReader = new CsvReader(reader, new CsvConfig());
        try {
            List<String> list;
            csvReader.readValues(); // ignore header
            while ((list = csvReader.readValues()) != null) {
                String badWord = getValue(list, 0);
                if (StringUtil.isBlank(badWord)) {
                    // skip
                    continue;
                }
                try {
                    boolean isDelete = false;
                    if (badWord.startsWith(DELETE_PREFIX)) {
                        isDelete = true;
                        badWord = badWord.substring(2);
                    }
                    final String target = badWord;
                    SuggestBadWord suggestBadWord = suggestBadWordBhv.selectEntity(cb -> {
                        cb.query().setSuggestWord_Equal(target);
                    }).orElse(null);//TODO
                    final LocalDateTime now = ComponentUtil.getSystemHelper().getCurrentTime();
                    if (isDelete) {
                        suggestBadWord.setDeletedBy("system");
                        suggestBadWord.setDeletedTime(now);
                        suggestBadWordBhv.update(suggestBadWord);
                    } else if (suggestBadWord == null) {
                        suggestBadWord = new SuggestBadWord();
                        suggestBadWord.setSuggestWord(badWord);
                        suggestBadWord.setCreatedBy("system");
                        suggestBadWord.setCreatedTime(now);
                        suggestBadWordBhv.insert(suggestBadWord);
                    } else {
                        suggestBadWord.setUpdatedBy("system");
                        suggestBadWord.setUpdatedTime(now);
                        suggestBadWordBhv.update(suggestBadWord);
                    }
                } catch (final Exception e) {
                    log.warn("Failed to read a sugget elevate word: " + list, e);
                }
            }
        } catch (final IOException e) {
            log.warn("Failed to read a sugget elevate word.", e);
        }
    }

    public void exportCsv(final Writer writer) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, cfg);
        try {
            final List<String> list = new ArrayList<>();
            list.add("BadWord");
            csvWriter.writeValues(list);

            suggestBadWordBhv.selectCursor(cb -> {
                cb.query().setDeletedBy_IsNull();
            }, new EntityRowHandler<SuggestBadWord>() {
                @Override
                public void handle(final SuggestBadWord entity) {
                    final List<String> list = new ArrayList<String>();
                    addToList(list, entity.getSuggestWord());
                    try {
                        csvWriter.writeValues(list);
                    } catch (final IOException e) {
                        log.warn("Failed to write a sugget bad word: " + entity, e);
                    }
                }

                private void addToList(final List<String> list, final Object value) {
                    if (value == null) {
                        list.add(StringUtil.EMPTY);
                    } else {
                        list.add(value.toString());
                    }
                }
            });

            csvWriter.flush();
        } catch (final IOException e) {
            log.warn("Failed to write a sugget bad word.", e);
        }
    }

    private static String getValue(final List<String> list, final int index) {
        if (index >= list.size()) {
            return StringUtil.EMPTY;
        }
        String item = list.get(index).trim();
        if (StringUtil.isBlank(item)) {
            return StringUtil.EMPTY;
        }
        if (item.length() > 1 && item.charAt(0) == '"' && item.charAt(item.length() - 1) == '"') {
            item = item.substring(1, item.length() - 1);
        }
        return item;
    }
}
