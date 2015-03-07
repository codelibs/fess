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

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvReader;
import jp.sf.orangesignal.csv.CsvWriter;
import net.arnx.jsonic.JSON;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.service.BsSearchLogService;
import org.codelibs.fess.db.allcommon.CDef.AccessType;
import org.codelibs.fess.db.cbean.ClickLogCB;
import org.codelibs.fess.db.cbean.SearchLogCB;
import org.codelibs.fess.db.exbhv.ClickLogBhv;
import org.codelibs.fess.db.exbhv.SearchFieldLogBhv;
import org.codelibs.fess.db.exentity.SearchFieldLog;
import org.codelibs.fess.db.exentity.SearchLog;
import org.codelibs.fess.pager.SearchLogPager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.result.ListResultBean;

import com.ibm.icu.text.SimpleDateFormat;

public class SearchLogService extends BsSearchLogService implements Serializable {
    private static final Log log = LogFactory.getLog(SearchLogService.class);

    private static final long serialVersionUID = 1L;

    @Resource
    protected ClickLogBhv clickLogBhv;

    @Resource
    protected SearchFieldLogBhv searchFieldLogBhv;

    @Override
    protected void setupListCondition(final SearchLogCB cb, final SearchLogPager searchLogPager) {
        super.setupListCondition(cb, searchLogPager);
        cb.setupSelect_UserInfo();

        // setup condition
        if ("searchWord".equals(searchLogPager.sortField)) {
            if (Constants.DESC.equals(searchLogPager.sortOrder)) {
                cb.query().addOrderBy_SearchWord_Desc();
            } else {
                cb.query().addOrderBy_SearchWord_Asc();
            }
            cb.query().addOrderBy_RequestedTime_Desc();
        } else if ("userCode".equals(searchLogPager.userCode)) {
            if (Constants.DESC.equals(searchLogPager.sortOrder)) {
                cb.query().queryUserInfo().addOrderBy_Code_Desc();
            } else {
                cb.query().queryUserInfo().addOrderBy_Code_Asc();
            }
            cb.query().addOrderBy_RequestedTime_Desc();
        } else if ("hitCount".equals(searchLogPager.sortField)) {
            if (Constants.DESC.equals(searchLogPager.sortOrder)) {
                cb.query().addOrderBy_HitCount_Desc();
            } else {
                cb.query().addOrderBy_HitCount_Asc();
            }
            cb.query().addOrderBy_RequestedTime_Desc();
        } else if ("responseTime".equals(searchLogPager.sortField)) {
            if (Constants.DESC.equals(searchLogPager.sortOrder)) {
                cb.query().addOrderBy_ResponseTime_Desc();
            } else {
                cb.query().addOrderBy_ResponseTime_Asc();
            }
            cb.query().addOrderBy_RequestedTime_Desc();
        } else {
            if (Constants.ASC.equals(searchLogPager.sortOrder)) {
                cb.query().addOrderBy_RequestedTime_Asc();
            } else {
                cb.query().addOrderBy_RequestedTime_Desc();
            }
        }

        buildSearchCondition(searchLogPager, cb);
    }

    @Override
    public SearchLog getSearchLog(final Map<String, String> keys) {
        final SearchLog searchLog = super.getSearchLog(keys);
        if (searchLog != null) {
            searchLog.setClickLogList(clickLogBhv.selectList(cb -> {
                cb.query().setSearchId_Equal(searchLog.getId());
            }));
        }
        return searchLog;
    }

    @Override
    protected void setupEntityCondition(final SearchLogCB cb, final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(final SearchLog searchLog) {
        super.setupStoreCondition(searchLog);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final SearchLog searchLog) {
        super.setupDeleteCondition(searchLog);

        // setup condition

    }

    @Override
    public void delete(final SearchLog searchLog) {
        clickLogBhv.batchDelete(searchLog.getClickLogList());
        searchFieldLogBhv.varyingQueryDelete(cb -> {
            cb.query().setSearchId_Equal(searchLog.getId());
        }, op -> op.allowNonQueryDelete());
        super.delete(searchLog);
    }

    public void deleteAll(final SearchLogPager searchLogPager) {
        clickLogBhv.varyingQueryDelete(cb2 -> {
            if (StringUtil.isNotBlank(searchLogPager.searchWord)) {
                cb2.query().querySearchLog().setSearchWord_LikeSearch(searchLogPager.searchWord, op -> op.likeContain());
            }

            if (StringUtil.isNotBlank(searchLogPager.startDate)) {
                final StringBuilder buf = new StringBuilder(20);
                buf.append(searchLogPager.startDate);
                buf.append('+');
                if (StringUtil.isNotBlank(searchLogPager.startHour)) {
                    buf.append(searchLogPager.startHour);
                } else {
                    buf.append("00");
                }
                buf.append(':');
                if (StringUtil.isNotBlank(searchLogPager.startMin)) {
                    buf.append(searchLogPager.startMin);
                } else {
                    buf.append("00");
                }

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm");
                try {
                    final LocalDateTime startDate = LocalDateTime.parse(buf.toString(), formatter);
                    cb2.query().querySearchLog().setRequestedTime_GreaterEqual(startDate);
                } catch (final DateTimeParseException e) {
                    searchLogPager.startDate = null;
                    searchLogPager.startHour = null;
                    searchLogPager.startMin = null;
                }
            }

            if (StringUtil.isNotBlank(searchLogPager.endDate)) {
                final StringBuilder buf = new StringBuilder(20);
                buf.append(searchLogPager.endDate);
                buf.append('+');
                if (StringUtil.isNotBlank(searchLogPager.endHour)) {
                    buf.append(searchLogPager.endHour);
                } else {
                    buf.append("00");
                }
                buf.append(':');
                if (StringUtil.isNotBlank(searchLogPager.endMin)) {
                    buf.append(searchLogPager.endMin);
                } else {
                    buf.append("00");
                }

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm");
                try {
                    final LocalDateTime endDate = LocalDateTime.parse(buf.toString(), formatter);
                    cb2.query().querySearchLog().setRequestedTime_LessThan(endDate);
                } catch (final DateTimeParseException e) {
                    searchLogPager.endDate = null;
                    searchLogPager.endHour = null;
                    searchLogPager.endMin = null;
                }
            }

            if (StringUtil.isNotBlank(searchLogPager.startPage)) {
                cb2.query().querySearchLog().setQueryOffset_Equal(0);
            }
        }, op -> op.allowNonQueryDelete());
        searchFieldLogBhv.varyingQueryDelete(cb2 -> {}, op -> op.allowNonQueryDelete());
        searchLogBhv.varyingQueryDelete(cb1 -> {
            if (StringUtil.isNotBlank(searchLogPager.searchWord)) {
                cb1.query().setSearchWord_LikeSearch(searchLogPager.searchWord, op -> op.likeContain());
            }

            if (StringUtil.isNotBlank(searchLogPager.userCode)) {
                cb1.setupSelect_UserInfo();
                cb1.query().queryUserInfo().setCode_Equal(searchLogPager.userCode);
            }

            if (StringUtil.isNotBlank(searchLogPager.startDate)) {
                final StringBuilder buf = new StringBuilder(20);
                buf.append(searchLogPager.startDate);
                buf.append('+');
                if (StringUtil.isNotBlank(searchLogPager.startHour)) {
                    buf.append(searchLogPager.startHour);
                } else {
                    buf.append("00");
                }
                buf.append(':');
                if (StringUtil.isNotBlank(searchLogPager.startMin)) {
                    buf.append(searchLogPager.startMin);
                } else {
                    buf.append("00");
                }

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm");
                try {
                    final LocalDateTime startDate = LocalDateTime.parse(buf.toString(), formatter);
                    cb1.query().setRequestedTime_GreaterEqual(startDate);
                } catch (final DateTimeParseException e) {
                    searchLogPager.startDate = null;
                    searchLogPager.startHour = null;
                    searchLogPager.startMin = null;
                }
            }

            if (StringUtil.isNotBlank(searchLogPager.endDate)) {
                final StringBuilder buf = new StringBuilder(20);
                buf.append(searchLogPager.endDate);
                buf.append('+');
                if (StringUtil.isNotBlank(searchLogPager.endHour)) {
                    buf.append(searchLogPager.endHour);
                } else {
                    buf.append("00");
                }
                buf.append(':');
                if (StringUtil.isNotBlank(searchLogPager.endMin)) {
                    buf.append(searchLogPager.endMin);
                } else {
                    buf.append("00");
                }

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm");
                try {
                    final LocalDateTime endDate = LocalDateTime.parse(buf.toString(), formatter);
                    cb1.query().setRequestedTime_LessThan(endDate);
                } catch (final DateTimeParseException e) {
                    searchLogPager.endDate = null;
                    searchLogPager.endHour = null;
                    searchLogPager.endMin = null;
                }
            }

            if (StringUtil.isNotBlank(searchLogPager.startPage)) {
                cb1.query().setQueryOffset_Equal(0);
            }
        }, op -> op.allowNonQueryDelete());
    }

    public void exportCsv(final Writer writer) {
        dump(writer, null);
    }

    public void importCsv(final Reader reader) {
        final CsvReader csvReader = new CsvReader(reader, new CsvConfig());
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
        try {
            List<String> list;
            csvReader.readValues(); // ignore header
            while ((list = csvReader.readValues()) != null) {
                try {
                    final SearchLog entity = new SearchLog();
                    entity.setSearchWord(list.get(0));
                    entity.setSearchQuery(list.get(1));
                    entity.setSolrQuery(list.get(2));
                    entity.setRequestedTime(LocalDateTime.parse(list.get(3), formatter));
                    entity.setResponseTime(Integer.parseInt(list.get(4)));
                    entity.setHitCount(Long.parseLong(list.get(5)));
                    entity.setQueryOffset(Integer.parseInt(list.get(6)));
                    entity.setQueryPageSize(Integer.parseInt(list.get(7)));
                    entity.setUserAgent(list.get(8));
                    entity.setReferer(list.get(9));
                    entity.setClientIp(list.get(10));
                    entity.setUserSessionId(list.get(11));
                    entity.setAccessTypeAsAccessType(AccessType.valueOf(list.get(12)));
                    if (list.size() >= 14) {
                        final String jsonStr = list.get(13);
                        @SuppressWarnings("rawtypes")
                        final List objList = JSON.decode(jsonStr);
                        for (final Object obj : objList) {
                            @SuppressWarnings("rawtypes")
                            final Map objMap = (Map) obj;
                            entity.addSearchFieldLogValue((String) objMap.get(Constants.ITEM_NAME),
                                    (String) objMap.get(Constants.ITEM_VALUE));
                        }
                    }
                    searchLogBhv.insert(entity);
                } catch (final Exception e) {
                    log.warn("Failed to read a search log: " + list, e);
                }
            }
        } catch (final IOException e) {
            log.warn("Failed to read a search log.", e);
        }
    }

    public void dump(final Writer writer, final SearchLogPager searchLogPager) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        final CsvWriter csvWriter = new CsvWriter(writer, cfg);
        try {
            final List<String> list = new ArrayList<String>();
            list.add("SearchWord");
            list.add("SearchQuery");
            list.add("SolrQuery");
            list.add("RequestedTime");
            list.add("ResponseTime");
            list.add("HitCount");
            list.add("QueryOffset");
            list.add("QueryPageSize");
            list.add("UserAgent");
            list.add("Referer");
            list.add("ClientIp");
            list.add("UserSessionId");
            list.add("AccessType");
            list.add("Fields");
            csvWriter.writeValues(list);
            final SimpleDateFormat sdf = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            searchLogBhv.selectCursor(cb -> {
                if (searchLogPager != null) {
                    buildSearchCondition(searchLogPager, cb);
                }
            }, new EntityRowHandler<SearchLog>() {
                @Override
                public void handle(final SearchLog entity) {
                    final ListResultBean<SearchFieldLog> fieldLogList =
                            ComponentUtil.getComponent(SearchFieldLogBhv.class).selectList(cb2 -> {
                                cb2.query().setSearchId_Equal(entity.getId());
                            });
                    String query = StringUtil.EMPTY;
                    String solrQuery = StringUtil.EMPTY;
                    final List<Map<String, String>> jsonObjList = new ArrayList<Map<String, String>>(fieldLogList.size());
                    for (final SearchFieldLog fieldLog : fieldLogList) {
                        final String name = fieldLog.getName();
                        if (Constants.SEARCH_FIELD_LOG_SEARCH_QUERY.equals(name)) {
                            query = fieldLog.getValue();
                        } else if (Constants.SEARCH_FIELD_LOG_SOLR_QUERY.equals(name)) {
                            solrQuery = fieldLog.getValue();
                        } else {
                            final Map<String, String> objMap = new HashMap<String, String>(2);
                            objMap.put(Constants.ITEM_NAME, fieldLog.getName());
                            objMap.put(Constants.ITEM_VALUE, fieldLog.getValue());
                            jsonObjList.add(objMap);
                        }
                    }

                    final List<String> list = new ArrayList<String>();
                    addToList(list, entity.getSearchWord());
                    addToList(list, query);
                    addToList(list, solrQuery);
                    addToList(list, entity.getRequestedTime());
                    addToList(list, entity.getResponseTime());
                    addToList(list, entity.getHitCount());
                    addToList(list, entity.getQueryOffset());
                    addToList(list, entity.getQueryPageSize());
                    addToList(list, entity.getUserAgent());
                    addToList(list, entity.getReferer());
                    addToList(list, entity.getClientIp());
                    addToList(list, entity.getUserSessionId());
                    addToList(list, entity.getAccessType());
                    addToList(list, JSON.encode(jsonObjList));
                    try {
                        csvWriter.writeValues(list);
                    } catch (final IOException e) {
                        log.warn("Failed to write a search log: " + entity, e);
                    }
                }

                private void addToList(final List<String> list, final Object value) {
                    if (value == null) {
                        list.add(StringUtil.EMPTY);
                    } else if (value instanceof Timestamp) {
                        list.add(sdf.format((Date) value));
                    } else {
                        list.add(value.toString());
                    }
                }
            });
            csvWriter.flush();
        } catch (final IOException e) {
            log.warn("Failed to write a search log.", e);
        }
    }

    private void buildSearchCondition(final SearchLogPager searchLogPager, final SearchLogCB cb) {
        buildSearchCondition(searchLogPager, cb, null);
    }

    private void buildSearchCondition(final SearchLogPager searchLogPager, final SearchLogCB cb1, final ClickLogCB cb2) {
        if (StringUtil.isNotBlank(searchLogPager.searchWord)) {
            cb1.query().setSearchWord_LikeSearch(searchLogPager.searchWord, op -> op.likeContain());
            if (cb2 != null) {
                cb2.query().querySearchLog().setSearchWord_LikeSearch(searchLogPager.searchWord, op -> op.likeContain());
            }
        }

        if (StringUtil.isNotBlank(searchLogPager.userCode)) {
            cb1.setupSelect_UserInfo();
            cb1.query().queryUserInfo().setCode_Equal(searchLogPager.userCode);
        }

        if (StringUtil.isNotBlank(searchLogPager.startDate)) {
            final StringBuilder buf = new StringBuilder(20);
            buf.append(searchLogPager.startDate);
            buf.append('+');
            if (StringUtil.isNotBlank(searchLogPager.startHour)) {
                buf.append(searchLogPager.startHour);
            } else {
                buf.append("00");
            }
            buf.append(':');
            if (StringUtil.isNotBlank(searchLogPager.startMin)) {
                buf.append(searchLogPager.startMin);
            } else {
                buf.append("00");
            }

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm");
            try {
                final LocalDateTime startDate = LocalDateTime.parse(buf.toString(), formatter);
                cb1.query().setRequestedTime_GreaterEqual(startDate);
                if (cb2 != null) {
                    cb2.query().querySearchLog().setRequestedTime_GreaterEqual(startDate);
                }
            } catch (final DateTimeParseException e) {
                searchLogPager.startDate = null;
                searchLogPager.startHour = null;
                searchLogPager.startMin = null;
            }
        }

        if (StringUtil.isNotBlank(searchLogPager.endDate)) {
            final StringBuilder buf = new StringBuilder(20);
            buf.append(searchLogPager.endDate);
            buf.append('+');
            if (StringUtil.isNotBlank(searchLogPager.endHour)) {
                buf.append(searchLogPager.endHour);
            } else {
                buf.append("00");
            }
            buf.append(':');
            if (StringUtil.isNotBlank(searchLogPager.endMin)) {
                buf.append(searchLogPager.endMin);
            } else {
                buf.append("00");
            }

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm");
            try {
                final LocalDateTime endDate = LocalDateTime.parse(buf.toString(), formatter);
                cb1.query().setRequestedTime_LessThan(endDate);
                if (cb2 != null) {
                    cb2.query().querySearchLog().setRequestedTime_LessThan(endDate);
                }
            } catch (final DateTimeParseException e) {
                searchLogPager.endDate = null;
                searchLogPager.endHour = null;
                searchLogPager.endMin = null;
            }
        }

        if (StringUtil.isNotBlank(searchLogPager.startPage)) {
            cb1.query().setQueryOffset_Equal(0);
            if (cb2 != null) {
                cb2.query().querySearchLog().setQueryOffset_Equal(0);
            }
        }
    }

    public void deleteBefore(final int days) {
        final LocalDateTime targetTime = LocalDateTime.now().minusDays(days);
        clickLogBhv.varyingQueryDelete(cb2 -> {
            cb2.query().querySearchLog().setRequestedTime_LessThan(targetTime);
        }, op -> op.allowNonQueryDelete());
        searchFieldLogBhv.varyingQueryDelete(cb3 -> {
            cb3.query().querySearchLog().setRequestedTime_LessThan(targetTime);
        }, op -> op.allowNonQueryDelete());
        searchLogBhv.varyingQueryDelete(cb1 -> {
            cb1.query().setRequestedTime_LessThan(targetTime);
        }, op -> op.allowNonQueryDelete());
    }

    public void deleteBotsLog(final String[] bots) {
        final LocalDateTime now = ComponentUtil.getSystemHelper().getCurrentTime();
        for (final String value : bots) {
            final String userAgent = value.trim();
            clickLogBhv.varyingQueryDelete(cb2 -> {
                cb2.query().querySearchLog().setRequestedTime_LessThan(now);
                cb2.query().querySearchLog().setUserAgent_LikeSearch(userAgent, op -> op.likeContain());
            }, op -> op.allowNonQueryDelete());
            searchFieldLogBhv.varyingQueryDelete(cb3 -> {
                cb3.query().querySearchLog().setRequestedTime_LessThan(now);
                cb3.query().querySearchLog().setUserAgent_LikeSearch(userAgent, op -> op.likeContain());
            }, op -> op.allowNonQueryDelete());
            searchLogBhv.varyingQueryDelete(cb1 -> {
                cb1.query().setRequestedTime_LessThan(now);
                cb1.query().setUserAgent_LikeSearch(userAgent, op -> op.likeContain());
            }, op -> op.allowNonQueryDelete());
        }
    }

    @Override
    public void store(final SearchLog searchLog) {
        super.store(searchLog);
        final List<SearchFieldLog> searchFieldLogList = searchLog.getSearchFieldLogList();
        if (!searchFieldLogList.isEmpty()) {
            final List<SearchFieldLog> fieldLogList = new ArrayList<SearchFieldLog>();
            for (final SearchFieldLog fieldLog : searchFieldLogList) {
                fieldLog.setSearchId(searchLog.getId());
                fieldLogList.add(fieldLog);
            }
            searchFieldLogBhv.batchInsert(fieldLogList);
        }
    }

    public void store(final List<SearchLog> searchLogList) {
        for (final SearchLog searchLog : searchLogList) {
            searchLogBhv.insert(searchLog);
            final List<SearchFieldLog> fieldLogList = new ArrayList<SearchFieldLog>();
            for (final SearchFieldLog fieldLog : searchLog.getSearchFieldLogList()) {
                fieldLog.setSearchId(searchLog.getId());
                fieldLogList.add(fieldLog);
            }
            searchFieldLogBhv.batchInsert(fieldLogList);
        }
    }

}
