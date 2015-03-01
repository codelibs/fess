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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.crud.service.BsPathMappingService;
import org.codelibs.fess.db.allcommon.CDef;
import org.codelibs.fess.db.cbean.PathMappingCB;
import org.codelibs.fess.db.exentity.PathMapping;
import org.codelibs.fess.pager.PathMappingPager;

public class PathMappingService extends BsPathMappingService implements
        Serializable {

    private static final long serialVersionUID = 1L;

    public List<PathMapping> getPathMappingList(
            final Collection<CDef.ProcessType> cdefList) {

        return pathMappingBhv.selectList(cb -> {
            cb.query().setDeletedBy_IsNull();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().setProcessType_InScope_AsProcessType(cdefList);
        });
    }

    @Override
    protected void setupListCondition(final PathMappingCB cb,
            final PathMappingPager pathMappingPager) {
        super.setupListCondition(cb, pathMappingPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final PathMappingCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(final PathMapping pathMapping) {
        super.setupStoreCondition(pathMapping);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final PathMapping pathMapping) {
        super.setupDeleteCondition(pathMapping);

        // setup condition

    }

}
