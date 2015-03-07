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
import org.codelibs.fess.db.cbean.SuggestElevateWordCB;
import org.codelibs.fess.db.exbhv.SuggestElevateWordBhv;
import org.codelibs.fess.db.exentity.SuggestElevateWord;
import org.codelibs.fess.pager.SuggestElevateWordPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsSuggestElevateWordService {

    @Resource
    protected SuggestElevateWordBhv suggestElevateWordBhv;

    public BsSuggestElevateWordService() {
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
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
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
            cb.query().setId_Equal(Long.parseLong(suggestElevateWordPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final SuggestElevateWordCB cb, final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final SuggestElevateWord suggestElevateWord) {
    }

    protected void setupDeleteCondition(final SuggestElevateWord suggestElevateWord) {
    }
}