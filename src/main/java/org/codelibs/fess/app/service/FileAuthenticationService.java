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
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FileAuthPager;
import org.codelibs.fess.es.config.cbean.FileAuthenticationCB;
import org.codelibs.fess.es.config.exbhv.FileAuthenticationBhv;
import org.codelibs.fess.es.config.exentity.FileAuthentication;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class FileAuthenticationService {

    @Resource
    protected FileAuthenticationBhv fileAuthenticationBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<FileAuthentication> getFileAuthenticationList(final FileAuthPager fileAuthenticationPager) {

        final PagingResultBean<FileAuthentication> fileAuthenticationList = fileAuthenticationBhv.selectPage(cb -> {
            cb.paging(fileAuthenticationPager.getPageSize(), fileAuthenticationPager.getCurrentPageNumber());
            setupListCondition(cb, fileAuthenticationPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(fileAuthenticationList, fileAuthenticationPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        fileAuthenticationPager.setPageNumberList(fileAuthenticationList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return fileAuthenticationList;
    }

    public OptionalEntity<FileAuthentication> getFileAuthentication(final String id) {
        return fileAuthenticationBhv.selectByPK(id);
    }

    public void store(final FileAuthentication fileAuthentication) {
        fileAuthentication.setParameters(ParameterUtil.encrypt(fileAuthentication.getParameters()));
        fileAuthenticationBhv.insertOrUpdate(fileAuthentication, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public void delete(final FileAuthentication fileAuthentication) {

        fileAuthenticationBhv.delete(fileAuthentication, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    protected void setupListCondition(final FileAuthenticationCB cb, final FileAuthPager fileAuthenticationPager) {
        if (fileAuthenticationPager.id != null) {
            cb.query().docMeta().setId_Equal(fileAuthenticationPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Hostname_Asc();

        // search

    }

    public List<FileAuthentication> getFileAuthenticationList(final String fileConfigId) {
        return fileAuthenticationBhv.selectList(cb -> {
            cb.query().setFileConfigId_Equal(fileConfigId);
            cb.fetchFirst(fessConfig.getPageFileAuthMaxFetchSizeAsInteger());
        });
    }
}
