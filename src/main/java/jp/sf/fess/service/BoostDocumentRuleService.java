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

package jp.sf.fess.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jp.sf.fess.crud.service.BsBoostDocumentRuleService;
import jp.sf.fess.db.cbean.BoostDocumentRuleCB;
import jp.sf.fess.db.exentity.BoostDocumentRule;
import jp.sf.fess.pager.BoostDocumentRulePager;

public class BoostDocumentRuleService extends BsBoostDocumentRuleService
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setupListCondition(final BoostDocumentRuleCB cb,
            final BoostDocumentRulePager boostDocumentRulePager) {
        super.setupListCondition(cb, boostDocumentRulePager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final BoostDocumentRuleCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(final BoostDocumentRule boostDocumentRule) {
        super.setupStoreCondition(boostDocumentRule);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(
            final BoostDocumentRule boostDocumentRule) {
        super.setupDeleteCondition(boostDocumentRule);

        // setup condition

    }

    public List<BoostDocumentRule> getAvailableBoostDocumentRuleList() {
        final BoostDocumentRuleCB cb = new BoostDocumentRuleCB();
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();
        return boostDocumentRuleBhv.selectList(cb);
    }

}
