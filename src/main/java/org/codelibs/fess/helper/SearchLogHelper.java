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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.SearchService;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.es.log.exbhv.ClickLogBhv;
import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchFieldLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.es.log.exbhv.UserInfoBhv;
import org.codelibs.fess.es.log.exentity.ClickLog;
import org.codelibs.fess.es.log.exentity.SearchFieldLog;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.es.log.exentity.UserInfo;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalThing;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.script.Script;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchLogHelper {
    private static final Logger logger = LoggerFactory.getLogger(SearchLogHelper.class);

    public long userCheckInterval = 5 * 60 * 1000L;// 5 min

    public int userInfoCacheSize = 1000;

    protected volatile Queue<SearchLog> searchLogQueue = new ConcurrentLinkedQueue<>();

    protected volatile Queue<ClickLog> clickLogQueue = new ConcurrentLinkedQueue<>();

    protected Map<String, Long> userInfoCache;

    @PostConstruct
    public void init() {
        userInfoCache = new LruHashMap<>(userInfoCacheSize);
    }

    public void addSearchLog(final SearchRequestParams params, final LocalDateTime requestedTime, final String queryId, final String query,
            final int pageStart, final int pageSize, final QueryResponseList queryResponseList) {

        final RoleQueryHelper roleQueryHelper = ComponentUtil.getRoleQueryHelper();
        final UserInfoHelper userInfoHelper = ComponentUtil.getUserInfoHelper();
        final SearchLog searchLog = new SearchLog();

        if (ComponentUtil.getFessConfig().isUserInfo()) {
            final String userCode = userInfoHelper.getUserCode();
            if (userCode != null) {
                searchLog.setUserSessionId(userCode);
            }
        }

        searchLog.setRoles(roleQueryHelper.build(params.getType()).stream().toArray(n -> new String[n]));
        searchLog.setQueryId(queryId);
        searchLog.setHitCount(queryResponseList.getAllRecordCount());
        searchLog.setResponseTime(queryResponseList.getExecTime());
        searchLog.setQueryTime(queryResponseList.getQueryTime());
        searchLog.setSearchWord(StringUtils.abbreviate(query, 1000));
        searchLog.setSearchQuery(StringUtils.abbreviate(queryResponseList.getSearchQuery(), 1000));
        searchLog.setRequestedAt(requestedTime);
        searchLog.setQueryOffset(pageStart);
        searchLog.setQueryPageSize(pageSize);
        ComponentUtil.getRequestManager().findUserBean(FessUserBean.class).ifPresent(user -> {
            searchLog.setUser(user.getUserId());
        });

        final HttpServletRequest request = LaRequestUtil.getRequest();
        searchLog.setClientIp(StringUtils.abbreviate(ComponentUtil.getViewHelper().getClientIp(request), 100));
        searchLog.setReferer(StringUtils.abbreviate(request.getHeader("referer"), 1000));
        searchLog.setUserAgent(StringUtils.abbreviate(request.getHeader("user-agent"), 255));
        final Object accessType = request.getAttribute(Constants.SEARCH_LOG_ACCESS_TYPE);
        if (Constants.SEARCH_LOG_ACCESS_TYPE_JSON.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        } else if (Constants.SEARCH_LOG_ACCESS_TYPE_XML.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_XML);
        } else if (Constants.SEARCH_LOG_ACCESS_TYPE_OTHER.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_OTHER);
        } else {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_WEB);
        }
        final Object languages = request.getAttribute(Constants.REQUEST_LANGUAGES);
        if (languages != null) {
            searchLog.setLanguages(StringUtils.join((String[]) languages, ","));
        } else {
            searchLog.setLanguages("");
        }

        @SuppressWarnings("unchecked")
        final Map<String, List<String>> fieldLogMap = (Map<String, List<String>>) request.getAttribute(Constants.FIELD_LOGS);
        if (fieldLogMap != null) {
            for (final Map.Entry<String, List<String>> logEntry : fieldLogMap.entrySet()) {
                for (final String value : logEntry.getValue()) {
                    searchLog.addSearchFieldLogValue(logEntry.getKey(), StringUtils.abbreviate(value, 1000));
                }
            }
        }

        searchLogQueue.add(searchLog);
    }

    public void addClickLog(final ClickLog clickLog) {
        clickLogQueue.add(clickLog);
    }

    public void storeSearchLog() {
        if (!searchLogQueue.isEmpty()) {
            final Queue<SearchLog> queue = searchLogQueue;
            searchLogQueue = new ConcurrentLinkedQueue<>();
            processSearchLogQueue(queue);
        }

        if (!clickLogQueue.isEmpty()) {
            final Queue<ClickLog> queue = clickLogQueue;
            clickLogQueue = new ConcurrentLinkedQueue<>();
            processClickLogQueue(queue);
        }
    }

    public int getClickCount(final String url) {
        final ClickLogBhv clickLogBhv = ComponentUtil.getComponent(ClickLogBhv.class);
        return clickLogBhv.selectCount(cb -> {
            cb.query().setUrl_Equal(url);
        });
    }

    public long getFavoriteCount(final String url) {
        final FavoriteLogBhv favoriteLogBhv = ComponentUtil.getComponent(FavoriteLogBhv.class);
        return favoriteLogBhv.selectCount(cb -> {
            cb.query().setUrl_Equal(url);
        });
    }

    public void updateUserInfo(final String userCode) {
        final long current = System.currentTimeMillis();
        final Long time = userInfoCache.get(userCode);
        if (time == null || current - time.longValue() > userCheckInterval) {

            final UserInfoBhv userInfoBhv = ComponentUtil.getComponent(UserInfoBhv.class);

            final LocalDateTime now = ComponentUtil.getSystemHelper().getCurrentTimeAsLocalDateTime();
            userInfoBhv.selectByPK(userCode).ifPresent(userInfo -> {
                userInfo.setUpdatedAt(now);
                new Thread(() -> {
                    userInfoBhv.insertOrUpdate(userInfo);
                }).start();
            }).orElse(() -> {
                final UserInfo userInfo = new UserInfo();
                userInfo.setId(userCode);
                userInfo.setCreatedAt(now);
                userInfo.setUpdatedAt(now);
                userInfoBhv.insert(userInfo);
            });
            userInfoCache.put(userCode, current);
        }
    }

    protected void processSearchLogQueue(final Queue<SearchLog> queue) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String value = fessConfig.getPurgeByBots();
        String[] botNames;
        if (StringUtil.isBlank(value)) {
            botNames = StringUtil.EMPTY_STRINGS;
        } else {
            botNames = value.split(",");
        }

        final List<SearchLog> searchLogList = new ArrayList<>();
        final Map<String, UserInfo> userInfoMap = new HashMap<>();
        queue.stream().forEach(
                searchLog -> {
                    final String userAgent = searchLog.getUserAgent();
                    final boolean isBot =
                            userAgent != null
                                    && stream(botNames).get(stream -> stream.anyMatch(botName -> userAgent.indexOf(botName) >= 0));
                    if (!isBot) {
                        searchLog.getUserInfo().ifPresent(userInfo -> {
                            final String code = userInfo.getId();
                            final UserInfo oldUserInfo = userInfoMap.get(code);
                            if (oldUserInfo != null) {
                                userInfo.setCreatedAt(oldUserInfo.getCreatedAt());
                            }
                            userInfoMap.put(code, userInfo);
                        });
                        searchLogList.add(searchLog);
                    }
                });

        if (!userInfoMap.isEmpty()) {
            final List<UserInfo> insertList = new ArrayList<>(userInfoMap.values());
            final List<UserInfo> updateList = new ArrayList<>();
            final UserInfoBhv userInfoBhv = ComponentUtil.getComponent(UserInfoBhv.class);
            userInfoBhv.selectList(cb -> {
                cb.query().setId_InScope(userInfoMap.keySet());
                cb.fetchFirst(userInfoMap.size());
            }).forEach(userInfo -> {
                final String code = userInfo.getId();
                final UserInfo entity = userInfoMap.get(code);
                entity.setId(userInfo.getId());
                entity.setCreatedAt(userInfo.getCreatedAt());
                updateList.add(entity);
                insertList.remove(entity);
            });
            userInfoBhv.batchInsert(insertList);
            userInfoBhv.batchUpdate(updateList);
            searchLogList.stream().forEach(searchLog -> {
                searchLog.getUserInfo().ifPresent(userInfo -> {
                    searchLog.setUserInfoId(userInfo.getId());
                });
            });
        }

        if (!searchLogList.isEmpty()) {
            storeSearchLogList(searchLogList);
            if (fessConfig.isSuggestSearchLog()) {
                final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();
                suggestHelper.indexFromSearchLog(searchLogList);
            }
        }
    }

    private void storeSearchLogList(final List<SearchLog> searchLogList) {
        final SearchLogBhv searchLogBhv = ComponentUtil.getComponent(SearchLogBhv.class);
        final SearchFieldLogBhv searchFieldLogBhv = ComponentUtil.getComponent(SearchFieldLogBhv.class);
        searchLogBhv.batchUpdate(searchLogList, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
        searchLogList.stream().forEach(searchLog -> {
            final List<SearchFieldLog> fieldLogList = new ArrayList<>();
            searchLog.getSearchFieldLogList().stream().forEach(fieldLog -> {
                fieldLog.setSearchLogId(searchLog.getId());
                fieldLogList.add(fieldLog);
            });
            searchFieldLogBhv.batchInsert(fieldLogList);
        });
    }

    protected void processClickLogQueue(final Queue<ClickLog> queue) {
        final Map<String, Integer> clickCountMap = new HashMap<>();
        final List<ClickLog> clickLogList = new ArrayList<>();
        for (final ClickLog clickLog : queue) {
            try {
                final SearchLogBhv searchLogBhv = ComponentUtil.getComponent(SearchLogBhv.class);
                searchLogBhv.selectEntity(cb -> {
                    cb.query().setQueryId_Equal(clickLog.getQueryId());
                }).ifPresent(entity -> {
                    clickLogList.add(clickLog);
                    final String docId = clickLog.getDocId();
                    Integer countObj = clickCountMap.get(docId);
                    if (countObj == null) {
                        countObj = Integer.valueOf(1);
                    } else {
                        countObj = countObj.intValue() + 1;
                    }
                    clickCountMap.put(docId, countObj);
                }).orElse(() -> {
                    logger.warn("Not Found for SearchLog: " + clickLog);
                });
            } catch (final Exception e) {
                logger.warn("Failed to process: " + clickLog, e);
            }
        }
        if (!clickLogList.isEmpty()) {
            try {
                final ClickLogBhv clickLogBhv = ComponentUtil.getComponent(ClickLogBhv.class);
                clickLogBhv.batchInsert(clickLogList);
            } catch (final Exception e) {
                logger.warn("Failed to insert: " + clickLogList, e);
            }
        }

        if (!clickCountMap.isEmpty()) {
            final SearchService searchService = ComponentUtil.getComponent(SearchService.class);
            try {
                searchService.bulkUpdate(builder -> {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    searchService.getDocumentListByDocIds(clickCountMap.keySet().toArray(new String[clickCountMap.size()]),
                            new String[] { fessConfig.getIndexFieldDocId() }, OptionalThing.of(FessUserBean.empty())).forEach(
                            doc -> {
                                final String id = DocumentUtil.getValue(doc, fessConfig.getIndexFieldId(), String.class);
                                final String docId = DocumentUtil.getValue(doc, fessConfig.getIndexFieldDocId(), String.class);
                                if (id != null && docId != null && clickCountMap.containsKey(docId)) {
                                    final Integer count = clickCountMap.get(docId);
                                    final Script script =
                                            new Script("ctx._source." + fessConfig.getIndexFieldClickCount() + "+=" + count.toString());
                                    final Map<String, Object> upsertMap = new HashMap<>();
                                    upsertMap.put(fessConfig.getIndexFieldClickCount(), count);
                                    builder.add(new UpdateRequest(fessConfig.getIndexDocumentUpdateIndex(), fessConfig
                                            .getIndexDocumentType(), id).script(script).upsert(upsertMap));
                                }
                            });
                });
            } catch (final Exception e) {
                logger.warn("Failed to update clickCounts", e);
            }
        }
    }
}
