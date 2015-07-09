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

package org.codelibs.fess.helper;

import org.codelibs.fess.es.exentity.CrawlingConfig;
import org.codelibs.fess.service.FailureUrlService;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.robot.RobotMultipleCrawlAccessException;
import org.codelibs.robot.S2RobotContext;
import org.codelibs.robot.entity.UrlQueue;
import org.codelibs.robot.helper.impl.LogHelperImpl;
import org.codelibs.robot.log.LogType;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotLogHelper extends LogHelperImpl {
    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(RobotLogHelper.class);

    @Override
    public void log(final LogType key, final Object... objs) {
        try {
            switch (key) {
            case CRAWLING_ACCESS_EXCEPTION: {
                final S2RobotContext robotContext = (S2RobotContext) objs[0];
                final UrlQueue urlQueue = (UrlQueue) objs[1];
                Throwable e = (Throwable) objs[2];
                if (e instanceof RobotMultipleCrawlAccessException) {
                    final Throwable[] causes = ((RobotMultipleCrawlAccessException) e).getCauses();
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

                storeFailureUrl(robotContext, urlQueue, e.getClass().getCanonicalName(), e);
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

    private void storeFailureUrl(final S2RobotContext robotContext, final UrlQueue urlQueue, final String errorName, final Throwable e) {

        final CrawlingConfig crawlingConfig = getCrawlingConfig(robotContext.getSessionId());
        final String url = urlQueue.getUrl();

        final FailureUrlService failureUrlService = SingletonS2Container.getComponent(FailureUrlService.class);
        failureUrlService.store(crawlingConfig, errorName, url, e);
    }

    private CrawlingConfig getCrawlingConfig(final String sessionCountId) {
        return ComponentUtil.getCrawlingConfigHelper().get(sessionCountId);
    }

}
