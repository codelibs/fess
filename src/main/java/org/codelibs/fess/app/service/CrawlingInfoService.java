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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.CrawlingInfoPager;
import org.codelibs.fess.es.config.cbean.CrawlingInfoCB;
import org.codelibs.fess.es.config.exbhv.CrawlingInfoBhv;
import org.codelibs.fess.es.config.exbhv.CrawlingInfoParamBhv;
import org.codelibs.fess.es.config.exentity.CrawlingInfo;
import org.codelibs.fess.es.config.exentity.CrawlingInfoParam;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;

public class CrawlingInfoService {

    private static final Logger logger = LogManager.getLogger(CrawlingInfoService.class);

    @Resource
    protected CrawlingInfoParamBhv crawlingInfoParamBhv;

    @Resource
    protected CrawlingInfoBhv crawlingInfoBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<CrawlingInfo> getCrawlingInfoList(final CrawlingInfoPager crawlingInfoPager) {

        final PagingResultBean<CrawlingInfo> crawlingInfoList = crawlingInfoBhv.selectPage(cb -> {
            cb.paging(crawlingInfoPager.getPageSize(), crawlingInfoPager.getCurrentPageNumber());
            setupListCondition(cb, crawlingInfoPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(crawlingInfoList, crawlingInfoPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        crawlingInfoPager.setPageNumberList(
                crawlingInfoList.pageRange(op -> op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger())).createPageNumberList());

        return crawlingInfoList;
    }

    public OptionalEntity<CrawlingInfo> getCrawlingInfo(final String id) {
        return crawlingInfoBhv.selectByPK(id);
    }

    public void store(final CrawlingInfo crawlingInfo) {
        setupStoreCondition(crawlingInfo);

        crawlingInfoBhv.insertOrUpdate(crawlingInfo, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    public void delete(final CrawlingInfo crawlingInfo) {
        setupDeleteCondition(crawlingInfo);

        crawlingInfoBhv.delete(crawlingInfo, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    protected void setupListCondition(final CrawlingInfoCB cb, final CrawlingInfoPager crawlingInfoPager) {
        if (crawlingInfoPager.id != null) {
            cb.query().docMeta().setId_Equal(crawlingInfoPager.id);
        }
        // TODO Long, Integer, String supported only.
        if (StringUtil.isNotBlank(crawlingInfoPager.sessionId)) {
            cb.query().setSessionId_Match(crawlingInfoPager.sessionId);
        }
        cb.query().addOrderBy_CreatedTime_Desc();
    }

    protected void setupStoreCondition(final CrawlingInfo crawlingInfo) {
        if (crawlingInfo == null) {
            throw new FessSystemException("Crawling Session is null.");
        }
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        if (crawlingInfo.getCreatedTime() == null) {
            crawlingInfo.setCreatedTime(now);
        }
    }

    protected void setupDeleteCondition(final CrawlingInfo crawlingInfo) {
        crawlingInfoParamBhv.queryDelete(cb -> cb.query().setCrawlingInfoId_Equal(crawlingInfo.getId()));
    }

    public void deleteSessionIdsBefore(final String activeSessionId, final String name, final long date) {
        final List<CrawlingInfo> crawlingInfoList = crawlingInfoBhv.selectList(cb -> {
            cb.query().filtered((cq, cf) -> {
                cq.setExpiredTime_LessEqual(date);
                if (StringUtil.isNotBlank(name)) {
                    cf.setName_Equal(name);
                }
                if (activeSessionId != null) {
                    cf.setSessionId_NotEqual(activeSessionId);
                }

            });

            cb.fetchFirst(fessConfig.getPageCrawlingInfoMaxFetchSizeAsInteger());
            cb.specify().columnId();
        });
        if (!crawlingInfoList.isEmpty()) {
            final List<String> crawlingInfoIdList = new ArrayList<>();
            for (final CrawlingInfo cs : crawlingInfoList) {
                crawlingInfoIdList.add(cs.getId());
            }
            crawlingInfoParamBhv.queryDelete(cb2 -> cb2.query().setCrawlingInfoId_InScope(crawlingInfoIdList));
            crawlingInfoBhv.batchDelete(crawlingInfoList, op -> op.setRefreshPolicy(Constants.TRUE));
        }
    }

    public void storeInfo(final List<CrawlingInfoParam> crawlingInfoParamList) {
        if (crawlingInfoParamList == null) {
            throw new FessSystemException("Crawling Session Info is null.");
        }

        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        for (final CrawlingInfoParam crawlingInfoParam : crawlingInfoParamList) {
            if (crawlingInfoParam.getCreatedTime() == null) {
                crawlingInfoParam.setCreatedTime(now);
            }
        }
        crawlingInfoParamBhv.batchInsert(crawlingInfoParamList, op -> op.setRefreshPolicy(Constants.TRUE));
    }

    public List<CrawlingInfoParam> getCrawlingInfoParamList(final String id) {
        return crawlingInfoParamBhv.selectList(cb -> {
            cb.query().setCrawlingInfoId_Equal(id);
            cb.query().addOrderBy_CreatedTime_Asc();
            cb.fetchFirst(fessConfig.getPageCrawlingInfoParamMaxFetchSizeAsInteger());
        });
    }

    public List<CrawlingInfoParam> getLastCrawlingInfoParamList(final String sessionId) {
        final CrawlingInfo crawlingInfo = getLast(sessionId);
        if (crawlingInfo == null) {
            return Collections.emptyList();
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return crawlingInfoParamBhv.selectList(cb -> {
            cb.query().setCrawlingInfoId_Equal(crawlingInfo.getId());
            cb.query().addOrderBy_CreatedTime_Asc();
            cb.paging(fessConfig.getPageCrawlingInfoParamMaxFetchSizeAsInteger(), 1);
        });
    }

    public void deleteOldSessions(final Set<String> activeSessionId) {
        final List<CrawlingInfo> activeSessionList =
                activeSessionId.isEmpty() ? Collections.emptyList() : crawlingInfoBhv.selectList(cb -> {
                    cb.query().setSessionId_InScope(activeSessionId);
                    cb.fetchFirst(fessConfig.getPageCrawlingInfoMaxFetchSizeAsInteger());
                    cb.specify().columnId();
                });
        final List<String> idList = activeSessionList.stream().map(CrawlingInfo::getId).collect(Collectors.toList());
        crawlingInfoParamBhv.queryDelete(cb1 -> cb1.query().filtered((cq, cf) -> {
            cq.matchAll();
            if (!idList.isEmpty()) {
                cf.not(subCf -> subCf.setCrawlingInfoId_InScope(idList));
            }
        }));
        crawlingInfoBhv.queryDelete(cb2 -> cb2.query().filtered((cq, cf) -> {
            cq.matchAll();
            if (!idList.isEmpty()) {
                cf.not(subCf -> subCf.setId_InScope(idList));
            }
        }));
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
                    CrawlingInfo crawlingInfo = crawlingInfoBhv.selectEntity(cb -> {
                        cb.query().setSessionId_Equal(sessionId);
                        cb.specify().columnSessionId();
                    }).orElse(null);//TODO
                    if (crawlingInfo == null) {
                        crawlingInfo = new CrawlingInfo();
                        crawlingInfo.setSessionId(list.get(0));
                        crawlingInfo.setCreatedTime(formatter.parse(list.get(1)).getTime());
                        crawlingInfoBhv.insert(crawlingInfo, op -> op.setRefreshPolicy(Constants.TRUE));
                    }

                    final CrawlingInfoParam entity = new CrawlingInfoParam();
                    entity.setCrawlingInfoId(crawlingInfo.getId());
                    entity.setKey(list.get(2));
                    entity.setValue(list.get(3));
                    entity.setCreatedTime(formatter.parse(list.get(4)).getTime());
                    crawlingInfoParamBhv.insert(entity, op -> op.setRefreshPolicy(Constants.TRUE));
                } catch (final Exception e) {
                    logger.warn("Failed to read a click log: {}", list, e);
                }
            }
        } catch (final IOException e) {
            logger.warn("Failed to read a click log.", e);
        }
    }

    public void exportCsv(final Writer writer) {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, cfg);
        try {
            final List<String> list = new ArrayList<>();
            list.add("SessionId");
            list.add("SessionCreatedTime");
            list.add("Key");
            list.add("Value");
            list.add("CreatedTime");
            csvWriter.writeValues(list);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            crawlingInfoParamBhv.selectCursor(cb -> cb.query().matchAll(), new EntityRowHandler<CrawlingInfoParam>() {
                @Override
                public void handle(final CrawlingInfoParam entity) {
                    final List<String> list = new ArrayList<>();
                    entity.getCrawlingInfo().ifPresent(crawlingInfo -> {
                        addToList(list, crawlingInfo.getSessionId());
                        addToList(list, crawlingInfo.getCreatedTime());
                    });
                    // TODO
                    if (!entity.getCrawlingInfo().isPresent()) {
                        addToList(list, "");
                        addToList(list, "");
                    }
                    addToList(list, entity.getKey());
                    addToList(list, entity.getValue());
                    addToList(list, entity.getCreatedTime());
                    try {
                        csvWriter.writeValues(list);
                    } catch (final IOException e) {
                        logger.warn("Failed to write a crawling session info: {}", entity, e);
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
            logger.warn("Failed to write a crawling session info.", e);
        }
    }

    public void deleteBefore(final long date) {
        crawlingInfoBhv.selectBulk(cb -> cb.query().setExpiredTime_LessThan(date), list -> {
            final List<String> idList = list.stream().map(CrawlingInfo::getId).collect(Collectors.toList());
            crawlingInfoParamBhv.queryDelete(cb1 -> cb1.query().setCrawlingInfoId_InScope(idList));
            crawlingInfoBhv.queryDelete(cb2 -> cb2.query().setExpiredTime_LessThan(date));
        });
    }

    public CrawlingInfo getLast(final String sessionId) {
        final ListResultBean<CrawlingInfo> list = crawlingInfoBhv.selectList(cb -> {
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