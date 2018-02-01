/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
import org.codelibs.fess.app.pager.DataConfigPager;
import org.codelibs.fess.es.config.cbean.DataConfigCB;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exbhv.DataConfigToLabelBhv;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.es.config.exentity.DataConfigToLabel;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class DataConfigService {

    @Resource
    protected DataConfigToLabelBhv dataConfigToLabelBhv;

    @Resource
    protected DataConfigBhv dataConfigBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<DataConfig> getDataConfigList(final DataConfigPager dataConfigPager) {

        final PagingResultBean<DataConfig> dataConfigList = dataConfigBhv.selectPage(cb -> {
            cb.paging(dataConfigPager.getPageSize(), dataConfigPager.getCurrentPageNumber());

            setupListCondition(cb, dataConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(dataConfigList, dataConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        dataConfigPager.setPageNumberList(dataConfigList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return dataConfigList;
    }

    public void delete(final DataConfig dataConfig) {

        final String dataConfigId = dataConfig.getId();

        dataConfigBhv.delete(dataConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        dataConfigToLabelBhv.queryDelete(cb -> {
            cb.query().setDataConfigId_Equal(dataConfigId);
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
        return dataConfigBhv.selectList(cb -> {
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(fessConfig.getPageDataConfigMaxFetchSizeAsInteger());
        });
    }

    public OptionalEntity<DataConfig> getDataConfig(final String id) {
        return dataConfigBhv.selectByPK(id).map(entity -> {

            final List<DataConfigToLabel> fctltmList = dataConfigToLabelBhv.selectList(fctltmCb -> {
                fctltmCb.query().setDataConfigId_Equal(entity.getId());
                fctltmCb.fetchFirst(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger());
            });
            if (!fctltmList.isEmpty()) {
                final List<String> labelTypeIds = new ArrayList<>(fctltmList.size());
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

        dataConfigBhv.insertOrUpdate(dataConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
        final String dataConfigId = dataConfig.getId();
        if (isNew) {
            // Insert
            if (labelTypeIds != null) {
                final List<DataConfigToLabel> fctltmList = new ArrayList<>();
                for (final String labelTypeId : labelTypeIds) {
                    final DataConfigToLabel mapping = new DataConfigToLabel();
                    mapping.setDataConfigId(dataConfigId);
                    mapping.setLabelTypeId(labelTypeId);
                    fctltmList.add(mapping);
                }
                dataConfigToLabelBhv.batchInsert(fctltmList, op -> {
                    op.setRefreshPolicy(Constants.TRUE);
                });
            }
        } else {
            // Update
            if (labelTypeIds != null) {
                final List<DataConfigToLabel> fctltmList = dataConfigToLabelBhv.selectList(fctltmCb -> {
                    fctltmCb.query().setDataConfigId_Equal(dataConfigId);
                    fctltmCb.fetchFirst(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger());
                });
                final List<DataConfigToLabel> newList = new ArrayList<>();
                final List<DataConfigToLabel> matchedList = new ArrayList<>();
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
                    op.setRefreshPolicy(Constants.TRUE);
                });
                dataConfigToLabelBhv.batchDelete(fctltmList, op -> {
                    op.setRefreshPolicy(Constants.TRUE);
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
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
