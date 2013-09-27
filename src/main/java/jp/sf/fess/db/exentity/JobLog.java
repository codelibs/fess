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

package jp.sf.fess.db.exentity;

import java.sql.Timestamp;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsJobLog;

/**
 * The entity of JOB_LOG.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class JobLog extends BsJobLog {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    private ScheduledJob scheduledJob;

    public JobLog() {
    }

    public JobLog(final ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
        setJobName(scheduledJob.getName());
        setScriptType(scheduledJob.getScriptType());
        setScriptData(scheduledJob.getScriptData());
        setTarget(scheduledJob.getTarget());
        setStartTime(new Timestamp(System.currentTimeMillis()));
        setJobStatus(Constants.RUNNING);
    }

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }
}
