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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.service.BsFailureUrlService;
import org.codelibs.fess.db.cbean.FailureUrlCB;
import org.codelibs.fess.db.exbhv.FailureUrlBhv;
import org.codelibs.fess.db.exentity.CrawlingConfig;
import org.codelibs.fess.db.exentity.FailureUrl;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.FailureUrlPager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.seasar.framework.container.SingletonS2Container;

public class FailureUrlService extends BsFailureUrlService implements
        Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Override
    protected void setupListCondition(final FailureUrlCB cb,
            final FailureUrlPager failureUrlPager) {
        super.setupListCondition(cb, failureUrlPager);

        // setup condition
        cb.query().addOrderBy_LastAccessTime_Desc();

        buildSearchCondition(failureUrlPager, cb);
    }

    @Override
    protected void setupEntityCondition(final FailureUrlCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(final FailureUrl failureUrl) {
        super.setupStoreCondition(failureUrl);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final FailureUrl failureUrl) {
        super.setupDeleteCondition(failureUrl);

        // setup condition

    }

    public void deleteAll(final FailureUrlPager failureUrlPager) {
        failureUrlBhv.varyingQueryDelete(cb -> {
            buildSearchCondition(failureUrlPager, cb);
        }, op -> {
            op.allowNonQueryDelete();
        });
    }

    private void buildSearchCondition(final FailureUrlPager failureUrlPager,
            final FailureUrlCB cb) {
        // search
        if (StringUtil.isNotBlank(failureUrlPager.url)) {
            cb.query().setUrl_LikeSearch(failureUrlPager.url,
                    op -> op.likeContain());
        }

        if (StringUtil.isNotBlank(failureUrlPager.errorCountMax)) {
            cb.query().setErrorCount_LessEqual(
                    Integer.parseInt(failureUrlPager.errorCountMax));
        }
        if (StringUtil.isNotBlank(failureUrlPager.errorCountMin)) {
            cb.query().setErrorCount_GreaterEqual(
                    Integer.parseInt(failureUrlPager.errorCountMin));
        }

        if (StringUtil.isNotBlank(failureUrlPager.errorName)) {
            cb.query().setErrorName_LikeSearch(failureUrlPager.errorName,
                    op -> op.likeContain());
        }

    }

    public List<String> getExcludedUrlList(final String configId) {
        final String failureCountStr = crawlerProperties.getProperty(
                Constants.FAILURE_COUNT_THRESHOLD_PROPERTY,
                Constants.DEFAULT_FAILURE_COUNT);
        final String ignoreFailureType = crawlerProperties.getProperty(
                Constants.IGNORE_FAILURE_TYPE_PROPERTY,
                Constants.DEFAULT_IGNORE_FAILURE_TYPE);
        int failureCount;
        try {
            failureCount = Integer.parseInt(failureCountStr);
        } catch (final NumberFormatException ignore) {
            failureCount = 10;
        }

        if (failureCount < 0) {
            return null;
        }

        final int count = failureCount;
        final ListResultBean<FailureUrl> list = failureUrlBhv
                .selectList(cb -> {
                    cb.query().setConfigId_Equal(configId);
                    cb.query().setErrorCount_GreaterEqual(count);
                });
        if (list.isEmpty()) {
            return null;
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
        failureUrlBhv.varyingQueryDelete(cb -> {
            cb.query().setConfigId_Equal(configId);
        }, op -> op.allowNonQueryDelete());
    }

    public void store(final CrawlingConfig crawlingConfig,
            final String errorName, final String url, final Throwable e) {
        final FailureUrlBhv failureUrlBhv = SingletonS2Container
                .getComponent(FailureUrlBhv.class);
        FailureUrl failureUrl = failureUrlBhv.selectEntity(cb -> {
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
        failureUrl.setLastAccessTime(ComponentUtil.getSystemHelper()
                .getCurrentTime());
        failureUrl.setThreadName(Thread.currentThread().getName());

        failureUrlBhv.insertOrUpdate(failureUrl);
    }

    private String getStackTrace(final Throwable t) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        return systemHelper.abbreviateLongText(sw.toString());
    }
}
