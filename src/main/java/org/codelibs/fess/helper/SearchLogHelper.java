/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.concurrent.CommonPoolUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.log.exbhv.ClickLogBhv;
import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.es.log.exbhv.UserInfoBhv;
import org.codelibs.fess.es.log.exentity.ClickLog;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.es.log.exentity.UserInfo;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.script.Script;
import org.lastaflute.web.util.LaRequestUtil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class SearchLogHelper {
    private static final Logger logger = LogManager.getLogger(SearchLogHelper.class);

    protected long userCheckInterval = 10 * 60 * 1000L;// 10 min

    protected int userInfoCacheSize = 10000;

    protected volatile Queue<SearchLog> searchLogQueue = new ConcurrentLinkedQueue<>();

    protected volatile Queue<ClickLog> clickLogQueue = new ConcurrentLinkedQueue<>();

    protected LoadingCache<String, UserInfo> userInfoCache;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        userInfoCache = CacheBuilder.newBuilder()//
                .maximumSize(userInfoCacheSize)//
                .expireAfterWrite(userCheckInterval, TimeUnit.MILLISECONDS)//
                .build(new CacheLoader<String, UserInfo>() {
                    @Override
                    public UserInfo load(final String key) throws Exception {
                        return storeUserInfo(key);
                    }
                });
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
                searchLog.setUserInfo(getUserInfo(userCode));
            }
        }

        searchLog.setRoles(roleQueryHelper.build(params.getType()).stream().toArray(n -> new String[n]));
        searchLog.setQueryId(queryId);
        searchLog.setHitCount(queryResponseList.getAllRecordCount());
        searchLog.setHitCountRelation(queryResponseList.getAllRecordCountRelation());
        searchLog.setResponseTime(queryResponseList.getExecTime());
        searchLog.setQueryTime(queryResponseList.getQueryTime());
        searchLog.setSearchWord(StringUtils.abbreviate(query, 1000));
        searchLog.setRequestedAt(requestedTime);
        searchLog.setSearchQuery(StringUtils.abbreviate(queryResponseList.getSearchQuery(), 1000));
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
        } else if (Constants.SEARCH_LOG_ACCESS_TYPE_GSA.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_GSA);
        } else if (Constants.SEARCH_LOG_ACCESS_TYPE_OTHER.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_OTHER);
        } else if (Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN);
        } else {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_WEB);
        }
        final Object languages = request.getAttribute(Constants.REQUEST_LANGUAGES);
        if (languages != null) {
            searchLog.setLanguages(StringUtils.join((String[]) languages, ","));
        } else {
            searchLog.setLanguages(StringUtil.EMPTY);
        }
        final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        if (StringUtil.isNotBlank(virtualHostKey)) {
            searchLog.setVirtualHost(virtualHostKey);
        } else {
            searchLog.setVirtualHost(StringUtil.EMPTY);
        }

        @SuppressWarnings("unchecked")
        final Map<String, List<String>> fieldLogMap = (Map<String, List<String>>) request.getAttribute(Constants.FIELD_LOGS);
        if (fieldLogMap != null) {
            final int queryMaxLength = ComponentUtil.getFessConfig().getQueryMaxLengthAsInteger();
            for (final Map.Entry<String, List<String>> logEntry : fieldLogMap.entrySet()) {
                for (final String value : logEntry.getValue()) {
                    searchLog.addSearchFieldLogValue(logEntry.getKey(), StringUtils.abbreviate(value, queryMaxLength));
                }
            }
        }

        addDocumentsInResponse(queryResponseList, searchLog);

        searchLogQueue.add(searchLog);
    }

    protected void addDocumentsInResponse(final QueryResponseList queryResponseList, final SearchLog searchLog) {
        if (ComponentUtil.getFessConfig().isLoggingSearchDocsEnabled()) {
            queryResponseList.stream().forEach(res -> {
                final Map<String, Object> map = new HashMap<>();
                Arrays.stream(ComponentUtil.getFessConfig().getLoggingSearchDocsFieldsAsArray()).forEach(s -> map.put(s, res.get(s)));
                searchLog.addDocument(map);
            });
        }
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

    protected UserInfo storeUserInfo(final String userCode) {
        final UserInfoBhv userInfoBhv = ComponentUtil.getComponent(UserInfoBhv.class);

        final LocalDateTime now = ComponentUtil.getSystemHelper().getCurrentTimeAsLocalDateTime();
        final UserInfo userInfo = userInfoBhv.selectByPK(userCode).map(e -> {
            e.setUpdatedAt(now);
            return e;
        }).orElseGet(() -> {
            final UserInfo e = new UserInfo();
            e.setId(userCode);
            e.setCreatedAt(now);
            e.setUpdatedAt(now);
            return e;
        });
        CommonPoolUtil.execute(() -> userInfoBhv.insertOrUpdate(userInfo));
        return userInfo;
    }

    public OptionalEntity<UserInfo> getUserInfo(final String userCode) {
        if (StringUtil.isNotBlank(userCode)) {
            try {
                return OptionalEntity.of(userInfoCache.get(userCode));
            } catch (final ExecutionException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to access UserInfo cache.", e);
                }
            }
        }
        return OptionalEntity.empty();
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

    protected void storeSearchLogList(final List<SearchLog> searchLogList) {
        final SearchLogBhv searchLogBhv = ComponentUtil.getComponent(SearchLogBhv.class);
        searchLogBhv.batchUpdate(searchLogList, op -> {
            op.setRefreshPolicy(Constants.TRUE);
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
                        countObj = 1;
                    } else {
                        countObj = countObj.intValue() + 1;
                    }
                    clickCountMap.put(docId, countObj);
                }).orElse(() -> {
                    logger.warn("Not Found for SearchLog: {}", clickLog);
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
            final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
            try {
                searchHelper.bulkUpdate(builder -> {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    searchHelper.getDocumentListByDocIds(clickCountMap.keySet().toArray(new String[clickCountMap.size()]),
                            new String[] { fessConfig.getIndexFieldDocId(), fessConfig.getIndexFieldLang() },
                            OptionalThing.of(FessUserBean.empty()), SearchRequestType.ADMIN_SEARCH).forEach(
                            doc -> {
                                final String id = DocumentUtil.getValue(doc, fessConfig.getIndexFieldId(), String.class);
                                final String docId = DocumentUtil.getValue(doc, fessConfig.getIndexFieldDocId(), String.class);
                                if (id != null && docId != null && clickCountMap.containsKey(docId)) {
                                    final Integer count = clickCountMap.get(docId);
                                    final Script script =
                                            ComponentUtil.getLanguageHelper().createScript(doc,
                                                    "ctx._source." + fessConfig.getIndexFieldClickCount() + "+=" + count.toString());
                                    final Map<String, Object> upsertMap = new HashMap<>();
                                    upsertMap.put(fessConfig.getIndexFieldClickCount(), count);
                                    builder.add(new UpdateRequest(fessConfig.getIndexDocumentUpdateIndex(), id).script(script).upsert(
                                            upsertMap));
                                }
                            });
                });
            } catch (final Exception e) {
                logger.warn("Failed to update clickCounts", e);
            }
        }
    }

    public void setUserCheckInterval(final long userCheckInterval) {
        this.userCheckInterval = userCheckInterval;
    }

    public void setUserInfoCacheSize(final int userInfoCacheSize) {
        this.userInfoCacheSize = userInfoCacheSize;
    }
}
