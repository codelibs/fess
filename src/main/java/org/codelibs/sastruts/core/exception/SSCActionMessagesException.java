/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.codelibs.sastruts.core.exception;

import org.seasar.struts.exception.ActionMessagesException;

/**
 * SSCActionMessagesException allows you to pass a root cause.
 * 
 * @author shinsuke
 *
 */
public class SSCActionMessagesException extends ActionMessagesException {

    /**
     * A default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    public SSCActionMessagesException(final String key, final boolean resource) {
        super(key, resource);
    }

    public SSCActionMessagesException(final String key, final Object... values) {
        super(key, values);
    }

    public SSCActionMessagesException(final Throwable cause, final String key, final boolean resource) {
        super(key, resource);
        initCause(cause);
    }

    public SSCActionMessagesException(final Throwable cause, final String key, final Object... values) {
        super(key, values);
        initCause(cause);
    }

}
