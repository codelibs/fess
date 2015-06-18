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

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.client.FessEsClient;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.entity.BoostDocumentRule;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.pager.BoostDocumentRulePager;
import org.dbflute.cbean.result.PagingResultBean;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.seasar.framework.beans.util.Beans;

public class BoostDocumentRuleService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected FieldHelper fieldHelper;

    public List<BoostDocumentRule> getBoostDocumentRuleList(final BoostDocumentRulePager boostDocumentRulePager) {
        final PagingResultBean<BoostDocumentRule> boostDocumentRuleList =
                fessEsClient.search(getIndex(), getType(), searchRequestBuilder -> {
                    if (boostDocumentRulePager.id != null) {
                        searchRequestBuilder.setQuery(QueryBuilders.idsQuery(getType()).addIds(boostDocumentRulePager.id));
                    }
                    searchRequestBuilder.addSort(SortBuilders.fieldSort("sortOrder").order(SortOrder.ASC));
                    searchRequestBuilder.setVersion(true);

                    final int size = boostDocumentRulePager.getPageSize();
                    final int pageNum = boostDocumentRulePager.getCurrentPageNumber();
                    // TODO modify size/pageNum
                        searchRequestBuilder.setFrom(size * (pageNum - 1));
                        searchRequestBuilder.setSize(size);
                        return true;
                    }, (searchRequestBuilder, execTime, searchResponse) -> {
                        return searchResponse.map(response -> {
                            final PagingResultBean<BoostDocumentRule> list = new PagingResultBean<>();
                            list.setTableDbName(getType());
                            final SearchHits searchHits = response.getHits();
                            searchHits.forEach(hit -> {
                                list.add(createEntity(response, hit));
                            });
                            list.setAllRecordCount((int) searchHits.totalHits());
                            list.setPageSize(boostDocumentRulePager.getPageSize());
                            list.setCurrentPageNumber(boostDocumentRulePager.getCurrentPageNumber());
                            return list;
                        }).orElseGet(() -> {
                            final PagingResultBean<BoostDocumentRule> emptyList = new PagingResultBean<>();
                            emptyList.setTableDbName(getType());
                            emptyList.setAllRecordCount(0);
                            emptyList.setPageSize(boostDocumentRulePager.getPageSize());
                            emptyList.setCurrentPageNumber(1);
                            return emptyList;
                        });
                    });

        // update pager
        Beans.copy(boostDocumentRuleList, boostDocumentRulePager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        boostDocumentRulePager.setPageNumberList(boostDocumentRuleList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return boostDocumentRuleList;
    }

    public BoostDocumentRule getBoostDocumentRule(final String id) {
        return fessEsClient.getDocument(getIndex(), getType(), searchRequestBuilder -> {
            searchRequestBuilder.setQuery(QueryBuilders.idsQuery(getType()).addIds(id));
            searchRequestBuilder.setVersion(true);
            return true;
        }, this::createEntity).get();
    }

    protected String getType() {
        return fieldHelper.boostDocumentRuleType;
    }

    protected String getIndex() {
        return fieldHelper.configIndex;
    }

    public void store(final BoostDocumentRule boostDocumentRule) throws CrudMessageException {
        fessEsClient.store(getIndex(), getType(), boostDocumentRule);
    }

    public void delete(final BoostDocumentRule boostDocumentRule) throws CrudMessageException {
        fessEsClient.delete(getIndex(), getType(), boostDocumentRule.getId(), boostDocumentRule.getVersion());
    }

    public List<BoostDocumentRule> getAvailableBoostDocumentRuleList() {
        return fessEsClient.getDocumentList(getIndex(), getType(), searchRequestBuilder -> {
            return true;
        }, this::createEntity);
    }

    protected BoostDocumentRule createEntity(SearchResponse response, SearchHit hit) {
        final BoostDocumentRule boostDocumentRule = BeanUtil.copyMapToNewBean(hit.getSource(), BoostDocumentRule.class);
        boostDocumentRule.setId(hit.getId());
        boostDocumentRule.setVersion(hit.getVersion());
        return boostDocumentRule;
    }

}
