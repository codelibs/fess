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

package jp.sf.fess.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;

import jp.sf.fess.db.cbean.FailureUrlCB;
import jp.sf.fess.db.exbhv.FailureUrlBhv;
import jp.sf.fess.db.exentity.CrawlingConfig;
import jp.sf.fess.db.exentity.FailureUrl;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.RobotMultipleCrawlAccessException;
import org.seasar.robot.S2RobotContext;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.helper.impl.LogHelperImpl;
import org.seasar.robot.log.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotLogHelper extends LogHelperImpl {
    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(RobotLogHelper.class);

    public int maxStackTraceLength = 4000;

    @Override
    public void log(final LogType key, final Object... objs) {
        try {
            switch (key) {
            case CRAWLING_ACCESS_EXCEPTION: {
                final S2RobotContext robotContext = (S2RobotContext) objs[0];
                final UrlQueue urlQueue = (UrlQueue) objs[1];
                Throwable e = (Throwable) objs[2];
                if (e instanceof RobotMultipleCrawlAccessException) {
                    final Throwable[] causes = ((RobotMultipleCrawlAccessException) e)
                            .getCauses();
                    if (causes.length > 0) {
                        e = causes[causes.length - 1];
                    }
                }

                String errorName;
                final Throwable cause = e.getCause();
                if (cause != null) {
                    errorName = cause.getClass().getCanonicalName();
                } else {
                    errorName = e.getClass().getCanonicalName();
                }
                storeFailureUrl(robotContext, urlQueue, errorName, e);
                break;
            }
            case CRAWLING_EXCETPION: {
                final S2RobotContext robotContext = (S2RobotContext) objs[0];
                final UrlQueue urlQueue = (UrlQueue) objs[1];
                final Throwable e = (Throwable) objs[2];

                storeFailureUrl(robotContext, urlQueue, e.getClass()
                        .getCanonicalName(), e);
                break;
            }
            default:
                break;
            }
        } catch (final Exception e) {
            logger.warn("Failed to store a failure url.", e);
        }

        super.log(key, objs);
    }

    private void storeFailureUrl(final S2RobotContext robotContext,
            final UrlQueue urlQueue, final String errorName, final Throwable e) {
        Long webConfigId = null;
        Long fileConfigId = null;

        final CrawlingConfig crawlingConfig = getCrawlingConfig(robotContext
                .getSessionId());
        if (crawlingConfig instanceof WebCrawlingConfig) {
            webConfigId = crawlingConfig.getId();
        } else if (crawlingConfig instanceof FileCrawlingConfig) {
            fileConfigId = crawlingConfig.getId();
        } else {
            return;
        }

        final FailureUrlBhv failureUrlBhv = SingletonS2Container
                .getComponent(FailureUrlBhv.class);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.query().setUrl_Equal(urlQueue.getUrl());
        if (webConfigId != null) {
            cb.query().setWebConfigId_Equal(webConfigId);
        }
        if (fileConfigId != null) {
            cb.query().setFileConfigId_Equal(fileConfigId);
        }
        FailureUrl failureUrl = failureUrlBhv.selectEntity(cb);

        if (failureUrl != null) {
            failureUrl.setErrorCount(failureUrl.getErrorCount() + 1);
        } else {
            // new
            failureUrl = new FailureUrl();
            failureUrl.setErrorCount(1);
            failureUrl.setUrl(urlQueue.getUrl());
            if (webConfigId != null) {
                failureUrl.setWebConfigId(webConfigId);
            }
            if (fileConfigId != null) {
                failureUrl.setFileConfigId(fileConfigId);
            }
        }

        failureUrl.setErrorName(errorName);
        failureUrl.setErrorLog(StringUtils.abbreviate(getStackTrace(e), 4000));
        failureUrl.setLastAccessTime(new Timestamp(System.currentTimeMillis()));
        failureUrl.setThreadName(Thread.currentThread().getName());

        failureUrlBhv.insertOrUpdate(failureUrl);
    }

    private CrawlingConfig getCrawlingConfig(final String sessionCountId) {
        return SingletonS2Container.getComponent(CrawlingConfigHelper.class)
                .get(sessionCountId);
    }

    private String getStackTrace(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        final String str = sw.toString();
        return str.length() > maxStackTraceLength ? str.substring(0,
                maxStackTraceLength) : str;
    }
}
