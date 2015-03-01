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
import java.util.Map;

import org.codelibs.fess.crud.service.BsKeyMatchService;
import org.codelibs.fess.db.cbean.KeyMatchCB;
import org.codelibs.fess.db.exentity.KeyMatch;
import org.codelibs.fess.pager.KeyMatchPager;

public class KeyMatchService extends BsKeyMatchService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setupListCondition(final KeyMatchCB cb,
            final KeyMatchPager keyMatchPager) {
        super.setupListCondition(cb, keyMatchPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_Term_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final KeyMatchCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(final KeyMatch keyMatch) {
        super.setupStoreCondition(keyMatch);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final KeyMatch keyMatch) {
        super.setupDeleteCondition(keyMatch);

        // setup condition

    }

    public List<KeyMatch> getAvailableKeyMatchList() {
        return keyMatchBhv.selectList(cb -> {
            cb.query().setDeletedBy_IsNull();
        });
    }

}
