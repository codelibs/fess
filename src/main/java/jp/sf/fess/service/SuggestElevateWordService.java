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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.sf.fess.crud.service.BsSuggestElevateWordService;
import jp.sf.fess.db.cbean.SuggestElevateWordCB;
import jp.sf.fess.db.exentity.SuggestElevateWord;
import jp.sf.fess.pager.SuggestElevateWordPager;
import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvReader;
import jp.sf.orangesignal.csv.CsvWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.StringUtil;
import org.seasar.dbflute.cbean.EntityRowHandler;

public class SuggestElevateWordService extends BsSuggestElevateWordService
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(SuggestElevateWordService.class);

    @Override
    protected void setupListCondition(final SuggestElevateWordCB cb,
            final SuggestElevateWordPager suggestElevateWordPager) {
        super.setupListCondition(cb, suggestElevateWordPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SuggestWord_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final SuggestElevateWordCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(
            final SuggestElevateWord suggestElevateWord) {
        super.setupStoreCondition(suggestElevateWord);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(
            final SuggestElevateWord suggestElevateWord) {
        super.setupDeleteCondition(suggestElevateWord);

        // setup condition

    }

    public void importCsv(final Reader reader) {
        final CsvReader csvReader = new CsvReader(reader, new CsvConfig());
        try {
            List<String> list;
            csvReader.readValues(); // ignore header
            while ((list = csvReader.readValues()) != null) {
                final String suggestWord = getValue(list, 0);
                if (StringUtil.isBlank(suggestWord)) {
                    // skip
                    continue;
                }
                try {
                    final SuggestElevateWordCB cb = new SuggestElevateWordCB();
                    final String role = getValue(list, 2);
                    final String label = getValue(list, 3);
                    cb.query().setSuggestWord_Equal(suggestWord);
                    if (StringUtil.isNotBlank(role)) {
                        cb.query().setTargetRole_Equal(role);
                    }
                    if (StringUtil.isNotBlank(label)) {
                        cb.query().setTargetLabel_Equal(label);
                    }
                    SuggestElevateWord suggestElevateWord = suggestElevateWordBhv
                            .selectEntity(cb);
                    final String reading = getValue(list, 1);
                    final String boost = getValue(list, 4);
                    if (suggestElevateWord == null) {
                        suggestElevateWord = new SuggestElevateWord();
                        suggestElevateWord.setSuggestWord(suggestWord);
                        suggestElevateWord.setReading(reading);
                        suggestElevateWord.setTargetRole(role);
                        suggestElevateWord.setTargetLabel(label);
                        suggestElevateWord
                                .setBoost(StringUtil.isBlank(boost) ? BigDecimal.ONE
                                        : new BigDecimal(boost));
                        suggestElevateWord.setCreatedBy("system");
                        suggestElevateWord.setCreatedTime(new Timestamp(System
                                .currentTimeMillis()));
                        suggestElevateWordBhv.insert(suggestElevateWord);
                    } else if (StringUtil.isBlank(reading)
                            && StringUtil.isBlank(boost)) {
                        suggestElevateWord.setDeletedBy("system");
                        suggestElevateWord.setDeletedTime(new Timestamp(System
                                .currentTimeMillis()));
                        suggestElevateWordBhv.update(suggestElevateWord);
                    } else {
                        suggestElevateWord.setReading(reading);
                        suggestElevateWord
                                .setBoost(StringUtil.isBlank(boost) ? BigDecimal.ONE
                                        : new BigDecimal(boost));
                        suggestElevateWord.setUpdatedBy("system");
                        suggestElevateWord.setUpdatedTime(new Timestamp(System
                                .currentTimeMillis()));
                        suggestElevateWordBhv.update(suggestElevateWord);
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
            final List<String> list = new ArrayList<String>();
            list.add("SuggestWord");
            list.add("Reading");
            list.add("Role");
            list.add("Label");
            list.add("Boost");
            csvWriter.writeValues(list);

            final SuggestElevateWordCB cb = new SuggestElevateWordCB();
            cb.query().setDeletedBy_IsNull();
            suggestElevateWordBhv.selectCursor(cb,
                    new EntityRowHandler<SuggestElevateWord>() {
                        @Override
                        public void handle(final SuggestElevateWord entity) {
                            final List<String> list = new ArrayList<String>();
                            addToList(list, entity.getSuggestWord());
                            addToList(list, entity.getReading());
                            addToList(list, entity.getTargetRole());
                            addToList(list, entity.getTargetLabel());
                            addToList(list, entity.getBoost());
                            try {
                                csvWriter.writeValues(list);
                            } catch (final IOException e) {
                                log.warn(
                                        "Failed to write a sugget elevate word: "
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
            log.warn("Failed to write a sugget elevate word.", e);
        }
    }

    static String getValue(final List<String> list, final int index) {
        if (index >= list.size()) {
            return StringUtil.EMPTY;
        }
        String item = list.get(index).trim();
        if (StringUtil.isBlank(item)) {
            return StringUtil.EMPTY;
        }
        if (item.length() > 1 && item.charAt(0) == '"'
                && item.charAt(item.length() - 1) == '"') {
            item = item.substring(1, item.length() - 1);
        }
        return item;
    }

}
