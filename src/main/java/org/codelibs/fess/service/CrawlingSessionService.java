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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.CsvReader;
import jp.sf.orangesignal.csv.CsvWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.CrawlingSessionCB;
import org.codelibs.fess.db.exbhv.CrawlingSessionBhv;
import org.codelibs.fess.db.exbhv.CrawlingSessionInfoBhv;
import org.codelibs.fess.db.exentity.CrawlingSession;
import org.codelibs.fess.db.exentity.CrawlingSessionInfo;
import org.codelibs.fess.pager.CrawlingSessionPager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public class CrawlingSessionService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(CrawlingSessionService.class);

    @Resource
    protected CrawlingSessionInfoBhv crawlingSessionInfoBhv;

    @Resource
    protected CrawlingSessionBhv crawlingSessionBhv;

    public CrawlingSessionService() {
        super();
    }

    public List<CrawlingSession> getCrawlingSessionList(final CrawlingSessionPager crawlingSessionPager) {

        final PagingResultBean<CrawlingSession> crawlingSessionList = crawlingSessionBhv.selectPage(cb -> {
            cb.paging(crawlingSessionPager.getPageSize(), crawlingSessionPager.getCurrentPageNumber());
            setupListCondition(cb, crawlingSessionPager);
        });

        // update pager
        Beans.copy(crawlingSessionList, crawlingSessionPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        crawlingSessionPager.setPageNumberList(crawlingSessionList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return crawlingSessionList;
    }

    public CrawlingSession getCrawlingSession(final Map<String, String> keys) {
        final CrawlingSession crawlingSession = crawlingSessionBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (crawlingSession == null) {
            // TODO exception?
            return null;
        }

        return crawlingSession;
    }

    public void store(final CrawlingSession crawlingSession) throws CrudMessageException {
        setupStoreCondition(crawlingSession);

        crawlingSessionBhv.insertOrUpdate(crawlingSession);

    }

    public void delete(final CrawlingSession crawlingSession) throws CrudMessageException {
        setupDeleteCondition(crawlingSession);

        crawlingSessionBhv.delete(crawlingSession);

    }

    protected void setupEntityCondition(final CrawlingSessionCB cb, final Map<String, String> keys) {
    }

    protected void setupListCondition(final CrawlingSessionCB cb, final CrawlingSessionPager crawlingSessionPager) {
        if (crawlingSessionPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(crawlingSessionPager.id));
        }
        // TODO Long, Integer, String supported only.
        if (StringUtil.isNotBlank(crawlingSessionPager.sessionId)) {
            cb.query().setSessionId_LikeSearch(crawlingSessionPager.sessionId, op -> {
                op.likeContain();
            });
        }
        cb.query().addOrderBy_CreatedTime_Desc();
    }

    protected void setupStoreCondition(final CrawlingSession crawlingSession) {
        if (crawlingSession == null) {
            throw new FessSystemException("Crawling Session is null.");
        }
        final LocalDateTime now = ComponentUtil.getSystemHelper().getCurrentTime();
        if (crawlingSession.getCreatedTime() == null) {
            crawlingSession.setCreatedTime(now);
        }
    }

    protected void setupDeleteCondition(final CrawlingSession crawlingSession) {
        crawlingSessionInfoBhv.varyingQueryDelete(cb -> {
            cb.query().setCrawlingSessionId_Equal(crawlingSession.getId());
        }, op -> {
            op.allowNonQueryDelete();
        });
    }

    public void deleteSessionIdsBefore(final String activeSessionId, final String name, final LocalDateTime date) {
        final List<CrawlingSession> crawlingSessionList = crawlingSessionBhv.selectList(cb -> {
            cb.query().setExpiredTime_LessEqual(date);
            if (StringUtil.isNotBlank(name)) {
                cb.query().setName_Equal(name);
            }
            if (activeSessionId != null) {
                cb.query().setSessionId_NotEqual(activeSessionId);
            }
            cb.specify().columnId();
        });
        if (!crawlingSessionList.isEmpty()) {
            final List<Long> crawlingSessionIdList = new ArrayList<Long>();
            for (final CrawlingSession cs : crawlingSessionList) {
                crawlingSessionIdList.add(cs.getId());
            }

            crawlingSessionInfoBhv.varyingQueryDelete(cb2 -> {
                cb2.query().setCrawlingSessionId_InScope(crawlingSessionIdList);
            }, op -> {
                op.allowNonQueryDelete();
            });

            crawlingSessionBhv.batchDelete(crawlingSessionList);
        }
    }

    public CrawlingSession get(final String sessionId) {
        return crawlingSessionBhv.selectEntity(cb -> {
            cb.query().setSessionId_Equal(sessionId);
        }).orElse(null);//TODO
    }

    public void storeInfo(final List<CrawlingSessionInfo> crawlingSessionInfoList) {
        if (crawlingSessionInfoList == null) {
            throw new FessSystemException("Crawling Session Info is null.");
        }

        final LocalDateTime now = ComponentUtil.getSystemHelper().getCurrentTime();
        for (final CrawlingSessionInfo crawlingSessionInfo : crawlingSessionInfoList) {
            if (crawlingSessionInfo.getCreatedTime() == null) {
                crawlingSessionInfo.setCreatedTime(now);
            }
        }
        crawlingSessionInfoBhv.batchInsert(crawlingSessionInfoList);
    }

    public List<CrawlingSessionInfo> getCrawlingSessionInfoList(final Long id) {
        return crawlingSessionInfoBhv.selectList(cb -> {
            cb.query().queryCrawlingSession().setId_Equal(id);
            cb.query().addOrderBy_Id_Asc();
        });
    }

    public List<CrawlingSessionInfo> getLastCrawlingSessionInfoList(final String sessionId) {
        final CrawlingSession crawlingSession = getLast(sessionId);
        if (crawlingSession == null) {
            return Collections.emptyList();
        }
        return crawlingSessionInfoBhv.selectList(cb -> {
            cb.query().setCrawlingSessionId_Equal(crawlingSession.getId());
            cb.query().addOrderBy_Id_Asc();
        });
    }

    public void deleteOldSessions(final Set<String> activeSessionId) {
        crawlingSessionInfoBhv.varyingQueryDelete(cb1 -> {
            if (!activeSessionId.isEmpty()) {
                cb1.query().queryCrawlingSession().setSessionId_NotInScope(activeSessionId);
            }
        }, op -> {
            op.allowNonQueryDelete();
        });
        crawlingSessionBhv.varyingQueryDelete(cb2 -> {
            if (!activeSessionId.isEmpty()) {
                cb2.query().setSessionId_NotInScope(activeSessionId);
            }
        }, op -> {
            op.allowNonQueryDelete();
        });
    }

    public void importCsv(final Reader reader) {
        final CsvReader csvReader = new CsvReader(reader, new CsvConfig());
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
        try {
            List<String> list;
            csvReader.readValues(); // ignore header
            while ((list = csvReader.readValues()) != null) {
                try {
                    final String sessionId = list.get(0);
                    CrawlingSession crawlingSession = crawlingSessionBhv.selectEntity(cb -> {
                        cb.query().setSessionId_Equal(sessionId);
                        cb.specify().columnSessionId();
                    }).orElse(null);//TODO
                    if (crawlingSession == null) {
                        crawlingSession = new CrawlingSession();
                        crawlingSession.setSessionId(list.get(0));
                        crawlingSession.setCreatedTime(LocalDateTime.parse(list.get(1), formatter));
                        crawlingSessionBhv.insert(crawlingSession);
                    }

                    final CrawlingSessionInfo entity = new CrawlingSessionInfo();
                    entity.setCrawlingSessionId(crawlingSession.getId());
                    entity.setKey(list.get(2));
                    entity.setValue(list.get(3));
                    entity.setCreatedTime(LocalDateTime.parse(list.get(4), formatter));
                    crawlingSessionInfoBhv.insert(entity);
                } catch (final Exception e) {
                    log.warn("Failed to read a click log: " + list, e);
                }
            }
        } catch (final IOException e) {
            log.warn("Failed to read a click log.", e);
        }
    }

    public void exportCsv(final Writer writer) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, cfg);
        try {
            final List<String> list = new ArrayList<String>();
            list.add("SessionId");
            list.add("SessionCreatedTime");
            list.add("Key");
            list.add("Value");
            list.add("CreatedTime");
            csvWriter.writeValues(list);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            crawlingSessionInfoBhv.selectCursor(cb -> {
                cb.setupSelect_CrawlingSession();
            }, new EntityRowHandler<CrawlingSessionInfo>() {
                @Override
                public void handle(final CrawlingSessionInfo entity) {
                    final List<String> list = new ArrayList<String>();
                    entity.getCrawlingSession().ifPresent(crawlingSession -> {
                        addToList(list, crawlingSession.getSessionId());
                        addToList(list, crawlingSession.getCreatedTime());
                    });
                    // TODO
                    if (!entity.getCrawlingSession().isPresent()) {
                        addToList(list, "");
                        addToList(list, "");
                    }
                    addToList(list, entity.getKey());
                    addToList(list, entity.getValue());
                    addToList(list, entity.getCreatedTime());
                    try {
                        csvWriter.writeValues(list);
                    } catch (final IOException e) {
                        log.warn("Failed to write a crawling session info: " + entity, e);
                    }
                }

                private void addToList(final List<String> list, final Object value) {
                    if (value == null) {
                        list.add(StringUtil.EMPTY);
                    } else if (value instanceof LocalDateTime) {
                        list.add(((LocalDateTime) value).format(formatter));
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

    public void deleteBefore(final LocalDateTime date) {
        crawlingSessionInfoBhv.varyingQueryDelete(cb1 -> {
            cb1.setupSelect_CrawlingSession();
            cb1.query().queryCrawlingSession().setExpiredTime_LessThan(date);
        }, op -> {
            op.allowNonQueryDelete();
        });
        crawlingSessionBhv.varyingQueryDelete(cb2 -> {
            cb2.query().setExpiredTime_LessThan(date);
        }, op -> {
            op.allowNonQueryDelete();
        });
    }

    public CrawlingSession getLast(final String sessionId) {
        final ListResultBean<CrawlingSession> list = crawlingSessionBhv.selectList(cb -> {
            cb.query().setSessionId_Equal(sessionId);
            cb.query().addOrderBy_CreatedTime_Desc();
            cb.fetchFirst(1);
        });
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
