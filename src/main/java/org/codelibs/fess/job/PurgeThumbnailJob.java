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
package org.codelibs.fess.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.util.ComponentUtil;

public class PurgeThumbnailJob {
    private static final Logger logger = LogManager.getLogger(PurgeThumbnailJob.class);

    private long expiry = 30 * 24 * 60 * 60 * 1000L;

    public String execute() {
        try {
            final long count = ComponentUtil.getThumbnailManager().purge(getExpiry());
            return "Deleted " + count + " thumbnail files.";
        } catch (final Exception e) {
            logger.error("Failed to purge thumbnails.", e);
            return e.getMessage();
        }
    }

    public long getExpiry() {
        return expiry;
    }

    public PurgeThumbnailJob expiry(final long expiry) {
        if (expiry > 0) {
            this.expiry = expiry;
        }
        return this;
    }
}
