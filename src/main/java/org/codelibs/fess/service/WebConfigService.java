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

import java.beans.Beans;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.es.cbean.WebConfigCB;
import org.codelibs.fess.es.exbhv.WebConfigBhv;
import org.codelibs.fess.es.exbhv.WebConfigToLabelBhv;
import org.codelibs.fess.es.exbhv.WebConfigToRoleBhv;
import org.codelibs.fess.es.exentity.WebConfig;
import org.codelibs.fess.es.exentity.WebConfigToLabel;
import org.codelibs.fess.es.exentity.WebConfigToRole;
import org.codelibs.fess.pager.WebConfigPager;
import org.dbflute.cbean.result.PagingResultBean;

public class WebConfigService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected WebConfigToLabelBhv webConfigToLabelBhv;

    @Resource
    protected WebConfigToRoleBhv webConfigToRoleBhv;

    @Resource
    protected WebConfigBhv webConfigBhv;

    public WebConfigService() {
        super();
    }

    public List<WebConfig> getWebConfigList(final WebConfigPager webConfigPager) {

        final PagingResultBean<WebConfig> webConfigList = webConfigBhv.selectPage(cb -> {
            cb.paging(webConfigPager.getPageSize(), webConfigPager.getCurrentPageNumber());
            setupListCondition(cb, webConfigPager);
        });

        // update pager
        Beans.copy(webConfigList, webConfigPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        webConfigPager.setPageNumberList(webConfigList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return webConfigList;
    }

    public void delete(final WebConfig webConfig) throws CrudMessageException {
        setupDeleteCondition(webConfig);

        webConfigBhv.delete(webConfig, op -> {
            op.setRefresh(true);
        });

    }

    public List<WebConfig> getAllWebConfigList() {
        return getAllWebConfigList(true, true, true, null);
    }

    public List<WebConfig> getWebConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllWebConfigList();
        } else {
            return getAllWebConfigList(true, true, false, idList);
        }
    }

    public List<WebConfig> getAllWebConfigList(final boolean withLabelType, final boolean withRoleType, final boolean available,
            final List<String> idList) {
        final List<WebConfig> list = webConfigBhv.selectList(cb -> {
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
        });

        return list;
    }

    public WebConfig getWebConfig(final Map<String, String> keys) {
        final WebConfig webConfig = webConfigBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO

        if (webConfig != null) {
            final List<WebConfigToLabel> wctltmList = webConfigToLabelBhv.selectList(wctltmCb -> {
                wctltmCb.query().setWebConfigId_Equal(webConfig.getId());
            });
            if (!wctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<String>(wctltmList.size());
                for (final WebConfigToLabel mapping : wctltmList) {
                    labelTypeIds.add(mapping.getLabelTypeId());
                }
                webConfig.setLabelTypeIds(labelTypeIds.toArray(new String[labelTypeIds.size()]));
            }

            final List<WebConfigToRole> wctrtmList = webConfigToRoleBhv.selectList(wctrtmCb -> {
                wctrtmCb.query().setWebConfigId_Equal(webConfig.getId());
            });
            if (!wctrtmList.isEmpty()) {
                final List<String> roleTypeIds = new ArrayList<String>(wctrtmList.size());
                for (final WebConfigToRole mapping : wctrtmList) {
                    roleTypeIds.add(mapping.getRoleTypeId());
                }
                webConfig.setRoleTypeIds(roleTypeIds.toArray(new String[roleTypeIds.size()]));
            }
        }

        return webConfig;
    }

    public void store(final WebConfig webConfig) {
        final boolean isNew = webConfig.getId() == null;
        final String[] labelTypeIds = webConfig.getLabelTypeIds();
        final String[] roleTypeIds = webConfig.getRoleTypeIds();
        setupStoreCondition(webConfig);

        webConfigBhv.insertOrUpdate(webConfig, op -> {
            op.setRefresh(true);
        });
        final String webConfigId = webConfig.getId();
        if (isNew) {
            // Insert
            if (labelTypeIds != null) {
                final List<WebConfigToLabel> wctltmList = new ArrayList<WebConfigToLabel>();
                for (final String id : labelTypeIds) {
                    final WebConfigToLabel mapping = new WebConfigToLabel();
                    mapping.setWebConfigId(webConfigId);
                    mapping.setLabelTypeId(id);
                    wctltmList.add(mapping);
                }
                webConfigToLabelBhv.batchInsert(wctltmList, op -> {
                    op.setRefresh(true);
                });
            }
            if (roleTypeIds != null) {
                final List<WebConfigToRole> wctrtmList = new ArrayList<WebConfigToRole>();
                for (final String id : roleTypeIds) {
                    final WebConfigToRole mapping = new WebConfigToRole();
                    mapping.setWebConfigId(webConfigId);
                    mapping.setRoleTypeId(id);
                    wctrtmList.add(mapping);
                }
                webConfigToRoleBhv.batchInsert(wctrtmList, op -> {
                    op.setRefresh(true);
                });
            }
        } else {
            // Update
            if (labelTypeIds != null) {
                final List<WebConfigToLabel> list = webConfigToLabelBhv.selectList(wctltmCb -> {
                    wctltmCb.query().setWebConfigId_Equal(webConfigId);
                });
                final List<WebConfigToLabel> newList = new ArrayList<WebConfigToLabel>();
                final List<WebConfigToLabel> matchedList = new ArrayList<WebConfigToLabel>();
                for (final String id : labelTypeIds) {
                    boolean exist = false;
                    for (final WebConfigToLabel mapping : list) {
                        if (mapping.getLabelTypeId().equals(id)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final WebConfigToLabel mapping = new WebConfigToLabel();
                        mapping.setWebConfigId(webConfigId);
                        mapping.setLabelTypeId(id);
                        newList.add(mapping);
                    }
                }
                list.removeAll(matchedList);
                webConfigToLabelBhv.batchInsert(newList, op -> {
                    op.setRefresh(true);
                });
                webConfigToLabelBhv.batchDelete(list, op -> {
                    op.setRefresh(true);
                });
            }
            if (roleTypeIds != null) {
                final List<WebConfigToRole> list = webConfigToRoleBhv.selectList(wctrtmCb -> {
                    wctrtmCb.query().setWebConfigId_Equal(webConfigId);
                });
                final List<WebConfigToRole> newList = new ArrayList<WebConfigToRole>();
                final List<WebConfigToRole> matchedList = new ArrayList<WebConfigToRole>();
                for (final String id : roleTypeIds) {
                    boolean exist = false;
                    for (final WebConfigToRole mapping : list) {
                        if (mapping.getRoleTypeId().equals(id)) {
                            exist = true;
                            matchedList.add(mapping);
                            break;
                        }
                    }
                    if (!exist) {
                        // new
                        final WebConfigToRole mapping = new WebConfigToRole();
                        mapping.setWebConfigId(webConfigId);
                        mapping.setRoleTypeId(id);
                        newList.add(mapping);
                    }
                }
                list.removeAll(matchedList);
                webConfigToRoleBhv.batchInsert(newList, op -> {
                    op.setRefresh(true);
                });
                webConfigToRoleBhv.batchDelete(list, op -> {
                    op.setRefresh(true);
                });
            }
        }
    }

    protected void setupListCondition(final WebConfigCB cb, final WebConfigPager webConfigPager) {
        if (webConfigPager.id != null) {
            cb.query().docMeta().setId_Equal(webConfigPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    protected void setupEntityCondition(final WebConfigCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final WebConfig webConfig) {

        // setup condition

    }

    protected void setupDeleteCondition(final WebConfig webConfig) {

        // setup condition

    }

    public WebConfig getWebConfig(final String id) {
        return webConfigBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(id);
        }).orElse(null);//TODO
    }
}
