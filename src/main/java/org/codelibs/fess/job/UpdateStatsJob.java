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

package org.codelibs.fess.job;

import org.codelibs.fess.service.SearchFieldLogService;
import org.codelibs.fess.util.ComponentUtil;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateStatsJob {

    private static final Logger logger = LoggerFactory
            .getLogger(UpdateStatsJob.class);

    public String execute() {
        final SearchFieldLogService searchFieldLogService = SingletonS2Container
                .getComponent(SearchFieldLogService.class);

        final StringBuilder resultBuf = new StringBuilder();

        try {
            // update stats fields
            searchFieldLogService.updateFieldLabels();
        } catch (final Exception e) {
            logger.error("Failed to execute the hourly task.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        try {
            ComponentUtil.getKeyMatchHelper().update();
        } catch (final Exception e) {
            logger.error("Failed to execute the hourly task.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

}
