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
import org.codelibs.fess.crud.service.BsWebCrawlingConfigService;
import org.codelibs.fess.db.cbean.WebCrawlingConfigCB;
import org.codelibs.fess.db.exbhv.WebConfigToLabelTypeMappingBhv;
import org.codelibs.fess.db.exbhv.WebConfigToRoleTypeMappingBhv;
import org.codelibs.fess.db.exentity.WebConfigToLabelTypeMapping;
import org.codelibs.fess.db.exentity.WebConfigToRoleTypeMapping;
import org.codelibs.fess.db.exentity.WebCrawlingConfig;
import org.codelibs.fess.pager.WebCrawlingConfigPager;

public class WebCrawlingConfigService extends BsWebCrawlingConfigService
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected WebConfigToLabelTypeMappingBhv webConfigToLabelTypeMappingBhv;

    @Resource
    protected WebConfigToRoleTypeMappingBhv webConfigToRoleTypeMappingBhv;

    public List<WebCrawlingConfig> getAllWebCrawlingConfigList() {
        return getAllWebCrawlingConfigList(true, true, true, null);
    }

    public List<WebCrawlingConfig> getWebCrawlingConfigListByIds(
            final List<Long> idList) {
        if (idList == null) {
            return getAllWebCrawlingConfigList();
        } else {
            return getAllWebCrawlingConfigList(true, true, false, idList);
        }
    }

    public List<WebCrawlingConfig> getAllWebCrawlingConfigList(
            final boolean withLabelType, final boolean withRoleType,
            final boolean available, final List<Long> idList) {
        final List<WebCrawlingConfig> list = webCrawlingConfigBhv
                .selectList(cb -> {
                    cb.query().setDeletedBy_IsNull();
                    if (available) {
                        cb.query().setAvailable_Equal(Constants.T);
                    }
                    if (idList != null) {
                        cb.query().setId_InScope(idList);
                    }
                });
        if (withLabelType) {
            webCrawlingConfigBhv.loadWebConfigToLabelTypeMapping(list, cb -> {
                cb.setupSelect_LabelType();
                cb.query().queryLabelType().setDeletedBy_IsNull();
                cb.query().queryLabelType().addOrderBy_SortOrder_Asc();
            });
        }
        if (withRoleType) {
            webCrawlingConfigBhv.loadWebConfigToRoleTypeMapping(list, cb -> {
                cb.setupSelect_RoleType();
                cb.query().queryRoleType().setDeletedBy_IsNull();
                cb.query().queryRoleType().addOrderBy_SortOrder_Asc();
            });
        }
        return list;
    }

    @Override
    public WebCrawlingConfig getWebCrawlingConfig(final Map<String, String> keys) {
        final WebCrawlingConfig webCrawlingConfig = super
                .getWebCrawlingConfig(keys);

        if (webCrawlingConfig != null) {
            final List<WebConfigToLabelTypeMapping> wctltmList = webConfigToLabelTypeMappingBhv
                    .selectList(wctltmCb -> {
                        wctltmCb.query().setWebConfigId_Equal(
                                webCrawlingConfig.getId());
                        wctltmCb.query().queryLabelType().setDeletedBy_IsNull();
                        wctltmCb.query().queryWebCrawlingConfig()
                                .setDeletedBy_IsNull();
                    });
            if (!wctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(
                        wctltmList.size());
                for (final WebConfigToLabelTypeMapping mapping : wctltmList) {
                    labelTypeIds.add(Long.toString(mapping.getLabelTypeId()));
                }
                webCrawlingConfig.setLabelTypeIds(labelTypeIds
                        .toArray(new String[labelTypeIds.size()]));
            }

            final List<WebConfigToRoleTypeMapping> wctrtmList = webConfigToRoleTypeMappingBhv
                    .selectList(wctrtmCb -> {
                        wctrtmCb.query().setWebConfigId_Equal(
                                webCrawlingConfig.getId());
                        wctrtmCb.query().queryRoleType().setDeletedBy_IsNull();
                        wctrtmCb.query().queryWebCrawlingConfig()
                                .setDeletedBy_IsNull();
                    });
            if (!wctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(
                        wctrtmList.size());
                for (final WebConfigToRoleTypeMapping mapping : wctrtmList) {
                    roleTypeIds.add(Long.toString(mapping.getRoleTypeId()));
                }
                webCrawlingConfig.setRoleTypeIds(roleTypeIds
                        .toArray(new String[roleTypeIds.size()]));
            }
        }

        return webCrawlingConfig;
    }

    @Override
    public void store(final WebCrawlingConfig webCrawlingConfig) {
        final boolean isNew = webCrawlingConfig.getId() == null;
        final String[] labelTypeIds = webCrawlingConfig.getLabelTypeIds();
        final String[] roleTypeIds = webCrawlingConfig.getRoleTypeIds();
        super.store(webCrawlingConfig);
        final Long webConfigId = webCrawlingConfig.getId();
        if (isNew) {
            // Insert
            if (labelTypeIds != null) {
                final List<WebConfigToLabelTypeMapping> wctltmList = new ArrayList<WebConfigToLabelTypeMapping>();
                for (final String id : labelTypeIds) {
                    final WebConfigToLabelTypeMapping mapping = new WebConfigToLabelTypeMapping();
                    mapping.setWebConfigId(webConfigId);
                    mapping.setLabelTypeId(Long.parseLong(id));
                    wctltmList.add(mapping);
                }
                webConfigToLabelTypeMappingBhv.batchInsert(wctltmList);
            }
            if (roleTypeIds != null) {
                final List<WebConfigToRoleTypeMapping> wctrtmList = new ArrayList<WebConfigToRoleTypeMapping>();
                for (final String id : roleTypeIds) {
                    final WebConfigToRoleTypeMapping mapping = new WebConfigToRoleTypeMapping();
                    mapping.setWebConfigId(webConfigId);
                    mapping.setRoleTypeId(Long.parseLong(id));
                    wctrtmList.add(mapping);
                }
                webConfigToRoleTypeMappingBhv.batchInsert(wctrtmList);
            }
        } else {
            // Update
            if (labelTypeIds != null) {
                final List<WebConfigToLabelTypeMapping> list = webConfigToLabelTypeMappingBhv
                        .selectList(wctltmCb -> {
                            wctltmCb.query().setWebConfigId_Equal(webConfigId);
                        });
                final List<WebConfigToLabelTypeMapping> newList = new ArrayList<WebConfigToLabelTypeMapping>();
                final List<WebConfigToLabelTypeMapping> matchedList = new ArrayList<WebConfigToLabelTypeMapping>();
                for (final String id : labelTypeIds) {
                    final Long labelTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final WebConfigToLabelTypeMapping mapping : list) {
                        if (mapping.getLabelTypeId().equals(labelTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final WebConfigToLabelTypeMapping mapping = new WebConfigToLabelTypeMapping();
                        mapping.setWebConfigId(webConfigId);
                        mapping.setLabelTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                list.removeAll(matchedList);
                webConfigToLabelTypeMappingBhv.batchInsert(newList);
                webConfigToLabelTypeMappingBhv.batchDelete(list);
            }
            if (roleTypeIds != null) {
                final List<WebConfigToRoleTypeMapping> list = webConfigToRoleTypeMappingBhv
                        .selectList(wctrtmCb -> {
                            wctrtmCb.query().setWebConfigId_Equal(webConfigId);
                        });
                final List<WebConfigToRoleTypeMapping> newList = new ArrayList<WebConfigToRoleTypeMapping>();
                final List<WebConfigToRoleTypeMapping> matchedList = new ArrayList<WebConfigToRoleTypeMapping>();
                for (final String id : roleTypeIds) {
                    final Long roleTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final WebConfigToRoleTypeMapping mapping : list) {
                        if (mapping.getRoleTypeId().equals(roleTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final WebConfigToRoleTypeMapping mapping = new WebConfigToRoleTypeMapping();
                        mapping.setWebConfigId(webConfigId);
                        mapping.setRoleTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                list.removeAll(matchedList);
                webConfigToRoleTypeMappingBhv.batchInsert(newList);
                webConfigToRoleTypeMappingBhv.batchDelete(list);
            }
        }
    }

    @Override
    protected void setupListCondition(final WebCrawlingConfigCB cb,
            final WebCrawlingConfigPager webCrawlingConfigPager) {
        super.setupListCondition(cb, webCrawlingConfigPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final WebCrawlingConfigCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(final WebCrawlingConfig webCrawlingConfig) {
        super.setupStoreCondition(webCrawlingConfig);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(
            final WebCrawlingConfig webCrawlingConfig) {
        super.setupDeleteCondition(webCrawlingConfig);

        // setup condition

    }

    public WebCrawlingConfig getWebCrawlingConfig(final long id) {
        return webCrawlingConfigBhv.selectEntity(cb -> {
            cb.query().setId_Equal(id);
            cb.query().setDeletedBy_IsNull();
        }).orElse(null);//TODO
    }
}
