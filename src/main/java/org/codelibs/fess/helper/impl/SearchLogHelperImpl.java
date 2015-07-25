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

package org.codelibs.fess.helper.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.client.FessEsClient;
import org.codelibs.fess.es.exbhv.ClickLogBhv;
import org.codelibs.fess.es.exbhv.SearchLogBhv;
import org.codelibs.fess.es.exbhv.UserInfoBhv;
import org.codelibs.fess.es.exentity.ClickLog;
import org.codelibs.fess.es.exentity.SearchLog;
import org.codelibs.fess.es.exentity.UserInfo;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchLogHelperImpl extends SearchLogHelper {
    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(SearchLogHelperImpl.class);

    @Override
    public void updateUserInfo(final String userCode) {
        final long current = System.currentTimeMillis();
        final Long time = userInfoCache.get(userCode);
        if (time == null || current - time.longValue() > userCheckInterval) {

            final UserInfoBhv userInfoBhv = ComponentUtil.getComponent(UserInfoBhv.class);

            final List<UserInfo> list = userInfoBhv.selectList(cb -> {
                cb.query().setCode_Equal(userCode);
                cb.query().addOrderBy_UpdatedTime_Desc();
            });

            final UserInfo userInfo;
            final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
            if (list.isEmpty()) {
                userInfo = new UserInfo();
                userInfo.setCode(userCode);
                userInfo.setCreatedTime(now);
            } else {
                userInfo = list.get(0);
            }
            userInfo.setUpdatedTime(now);
            new Thread(() -> {
                userInfoBhv.insertOrUpdate(userInfo);
            }).start();
            userInfoCache.put(userCode, current);
        }
    }

    @Override
    protected void processSearchLogQueue(final Queue<SearchLog> queue) {
        final List<SearchLog> searchLogList = new ArrayList<SearchLog>();
        final String value = crawlerProperties.getProperty(Constants.PURGE_BY_BOTS_PROPERTY, StringUtil.EMPTY);
        String[] botNames;
        if (StringUtil.isBlank(value)) {
            botNames = StringUtil.EMPTY_STRINGS;
        } else {
            botNames = value.split(",");
        }

        Constants.TRUE.equals(crawlerProperties.getProperty(Constants.SUGGEST_SEARCH_LOG_PROPERTY, Constants.TRUE));
        final String dayForCleanupStr = crawlerProperties.getProperty(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY, "30");
        try {
            Integer.parseInt(dayForCleanupStr);
        } catch (final NumberFormatException e) {}

        final Map<String, UserInfo> userInfoMap = new HashMap<String, UserInfo>();
        for (final SearchLog searchLog : queue) {
            boolean add = true;
            for (final String botName : botNames) {
                if (searchLog.getUserAgent() != null && searchLog.getUserAgent().indexOf(botName) >= 0) {
                    add = false;
                    break;
                }
            }
            if (add) {
                final UserInfo userInfo = searchLog.getUserInfo().orElse(null);// TODO
                if (userInfo != null) {
                    final String code = userInfo.getCode();
                    final UserInfo oldUserInfo = userInfoMap.get(code);
                    if (oldUserInfo != null) {
                        userInfo.setCreatedTime(oldUserInfo.getCreatedTime());
                    }
                    userInfoMap.put(code, userInfo);
                }
                searchLogList.add(searchLog);

                // TODO
                //                if (suggestAvailable && searchLog.getHitCount() > 0) {
                //                    final List<SearchFieldLog> searchFieldLogList = searchLog.getSearchFieldLogList();
                //                    for (final SearchFieldLog searchFieldLog : searchFieldLogList) {
                //                        if ("solrQuery".equals(searchFieldLog.getName())) {
                //                            suggestService.addSolrParams(searchFieldLog.getValue(), dayForCleanup);
                //                            addedSuggest = true;
                //                        }
                //                    }
                //                }
            }
        }
        //        if (addedSuggest) {
        //            suggestService.commit();
        //        }

        if (!userInfoMap.isEmpty()) {
            final List<UserInfo> insertList = new ArrayList<UserInfo>(userInfoMap.values());
            final List<UserInfo> updateList = new ArrayList<UserInfo>();
            final UserInfoBhv userInfoBhv = SingletonLaContainer.getComponent(UserInfoBhv.class);
            final List<UserInfo> list = userInfoBhv.selectList(cb -> {
                cb.query().setCode_InScope(userInfoMap.keySet());
            });
            for (final UserInfo userInfo : list) {
                final String code = userInfo.getCode();
                final UserInfo entity = userInfoMap.get(code);
                BeanUtil.copyBeanToBean(userInfo, entity, option -> option.include("id", "createdTime"));
                updateList.add(entity);
                insertList.remove(entity);
            }
            userInfoBhv.batchInsert(insertList);
            userInfoBhv.batchUpdate(updateList);
            for (final SearchLog searchLog : searchLogList) {
                final UserInfo userInfo = searchLog.getUserInfo().orElse(null);//TODO
                if (userInfo != null) {
                    final UserInfo entity = userInfoMap.get(userInfo.getCode());
                    searchLog.setUserInfoId(entity.getId());
                }
            }
        }

        if (!searchLogList.isEmpty()) {
            final SearchLogBhv searchLogBhv = ComponentUtil.getComponent(SearchLogBhv.class);
            searchLogBhv.batchUpdate(searchLogList);
            // TODO SearchLogValue
        }
    }

    @Override
    protected void processClickLogQueue(final Queue<ClickLog> queue) {
        final Map<String, Long> clickCountMap = new HashMap<String, Long>();
        final List<ClickLog> clickLogList = new ArrayList<ClickLog>();
        for (final ClickLog clickLog : queue) {
            try {
                final SearchLogBhv searchLogBhv = SingletonLaContainer.getComponent(SearchLogBhv.class);
                final SearchLog entity = searchLogBhv.selectEntity(cb -> {
                    cb.query().setRequestedTime_Equal(clickLog.getQueryRequestedTime());
                    cb.query().setUserSessionId_Equal(clickLog.getUserSessionId());
                }).orElse(null);//TODO
                if (entity != null) {
                    clickLog.setSearchLogId(entity.getId());
                    clickLogList.add(clickLog);
                } else {
                    logger.warn("Not Found[ClickLog]: " + clickLog);
                }

                final String docId = clickLog.getDocId();
                Long countObj = clickCountMap.get(docId);
                final long clickCount = clickLog.getClickCount();
                if (countObj == null) {
                    countObj = Long.valueOf(clickCount);
                } else {
                    countObj = Math.max(countObj.longValue(), clickCount) + 1;
                }
                clickCountMap.put(docId, countObj);
            } catch (final Exception e) {
                logger.warn("Failed to process: " + clickLog, e);
            }
        }
        if (!clickLogList.isEmpty()) {
            try {
                final ClickLogBhv clickLogBhv = SingletonLaContainer.getComponent(ClickLogBhv.class);
                clickLogBhv.batchInsert(clickLogList);
            } catch (final Exception e) {
                logger.warn("Failed to insert: " + clickLogList, e);
            }
        }

        final FessEsClient fessEsClient = ComponentUtil.getElasticsearchClient();
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        for (final Map.Entry<String, Long> entry : clickCountMap.entrySet()) {
            try {
                // TODO buik update
                fessEsClient.update(fieldHelper.docIndex, fieldHelper.docType, entry.getKey(), fieldHelper.clickCountField,
                        entry.getValue() + 1);
            } catch (final Exception e) {
                logger.warn("Failed to update a clickCount(" + entry.getValue() + ") for " + entry.getKey(), e);
            }
        }
    }
}
