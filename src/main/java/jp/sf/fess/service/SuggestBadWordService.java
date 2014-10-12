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

import java.io.Serializable;
import java.util.Map;

import jp.sf.fess.crud.service.BsSuggestBadWordService;
import jp.sf.fess.db.cbean.SuggestBadWordCB;
import jp.sf.fess.db.exentity.SuggestBadWord;
import jp.sf.fess.pager.SuggestBadWordPager;

public class SuggestBadWordService extends BsSuggestBadWordService implements
        Serializable {

    private static final long serialVersionUID = 1L;

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

}
