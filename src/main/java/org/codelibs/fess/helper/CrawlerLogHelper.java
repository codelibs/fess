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
package org.codelibs.fess.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.exception.MultipleCrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.LogHelperImpl;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.entity.FailureUrlDetails;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.CrawlerStatsHelper.StatsAction;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Helper class for crawler logging operations.
 * Extends LogHelperImpl to provide specialized logging functionality for crawler operations,
 * including failure URL tracking and crawler statistics integration.
 */
public class CrawlerLogHelper extends LogHelperImpl {

    /**
     * Creates a new instance of CrawlerLogHelper.
     */
    public CrawlerLogHelper() {
    }

    private static final Logger logger = LogManager.getLogger(CrawlerLogHelper.class);

    /** The details of the document whose crawling failed, from the crawler thread to the failure URL. */
    private static final ThreadLocal<PendingDetails> pendingDetails = new ThreadLocal<>();

    private record PendingDetails(String url, FailureUrlDetails details) {
    }

    /**
     * Remembers what the crawler thread knows about the document whose response it could not process,
     * for the failure URL that the crawler thread stores next on the same thread. The values are
     * dropped when the crawling of the URL starts or ends, or when they are used.
     *
     * @param url the URL that is being crawled
     * @param details the details of its response
     */
    public static void rememberFailureDetails(final String url, final FailureUrlDetails details) {
        if (details == null) {
            pendingDetails.remove();
        } else {
            pendingDetails.set(new PendingDetails(url, details));
        }
    }

    /**
     * Forgets the details that were remembered on this thread.
     */
    public static void clearFailureDetails() {
        pendingDetails.remove();
    }

    /**
     * Takes the details that were remembered on this thread for the URL, and forgets them.
     *
     * @param url the URL of the failure
     * @return the details, or null when none were remembered for the URL
     */
    public static FailureUrlDetails takeFailureDetails(final String url) {
        final PendingDetails pending = pendingDetails.get();
        pendingDetails.remove();
        return pending != null && pending.url().equals(url) ? pending.details() : null;
    }

    @Override
    public void log(final LogType key, final Object... objs) {
        if (key == LogType.START_CRAWLING || key == LogType.CLEANUP_CRAWLING) {
            // the thread is pooled: details of one URL must not reach the failure of the next
            clearFailureDetails();
        }
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
            logger.warn("Failed to store failure url: url={}", urlQueue.getLogUrl(), e);
        }

        if (cae.isDebugEnabled()) {
            logger.debug("[{}] Crawling Access Exception: url={}", failureUrlId, urlQueue.getLogUrl(), cae);
        } else if (cae.isInfoEnabled()) {
            logger.info("[{}] {}", failureUrlId, removeUserInfo(cae.getMessage(), urlQueue));
        } else if (cae.isWarnEnabled()) {
            logger.warn("[{}] Crawling Access Exception: url={}", failureUrlId, urlQueue.getLogUrl(), cae);
        } else if (cae.isErrorEnabled()) {
            logger.error("[{}] Crawling Access Exception: url={}", failureUrlId, urlQueue.getLogUrl(), cae);
        }

        ComponentUtil.getCrawlerStatsHelper().record(urlQueue, StatsAction.ACCESS_EXCEPTION);
    }

    @Override
    protected void processCrawlingException(final Object... objs) {
        final UrlQueue<?> urlQueue = objs.length > 1 && objs[1] instanceof UrlQueue<?> ? (UrlQueue<?>) objs[1] : null;
        try {
            final CrawlerContext crawlerContext = (CrawlerContext) objs[0];
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
            logger.warn("Failed to store failure url: url={}", urlQueue != null ? urlQueue.getLogUrl() : "unknown", e);
        }

        super.processCrawlingException(objs);
        if (urlQueue != null) {
            ComponentUtil.getCrawlerStatsHelper().record(urlQueue, StatsAction.ACCESS_EXCEPTION);
        }
    }

    /**
     * Stores a failure URL with error information for later analysis.
     *
     * @param crawlerContext the crawler context
     * @param urlQueue the URL queue containing the failed URL
     * @param errorName the name/type of the error
     * @param e the throwable that caused the failure
     * @return the stored FailureUrl entity
     */
    protected FailureUrl storeFailureUrl(final CrawlerContext crawlerContext, final UrlQueue<?> urlQueue, final String errorName,
            final Throwable e) {

        final CrawlingConfig crawlingConfig = getCrawlingConfig(crawlerContext.getSessionId());
        final String url = urlQueue.getUrl();

        FailureUrlDetails details = takeFailureDetails(url);
        if (details == null && e instanceof MaxLengthExceededException) {
            // the client rejected the document before it returned a response: the size is only in the message
            details = FailureUrlDetails.ofMaxLengthMessage(e.getMessage());
        }
        final FailureUrlService failureUrlService = ComponentUtil.getComponent(FailureUrlService.class);
        return failureUrlService.store(crawlingConfig, errorName, url, e, details);
    }

    /**
     * Gets the crawling configuration for the specified session ID.
     *
     * @param sessionCountId the session count ID
     * @return the crawling configuration
     */
    protected CrawlingConfig getCrawlingConfig(final String sessionCountId) {
        return ComponentUtil.getCrawlingConfigHelper().get(sessionCountId);
    }

}
