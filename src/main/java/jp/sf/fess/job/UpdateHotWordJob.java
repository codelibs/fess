/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

import jp.sf.fess.helper.HotSearchWordHelper;
import jp.sf.fess.util.ComponentUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateHotWordJob {

    private static final Logger logger = LoggerFactory
            .getLogger(UpdateHotWordJob.class);

    public String execute() {
        final HotSearchWordHelper hotSearchWordHelper = ComponentUtil
                .getHotSearchWordHelper();

        final StringBuilder resultBuf = new StringBuilder();

        // hot words
        try {
            hotSearchWordHelper.reload();
        } catch (final Exception e) {
            logger.error("Failed to store a search log.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

}
