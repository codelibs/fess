/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SearchLogPager;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.allcommon.EsPagingResultBean;
import org.codelibs.fess.opensearch.log.cbean.ClickLogCB;
import org.codelibs.fess.opensearch.log.cbean.FavoriteLogCB;
import org.codelibs.fess.opensearch.log.cbean.SearchLogCB;
import org.codelibs.fess.opensearch.log.cbean.UserInfoCB;
import org.codelibs.fess.opensearch.log.exbhv.ClickLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.UserInfoBhv;
import org.codelibs.fess.opensearch.log.exentity.ClickLog;
import org.codelibs.fess.opensearch.log.exentity.FavoriteLog;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.opensearch.log.exentity.UserInfo;
import org.codelibs.fess.taglib.FessFunctions;
import org.dbflute.optional.OptionalEntity;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.BucketOrder;
import org.opensearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.opensearch.search.aggregations.bucket.histogram.Histogram;
import org.opensearch.search.aggregations.bucket.terms.Terms;
import org.opensearch.search.aggregations.metrics.Avg;
import org.opensearch.search.aggregations.metrics.Cardinality;

import jakarta.annotation.Resource;

/**
 * Service class for managing search logs and related analytics.
 *
 * This service provides functionality for querying, aggregating, and managing
 * various types of search logs including search logs, click logs, favorite logs,
 * and user information logs. It supports different aggregation types for
 * analytics and reporting purposes.
 */
public class SearchLogService {

    /** Date format pattern for parsing time ranges. */
    private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /** Field name for count values in aggregation results. */
    private static final String COUNT = "count";

    /** Field name for key values in aggregation results. */
    private static final String KEY = "key";

    /** Field name for ID values in aggregation results. */
    private static final String ID = "id";

    /** Field name for user information ID in aggregations. */
    private static final String USER_INFO_ID = "userInfoId";

    /** Field name for query time in aggregations. */
    private static final String QUERY_TIME = "queryTime";

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(SearchLogService.class);

    /** Behavior handler for search log operations. */
    @Resource
    private SearchLogBhv searchLogBhv;

    /** Behavior handler for click log operations. */
    @Resource
    private ClickLogBhv clickLogBhv;

    /** Behavior handler for favorite log operations. */
    @Resource
    private FavoriteLogBhv favoriteLogBhv;

    /** Behavior handler for user information operations. */
    @Resource
    private UserInfoBhv userInfoBhv;

    /** System helper for date/time operations. */
    @Resource
    private SystemHelper systemHelper;

    /** Fess configuration settings. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor for creating a new SearchLogService instance.
     */
    public SearchLogService() {
        // Default constructor
    }

    /**
     * Deletes search logs older than the specified number of days.
     *
     * @param days Number of days to keep (logs older than this will be deleted)
     */
    public void deleteBefore(final int days) {
        searchLogBhv.queryDelete(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

    /**
     * Retrieves a list of search logs based on the specified pager criteria.
     *
     * This method supports various log types including search logs, click logs,
     * favorite logs, user information, and different aggregation types for analytics.
     *
     * @param pager The search log pager containing filter criteria and pagination settings
     * @return List of search log entries or aggregated data based on the log type
     */
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

    /**
     * Updates the pager with aggregation result information.
     *
     * @param pager The search log pager to update
     * @param size The size of the aggregation results
     */
    private void updatePagerByAgg(final SearchLogPager pager, final int size) {
        pager.setAllPageCount(1);
        pager.setAllRecordCount(size);
        pager.setCurrentPageNumber(1);
        pager.setExistNextPage(false);
        pager.setExistPrePage(false);
        pager.setPageSize(pager.getPageSize());
    }

    /**
     * Creates search conditions for search log queries based on pager criteria.
     *
     * @param pager The search log pager containing filter criteria
     * @param cb The search log condition bean to configure
     */
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

    /**
     * Creates search conditions for favorite log queries based on pager criteria.
     *
     * @param pager The search log pager containing filter criteria
     * @param cb The favorite log condition bean to configure
     */
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

    /**
     * Creates search conditions for user info queries based on pager criteria.
     *
     * @param pager The search log pager containing filter criteria
     * @param cb The user info condition bean to configure
     */
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

    /**
     * Creates search conditions for click log queries based on pager criteria.
     *
     * @param pager The search log pager containing filter criteria
     * @param cb The click log condition bean to configure
     */
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

    /**
     * Parses a date/time string and converts it to UTC timezone.
     *
     * @param value The date/time string to parse
     * @param formatter The date/time formatter to use
     * @return LocalDateTime in UTC timezone
     */
    protected LocalDateTime parseDateTime(final String value, final DateTimeFormatter formatter) {
        return LocalDateTime.parse(value, formatter).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    /**
     * Retrieves a specific search log entry by log type and ID.
     *
     * @param logType The type of log to retrieve (search, click, favorite, userinfo)
     * @param id The ID of the log entry
     * @return Optional entity containing the log entry if found
     */
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

    /**
     * Retrieves a search log entry as a formatted map of field names and values.
     *
     * @param logType The type of log to retrieve (search, click, favorite, userinfo)
     * @param id The ID of the log entry
     * @return Map containing formatted field names and values for display
     */
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

    /**
     * Converts a number to its string representation, handling null values.
     *
     * @param value The number to convert
     * @return String representation of the number, or empty string if null
     */
    private String toNumberString(final Number value) {
        return value != null ? value.toString() : StringUtil.EMPTY;
    }

    /**
     * Deletes a search log entry based on its type.
     *
     * @param e The log entity to delete (ClickLog, FavoriteLog, UserInfo, or SearchLog)
     * @throws FessSystemException if the entity type is not recognized
     */
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
