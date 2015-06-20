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

package jp.sf.fess.service;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.util.StringUtil;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;

import com.ibm.icu.text.SimpleDateFormat;

import jp.sf.fess.FessSystemException;
import jp.sf.fess.crud.service.BsCrawlingSessionService;
import jp.sf.fess.db.cbean.CrawlingSessionCB;
import jp.sf.fess.db.cbean.CrawlingSessionInfoCB;
import jp.sf.fess.db.exbhv.CrawlingSessionInfoBhv;
import jp.sf.fess.db.exentity.CrawlingSession;
import jp.sf.fess.db.exentity.CrawlingSessionInfo;
import jp.sf.fess.pager.CrawlingSessionPager;
import jp.sf.fess.util.CsvUtil;
import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvReader;
import jp.sf.orangesignal.csv.CsvWriter;

public class CrawlingSessionService extends BsCrawlingSessionService implements
        Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(CrawlingSessionService.class);

    @Resource
    protected CrawlingSessionInfoBhv crawlingSessionInfoBhv;

    @Override
    protected void setupListCondition(final CrawlingSessionCB cb,
            final CrawlingSessionPager crawlingSessionPager) {
        super.setupListCondition(cb, crawlingSessionPager);
        if (StringUtil.isNotBlank(crawlingSessionPager.sessionId)) {
            cb.query().setSessionId_LikeSearch(crawlingSessionPager.sessionId,
                    new LikeSearchOption().likeContain());
        }
        cb.query().addOrderBy_CreatedTime_Desc();
    }

    @Override
    protected void setupStoreCondition(final CrawlingSession crawlingSession) {
        if (crawlingSession == null) {
            throw new FessSystemException("Crawling Session is null.");
        }
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        if (crawlingSession.getCreatedTime() == null) {
            crawlingSession.setCreatedTime(now);
        }
    }

    @Override
    protected void setupDeleteCondition(final CrawlingSession crawlingSession) {
        final CrawlingSessionInfoCB cb = new CrawlingSessionInfoCB();
        cb.query().setCrawlingSessionId_Equal(crawlingSession.getId());
        crawlingSessionInfoBhv
                .varyingQueryDelete(cb,
                        new DeleteOption<CrawlingSessionInfoCB>()
                                .allowNonQueryDelete());
    }

    public void deleteSessionIdsBefore(final String activeSessionId,
            final String name, final Date date) {
        final CrawlingSessionCB cb = new CrawlingSessionCB();
        cb.query().setExpiredTime_LessEqual(new Timestamp(date.getTime()));
        if (StringUtil.isNotBlank(name)) {
            cb.query().setName_Equal(name);
        }
        if (activeSessionId != null) {
            cb.query().setSessionId_NotEqual(activeSessionId);
        }
        cb.specify().columnId();
        final List<CrawlingSession> crawlingSessionList = crawlingSessionBhv
                .selectList(cb);
        if (!crawlingSessionList.isEmpty()) {
            final List<Long> crawlingSessionIdList = new ArrayList<Long>();
            for (final CrawlingSession cs : crawlingSessionList) {
                crawlingSessionIdList.add(cs.getId());
            }

            final CrawlingSessionInfoCB cb2 = new CrawlingSessionInfoCB();
            cb2.query().setCrawlingSessionId_InScope(crawlingSessionIdList);
            crawlingSessionInfoBhv.varyingQueryDelete(cb2,
                    new DeleteOption<CrawlingSessionInfoCB>()
                            .allowNonQueryDelete());

            crawlingSessionBhv.batchDelete(crawlingSessionList);
        }
    }

    public CrawlingSession get(final String sessionId) {
        final CrawlingSessionCB cb = new CrawlingSessionCB();
        cb.query().setSessionId_Equal(sessionId);
        return crawlingSessionBhv.selectEntity(cb);
    }

    public void storeInfo(
            final List<CrawlingSessionInfo> crawlingSessionInfoList) {
        if (crawlingSessionInfoList == null) {
            throw new FessSystemException("Crawling Session Info is null.");
        }

        final Timestamp now = new Timestamp(System.currentTimeMillis());
        for (final CrawlingSessionInfo crawlingSessionInfo : crawlingSessionInfoList) {
            if (crawlingSessionInfo.getCreatedTime() == null) {
                crawlingSessionInfo.setCreatedTime(now);
            }
        }
        crawlingSessionInfoBhv.batchInsert(crawlingSessionInfoList);
    }

    public List<CrawlingSessionInfo> getCrawlingSessionInfoList(final Long id) {
        final CrawlingSessionInfoCB cb = new CrawlingSessionInfoCB();
        cb.query().queryCrawlingSession().setId_Equal(id);
        cb.query().addOrderBy_Id_Asc();
        return crawlingSessionInfoBhv.selectList(cb);
    }

    public List<CrawlingSessionInfo> getLastCrawlingSessionInfoList(
            final String sessionId) {
        final CrawlingSession crawlingSession = getLast(sessionId);
        if (crawlingSession == null) {
            return Collections.emptyList();
        }
        final CrawlingSessionInfoCB cb = new CrawlingSessionInfoCB();
        cb.query().setCrawlingSessionId_Equal(crawlingSession.getId());
        cb.query().addOrderBy_Id_Asc();
        return crawlingSessionInfoBhv.selectList(cb);
    }

    public void deleteOldSessions(final Set<String> activeSessionId) {
        final CrawlingSessionInfoCB cb1 = new CrawlingSessionInfoCB();
        if (!activeSessionId.isEmpty()) {
            cb1.query().queryCrawlingSession()
                    .setSessionId_NotInScope(activeSessionId);
        }
        crawlingSessionInfoBhv
                .varyingQueryDelete(cb1,
                        new DeleteOption<CrawlingSessionInfoCB>()
                                .allowNonQueryDelete());
        final CrawlingSessionCB cb2 = new CrawlingSessionCB();
        if (!activeSessionId.isEmpty()) {
            cb2.query().setSessionId_NotInScope(activeSessionId);
        }
        crawlingSessionBhv.varyingQueryDelete(cb2,
                new DeleteOption<CrawlingSessionCB>().allowNonQueryDelete());
    }

    public void importCsv(final Reader reader) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        @SuppressWarnings("resource")
        final CsvReader csvReader = new CsvReader(reader, cfg);
        try {
            List<String> list;
            csvReader.readValues(); // ignore header
            while ((list = csvReader.readValues()) != null) {
                final String sessionId = CsvUtil.get(list, 0);
                if (sessionId == null) {
                    log.warn("No crawling session log: Line " + csvReader.getLineNumber());
                    continue;
                }
                try {
                    final CrawlingSessionCB cb = new CrawlingSessionCB();
                    cb.query().setSessionId_Equal(sessionId);
                    cb.specify().columnSessionId();
                    CrawlingSession crawlingSession = crawlingSessionBhv
                            .selectEntity(cb);
                    if (crawlingSession == null) {
                        crawlingSession = new CrawlingSession();
                        crawlingSession.setSessionId(sessionId);
                        crawlingSession.setCreatedTime(CsvUtil.getAsTimestamp(list, 1, new Timestamp(System.currentTimeMillis())));
                        crawlingSessionBhv.insert(crawlingSession);
                    }

                    final CrawlingSessionInfo entity = new CrawlingSessionInfo();
                    entity.setCrawlingSessionId(crawlingSession.getId());
                    entity.setKey(CsvUtil.get(list, 2, "Unknown"));
                    entity.setValue(CsvUtil.get(list, 3,StringUtil.EMPTY));
                    entity.setCreatedTime(CsvUtil.getAsTimestamp(list, 4, new Timestamp(System.currentTimeMillis())));
                    crawlingSessionInfoBhv.insert(entity);
                } catch (final Exception e) {
                    log.warn("Failed to read a crawling session info: " + list, e);
                }
            }
        } catch (final IOException e) {
            log.warn("Failed to read a crawling session info.", e);
        }
    }

    public void exportCsv(final Writer writer) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, cfg);
        final CrawlingSessionInfoCB cb = new CrawlingSessionInfoCB();
        cb.setupSelect_CrawlingSession();
        try {
            final List<String> list = new ArrayList<String>();
            list.add("SessionId");
            list.add("SessionCreatedTime");
            list.add("Key");
            list.add("Value");
            list.add("CreatedTime");
            csvWriter.writeValues(list);
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            crawlingSessionInfoBhv.selectCursor(cb,
                    new EntityRowHandler<CrawlingSessionInfo>() {
                        @Override
                        public void handle(final CrawlingSessionInfo entity) {
                            final List<String> list = new ArrayList<String>();
                            addToList(list, entity.getCrawlingSession()
                                    .getSessionId());
                            addToList(list, entity.getCrawlingSession()
                                    .getCreatedTime());
                            addToList(list, entity.getKey());
                            addToList(list, entity.getValue());
                            addToList(list, entity.getCreatedTime());
                            try {
                                csvWriter.writeValues(list);
                            } catch (final IOException e) {
                                log.warn(
                                        "Failed to write a crawling session info: "
                                                + entity, e);
                            }
                        }

                        private void addToList(final List<String> list,
                                final Object value) {
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
            log.warn("Failed to write a crawling session info.", e);
        }
    }

    public void deleteBefore(final Date date) {
        final Timestamp timestamp = new Timestamp(date.getTime());
        final CrawlingSessionInfoCB cb1 = new CrawlingSessionInfoCB();
        cb1.setupSelect_CrawlingSession();
        cb1.query().queryCrawlingSession().setExpiredTime_LessThan(timestamp);
        crawlingSessionInfoBhv
                .varyingQueryDelete(cb1,
                        new DeleteOption<CrawlingSessionInfoCB>()
                                .allowNonQueryDelete());
        final CrawlingSessionCB cb2 = new CrawlingSessionCB();
        cb2.query().setExpiredTime_LessThan(timestamp);
        crawlingSessionBhv.varyingQueryDelete(cb2,
                new DeleteOption<CrawlingSessionCB>().allowNonQueryDelete());
    }

    public CrawlingSession getLast(final String sessionId) {
        final CrawlingSessionCB cb = new CrawlingSessionCB();
        cb.query().setSessionId_Equal(sessionId);
        cb.query().addOrderBy_CreatedTime_Desc();
        cb.fetchFirst(1);
        final ListResultBean<CrawlingSession> list = crawlingSessionBhv
                .selectList(cb);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
