/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import org.codelibs.fess.crawler.interval.impl.DefaultIntervalController;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.util.ComponentUtil;

public class FessIntervalController extends DefaultIntervalController {

    public long getDelayMillisAfterProcessing() {
        return delayMillisAfterProcessing;
    }

    public void setDelayMillisAfterProcessing(final long delayMillisAfterProcessing) {
        this.delayMillisAfterProcessing = delayMillisAfterProcessing;
    }

    public long getDelayMillisAtNoUrlInQueue() {
        return delayMillisAtNoUrlInQueue;
    }

    public void setDelayMillisAtNoUrlInQueue(final long delayMillisAtNoUrlInQueue) {
        this.delayMillisAtNoUrlInQueue = delayMillisAtNoUrlInQueue;
    }

    public long getDelayMillisBeforeProcessing() {
        return delayMillisBeforeProcessing;
    }

    public void setDelayMillisBeforeProcessing(final long delayMillisBeforeProcessing) {
        this.delayMillisBeforeProcessing = delayMillisBeforeProcessing;
    }

    public long getDelayMillisForWaitingNewUrl() {
        return delayMillisForWaitingNewUrl;
    }

    public void setDelayMillisForWaitingNewUrl(final long delayMillisForWaitingNewUrl) {
        this.delayMillisForWaitingNewUrl = delayMillisForWaitingNewUrl;
    }

    @Override
    protected void delayForWaitingNewUrl() {
        ComponentUtil.getSystemHelper().calibrateCpuLoad();
        try {
            final IntervalControlHelper intervalControlHelper = ComponentUtil.getIntervalControlHelper();
            intervalControlHelper.checkCrawlerStatus();
            intervalControlHelper.delayByRules();
        } catch (final Exception e) {}

        super.delayForWaitingNewUrl();
    }
}
