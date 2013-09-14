/*
 * Copyright 2009-2013 the Fess Project and the Others.
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
import jp.sf.fess.crud.service.BsFileCrawlingConfigService;
import jp.sf.fess.db.cbean.FileConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.exbhv.FileConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.FileConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.pager.FileCrawlingConfigPager;

import org.seasar.dbflute.bhv.ConditionBeanSetupper;

public class FileCrawlingConfigService extends BsFileCrawlingConfigService
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected FileConfigToBrowserTypeMappingBhv fileConfigToBrowserTypeMappingBhv;

    @Resource
    protected FileConfigToRoleTypeMappingBhv fileConfigToRoleTypeMappingBhv;

    @Resource
    protected FileConfigToLabelTypeMappingBhv fileConfigToLabelTypeMappingBhv;

    public List<FileCrawlingConfig> getAllFileCrawlingConfigList() {
        return getAllFileCrawlingConfigList(true, true, true, true, null);
    }

    public List<FileCrawlingConfig> getFileCrawlingConfigListByIds(
            final List<Long> idList) {
        if (idList == null) {
            return getAllFileCrawlingConfigList();
        } else {
            return getAllFileCrawlingConfigList(true, true, true, false, idList);
        }
    }

    public List<FileCrawlingConfig> getAllFileCrawlingConfigList(
            final boolean withBrowserType, final boolean withLabelType,
            final boolean withRoleType, final boolean available,
            final List<Long> idList) {
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
        cb.query().setDeletedBy_IsNull();
        if (available) {
            cb.query().setAvailable_Equal(Constants.T);
        }
        if (idList != null) {
            cb.query().setId_InScope(idList);
        }
        final List<FileCrawlingConfig> list = fileCrawlingConfigBhv
                .selectList(cb);
        if (withBrowserType) {
            final ConditionBeanSetupper<FileConfigToBrowserTypeMappingCB> setupper1 = new ConditionBeanSetupper<FileConfigToBrowserTypeMappingCB>() {
                @Override
                public void setup(final FileConfigToBrowserTypeMappingCB cb) {
                    cb.setupSelect_BrowserType();
                    cb.query().queryBrowserType().setDeletedBy_IsNull();
                    cb.query().queryBrowserType().addOrderBy_SortOrder_Asc();
                }

            };
            fileCrawlingConfigBhv.loadFileConfigToBrowserTypeMappingList(list,
                    setupper1);
        }
        if (withBrowserType) {
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> setupper2 = new ConditionBeanSetupper<FileConfigToRoleTypeMappingCB>() {
                @Override
                public void setup(final FileConfigToRoleTypeMappingCB cb) {
                    cb.setupSelect_RoleType();
                    cb.query().queryRoleType().setDeletedBy_IsNull();
                    cb.query().queryRoleType().addOrderBy_SortOrder_Asc();
                }

            };
            fileCrawlingConfigBhv.loadFileConfigToRoleTypeMappingList(list,
                    setupper2);
        }
        if (withRoleType) {
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> setupper3 = new ConditionBeanSetupper<FileConfigToLabelTypeMappingCB>() {
                @Override
                public void setup(final FileConfigToLabelTypeMappingCB cb) {
                    cb.setupSelect_LabelType();
                    cb.query().queryLabelType().setDeletedBy_IsNull();
                    cb.query().queryLabelType().addOrderBy_SortOrder_Asc();
                }

            };
            fileCrawlingConfigBhv.loadFileConfigToLabelTypeMappingList(list,
                    setupper3);
        }
        return list;
    }

    @Override
    public FileCrawlingConfig getFileCrawlingConfig(
            final Map<String, String> keys) {
        final FileCrawlingConfig fileCrawlingConfig = super
                .getFileCrawlingConfig(keys);

        if (fileCrawlingConfig != null) {
            final FileConfigToBrowserTypeMappingCB fctbtmCb = new FileConfigToBrowserTypeMappingCB();
            fctbtmCb.query().setFileConfigId_Equal(fileCrawlingConfig.getId());
            fctbtmCb.query().queryBrowserType().setDeletedBy_IsNull();
            fctbtmCb.query().queryFileCrawlingConfig().setDeletedBy_IsNull();
            final List<FileConfigToBrowserTypeMapping> fctbtmList = fileConfigToBrowserTypeMappingBhv
                    .selectList(fctbtmCb);
            if (!fctbtmList.isEmpty()) {
                final List<String> browserTypeIds = new ArrayList<String>(
                        fctbtmList.size());
                for (final FileConfigToBrowserTypeMapping mapping : fctbtmList) {
                    browserTypeIds
                            .add(Long.toString(mapping.getBrowserTypeId()));
                }
                fileCrawlingConfig.setBrowserTypeIds(browserTypeIds
                        .toArray(new String[browserTypeIds.size()]));
            }

            final FileConfigToRoleTypeMappingCB fctrtmCb = new FileConfigToRoleTypeMappingCB();
            fctrtmCb.query().setFileConfigId_Equal(fileCrawlingConfig.getId());
            fctrtmCb.query().queryRoleType().setDeletedBy_IsNull();
            fctrtmCb.query().queryFileCrawlingConfig().setDeletedBy_IsNull();
            final List<FileConfigToRoleTypeMapping> fctrtmList = fileConfigToRoleTypeMappingBhv
                    .selectList(fctrtmCb);
            if (!fctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(
                        fctrtmList.size());
                for (final FileConfigToRoleTypeMapping mapping : fctrtmList) {
                    roleTypeIds.add(Long.toString(mapping.getRoleTypeId()));
                }
                fileCrawlingConfig.setRoleTypeIds(roleTypeIds
                        .toArray(new String[roleTypeIds.size()]));
            }

            final FileConfigToLabelTypeMappingCB fctltmCb = new FileConfigToLabelTypeMappingCB();
            fctltmCb.query().setFileConfigId_Equal(fileCrawlingConfig.getId());
            fctltmCb.query().queryLabelType().setDeletedBy_IsNull();
            fctltmCb.query().queryFileCrawlingConfig().setDeletedBy_IsNull();
            final List<FileConfigToLabelTypeMapping> fctltmList = fileConfigToLabelTypeMappingBhv
                    .selectList(fctltmCb);
            if (!fctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(
                        fctltmList.size());
                for (final FileConfigToLabelTypeMapping mapping : fctltmList) {
                    labelTypeIds.add(Long.toString(mapping.getLabelTypeId()));
                }
                fileCrawlingConfig.setLabelTypeIds(labelTypeIds
                        .toArray(new String[labelTypeIds.size()]));
            }

        }

        return fileCrawlingConfig;
    }

    @Override
    public void store(final FileCrawlingConfig fileCrawlingConfig) {
        final boolean isNew = fileCrawlingConfig.getId() == null;
        final String[] browserTypeIds = fileCrawlingConfig.getBrowserTypeIds();
        final String[] labelTypeIds = fileCrawlingConfig.getLabelTypeIds();
        final String[] roleTypeIds = fileCrawlingConfig.getRoleTypeIds();
        super.store(fileCrawlingConfig);
        final Long fileConfigId = fileCrawlingConfig.getId();
        if (isNew) {
            // Insert
            if (browserTypeIds != null) {
                final List<FileConfigToBrowserTypeMapping> fctbtmList = new ArrayList<FileConfigToBrowserTypeMapping>();
                for (final String browserTypeId : browserTypeIds) {
                    final FileConfigToBrowserTypeMapping mapping = new FileConfigToBrowserTypeMapping();
                    mapping.setFileConfigId(fileConfigId);
                    mapping.setBrowserTypeId(Long.parseLong(browserTypeId));
                    fctbtmList.add(mapping);
                }
                fileConfigToBrowserTypeMappingBhv.batchInsert(fctbtmList);
            }
            if (labelTypeIds != null) {
                final List<FileConfigToLabelTypeMapping> fctltmList = new ArrayList<FileConfigToLabelTypeMapping>();
                for (final String labelTypeId : labelTypeIds) {
                    final FileConfigToLabelTypeMapping mapping = new FileConfigToLabelTypeMapping();
                    mapping.setFileConfigId(fileConfigId);
                    mapping.setLabelTypeId(Long.parseLong(labelTypeId));
                    fctltmList.add(mapping);
                }
                fileConfigToLabelTypeMappingBhv.batchInsert(fctltmList);
            }
            if (roleTypeIds != null) {
                final List<FileConfigToRoleTypeMapping> fctrtmList = new ArrayList<FileConfigToRoleTypeMapping>();
                for (final String roleTypeId : roleTypeIds) {
                    final FileConfigToRoleTypeMapping mapping = new FileConfigToRoleTypeMapping();
                    mapping.setFileConfigId(fileConfigId);
                    mapping.setRoleTypeId(Long.parseLong(roleTypeId));
                    fctrtmList.add(mapping);
                }
                fileConfigToRoleTypeMappingBhv.batchInsert(fctrtmList);
            }
        } else {
            // Update
            if (browserTypeIds != null) {
                final FileConfigToBrowserTypeMappingCB fctbtmCb = new FileConfigToBrowserTypeMappingCB();
                fctbtmCb.query().setFileConfigId_Equal(fileConfigId);
                final List<FileConfigToBrowserTypeMapping> fctbtmList = fileConfigToBrowserTypeMappingBhv
                        .selectList(fctbtmCb);
                final List<FileConfigToBrowserTypeMapping> newList = new ArrayList<FileConfigToBrowserTypeMapping>();
                final List<FileConfigToBrowserTypeMapping> matchedList = new ArrayList<FileConfigToBrowserTypeMapping>();
                for (final String id : browserTypeIds) {
                    final Long browserTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final FileConfigToBrowserTypeMapping mapping : fctbtmList) {
                        if (mapping.getBrowserTypeId().equals(browserTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final FileConfigToBrowserTypeMapping mapping = new FileConfigToBrowserTypeMapping();
                        mapping.setFileConfigId(fileConfigId);
                        mapping.setBrowserTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                fctbtmList.removeAll(matchedList);
                fileConfigToBrowserTypeMappingBhv.batchInsert(newList);
                fileConfigToBrowserTypeMappingBhv.batchDelete(fctbtmList);
            }
            if (labelTypeIds != null) {
                final FileConfigToLabelTypeMappingCB fctltmCb = new FileConfigToLabelTypeMappingCB();
                fctltmCb.query().setFileConfigId_Equal(fileConfigId);
                final List<FileConfigToLabelTypeMapping> fctltmList = fileConfigToLabelTypeMappingBhv
                        .selectList(fctltmCb);
                final List<FileConfigToLabelTypeMapping> newList = new ArrayList<FileConfigToLabelTypeMapping>();
                final List<FileConfigToLabelTypeMapping> matchedList = new ArrayList<FileConfigToLabelTypeMapping>();
                for (final String id : labelTypeIds) {
                    final Long labelTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final FileConfigToLabelTypeMapping mapping : fctltmList) {
                        if (mapping.getLabelTypeId().equals(labelTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final FileConfigToLabelTypeMapping mapping = new FileConfigToLabelTypeMapping();
                        mapping.setFileConfigId(fileConfigId);
                        mapping.setLabelTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                fctltmList.removeAll(matchedList);
                fileConfigToLabelTypeMappingBhv.batchInsert(newList);
                fileConfigToLabelTypeMappingBhv.batchDelete(fctltmList);
            }
            if (roleTypeIds != null) {
                final FileConfigToRoleTypeMappingCB fctrtmCb = new FileConfigToRoleTypeMappingCB();
                fctrtmCb.query().setFileConfigId_Equal(fileConfigId);
                final List<FileConfigToRoleTypeMapping> fctrtmList = fileConfigToRoleTypeMappingBhv
                        .selectList(fctrtmCb);
                final List<FileConfigToRoleTypeMapping> newList = new ArrayList<FileConfigToRoleTypeMapping>();
                final List<FileConfigToRoleTypeMapping> matchedList = new ArrayList<FileConfigToRoleTypeMapping>();
                for (final String id : roleTypeIds) {
                    final Long roleTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final FileConfigToRoleTypeMapping mapping : fctrtmList) {
                        if (mapping.getRoleTypeId().equals(roleTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final FileConfigToRoleTypeMapping mapping = new FileConfigToRoleTypeMapping();
                        mapping.setFileConfigId(fileConfigId);
                        mapping.setRoleTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                fctrtmList.removeAll(matchedList);
                fileConfigToRoleTypeMappingBhv.batchInsert(newList);
                fileConfigToRoleTypeMappingBhv.batchDelete(fctrtmList);
            }
        }
    }

    @Override
    protected void setupListCondition(final FileCrawlingConfigCB cb,
            final FileCrawlingConfigPager fileCrawlingConfigPager) {
        super.setupListCondition(cb, fileCrawlingConfigPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final FileCrawlingConfigCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(
            final FileCrawlingConfig fileCrawlingConfig) {
        super.setupStoreCondition(fileCrawlingConfig);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(
            final FileCrawlingConfig fileCrawlingConfig) {
        super.setupDeleteCondition(fileCrawlingConfig);

        // setup condition

    }

}
