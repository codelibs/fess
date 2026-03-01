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
package org.codelibs.fess.llm;

/**
 * Represents the intent type detected from a user's chat message.
 */
public enum ChatIntent {

    /** User wants to search for documents in Fess */
    SEARCH("search"),

    /** User wants a summary of a specific document */
    SUMMARY("summary"),

    /** User is asking a FAQ-type question */
    FAQ("faq"),

    /** Intent is unclear - need to ask user for clarification */
    UNCLEAR("unclear");

    private final String value;

    ChatIntent(final String value) {
        this.value = value;
    }

    /**
     * Returns the string value of this intent.
     *
     * @return the intent value
     */
    public String getValue() {
        return value;
    }

    /**
     * Parses a string value to ChatIntent enum.
     *
     * @param value the string value to parse
     * @return the corresponding ChatIntent, defaults to UNCLEAR if not found
     */
    public static ChatIntent fromValue(final String value) {
        if (value == null) {
            return UNCLEAR;
        }
        for (final ChatIntent intent : values()) {
            if (intent.value.equalsIgnoreCase(value.trim())) {
                return intent;
            }
        }
        return UNCLEAR;
    }
}
