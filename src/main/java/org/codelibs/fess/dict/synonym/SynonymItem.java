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
package org.codelibs.fess.dict.synonym;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.collection.ArrayUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.dict.DictionaryItem;

/**
 * Represents an item in a synonym dictionary.
 * This class stores a set of input words and their corresponding output synonyms.
 * It also tracks updated values for the item.
 */
public class SynonymItem extends DictionaryItem {
    /** The original input words. */
    private final String[] inputs;

    /** The original output synonyms. */
    private final String[] outputs;

    /** The new input words, if updated. */
    private String[] newInputs;

    /** The new output synonyms, if updated. */
    private String[] newOutputs;

    /**
     * Constructs a new synonym item.
     *
     * @param id      The unique identifier of the item.
     * @param inputs  The input words.
     * @param outputs The output synonyms.
     */
    public SynonymItem(final long id, final String[] inputs, final String[] outputs) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;

        if (id == 0) {
            // create
            newInputs = inputs;
            newOutputs = outputs;
        }
    }

    /**
     * Gets the new input words.
     *
     * @return The new input words.
     */
    public String[] getNewInputs() {
        return newInputs;
    }

    /**
     * Sets the new input words.
     *
     * @param newInputs The new input words.
     */
    public void setNewInputs(final String[] newInputs) {
        this.newInputs = newInputs;
    }

    /**
     * Gets the new output synonyms.
     *
     * @return The new output synonyms.
     */
    public String[] getNewOutputs() {
        return newOutputs;
    }

    /**
     * Sets the new output synonyms.
     *
     * @param newOutputs The new output synonyms.
     */
    public void setNewOutputs(final String[] newOutputs) {
        this.newOutputs = newOutputs;
    }

    /**
     * Gets the original input words.
     *
     * @return The original input words.
     */
    public String[] getInputs() {
        return inputs;
    }

    /**
     * Gets the input words as a newline-separated string.
     *
     * @return The input words as a string.
     */
    public String getInputsValue() {
        if (inputs == null) {
            return StringUtil.EMPTY;
        }
        return String.join("\n", inputs);
    }

    /**
     * Gets the original output synonyms.
     *
     * @return The original output synonyms.
     */
    public String[] getOutputs() {
        return outputs;
    }

    /**
     * Gets the output synonyms as a newline-separated string.
     *
     * @return The output synonyms as a string.
     */
    public String getOutputsValue() {
        if (outputs == null) {
            return StringUtil.EMPTY;
        }
        return String.join("\n", outputs);
    }

    /**
     * Checks if the item has been updated.
     *
     * @return true if the item has been updated, false otherwise.
     */
    public boolean isUpdated() {
        return newInputs != null && newOutputs != null;
    }

    /**
     * Checks if the item has been marked for deletion.
     *
     * @return true if the item is marked for deletion, false otherwise.
     */
    public boolean isDeleted() {
        return isUpdated() && newInputs.length == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(inputs), Arrays.hashCode(outputs));
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SynonymItem other = (SynonymItem) obj;
        if (!ArrayUtil.equalsIgnoreSequence(inputs, other.inputs) || !ArrayUtil.equalsIgnoreSequence(outputs, other.outputs)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SynonymItem [id=" + id + ", inputs=" + Arrays.toString(inputs) + ", outputs=" + Arrays.toString(outputs) + ", newInputs="
                + Arrays.toString(newInputs) + ", newOutputs=" + Arrays.toString(newOutputs) + "]";
    }

    /**
     * Converts the item to a string representation for writing to a file.
     *
     * @return A string in the format "input1,input2=>output1,output2".
     */
    public String toLineString() {
        if (isUpdated()) {
            if (Arrays.equals(newInputs, newOutputs)) {
                return StringUtils.join(newInputs, ",");
            }
            return StringUtils.join(newInputs, ",") + "=>" + StringUtils.join(newOutputs, ",");
        }
        if (Arrays.equals(inputs, outputs)) {
            return StringUtils.join(inputs, ",");
        }
        return StringUtils.join(inputs, ",") + "=>" + StringUtils.join(outputs, ",");
    }

}