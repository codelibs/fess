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
package org.codelibs.fess.entity;

import java.util.Arrays;

/**
 * Entity class representing a request parameter with a name and associated values.
 * This class encapsulates HTTP request parameters that can have multiple values,
 * such as query parameters, form parameters, or other request-related data.
 *
 * <p>This class is immutable and thread-safe. Once created, the parameter name
 * and values cannot be modified.</p>
 *
 */
public class RequestParameter {

    /** The name of the request parameter. */
    private final String name;

    /** The array of values associated with this parameter. */
    private final String[] values;

    /**
     * Constructs a new RequestParameter with the specified name and values.
     *
     * @param name the name of the parameter, must not be null
     * @param values the array of values for this parameter, can be null or empty
     */
    public RequestParameter(final String name, final String[] values) {
        this.name = name;
        this.values = values;
    }

    /**
     * Returns the name of this request parameter.
     *
     * @return the parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the array of values associated with this request parameter.
     *
     * @return the parameter values array, may be null or empty
     */
    public String[] getValues() {
        return values;
    }

    /**
     * Returns a string representation of this RequestParameter.
     * The format includes the parameter name and its values in array format.
     *
     * @return a string representation of this object in the format "[name, [value1, value2, ...]]"
     */
    @Override
    public String toString() {
        return "[" + name + ", " + Arrays.toString(values) + "]";
    }
}
