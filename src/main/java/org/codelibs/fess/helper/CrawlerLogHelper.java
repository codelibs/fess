/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MultipleCrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.LogHelperImpl;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.CrawlerStatsHelper.StatsAction;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.codelibs.fess.util.ComponentUtil;

public class CrawlerLogHelper extends LogHelperImpl {
    private static final Logger logger = LogManager.getLogger(CrawlerLogHelper.class);

    @Override
    public void log(final LogType key, final Object... objs) {
        if (!ComponentUtil.available()) {
            if (logger.isDebugEnabled()) {
                logger.debug("container was destroyed.");
            }
            return;
        }
        super.log(key, objs);
    }

    @Override
    protected void processStartCrawling(final Object... objs) {
        super.processStartCrawling(objs);
        if (objs.length > 1 && objs[1] instanceof final UrlQueue<?> urlQueue) {
            ComponentUtil.getCrawlerStatsHelper().begin(urlQueue);
        }
    }

    @Override
    protected void processCleanupCrawling(final Object... objs) {
        super.processCleanupCrawling(objs);
        if (objs.length > 1 && objs[1] instanceof final UrlQueue<?> urlQueue) {
            ComponentUtil.getCrawlerStatsHelper().done(urlQueue);
        }
    }

    @Override
    protected void processProcessChildUrlByException(final Object... objs) {
        super.processProcessChildUrlByException(objs);
        if (objs.length > 1 && objs[1] instanceof final UrlQueue<?> urlQueue) {
            ComponentUtil.getCrawlerStatsHelper().record(urlQueue, StatsAction.CHILD_URL);
        }
    }

    @Override
    protected void processProcessChildUrlsByException(final Object... objs) {
        super.processProcessChildUrlsByException(objs);
        if (objs.length > 1 && objs[1] instanceof final UrlQueue<?> urlQueue) {
            ComponentUtil.getCrawlerStatsHelper().record(urlQueue, StatsAction.CHILD_URLS);
        }
    }

    @Override
    protected void processFinishedCrawling(final Object... objs) {
        super.processFinishedCrawling(objs);
        if (objs.length > 1 && objs[1] instanceof final UrlQueue<?> urlQueue) {
            ComponentUtil.getCrawlerStatsHelper().record(urlQueue, StatsAction.FINISHED);
        }
    }

    @Override
    protected void processCrawlingAccessException(final Object... objs) {
        String failureUrlId = "?";
        final CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final CrawlingAccessException cae = (CrawlingAccessException) objs[2];
        try {
            Throwable t = cae;
            if (t instanceof final MultipleCrawlingAccessException mcae) {
                final Throwable[] causes = mcae.getCauses();
                if (causes.length > 0) {
                    t = causes[causes.length - 1];
                }
            }

            String errorName;
            final Throwable cause = t.getCause();
            if (cause != null) {
                errorName = cause.getClass().getCanonicalName();
            } else {
                errorName = t.getClass().getCanonicalName();
            }
            final FailureUrl failureUrl = storeFailureUrl(crawlerContext, urlQueue, errorName, t);
            if (failureUrl != null) {
                failureUrlId = failureUrl.getId();
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

        if (cae.isDebugEnabled()) {
            logger.debug("[{}] Crawling Access Exception at {}", failureUrlId, urlQueue.getUrl(), cae);
        } else if (cae.isInfoEnabled()) {
            logger.info("[{}] {}", failureUrlId, cae.getMessage());
        } else if (cae.isWarnEnabled()) {
            logger.warn("[{}] Crawling Access Exception at {}", failureUrlId, urlQueue.getUrl(), cae);
        } else if (cae.isErrorEnabled()) {
            logger.error("[{}] Crawling Access Exception at {}", failureUrlId, urlQueue.getUrl(), cae);
        }

        ComponentUtil.getCrawlerStatsHelper().record(urlQueue, StatsAction.ACCESS_EXCEPTION);
    }

    @Override
    protected void processCrawlingException(final Object... objs) {
        try {
            final CrawlerContext crawlerContext = (CrawlerContext) objs[0];
            final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
            final Throwable e = (Throwable) objs[2];

            storeFailureUrl(crawlerContext, urlQueue, e.getClass().getCanonicalName(), e);
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

        super.processCrawlingException(objs);
        if (objs.length > 1 && objs[1] instanceof final UrlQueue<?> urlQueue) {
            ComponentUtil.getCrawlerStatsHelper().record(urlQueue, StatsAction.ACCESS_EXCEPTION);
        }
    }

    protected FailureUrl storeFailureUrl(final CrawlerContext crawlerContext, final UrlQueue<?> urlQueue, final String errorName,
            final Throwable e) {

        final CrawlingConfig crawlingConfig = getCrawlingConfig(crawlerContext.getSessionId());
        final String url = urlQueue.getUrl();

        final FailureUrlService failureUrlService = ComponentUtil.getComponent(FailureUrlService.class);
        return failureUrlService.store(crawlingConfig, errorName, url, e);
    }

    protected CrawlingConfig getCrawlingConfig(final String sessionCountId) {
        return ComponentUtil.getCrawlingConfigHelper().get(sessionCountId);
    }

}
