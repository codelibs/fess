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
import org.codelibs.fess.app.pager.RelatedContentPager;
import org.codelibs.fess.es.config.cbean.RelatedContentCB;
import org.codelibs.fess.es.config.exbhv.RelatedContentBhv;
import org.codelibs.fess.es.config.exentity.RelatedContent;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class RelatedContentService extends FessAppService {

    @Resource
    protected RelatedContentBhv relatedContentBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<RelatedContent> getRelatedContentList(final RelatedContentPager relatedContentPager) {

        final PagingResultBean<RelatedContent> relatedContentList = relatedContentBhv.selectPage(cb -> {
            cb.paging(relatedContentPager.getPageSize(), relatedContentPager.getCurrentPageNumber());
            setupListCondition(cb, relatedContentPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(relatedContentList, relatedContentPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        relatedContentPager.setPageNumberList(
                relatedContentList.pageRange(op -> op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger())).createPageNumberList());

        return relatedContentList;
    }

    public OptionalEntity<RelatedContent> getRelatedContent(final String id) {
        return relatedContentBhv.selectByPK(id);
    }

    public void store(final RelatedContent relatedContent) {

        relatedContentBhv.insertOrUpdate(relatedContent, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedContentHelper().update();
    }

    public void delete(final RelatedContent relatedContent) {

        relatedContentBhv.delete(relatedContent, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedContentHelper().update();
    }

    protected void setupListCondition(final RelatedContentCB cb, final RelatedContentPager relatedContentPager) {
        if (StringUtil.isNotBlank(relatedContentPager.term)) {
            cb.query().setTerm_Wildcard(wrapQuery(relatedContentPager.term));
        }
        if (StringUtil.isNotBlank(relatedContentPager.content)) {
            cb.query().setContent_Wildcard(wrapQuery(relatedContentPager.content));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Term_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

    public List<RelatedContent> getAvailableRelatedContentList() {
        return relatedContentBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Term_Asc();
            cb.fetchFirst(fessConfig.getPageDocboostMaxFetchSizeAsInteger());
        });
    }

}