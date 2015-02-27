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

package jp.sf.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.db.cbean.OverlappingHostCB;
import jp.sf.fess.db.exbhv.OverlappingHostBhv;
import jp.sf.fess.db.exentity.OverlappingHost;
import jp.sf.fess.pager.OverlappingHostPager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsOverlappingHostService {

    @Resource
    protected OverlappingHostBhv overlappingHostBhv;

    public BsOverlappingHostService() {
        super();
    }

    public List<OverlappingHost> getOverlappingHostList(
            final OverlappingHostPager overlappingHostPager) {

        final OverlappingHostCB cb = new OverlappingHostCB();

        cb.fetchFirst(overlappingHostPager.getPageSize());
        cb.fetchPage(overlappingHostPager.getCurrentPageNumber());

        setupListCondition(cb, overlappingHostPager);

        final PagingResultBean<OverlappingHost> overlappingHostList = overlappingHostBhv
                .selectPage(cb);

        // update pager
        Beans.copy(overlappingHostList, overlappingHostPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        overlappingHostList.setPageRangeSize(5);
        overlappingHostPager.setPageNumberList(overlappingHostList.pageRange()
                .createPageNumberList());

        return overlappingHostList;
    }

    public OverlappingHost getOverlappingHost(final Map<String, String> keys) {
        final OverlappingHostCB cb = new OverlappingHostCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final OverlappingHost overlappingHost = overlappingHostBhv
                .selectEntity(cb);
        if (overlappingHost == null) {
            // TODO exception?
            return null;
        }

        return overlappingHost;
    }

    public void store(final OverlappingHost overlappingHost)
            throws CrudMessageException {
        setupStoreCondition(overlappingHost);

        overlappingHostBhv.insertOrUpdate(overlappingHost);

    }

    public void delete(final OverlappingHost overlappingHost)
            throws CrudMessageException {
        setupDeleteCondition(overlappingHost);

        overlappingHostBhv.delete(overlappingHost);

    }

    protected void setupListCondition(final OverlappingHostCB cb,
            final OverlappingHostPager overlappingHostPager) {

        if (overlappingHostPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(overlappingHostPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final OverlappingHostCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final OverlappingHost overlappingHost) {
    }

    protected void setupDeleteCondition(final OverlappingHost overlappingHost) {
    }
}