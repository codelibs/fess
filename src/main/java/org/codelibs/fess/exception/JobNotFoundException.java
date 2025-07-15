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
package org.codelibs.fess.exception;

import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;

/**
 * Exception thrown when a scheduled job cannot be found.
 * This exception is typically thrown when attempting to access or
 * manipulate a job that does not exist in the system.
 */
public class JobNotFoundException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new JobNotFoundException with a message derived from the scheduled job.
     *
     * @param scheduledJob the scheduled job that was not found
     */
    public JobNotFoundException(final ScheduledJob scheduledJob) {
        super(scheduledJob.toString());
    }

    /**
     * Constructs a new JobNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the job was not found
     */
    public JobNotFoundException(final String message) {
        super(message);
    }

}
