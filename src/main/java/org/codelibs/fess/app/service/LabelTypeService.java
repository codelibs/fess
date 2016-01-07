/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.LabelTypePager;
import org.codelibs.fess.es.config.cbean.LabelTypeCB;
import org.codelibs.fess.es.config.exbhv.LabelToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.config.exentity.LabelToRole;
import org.codelibs.fess.es.config.exentity.LabelType;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class LabelTypeService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected LabelToRoleBhv labelToRoleBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<LabelType> getLabelTypeList(final LabelTypePager labelTypePager) {

        final PagingResultBean<LabelType> labelTypeList = labelTypeBhv.selectPage(cb -> {
            cb.paging(labelTypePager.getPageSize(), labelTypePager.getCurrentPageNumber());
            setupListCondition(cb, labelTypePager);
        });

        // update pager
        BeanUtil.copyBeanToBean(labelTypeList, labelTypePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        labelTypePager.setPageNumberList(labelTypeList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return labelTypeList;
    }

    public void delete(final LabelType labelType) {

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

    public List<LabelType> getLabelTypeList() {
        return labelTypeBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger().intValue(), 1);
        });
    }

    public List<LabelType> getLabelTypeListWithRoles() {
        final ListResultBean<LabelType> labelTypeList = labelTypeBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger().intValue(), 1);
        });

        return labelTypeList;
    }

    public void store(final LabelType labelType) {
        final boolean isNew = labelType.getId() == null;
        final String[] roleTypeIds = labelType.getRoleTypeIds();

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

    public OptionalEntity<LabelType> getLabelType(final String id) {
        return labelTypeBhv.selectByPK(id).map(entity -> {
            final List<LabelToRole> wctrtmList = labelToRoleBhv.selectList(wctrtmCb -> {
                wctrtmCb.query().setLabelTypeId_Equal(entity.getId());
            });
            if (!wctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(wctrtmList.size());
                for (final LabelToRole mapping : wctrtmList) {
                    roleTypeIds.add(mapping.getRoleTypeId());
                }
                entity.setRoleTypeIds(roleTypeIds.toArray(new String[roleTypeIds.size()]));
            }
            return entity;
        });
    }

}
