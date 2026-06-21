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
package org.codelibs.fess.api.v2.handlers;

/**
 * Stateless helpers that enforce length/item-count bounds on v2 request
 * parameters, throwing {@link InvalidRequestParameterException} on violation.
 */
public final class V2ParamValidator {

    private V2ParamValidator() {
    }

    /**
     * Checks that a string parameter does not exceed the given maximum length.
     *
     * @param value the parameter value (may be {@code null})
     * @param max   the maximum allowed length (inclusive)
     * @param name  the parameter name used in the exception message
     * @return the original value, or {@code null} if the input is {@code null}
     * @throws InvalidRequestParameterException if {@code value.length() > max}
     */
    public static String checkMaxLength(final String value, final int max, final String name) {
        if (value != null && value.length() > max) {
            throw new InvalidRequestParameterException(name + " exceeds the maximum length of " + max);
        }
        return value;
    }

    /**
     * Checks that a string array parameter does not exceed the given item count
     * or per-element length.
     *
     * @param values   the parameter values (may be {@code null})
     * @param maxItems the maximum allowed number of values (inclusive)
     * @param maxLen   the maximum allowed length of each individual value (inclusive)
     * @param name     the parameter name used in the exception message
     * @return the original array, or {@code null} if the input is {@code null}
     * @throws InvalidRequestParameterException if the array size or any element length is exceeded
     */
    public static String[] checkArray(final String[] values, final int maxItems, final int maxLen, final String name) {
        if (values != null) {
            if (values.length > maxItems) {
                throw new InvalidRequestParameterException(name + " exceeds the maximum number of values: " + maxItems);
            }
            for (final String v : values) {
                checkMaxLength(v, maxLen, name);
            }
        }
        return values;
    }
}
