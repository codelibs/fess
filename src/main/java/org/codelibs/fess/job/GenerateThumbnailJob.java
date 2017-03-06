/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateThumbnailJob {
    private static final Logger logger = LoggerFactory.getLogger(GenerateThumbnailJob.class);

    public String execute() {
        int totalCount = 0;
        int count = 1;
        try {
            while (count != 0) {
                count = ComponentUtil.getThumbnailManager().generate();
                totalCount += count;
            }
            return "Created " + totalCount + " thumbnail files.";
        } catch (final Exception e) {
            logger.error("Failed to purge user info.", e);
            return e.getMessage();
        }
    }

}
