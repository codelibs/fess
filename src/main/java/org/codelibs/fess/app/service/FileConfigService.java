/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FileConfigPager;
import org.codelibs.fess.es.config.cbean.FileConfigCB;
import org.codelibs.fess.es.config.exbhv.FileAuthenticationBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class FileConfigService extends FessAppService {

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

    public OptionalEntity<FileConfig> getFileConfigByName(final String name) {
        final ListResultBean<FileConfig> list = fileConfigBhv.selectList(cb -> {
            cb.query().setName_Equal(name);
            cb.query().addOrderBy_SortOrder_Asc();
        });
        if (list.isEmpty()) {
            return OptionalEntity.empty();
        }
        return OptionalEntity.of(list.get(0));
    }

    public void store(final FileConfig fileConfig) {
        fileConfig.setConfigParameter(ParameterUtil.encrypt(fileConfig.getConfigParameter()));
        fileConfigBhv.insertOrUpdate(fileConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    protected void setupListCondition(final FileConfigCB cb, final FileConfigPager fileConfigPager) {
        if (StringUtil.isNotBlank(fileConfigPager.name)) {
            cb.query().setName_Wildcard(wrapQuery(fileConfigPager.name));
        }
        if (StringUtil.isNotBlank(fileConfigPager.paths)) {
            cb.query().setPaths_Wildcard(wrapQuery(fileConfigPager.paths));
        }
        if (StringUtil.isNotBlank(fileConfigPager.description)) {
            if (fileConfigPager.description.startsWith("*")) {
                cb.query().setDescription_Wildcard(fileConfigPager.description);
            } else if (fileConfigPager.description.endsWith("*")) {
                cb.query().setDescription_Prefix(fileConfigPager.description.replaceAll("\\*$", StringUtil.EMPTY));
            } else {
                cb.query().setDescription_MatchPhrase(fileConfigPager.description);
            }
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
