/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.scheduler;

import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.validation.CronExpression;
import org.codelibs.fess.validation.CustomSize;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * The create form for Scheduler.
 *
 * @author shinsuke
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
    }

    /**
     * The CRUD mode for the form.
     */
    @ValidateTypeFailure
    public Integer crudMode;

    /**
     * The name of the scheduled job.
     */
    @Required
    @Size(max = 100)
    public String name;

    /**
     * The target class for the scheduled job.
     */
    @Required
    @Size(max = 100)
    public String target;

    /**
     * The cron expression defining when the job should run.
     */
    @Size(max = 100)
    @CronExpression
    public String cronExpression;

    /**
     * The type of script for the scheduled job.
     */
    @Required
    @Size(max = 100)
    public String scriptType;

    /**
     * The script data or code for the scheduled job.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String scriptData;

    /**
     * Whether this job is related to crawling.
     */
    public String crawler;

    /**
     * Whether job logging is enabled.
     */
    public String jobLogging;

    /**
     * Whether the scheduled job is available/enabled.
     */
    public String available;

    /**
     * The sort order for displaying this scheduled job.
     */
    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer sortOrder;

    /**
     * The username of who created this scheduled job.
     */
    @Size(max = 1000)
    public String createdBy;

    /**
     * The timestamp when this scheduled job was created.
     */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creating a new scheduled job.
     */
    public void initialize() {
        target = Constants.DEFAULT_JOB_TARGET;
        cronExpression = Constants.DEFAULT_CRON_EXPRESSION;
        scriptType = Constants.DEFAULT_JOB_SCRIPT_TYPE;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();

    }
}
