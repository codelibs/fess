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

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.es.cbean.LabelTypeCB;
import org.codelibs.fess.es.exbhv.LabelToRoleBhv;
import org.codelibs.fess.es.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.exentity.LabelToRole;
import org.codelibs.fess.es.exentity.LabelType;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.pager.LabelTypePager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public class LabelTypeService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected LabelToRoleBhv labelToRoleBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    public LabelTypeService() {
        super();
    }

    public List<LabelType> getLabelTypeList(final LabelTypePager labelTypePager) {

        final PagingResultBean<LabelType> labelTypeList = labelTypeBhv.selectPage(cb -> {
            cb.paging(labelTypePager.getPageSize(), labelTypePager.getCurrentPageNumber());
            setupListCondition(cb, labelTypePager);
        });

        // update pager
        Beans.copy(labelTypeList, labelTypePager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        labelTypePager.setPageNumberList(labelTypeList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return labelTypeList;
    }

    public void delete(final LabelType labelType) throws CrudMessageException {
        setupDeleteCondition(labelType);

        labelTypeBhv.delete(labelType, op -> {
            op.setRefresh(true);
        });

        labelToRoleBhv.queryDelete(cb -> {
            cb.query().setLabelTypeId_Equal(labelType.getId());
        });
    }

    protected void setupListCondition(final LabelTypeCB cb, final LabelTypePager labelTypePager) {
        if (labelTypePager.id != null) {
            cb.query().docMeta().setId_Equal(labelTypePager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
        // search

    }

    protected void setupEntityCondition(final LabelTypeCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final LabelType labelType) {

        // setup condition

    }

    protected void setupDeleteCondition(final LabelType labelType) {

        // setup condition

    }

    public List<LabelType> getLabelTypeList() {
        return labelTypeBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });
    }

    public List<LabelType> getLabelTypeListWithRoles() {
        final ListResultBean<LabelType> labelTypeList = labelTypeBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });

        return labelTypeList;
    }

    public void store(final LabelType labelType) {
        final boolean isNew = labelType.getId() == null;
        final String[] roleTypeIds = labelType.getRoleTypeIds();
        setupStoreCondition(labelType);

        labelTypeBhv.insertOrUpdate(labelType, op -> {
            op.setRefresh(true);
        });
        final String labelTypeId = labelType.getId();
        if (isNew) {
            // Insert
            if (roleTypeIds != null) {
                final List<LabelToRole> lttrtmList = new ArrayList<LabelToRole>();
                for (final String id : roleTypeIds) {
                    final LabelToRole mapping = new LabelToRole();
                    mapping.setLabelTypeId(labelTypeId);
                    mapping.setRoleTypeId(id);
                    lttrtmList.add(mapping);
                }
                labelToRoleBhv.batchInsert(lttrtmList, op -> {
                    op.setRefresh(true);
                });
            }
        } else {
            // Update
            if (roleTypeIds != null) {
                final List<LabelToRole> list = labelToRoleBhv.selectList(lttrtmCb -> {
                    lttrtmCb.query().setLabelTypeId_Equal(labelTypeId);
                });
                final List<LabelToRole> newList = new ArrayList<LabelToRole>();
                final List<LabelToRole> matchedList = new ArrayList<LabelToRole>();
                for (final String roleTypeId : roleTypeIds) {
                    boolean exist = false;
                    for (final LabelToRole mapping : list) {
                        if (mapping.getRoleTypeId().equals(roleTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final LabelToRole mapping = new LabelToRole();
                        mapping.setLabelTypeId(labelTypeId);
                        mapping.setRoleTypeId(roleTypeId);
                        newList.add(mapping);
                    }
                }
                list.removeAll(matchedList);
                labelToRoleBhv.batchInsert(newList, op -> {
                    op.setRefresh(true);
                });
                labelToRoleBhv.batchDelete(list, op -> {
                    op.setRefresh(true);
                });
            }
        }

        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        if (labelTypeHelper != null) {
            labelTypeHelper.refresh(getLabelTypeListWithRoles());
        }
    }

    public LabelType getLabelType(final Map<String, String> keys) {
        final LabelType labelType = labelTypeBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (labelType != null) {
            final List<LabelToRole> wctrtmList = labelToRoleBhv.selectList(wctrtmCb -> {
                wctrtmCb.query().setLabelTypeId_Equal(labelType.getId());
            });
            if (!wctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(wctrtmList.size());
                for (final LabelToRole mapping : wctrtmList) {
                    roleTypeIds.add(mapping.getRoleTypeId());
                }
                labelType.setRoleTypeIds(roleTypeIds.toArray(new String[roleTypeIds.size()]));
            }
        }
        return labelType;
    }

}
