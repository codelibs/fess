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
package org.codelibs.fess.helper;

import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.timer.LogNotificationTarget;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Helper that manages the lifecycle of the log notification timer,
 * which periodically flushes buffered log events to OpenSearch.
 */
public class LogNotificationHelper {

    /**
     * Default constructor.
     */
    public LogNotificationHelper() {
        // Default constructor
    }

    private TimeoutTask timeoutTask;
    private LogNotificationTarget logNotificationTarget;

    /**
     * Initializes the log notification timer.
     */
    @PostConstruct
    public void init() {
        logNotificationTarget = new LogNotificationTarget();
        timeoutTask = TimeoutManager.getInstance().addTimeoutTarget(logNotificationTarget, 30, true);
    }

    /**
     * Stops the log notification timer and performs a final flush.
     */
    @PreDestroy
    public void destroy() {
        if (timeoutTask != null) {
            timeoutTask.cancel();
        }
        if (logNotificationTarget != null) {
            logNotificationTarget.flush();
        }
    }
}
