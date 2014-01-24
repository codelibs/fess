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
import jp.sf.fess.crud.service.BsWebCrawlingConfigService;
import jp.sf.fess.db.cbean.WebConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.exbhv.WebConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.WebConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebCrawlingConfig;
import jp.sf.fess.pager.WebCrawlingConfigPager;

import org.seasar.dbflute.bhv.ConditionBeanSetupper;

public class WebCrawlingConfigService extends BsWebCrawlingConfigService
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected WebConfigToBrowserTypeMappingBhv webConfigToBrowserTypeMappingBhv;

    @Resource
    protected WebConfigToLabelTypeMappingBhv webConfigToLabelTypeMappingBhv;

    @Resource
    protected WebConfigToRoleTypeMappingBhv webConfigToRoleTypeMappingBhv;

    public List<WebCrawlingConfig> getAllWebCrawlingConfigList() {
        return getAllWebCrawlingConfigList(true, true, true, true, null);
    }

    public List<WebCrawlingConfig> getWebCrawlingConfigListByIds(
            final List<Long> idList) {
        if (idList == null) {
            return getAllWebCrawlingConfigList();
        } else {
            return getAllWebCrawlingConfigList(true, true, true, false, idList);
        }
    }

    public List<WebCrawlingConfig> getAllWebCrawlingConfigList(
            final boolean withBrowserType, final boolean withLabelType,
            final boolean withRoleType, final boolean available,
            final List<Long> idList) {
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.query().setDeletedBy_IsNull();
        if (available) {
            cb.query().setAvailable_Equal(Constants.T);
        }
        if (idList != null) {
            cb.query().setId_InScope(idList);
        }
        final List<WebCrawlingConfig> list = webCrawlingConfigBhv
                .selectList(cb);
        if (withBrowserType) {
            final ConditionBeanSetupper<WebConfigToBrowserTypeMappingCB> setupper = new ConditionBeanSetupper<WebConfigToBrowserTypeMappingCB>() {
                @Override
                public void setup(final WebConfigToBrowserTypeMappingCB cb) {
                    cb.setupSelect_BrowserType();
                    cb.query().queryBrowserType().setDeletedBy_IsNull();
                    cb.query().queryBrowserType().addOrderBy_SortOrder_Asc();
                }

            };
            webCrawlingConfigBhv.loadWebConfigToBrowserTypeMappingList(list,
                    setupper);
        }
        if (withLabelType) {
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> setupper = new ConditionBeanSetupper<WebConfigToLabelTypeMappingCB>() {
                @Override
                public void setup(final WebConfigToLabelTypeMappingCB cb) {
                    cb.setupSelect_LabelType();
                    cb.query().queryLabelType().setDeletedBy_IsNull();
                    cb.query().queryLabelType().addOrderBy_SortOrder_Asc();
                }

            };
            webCrawlingConfigBhv.loadWebConfigToLabelTypeMappingList(list,
                    setupper);
        }
        if (withRoleType) {
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> setupper = new ConditionBeanSetupper<WebConfigToRoleTypeMappingCB>() {
                @Override
                public void setup(final WebConfigToRoleTypeMappingCB cb) {
                    cb.setupSelect_RoleType();
                    cb.query().queryRoleType().setDeletedBy_IsNull();
                    cb.query().queryRoleType().addOrderBy_SortOrder_Asc();
                }

            };
            webCrawlingConfigBhv.loadWebConfigToRoleTypeMappingList(list,
                    setupper);
        }
        return list;
    }

    @Override
    public WebCrawlingConfig getWebCrawlingConfig(final Map<String, String> keys) {
        final WebCrawlingConfig webCrawlingConfig = super
                .getWebCrawlingConfig(keys);

        if (webCrawlingConfig != null) {
            final WebConfigToBrowserTypeMappingCB wctbtmCb = new WebConfigToBrowserTypeMappingCB();
            wctbtmCb.query().setWebConfigId_Equal(webCrawlingConfig.getId());
            wctbtmCb.query().queryBrowserType().setDeletedBy_IsNull();
            wctbtmCb.query().queryWebCrawlingConfig().setDeletedBy_IsNull();
            final List<WebConfigToBrowserTypeMapping> wctbtmList = webConfigToBrowserTypeMappingBhv
                    .selectList(wctbtmCb);
            if (!wctbtmList.isEmpty()) {
                final List<String> browserTypeIds = new ArrayList<String>(
                        wctbtmList.size());
                for (final WebConfigToBrowserTypeMapping mapping : wctbtmList) {
                    browserTypeIds
                            .add(Long.toString(mapping.getBrowserTypeId()));
                }
                webCrawlingConfig.setBrowserTypeIds(browserTypeIds
                        .toArray(new String[browserTypeIds.size()]));
            }

            final WebConfigToLabelTypeMappingCB wctltmCb = new WebConfigToLabelTypeMappingCB();
            wctltmCb.query().setWebConfigId_Equal(webCrawlingConfig.getId());
            wctltmCb.query().queryLabelType().setDeletedBy_IsNull();
            wctltmCb.query().queryWebCrawlingConfig().setDeletedBy_IsNull();
            final List<WebConfigToLabelTypeMapping> wctltmList = webConfigToLabelTypeMappingBhv
                    .selectList(wctltmCb);
            if (!wctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(
                        wctltmList.size());
                for (final WebConfigToLabelTypeMapping mapping : wctltmList) {
                    labelTypeIds.add(Long.toString(mapping.getLabelTypeId()));
                }
                webCrawlingConfig.setLabelTypeIds(labelTypeIds
                        .toArray(new String[labelTypeIds.size()]));
            }

            final WebConfigToRoleTypeMappingCB wctrtmCb = new WebConfigToRoleTypeMappingCB();
            wctrtmCb.query().setWebConfigId_Equal(webCrawlingConfig.getId());
            wctrtmCb.query().queryRoleType().setDeletedBy_IsNull();
            wctrtmCb.query().queryWebCrawlingConfig().setDeletedBy_IsNull();
            final List<WebConfigToRoleTypeMapping> wctrtmList = webConfigToRoleTypeMappingBhv
                    .selectList(wctrtmCb);
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
        final String[] browserTypeIds = webCrawlingConfig.getBrowserTypeIds();
        final String[] labelTypeIds = webCrawlingConfig.getLabelTypeIds();
        final String[] roleTypeIds = webCrawlingConfig.getRoleTypeIds();
        super.store(webCrawlingConfig);
        final Long webConfigId = webCrawlingConfig.getId();
        if (isNew) {
            // Insert
            if (browserTypeIds != null) {
                final List<WebConfigToBrowserTypeMapping> wctbtmList = new ArrayList<WebConfigToBrowserTypeMapping>();
                for (final String id : browserTypeIds) {
                    final WebConfigToBrowserTypeMapping mapping = new WebConfigToBrowserTypeMapping();
                    mapping.setWebConfigId(webConfigId);
                    mapping.setBrowserTypeId(Long.parseLong(id));
                    wctbtmList.add(mapping);
                }
                webConfigToBrowserTypeMappingBhv.batchInsert(wctbtmList);
            }
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
            if (browserTypeIds != null) {
                final WebConfigToBrowserTypeMappingCB wctbtmCb = new WebConfigToBrowserTypeMappingCB();
                wctbtmCb.query().setWebConfigId_Equal(webConfigId);
                final List<WebConfigToBrowserTypeMapping> list = webConfigToBrowserTypeMappingBhv
                        .selectList(wctbtmCb);
                final List<WebConfigToBrowserTypeMapping> newList = new ArrayList<WebConfigToBrowserTypeMapping>();
                final List<WebConfigToBrowserTypeMapping> matchedList = new ArrayList<WebConfigToBrowserTypeMapping>();
                for (final String id : browserTypeIds) {
                    final Long browserTypeId = Long.parseLong(id);
                    boolean exist = false;
                    for (final WebConfigToBrowserTypeMapping mapping : list) {
                        if (mapping.getBrowserTypeId().equals(browserTypeId)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final WebConfigToBrowserTypeMapping mapping = new WebConfigToBrowserTypeMapping();
                        mapping.setWebConfigId(webConfigId);
                        mapping.setBrowserTypeId(Long.parseLong(id));
                        newList.add(mapping);
                    }
                }
                list.removeAll(matchedList);
                webConfigToBrowserTypeMappingBhv.batchInsert(newList);
                webConfigToBrowserTypeMappingBhv.batchDelete(list);
            }
            if (labelTypeIds != null) {
                final WebConfigToLabelTypeMappingCB wctltmCb = new WebConfigToLabelTypeMappingCB();
                wctltmCb.query().setWebConfigId_Equal(webConfigId);
                final List<WebConfigToLabelTypeMapping> list = webConfigToLabelTypeMappingBhv
                        .selectList(wctltmCb);
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
                final WebConfigToRoleTypeMappingCB wctrtmCb = new WebConfigToRoleTypeMappingCB();
                wctrtmCb.query().setWebConfigId_Equal(webConfigId);
                final List<WebConfigToRoleTypeMapping> list = webConfigToRoleTypeMappingBhv
                        .selectList(wctrtmCb);
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
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.query().setId_Equal(id);
        cb.query().setDeletedBy_IsNull();
        return webCrawlingConfigBhv.selectEntity(cb);
    }
}
