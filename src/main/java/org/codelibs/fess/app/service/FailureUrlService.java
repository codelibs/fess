/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FailureUrlPager;
import org.codelibs.fess.es.config.cbean.FailureUrlCB;
import org.codelibs.fess.es.config.exbhv.FailureUrlBhv;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.FailureUrl;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.di.core.SingletonLaContainer;

public class FailureUrlService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected DynamicProperties systemProperties;

    @Resource
    protected FailureUrlBhv failureUrlBhv;

    public List<FailureUrl> getFailureUrlList(final FailureUrlPager failureUrlPager) {

        final PagingResultBean<FailureUrl> failureUrlList = failureUrlBhv.selectPage(cb -> {
            cb.paging(failureUrlPager.getPageSize(), failureUrlPager.getCurrentPageNumber());
            setupListCondition(cb, failureUrlPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(failureUrlList, failureUrlPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        failureUrlPager.setPageNumberList(failureUrlList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return failureUrlList;
    }

    public OptionalEntity<FailureUrl> getFailureUrl(final String id) {
        return failureUrlBhv.selectByPK(id);
    }

    public void store(final FailureUrl failureUrl) {

        failureUrlBhv.insertOrUpdate(failureUrl, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final FailureUrl failureUrl) {

        failureUrlBhv.delete(failureUrl, op -> {
            op.setRefresh(true);
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
            cb.query().setUrl_Match(failureUrlPager.url);
        }

        if (StringUtil.isNotBlank(failureUrlPager.errorCountMax)) {
            cb.query().setErrorCount_LessEqual(Integer.parseInt(failureUrlPager.errorCountMax));
        }
        if (StringUtil.isNotBlank(failureUrlPager.errorCountMin)) {
            cb.query().setErrorCount_GreaterEqual(Integer.parseInt(failureUrlPager.errorCountMin));
        }

        if (StringUtil.isNotBlank(failureUrlPager.errorName)) {
            cb.query().setErrorName_Match(failureUrlPager.errorName);
        }

    }

    public List<String> getExcludedUrlList(final String configId) {
        final String failureCountStr = systemProperties.getProperty(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY);
        final String ignoreFailureType =
                systemProperties.getProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, Constants.DEFAULT_IGNORE_FAILURE_TYPE);
        int failureCount = Constants.DEFAULT_FAILURE_COUNT;
        if (failureCountStr != null) {
            try {
                failureCount = Integer.parseInt(failureCountStr);
            } catch (final NumberFormatException ignore) {
                // ignore
            }
        }

        if (failureCount < 0) {
            return Collections.emptyList();
        }

        final int count = failureCount;
        final ListResultBean<FailureUrl> list = failureUrlBhv.selectList(cb -> {
            cb.query().setConfigId_Equal(configId);
            cb.query().setErrorCount_GreaterEqual(count);
        });
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        Pattern pattern = null;
        if (StringUtil.isNotBlank(ignoreFailureType)) {
            pattern = Pattern.compile(ignoreFailureType);
        }
        final List<String> urlList = new ArrayList<String>();
        for (final FailureUrl failureUrl : list) {
            if (pattern != null) {
                if (!pattern.matcher(failureUrl.getUrl()).matches()) {
                    urlList.add(failureUrl.getUrl());
                }
            } else {
                urlList.add(failureUrl.getUrl());
            }
        }
        return urlList;
    }

    public void deleteByConfigId(final String configId) {
        failureUrlBhv.queryDelete(cb -> {
            cb.query().setConfigId_Equal(configId);
        });
    }

    public void store(final CrawlingConfig crawlingConfig, final String errorName, final String url, final Throwable e) {
        final FailureUrlBhv bhv = SingletonLaContainer.getComponent(FailureUrlBhv.class);
        FailureUrl failureUrl = bhv.selectEntity(cb -> {
            cb.query().setUrl_Equal(url);
            if (crawlingConfig != null) {
                cb.query().setConfigId_Equal(crawlingConfig.getConfigId());
            }
        }).orElse(null);//TODO

        if (failureUrl != null) {
            failureUrl.setErrorCount(failureUrl.getErrorCount() + 1);
        } else {
            // new
            failureUrl = new FailureUrl();
            failureUrl.setErrorCount(1);
            failureUrl.setUrl(url);
            if (crawlingConfig != null) {
                failureUrl.setConfigId(crawlingConfig.getConfigId());
            }
        }

        failureUrl.setErrorName(errorName);
        failureUrl.setErrorLog(StringUtils.abbreviate(getStackTrace(e), 4000));
        failureUrl.setLastAccessTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        failureUrl.setThreadName(Thread.currentThread().getName());

        bhv.insertOrUpdate(failureUrl);
    }

    private String getStackTrace(final Throwable t) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        return systemHelper.abbreviateLongText(sw.toString());
    }
}
