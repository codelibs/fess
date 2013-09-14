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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.service.BsFailureUrlService;
import jp.sf.fess.db.cbean.FailureUrlCB;
import jp.sf.fess.db.exentity.FailureUrl;
import jp.sf.fess.pager.FailureUrlPager;

import org.codelibs.core.util.DynamicProperties;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.framework.util.StringUtil;

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
        cb.setupSelect_FileCrawlingConfig();
        cb.setupSelect_WebCrawlingConfig();

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
        final FailureUrlCB cb = new FailureUrlCB();
        buildSearchCondition(failureUrlPager, cb);
        failureUrlBhv.varyingQueryDelete(cb,
                new DeleteOption<FailureUrlCB>().allowNonQueryDelete());
    }

    private void buildSearchCondition(final FailureUrlPager failureUrlPager,
            final FailureUrlCB cb) {
        // search
        if (StringUtil.isNotBlank(failureUrlPager.url)) {
            cb.query().setUrl_LikeSearch(failureUrlPager.url,
                    new LikeSearchOption().likeContain());
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
                    new LikeSearchOption().likeContain());
        }

    }

    public List<String> getExcludedUrlList(final Long webConfigId,
            final Long fileConfigId) {
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

        final FailureUrlCB cb = new FailureUrlCB();
        cb.query().setWebConfigId_Equal(webConfigId);
        cb.query().setFileConfigId_Equal(fileConfigId);
        cb.query().setErrorCount_GreaterEqual(failureCount);
        final ListResultBean<FailureUrl> list = failureUrlBhv.selectList(cb);
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

    public void deleteByFileConfigId(final Long id) {
        final FailureUrlCB cb = new FailureUrlCB();
        cb.query().setFileConfigId_Equal(id);
        failureUrlBhv.varyingQueryDelete(cb,
                new DeleteOption<FailureUrlCB>().allowNonQueryDelete());
    }

    public void deleteByWebConfigId(final Long id) {
        final FailureUrlCB cb = new FailureUrlCB();
        cb.query().setWebConfigId_Equal(id);
        failureUrlBhv.varyingQueryDelete(cb,
                new DeleteOption<FailureUrlCB>().allowNonQueryDelete());
    }
}
