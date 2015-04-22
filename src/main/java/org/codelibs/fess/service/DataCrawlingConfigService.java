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

import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.DataCrawlingConfigCB;
import org.codelibs.fess.db.exbhv.DataConfigToLabelTypeMappingBhv;
import org.codelibs.fess.db.exbhv.DataConfigToRoleTypeMappingBhv;
import org.codelibs.fess.db.exbhv.DataCrawlingConfigBhv;
import org.codelibs.fess.db.exentity.DataConfigToLabelTypeMapping;
import org.codelibs.fess.db.exentity.DataConfigToRoleTypeMapping;
import org.codelibs.fess.db.exentity.DataCrawlingConfig;
import org.codelibs.fess.pager.DataCrawlingConfigPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public class DataCrawlingConfigService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected DataConfigToRoleTypeMappingBhv dataConfigToRoleTypeMappingBhv;

    @Resource
    protected DataConfigToLabelTypeMappingBhv dataConfigToLabelTypeMappingBhv;

    @Resource
    protected DataCrawlingConfigBhv dataCrawlingConfigBhv;

    public DataCrawlingConfigService() {
        super();
    }

    public List<DataCrawlingConfig> getDataCrawlingConfigList(final DataCrawlingConfigPager dataCrawlingConfigPager) {

        final PagingResultBean<DataCrawlingConfig> dataCrawlingConfigList = dataCrawlingConfigBhv.selectPage(cb -> {
            cb.paging(dataCrawlingConfigPager.getPageSize(), dataCrawlingConfigPager.getCurrentPageNumber());

            setupListCondition(cb, dataCrawlingConfigPager);
        });

        // update pager
        Beans.copy(dataCrawlingConfigList, dataCrawlingConfigPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        dataCrawlingConfigPager.setPageNumberList(dataCrawlingConfigList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return dataCrawlingConfigList;
    }

    public void delete(final DataCrawlingConfig dataCrawlingConfig) throws CrudMessageException {
        setupDeleteCondition(dataCrawlingConfig);

        dataCrawlingConfigBhv.delete(dataCrawlingConfig);

    }

    public List<DataCrawlingConfig> getAllDataCrawlingConfigList() {
        return getAllDataCrawlingConfigList(true, true, true, null);
    }

    public List<DataCrawlingConfig> getDataCrawlingConfigListByIds(final List<Long> idList) {
        if (idList == null) {
            return getAllDataCrawlingConfigList();
        } else {
            return getAllDataCrawlingConfigList(true, true, false, idList);
        }
    }

    public List<DataCrawlingConfig> getAllDataCrawlingConfigList(final boolean withLabelType, final boolean withRoleType,
            final boolean available, final List<Long> idList) {
        final List<DataCrawlingConfig> list = dataCrawlingConfigBhv.selectList(cb -> {
            cb.query().setDeletedBy_IsNull();
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
        });
        if (withRoleType) {
            dataCrawlingConfigBhv.loadDataConfigToRoleTypeMapping(list, cb -> {
                cb.setupSelect_RoleType();
                cb.query().queryRoleType().setDeletedBy_IsNull();
                cb.query().queryRoleType().addOrderBy_SortOrder_Asc();
            });
        }
        if (withLabelType) {
            dataCrawlingConfigBhv.loadDataConfigToLabelTypeMapping(list, cb -> {
                cb.setupSelect_LabelType();
                cb.query().queryLabelType().setDeletedBy_IsNull();
                cb.query().queryLabelType().addOrderBy_SortOrder_Asc();
            });
        }
        return list;
    }

    public DataCrawlingConfig getDataCrawlingConfig(final Map<String, String> keys) {

        final DataCrawlingConfig dataCrawlingConfig = dataCrawlingConfigBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO

        if (dataCrawlingConfig != null) {

            final List<DataConfigToRoleTypeMapping> fctrtmList = dataConfigToRoleTypeMappingBhv.selectList(fctrtmCb -> {
                fctrtmCb.query().setDataConfigId_Equal(dataCrawlingConfig.getId());
                fctrtmCb.query().queryRoleType().setDeletedBy_IsNull();
                fctrtmCb.query().queryDataCrawlingConfig().setDeletedBy_IsNull();
            });
            if (!fctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(fctrtmList.size());
                for (final DataConfigToRoleTypeMapping mapping : fctrtmList) {
                    roleTypeIds.add(Long.toString(mapping.getRoleTypeId()));
                }
                dataCrawlingConfig.setRoleTypeIds(roleTypeIds.toArray(new String[roleTypeIds.size()]));
            }

            final List<DataConfigToLabelTypeMapping> fctltmList = dataConfigToLabelTypeMappingBhv.selectList(fctltmCb -> {
                fctltmCb.query().setDataConfigId_Equal(dataCrawlingConfig.getId());
                fctltmCb.query().queryLabelType().setDeletedBy_IsNull();
                fctltmCb.query().queryDataCrawlingConfig().setDeletedBy_IsNull();
            });
            if (!fctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(fctltmList.size());
                for (final DataConfigToLabelTypeMapping mapping : fctltmList) {
                    labelTypeIds.add(Long.toString(mapping.getLabelTypeId()));
                }
                dataCrawlingConfig.setLabelTypeIds(labelTypeIds.toArray(new String[labelTypeIds.size()]));
            }

        }

        return dataCrawlingConfig;
    }

    public void store(final DataCrawlingConfig dataCrawlingConfig) {
        final boolean isNew = dataCrawlingConfig.getId() == null;
        final String[] labelTypeIds = dataCrawlingConfig.getLabelTypeIds();
        final String[] roleTypeIds = dataCrawlingConfig.getRoleTypeIds();
        setupStoreCondition(dataCrawlingConfig);

        dataCrawlingConfigBhv.insertOrUpdate(dataCrawlingConfig);
        final Long dataConfigId = dataCrawlingConfig.getId();
        if (isNew) {
            // Insert
            if (labelTypeIds != null) {
                final List<DataConfigToLabelTypeMapping> fctltmList = new ArrayList<DataConfigToLabelTypeMapping>();
                for (final String labelTypeId : labelTypeIds) {
                    final DataConfigToLabelTypeMapping mapping = new DataConfigToLabelTypeMapping();
                    mapping.setDataConfigId(dataConfigId);
                    mapping.setLabelTypeId(Long.parseLong(labelTypeId));
                    fctltmList.add(mapping);
                }
                dataConfigToLabelTypeMappingBhv.batchInsert(fctltmList);
            }
            if (roleTypeIds != null) {
                final List<DataConfigToRoleTypeMapping> fctrtmList = new ArrayList<DataConfigToRoleTypeMapping>();
                for (final String roleTypeId : roleTypeIds) {
                    final DataConfigToRoleTypeMapping mapping = new DataConfigToRoleTypeMapping();
                    mapping.setDataConfigId(dataConfigId);
                    mapping.setRoleTypeId(Long.parseLong(roleTypeId));
                    fctrtmList.add(mapping);
                }
                dataConfigToRoleTypeMappingBhv.batchInsert(fctrtmList);
            }
        } else {
            // Update
            if (labelTypeIds != null) {
                final List<DataConfigToLabelTypeMapping> fctltmList = dataConfigToLabelTypeMappingBhv.selectList(fctltmCb -> {
                    fctltmCb.query().setDataConfigId_Equal(dataConfigId);
                });
                final List<DataConfigToLabelTypeMapping> newList = new ArrayList<DataConfigToLabelTypeMapping>();
                final List<DataConfigToLabelTypeMapping> matchedList = new ArrayList<DataConfigToLabelTypeMapping>();
                for (final String id : labelTypeIds) {
                    final Long labelTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final DataConfigToLabelTypeMapping mapping : fctltmList) {
                        if (mapping.getLabelTypeId().equals(labelTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final DataConfigToLabelTypeMapping mapping = new DataConfigToLabelTypeMapping();
                        mapping.setDataConfigId(dataConfigId);
                        mapping.setLabelTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                fctltmList.removeAll(matchedList);
                dataConfigToLabelTypeMappingBhv.batchInsert(newList);
                dataConfigToLabelTypeMappingBhv.batchDelete(fctltmList);
            }
            if (roleTypeIds != null) {
                final List<DataConfigToRoleTypeMapping> fctrtmList = dataConfigToRoleTypeMappingBhv.selectList(fctrtmCb -> {
                    fctrtmCb.query().setDataConfigId_Equal(dataConfigId);
                });
                final List<DataConfigToRoleTypeMapping> newList = new ArrayList<DataConfigToRoleTypeMapping>();
                final List<DataConfigToRoleTypeMapping> matchedList = new ArrayList<DataConfigToRoleTypeMapping>();
                for (final String id : roleTypeIds) {
                    final Long roleTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final DataConfigToRoleTypeMapping mapping : fctrtmList) {
                        if (mapping.getRoleTypeId().equals(roleTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final DataConfigToRoleTypeMapping mapping = new DataConfigToRoleTypeMapping();
                        mapping.setDataConfigId(dataConfigId);
                        mapping.setRoleTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                fctrtmList.removeAll(matchedList);
                dataConfigToRoleTypeMappingBhv.batchInsert(newList);
                dataConfigToRoleTypeMappingBhv.batchDelete(fctrtmList);
            }
        }
    }

    protected void setupListCondition(final DataCrawlingConfigCB cb, final DataCrawlingConfigPager dataCrawlingConfigPager) {
        if (dataCrawlingConfigPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(dataCrawlingConfigPager.id));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    protected void setupEntityCondition(final DataCrawlingConfigCB cb, final Map<String, String> keys) {

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    protected void setupStoreCondition(final DataCrawlingConfig dataCrawlingConfig) {

        // setup condition

    }

    protected void setupDeleteCondition(final DataCrawlingConfig dataCrawlingConfig) {

        // setup condition

    }

    public DataCrawlingConfig getDataCrawlingConfig(final long id) {
        return dataCrawlingConfigBhv.selectEntity(cb -> {
            cb.query().setId_Equal(id);
            cb.query().setDeletedBy_IsNull();
        }).orElse(null);//TODO
    }

}
