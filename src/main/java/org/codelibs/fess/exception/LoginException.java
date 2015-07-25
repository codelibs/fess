/*
 * Copyright 2012 the CodeLibs Project and the Others.
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
 * @author shinsuke
 * 
 */
public class LoginException extends SSCRuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public LoginException(final String messageCode, final Object[] args, final Throwable cause) {
        super(messageCode, args, cause);
    }

    public LoginException(final String messageCode, final Object[] args) {
        super(messageCode, args);
    }

    public LoginException(final String messageCode, final Throwable cause) {
        super(messageCode, cause);
    }

    public LoginException(final String messageCode) {
        super(messageCode);
    }

}
