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
import org.codelibs.fess.app.pager.RelatedQueryPager;
import org.codelibs.fess.es.config.cbean.RelatedQueryCB;
import org.codelibs.fess.es.config.exbhv.RelatedQueryBhv;
import org.codelibs.fess.es.config.exentity.RelatedQuery;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class RelatedQueryService extends FessAppService {

    @Resource
    protected RelatedQueryBhv relatedQueryBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<RelatedQuery> getRelatedQueryList(final RelatedQueryPager relatedQueryPager) {

        final PagingResultBean<RelatedQuery> relatedQueryList = relatedQueryBhv.selectPage(cb -> {
            cb.paging(relatedQueryPager.getPageSize(), relatedQueryPager.getCurrentPageNumber());
            setupListCondition(cb, relatedQueryPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(relatedQueryList, relatedQueryPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        relatedQueryPager.setPageNumberList(
                relatedQueryList.pageRange(op -> op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger())).createPageNumberList());

        return relatedQueryList;
    }

    public OptionalEntity<RelatedQuery> getRelatedQuery(final String id) {
        return relatedQueryBhv.selectByPK(id);
    }

    public void store(final RelatedQuery relatedQuery) {

        relatedQueryBhv.insertOrUpdate(relatedQuery, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedQueryHelper().update();
    }

    public void delete(final RelatedQuery relatedQuery) {

        relatedQueryBhv.delete(relatedQuery, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedQueryHelper().update();
    }

    protected void setupListCondition(final RelatedQueryCB cb, final RelatedQueryPager relatedQueryPager) {
        if (StringUtil.isNotBlank(relatedQueryPager.term)) {
            cb.query().setTerm_Wildcard(wrapQuery(relatedQueryPager.term));
        }
        if (StringUtil.isNotBlank(relatedQueryPager.queries)) {
            cb.query().setQueries_Wildcard(wrapQuery(relatedQueryPager.queries));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Term_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

}