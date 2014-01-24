/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

import jp.sf.fess.crud.service.BsRequestHeaderService;
import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.exentity.RequestHeader;
import jp.sf.fess.pager.RequestHeaderPager;

public class RequestHeaderService extends BsRequestHeaderService implements
        Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setupListCondition(final RequestHeaderCB cb,
            final RequestHeaderPager requestHeaderPager) {
        super.setupListCondition(cb, requestHeaderPager);

        // setup condition
        cb.setupSelect_WebCrawlingConfig();
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_Name_Asc();
        cb.query().addOrderBy_WebCrawlingConfigId_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final RequestHeaderCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(final RequestHeader requestHeader) {
        super.setupStoreCondition(requestHeader);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final RequestHeader requestHeader) {
        super.setupDeleteCondition(requestHeader);

        // setup condition

    }

    public List<RequestHeader> getRequestHeaderList(
            final Long webCrawlingConfigId) {
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.query().setWebCrawlingConfigId_Equal(webCrawlingConfigId);
        cb.query().setDeletedBy_IsNull();
        return requestHeaderBhv.selectList(cb);
    }

}
