/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SearchLogPager;
import org.codelibs.fess.es.log.allcommon.EsPagingResultBean;
import org.codelibs.fess.es.log.cbean.ClickLogCB;
import org.codelibs.fess.es.log.cbean.FavoriteLogCB;
import org.codelibs.fess.es.log.cbean.SearchLogCB;
import org.codelibs.fess.es.log.cbean.UserInfoCB;
import org.codelibs.fess.es.log.exbhv.ClickLogBhv;
import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.es.log.exbhv.UserInfoBhv;
import org.codelibs.fess.es.log.exentity.ClickLog;
import org.codelibs.fess.es.log.exentity.FavoriteLog;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.es.log.exentity.UserInfo;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.taglib.FessFunctions;
import org.dbflute.optional.OptionalEntity;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.BucketOrder;
import org.opensearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.opensearch.search.aggregations.bucket.histogram.Histogram;
import org.opensearch.search.aggregations.bucket.terms.Terms;
import org.opensearch.search.aggregations.metrics.Avg;
import org.opensearch.search.aggregations.metrics.Cardinality;

public class SearchLogService {

    private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    private static final String COUNT = "count";

    private static final String KEY = "key";

    private static final String ID = "id";

    private static final String USER_INFO_ID = "userInfoId";

    private static final String QUERY_TIME = "queryTime";

    private static final Logger logger = LogManager.getLogger(SearchLogService.class);

    @Resource
    private SearchLogBhv searchLogBhv;

    @Resource
    private ClickLogBhv clickLogBhv;

    @Resource
    private FavoriteLogBhv favoriteLogBhv;

    @Resource
    private UserInfoBhv userInfoBhv;

    @Resource
    private SystemHelper systemHelper;

    @Resource
    protected FessConfig fessConfig;

    public void deleteBefore(final int days) {
        searchLogBhv.queryDelete(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

    public List<?> getSearchLogList(final SearchLogPager pager) {
        final EsPagingResultBean<?> list;
        if (SearchLogPager.LOG_TYPE_USERINFO.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) userInfoBhv.selectPage(cb -> {
                cb.paging(pager.getPageSize(), pager.getCurrentPageNumber());
                cb.query().addOrderBy_UpdatedAt_Desc();
                createUserInfoCondition(pager, cb);
            });
        } else if (SearchLogPager.LOG_TYPE_CLICK.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) clickLogBhv.selectPage(cb -> {
                cb.paging(pager.getPageSize(), pager.getCurrentPageNumber());
                cb.query().addOrderBy_RequestedAt_Desc();
                createClickLogCondition(pager, cb);
            });
        } else if (SearchLogPager.LOG_TYPE_CLICK_COUNT.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) clickLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createClickLogCondition(pager, cb);
                cb.aggregation().setUrl_Terms(SearchLogPager.LOG_TYPE_CLICK_COUNT, op -> {
                    op.size(pager.getPageSize());
                    if (fessConfig.getSearchlogAggShardSizeAsInteger() >= 0) {
                        op.shardSize(fessConfig.getSearchlogAggShardSizeAsInteger());
                    }
                }, null);
            });
            final Terms agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_CLICK_COUNT);
            final List<? extends Terms.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                map.put(COUNT, e.getDocCount());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_FAVORITE.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) favoriteLogBhv.selectPage(cb -> {
                cb.paging(pager.getPageSize(), pager.getCurrentPageNumber());
                cb.query().addOrderBy_CreatedAt_Desc();
                createFavoriteLogCondition(pager, cb);
            });
        } else if (SearchLogPager.LOG_TYPE_FAVORITE_COUNT.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) favoriteLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createFavoriteLogCondition(pager, cb);
                cb.aggregation().setUrl_Terms(SearchLogPager.LOG_TYPE_FAVORITE_COUNT, op -> {
                    op.size(pager.getPageSize());
                    if (fessConfig.getSearchlogAggShardSizeAsInteger() >= 0) {
                        op.shardSize(fessConfig.getSearchlogAggShardSizeAsInteger());
                    }
                }, null);
            });
            final Terms agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_FAVORITE_COUNT);
            final List<? extends Terms.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                map.put(COUNT, e.getDocCount());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_SEARCH_COUNT_HOUR.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createSearchLogCondition(pager, cb);
                cb.aggregation().setRequestedAt_DateHistogram(SearchLogPager.LOG_TYPE_SEARCH_COUNT_HOUR, op -> {
                    op.calendarInterval(DateHistogramInterval.HOUR);
                    op.minDocCount(0);
                    op.order(BucketOrder.key(true));
                }, null);
            });
            final Histogram agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_SEARCH_COUNT_HOUR);
            final List<? extends Histogram.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                map.put(COUNT, e.getDocCount());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_SEARCH_COUNT_DAY.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createSearchLogCondition(pager, cb);
                cb.aggregation().setRequestedAt_DateHistogram(SearchLogPager.LOG_TYPE_SEARCH_COUNT_DAY, op -> {
                    op.calendarInterval(DateHistogramInterval.DAY);
                    op.minDocCount(0);
                    op.order(BucketOrder.key(true));
                }, null);
            });
            final Histogram agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_SEARCH_COUNT_DAY);
            final List<? extends Histogram.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                map.put(COUNT, e.getDocCount());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_SEARCH_USER_HOUR.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createSearchLogCondition(pager, cb);
                cb.aggregation().setRequestedAt_DateHistogram(SearchLogPager.LOG_TYPE_SEARCH_USER_HOUR, op -> {
                    op.calendarInterval(DateHistogramInterval.HOUR);
                    op.subAggregation(AggregationBuilders.cardinality(USER_INFO_ID).field(USER_INFO_ID));
                    op.minDocCount(0);
                    op.order(BucketOrder.key(true));
                }, null);
            });
            final Histogram agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_SEARCH_USER_HOUR);
            final List<? extends Histogram.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                final Cardinality value = e.getAggregations().get(USER_INFO_ID);
                map.put(COUNT, value.getValue());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_SEARCH_USER_DAY.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createSearchLogCondition(pager, cb);
                cb.aggregation().setRequestedAt_DateHistogram(SearchLogPager.LOG_TYPE_SEARCH_USER_DAY, op -> {
                    op.calendarInterval(DateHistogramInterval.DAY);
                    op.subAggregation(AggregationBuilders.cardinality(USER_INFO_ID).field(USER_INFO_ID));
                    op.minDocCount(0);
                    op.order(BucketOrder.key(true));
                }, null);
            });
            final Histogram agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_SEARCH_USER_DAY);
            final List<? extends Histogram.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                final Cardinality value = e.getAggregations().get(USER_INFO_ID);
                map.put(COUNT, value.getValue());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_SEARCH_REQTIMEAVG_HOUR.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createSearchLogCondition(pager, cb);
                cb.aggregation().setRequestedAt_DateHistogram(SearchLogPager.LOG_TYPE_SEARCH_REQTIMEAVG_HOUR, op -> {
                    op.calendarInterval(DateHistogramInterval.HOUR);
                    op.subAggregation(AggregationBuilders.avg(QUERY_TIME).field(QUERY_TIME));
                    op.minDocCount(0);
                    op.order(BucketOrder.key(true));
                }, null);
            });
            final Histogram agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_SEARCH_REQTIMEAVG_HOUR);
            final List<? extends Histogram.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                final Avg value = e.getAggregations().get(QUERY_TIME);
                map.put(COUNT, value.getValueAsString());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_SEARCH_REQTIMEAVG_DAY.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createSearchLogCondition(pager, cb);
                cb.aggregation().setRequestedAt_DateHistogram(SearchLogPager.LOG_TYPE_SEARCH_REQTIMEAVG_DAY, op -> {
                    op.calendarInterval(DateHistogramInterval.DAY);
                    op.subAggregation(AggregationBuilders.avg(QUERY_TIME).field(QUERY_TIME));
                    op.minDocCount(0);
                    op.order(BucketOrder.key(true));
                }, null);
            });
            final Histogram agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_SEARCH_REQTIMEAVG_DAY);
            final List<? extends Histogram.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                final Avg value = e.getAggregations().get(QUERY_TIME);
                map.put(COUNT, value.getValueAsString());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_SEARCH_KEYWORD.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createSearchLogCondition(pager, cb);
                cb.aggregation().setSearchWord_Terms(SearchLogPager.LOG_TYPE_SEARCH_KEYWORD, op -> {
                    op.size(pager.getPageSize());
                    if (fessConfig.getSearchlogAggShardSizeAsInteger() >= 0) {
                        op.shardSize(fessConfig.getSearchlogAggShardSizeAsInteger());
                    }
                }, null);
            });
            final Terms agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_SEARCH_KEYWORD);
            final List<? extends Terms.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                map.put(COUNT, e.getDocCount());
                return map;
            }).collect(Collectors.toList());
        } else if (SearchLogPager.LOG_TYPE_SEARCH_ZEROHIT.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.fetchFirst(0);
                createSearchLogCondition(pager, cb);
                cb.query().setHitCount_Equal(0L);
                cb.aggregation().setSearchWord_Terms(SearchLogPager.LOG_TYPE_SEARCH_ZEROHIT, op -> {
                    op.size(pager.getPageSize());
                    if (fessConfig.getSearchlogAggShardSizeAsInteger() >= 0) {
                        op.shardSize(fessConfig.getSearchlogAggShardSizeAsInteger());
                    }
                }, null);
            });
            final Terms agg = list.getAggregations().get(SearchLogPager.LOG_TYPE_SEARCH_ZEROHIT);
            final List<? extends Terms.Bucket> buckets = agg.getBuckets();
            updatePagerByAgg(pager, buckets.size());
            return buckets.stream().map(e -> {
                final Map<String, Object> map = new HashMap<>();
                map.put(ID, Base64.getUrlEncoder().encodeToString(e.getKeyAsString().getBytes(StandardCharsets.UTF_8)));
                map.put(KEY, e.getKeyAsString());
                map.put(COUNT, e.getDocCount());
                return map;
            }).collect(Collectors.toList());
            //        } else if (SearchLogPager.LOG_TYPE_SEARCH_ZEROCLICK.equalsIgnoreCase(pager.logType)) {
            //            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
            //                cb.fetchFirst(0);
            //                createSearchLogCondition(pager, cb);
            //                // TODO 0 clicked
            //                });
        } else {
            list = (EsPagingResultBean<?>) searchLogBhv.selectPage(cb -> {
                cb.paging(pager.getPageSize(), pager.getCurrentPageNumber());
                cb.query().addOrderBy_RequestedAt_Desc();
                createSearchLogCondition(pager, cb);
            });
        }

        // update pager
        BeanUtil.copyBeanToBean(list, pager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        pager.setPageNumberList(list.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return list;
    }

    private void updatePagerByAgg(final SearchLogPager pager, final int size) {
        pager.setAllPageCount(1);
        pager.setAllRecordCount(size);
        pager.setCurrentPageNumber(1);
        pager.setExistNextPage(false);
        pager.setExistPrePage(false);
        pager.setPageSize(pager.getPageSize());
    }

    private void createSearchLogCondition(final SearchLogPager pager, final SearchLogCB cb) {
        if (StringUtil.isNotBlank(pager.queryId)) {
            cb.query().setQueryId_Term(pager.queryId);
        }
        if (StringUtil.isNotBlank(pager.userSessionId)) {
            cb.query().setUserSessionId_Term(pager.userSessionId);
        }
        if (StringUtil.isNotBlank(pager.accessType)) {
            cb.query().setAccessType_Term(pager.accessType);
        }
        if (StringUtil.isNotBlank(pager.requestedTimeRange)) {
            final String[] values = pager.requestedTimeRange.split(" - ");
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);
            try {
                if (values.length > 0) {
                    cb.query().setRequestedAt_GreaterEqual(parseDateTime(values[0], formatter));
                }
                if (values.length > 1) {
                    cb.query().setRequestedAt_LessEqual(LocalDateTime.parse(values[1], formatter));
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse {}", pager.requestedTimeRange, e);
                }
            }
        }
    }

    private void createFavoriteLogCondition(final SearchLogPager pager, final FavoriteLogCB cb) {
        if (StringUtil.isNotBlank(pager.queryId)) {
            cb.query().setQueryId_Term(pager.queryId);
        }
        if (StringUtil.isNotBlank(pager.userSessionId)) {
            cb.query().setUserInfoId_Term(pager.userSessionId);
        }
        if (StringUtil.isNotBlank(pager.requestedTimeRange)) {
            final String[] values = pager.requestedTimeRange.split(" - ");
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);
            try {
                if (values.length > 0) {
                    cb.query().setCreatedAt_GreaterEqual(LocalDateTime.parse(values[0], formatter));
                }
                if (values.length > 1) {
                    cb.query().setCreatedAt_LessEqual(parseDateTime(values[1], formatter));
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse {}", pager.requestedTimeRange, e);
                }
            }
        }
    }

    private void createUserInfoCondition(final SearchLogPager pager, final UserInfoCB cb) {
        if (StringUtil.isNotBlank(pager.userSessionId)) {
            cb.query().setId_Equal(pager.userSessionId);
        }
        if (StringUtil.isNotBlank(pager.requestedTimeRange)) {
            final String[] values = pager.requestedTimeRange.split(" - ");
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);
            try {
                if (values.length > 0) {
                    cb.query().setUpdatedAt_GreaterEqual(LocalDateTime.parse(values[0], formatter));
                }
                if (values.length > 1) {
                    cb.query().setUpdatedAt_LessEqual(LocalDateTime.parse(values[1], formatter));
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse {}", pager.requestedTimeRange, e);
                }
            }
        }
    }

    private void createClickLogCondition(final SearchLogPager pager, final ClickLogCB cb) {
        if (StringUtil.isNotBlank(pager.queryId)) {
            cb.query().setQueryId_Term(pager.queryId);
        }
        if (StringUtil.isNotBlank(pager.userSessionId)) {
            cb.query().setUserSessionId_Term(pager.userSessionId);
        }
        if (StringUtil.isNotBlank(pager.requestedTimeRange)) {
            final String[] values = pager.requestedTimeRange.split(" - ");
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);
            try {
                if (values.length > 0) {
                    cb.query().setRequestedAt_GreaterEqual(LocalDateTime.parse(values[0], formatter));
                }
                if (values.length > 1) {
                    cb.query().setRequestedAt_LessEqual(LocalDateTime.parse(values[1], formatter));
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse {}", pager.requestedTimeRange, e);
                }
            }
        }
    }

    protected LocalDateTime parseDateTime(final String value, final DateTimeFormatter formatter) {
        return LocalDateTime.parse(value, formatter).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public OptionalEntity<?> getSearchLog(final String logType, final String id) {
        if (SearchLogPager.LOG_TYPE_CLICK.equalsIgnoreCase(logType)) {
            return clickLogBhv.selectByPK(id);
        }
        if (SearchLogPager.LOG_TYPE_FAVORITE.equalsIgnoreCase(logType)) {
            return favoriteLogBhv.selectByPK(id);
        }
        if (SearchLogPager.LOG_TYPE_USERINFO.equalsIgnoreCase(logType)) {
            return userInfoBhv.selectByPK(id);
        }
        return searchLogBhv.selectByPK(id);
    }

    public Map<String, String> getSearchLogMap(final String logType, final String id) {
        if (SearchLogPager.LOG_TYPE_USERINFO.equalsIgnoreCase(logType)) {
            return userInfoBhv.selectByPK(id).map(e -> {
                final Map<String, String> params = new LinkedHashMap<>();
                params.put("User Info ID", e.getId());
                params.put("Created Time", FessFunctions.formatDate(e.getCreatedAt()));
                params.put("Updated Time", FessFunctions.formatDate(e.getUpdatedAt()));
                return params;
            }).get();
        }
        if (SearchLogPager.LOG_TYPE_CLICK.equalsIgnoreCase(logType)) {
            return clickLogBhv.selectByPK(id).map(e -> {
                final Map<String, String> params = new LinkedHashMap<>();
                params.put("ID", e.getId());
                params.put("Query ID", e.getQueryId());
                params.put("Doc ID", e.getDocId());
                params.put("User Session ID", e.getUserSessionId());
                params.put("URL", e.getUrl());
                params.put("URL ID", e.getUrlId());
                params.put("Order", toNumberString(e.getOrder()));
                params.put("Query Requested Time", FessFunctions.formatDate(e.getQueryRequestedAt()));
                params.put("Requested Time", FessFunctions.formatDate(e.getRequestedAt()));
                return params;
            }).get();
        }
        if (SearchLogPager.LOG_TYPE_FAVORITE.equalsIgnoreCase(logType)) {
            return favoriteLogBhv.selectByPK(id).map(e -> {
                final Map<String, String> params = new LinkedHashMap<>();
                params.put("ID", e.getId());
                params.put("Query ID", e.getQueryId());
                params.put("Doc ID", e.getDocId());
                params.put("User Info ID", e.getUserInfoId());
                params.put("URL", e.getUrl());
                params.put("Created Time", FessFunctions.formatDate(e.getCreatedAt()));
                params.put("Requested Time", FessFunctions.formatDate(e.getRequestedAt()));
                return params;
            }).get();
        }
        return searchLogBhv.selectByPK(id).map(e -> {
            final Map<String, String> params = new LinkedHashMap<>();
            params.put("ID", e.getId());
            params.put("Query ID", e.getQueryId());
            params.put("User Info ID", e.getUserInfoId());
            params.put("User Session ID", e.getUserSessionId());
            params.put("Access Type", e.getAccessType());
            params.put("Search Word", e.getSearchWord());
            params.put("Requested Time", FessFunctions.formatDate(e.getRequestedAt()));
            params.put("Query Time", toNumberString(e.getQueryTime()));
            params.put("Response Time", toNumberString(e.getResponseTime()));
            params.put("Hit Count", toNumberString(e.getHitCount()));
            params.put("Offset", toNumberString(e.getQueryOffset()));
            params.put("Page Size", toNumberString(e.getQueryPageSize()));
            params.put("Client IP", e.getClientIp());
            params.put("Referer", e.getReferer());
            params.put("Languages", e.getLanguages());
            params.put("Virtual Host", e.getVirtualHost());
            params.put("Roles", e.getRoles() != null ? String.join(" ", e.getRoles()) : StringUtil.EMPTY);
            params.put("User Agent", e.getUserAgent());
            e.getSearchFieldLogList().stream().forEach(p -> {
                params.put(p.getFirst(), p.getSecond());
            });
            e.getRequestHeaderList().stream().forEach(p -> {
                params.put(p.getFirst(), p.getSecond());
            });
            return params;
        }).get();
    }

    private String toNumberString(final Number value) {
        return value != null ? value.toString() : StringUtil.EMPTY;
    }

    public void deleteSearchLog(final Object e) {
        if (e instanceof final ClickLog clickLog) {
            clickLogBhv.delete(clickLog);
        } else if (e instanceof final FavoriteLog favoriteLog) {
            favoriteLogBhv.delete(favoriteLog);
        } else if (e instanceof final UserInfo userInfo) {
            userInfoBhv.delete(userInfo);
        } else if (e instanceof final SearchLog searchLog) {
            searchLogBhv.delete(searchLog);
        } else {
            throw new FessSystemException("Unknown log entity: " + e);
        }
    }
}
