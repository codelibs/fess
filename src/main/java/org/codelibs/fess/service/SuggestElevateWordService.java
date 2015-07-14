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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.es.cbean.SuggestElevateWordCB;
import org.codelibs.fess.es.exbhv.SuggestElevateWordBhv;
import org.codelibs.fess.es.exentity.SuggestElevateWord;
import org.codelibs.fess.pager.SuggestElevateWordPager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvReader;
import jp.sf.orangesignal.csv.CsvWriter;

public class SuggestElevateWordService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(SuggestElevateWordService.class);

    @Resource
    protected SuggestElevateWordBhv suggestElevateWordBhv;

    public SuggestElevateWordService() {
        super();
    }

    public List<SuggestElevateWord> getSuggestElevateWordList(final SuggestElevateWordPager suggestElevateWordPager) {

        final PagingResultBean<SuggestElevateWord> suggestElevateWordList = suggestElevateWordBhv.selectPage(cb -> {
            cb.paging(suggestElevateWordPager.getPageSize(), suggestElevateWordPager.getCurrentPageNumber());
            setupListCondition(cb, suggestElevateWordPager);
        });

        // update pager
        Beans.copy(suggestElevateWordList, suggestElevateWordPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        suggestElevateWordPager.setPageNumberList(suggestElevateWordList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return suggestElevateWordList;
    }

    public SuggestElevateWord getSuggestElevateWord(final Map<String, String> keys) {
        final SuggestElevateWord suggestElevateWord = suggestElevateWordBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (suggestElevateWord == null) {
            // TODO exception?
            return null;
        }

        return suggestElevateWord;
    }

    public void store(final SuggestElevateWord suggestElevateWord) throws CrudMessageException {
        setupStoreCondition(suggestElevateWord);

        suggestElevateWordBhv.insertOrUpdate(suggestElevateWord);

    }

    public void delete(final SuggestElevateWord suggestElevateWord) throws CrudMessageException {
        setupDeleteCondition(suggestElevateWord);

        suggestElevateWordBhv.delete(suggestElevateWord);

    }

    protected void setupListCondition(final SuggestElevateWordCB cb, final SuggestElevateWordPager suggestElevateWordPager) {
        if (suggestElevateWordPager.id != null) {
            cb.query().docMeta().setId_Equal(suggestElevateWordPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SuggestWord_Asc();

        // search

    }

    protected void setupEntityCondition(final SuggestElevateWordCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final SuggestElevateWord suggestElevateWord) {

        // setup condition

    }

    protected void setupDeleteCondition(final SuggestElevateWord suggestElevateWord) {

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
                    final String role = getValue(list, 2);
                    final String label = getValue(list, 3);
                    SuggestElevateWord suggestElevateWord = suggestElevateWordBhv.selectEntity(cb -> {
                        cb.query().setSuggestWord_Equal(suggestWord);
                        if (StringUtil.isNotBlank(role)) {
                            cb.query().setTargetRole_Equal(role);
                        }
                        if (StringUtil.isNotBlank(label)) {
                            cb.query().setTargetLabel_Equal(label);
                        }
                    }).orElse(null);//TODO
                    final String reading = getValue(list, 1);
                    final String boost = getValue(list, 4);
                    final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
                    if (suggestElevateWord == null) {
                        suggestElevateWord = new SuggestElevateWord();
                        suggestElevateWord.setSuggestWord(suggestWord);
                        suggestElevateWord.setReading(reading);
                        suggestElevateWord.setTargetRole(role);
                        suggestElevateWord.setTargetLabel(label);
                        suggestElevateWord.setBoost(StringUtil.isBlank(boost) ? 1.0f : Float.parseFloat(boost));
                        suggestElevateWord.setCreatedBy("system");
                        suggestElevateWord.setCreatedTime(now);
                        suggestElevateWordBhv.insert(suggestElevateWord);
                    } else if (StringUtil.isBlank(reading) && StringUtil.isBlank(boost)) {
                        suggestElevateWordBhv.delete(suggestElevateWord);
                    } else {
                        suggestElevateWord.setReading(reading);
                        suggestElevateWord.setBoost(StringUtil.isBlank(boost) ? 1.0f : Float.parseFloat(boost));
                        suggestElevateWord.setUpdatedBy("system");
                        suggestElevateWord.setUpdatedTime(now);
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

            suggestElevateWordBhv.selectCursor(cb -> {
                cb.query().matchAll();
            }, new EntityRowHandler<SuggestElevateWord>() {
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
                        log.warn("Failed to write a sugget elevate word: " + entity, e);
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
        if (item.length() > 1 && item.charAt(0) == '"' && item.charAt(item.length() - 1) == '"') {
            item = item.substring(1, item.length() - 1);
        }
        return item;
    }

}
