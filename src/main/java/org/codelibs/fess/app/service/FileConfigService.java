/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import org.codelibs.fess.app.pager.FileConfigPager;
import org.codelibs.fess.es.config.cbean.FileConfigCB;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigToLabelBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigToRoleBhv;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.es.config.exentity.FileConfigToLabel;
import org.codelibs.fess.es.config.exentity.FileConfigToRole;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class FileConfigService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected FileConfigToRoleBhv fileConfigToRoleBhv;

    @Resource
    protected FileConfigToLabelBhv fileConfigToLabelBhv;

    @Resource
    protected FileConfigBhv fileConfigBhv;

    public FileConfigService() {
        super();
    }

    public List<FileConfig> getFileConfigList(final FileConfigPager fileConfigPager) {

        final PagingResultBean<FileConfig> fileConfigList = fileConfigBhv.selectPage(cb -> {
            cb.paging(fileConfigPager.getPageSize(), fileConfigPager.getCurrentPageNumber());
            setupListCondition(cb, fileConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(fileConfigList, fileConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        fileConfigPager.setPageNumberList(fileConfigList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return fileConfigList;
    }

    public void delete(final FileConfig fileConfig) {
        setupDeleteCondition(fileConfig);

        fileConfigBhv.delete(fileConfig, op -> {
            op.setRefresh(true);
        });

    }

    public List<FileConfig> getAllFileConfigList() {
        return getAllFileConfigList(true, true, true, null);
    }

    public List<FileConfig> getFileConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllFileConfigList();
        } else {
            return getAllFileConfigList(true, true, false, idList);
        }
    }

    public List<FileConfig> getAllFileConfigList(final boolean withLabelType, final boolean withRoleType, final boolean available,
            final List<String> idList) {
        final List<FileConfig> list = fileConfigBhv.selectList(cb -> {
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
        });
        return list;
    }

    public OptionalEntity<FileConfig> getFileConfig(final String id) {
        return fileConfigBhv.selectByPK(id).map(entity -> {

            final List<FileConfigToRole> fctrtmList = fileConfigToRoleBhv.selectList(fctrtmCb -> {
                fctrtmCb.query().setFileConfigId_Equal(entity.getId());
            });
            if (!fctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(fctrtmList.size());
                for (final FileConfigToRole mapping : fctrtmList) {
                    roleTypeIds.add(mapping.getRoleTypeId());
                }
                entity.setRoleTypeIds(roleTypeIds.toArray(new String[roleTypeIds.size()]));
            }

            final List<FileConfigToLabel> fctltmList = fileConfigToLabelBhv.selectList(fctltmCb -> {
                fctltmCb.query().setFileConfigId_Equal(entity.getId());
            });
            if (!fctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(fctltmList.size());
                for (final FileConfigToLabel mapping : fctltmList) {
                    labelTypeIds.add(mapping.getLabelTypeId());
                }
                entity.setLabelTypeIds(labelTypeIds.toArray(new String[labelTypeIds.size()]));
            }
            return entity;
        });
    }

    public void store(final FileConfig fileConfig) {
        final boolean isNew = fileConfig.getId() == null;
        final String[] labelTypeIds = fileConfig.getLabelTypeIds();
        final String[] roleTypeIds = fileConfig.getRoleTypeIds();
        setupStoreCondition(fileConfig);

        fileConfigBhv.insertOrUpdate(fileConfig, op -> {
            op.setRefresh(true);
        });
        final String fileConfigId = fileConfig.getId();
        if (isNew) {
            // Insert
            if (labelTypeIds != null) {
                final List<FileConfigToLabel> fctltmList = new ArrayList<FileConfigToLabel>();
                for (final String labelTypeId : labelTypeIds) {
                    final FileConfigToLabel mapping = new FileConfigToLabel();
                    mapping.setFileConfigId(fileConfigId);
                    mapping.setLabelTypeId(labelTypeId);
                    fctltmList.add(mapping);
                }
                fileConfigToLabelBhv.batchInsert(fctltmList, op -> {
                    op.setRefresh(true);
                });
            }
            if (roleTypeIds != null) {
                final List<FileConfigToRole> fctrtmList = new ArrayList<FileConfigToRole>();
                for (final String roleTypeId : roleTypeIds) {
                    final FileConfigToRole mapping = new FileConfigToRole();
                    mapping.setFileConfigId(fileConfigId);
                    mapping.setRoleTypeId(roleTypeId);
                    fctrtmList.add(mapping);
                }
                fileConfigToRoleBhv.batchInsert(fctrtmList, op -> {
                    op.setRefresh(true);
                });
            }
        } else {
            // Update
            if (labelTypeIds != null) {
                final List<FileConfigToLabel> fctltmList = fileConfigToLabelBhv.selectList(fctltmCb -> {
                    fctltmCb.query().setFileConfigId_Equal(fileConfigId);
                });
                final List<FileConfigToLabel> newList = new ArrayList<FileConfigToLabel>();
                final List<FileConfigToLabel> matchedList = new ArrayList<FileConfigToLabel>();
                for (final String id : labelTypeIds) {
                    boolean exist = false;
                    for (final FileConfigToLabel mapping : fctltmList) {
                        if (mapping.getLabelTypeId().equals(id)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final FileConfigToLabel mapping = new FileConfigToLabel();
                        mapping.setFileConfigId(fileConfigId);
                        mapping.setLabelTypeId(id);
                        newList.add(mapping);
                    }
                }
                fctltmList.removeAll(matchedList);
                fileConfigToLabelBhv.batchInsert(newList, op -> {
                    op.setRefresh(true);
                });
                fileConfigToLabelBhv.batchDelete(fctltmList, op -> {
                    op.setRefresh(true);
                });
            }
            if (roleTypeIds != null) {
                final List<FileConfigToRole> fctrtmList = fileConfigToRoleBhv.selectList(fctrtmCb -> {
                    fctrtmCb.query().setFileConfigId_Equal(fileConfigId);
                });
                final List<FileConfigToRole> newList = new ArrayList<FileConfigToRole>();
                final List<FileConfigToRole> matchedList = new ArrayList<FileConfigToRole>();
                for (final String id : roleTypeIds) {
                    boolean exist = false;
                    for (final FileConfigToRole mapping : fctrtmList) {
                        if (mapping.getRoleTypeId().equals(id)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final FileConfigToRole mapping = new FileConfigToRole();
                        mapping.setFileConfigId(fileConfigId);
                        mapping.setRoleTypeId(id);
                        newList.add(mapping);
                    }
                }
                fctrtmList.removeAll(matchedList);
                fileConfigToRoleBhv.batchInsert(newList, op -> {
                    op.setRefresh(true);
                });
                fileConfigToRoleBhv.batchDelete(fctrtmList, op -> {
                    op.setRefresh(true);
                });
            }
        }
    }

    protected void setupListCondition(final FileConfigCB cb, final FileConfigPager fileConfigPager) {
        if (fileConfigPager.id != null) {
            cb.query().docMeta().setId_Equal(fileConfigPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    protected void setupEntityCondition(final FileConfigCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final FileConfig fileConfig) {

        // setup condition

    }

    protected void setupDeleteCondition(final FileConfig fileConfig) {

        // setup condition

    }

}
