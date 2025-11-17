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
package org.codelibs.fess.dict.protwords;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.dict.DictionaryItem;

/**
 * Dictionary item for protected words.
 * This class represents a single protected word entry in the dictionary.
 */
public class ProtwordsItem extends DictionaryItem {
    private final String input;

    private String newInput;

    /**
     * Constructor for ProtwordsItem.
     * @param id the unique identifier for this item
     * @param input the protected word input
     */
    public ProtwordsItem(final long id, final String input) {
        this.id = id;
        this.input = input;

        if (id == 0) {
            // create
            newInput = input;
        }
    }

    /**
     * Gets the new input value for this item.
     * @return the new input value
     */
    public String getNewInput() {
        return newInput;
    }

    /**
     * Sets the new input value for this item.
     * @param newInput the new input value
     */
    public void setNewInput(final String newInput) {
        this.newInput = newInput;
    }

    /**
     * Gets the input value for this item.
     * @return the input value
     */
    public String getInput() {
        return input;
    }

    /**
     * Gets the input value or empty string if null.
     * @return the input value or empty string
     */
    public String getInputValue() {
        if (input == null) {
            return StringUtil.EMPTY;
        }
        return input;
    }

    /**
     * Checks if this item has been updated.
     * @return true if updated, false otherwise
     */
    public boolean isUpdated() {
        return newInput != null;
    }

    /**
     * Checks if this item has been deleted.
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return isUpdated() && newInput.length() == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(input);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ProtwordsItem other = (ProtwordsItem) obj;
        return java.util.Objects.equals(input, other.input);
    }

    @Override
    public String toString() {
        return "ProtwordsItem [id=" + id + ", inputs=" + input + ", newInputs=" + newInput + "]";
    }

    /**
     * Converts this item to a string representation for writing to file.
     * @return the string representation of this item
     */
    public String toLineString() {
        if (isUpdated()) {
            return StringUtils.join(newInput);
        }
        return StringUtils.join(input);
    }

}
