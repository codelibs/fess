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

package org.codelibs.fess.app.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.BoostDocumentRulePager;
import org.codelibs.fess.es.cbean.BoostDocumentRuleCB;
import org.codelibs.fess.es.exbhv.BoostDocumentRuleBhv;
import org.codelibs.fess.es.exentity.BoostDocumentRule;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class BoostDocumentRuleService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected BoostDocumentRuleBhv boostDocumentRuleBhv;

    public BoostDocumentRuleService() {
        super();
    }

    public List<BoostDocumentRule> getBoostDocumentRuleList(final BoostDocumentRulePager boostDocumentRulePager) {

        final PagingResultBean<BoostDocumentRule> boostDocumentRuleList = boostDocumentRuleBhv.selectPage(cb -> {
            cb.paging(boostDocumentRulePager.getPageSize(), boostDocumentRulePager.getCurrentPageNumber());
            setupListCondition(cb, boostDocumentRulePager);
        });

        // update pager
        BeanUtil.copyBeanToBean(boostDocumentRuleList, boostDocumentRulePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        boostDocumentRulePager.setPageNumberList(boostDocumentRuleList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return boostDocumentRuleList;
    }

    public OptionalEntity<BoostDocumentRule> getBoostDocumentRule(final String id) {
        return boostDocumentRuleBhv.selectByPK(id);
    }

    public void store(final BoostDocumentRule boostDocumentRule) {
        setupStoreCondition(boostDocumentRule);

        boostDocumentRuleBhv.insertOrUpdate(boostDocumentRule, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final BoostDocumentRule boostDocumentRule) {
        setupDeleteCondition(boostDocumentRule);

        boostDocumentRuleBhv.delete(boostDocumentRule, op -> {
            op.setRefresh(true);
        });

    }

    protected void setupListCondition(final BoostDocumentRuleCB cb, final BoostDocumentRulePager boostDocumentRulePager) {
        if (boostDocumentRulePager.id != null) {
            cb.query().docMeta().setId_Equal(boostDocumentRulePager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    protected void setupEntityCondition(final BoostDocumentRuleCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final BoostDocumentRule boostDocumentRule) {

        // setup condition

    }

    protected void setupDeleteCondition(final BoostDocumentRule boostDocumentRule) {

        // setup condition

    }

    public List<BoostDocumentRule> getAvailableBoostDocumentRuleList() {
        return boostDocumentRuleBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
        });
    }

}