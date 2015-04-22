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
import org.codelibs.fess.db.cbean.FileCrawlingConfigCB;
import org.codelibs.fess.db.exbhv.FileConfigToLabelTypeMappingBhv;
import org.codelibs.fess.db.exbhv.FileConfigToRoleTypeMappingBhv;
import org.codelibs.fess.db.exbhv.FileCrawlingConfigBhv;
import org.codelibs.fess.db.exentity.FileConfigToLabelTypeMapping;
import org.codelibs.fess.db.exentity.FileConfigToRoleTypeMapping;
import org.codelibs.fess.db.exentity.FileCrawlingConfig;
import org.codelibs.fess.pager.FileCrawlingConfigPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public class FileCrawlingConfigService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected FileConfigToRoleTypeMappingBhv fileConfigToRoleTypeMappingBhv;

    @Resource
    protected FileConfigToLabelTypeMappingBhv fileConfigToLabelTypeMappingBhv;

    @Resource
    protected FileCrawlingConfigBhv fileCrawlingConfigBhv;

    public FileCrawlingConfigService() {
        super();
    }

    public List<FileCrawlingConfig> getFileCrawlingConfigList(final FileCrawlingConfigPager fileCrawlingConfigPager) {

        final PagingResultBean<FileCrawlingConfig> fileCrawlingConfigList = fileCrawlingConfigBhv.selectPage(cb -> {
            cb.paging(fileCrawlingConfigPager.getPageSize(), fileCrawlingConfigPager.getCurrentPageNumber());
            setupListCondition(cb, fileCrawlingConfigPager);
        });

        // update pager
        Beans.copy(fileCrawlingConfigList, fileCrawlingConfigPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        fileCrawlingConfigPager.setPageNumberList(fileCrawlingConfigList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return fileCrawlingConfigList;
    }

    public void delete(final FileCrawlingConfig fileCrawlingConfig) throws CrudMessageException {
        setupDeleteCondition(fileCrawlingConfig);

        fileCrawlingConfigBhv.delete(fileCrawlingConfig);

    }

    public List<FileCrawlingConfig> getAllFileCrawlingConfigList() {
        return getAllFileCrawlingConfigList(true, true, true, null);
    }

    public List<FileCrawlingConfig> getFileCrawlingConfigListByIds(final List<Long> idList) {
        if (idList == null) {
            return getAllFileCrawlingConfigList();
        } else {
            return getAllFileCrawlingConfigList(true, true, false, idList);
        }
    }

    public List<FileCrawlingConfig> getAllFileCrawlingConfigList(final boolean withLabelType, final boolean withRoleType,
            final boolean available, final List<Long> idList) {
        final List<FileCrawlingConfig> list = fileCrawlingConfigBhv.selectList(cb -> {
            cb.query().setDeletedBy_IsNull();
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
        });
        if (withRoleType) {
            fileCrawlingConfigBhv.loadFileConfigToRoleTypeMapping(list, cb -> {
                cb.setupSelect_RoleType();
                cb.query().queryRoleType().setDeletedBy_IsNull();
                cb.query().queryRoleType().addOrderBy_SortOrder_Asc();
            });
        }
        if (withLabelType) {
            fileCrawlingConfigBhv.loadFileConfigToLabelTypeMapping(list, cb -> {
                cb.setupSelect_LabelType();
                cb.query().queryLabelType().setDeletedBy_IsNull();
                cb.query().queryLabelType().addOrderBy_SortOrder_Asc();
            });
        }
        return list;
    }

    public FileCrawlingConfig getFileCrawlingConfig(final Map<String, String> keys) {
        final FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO

        if (fileCrawlingConfig != null) {

            final List<FileConfigToRoleTypeMapping> fctrtmList = fileConfigToRoleTypeMappingBhv.selectList(fctrtmCb -> {
                fctrtmCb.query().setFileConfigId_Equal(fileCrawlingConfig.getId());
                fctrtmCb.query().queryRoleType().setDeletedBy_IsNull();
                fctrtmCb.query().queryFileCrawlingConfig().setDeletedBy_IsNull();
            });
            if (!fctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(fctrtmList.size());
                for (final FileConfigToRoleTypeMapping mapping : fctrtmList) {
                    roleTypeIds.add(Long.toString(mapping.getRoleTypeId()));
                }
                fileCrawlingConfig.setRoleTypeIds(roleTypeIds.toArray(new String[roleTypeIds.size()]));
            }

            final List<FileConfigToLabelTypeMapping> fctltmList = fileConfigToLabelTypeMappingBhv.selectList(fctltmCb -> {
                fctltmCb.query().setFileConfigId_Equal(fileCrawlingConfig.getId());
                fctltmCb.query().queryLabelType().setDeletedBy_IsNull();
                fctltmCb.query().queryFileCrawlingConfig().setDeletedBy_IsNull();
            });
            if (!fctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(fctltmList.size());
                for (final FileConfigToLabelTypeMapping mapping : fctltmList) {
                    labelTypeIds.add(Long.toString(mapping.getLabelTypeId()));
                }
                fileCrawlingConfig.setLabelTypeIds(labelTypeIds.toArray(new String[labelTypeIds.size()]));
            }

        }

        return fileCrawlingConfig;
    }

    public void store(final FileCrawlingConfig fileCrawlingConfig) {
        final boolean isNew = fileCrawlingConfig.getId() == null;
        final String[] labelTypeIds = fileCrawlingConfig.getLabelTypeIds();
        final String[] roleTypeIds = fileCrawlingConfig.getRoleTypeIds();
        setupStoreCondition(fileCrawlingConfig);

        fileCrawlingConfigBhv.insertOrUpdate(fileCrawlingConfig);
        final Long fileConfigId = fileCrawlingConfig.getId();
        if (isNew) {
            // Insert
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
            if (labelTypeIds != null) {
                final List<FileConfigToLabelTypeMapping> fctltmList = fileConfigToLabelTypeMappingBhv.selectList(fctltmCb -> {
                    fctltmCb.query().setFileConfigId_Equal(fileConfigId);
                });
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
                final List<FileConfigToRoleTypeMapping> fctrtmList = fileConfigToRoleTypeMappingBhv.selectList(fctrtmCb -> {
                    fctrtmCb.query().setFileConfigId_Equal(fileConfigId);
                });
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

    protected void setupListCondition(final FileCrawlingConfigCB cb, final FileCrawlingConfigPager fileCrawlingConfigPager) {
        if (fileCrawlingConfigPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(fileCrawlingConfigPager.id));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    protected void setupEntityCondition(final FileCrawlingConfigCB cb, final Map<String, String> keys) {

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    protected void setupStoreCondition(final FileCrawlingConfig fileCrawlingConfig) {

        // setup condition

    }

    protected void setupDeleteCondition(final FileCrawlingConfig fileCrawlingConfig) {

        // setup condition

    }

    public FileCrawlingConfig getFileCrawlingConfig(final long id) {
        return fileCrawlingConfigBhv.selectEntity(cb -> {
            cb.query().setId_Equal(id);
            cb.query().setDeletedBy_IsNull();
        }).orElse(null);//TODO
    }

}
