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

package org.codelibs.fess.app.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.DataConfigPager;
import org.codelibs.fess.es.cbean.DataConfigCB;
import org.codelibs.fess.es.exbhv.DataConfigBhv;
import org.codelibs.fess.es.exbhv.DataConfigToLabelBhv;
import org.codelibs.fess.es.exbhv.DataConfigToRoleBhv;
import org.codelibs.fess.es.exentity.DataConfig;
import org.codelibs.fess.es.exentity.DataConfigToLabel;
import org.codelibs.fess.es.exentity.DataConfigToRole;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class DataConfigService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected DataConfigToRoleBhv dataConfigToRoleBhv;

    @Resource
    protected DataConfigToLabelBhv dataConfigToLabelBhv;

    @Resource
    protected DataConfigBhv dataConfigBhv;

    public DataConfigService() {
        super();
    }

    public List<DataConfig> getDataConfigList(final DataConfigPager dataConfigPager) {

        final PagingResultBean<DataConfig> dataConfigList = dataConfigBhv.selectPage(cb -> {
            cb.paging(dataConfigPager.getPageSize(), dataConfigPager.getCurrentPageNumber());

            setupListCondition(cb, dataConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(dataConfigList, dataConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        dataConfigPager.setPageNumberList(dataConfigList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return dataConfigList;
    }

    public void delete(final DataConfig dataConfig) {
        setupDeleteCondition(dataConfig);

        dataConfigBhv.delete(dataConfig, op -> {
            op.setRefresh(true);
        });

    }

    public List<DataConfig> getAllDataConfigList() {
        return getAllDataConfigList(true, true, true, null);
    }

    public List<DataConfig> getDataConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllDataConfigList();
        } else {
            return getAllDataConfigList(true, true, false, idList);
        }
    }

    public List<DataConfig> getAllDataConfigList(final boolean withLabelType, final boolean withRoleType, final boolean available,
            final List<String> idList) {
        final List<DataConfig> list = dataConfigBhv.selectList(cb -> {
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
        });
        return list;
    }

    public OptionalEntity<DataConfig> getDataConfig(final String id) {
        return dataConfigBhv.selectByPK(id).map(entity -> {

            final List<DataConfigToRole> fctrtmList = dataConfigToRoleBhv.selectList(fctrtmCb -> {
                fctrtmCb.query().setDataConfigId_Equal(entity.getId());
            });
            if (!fctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(fctrtmList.size());
                for (final DataConfigToRole mapping : fctrtmList) {
                    roleTypeIds.add(mapping.getRoleTypeId());
                }
                entity.setRoleTypeIds(roleTypeIds.toArray(new String[roleTypeIds.size()]));
            }

            final List<DataConfigToLabel> fctltmList = dataConfigToLabelBhv.selectList(fctltmCb -> {
                fctltmCb.query().setDataConfigId_Equal(entity.getId());
            });
            if (!fctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(fctltmList.size());
                for (final DataConfigToLabel mapping : fctltmList) {
                    labelTypeIds.add(mapping.getLabelTypeId());
                }
                entity.setLabelTypeIds(labelTypeIds.toArray(new String[labelTypeIds.size()]));
            }

            return entity;
        });
    }

    public void store(final DataConfig dataConfig) {
        final boolean isNew = dataConfig.getId() == null;
        final String[] labelTypeIds = dataConfig.getLabelTypeIds();
        final String[] roleTypeIds = dataConfig.getRoleTypeIds();
        setupStoreCondition(dataConfig);

        dataConfigBhv.insertOrUpdate(dataConfig, op -> {
            op.setRefresh(true);
        });
        final String dataConfigId = dataConfig.getId();
        if (isNew) {
            // Insert
            if (labelTypeIds != null) {
                final List<DataConfigToLabel> fctltmList = new ArrayList<DataConfigToLabel>();
                for (final String labelTypeId : labelTypeIds) {
                    final DataConfigToLabel mapping = new DataConfigToLabel();
                    mapping.setDataConfigId(dataConfigId);
                    mapping.setLabelTypeId(labelTypeId);
                    fctltmList.add(mapping);
                }
                dataConfigToLabelBhv.batchInsert(fctltmList, op -> {
                    op.setRefresh(true);
                });
            }
            if (roleTypeIds != null) {
                final List<DataConfigToRole> fctrtmList = new ArrayList<DataConfigToRole>();
                for (final String roleTypeId : roleTypeIds) {
                    final DataConfigToRole mapping = new DataConfigToRole();
                    mapping.setDataConfigId(dataConfigId);
                    mapping.setRoleTypeId(roleTypeId);
                    fctrtmList.add(mapping);
                }
                dataConfigToRoleBhv.batchInsert(fctrtmList, op -> {
                    op.setRefresh(true);
                });
            }
        } else {
            // Update
            if (labelTypeIds != null) {
                final List<DataConfigToLabel> fctltmList = dataConfigToLabelBhv.selectList(fctltmCb -> {
                    fctltmCb.query().setDataConfigId_Equal(dataConfigId);
                });
                final List<DataConfigToLabel> newList = new ArrayList<DataConfigToLabel>();
                final List<DataConfigToLabel> matchedList = new ArrayList<DataConfigToLabel>();
                for (final String id : labelTypeIds) {
                    boolean exist = false;
                    for (final DataConfigToLabel mapping : fctltmList) {
                        if (mapping.getLabelTypeId().equals(id)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final DataConfigToLabel mapping = new DataConfigToLabel();
                        mapping.setDataConfigId(dataConfigId);
                        mapping.setLabelTypeId(id);
                        newList.add(mapping);
                    }
                }
                fctltmList.removeAll(matchedList);
                dataConfigToLabelBhv.batchInsert(newList, op -> {
                    op.setRefresh(true);
                });
                dataConfigToLabelBhv.batchDelete(fctltmList, op -> {
                    op.setRefresh(true);
                });
            }
            if (roleTypeIds != null) {
                final List<DataConfigToRole> fctrtmList = dataConfigToRoleBhv.selectList(fctrtmCb -> {
                    fctrtmCb.query().setDataConfigId_Equal(dataConfigId);
                });
                final List<DataConfigToRole> newList = new ArrayList<DataConfigToRole>();
                final List<DataConfigToRole> matchedList = new ArrayList<DataConfigToRole>();
                for (final String id : roleTypeIds) {
                    boolean exist = false;
                    for (final DataConfigToRole mapping : fctrtmList) {
                        if (mapping.getRoleTypeId().equals(id)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final DataConfigToRole mapping = new DataConfigToRole();
                        mapping.setDataConfigId(dataConfigId);
                        mapping.setRoleTypeId(id);
                        newList.add(mapping);
                    }
                }
                fctrtmList.removeAll(matchedList);
                dataConfigToRoleBhv.batchInsert(newList, op -> {
                    op.setRefresh(true);
                });
                dataConfigToRoleBhv.batchDelete(fctrtmList, op -> {
                    op.setRefresh(true);
                });
            }
        }
    }

    protected void setupListCondition(final DataConfigCB cb, final DataConfigPager dataConfigPager) {
        if (dataConfigPager.id != null) {
            cb.query().docMeta().setId_Equal(dataConfigPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    protected void setupEntityCondition(final DataConfigCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final DataConfig dataConfig) {

        // setup condition

    }

    protected void setupDeleteCondition(final DataConfig dataConfig) {

        // setup condition

    }

}
