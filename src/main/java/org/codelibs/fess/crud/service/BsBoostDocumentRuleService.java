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
import org.codelibs.fess.db.cbean.BoostDocumentRuleCB;
import org.codelibs.fess.db.exbhv.BoostDocumentRuleBhv;
import org.codelibs.fess.db.exentity.BoostDocumentRule;
import org.codelibs.fess.pager.BoostDocumentRulePager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsBoostDocumentRuleService {

    @Resource
    protected BoostDocumentRuleBhv boostDocumentRuleBhv;

    public BsBoostDocumentRuleService() {
        super();
    }

    public List<BoostDocumentRule> getBoostDocumentRuleList(
            final BoostDocumentRulePager boostDocumentRulePager) {

        final PagingResultBean<BoostDocumentRule> boostDocumentRuleList = boostDocumentRuleBhv
                .selectPage(cb -> {
                    cb.paging(boostDocumentRulePager.getPageSize(),
                            boostDocumentRulePager.getCurrentPageNumber());
                    setupListCondition(cb, boostDocumentRulePager);
                });

        // update pager
        Beans.copy(boostDocumentRuleList, boostDocumentRulePager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        boostDocumentRulePager.setPageNumberList(boostDocumentRuleList
                .pageRange(op -> {
                    op.rangeSize(5);
                }).createPageNumberList());

        return boostDocumentRuleList;
    }

    public BoostDocumentRule getBoostDocumentRule(final Map<String, String> keys) {
        final BoostDocumentRule boostDocumentRule = boostDocumentRuleBhv
                .selectEntity(cb -> {
                    cb.query().setId_Equal(Long.parseLong(keys.get("id")));
                    setupEntityCondition(cb, keys);
                }).orElse(null);//TODO
        if (boostDocumentRule == null) {
            // TODO exception?
            return null;
        }

        return boostDocumentRule;
    }

    public void store(final BoostDocumentRule boostDocumentRule)
            throws CrudMessageException {
        setupStoreCondition(boostDocumentRule);

        boostDocumentRuleBhv.insertOrUpdate(boostDocumentRule);

    }

    public void delete(final BoostDocumentRule boostDocumentRule)
            throws CrudMessageException {
        setupDeleteCondition(boostDocumentRule);

        boostDocumentRuleBhv.delete(boostDocumentRule);

    }

    protected void setupListCondition(final BoostDocumentRuleCB cb,
            final BoostDocumentRulePager boostDocumentRulePager) {

        if (boostDocumentRulePager.id != null) {
            cb.query().setId_Equal(Long.parseLong(boostDocumentRulePager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final BoostDocumentRuleCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final BoostDocumentRule boostDocumentRule) {
    }

    protected void setupDeleteCondition(
            final BoostDocumentRule boostDocumentRule) {
    }
}