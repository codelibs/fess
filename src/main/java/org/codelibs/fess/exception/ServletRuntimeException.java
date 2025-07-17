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

import jakarta.servlet.ServletException;

/**
 * Runtime exception wrapper for ServletException.
 *
 * This exception is used to wrap checked ServletExceptions and convert them
 * into unchecked RuntimeExceptions, allowing them to be thrown from methods
 * that don't declare ServletException in their throws clause.
 */
public class ServletRuntimeException extends RuntimeException {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ServletRuntimeException wrapping the given ServletException.
     *
     * @param e The ServletException to wrap
     */
    public ServletRuntimeException(final ServletException e) {
        super(e);
    }

}
