/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.job;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.logic.AccessContextLogic;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.es.config.exbhv.JobLogBhv;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.job.LaCron;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.LaJobRunner;
import org.lastaflute.job.LaJobScheduler;

public class AllJobScheduler implements LaJobScheduler {

    protected static final String APP_TYPE = "JOB";

    @Resource
    private TimeManager timeManager;

    @Resource
    private FessConfig fessConfig;

    @Resource
    private AccessContextLogic accessContextLogic;

    @Resource
    private ScheduledJobService scheduledJobService;

    @Resource
    private SystemHelper systemHelper;

    protected Class<? extends LaJob> jobClass = ScriptExecutorJob.class;

    @Override
    public void schedule(final LaCron cron) {
        scheduledJobService.start(cron);

        final String myName = fessConfig.getSchedulerTargetName();
        if (StringUtil.isNotBlank(myName)) {
            ComponentUtil.getComponent(JobLogBhv.class).queryDelete(cb -> {
                cb.query().setJobStatus_Equal(Constants.RUNNING);
                cb.query().setTarget_Equal(myName);
            });
        }

    }

    @Override
    public LaJobRunner createRunner() {
        return new LaJobRunner().useAccessContext(resource -> {
            return accessContextLogic.create(resource, () -> OptionalThing.empty(), () -> OptionalThing.empty(), () -> APP_TYPE);
        });
    }

}
