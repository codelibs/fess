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
import org.codelibs.fess.app.pager.FileConfigPager;
import org.codelibs.fess.es.config.cbean.FileConfigCB;
import org.codelibs.fess.es.config.exbhv.FileAuthenticationBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class FileConfigService {

    @Resource
    protected FileConfigBhv fileConfigBhv;

    @Resource
    protected FileAuthenticationBhv fileAuthenticationBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<FileConfig> getFileConfigList(final FileConfigPager fileConfigPager) {

        final PagingResultBean<FileConfig> fileConfigList = fileConfigBhv.selectPage(cb -> {
            cb.paging(fileConfigPager.getPageSize(), fileConfigPager.getCurrentPageNumber());
            setupListCondition(cb, fileConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(fileConfigList, fileConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        fileConfigPager.setPageNumberList(fileConfigList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return fileConfigList;
    }

    public void delete(final FileConfig fileConfig) {

        final String fileConfigId = fileConfig.getId();

        fileConfigBhv.delete(fileConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        fileAuthenticationBhv.queryDelete(cb -> {
            cb.query().setFileConfigId_Equal(fileConfigId);
        });
    }

    public OptionalEntity<FileConfig> getFileConfig(final String id) {
        return fileConfigBhv.selectByPK(id);
    }

    public void store(final FileConfig fileConfig) {
        fileConfigBhv.insertOrUpdate(fileConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    protected void setupListCondition(final FileConfigCB cb, final FileConfigPager fileConfigPager) {
        if (fileConfigPager.id != null) {
            cb.query().docMeta().setId_Equal(fileConfigPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
