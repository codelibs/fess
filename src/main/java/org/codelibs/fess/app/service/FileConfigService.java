/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FileConfigPager;
import org.codelibs.fess.es.config.cbean.FileConfigCB;
import org.codelibs.fess.es.config.exbhv.FileAuthenticationBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigToLabelBhv;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.es.config.exentity.FileConfigToLabel;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class FileConfigService {

    @Resource
    protected FileConfigToLabelBhv fileConfigToLabelBhv;

    @Resource
    protected FileConfigBhv fileConfigBhv;

    @Resource
    protected FileAuthenticationBhv fileAuthenticationBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<FileConfig> getFileConfigList(final FileConfigPager fileConfigPager) {

        final PagingResultBean<FileConfig> fileConfigList = fileConfigBhv.selectPage(cb -> {
            cb.paging(fileConfigPager.getPageSize(), fileConfigPager.getCurrentPageNumber());
            setupListCondition(cb, fileConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(fileConfigList, fileConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        fileConfigPager.setPageNumberList(fileConfigList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return fileConfigList;
    }

    public void delete(final FileConfig fileConfig) {

        final String fileConfigId = fileConfig.getId();

        fileConfigBhv.delete(fileConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        fileConfigToLabelBhv.queryDelete(cb -> {
            cb.query().setFileConfigId_Equal(fileConfigId);
        });

        fileAuthenticationBhv.queryDelete(cb -> {
            cb.query().setFileConfigId_Equal(fileConfigId);
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
        return fileConfigBhv.selectList(cb -> {
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(fessConfig.getPageFileConfigMaxFetchSizeAsInteger());
        });
    }

    public OptionalEntity<FileConfig> getFileConfig(final String id) {
        return fileConfigBhv.selectByPK(id).map(entity -> {

            final List<FileConfigToLabel> fctltmList = fileConfigToLabelBhv.selectList(fctltmCb -> {
                fctltmCb.query().setFileConfigId_Equal(entity.getId());
                fctltmCb.fetchFirst(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger());
            });
            if (!fctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<>(fctltmList.size());
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

        fileConfigBhv.insertOrUpdate(fileConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
        final String fileConfigId = fileConfig.getId();
        if (isNew) {
            // Insert
            if (labelTypeIds != null) {
                final List<FileConfigToLabel> fctltmList = new ArrayList<>();
                for (final String labelTypeId : labelTypeIds) {
                    final FileConfigToLabel mapping = new FileConfigToLabel();
                    mapping.setFileConfigId(fileConfigId);
                    mapping.setLabelTypeId(labelTypeId);
                    fctltmList.add(mapping);
                }
                fileConfigToLabelBhv.batchInsert(fctltmList, op -> {
                    op.setRefreshPolicy(Constants.TRUE);
                });
            }
        } else {
            // Update
            if (labelTypeIds != null) {
                final List<FileConfigToLabel> fctltmList = fileConfigToLabelBhv.selectList(fctltmCb -> {
                    fctltmCb.query().setFileConfigId_Equal(fileConfigId);
                    fctltmCb.fetchFirst(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger());
                });
                final List<FileConfigToLabel> newList = new ArrayList<>();
                final List<FileConfigToLabel> matchedList = new ArrayList<>();
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
                    op.setRefreshPolicy(Constants.TRUE);
                });
                fileConfigToLabelBhv.batchDelete(fctltmList, op -> {
                    op.setRefreshPolicy(Constants.TRUE);
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
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
