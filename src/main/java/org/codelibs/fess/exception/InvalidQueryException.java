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
package org.codelibs.fess.exception;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.lastaflute.web.validation.VaMessenger;

public class InvalidQueryException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    private final transient VaMessenger<FessMessages> messageCode;

    public InvalidQueryException(final VaMessenger<FessMessages> messageCode, final String message, final Throwable cause) {
        super(message, cause);
        this.messageCode = messageCode;
    }

    public InvalidQueryException(final VaMessenger<FessMessages> messageCode, final String message) {
        super(message);
        this.messageCode = messageCode;
    }

    /**
     * @return the messageCode
     */
    public VaMessenger<FessMessages> getMessageCode() {
        return messageCode;
    }

}
