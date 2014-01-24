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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.service.BsDataCrawlingConfigService;
import jp.sf.fess.db.cbean.DataConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.DataCrawlingConfigCB;
import jp.sf.fess.db.exbhv.DataConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exbhv.DataConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.DataConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.DataConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.pager.DataCrawlingConfigPager;

import org.seasar.dbflute.bhv.ConditionBeanSetupper;

public class DataCrawlingConfigService extends BsDataCrawlingConfigService
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected DataConfigToBrowserTypeMappingBhv dataConfigToBrowserTypeMappingBhv;

    @Resource
    protected DataConfigToRoleTypeMappingBhv dataConfigToRoleTypeMappingBhv;

    @Resource
    protected DataConfigToLabelTypeMappingBhv dataConfigToLabelTypeMappingBhv;

    public List<DataCrawlingConfig> getAllDataCrawlingConfigList() {
        return getAllDataCrawlingConfigList(true, true, true, true, null);
    }

    public List<DataCrawlingConfig> getDataCrawlingConfigListByIds(
            final List<Long> idList) {
        if (idList == null) {
            return getAllDataCrawlingConfigList();
        } else {
            return getAllDataCrawlingConfigList(true, true, true, false, idList);
        }
    }

    public List<DataCrawlingConfig> getAllDataCrawlingConfigList(
            final boolean withBrowserType, final boolean withLabelType,
            final boolean withRoleType, final boolean available,
            final List<Long> idList) {
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.query().setDeletedBy_IsNull();
        if (available) {
            cb.query().setAvailable_Equal(Constants.T);
        }
        if (idList != null) {
            cb.query().setId_InScope(idList);
        }
        final List<DataCrawlingConfig> list = dataCrawlingConfigBhv
                .selectList(cb);
        if (withBrowserType) {
            final ConditionBeanSetupper<DataConfigToBrowserTypeMappingCB> setupper1 = new ConditionBeanSetupper<DataConfigToBrowserTypeMappingCB>() {
                @Override
                public void setup(final DataConfigToBrowserTypeMappingCB cb) {
                    cb.setupSelect_BrowserType();
                    cb.query().queryBrowserType().setDeletedBy_IsNull();
                    cb.query().queryBrowserType().addOrderBy_SortOrder_Asc();
                }

            };
            dataCrawlingConfigBhv.loadDataConfigToBrowserTypeMappingList(list,
                    setupper1);
        }
        if (withBrowserType) {
            final ConditionBeanSetupper<DataConfigToRoleTypeMappingCB> setupper2 = new ConditionBeanSetupper<DataConfigToRoleTypeMappingCB>() {
                @Override
                public void setup(final DataConfigToRoleTypeMappingCB cb) {
                    cb.setupSelect_RoleType();
                    cb.query().queryRoleType().setDeletedBy_IsNull();
                    cb.query().queryRoleType().addOrderBy_SortOrder_Asc();
                }

            };
            dataCrawlingConfigBhv.loadDataConfigToRoleTypeMappingList(list,
                    setupper2);
        }
        if (withRoleType) {
            final ConditionBeanSetupper<DataConfigToLabelTypeMappingCB> setupper3 = new ConditionBeanSetupper<DataConfigToLabelTypeMappingCB>() {
                @Override
                public void setup(final DataConfigToLabelTypeMappingCB cb) {
                    cb.setupSelect_LabelType();
                    cb.query().queryLabelType().setDeletedBy_IsNull();
                    cb.query().queryLabelType().addOrderBy_SortOrder_Asc();
                }

            };
            dataCrawlingConfigBhv.loadDataConfigToLabelTypeMappingList(list,
                    setupper3);
        }
        return list;
    }

    @Override
    public DataCrawlingConfig getDataCrawlingConfig(
            final Map<String, String> keys) {
        final DataCrawlingConfig dataCrawlingConfig = super
                .getDataCrawlingConfig(keys);

        if (dataCrawlingConfig != null) {
            final DataConfigToBrowserTypeMappingCB fctbtmCb = new DataConfigToBrowserTypeMappingCB();
            fctbtmCb.query().setDataConfigId_Equal(dataCrawlingConfig.getId());
            fctbtmCb.query().queryBrowserType().setDeletedBy_IsNull();
            fctbtmCb.query().queryDataCrawlingConfig().setDeletedBy_IsNull();
            final List<DataConfigToBrowserTypeMapping> fctbtmList = dataConfigToBrowserTypeMappingBhv
                    .selectList(fctbtmCb);
            if (!fctbtmList.isEmpty()) {
                final List<String> browserTypeIds = new ArrayList<String>(
                        fctbtmList.size());
                for (final DataConfigToBrowserTypeMapping mapping : fctbtmList) {
                    browserTypeIds
                            .add(Long.toString(mapping.getBrowserTypeId()));
                }
                dataCrawlingConfig.setBrowserTypeIds(browserTypeIds
                        .toArray(new String[browserTypeIds.size()]));
            }

            final DataConfigToRoleTypeMappingCB fctrtmCb = new DataConfigToRoleTypeMappingCB();
            fctrtmCb.query().setDataConfigId_Equal(dataCrawlingConfig.getId());
            fctrtmCb.query().queryRoleType().setDeletedBy_IsNull();
            fctrtmCb.query().queryDataCrawlingConfig().setDeletedBy_IsNull();
            final List<DataConfigToRoleTypeMapping> fctrtmList = dataConfigToRoleTypeMappingBhv
                    .selectList(fctrtmCb);
            if (!fctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(
                        fctrtmList.size());
                for (final DataConfigToRoleTypeMapping mapping : fctrtmList) {
                    roleTypeIds.add(Long.toString(mapping.getRoleTypeId()));
                }
                dataCrawlingConfig.setRoleTypeIds(roleTypeIds
                        .toArray(new String[roleTypeIds.size()]));
            }

            final DataConfigToLabelTypeMappingCB fctltmCb = new DataConfigToLabelTypeMappingCB();
            fctltmCb.query().setDataConfigId_Equal(dataCrawlingConfig.getId());
            fctltmCb.query().queryLabelType().setDeletedBy_IsNull();
            fctltmCb.query().queryDataCrawlingConfig().setDeletedBy_IsNull();
            final List<DataConfigToLabelTypeMapping> fctltmList = dataConfigToLabelTypeMappingBhv
                    .selectList(fctltmCb);
            if (!fctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(
                        fctltmList.size());
                for (final DataConfigToLabelTypeMapping mapping : fctltmList) {
                    labelTypeIds.add(Long.toString(mapping.getLabelTypeId()));
                }
                dataCrawlingConfig.setLabelTypeIds(labelTypeIds
                        .toArray(new String[labelTypeIds.size()]));
            }

        }

        return dataCrawlingConfig;
    }

    @Override
    public void store(final DataCrawlingConfig dataCrawlingConfig) {
        final boolean isNew = dataCrawlingConfig.getId() == null;
        final String[] browserTypeIds = dataCrawlingConfig.getBrowserTypeIds();
        final String[] labelTypeIds = dataCrawlingConfig.getLabelTypeIds();
        final String[] roleTypeIds = dataCrawlingConfig.getRoleTypeIds();
        super.store(dataCrawlingConfig);
        final Long dataConfigId = dataCrawlingConfig.getId();
        if (isNew) {
            // Insert
            if (browserTypeIds != null) {
                final List<DataConfigToBrowserTypeMapping> fctbtmList = new ArrayList<DataConfigToBrowserTypeMapping>();
                for (final String browserTypeId : browserTypeIds) {
                    final DataConfigToBrowserTypeMapping mapping = new DataConfigToBrowserTypeMapping();
                    mapping.setDataConfigId(dataConfigId);
                    mapping.setBrowserTypeId(Long.parseLong(browserTypeId));
                    fctbtmList.add(mapping);
                }
                dataConfigToBrowserTypeMappingBhv.batchInsert(fctbtmList);
            }
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
            if (browserTypeIds != null) {
                final DataConfigToBrowserTypeMappingCB fctbtmCb = new DataConfigToBrowserTypeMappingCB();
                fctbtmCb.query().setDataConfigId_Equal(dataConfigId);
                final List<DataConfigToBrowserTypeMapping> fctbtmList = dataConfigToBrowserTypeMappingBhv
                        .selectList(fctbtmCb);
                final List<DataConfigToBrowserTypeMapping> newList = new ArrayList<DataConfigToBrowserTypeMapping>();
                final List<DataConfigToBrowserTypeMapping> matchedList = new ArrayList<DataConfigToBrowserTypeMapping>();
                for (final String id : browserTypeIds) {
                    final Long browserTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final DataConfigToBrowserTypeMapping mapping : fctbtmList) {
                        if (mapping.getBrowserTypeId().equals(browserTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final DataConfigToBrowserTypeMapping mapping = new DataConfigToBrowserTypeMapping();
                        mapping.setDataConfigId(dataConfigId);
                        mapping.setBrowserTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                fctbtmList.removeAll(matchedList);
                dataConfigToBrowserTypeMappingBhv.batchInsert(newList);
                dataConfigToBrowserTypeMappingBhv.batchDelete(fctbtmList);
            }
            if (labelTypeIds != null) {
                final DataConfigToLabelTypeMappingCB fctltmCb = new DataConfigToLabelTypeMappingCB();
                fctltmCb.query().setDataConfigId_Equal(dataConfigId);
                final List<DataConfigToLabelTypeMapping> fctltmList = dataConfigToLabelTypeMappingBhv
                        .selectList(fctltmCb);
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
                final DataConfigToRoleTypeMappingCB fctrtmCb = new DataConfigToRoleTypeMappingCB();
                fctrtmCb.query().setDataConfigId_Equal(dataConfigId);
                final List<DataConfigToRoleTypeMapping> fctrtmList = dataConfigToRoleTypeMappingBhv
                        .selectList(fctrtmCb);
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

    @Override
    protected void setupListCondition(final DataCrawlingConfigCB cb,
            final DataCrawlingConfigPager dataCrawlingConfigPager) {
        super.setupListCondition(cb, dataCrawlingConfigPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final DataCrawlingConfigCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(
            final DataCrawlingConfig dataCrawlingConfig) {
        super.setupStoreCondition(dataCrawlingConfig);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(
            final DataCrawlingConfig dataCrawlingConfig) {
        super.setupDeleteCondition(dataCrawlingConfig);

        // setup condition

    }

    public DataCrawlingConfig getDataCrawlingConfig(final long id) {
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.query().setId_Equal(id);
        cb.query().setDeletedBy_IsNull();
        return dataCrawlingConfigBhv.selectEntity(cb);
    }

}
