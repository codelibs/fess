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
package org.codelibs.fess.crawler.interval;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.interval.impl.DefaultIntervalController;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.util.ComponentUtil;

/**
 * FessIntervalController extends DefaultIntervalController to provide
 * Fess-specific interval control functionality for web crawling operations.
 * This controller manages delays and timing for various crawling states
 * including processing delays, queue waiting times, and new URL discovery.
 */
public class FessIntervalController extends DefaultIntervalController {

    private static final Logger logger = LogManager.getLogger(FessIntervalController.class);

    /**
     * Default constructor.
     */
    public FessIntervalController() {
        super();
    }

    /**
     * Gets the delay time in milliseconds after processing a URL.
     *
     * @return the delay time in milliseconds after processing
     */
    public long getDelayMillisAfterProcessing() {
        return delayMillisAfterProcessing;
    }

    /**
     * Sets the delay time in milliseconds after processing a URL.
     *
     * @param delayMillisAfterProcessing the delay time in milliseconds after processing
     */
    public void setDelayMillisAfterProcessing(final long delayMillisAfterProcessing) {
        this.delayMillisAfterProcessing = delayMillisAfterProcessing;
    }

    /**
     * Gets the delay time in milliseconds when there are no URLs in the queue.
     *
     * @return the delay time in milliseconds when no URLs are available
     */
    public long getDelayMillisAtNoUrlInQueue() {
        return delayMillisAtNoUrlInQueue;
    }

    /**
     * Sets the delay time in milliseconds when there are no URLs in the queue.
     *
     * @param delayMillisAtNoUrlInQueue the delay time in milliseconds when no URLs are available
     */
    public void setDelayMillisAtNoUrlInQueue(final long delayMillisAtNoUrlInQueue) {
        this.delayMillisAtNoUrlInQueue = delayMillisAtNoUrlInQueue;
    }

    /**
     * Gets the delay time in milliseconds before processing a URL.
     *
     * @return the delay time in milliseconds before processing
     */
    public long getDelayMillisBeforeProcessing() {
        return delayMillisBeforeProcessing;
    }

    /**
     * Sets the delay time in milliseconds before processing a URL.
     *
     * @param delayMillisBeforeProcessing the delay time in milliseconds before processing
     */
    public void setDelayMillisBeforeProcessing(final long delayMillisBeforeProcessing) {
        this.delayMillisBeforeProcessing = delayMillisBeforeProcessing;
    }

    /**
     * Gets the delay time in milliseconds for waiting for new URLs.
     *
     * @return the delay time in milliseconds for waiting for new URLs
     */
    public long getDelayMillisForWaitingNewUrl() {
        return delayMillisForWaitingNewUrl;
    }

    /**
     * Sets the delay time in milliseconds for waiting for new URLs.
     *
     * @param delayMillisForWaitingNewUrl the delay time in milliseconds for waiting for new URLs
     */
    public void setDelayMillisForWaitingNewUrl(final long delayMillisForWaitingNewUrl) {
        this.delayMillisForWaitingNewUrl = delayMillisForWaitingNewUrl;
    }

    /**
     * Delays the crawler while waiting for new URLs to be available.
     * This method calibrates CPU load, checks crawler status, applies
     * interval control rules, and then calls the parent implementation.
     */
    @Override
    protected void delayForWaitingNewUrl() {
        ComponentUtil.getSystemHelper().calibrateCpuLoad();
        try {
            final IntervalControlHelper intervalControlHelper = ComponentUtil.getIntervalControlHelper();
            intervalControlHelper.checkCrawlerStatus();
            intervalControlHelper.delayByRules();
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to apply interval control rules", e);
            }
        }

        super.delayForWaitingNewUrl();
    }
}
