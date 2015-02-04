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

package jp.sf.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.db.cbean.BoostDocumentRuleCB;
import jp.sf.fess.db.exbhv.BoostDocumentRuleBhv;
import jp.sf.fess.db.exentity.BoostDocumentRule;
import jp.sf.fess.pager.BoostDocumentRulePager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsBoostDocumentRuleService {

    @Resource
    protected BoostDocumentRuleBhv boostDocumentRuleBhv;

    public BsBoostDocumentRuleService() {
        super();
    }

    public List<BoostDocumentRule> getBoostDocumentRuleList(
            final BoostDocumentRulePager boostDocumentRulePager) {

        final BoostDocumentRuleCB cb = new BoostDocumentRuleCB();

        cb.fetchFirst(boostDocumentRulePager.getPageSize());
        cb.fetchPage(boostDocumentRulePager.getCurrentPageNumber());

        setupListCondition(cb, boostDocumentRulePager);

        final PagingResultBean<BoostDocumentRule> boostDocumentRuleList = boostDocumentRuleBhv
                .selectPage(cb);

        // update pager
        Beans.copy(boostDocumentRuleList, boostDocumentRulePager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        boostDocumentRuleList.setPageRangeSize(5);
        boostDocumentRulePager.setPageNumberList(boostDocumentRuleList
                .pageRange().createPageNumberList());

        return boostDocumentRuleList;
    }

    public BoostDocumentRule getBoostDocumentRule(final Map<String, String> keys) {
        final BoostDocumentRuleCB cb = new BoostDocumentRuleCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final BoostDocumentRule boostDocumentRule = boostDocumentRuleBhv
                .selectEntity(cb);
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