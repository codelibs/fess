/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.job;

import jp.sf.fess.service.SearchFieldLogService;

import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HourlyJob {

    private static final Logger logger = LoggerFactory
            .getLogger(HourlyJob.class);

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

        return resultBuf.toString();
    }

}
