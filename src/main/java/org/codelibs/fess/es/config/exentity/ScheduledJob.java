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
package org.codelibs.fess.es.config.exentity;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.config.bsentity.BsScheduledJob;
import org.codelibs.fess.exception.JobNotFoundException;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.job.LaScheduledJob;
import org.lastaflute.job.key.LaJobUnique;

/**
 * @author FreeGen
 */
public class ScheduledJob extends BsScheduledJob {

    private static final long serialVersionUID = 1L;

    @Override
    public String getScriptType() {
        final String st = super.getScriptType();
        if (StringUtil.isBlank(st)) {
            return "groovy";
        }
        return st;
    }

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
        return ComponentUtil.getJobManager().findJobByUniqueOf(LaJobUnique.of(getId())).map(LaScheduledJob::isExecutingNow).orElse(false);
    }

    public void start() {
        ComponentUtil.getJobManager().findJobByUniqueOf(LaJobUnique.of(getId())).ifPresent(job -> {
            job.launchNow();
        }).orElse(() -> {
            throw new JobNotFoundException(this);
        });
    }

    public void stop() {
        ComponentUtil.getJobManager().findJobByUniqueOf(LaJobUnique.of(getId())).ifPresent(job -> {
            job.stopNow();
        }).orElse(() -> {
            throw new JobNotFoundException(this);
        });
    }

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    @Override
    public String toString() {
        return "ScheduledJob [available=" + available + ", crawler=" + crawler + ", createdBy=" + createdBy + ", createdTime=" + createdTime
                + ", cronExpression=" + cronExpression + ", jobLogging=" + jobLogging + ", name=" + name + ", scriptData=" + scriptData
                + ", scriptType=" + scriptType + ", sortOrder=" + sortOrder + ", target=" + target + ", updatedBy=" + updatedBy
                + ", updatedTime=" + updatedTime + ", docMeta=" + docMeta + "]";
    }
}
