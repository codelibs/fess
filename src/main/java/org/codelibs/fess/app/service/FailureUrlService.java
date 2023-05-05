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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FailureUrlPager;
import org.codelibs.fess.es.config.cbean.FailureUrlCB;
import org.codelibs.fess.es.config.exbhv.FailureUrlBhv;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.FailureUrl;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class FailureUrlService {

    private static final Logger logger = LogManager.getLogger(FailureUrlService.class);

    @Resource
    protected FailureUrlBhv failureUrlBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<FailureUrl> getFailureUrlList(final FailureUrlPager failureUrlPager) {

        final PagingResultBean<FailureUrl> failureUrlList = failureUrlBhv.selectPage(cb -> {
            cb.paging(failureUrlPager.getPageSize(), failureUrlPager.getCurrentPageNumber());
            setupListCondition(cb, failureUrlPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(failureUrlList, failureUrlPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        failureUrlPager.setPageNumberList(failureUrlList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return failureUrlList;
    }

    public OptionalEntity<FailureUrl> getFailureUrl(final String id) {
        return failureUrlBhv.selectByPK(id);
    }

    public void store(final FailureUrl failureUrl) {

        failureUrlBhv.insertOrUpdate(failureUrl, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public void delete(final FailureUrl failureUrl) {

        failureUrlBhv.delete(failureUrl, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    protected void setupListCondition(final FailureUrlCB cb, final FailureUrlPager failureUrlPager) {
        if (failureUrlPager.id != null) {
            cb.query().docMeta().setId_Equal(failureUrlPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_LastAccessTime_Desc();

        buildSearchCondition(failureUrlPager, cb);
    }

    public void deleteAll(final FailureUrlPager failureUrlPager) {
        failureUrlBhv.queryDelete(cb -> {
            buildSearchCondition(failureUrlPager, cb);
        });
    }

    private void buildSearchCondition(final FailureUrlPager failureUrlPager, final FailureUrlCB cb) {
        // search
        if (StringUtil.isNotBlank(failureUrlPager.url)) {
            cb.query().setUrl_Wildcard(failureUrlPager.url);
        }

        if (StringUtil.isNotBlank(failureUrlPager.errorCountMax)) {
            cb.query().setErrorCount_LessEqual(Integer.parseInt(failureUrlPager.errorCountMax));
        }
        if (StringUtil.isNotBlank(failureUrlPager.errorCountMin)) {
            cb.query().setErrorCount_GreaterEqual(Integer.parseInt(failureUrlPager.errorCountMin));
        }

        if (StringUtil.isNotBlank(failureUrlPager.errorName)) {
            cb.query().setErrorName_Wildcard(failureUrlPager.errorName);
        }

    }

    public void deleteByConfigId(final String configId) {
        failureUrlBhv.queryDelete(cb -> {
            cb.query().setConfigId_Equal(configId);
        });
    }

    public void store(final CrawlingConfig crawlingConfig, final String errorName, final String url, final Throwable e) {
        if (e instanceof ContainerNotAvailableException) {
            return;
        }

        final FailureUrlBhv bhv = ComponentUtil.getComponent(FailureUrlBhv.class);
        final FailureUrl failureUrl = bhv.selectEntity(cb -> {
            cb.query().setUrl_Equal(url);
            if (crawlingConfig != null) {
                cb.query().setConfigId_Equal(crawlingConfig.getConfigId());
            }
        }).map(entity -> {
            entity.setErrorCount(entity.getErrorCount() + 1);
            return entity;
        }).orElseGet(() -> {
            final FailureUrl entity = new FailureUrl();
            entity.setErrorCount(1);
            entity.setUrl(url);
            if (crawlingConfig != null) {
                entity.setConfigId(crawlingConfig.getConfigId());
            }
            return entity;
        });

        failureUrl.setErrorName(errorName);
        failureUrl.setErrorLog(getStackTrace(e));
        failureUrl.setLastAccessTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        failureUrl.setThreadName(Thread.currentThread().getName());

        bhv.insertOrUpdate(failureUrl, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    private String getStackTrace(final Throwable t) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
            pw.flush();
            return systemHelper.abbreviateLongText(sw.toString());
        } catch (final IOException e) {
            logger.warn("Failed to print the stack trace {}", t.getMessage(), e);
        }
        return StringUtil.EMPTY;
    }
}
