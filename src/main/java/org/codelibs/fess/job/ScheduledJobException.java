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
package org.codelibs.fess.job;

import org.codelibs.fess.exception.FessSystemException;

public class ScheduledJobException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message Exception message.
     * @param cause Root cause for this exception.
     */
    public ScheduledJobException(final String message, final Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message Exception message.
     */
    public ScheduledJobException(final String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

}
