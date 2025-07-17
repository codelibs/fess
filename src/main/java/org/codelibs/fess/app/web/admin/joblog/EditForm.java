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
package org.codelibs.fess.app.web.admin.joblog;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * Form class for editing job log entries in the admin interface.
 * This form handles the editing of system job execution logs,
 * providing details about scheduled tasks and their execution status.
 */
public class EditForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
        // Default constructor
    }

    /**
     * The CRUD operation mode for this form.
     * Indicates whether this is a create, read, update, or delete operation.
     */
    @ValidateTypeFailure
    public int crudMode;

    /**
     * The unique identifier of the job log entry being edited.
     * This is a required field for identifying which job log to update.
     */
    @Required
    @ValidateTypeFailure
    public String id;

    /**
     * The name of the job that was executed.
     * This is a required field identifying the specific job type.
     */
    @Required
    public String jobName;

    /**
     * The execution status of the job.
     * This is a required field indicating success, failure, or running status.
     */
    @Required
    public String jobStatus;

    /**
     * The target or scope of the job execution.
     * This is a required field describing what the job operated on.
     */
    @Required
    public String target;

    /**
     * The type of script that was executed for this job.
     * This is a required field indicating the script engine or type used.
     */
    @Required
    public String scriptType;

    /**
     * The script data or code that was executed.
     * This field contains the actual script content that was run.
     */
    public String scriptData;

    /**
     * The result or output from the script execution.
     * This field contains any output or result data from the script.
     */
    public String scriptResult;

    /**
     * The timestamp when the job started execution.
     * This is a required field indicating when the job began.
     */
    @Required
    public String startTime;

    /**
     * The timestamp when the job completed execution.
     * This field indicates when the job finished, if it has completed.
     */
    public String endTime;

    /**
     * Initializes the form with default null values.
     * This method resets all fields to their default state for creating a new entry.
     */
    public void initialize() {
        id = null;
        jobName = null;
        jobStatus = null;
        target = null;
        scriptType = null;
        scriptData = null;
        scriptResult = null;
        startTime = null;
        endTime = null;
    }
}
