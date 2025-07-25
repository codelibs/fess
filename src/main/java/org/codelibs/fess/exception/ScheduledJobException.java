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

/**
 * This exception is thrown when a scheduled job fails.
 */
public class ScheduledJobException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param message Exception message.
     * @param cause Root cause for this exception.
     */
    public ScheduledJobException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * @param message Exception message.
     */
    public ScheduledJobException(final String message) {
        super(message);
    }

}
