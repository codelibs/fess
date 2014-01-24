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

package jp.sf.fess.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jp.sf.fess.crud.service.BsFileAuthenticationService;
import jp.sf.fess.db.cbean.FileAuthenticationCB;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.pager.FileAuthenticationPager;

public class FileAuthenticationService extends BsFileAuthenticationService
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setupListCondition(final FileAuthenticationCB cb,
            final FileAuthenticationPager fileAuthenticationPager) {
        super.setupListCondition(cb, fileAuthenticationPager);

        // setup condition
        cb.setupSelect_FileCrawlingConfig();
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_Hostname_Asc();
        cb.query().addOrderBy_FileCrawlingConfigId_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final FileAuthenticationCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(
            final FileAuthentication fileAuthentication) {
        super.setupStoreCondition(fileAuthentication);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(
            final FileAuthentication fileAuthentication) {
        super.setupDeleteCondition(fileAuthentication);

        // setup condition

    }

    public List<FileAuthentication> getFileAuthenticationList(
            final Long fileCrawlingConfigId) {
        final FileAuthenticationCB cb = new FileAuthenticationCB();
        cb.query().setFileCrawlingConfigId_Equal(fileCrawlingConfigId);
        return fileAuthenticationBhv.selectList(cb);
    }
}
