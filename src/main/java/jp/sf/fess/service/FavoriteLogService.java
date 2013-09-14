/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.service;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.service.BsFavoriteLogService;
import jp.sf.fess.db.cbean.FavoriteLogCB;
import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.exbhv.UserInfoBhv;
import jp.sf.fess.db.exentity.FavoriteLog;
import jp.sf.fess.db.exentity.UserInfo;
import jp.sf.fess.pager.FavoriteLogPager;
import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.CoreLibConstants;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.framework.util.StringUtil;

import com.ibm.icu.text.SimpleDateFormat;

public class FavoriteLogService extends BsFavoriteLogService implements
        Serializable {
    private static final Log log = LogFactory.getLog(FavoriteLogService.class);

    private static final long serialVersionUID = 1L;

    @Resource
    protected UserInfoBhv userInfoBhv;

    @Override
    protected void setupListCondition(final FavoriteLogCB cb,
            final FavoriteLogPager favoriteLogPager) {
        super.setupListCondition(cb, favoriteLogPager);
        cb.setupSelect_UserInfo();

        cb.query().addOrderBy_CreatedTime_Desc();

        // setup condition

        // search
        buildSearchCondition(favoriteLogPager, cb);
    }

    @Override
    protected void setupEntityCondition(final FavoriteLogCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(final FavoriteLog favoriteLog) {
        super.setupStoreCondition(favoriteLog);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final FavoriteLog favoriteLog) {
        super.setupDeleteCondition(favoriteLog);

        // setup condition

    }

    public boolean addUrl(final String userCode, final String url) {
        final UserInfoCB cb = new UserInfoCB();
        cb.query().setCode_Equal(userCode);
        final UserInfo userInfo = userInfoBhv.selectEntity(cb);

        if (userInfo != null) {
            final FavoriteLog favoriteLog = new FavoriteLog();
            favoriteLog.setUserId(userInfo.getId());
            favoriteLog.setUrl(url);
            favoriteLog
                    .setCreatedTime(new Timestamp(System.currentTimeMillis()));
            favoriteLogBhv.insert(favoriteLog);
            return true;
        }

        return false;
    }

    public String[] getUrls(final String userCode, final String[] urls) {
        if (urls.length == 0) {
            return urls;
        }

        final UserInfoCB cb = new UserInfoCB();
        cb.query().setCode_Equal(userCode);
        final UserInfo userInfo = userInfoBhv.selectEntity(cb);

        if (userInfo != null) {
            final List<String> urlList = new ArrayList<String>();
            for (final String url : urls) {
                urlList.add(url);
            }
            final FavoriteLogCB cb2 = new FavoriteLogCB();
            cb2.query().setUserId_Equal(userInfo.getId());
            cb2.query().setUrl_InScope(urlList);

            final ListResultBean<FavoriteLog> list = favoriteLogBhv
                    .selectList(cb2);
            if (!list.isEmpty()) {
                urlList.clear();
                for (final FavoriteLog favoriteLog : list) {
                    urlList.add(favoriteLog.getUrl());
                }
                return urlList.toArray(new String[urlList.size()]);
            }
        }

        return new String[0];
    }

    public void deleteAll(final FavoriteLogPager favoriteLogPager) {
        final FavoriteLogCB cb1 = new FavoriteLogCB();
        buildSearchCondition(favoriteLogPager, cb1);
        favoriteLogBhv.varyingQueryDelete(cb1,
                new DeleteOption<FavoriteLogCB>().allowNonQueryDelete());
    }

    private void buildSearchCondition(final FavoriteLogPager favoriteLogPager,
            final FavoriteLogCB cb) {

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

            final SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd+HH:mm");
            try {
                final Date startDate = sdf.parse(buf.toString());
                cb.query().setCreatedTime_GreaterEqual(
                        new Timestamp(startDate.getTime()));
            } catch (final ParseException e) {
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

            final SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd+HH:mm");
            try {
                final Date endDate = sdf.parse(buf.toString());
                cb.query().setCreatedTime_LessThan(
                        new Timestamp(endDate.getTime()));
            } catch (final ParseException e) {
                favoriteLogPager.endDate = null;
                favoriteLogPager.endHour = null;
                favoriteLogPager.endMin = null;
            }
        }

    }

    public void dump(final Writer writer,
            final FavoriteLogPager favoriteLogPager) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        final CsvWriter csvWriter = new CsvWriter(writer, cfg);
        final FavoriteLogCB cb = new FavoriteLogCB();
        if (favoriteLogPager != null) {
            buildSearchCondition(favoriteLogPager, cb);
        }
        try {
            final List<String> list = new ArrayList<String>();
            list.add("UserCode");
            list.add("URL");
            list.add("Date");
            csvWriter.writeValues(list);
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            favoriteLogBhv.selectCursor(cb,
                    new EntityRowHandler<FavoriteLog>() {
                        @Override
                        public void handle(final FavoriteLog entity) {
                            final UserInfoCB cb = new UserInfoCB();
                            cb.query().setId_Equal(entity.getId());
                            final UserInfo userInfo = userInfoBhv
                                    .selectEntity(cb);
                            String userCode;
                            if (userInfo == null) {
                                userCode = Constants.EMPTY_STRING;
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
                                log.warn("Failed to write a search log: "
                                        + entity, e);
                            }
                        }

                        private void addToList(final List<String> list,
                                final Object value) {
                            if (value == null) {
                                list.add(Constants.EMPTY_STRING);
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
