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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.service.BsLabelTypeService;
import org.codelibs.fess.db.cbean.LabelTypeCB;
import org.codelibs.fess.db.exbhv.LabelTypeToRoleTypeMappingBhv;
import org.codelibs.fess.db.exentity.LabelType;
import org.codelibs.fess.db.exentity.LabelTypeToRoleTypeMapping;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.pager.LabelTypePager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;

public class LabelTypeService extends BsLabelTypeService implements
        Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected LabelTypeToRoleTypeMappingBhv labelTypeToRoleTypeMappingBhv;

    @Override
    protected void setupListCondition(final LabelTypeCB cb,
            final LabelTypePager labelTypePager) {
        super.setupListCondition(cb, labelTypePager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
        // search

    }

    @Override
    protected void setupEntityCondition(final LabelTypeCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(final LabelType labelType) {
        super.setupStoreCondition(labelType);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final LabelType labelType) {
        super.setupDeleteCondition(labelType);

        // setup condition

    }

    public List<LabelType> getLabelTypeList() {
        return labelTypeBhv.selectList(cb -> {
            cb.query().setDeletedBy_IsNull();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });
    }

    public List<LabelType> getLabelTypeListWithRoles() {
        final ListResultBean<LabelType> labelTypeList = labelTypeBhv
                .selectList(cb -> {
                    cb.query().setDeletedBy_IsNull();
                    cb.query().addOrderBy_SortOrder_Asc();
                    cb.query().addOrderBy_Name_Asc();
                });

        labelTypeBhv.loadLabelTypeToRoleTypeMapping(labelTypeList, cb -> {
            cb.setupSelect_RoleType();
            cb.query().queryRoleType().addOrderBy_Value_Asc();
        });

        return labelTypeList;
    }

    @Override
    public void store(final LabelType labelType) {
        final boolean isNew = labelType.getId() == null;
        final String[] roleTypeIds = labelType.getRoleTypeIds();
        super.store(labelType);
        final Long labelTypeId = labelType.getId();
        if (isNew) {
            // Insert
            if (roleTypeIds != null) {
                final List<LabelTypeToRoleTypeMapping> lttrtmList = new ArrayList<LabelTypeToRoleTypeMapping>();
                for (final String id : roleTypeIds) {
                    final LabelTypeToRoleTypeMapping mapping = new LabelTypeToRoleTypeMapping();
                    mapping.setLabelTypeId(labelTypeId);
                    mapping.setRoleTypeId(Long.parseLong(id));
                    lttrtmList.add(mapping);
                }
                labelTypeToRoleTypeMappingBhv.batchInsert(lttrtmList);
            }
        } else {
            // Update
            if (roleTypeIds != null) {
                final List<LabelTypeToRoleTypeMapping> list = labelTypeToRoleTypeMappingBhv
                        .selectList(lttrtmCb -> {
                            lttrtmCb.query().setLabelTypeId_Equal(labelTypeId);
                        });
                final List<LabelTypeToRoleTypeMapping> newList = new ArrayList<LabelTypeToRoleTypeMapping>();
                final List<LabelTypeToRoleTypeMapping> matchedList = new ArrayList<LabelTypeToRoleTypeMapping>();
                for (final String id : roleTypeIds) {
                    final Long roleTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final LabelTypeToRoleTypeMapping mapping : list) {
                        if (mapping.getRoleTypeId().equals(roleTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final LabelTypeToRoleTypeMapping mapping = new LabelTypeToRoleTypeMapping();
                        mapping.setLabelTypeId(labelTypeId);
                        mapping.setRoleTypeId(roleTypeId);
                        newList.add(mapping);
                    }
                }
                list.removeAll(matchedList);
                labelTypeToRoleTypeMappingBhv.batchInsert(newList);
                labelTypeToRoleTypeMappingBhv.batchDelete(list);
            }
        }

        final LabelTypeHelper labelTypeHelper = ComponentUtil
                .getLabelTypeHelper();
        if (labelTypeHelper != null) {
            labelTypeHelper.refresh(getLabelTypeListWithRoles());
        }
    }

    @Override
    public LabelType getLabelType(final Map<String, String> keys) {
        final LabelType labelType = super.getLabelType(keys);
        if (labelType != null) {
            final List<LabelTypeToRoleTypeMapping> wctrtmList = labelTypeToRoleTypeMappingBhv
                    .selectList(wctrtmCb -> {
                        wctrtmCb.query()
                                .setLabelTypeId_Equal(labelType.getId());
                        wctrtmCb.query().queryRoleType().setDeletedBy_IsNull();
                        wctrtmCb.query().queryLabelType().setDeletedBy_IsNull();
                    });
            if (!wctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(
                        wctrtmList.size());
                for (final LabelTypeToRoleTypeMapping mapping : wctrtmList) {
                    roleTypeIds.add(Long.toString(mapping.getRoleTypeId()));
                }
                labelType.setRoleTypeIds(roleTypeIds
                        .toArray(new String[roleTypeIds.size()]));
            }
        }
        return labelType;
    }

}
