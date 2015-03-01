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
import org.codelibs.fess.db.cbean.DataCrawlingConfigCB;
import org.codelibs.fess.db.exbhv.DataCrawlingConfigBhv;
import org.codelibs.fess.db.exentity.DataCrawlingConfig;
import org.codelibs.fess.pager.DataCrawlingConfigPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsDataCrawlingConfigService {

    @Resource
    protected DataCrawlingConfigBhv dataCrawlingConfigBhv;

    public BsDataCrawlingConfigService() {
        super();
    }

    public List<DataCrawlingConfig> getDataCrawlingConfigList(
            final DataCrawlingConfigPager dataCrawlingConfigPager) {

        final PagingResultBean<DataCrawlingConfig> dataCrawlingConfigList = dataCrawlingConfigBhv
                .selectPage(cb -> {
                    cb.paging(dataCrawlingConfigPager.getPageSize(),
                            dataCrawlingConfigPager.getCurrentPageNumber());

                    setupListCondition(cb, dataCrawlingConfigPager);
                });

        // update pager
        Beans.copy(dataCrawlingConfigList, dataCrawlingConfigPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        dataCrawlingConfigPager.setPageNumberList(dataCrawlingConfigList
                .pageRange(op -> {
                    op.rangeSize(5);
                }).createPageNumberList());

        return dataCrawlingConfigList;
    }

    public DataCrawlingConfig getDataCrawlingConfig(
            final Map<String, String> keys) {
        final DataCrawlingConfig dataCrawlingConfig = dataCrawlingConfigBhv
                .selectEntity(cb -> {
                    cb.query().setId_Equal(Long.parseLong(keys.get("id")));
                    setupEntityCondition(cb, keys);
                }).orElse(null);//TODO
        if (dataCrawlingConfig == null) {
            // TODO exception?
            return null;
        }

        return dataCrawlingConfig;
    }

    public void store(final DataCrawlingConfig dataCrawlingConfig)
            throws CrudMessageException {
        setupStoreCondition(dataCrawlingConfig);

        dataCrawlingConfigBhv.insertOrUpdate(dataCrawlingConfig);

    }

    public void delete(final DataCrawlingConfig dataCrawlingConfig)
            throws CrudMessageException {
        setupDeleteCondition(dataCrawlingConfig);

        dataCrawlingConfigBhv.delete(dataCrawlingConfig);

    }

    protected void setupListCondition(final DataCrawlingConfigCB cb,
            final DataCrawlingConfigPager dataCrawlingConfigPager) {

        if (dataCrawlingConfigPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(dataCrawlingConfigPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final DataCrawlingConfigCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(
            final DataCrawlingConfig dataCrawlingConfig) {
    }

    protected void setupDeleteCondition(
            final DataCrawlingConfig dataCrawlingConfig) {
    }
}