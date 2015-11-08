/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.CrawlingSessionPager;
import org.codelibs.fess.es.config.cbean.CrawlingSessionCB;
import org.codelibs.fess.es.config.exbhv.CrawlingSessionBhv;
import org.codelibs.fess.es.config.exbhv.CrawlingSessionInfoBhv;
import org.codelibs.fess.es.config.exentity.CrawlingSession;
import org.codelibs.fess.es.config.exentity.CrawlingSessionInfo;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;

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
        BeanUtil.copyBeanToBean(crawlingSessionList, crawlingSessionPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        crawlingSessionPager.setPageNumberList(crawlingSessionList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return crawlingSessionList;
    }

    public OptionalEntity<CrawlingSession> getCrawlingSession(final String id) {
        return crawlingSessionBhv.selectByPK(id);
    }

    public void store(final CrawlingSession crawlingSession) {
        setupStoreCondition(crawlingSession);

        crawlingSessionBhv.insertOrUpdate(crawlingSession, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final CrawlingSession crawlingSession) {
        setupDeleteCondition(crawlingSession);

        crawlingSessionBhv.delete(crawlingSession, op -> {
            op.setRefresh(true);
        });

    }

    protected void setupEntityCondition(final CrawlingSessionCB cb, final Map<String, String> keys) {
    }

    protected void setupListCondition(final CrawlingSessionCB cb, final CrawlingSessionPager crawlingSessionPager) {
        if (crawlingSessionPager.id != null) {
            cb.query().docMeta().setId_Equal(crawlingSessionPager.id);
        }
        // TODO Long, Integer, String supported only.
        if (StringUtil.isNotBlank(crawlingSessionPager.sessionId)) {
            cb.query().setSessionId_Match(crawlingSessionPager.sessionId);
        }
        cb.query().addOrderBy_CreatedTime_Desc();
    }

    protected void setupStoreCondition(final CrawlingSession crawlingSession) {
        if (crawlingSession == null) {
            throw new FessSystemException("Crawling Session is null.");
        }
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        if (crawlingSession.getCreatedTime() == null) {
            crawlingSession.setCreatedTime(now);
        }
    }

    protected void setupDeleteCondition(final CrawlingSession crawlingSession) {
        crawlingSessionInfoBhv.queryDelete(cb -> {
            cb.query().setCrawlingSessionId_Equal(crawlingSession.getId());
        });
    }

    public void deleteSessionIdsBefore(final String activeSessionId, final String name, final long date) {
        final List<CrawlingSession> crawlingSessionList = crawlingSessionBhv.selectList(cb -> {
            cb.query().filtered((cq, cf) -> {
                cq.setExpiredTime_LessEqual(date);
                if (StringUtil.isNotBlank(name)) {
                    cf.setName_Equal(name);
                }
                if (activeSessionId != null) {
                    cf.setSessionId_NotEqual(activeSessionId);
                }

            });

            cb.specify().columnId();
        });
        if (!crawlingSessionList.isEmpty()) {
            final List<String> crawlingSessionIdList = new ArrayList<>();
            for (final CrawlingSession cs : crawlingSessionList) {
                crawlingSessionIdList.add(cs.getId());
            }

            crawlingSessionInfoBhv.queryDelete(cb2 -> {
                cb2.query().setCrawlingSessionId_InScope(crawlingSessionIdList);
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

        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        for (final CrawlingSessionInfo crawlingSessionInfo : crawlingSessionInfoList) {
            if (crawlingSessionInfo.getCreatedTime() == null) {
                crawlingSessionInfo.setCreatedTime(now);
            }
        }
        crawlingSessionInfoBhv.batchInsert(crawlingSessionInfoList);
    }

    public List<CrawlingSessionInfo> getCrawlingSessionInfoList(final String id) {
        return crawlingSessionInfoBhv.selectList(cb -> {
            cb.query().setCrawlingSessionId_Equal(id);
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
        final List<CrawlingSession> activeSessionList = crawlingSessionBhv.selectList(cb -> {
            cb.query().setSessionId_InScope(activeSessionId);
            cb.specify().columnId();
        });
        final List<String> idList = activeSessionList.stream().map(session -> session.getId()).collect(Collectors.toList());
        if (!idList.isEmpty()) {
            crawlingSessionInfoBhv.queryDelete(cb1 -> {
                cb1.query().filtered((cq, cf) -> {
                    cq.matchAll();
                    cf.not(subCf -> {
                        subCf.setCrawlingSessionId_InScope(idList);
                    });
                });
            });
            crawlingSessionBhv.queryDelete(cb2 -> {
                cb2.query().filtered((cq, cf) -> {
                    cq.matchAll();
                    cf.not(subCf -> {
                        subCf.setId_InScope(idList);
                    });
                });
            });
        }
    }

    public void importCsv(final Reader reader) {
        @SuppressWarnings("resource")
        final CsvReader csvReader = new CsvReader(reader, new CsvConfig());
        final DateFormat formatter = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
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
                        crawlingSession.setCreatedTime(formatter.parse(list.get(1)).getTime());
                        crawlingSessionBhv.insert(crawlingSession);
                    }

                    final CrawlingSessionInfo entity = new CrawlingSessionInfo();
                    entity.setCrawlingSessionId(crawlingSession.getId());
                    entity.setKey(list.get(2));
                    entity.setValue(list.get(3));
                    entity.setCreatedTime(formatter.parse(list.get(4)).getTime());
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
                cb.query().matchAll();
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

    public void deleteBefore(final long date) {
        crawlingSessionBhv.selectBulk(cb -> {
            cb.query().setExpiredTime_LessThan(date);
        }, list -> {
            final List<String> idList = list.stream().map(entity -> entity.getId()).collect(Collectors.toList());
            crawlingSessionInfoBhv.queryDelete(cb1 -> {
                cb1.query().setCrawlingSessionId_InScope(idList);
            });
            crawlingSessionBhv.queryDelete(cb2 -> {
                cb2.query().setExpiredTime_LessThan(date);
            });
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