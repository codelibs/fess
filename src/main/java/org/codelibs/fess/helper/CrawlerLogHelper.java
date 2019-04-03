/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.MultipleCrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.LogHelperImpl;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerLogHelper extends LogHelperImpl {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerLogHelper.class);

    @Override
    public void log(final LogType key, final Object... objs) {
        if (!ComponentUtil.available()) {
            if (logger.isDebugEnabled()) {
                logger.debug("container was destroyed.");
            }
            return;
        }
        try {
            switch (key) {
            case CRAWLING_ACCESS_EXCEPTION: {
                final CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                Throwable e = (Throwable) objs[2];
                if (e instanceof MultipleCrawlingAccessException) {
                    final Throwable[] causes = ((MultipleCrawlingAccessException) e).getCauses();
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
                storeFailureUrl(crawlerContext, urlQueue, errorName, e);
                break;
            }
            case CRAWLING_EXCETPION: {
                final CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final Throwable e = (Throwable) objs[2];

                storeFailureUrl(crawlerContext, urlQueue, e.getClass().getCanonicalName(), e);
                break;
            }
            default:
                break;
            }
        } catch (final ContainerNotAvailableException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("container was destroyed.");
            }
            return;
        } catch (final Exception e) {
            if (!ComponentUtil.available()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("container was destroyed.");
                }
                return;
            }
            logger.warn("Failed to store a failure url.", e);
        }

        super.log(key, objs);
    }

    private void storeFailureUrl(final CrawlerContext crawlerContext, final UrlQueue<?> urlQueue, final String errorName, final Throwable e) {

        final CrawlingConfig crawlingConfig = getCrawlingConfig(crawlerContext.getSessionId());
        final String url = urlQueue.getUrl();

        final FailureUrlService failureUrlService = ComponentUtil.getComponent(FailureUrlService.class);
        failureUrlService.store(crawlingConfig, errorName, url, e);
    }

    private CrawlingConfig getCrawlingConfig(final String sessionCountId) {
        return ComponentUtil.getCrawlingConfigHelper().get(sessionCountId);
    }

}
