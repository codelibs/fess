/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.config.exentity;

import org.codelibs.fess.Constants;
import org.codelibs.fess.es.config.bsentity.BsScheduledJob;
import org.codelibs.fess.job.TriggeredJob;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class ScheduledJob extends BsScheduledJob {

    private static final long serialVersionUID = 1L;

    public boolean isLoggingEnabled() {
        return Constants.T.equals(getJobLogging());
    }

    public boolean isCrawlerJob() {
        return Constants.T.equals(getCrawler());
    }

    public boolean isEnabled() {
        return Constants.T.equals(getAvailable());
    }

    public boolean isRunning() {
        return ComponentUtil.getJobHelper().getJobExecutoer(getId()) != null;
    }

    public void start() {
        final ScheduledJob scheduledJob = this;
        new Thread(() -> new TriggeredJob().execute(scheduledJob)).start();
    }

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }
}
