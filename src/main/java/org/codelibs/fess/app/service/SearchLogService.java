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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.codelibs.fess.opensearch.log.exbhv.ChatLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.ClickLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.UserInfoBhv;
import org.codelibs.fess.opensearch.log.exentity.ClickLog;
import org.codelibs.fess.opensearch.log.exentity.FavoriteLog;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.opensearch.log.exentity.UserInfo;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.CsvUtil;
import org.dbflute.optional.OptionalEntity;

import com.orangesignal.csv.CsvWriter;

import jakarta.annotation.Resource;

/**
 * Service class for managing raw search logs.
 *
 * This service provides functionality for querying and managing
 * various types of search logs including search logs, click logs, favorite logs,
 * and user information logs.
 */
public class SearchLogService {

    /** Date format pattern for parsing time ranges. */
    private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(SearchLogService.class);

    /** CSV columns of the search log: the entity field names. */
    protected static final List<String> SEARCH_LOG_CSV_COLUMNS = List.of("id", "requestedAt", "searchWord", "hitCount", "hitCountRelation",
            "queryOffset", "queryPageSize", "queryTime", "responseTime", "accessType", "queryId", "userInfoId", "userSessionId", "user",
            "roles", "clientIp", "referer", "userAgent", "languages", "virtualHost");

    /** CSV columns of the click log: the entity field names. */
    protected static final List<String> CLICK_LOG_CSV_COLUMNS = List.of("id", "requestedAt", "queryRequestedAt", "searchWord", "accessType",
            "rank", "order", "url", "urlId", "docId", "queryId", "userSessionId");

    /** CSV columns of the favorite log: the entity field names. */
    protected static final List<String> FAVORITE_LOG_CSV_COLUMNS = List.of("id", "createdAt", "url", "docId", "queryId", "userInfoId");

    /** CSV columns of the user info: the entity field names. */
    protected static final List<String> USER_INFO_CSV_COLUMNS = List.of("id", "createdAt", "updatedAt");

    /** Behavior handler for search log operations. */
    @Resource
    protected SearchLogBhv searchLogBhv;

    /** Behavior handler for click log operations. */
    @Resource
    protected ClickLogBhv clickLogBhv;

    /** Behavior handler for favorite log operations. */
    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    /** Behavior handler for chat log operations. */
    @Resource
    protected ChatLogBhv chatLogBhv;

    /** Behavior handler for user information operations. */
    @Resource
    protected UserInfoBhv userInfoBhv;

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
     * Deletes click logs older than the specified number of days.
     *
     * @param days Number of days to keep (logs older than this will be deleted)
     */
    public void deleteClickLogBefore(final int days) {
        clickLogBhv.queryDelete(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

    /**
     * Deletes favorite logs older than the specified number of days.
     *
     * @param days Number of days to keep (logs older than this will be deleted)
     */
    public void deleteFavoriteLogBefore(final int days) {
        favoriteLogBhv.queryDelete(cb -> {
            cb.query().setCreatedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

    /**
     * Deletes chat logs older than the specified number of days.
     *
     * @param days Number of days to keep (logs older than this will be deleted)
     */
    public void deleteChatLogBefore(final int days) {
        chatLogBhv.queryDelete(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

    /**
     * Retrieves a list of search logs based on the specified pager criteria.
     *
     * This method supports the search, click, favorite and user information log types.
     * Any other log type is handled as the search log.
     *
     * @param pager The search log pager containing filter criteria and pagination settings
     * @return List of log entries based on the log type
     */
    public List<?> getSearchLogList(final SearchLogPager pager) {
        pager.logType = SearchLogPager.normalizeLogType(pager.logType);
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
        } else if (SearchLogPager.LOG_TYPE_FAVORITE.equalsIgnoreCase(pager.logType)) {
            list = (EsPagingResultBean<?>) favoriteLogBhv.selectPage(cb -> {
                cb.paging(pager.getPageSize(), pager.getCurrentPageNumber());
                cb.query().addOrderBy_CreatedAt_Desc();
                createFavoriteLogCondition(pager, cb);
            });
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
     * Writes every log entry matching the pager criteria as CSV, newest first: a header row of the
     * entity field names followed by one row per entry. The paging of the pager is ignored and the
     * number of rows is not limited, so the entries are read with a cursor and written one by one.
     * The writer is flushed but not closed.
     *
     * @param pager The search log pager containing the filter criteria and the log type
     * @param writer The writer to write the CSV to
     * @throws IOException if writing fails
     */
    public void exportCsv(final SearchLogPager pager, final Writer writer) throws IOException {
        pager.logType = SearchLogPager.normalizeLogType(pager.logType);
        // not closed: that would close the writer of the caller
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, CsvUtil.createCsvConfig());
        try {
            if (SearchLogPager.LOG_TYPE_USERINFO.equals(pager.logType)) {
                csvWriter.writeValues(USER_INFO_CSV_COLUMNS);
                userInfoBhv.selectCursor(cb -> {
                    cb.query().addOrderBy_UpdatedAt_Desc();
                    createUserInfoCondition(pager, cb);
                }, e -> writeRow(csvWriter, e.getId(), e.getCreatedAt(), e.getUpdatedAt()));
            } else if (SearchLogPager.LOG_TYPE_CLICK.equals(pager.logType)) {
                csvWriter.writeValues(CLICK_LOG_CSV_COLUMNS);
                clickLogBhv.selectCursor(cb -> {
                    cb.query().addOrderBy_RequestedAt_Desc();
                    createClickLogCondition(pager, cb);
                }, e -> writeRow(csvWriter, e.getId(), e.getRequestedAt(), e.getQueryRequestedAt(), e.getSearchWord(), e.getAccessType(),
                        e.getRank(), e.getOrder(), e.getUrl(), e.getUrlId(), e.getDocId(), e.getQueryId(), e.getUserSessionId()));
            } else if (SearchLogPager.LOG_TYPE_FAVORITE.equals(pager.logType)) {
                csvWriter.writeValues(FAVORITE_LOG_CSV_COLUMNS);
                favoriteLogBhv.selectCursor(cb -> {
                    cb.query().addOrderBy_CreatedAt_Desc();
                    createFavoriteLogCondition(pager, cb);
                }, e -> writeRow(csvWriter, e.getId(), e.getCreatedAt(), e.getUrl(), e.getDocId(), e.getQueryId(), e.getUserInfoId()));
            } else {
                csvWriter.writeValues(SEARCH_LOG_CSV_COLUMNS);
                searchLogBhv.selectCursor(cb -> {
                    cb.query().addOrderBy_RequestedAt_Desc();
                    createSearchLogCondition(pager, cb);
                }, e -> writeRow(csvWriter, e.getId(), e.getRequestedAt(), e.getSearchWord(), e.getHitCount(), e.getHitCountRelation(),
                        e.getQueryOffset(), e.getQueryPageSize(), e.getQueryTime(), e.getResponseTime(), e.getAccessType(), e.getQueryId(),
                        e.getUserInfoId(), e.getUserSessionId(), e.getUser(), e.getRoles() != null ? String.join(" ", e.getRoles()) : null,
                        e.getClientIp(), e.getReferer(), e.getUserAgent(), e.getLanguages(), e.getVirtualHost()));
            }
            csvWriter.flush();
        } catch (final UncheckedIOException e) {
            // thrown by the cursor handler, which cannot throw a checked exception
            throw e.getCause();
        }
    }

    /**
     * Writes one CSV row, formatting a date like the details page and guarding a text against formulas.
     *
     * @param csvWriter The CSV writer
     * @param values The cell values
     * @throws UncheckedIOException if writing fails
     */
    private void writeRow(final CsvWriter csvWriter, final Object... values) {
        try {
            csvWriter.writeValues(Arrays.stream(values)
                    .map(value -> CsvUtil.toCell(value instanceof final LocalDateTime date ? FessFunctions.formatDate(date) : value))
                    .toList());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
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
        if (StringUtil.isNotBlank(pager.searchWord)) {
            cb.query().setSearchWord_Term(pager.searchWord);
        }
        if (SearchLogPager.HIT_COUNT_ZERO.equals(pager.hitCount)) {
            cb.query().setHitCount_Equal(0L);
        } else if (SearchLogPager.HIT_COUNT_NONZERO.equals(pager.hitCount)) {
            cb.query().setHitCount_GreaterThan(0L);
        }
        final LocalDateTime[] range = parseRequestedTimeRange(pager.requestedTimeRange);
        if (range[0] != null) {
            cb.query().setRequestedAt_GreaterEqual(range[0]);
        }
        if (range[1] != null) {
            cb.query().setRequestedAt_LessThan(endOfMinute(range[1]));
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
        final LocalDateTime[] range = parseRequestedTimeRange(pager.requestedTimeRange);
        if (range[0] != null) {
            cb.query().setCreatedAt_GreaterEqual(range[0]);
        }
        if (range[1] != null) {
            cb.query().setCreatedAt_LessThan(endOfMinute(range[1]));
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
        final LocalDateTime[] range = parseRequestedTimeRange(pager.requestedTimeRange);
        if (range[0] != null) {
            cb.query().setUpdatedAt_GreaterEqual(range[0]);
        }
        if (range[1] != null) {
            cb.query().setUpdatedAt_LessThan(endOfMinute(range[1]));
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
        final LocalDateTime[] range = parseRequestedTimeRange(pager.requestedTimeRange);
        if (range[0] != null) {
            cb.query().setRequestedAt_GreaterEqual(range[0]);
        }
        if (range[1] != null) {
            cb.query().setRequestedAt_LessThan(endOfMinute(range[1]));
        }
    }

    /**
     * Parses the requested time range ("yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm") and converts both ends to UTC.
     *
     * @param requestedTimeRange The time range string in the system default time zone
     * @return An array of two elements (start, end) in UTC; an element is null when absent or unparsable
     */
    protected LocalDateTime[] parseRequestedTimeRange(final String requestedTimeRange) {
        final LocalDateTime[] result = new LocalDateTime[2];
        if (StringUtil.isBlank(requestedTimeRange)) {
            return result;
        }
        final String[] values = requestedTimeRange.split(" - ");
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);
        for (int i = 0; i < values.length && i < 2; i++) {
            try {
                result[i] = parseDateTime(values[i].trim(), formatter);
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse {}", requestedTimeRange, e);
                }
            }
        }
        return result;
    }

    /**
     * Returns the exclusive upper bound for the end of a requested time range. The range is entered with minute
     * precision, so its end covers the whole minute: "23:59" includes a log written at 23:59:30.
     *
     * @param end The end of the range in UTC
     * @return The start of the following minute
     */
    protected LocalDateTime endOfMinute(final LocalDateTime end) {
        return end.plusMinutes(1);
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
                params.put("Search Word", e.getSearchWord());
                params.put("Access Type", e.getAccessType());
                params.put("Rank", toNumberString(e.getRank()));
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
        switch (e) {
        case final ClickLog clickLog -> clickLogBhv.delete(clickLog);
        case final FavoriteLog favoriteLog -> favoriteLogBhv.delete(favoriteLog);
        case final UserInfo userInfo -> userInfoBhv.delete(userInfo);
        case final SearchLog searchLog -> searchLogBhv.delete(searchLog);
        case null, default -> throw new FessSystemException("Unknown log entity: " + e);
        }
    }
}
