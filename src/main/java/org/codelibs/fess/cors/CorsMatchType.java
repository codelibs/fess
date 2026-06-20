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
package org.codelibs.fess.cors;

/**
 * Describes how a request {@code Origin} matched the configured CORS allow list.
 */
public enum CorsMatchType {

    /** The Origin is an exact match of an allow-listed origin. Credentials and Origin reflection are permitted. */
    EXACT,

    /** No exact match, but {@code "*"} is configured. Literal {@code "*"} is returned and credentials are never sent. */
    WILDCARD;
}
