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

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.es.cbean.RequestHeaderCB;
import org.codelibs.fess.es.exbhv.RequestHeaderBhv;
import org.codelibs.fess.es.exentity.RequestHeader;
import org.codelibs.fess.pager.RequestHeaderPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public class RequestHeaderService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected RequestHeaderBhv requestHeaderBhv;

    public RequestHeaderService() {
        super();
    }

    public List<RequestHeader> getRequestHeaderList(final RequestHeaderPager requestHeaderPager) {

        final PagingResultBean<RequestHeader> requestHeaderList = requestHeaderBhv.selectPage(cb -> {
            cb.paging(requestHeaderPager.getPageSize(), requestHeaderPager.getCurrentPageNumber());
            setupListCondition(cb, requestHeaderPager);
        });

        // update pager
        Beans.copy(requestHeaderList, requestHeaderPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        requestHeaderPager.setPageNumberList(requestHeaderList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return requestHeaderList;
    }

    public RequestHeader getRequestHeader(final Map<String, String> keys) {
        final RequestHeader requestHeader = requestHeaderBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (requestHeader == null) {
            // TODO exception?
            return null;
        }

        return requestHeader;
    }

    public void store(final RequestHeader requestHeader) throws CrudMessageException {
        setupStoreCondition(requestHeader);

        requestHeaderBhv.insertOrUpdate(requestHeader, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final RequestHeader requestHeader) throws CrudMessageException {
        setupDeleteCondition(requestHeader);

        requestHeaderBhv.delete(requestHeader, op -> {
            op.setRefresh(true);
        });

    }

    protected void setupListCondition(final RequestHeaderCB cb, final RequestHeaderPager requestHeaderPager) {
        if (requestHeaderPager.id != null) {
            cb.query().docMeta().setId_Equal(requestHeaderPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        // TODO CONFIG cb.setupSelect_WebCrawlingConfig();
        cb.query().addOrderBy_Name_Asc();
        // TODO CONFIG cb.query().addOrderBy_WebCrawlingConfigId_Asc();

        // search

    }

    protected void setupEntityCondition(final RequestHeaderCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final RequestHeader requestHeader) {

        // setup condition

    }

    protected void setupDeleteCondition(final RequestHeader requestHeader) {

        // setup condition

    }

    public List<RequestHeader> getRequestHeaderList(final Long webCrawlingConfigId) {
        return requestHeaderBhv.selectList(cb -> {
            // TODO CONFIG cb.query().setWebCrawlingConfigId_Equal(webCrawlingConfigId);
                cb.query().matchAll();
            });
    }

}
