/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.LabelTypePager;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.LabelTypeCB;
import org.codelibs.fess.opensearch.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for label types.
 */
public class LabelTypeService extends FessAppService {

    /** The LabelType behavior. */
    @Resource
    protected LabelTypeBhv labelTypeBhv;

    /** The Fess config. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public LabelTypeService() {
        super();
    }

    /**
     * Get a list of label types.
     *
     * @param labelTypePager The pager for label types.
     * @return A list of label types.
     */
    public List<LabelType> getLabelTypeList(final LabelTypePager labelTypePager) {

        final PagingResultBean<LabelType> labelTypeList = labelTypeBhv.selectPage(cb -> {
            cb.paging(labelTypePager.getPageSize(), labelTypePager.getCurrentPageNumber());
            setupListCondition(cb, labelTypePager);
        });

        // update pager
        BeanUtil.copyBeanToBean(labelTypeList, labelTypePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        labelTypePager.setPageNumberList(labelTypeList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return labelTypeList;
    }

    /**
     * Delete a label type.
     *
     * @param labelType The label type to delete.
     */
    public void delete(final LabelType labelType) {

        labelTypeBhv.delete(labelType, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        if (labelTypeHelper != null) {
            labelTypeHelper.refresh(getLabelTypeList());
        }
    }

    /**
     * Set up list conditions.
     *
     * @param cb The condition bean.
     * @param labelTypePager The pager for label types.
     */
    protected void setupListCondition(final LabelTypeCB cb, final LabelTypePager labelTypePager) {
        if (StringUtil.isNotBlank(labelTypePager.name)) {
            cb.query().setName_Wildcard(wrapQuery(labelTypePager.name));
        }
        if (StringUtil.isNotBlank(labelTypePager.value)) {
            cb.query().setValue_Wildcard(wrapQuery(labelTypePager.value));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
        // search

    }

    /**
     * Get a list of all label types.
     *
     * @return A list of all label types.
     */
    public List<LabelType> getLabelTypeList() {
        return labelTypeBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger(), 1);
        });
    }

    /**
     * Store a label type.
     *
     * @param labelType The label type to store.
     */
    public void store(final LabelType labelType) {
        labelTypeBhv.insertOrUpdate(labelType, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        if (labelTypeHelper != null) {
            labelTypeHelper.refresh(getLabelTypeList());
        }
    }

    /**
     * Get a label type.
     *
     * @param id The ID of the label type.
     * @return An optional entity of the label type.
     */
    public OptionalEntity<LabelType> getLabelType(final String id) {
        return labelTypeBhv.selectByPK(id);
    }

}
