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

package jp.sf.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.db.cbean.DataCrawlingConfigCB;
import jp.sf.fess.db.exbhv.DataCrawlingConfigBhv;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.pager.DataCrawlingConfigPager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsDataCrawlingConfigService {

    @Resource
    protected DataCrawlingConfigBhv dataCrawlingConfigBhv;

    public BsDataCrawlingConfigService() {
        super();
    }

    public List<DataCrawlingConfig> getDataCrawlingConfigList(
            final DataCrawlingConfigPager dataCrawlingConfigPager) {

        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();

        cb.fetchFirst(dataCrawlingConfigPager.getPageSize());
        cb.fetchPage(dataCrawlingConfigPager.getCurrentPageNumber());

        setupListCondition(cb, dataCrawlingConfigPager);

        final PagingResultBean<DataCrawlingConfig> dataCrawlingConfigList = dataCrawlingConfigBhv
                .selectPage(cb);

        // update pager
        Beans.copy(dataCrawlingConfigList, dataCrawlingConfigPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        dataCrawlingConfigList.setPageRangeSize(5);
        dataCrawlingConfigPager.setPageNumberList(dataCrawlingConfigList
                .pageRange().createPageNumberList());

        return dataCrawlingConfigList;
    }

    public DataCrawlingConfig getDataCrawlingConfig(
            final Map<String, String> keys) {
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final DataCrawlingConfig dataCrawlingConfig = dataCrawlingConfigBhv
                .selectEntity(cb);
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