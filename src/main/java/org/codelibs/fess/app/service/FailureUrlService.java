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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FailureUrlPager;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.FailureUrlCB;
import org.codelibs.fess.opensearch.config.exbhv.FailureUrlBhv;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing failure URLs that occur during web crawling.
 * Provides functionality to store, retrieve, and manage failed crawling attempts
 * with their associated error information.
 */
public class FailureUrlService {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FailureUrlService.class);

    /** Behavior class for FailureUrl entity operations */
    @Resource
    protected FailureUrlBhv failureUrlBhv;

    /** Configuration settings for Fess */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Retrieves a paginated list of failure URLs based on the provided pager criteria.
     *
     * @param failureUrlPager the pager containing search criteria and pagination settings
     * @return a list of FailureUrl entities matching the criteria
     */
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

    /**
     * Retrieves a specific failure URL by its ID.
     *
     * @param id the unique identifier of the failure URL
     * @return an OptionalEntity containing the FailureUrl if found, empty otherwise
     */
    public OptionalEntity<FailureUrl> getFailureUrl(final String id) {
        return failureUrlBhv.selectByPK(id);
    }

    /**
     * Stores or updates a failure URL entity in the data store.
     *
     * @param failureUrl the FailureUrl entity to store or update
     */
    public void store(final FailureUrl failureUrl) {

        failureUrlBhv.insertOrUpdate(failureUrl, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Deletes a failure URL entity from the data store.
     *
     * @param failureUrl the FailureUrl entity to delete
     */
    public void delete(final FailureUrl failureUrl) {

        failureUrlBhv.delete(failureUrl, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Sets up the condition builder for listing failure URLs with pagination and filtering.
     *
     * @param cb the condition builder to configure
     * @param failureUrlPager the pager containing filter criteria
     */
    protected void setupListCondition(final FailureUrlCB cb, final FailureUrlPager failureUrlPager) {
        if (failureUrlPager.id != null) {
            cb.query().docMeta().setId_Equal(failureUrlPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_LastAccessTime_Desc();

        buildSearchCondition(failureUrlPager, cb);
    }

    /**
     * Deletes all failure URLs that match the criteria specified in the pager.
     *
     * @param failureUrlPager the pager containing deletion criteria
     */
    public void deleteAll(final FailureUrlPager failureUrlPager) {
        failureUrlBhv.queryDelete(cb -> {
            buildSearchCondition(failureUrlPager, cb);
        });
    }

    /**
     * Builds search conditions for failure URL queries based on pager criteria.
     *
     * @param failureUrlPager the pager containing search criteria
     * @param cb the condition builder to configure with search conditions
     */
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

    /**
     * Deletes all failure URLs associated with a specific configuration ID.
     *
     * @param configId the configuration ID to delete failure URLs for
     */
    public void deleteByConfigId(final String configId) {
        failureUrlBhv.queryDelete(cb -> {
            cb.query().setConfigId_Equal(configId);
        });
    }

    /**
     * Stores a new failure URL or updates an existing one with error information.
     * Creates a new failure URL entry or increments the error count for an existing URL.
     *
     * @param crawlingConfig the crawling configuration associated with the failure
     * @param errorName the name/type of the error that occurred
     * @param url the URL that failed to be crawled
     * @param e the exception that caused the failure
     * @return the stored or updated FailureUrl entity, or null if the exception should be ignored
     */
    public FailureUrl store(final CrawlingConfig crawlingConfig, final String errorName, final String url, final Throwable e) {
        if (e instanceof ContainerNotAvailableException) {
            return null;
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
        return failureUrl;
    }

    /**
     * Extracts and returns the stack trace from a throwable as a string.
     * The stack trace is abbreviated if it exceeds the configured maximum length.
     *
     * @param t the throwable to extract the stack trace from
     * @return the stack trace as a string, or empty string if extraction fails
     */
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
