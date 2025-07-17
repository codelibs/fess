/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.BoostDocPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.BoostDocumentRuleCB;
import org.codelibs.fess.opensearch.config.exbhv.BoostDocumentRuleBhv;
import org.codelibs.fess.opensearch.config.exentity.BoostDocumentRule;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for boost document rule management operations.
 * Provides CRUD operations for boost document rules.
 */
public class BoostDocumentRuleService extends FessAppService {
    /**
     * Default constructor for BoostDocumentRuleService.
     */
    public BoostDocumentRuleService() {
        super();
    }

    /** Database behavior for boost document rule operations. */
    @Resource
    protected BoostDocumentRuleBhv boostDocumentRuleBhv;

    /** Fess configuration. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Gets a paginated list of boost document rules.
     * @param boostDocumentRulePager The pager with search criteria and pagination settings.
     * @return List of boost document rules matching the criteria.
     */
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

    /**
     * Gets a boost document rule by its ID.
     * @param id The boost document rule ID.
     * @return Optional entity containing the boost document rule if found.
     */
    public OptionalEntity<BoostDocumentRule> getBoostDocumentRule(final String id) {
        return boostDocumentRuleBhv.selectByPK(id);
    }

    /**
     * Stores (inserts or updates) a boost document rule.
     * @param boostDocumentRule The boost document rule to store.
     */
    public void store(final BoostDocumentRule boostDocumentRule) {

        boostDocumentRuleBhv.insertOrUpdate(boostDocumentRule, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    /**
     * Deletes a boost document rule.
     * @param boostDocumentRule The boost document rule to delete.
     */
    public void delete(final BoostDocumentRule boostDocumentRule) {

        boostDocumentRuleBhv.delete(boostDocumentRule, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    /**
     * Sets up search conditions for boost document rule list queries.
     * @param cb The condition bean for the query.
     * @param boostDocumentRulePager The pager containing search criteria.
     */
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