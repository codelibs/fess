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
import org.codelibs.fess.db.cbean.CrawlingSessionCB;
import org.codelibs.fess.db.exbhv.CrawlingSessionBhv;
import org.codelibs.fess.db.exentity.CrawlingSession;
import org.codelibs.fess.pager.CrawlingSessionPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsCrawlingSessionService {

    @Resource
    protected CrawlingSessionBhv crawlingSessionBhv;

    public BsCrawlingSessionService() {
        super();
    }

    public List<CrawlingSession> getCrawlingSessionList(final CrawlingSessionPager crawlingSessionPager) {

        final PagingResultBean<CrawlingSession> crawlingSessionList = crawlingSessionBhv.selectPage(cb -> {
            cb.paging(crawlingSessionPager.getPageSize(), crawlingSessionPager.getCurrentPageNumber());
            setupListCondition(cb, crawlingSessionPager);
        });

        // update pager
        Beans.copy(crawlingSessionList, crawlingSessionPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        crawlingSessionPager.setPageNumberList(crawlingSessionList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return crawlingSessionList;
    }

    public CrawlingSession getCrawlingSession(final Map<String, String> keys) {
        final CrawlingSession crawlingSession = crawlingSessionBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (crawlingSession == null) {
            // TODO exception?
            return null;
        }

        return crawlingSession;
    }

    public void store(final CrawlingSession crawlingSession) throws CrudMessageException {
        setupStoreCondition(crawlingSession);

        crawlingSessionBhv.insertOrUpdate(crawlingSession);

    }

    public void delete(final CrawlingSession crawlingSession) throws CrudMessageException {
        setupDeleteCondition(crawlingSession);

        crawlingSessionBhv.delete(crawlingSession);

    }

    protected void setupListCondition(final CrawlingSessionCB cb, final CrawlingSessionPager crawlingSessionPager) {

        if (crawlingSessionPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(crawlingSessionPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final CrawlingSessionCB cb, final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final CrawlingSession crawlingSession) {
    }

    protected void setupDeleteCondition(final CrawlingSession crawlingSession) {
    }
}