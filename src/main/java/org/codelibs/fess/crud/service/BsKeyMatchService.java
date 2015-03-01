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
import org.codelibs.fess.db.cbean.KeyMatchCB;
import org.codelibs.fess.db.exbhv.KeyMatchBhv;
import org.codelibs.fess.db.exentity.KeyMatch;
import org.codelibs.fess.pager.KeyMatchPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsKeyMatchService {

    @Resource
    protected KeyMatchBhv keyMatchBhv;

    public BsKeyMatchService() {
        super();
    }

    public List<KeyMatch> getKeyMatchList(final KeyMatchPager keyMatchPager) {

        final PagingResultBean<KeyMatch> keyMatchList = keyMatchBhv
                .selectPage(cb -> {
                    cb.paging(keyMatchPager.getPageSize(),
                            keyMatchPager.getCurrentPageNumber());
                    setupListCondition(cb, keyMatchPager);
                });

        // update pager
        Beans.copy(keyMatchList, keyMatchPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        keyMatchPager.setPageNumberList(keyMatchList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return keyMatchList;
    }

    public KeyMatch getKeyMatch(final Map<String, String> keys) {
        final KeyMatch keyMatch = keyMatchBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (keyMatch == null) {
            // TODO exception?
            return null;
        }

        return keyMatch;
    }

    public void store(final KeyMatch keyMatch) throws CrudMessageException {
        setupStoreCondition(keyMatch);

        keyMatchBhv.insertOrUpdate(keyMatch);

    }

    public void delete(final KeyMatch keyMatch) throws CrudMessageException {
        setupDeleteCondition(keyMatch);

        keyMatchBhv.delete(keyMatch);

    }

    protected void setupListCondition(final KeyMatchCB cb,
            final KeyMatchPager keyMatchPager) {

        if (keyMatchPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(keyMatchPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final KeyMatchCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final KeyMatch keyMatch) {
    }

    protected void setupDeleteCondition(final KeyMatch keyMatch) {
    }
}