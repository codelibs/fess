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
import org.codelibs.fess.db.cbean.WebAuthenticationCB;
import org.codelibs.fess.db.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.db.exentity.WebAuthentication;
import org.codelibs.fess.pager.WebAuthenticationPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsWebAuthenticationService {

    @Resource
    protected WebAuthenticationBhv webAuthenticationBhv;

    public BsWebAuthenticationService() {
        super();
    }

    public List<WebAuthentication> getWebAuthenticationList(
            final WebAuthenticationPager webAuthenticationPager) {

        final PagingResultBean<WebAuthentication> webAuthenticationList = webAuthenticationBhv
                .selectPage(cb -> {
                    cb.paging(webAuthenticationPager.getPageSize(),
                            webAuthenticationPager.getCurrentPageNumber());
                    setupListCondition(cb, webAuthenticationPager);
                });

        // update pager
        Beans.copy(webAuthenticationList, webAuthenticationPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        webAuthenticationPager.setPageNumberList(webAuthenticationList
                .pageRange(op -> {
                    op.rangeSize(5);
                }).createPageNumberList());

        return webAuthenticationList;
    }

    public WebAuthentication getWebAuthentication(final Map<String, String> keys) {
        final WebAuthentication webAuthentication = webAuthenticationBhv
                .selectEntity(cb -> {
                    cb.query().setId_Equal(Long.parseLong(keys.get("id")));
                    setupEntityCondition(cb, keys);
                }).orElse(null);//TODO
        if (webAuthentication == null) {
            // TODO exception?
            return null;
        }

        return webAuthentication;
    }

    public void store(final WebAuthentication webAuthentication)
            throws CrudMessageException {
        setupStoreCondition(webAuthentication);

        webAuthenticationBhv.insertOrUpdate(webAuthentication);

    }

    public void delete(final WebAuthentication webAuthentication)
            throws CrudMessageException {
        setupDeleteCondition(webAuthentication);

        webAuthenticationBhv.delete(webAuthentication);

    }

    protected void setupListCondition(final WebAuthenticationCB cb,
            final WebAuthenticationPager webAuthenticationPager) {

        if (webAuthenticationPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(webAuthenticationPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final WebAuthenticationCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final WebAuthentication webAuthentication) {
    }

    protected void setupDeleteCondition(
            final WebAuthentication webAuthentication) {
    }
}