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
import java.io.Serializable;
import java.io.Writer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.FavoriteLogCB;
import org.codelibs.fess.db.exbhv.FavoriteLogBhv;
import org.codelibs.fess.db.exbhv.UserInfoBhv;
import org.codelibs.fess.db.exentity.FavoriteLog;
import org.codelibs.fess.db.exentity.UserInfo;
import org.codelibs.fess.pager.FavoriteLogPager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

import com.ibm.icu.text.SimpleDateFormat;

public class FavoriteLogService implements Serializable {
    private static final Log log = LogFactory.getLog(FavoriteLogService.class);

    private static final long serialVersionUID = 1L;

    @Resource
    protected UserInfoBhv userInfoBhv;

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    public FavoriteLogService() {
        super();
    }

    public List<FavoriteLog> getFavoriteLogList(final FavoriteLogPager favoriteLogPager) {

        final PagingResultBean<FavoriteLog> favoriteLogList = favoriteLogBhv.selectPage(cb -> {
            cb.paging(favoriteLogPager.getPageSize(), favoriteLogPager.getCurrentPageNumber());
            setupListCondition(cb, favoriteLogPager);
        });

        // update pager
        Beans.copy(favoriteLogList, favoriteLogPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        favoriteLogPager.setPageNumberList(favoriteLogList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return favoriteLogList;
    }

    public FavoriteLog getFavoriteLog(final Map<String, String> keys) {
        final FavoriteLog favoriteLog = favoriteLogBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (favoriteLog == null) {
            // TODO exception?
            return null;
        }

        return favoriteLog;
    }

    public void store(final FavoriteLog favoriteLog) throws CrudMessageException {
        setupStoreCondition(favoriteLog);

        favoriteLogBhv.insertOrUpdate(favoriteLog);

    }

    public void delete(final FavoriteLog favoriteLog) throws CrudMessageException {
        setupDeleteCondition(favoriteLog);

        favoriteLogBhv.delete(favoriteLog);

    }

    protected void setupListCondition(final FavoriteLogCB cb, final FavoriteLogPager favoriteLogPager) {
        if (favoriteLogPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(favoriteLogPager.id));
        }
        // TODO Long, Integer, String supported only.

        cb.setupSelect_UserInfo();

        cb.query().addOrderBy_CreatedTime_Desc();

        // setup condition

        // search
        buildSearchCondition(favoriteLogPager, cb);
    }

    protected void setupEntityCondition(final FavoriteLogCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final FavoriteLog favoriteLog) {

        // setup condition

    }

    protected void setupDeleteCondition(final FavoriteLog favoriteLog) {

        // setup condition

    }

    public boolean addUrl(final String userCode, final String url) {
        final UserInfo userInfo = userInfoBhv.selectEntity(cb -> {
            cb.query().setCode_Equal(userCode);
        }).orElse(null);//TODO

        if (userInfo != null) {
            final FavoriteLog favoriteLog = new FavoriteLog();
            favoriteLog.setUserId(userInfo.getId());
            favoriteLog.setUrl(url);
            favoriteLog.setCreatedTime(ComponentUtil.getSystemHelper().getCurrentTime());
            favoriteLogBhv.insert(favoriteLog);
            return true;
        }

        return false;
    }

    public List<String> getUrlList(final String userCode, final List<String> urlList) {
        if (urlList.isEmpty()) {
            return urlList;
        }

        final UserInfo userInfo = userInfoBhv.selectEntity(cb -> {
            cb.query().setCode_Equal(userCode);
        }).orElse(null);//TODO

        if (userInfo != null) {
            final ListResultBean<FavoriteLog> list = favoriteLogBhv.selectList(cb2 -> {
                cb2.query().setUserId_Equal(userInfo.getId());
                cb2.query().setUrl_InScope(urlList);
            });
            if (!list.isEmpty()) {
                final List<String> newUrlList = new ArrayList<String>(list.size());
                for (final FavoriteLog favoriteLog : list) {
                    newUrlList.add(favoriteLog.getUrl());
                }
                return newUrlList;
            }
        }

        return Collections.emptyList();
    }

    public void deleteAll(final FavoriteLogPager favoriteLogPager) {
        favoriteLogBhv.varyingQueryDelete(cb1 -> {
            buildSearchCondition(favoriteLogPager, cb1);
        }, op -> op.allowNonQueryDelete());
    }

    private void buildSearchCondition(final FavoriteLogPager favoriteLogPager, final FavoriteLogCB cb) {

        if (StringUtil.isNotBlank(favoriteLogPager.userCode)) {
            cb.setupSelect_UserInfo();
            cb.query().queryUserInfo().setCode_Equal(favoriteLogPager.userCode);
        }

        if (StringUtil.isNotBlank(favoriteLogPager.startDate)) {
            final StringBuilder buf = new StringBuilder(20);
            buf.append(favoriteLogPager.startDate);
            buf.append('+');
            if (StringUtil.isNotBlank(favoriteLogPager.startHour)) {
                buf.append(favoriteLogPager.startHour);
            } else {
                buf.append("00");
            }
            buf.append(':');
            if (StringUtil.isNotBlank(favoriteLogPager.startMin)) {
                buf.append(favoriteLogPager.startMin);
            } else {
                buf.append("00");
            }

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm");
            try {
                cb.query().setCreatedTime_GreaterEqual(LocalDateTime.parse(buf.toString(), formatter));
            } catch (final DateTimeParseException e) {
                favoriteLogPager.startDate = null;
                favoriteLogPager.startHour = null;
                favoriteLogPager.startMin = null;
            }
        }

        if (StringUtil.isNotBlank(favoriteLogPager.endDate)) {
            final StringBuilder buf = new StringBuilder(20);
            buf.append(favoriteLogPager.endDate);
            buf.append('+');
            if (StringUtil.isNotBlank(favoriteLogPager.endHour)) {
                buf.append(favoriteLogPager.endHour);
            } else {
                buf.append("00");
            }
            buf.append(':');
            if (StringUtil.isNotBlank(favoriteLogPager.endMin)) {
                buf.append(favoriteLogPager.endMin);
            } else {
                buf.append("00");
            }

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm");
            try {
                cb.query().setCreatedTime_LessThan(LocalDateTime.parse(buf.toString(), formatter));
            } catch (final DateTimeParseException e) {
                favoriteLogPager.endDate = null;
                favoriteLogPager.endHour = null;
                favoriteLogPager.endMin = null;
            }
        }

    }

    public void dump(final Writer writer, final FavoriteLogPager favoriteLogPager) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        final CsvWriter csvWriter = new CsvWriter(writer, cfg);
        try {
            final List<String> list = new ArrayList<String>();
            list.add("UserCode");
            list.add("URL");
            list.add("Date");
            csvWriter.writeValues(list);
            final SimpleDateFormat sdf = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            favoriteLogBhv.selectCursor(cb -> {
                if (favoriteLogPager != null) {
                    buildSearchCondition(favoriteLogPager, cb);
                }
            }, new EntityRowHandler<FavoriteLog>() {
                @Override
                public void handle(final FavoriteLog entity) {
                    final UserInfo userInfo = userInfoBhv.selectEntity(cb2 -> cb2.query().setId_Equal(entity.getId())).orElse(null);//TODO
                    String userCode;
                    if (userInfo == null) {
                        userCode = StringUtil.EMPTY;
                    } else {
                        userCode = userInfo.getCode();
                    }

                    final List<String> list = new ArrayList<String>();
                    addToList(list, userCode);
                    addToList(list, entity.getUrl());
                    addToList(list, entity.getCreatedTime());
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
            log.warn("Failed to write a favorite log.", e);
        }
    }
}
