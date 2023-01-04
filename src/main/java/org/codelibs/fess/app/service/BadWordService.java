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
package org.codelibs.fess.app.service;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.BadWordPager;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.es.config.cbean.BadWordCB;
import org.codelibs.fess.es.config.exbhv.BadWordBhv;
import org.codelibs.fess.es.config.exentity.BadWord;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;

public class BadWordService {

    private static final String DELETE_PREFIX = "--";

    private static final Logger logger = LogManager.getLogger(BadWordService.class);

    @Resource
    protected BadWordBhv badWordBhv;

    @Resource
    protected SearchEngineClient searchEngineClient;

    @Resource
    protected FessConfig fessConfig;

    public List<BadWord> getBadWordList(final BadWordPager badWordPager) {

        final PagingResultBean<BadWord> badWordList = badWordBhv.selectPage(cb -> {
            cb.paging(badWordPager.getPageSize(), badWordPager.getCurrentPageNumber());
            setupListCondition(cb, badWordPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(badWordList, badWordPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        badWordPager.setPageNumberList(
                badWordList.pageRange(op -> op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger())).createPageNumberList());

        return badWordList;
    }

    public OptionalEntity<BadWord> getBadWord(final String id) {
        return badWordBhv.selectByPK(id);
    }

    public void store(final BadWord badWord) {

        badWordBhv.insertOrUpdate(badWord, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    public void delete(final BadWord badWord) {

        badWordBhv.delete(badWord, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    protected void setupListCondition(final BadWordCB cb, final BadWordPager badWordPager) {
        if (badWordPager.id != null) {
            cb.query().docMeta().setId_Equal(badWordPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SuggestWord_Asc();

        // search

    }

    public void importCsv(final Reader reader) {
        @SuppressWarnings("resource")
        final CsvReader csvReader = new CsvReader(reader, new CsvConfig());
        try {
            List<String> list;
            csvReader.readValues(); // ignore header
            while ((list = csvReader.readValues()) != null) {
                String targetWord = getValue(list, 0);
                if (StringUtil.isBlank(targetWord)) {
                    // skip
                    continue;
                }
                try {
                    boolean isDelete = false;
                    if (targetWord.startsWith(DELETE_PREFIX)) {
                        isDelete = true;
                        targetWord = targetWord.substring(2);
                    }
                    final String target = targetWord;
                    BadWord badWord = badWordBhv.selectEntity(cb -> cb.query().setSuggestWord_Equal(target)).orElse(null);//TODO
                    final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
                    if (isDelete) {
                        badWordBhv.delete(badWord);
                    } else if (badWord == null) {
                        badWord = new BadWord();
                        badWord.setSuggestWord(targetWord);
                        badWord.setCreatedBy(Constants.SYSTEM_USER);
                        badWord.setCreatedTime(now);
                        badWordBhv.insert(badWord);
                    } else {
                        badWord.setUpdatedBy(Constants.SYSTEM_USER);
                        badWord.setUpdatedTime(now);
                        badWordBhv.update(badWord);
                    }
                } catch (final Exception e) {
                    logger.warn("Failed to read a sugget elevate word: {}", list, e);
                }
            }
            searchEngineClient.refresh("_all"); // TODO replace _all
        } catch (final IOException e) {
            logger.warn("Failed to read a sugget elevate word.", e);
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

            badWordBhv.selectCursor(cb -> cb.query().matchAll(), new EntityRowHandler<BadWord>() {
                @Override
                public void handle(final BadWord entity) {
                    final List<String> list = new ArrayList<>();
                    addToList(list, entity.getSuggestWord());
                    try {
                        csvWriter.writeValues(list);
                    } catch (final IOException e) {
                        logger.warn("Failed to write a sugget bad word: {}", entity, e);
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
            logger.warn("Failed to write a sugget bad word.", e);
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
