/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.ApiTokenPager;
import org.codelibs.fess.es.config.cbean.ApiTokenCB;
import org.codelibs.fess.es.config.exbhv.ApiTokenBhv;
import org.codelibs.fess.es.config.exentity.ApiToken;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class ApiTokenService {

    @Resource
    protected ApiTokenBhv apiTokenBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<ApiToken> getApiTokenList(final ApiTokenPager apiTokenPager) {

        final PagingResultBean<ApiToken> apiTokenList = apiTokenBhv.selectPage(cb -> {
            cb.paging(apiTokenPager.getPageSize(), apiTokenPager.getCurrentPageNumber());
            setupListCondition(cb, apiTokenPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(apiTokenList, apiTokenPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        apiTokenPager.setPageNumberList(apiTokenList.pageRange(op -> op.rangeSize(5)).createPageNumberList());

        return apiTokenList;
    }

    public OptionalEntity<ApiToken> getApiToken(final String id) {
        return apiTokenBhv.selectByPK(id);
    }

    public void store(final ApiToken apiToken) {

        apiTokenBhv.insertOrUpdate(apiToken, op -> op.setRefresh(true));

    }

    public void delete(final ApiToken apiToken) {

        apiTokenBhv.delete(apiToken, op -> op.setRefresh(true));

    }

    protected void setupListCondition(final ApiTokenCB cb, final ApiTokenPager apiTokenPager) {
        if (apiTokenPager.id != null) {
            cb.query().docMeta().setId_Equal(apiTokenPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

}