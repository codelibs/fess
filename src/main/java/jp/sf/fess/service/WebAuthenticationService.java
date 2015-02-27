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

import jp.sf.fess.crud.service.BsWebAuthenticationService;
import jp.sf.fess.db.cbean.WebAuthenticationCB;
import jp.sf.fess.db.exentity.WebAuthentication;
import jp.sf.fess.pager.WebAuthenticationPager;

public class WebAuthenticationService extends BsWebAuthenticationService
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setupListCondition(final WebAuthenticationCB cb,
            final WebAuthenticationPager webAuthenticationPager) {
        super.setupListCondition(cb, webAuthenticationPager);

        // setup condition
        cb.setupSelect_WebCrawlingConfig();
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_Hostname_Asc();
        cb.query().addOrderBy_WebCrawlingConfigId_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final WebAuthenticationCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(final WebAuthentication webAuthentication) {
        super.setupStoreCondition(webAuthentication);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(
            final WebAuthentication webAuthentication) {
        super.setupDeleteCondition(webAuthentication);

        // setup condition

    }

    public List<WebAuthentication> getWebAuthenticationList(
            final Long webCrawlingConfigId) {
        final WebAuthenticationCB cb = new WebAuthenticationCB();
        cb.query().setWebCrawlingConfigId_Equal(webCrawlingConfigId);
        return webAuthenticationBhv.selectList(cb);
    }

}
