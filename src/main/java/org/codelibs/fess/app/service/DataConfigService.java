/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
import org.codelibs.fess.app.pager.DataConfigPager;
import org.codelibs.fess.es.config.cbean.DataConfigCB;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class DataConfigService {

    @Resource
    protected DataConfigBhv dataConfigBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<DataConfig> getDataConfigList(final DataConfigPager dataConfigPager) {

        final PagingResultBean<DataConfig> dataConfigList = dataConfigBhv.selectPage(cb -> {
            cb.paging(dataConfigPager.getPageSize(), dataConfigPager.getCurrentPageNumber());

            setupListCondition(cb, dataConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(dataConfigList, dataConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        dataConfigPager.setPageNumberList(dataConfigList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return dataConfigList;
    }

    public void delete(final DataConfig dataConfig) {
        dataConfigBhv.delete(dataConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    public OptionalEntity<DataConfig> getDataConfig(final String id) {
        return dataConfigBhv.selectByPK(id);
    }

    public void store(final DataConfig dataConfig) {

        dataConfigBhv.insertOrUpdate(dataConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    protected void setupListCondition(final DataConfigCB cb, final DataConfigPager dataConfigPager) {
        if (dataConfigPager.id != null) {
            cb.query().docMeta().setId_Equal(dataConfigPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
