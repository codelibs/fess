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
import org.codelibs.fess.db.cbean.LabelTypeCB;
import org.codelibs.fess.db.exbhv.LabelTypeBhv;
import org.codelibs.fess.db.exentity.LabelType;
import org.codelibs.fess.pager.LabelTypePager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsLabelTypeService {

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    public BsLabelTypeService() {
        super();
    }

    public List<LabelType> getLabelTypeList(final LabelTypePager labelTypePager) {

        final PagingResultBean<LabelType> labelTypeList = labelTypeBhv
                .selectPage(cb -> {
                    cb.paging(labelTypePager.getPageSize(),
                            labelTypePager.getCurrentPageNumber());
                    setupListCondition(cb, labelTypePager);
                });

        // update pager
        Beans.copy(labelTypeList, labelTypePager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        labelTypePager.setPageNumberList(labelTypeList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return labelTypeList;
    }

    public LabelType getLabelType(final Map<String, String> keys) {
        final LabelType labelType = labelTypeBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (labelType == null) {
            // TODO exception?
            return null;
        }

        return labelType;
    }

    public void store(final LabelType labelType) throws CrudMessageException {
        setupStoreCondition(labelType);

        labelTypeBhv.insertOrUpdate(labelType);

    }

    public void delete(final LabelType labelType) throws CrudMessageException {
        setupDeleteCondition(labelType);

        labelTypeBhv.delete(labelType);

    }

    protected void setupListCondition(final LabelTypeCB cb,
            final LabelTypePager labelTypePager) {

        if (labelTypePager.id != null) {
            cb.query().setId_Equal(Long.parseLong(labelTypePager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final LabelTypeCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final LabelType labelType) {
    }

    protected void setupDeleteCondition(final LabelType labelType) {
    }
}