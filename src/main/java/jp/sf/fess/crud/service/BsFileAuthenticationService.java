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
import jp.sf.fess.db.cbean.FileAuthenticationCB;
import jp.sf.fess.db.exbhv.FileAuthenticationBhv;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.pager.FileAuthenticationPager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsFileAuthenticationService {

    @Resource
    protected FileAuthenticationBhv fileAuthenticationBhv;

    public BsFileAuthenticationService() {
        super();
    }

    public List<FileAuthentication> getFileAuthenticationList(
            final FileAuthenticationPager fileAuthenticationPager) {

        final FileAuthenticationCB cb = new FileAuthenticationCB();

        cb.fetchFirst(fileAuthenticationPager.getPageSize());
        cb.fetchPage(fileAuthenticationPager.getCurrentPageNumber());

        setupListCondition(cb, fileAuthenticationPager);

        final PagingResultBean<FileAuthentication> fileAuthenticationList = fileAuthenticationBhv
                .selectPage(cb);

        // update pager
        Beans.copy(fileAuthenticationList, fileAuthenticationPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        fileAuthenticationList.setPageRangeSize(5);
        fileAuthenticationPager.setPageNumberList(fileAuthenticationList
                .pageRange().createPageNumberList());

        return fileAuthenticationList;
    }

    public FileAuthentication getFileAuthentication(
            final Map<String, String> keys) {
        final FileAuthenticationCB cb = new FileAuthenticationCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final FileAuthentication fileAuthentication = fileAuthenticationBhv
                .selectEntity(cb);
        if (fileAuthentication == null) {
            // TODO exception?
            return null;
        }

        return fileAuthentication;
    }

    public void store(final FileAuthentication fileAuthentication)
            throws CrudMessageException {
        setupStoreCondition(fileAuthentication);

        fileAuthenticationBhv.insertOrUpdate(fileAuthentication);

    }

    public void delete(final FileAuthentication fileAuthentication)
            throws CrudMessageException {
        setupDeleteCondition(fileAuthentication);

        fileAuthenticationBhv.delete(fileAuthentication);

    }

    protected void setupListCondition(final FileAuthenticationCB cb,
            final FileAuthenticationPager fileAuthenticationPager) {

        if (fileAuthenticationPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(fileAuthenticationPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final FileAuthenticationCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(
            final FileAuthentication fileAuthentication) {
    }

    protected void setupDeleteCondition(
            final FileAuthentication fileAuthentication) {
    }
}