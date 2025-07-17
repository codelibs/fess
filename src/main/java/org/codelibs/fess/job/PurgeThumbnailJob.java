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
package org.codelibs.fess.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Job for purging expired thumbnail files from the system.
 * This job removes thumbnail files that have exceeded their configured expiration time
 * to prevent disk space from being consumed by old thumbnails.
 */
public class PurgeThumbnailJob {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(PurgeThumbnailJob.class);

    /**
     * Default constructor for PurgeThumbnailJob.
     * Creates a new instance of the thumbnail purging job with default expiry time (30 days).
     */
    public PurgeThumbnailJob() {
        // Default constructor
    }

    /** Expiration time for thumbnails in milliseconds (default: 30 days) */
    private long expiry = 30 * 24 * 60 * 60 * 1000L;

    /**
     * Executes the thumbnail purging job.
     * Removes thumbnail files that have exceeded the configured expiration time.
     *
     * @return a string containing the execution result with the number of deleted files or error message
     */
    public String execute() {
        try {
            final long count = ComponentUtil.getThumbnailManager().purge(getExpiry());
            return "Deleted " + count + " thumbnail files.";
        } catch (final Exception e) {
            logger.error("Failed to purge thumbnails.", e);
            return e.getMessage();
        }
    }

    /**
     * Gets the expiration time for thumbnails.
     *
     * @return the expiration time in milliseconds
     */
    public long getExpiry() {
        return expiry;
    }

    /**
     * Sets the expiration time for thumbnails.
     *
     * @param expiry the expiration time in milliseconds (must be positive)
     * @return this PurgeThumbnailJob instance for method chaining
     */
    public PurgeThumbnailJob expiry(final long expiry) {
        if (expiry > 0) {
            this.expiry = expiry;
        }
        return this;
    }
}
