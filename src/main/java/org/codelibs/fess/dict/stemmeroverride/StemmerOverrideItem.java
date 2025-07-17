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
package org.codelibs.fess.dict.stemmeroverride;

import java.util.Objects;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.dict.DictionaryItem;

/**
 * Represents an item in a stemmer override dictionary.
 * This class stores a mapping from an input word to its corresponding
 * output stem. It also tracks updated values for the item.
 */
public class StemmerOverrideItem extends DictionaryItem {
    /** The original input word. */
    private final String input;

    /** The original output stem. */
    private final String output;

    /** The new input word, if updated. */
    private String newInput;

    /** The new output stem, if updated. */
    private String newOutput;

    /**
     * Constructs a new stemmer override item.
     *
     * @param id     The unique identifier of the item.
     * @param input  The input word.
     * @param output The output stem.
     */
    public StemmerOverrideItem(final long id, final String input, final String output) {
        this.id = id;
        this.input = input;
        this.output = output;

        if (id == 0) {
            // create
            newInput = input;
            newOutput = output;
        }
    }

    /**
     * Gets the new input word.
     *
     * @return The new input word.
     */
    public String getNewInput() {
        return newInput;
    }

    /**
     * Sets the new input word.
     *
     * @param newInput The new input word.
     */
    public void setNewInput(final String newInput) {
        this.newInput = newInput;
    }

    /**
     * Gets the new output stem.
     *
     * @return The new output stem.
     */
    public String getNewOutput() {
        return newOutput;
    }

    /**
     * Sets the new output stem.
     *
     * @param newOutputs The new output stem.
     */
    public void setNewOutput(final String newOutputs) {
        newOutput = newOutputs;
    }

    /**
     * Gets the original input word.
     *
     * @return The original input word.
     */
    public String getInput() {
        return input;
    }

    /**
     * Gets the original output stem.
     *
     * @return The original output stem.
     */
    public String getOutput() {
        return output;
    }

    /**
     * Checks if the item has been updated.
     *
     * @return true if the item has been updated, false otherwise.
     */
    public boolean isUpdated() {
        return newInput != null && newOutput != null;
    }

    /**
     * Checks if the item has been marked for deletion.
     *
     * @return true if the item is marked for deletion, false otherwise.
     */
    public boolean isDeleted() {
        return isUpdated() && StringUtil.isBlank(newInput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final StemmerOverrideItem other = (StemmerOverrideItem) obj;
        if (!Objects.equals(input, other.input) || !Objects.equals(output, other.output)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StemmerOverrideItem [input=" + input + ", output=" + output + ", newInput=" + newInput + ", newOutput=" + newOutput + "]";
    }

    /**
     * Converts the item to a string representation for writing to a file.
     *
     * @return A string in the format "input=>output".
     */
    public String toLineString() {
        if (isUpdated()) {
            return newInput + "=>" + newOutput;
        }
        return input + "=>" + output;
    }

}