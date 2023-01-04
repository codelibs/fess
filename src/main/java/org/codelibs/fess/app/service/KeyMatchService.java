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

import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.KeyMatchPager;
import org.codelibs.fess.es.config.cbean.KeyMatchCB;
import org.codelibs.fess.es.config.exbhv.KeyMatchBhv;
import org.codelibs.fess.es.config.exentity.KeyMatch;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class KeyMatchService extends FessAppService {

    @Resource
    protected KeyMatchBhv keyMatchBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<KeyMatch> getKeyMatchList(final KeyMatchPager keyMatchPager) {

        final PagingResultBean<KeyMatch> keyMatchList = keyMatchBhv.selectPage(cb -> {
            cb.paging(keyMatchPager.getPageSize(), keyMatchPager.getCurrentPageNumber());
            setupListCondition(cb, keyMatchPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(keyMatchList, keyMatchPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        keyMatchPager.setPageNumberList(keyMatchList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return keyMatchList;
    }

    public OptionalEntity<KeyMatch> getKeyMatch(final String id) {
        return keyMatchBhv.selectByPK(id);
    }

    public void store(final KeyMatch keyMatch) {

        keyMatchBhv.insertOrUpdate(keyMatch, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public void delete(final KeyMatch keyMatch) {

        keyMatchBhv.delete(keyMatch, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    protected void setupListCondition(final KeyMatchCB cb, final KeyMatchPager keyMatchPager) {
        if (StringUtil.isNotBlank(keyMatchPager.term)) {
            cb.query().setTerm_Wildcard(wrapQuery(keyMatchPager.term));
        }
        if (StringUtil.isNotBlank(keyMatchPager.query)) {
            cb.query().setQuery_Wildcard(wrapQuery(keyMatchPager.query));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Term_Asc();

        // search

    }

}
