/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.config.exbhv;

import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomUtils;
import org.codelibs.fess.es.config.bsbhv.BsScheduledJobBhv;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FreeGen
 */
public class ScheduledJobBhv extends BsScheduledJobBhv {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJobBhv.class);

    private String indexName = null;

    @Override
    protected String asEsIndex() {
        if (indexName == null) {
            final String name = ComponentUtil.getFessConfig().getIndexConfigIndex();
            indexName = super.asEsIndex().replaceFirst(Pattern.quote(".fess_config"), name);
        }
        return indexName;
    }

    @Override
    public OptionalEntity<ScheduledJob> selectByPK(final String id) {
        Exception lastException = null;
        for (int i = 0; i < 30; i++) {
            try {
                return super.selectByPK(id);
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to select a job by " + id, e);
                }
                lastException = e;
                try {
                    Thread.sleep(RandomUtils.nextLong(500, 5000));
                } catch (final InterruptedException e1) {
                    // ignore
                }
            }
        }
        logger.warn("Failed to select a job by " + id, lastException);
        return OptionalEntity.empty();
    }
}
