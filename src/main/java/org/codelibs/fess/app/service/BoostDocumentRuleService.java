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
import org.codelibs.fess.app.pager.BoostDocPager;
import org.codelibs.fess.es.config.cbean.BoostDocumentRuleCB;
import org.codelibs.fess.es.config.exbhv.BoostDocumentRuleBhv;
import org.codelibs.fess.es.config.exentity.BoostDocumentRule;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class BoostDocumentRuleService extends FessAppService {

    @Resource
    protected BoostDocumentRuleBhv boostDocumentRuleBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<BoostDocumentRule> getBoostDocumentRuleList(final BoostDocPager boostDocumentRulePager) {

        final PagingResultBean<BoostDocumentRule> boostDocumentRuleList = boostDocumentRuleBhv.selectPage(cb -> {
            cb.paging(boostDocumentRulePager.getPageSize(), boostDocumentRulePager.getCurrentPageNumber());
            setupListCondition(cb, boostDocumentRulePager);
        });

        // update pager
        BeanUtil.copyBeanToBean(boostDocumentRuleList, boostDocumentRulePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        boostDocumentRulePager.setPageNumberList(
                boostDocumentRuleList.pageRange(op -> op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger())).createPageNumberList());

        return boostDocumentRuleList;
    }

    public OptionalEntity<BoostDocumentRule> getBoostDocumentRule(final String id) {
        return boostDocumentRuleBhv.selectByPK(id);
    }

    public void store(final BoostDocumentRule boostDocumentRule) {

        boostDocumentRuleBhv.insertOrUpdate(boostDocumentRule, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    public void delete(final BoostDocumentRule boostDocumentRule) {

        boostDocumentRuleBhv.delete(boostDocumentRule, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    protected void setupListCondition(final BoostDocumentRuleCB cb, final BoostDocPager boostDocumentRulePager) {
        if (StringUtil.isNotBlank(boostDocumentRulePager.urlExpr)) {
            cb.query().setUrlExpr_Wildcard(wrapQuery(boostDocumentRulePager.urlExpr));
        }
        if (StringUtil.isNotBlank(boostDocumentRulePager.boostExpr)) {
            cb.query().setBoostExpr_Wildcard(wrapQuery(boostDocumentRulePager.boostExpr));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

}