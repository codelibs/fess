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
package org.codelibs.fess.entity;

/**
 * The progress of a running crawl of one crawl configuration, as the admin screen and the admin API show it.
 */
public class CrawlProgress {

    /** The highest percentage shown for a crawl that has not completed. */
    public static final int MAX_RUNNING_PERCENT = 99;

    private final String configId;

    private final String name;

    private final long count;

    private final long lastCount;

    private final long updatedTime;

    private final Integer percent;

    /**
     * Creates the progress of a running crawl.
     *
     * @param configId the crawl configuration ID (W..., F... or D...)
     * @param name the display name of the crawl configuration
     * @param count the number of documents processed so far by the running crawl
     * @param lastCount the number of documents processed by the previous completed crawl, or 0 if unknown
     * @param updatedTime the time the count was written (epoch milliseconds)
     */
    public CrawlProgress(final String configId, final String name, final long count, final long lastCount, final long updatedTime) {
        this.configId = configId;
        this.name = name;
        this.count = count;
        this.lastCount = lastCount;
        this.updatedTime = updatedTime;
        this.percent = calculatePercent(count, lastCount);
    }

    /**
     * Calculates the percentage of a running crawl against the previous completed crawl. A crawl that
     * has not completed never reaches 100, because the previous count is only an estimate.
     *
     * @param count the number of documents processed so far
     * @param lastCount the number of documents processed by the previous completed crawl
     * @return the percentage between 0 and 99, or null if there is no previous count to compare with
     */
    public static Integer calculatePercent(final long count, final long lastCount) {
        if (lastCount <= 0) {
            return null;
        }
        if (count <= 0) {
            return 0;
        }
        return (int) Math.min(MAX_RUNNING_PERCENT, count * 100 / lastCount);
    }

    /**
     * Returns the crawl configuration ID.
     *
     * @return the crawl configuration ID
     */
    public String getConfigId() {
        return configId;
    }

    /**
     * Returns the display name of the crawl configuration.
     *
     * @return the name, or the configuration ID if the configuration is not found
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of documents processed so far by the running crawl.
     *
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * Returns the number of documents processed by the previous completed crawl.
     *
     * @return the count, or 0 if unknown
     */
    public long getLastCount() {
        return lastCount;
    }

    /**
     * Returns the time the count was written.
     *
     * @return the time in epoch milliseconds
     */
    public long getUpdatedTime() {
        return updatedTime;
    }

    /**
     * Returns the percentage of the running crawl.
     *
     * @return the percentage between 0 and 99, or null if the previous count is unknown
     */
    public Integer getPercent() {
        return percent;
    }
}
