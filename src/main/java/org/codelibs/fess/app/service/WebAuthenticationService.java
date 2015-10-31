/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import org.codelibs.fess.app.pager.WebAuthenticationPager;
import org.codelibs.fess.es.config.cbean.WebAuthenticationCB;
import org.codelibs.fess.es.config.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.es.config.exentity.WebAuthentication;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class WebAuthenticationService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected WebAuthenticationBhv webAuthenticationBhv;

    public WebAuthenticationService() {
        super();
    }

    public List<WebAuthentication> getWebAuthenticationList(final WebAuthenticationPager webAuthenticationPager) {

        final PagingResultBean<WebAuthentication> webAuthenticationList = webAuthenticationBhv.selectPage(cb -> {
            cb.paging(webAuthenticationPager.getPageSize(), webAuthenticationPager.getCurrentPageNumber());
            setupListCondition(cb, webAuthenticationPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(webAuthenticationList, webAuthenticationPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        webAuthenticationPager.setPageNumberList(webAuthenticationList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return webAuthenticationList;
    }

    public OptionalEntity<WebAuthentication> getWebAuthentication(final String id) {
        return webAuthenticationBhv.selectByPK(id);
    }

    public void store(final WebAuthentication webAuthentication) {
        setupStoreCondition(webAuthentication);

        webAuthenticationBhv.insertOrUpdate(webAuthentication, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final WebAuthentication webAuthentication) {
        setupDeleteCondition(webAuthentication);

        webAuthenticationBhv.delete(webAuthentication, op -> {
            op.setRefresh(true);
        });

    }

    protected void setupListCondition(final WebAuthenticationCB cb, final WebAuthenticationPager webAuthenticationPager) {
        if (webAuthenticationPager.id != null) {
            cb.query().docMeta().setId_Equal(webAuthenticationPager.id);
        }
        // TODO Long, Integer, String supported only.
        // setup condition
        cb.query().addOrderBy_Hostname_Asc();
        cb.query().addOrderBy_WebConfigId_Asc();

        // search

    }

    protected void setupEntityCondition(final WebAuthenticationCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final WebAuthentication webAuthentication) {

        // setup condition

    }

    protected void setupDeleteCondition(final WebAuthentication webAuthentication) {

        // setup condition

    }

    public List<WebAuthentication> getWebAuthenticationList(final String webConfigId) {
        return webAuthenticationBhv.selectList(cb -> {
            cb.query().setWebConfigId_Equal(webConfigId);
        });
    }

}
