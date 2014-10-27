/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.service;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.sf.fess.crud.service.BsSuggestBadWordService;
import jp.sf.fess.db.cbean.SuggestBadWordCB;
import jp.sf.fess.db.exentity.SuggestBadWord;
import jp.sf.fess.pager.SuggestBadWordPager;
import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvReader;
import jp.sf.orangesignal.csv.CsvWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.StringUtil;
import org.seasar.dbflute.cbean.EntityRowHandler;

public class SuggestBadWordService extends BsSuggestBadWordService implements
        Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(SuggestBadWordService.class);

    @Override
    protected void setupListCondition(final SuggestBadWordCB cb,
            final SuggestBadWordPager suggestBadWordPager) {
        super.setupListCondition(cb, suggestBadWordPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SuggestWord_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final SuggestBadWordCB cb,
            final Map<String, String> keys) {
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
                try {
                    final SuggestBadWordCB cb = new SuggestBadWordCB();
                    cb.query().setSuggestWord_Equal(strip(list.get(0)));
                    SuggestBadWord suggestBadWord = suggestBadWordBhv
                            .selectEntity(cb);
                    if (suggestBadWord == null) {
                        suggestBadWord = new SuggestBadWord();
                        suggestBadWord.setSuggestWord(strip(list.get(0)));
                        suggestBadWord.setTargetRole(strip(list.get(1)));
                        suggestBadWord.setTargetLabel(strip(list.get(2)));
                        suggestBadWord.setCreatedBy("system");
                        suggestBadWord.setCreatedTime(new Timestamp(System
                                .currentTimeMillis()));
                        suggestBadWordBhv.insert(suggestBadWord);
                    } else if (list.get(1).equals("\"\"")
                            && list.get(2).equals("\"\"")) {
                        suggestBadWord.setDeletedBy("system");
                        suggestBadWord.setDeletedTime(new Timestamp(System
                                .currentTimeMillis()));
                        suggestBadWordBhv.update(suggestBadWord);
                    } else {
                        suggestBadWord.setTargetRole(strip(list.get(1)));
                        suggestBadWord.setTargetLabel(strip(list.get(2)));
                        suggestBadWord.setUpdatedBy("system");
                        suggestBadWord.setUpdatedTime(new Timestamp(System
                                .currentTimeMillis()));
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
        final SuggestBadWordCB cb = new SuggestBadWordCB();
        cb.query().setDeletedBy_IsNull();
        try {
            final List<String> list = new ArrayList<String>();
            list.add("SuggestWord");
            list.add("Role");
            list.add("Label");
            csvWriter.writeValues(list);
            suggestBadWordBhv.selectCursor(cb,
                    new EntityRowHandler<SuggestBadWord>() {
                        @Override
                        public void handle(final SuggestBadWord entity) {
                            final List<String> list = new ArrayList<String>();
                            addToList(list, entity.getSuggestWord());
                            addToList(list, entity.getTargetRole());
                            addToList(list, entity.getTargetLabel());
                            try {
                                csvWriter.writeValues(list);
                            } catch (final IOException e) {
                                log.warn("Failed to write a sugget bad word: "
                                        + entity, e);
                            }
                        }

                        private void addToList(final List<String> list,
                                final Object value) {
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

    private static String strip(final String item) {
        return item.substring(1, item.length() - 1);
    }
}
