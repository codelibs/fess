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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Entity class containing highlighting configuration for search result text highlighting.
 * This class manages highlighting parameters such as fragment size, number of fragments,
 * and highlighting type for displaying search query matches in result snippets.
 */
public class HighlightInfo {
    /** The highlighting type (e.g., plain, html). */
    private String type;
    /** The size of each highlighted fragment in characters. */
    private int fragmentSize;
    /** The maximum number of highlighted fragments to return. */
    private int numOfFragments;
    /** The offset for fragment positioning. */
    private int fragmentOffset;

    /**
     * Default constructor that initializes highlighting settings from Fess configuration.
     * Loads default values for type, fragment size, number of fragments, and fragment offset.
     */
    public HighlightInfo() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        type = fessConfig.getQueryHighlightType();
        fragmentSize = fessConfig.getQueryHighlightFragmentSizeAsInteger();
        numOfFragments = fessConfig.getQueryHighlightNumberOfFragmentsAsInteger();
        fragmentOffset = fessConfig.getQueryHighlightFragmentOffsetAsInteger();
    }

    /**
     * Gets the highlighting type.
     *
     * @return the highlighting type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the highlighting type with fluent interface.
     *
     * @param type the highlighting type to set
     * @return this HighlightInfo instance for method chaining
     */
    public HighlightInfo type(final String type) {
        this.type = type;
        return this;
    }

    /**
     * Gets the fragment size.
     *
     * @return the fragment size in characters
     */
    public int getFragmentSize() {
        return fragmentSize;
    }

    /**
     * Sets the fragment size with fluent interface.
     *
     * @param fragmentSize the fragment size in characters
     * @return this HighlightInfo instance for method chaining
     */
    public HighlightInfo fragmentSize(final int fragmentSize) {
        this.fragmentSize = fragmentSize;
        return this;
    }

    /**
     * Gets the number of fragments.
     *
     * @return the maximum number of highlighted fragments
     */
    public int getNumOfFragments() {
        return numOfFragments;
    }

    /**
     * Sets the number of fragments with fluent interface.
     *
     * @param numOfFragments the maximum number of highlighted fragments
     * @return this HighlightInfo instance for method chaining
     */
    public HighlightInfo numOfFragments(final int numOfFragments) {
        this.numOfFragments = numOfFragments;
        return this;
    }

    /**
     * Gets the fragment offset.
     *
     * @return the fragment offset value
     */
    public int getFragmentOffset() {
        return fragmentOffset;
    }

    /**
     * Sets the fragment offset with fluent interface.
     *
     * @param fragmentOffset the fragment offset value
     * @return this HighlightInfo instance for method chaining
     */
    public HighlightInfo fragmentOffset(final int fragmentOffset) {
        this.fragmentOffset = fragmentOffset;
        return this;
    }
}
