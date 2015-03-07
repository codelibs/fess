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
import org.codelibs.fess.db.cbean.FileCrawlingConfigCB;
import org.codelibs.fess.db.exbhv.FileCrawlingConfigBhv;
import org.codelibs.fess.db.exentity.FileCrawlingConfig;
import org.codelibs.fess.pager.FileCrawlingConfigPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsFileCrawlingConfigService {

    @Resource
    protected FileCrawlingConfigBhv fileCrawlingConfigBhv;

    public BsFileCrawlingConfigService() {
        super();
    }

    public List<FileCrawlingConfig> getFileCrawlingConfigList(final FileCrawlingConfigPager fileCrawlingConfigPager) {

        final PagingResultBean<FileCrawlingConfig> fileCrawlingConfigList = fileCrawlingConfigBhv.selectPage(cb -> {
            cb.paging(fileCrawlingConfigPager.getPageSize(), fileCrawlingConfigPager.getCurrentPageNumber());
            setupListCondition(cb, fileCrawlingConfigPager);
        });

        // update pager
        Beans.copy(fileCrawlingConfigList, fileCrawlingConfigPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        fileCrawlingConfigPager.setPageNumberList(fileCrawlingConfigList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return fileCrawlingConfigList;
    }

    public FileCrawlingConfig getFileCrawlingConfig(final Map<String, String> keys) {
        final FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (fileCrawlingConfig == null) {
            // TODO exception?
            return null;
        }

        return fileCrawlingConfig;
    }

    public void store(final FileCrawlingConfig fileCrawlingConfig) throws CrudMessageException {
        setupStoreCondition(fileCrawlingConfig);

        fileCrawlingConfigBhv.insertOrUpdate(fileCrawlingConfig);

    }

    public void delete(final FileCrawlingConfig fileCrawlingConfig) throws CrudMessageException {
        setupDeleteCondition(fileCrawlingConfig);

        fileCrawlingConfigBhv.delete(fileCrawlingConfig);

    }

    protected void setupListCondition(final FileCrawlingConfigCB cb, final FileCrawlingConfigPager fileCrawlingConfigPager) {

        if (fileCrawlingConfigPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(fileCrawlingConfigPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final FileCrawlingConfigCB cb, final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final FileCrawlingConfig fileCrawlingConfig) {
    }

    protected void setupDeleteCondition(final FileCrawlingConfig fileCrawlingConfig) {
    }
}