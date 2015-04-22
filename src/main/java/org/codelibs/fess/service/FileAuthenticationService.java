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

package org.codelibs.fess.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.FileAuthenticationCB;
import org.codelibs.fess.db.exbhv.FileAuthenticationBhv;
import org.codelibs.fess.db.exentity.FileAuthentication;
import org.codelibs.fess.pager.FileAuthenticationPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public class FileAuthenticationService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected FileAuthenticationBhv fileAuthenticationBhv;

    public FileAuthenticationService() {
        super();
    }

    public List<FileAuthentication> getFileAuthenticationList(final FileAuthenticationPager fileAuthenticationPager) {

        final PagingResultBean<FileAuthentication> fileAuthenticationList = fileAuthenticationBhv.selectPage(cb -> {
            cb.paging(fileAuthenticationPager.getPageSize(), fileAuthenticationPager.getCurrentPageNumber());
            setupListCondition(cb, fileAuthenticationPager);
        });

        // update pager
        Beans.copy(fileAuthenticationList, fileAuthenticationPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        fileAuthenticationPager.setPageNumberList(fileAuthenticationList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return fileAuthenticationList;
    }

    public FileAuthentication getFileAuthentication(final Map<String, String> keys) {
        final FileAuthentication fileAuthentication = fileAuthenticationBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (fileAuthentication == null) {
            // TODO exception?
            return null;
        }

        return fileAuthentication;
    }

    public void store(final FileAuthentication fileAuthentication) throws CrudMessageException {
        setupStoreCondition(fileAuthentication);

        fileAuthenticationBhv.insertOrUpdate(fileAuthentication);

    }

    public void delete(final FileAuthentication fileAuthentication) throws CrudMessageException {
        setupDeleteCondition(fileAuthentication);

        fileAuthenticationBhv.delete(fileAuthentication);

    }

    protected void setupListCondition(final FileAuthenticationCB cb, final FileAuthenticationPager fileAuthenticationPager) {
        if (fileAuthenticationPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(fileAuthenticationPager.id));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.setupSelect_FileCrawlingConfig();
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_Hostname_Asc();
        cb.query().addOrderBy_FileCrawlingConfigId_Asc();

        // search

    }

    protected void setupEntityCondition(final FileAuthenticationCB cb, final Map<String, String> keys) {

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    protected void setupStoreCondition(final FileAuthentication fileAuthentication) {

        // setup condition

    }

    protected void setupDeleteCondition(final FileAuthentication fileAuthentication) {

        // setup condition

    }

    public List<FileAuthentication> getFileAuthenticationList(final Long fileCrawlingConfigId) {
        return fileAuthenticationBhv.selectList(cb -> {
            cb.query().setFileCrawlingConfigId_Equal(fileCrawlingConfigId);
        });
    }
}
