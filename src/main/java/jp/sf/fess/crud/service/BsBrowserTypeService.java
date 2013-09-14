/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.db.cbean.BrowserTypeCB;
import jp.sf.fess.db.exbhv.BrowserTypeBhv;
import jp.sf.fess.db.exentity.BrowserType;
import jp.sf.fess.pager.BrowserTypePager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsBrowserTypeService {

    @Resource
    protected BrowserTypeBhv browserTypeBhv;

    public BsBrowserTypeService() {
        super();
    }

    public List<BrowserType> getBrowserTypeList(
            final BrowserTypePager browserTypePager) {

        final BrowserTypeCB cb = new BrowserTypeCB();

        cb.fetchFirst(browserTypePager.getPageSize());
        cb.fetchPage(browserTypePager.getCurrentPageNumber());

        setupListCondition(cb, browserTypePager);

        final PagingResultBean<BrowserType> browserTypeList = browserTypeBhv
                .selectPage(cb);

        // update pager
        Beans.copy(browserTypeList, browserTypePager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        browserTypeList.setPageRangeSize(5);
        browserTypePager.setPageNumberList(browserTypeList.pageRange()
                .createPageNumberList());

        return browserTypeList;
    }

    public BrowserType getBrowserType(final Map<String, String> keys) {
        final BrowserTypeCB cb = new BrowserTypeCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final BrowserType browserType = browserTypeBhv.selectEntity(cb);
        if (browserType == null) {
            // TODO exception?
            return null;
        }

        return browserType;
    }

    public void store(final BrowserType browserType)
            throws CrudMessageException {
        setupStoreCondition(browserType);

        browserTypeBhv.insertOrUpdate(browserType);

    }

    public void delete(final BrowserType browserType)
            throws CrudMessageException {
        setupDeleteCondition(browserType);

        browserTypeBhv.delete(browserType);

    }

    protected void setupListCondition(final BrowserTypeCB cb,
            final BrowserTypePager browserTypePager) {

        if (browserTypePager.id != null) {
            cb.query().setId_Equal(Long.parseLong(browserTypePager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final BrowserTypeCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final BrowserType browserType) {
    }

    protected void setupDeleteCondition(final BrowserType browserType) {
    }
}