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

package org.codelibs.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.SuggestBadWordCB;
import org.codelibs.fess.db.exbhv.SuggestBadWordBhv;
import org.codelibs.fess.db.exentity.SuggestBadWord;
import org.codelibs.fess.pager.SuggestBadWordPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsSuggestBadWordService {

    @Resource
    protected SuggestBadWordBhv suggestBadWordBhv;

    public BsSuggestBadWordService() {
        super();
    }

    public List<SuggestBadWord> getSuggestBadWordList(final SuggestBadWordPager suggestBadWordPager) {

        final PagingResultBean<SuggestBadWord> suggestBadWordList = suggestBadWordBhv.selectPage(cb -> {
            cb.paging(suggestBadWordPager.getPageSize(), suggestBadWordPager.getCurrentPageNumber());
            setupListCondition(cb, suggestBadWordPager);
        });

        // update pager
        Beans.copy(suggestBadWordList, suggestBadWordPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        suggestBadWordPager.setPageNumberList(suggestBadWordList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return suggestBadWordList;
    }

    public SuggestBadWord getSuggestBadWord(final Map<String, String> keys) {
        final SuggestBadWord suggestBadWord = suggestBadWordBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (suggestBadWord == null) {
            // TODO exception?
            return null;
        }

        return suggestBadWord;
    }

    public void store(final SuggestBadWord suggestBadWord) throws CrudMessageException {
        setupStoreCondition(suggestBadWord);

        suggestBadWordBhv.insertOrUpdate(suggestBadWord);

    }

    public void delete(final SuggestBadWord suggestBadWord) throws CrudMessageException {
        setupDeleteCondition(suggestBadWord);

        suggestBadWordBhv.delete(suggestBadWord);

    }

    protected void setupListCondition(final SuggestBadWordCB cb, final SuggestBadWordPager suggestBadWordPager) {

        if (suggestBadWordPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(suggestBadWordPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final SuggestBadWordCB cb, final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final SuggestBadWord suggestBadWord) {
    }

    protected void setupDeleteCondition(final SuggestBadWord suggestBadWord) {
    }
}