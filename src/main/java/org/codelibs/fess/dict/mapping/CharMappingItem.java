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
package org.codelibs.fess.dict.mapping;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.dict.DictionaryItem;

/**
 * Represents a single character mapping rule that defines how input characters are mapped to output characters
 * for text analysis and search processing. This class is used in character mapping dictionaries to transform
 * text during indexing and search operations.
 *
 * <p>Each mapping item consists of one or more input character sequences that are mapped to a single output
 * character sequence. The mapping supports both original values and new values for update operations.</p>
 */
public class CharMappingItem extends DictionaryItem {
    /**
     * Array of input character sequences that will be mapped to the output sequence.
     * These represent the original/current input values for this mapping rule.
     */
    private final String[] inputs;

    /**
     * The output character sequence that input characters will be mapped to.
     * This represents the original/current output value for this mapping rule.
     */
    private final String output;

    /**
     * Array of new input character sequences for update operations.
     * When not null, indicates this item has pending updates.
     */
    private String[] newInputs;

    /**
     * The new output character sequence for update operations.
     * When not null, indicates this item has pending updates.
     */
    private String newOutput;

    /**
     * Constructs a new CharMappingItem with the specified ID, input sequences, and output sequence.
     *
     * @param id the unique identifier for this mapping item
     * @param inputs array of input character sequences that will be mapped to the output
     * @param output the output character sequence that inputs will be mapped to
     */
    public CharMappingItem(final long id, final String[] inputs, final String output) {
        this.id = id;
        this.inputs = inputs;
        this.output = output == null ? null : output.replace("\n", " ");
        Arrays.sort(inputs);

        if (id == 0) {
            // create
            newInputs = inputs;
            newOutput = output;
        }
    }

    /**
     * Returns the array of new input character sequences for update operations.
     * Returns a defensive copy to prevent external modification.
     *
     * @return array of new input sequences (defensive copy), or null if no updates are pending
     */
    public String[] getNewInputs() {
        return newInputs == null ? null : newInputs.clone();
    }

    /**
     * Sets the array of new input character sequences for update operations.
     *
     * @param newInputs array of new input sequences to set
     */
    public void setNewInputs(final String[] newInputs) {
        this.newInputs = newInputs;
    }

    /**
     * Returns the new output character sequence for update operations.
     *
     * @return the new output sequence, or null if no updates are pending
     */
    public String getNewOutput() {
        return newOutput;
    }

    /**
     * Sets the new output character sequence for update operations.
     * Newline characters in the output are automatically replaced with spaces.
     *
     * @param newOutput the new output sequence to set
     */
    public void setNewOutput(final String newOutput) {
        this.newOutput = newOutput == null ? null : newOutput.replace("\n", " ");
    }

    /**
     * Returns the array of input character sequences that are mapped to the output.
     * Returns a defensive copy to prevent external modification.
     *
     * @return array of input sequences (defensive copy)
     */
    public String[] getInputs() {
        return inputs == null ? null : inputs.clone();
    }

    /**
     * Returns all input sequences joined with newline characters as a single string.
     * This is useful for display purposes in forms and user interfaces.
     *
     * @return string representation of all inputs separated by newlines, or empty string if inputs is null
     */
    public String getInputsValue() {
        if (inputs == null) {
            return StringUtil.EMPTY;
        }
        return String.join("\n", inputs);
    }

    /**
     * Returns the output character sequence that inputs are mapped to.
     *
     * @return the output sequence
     */
    public String getOutput() {
        return output;
    }

    /**
     * Checks whether this mapping item has pending updates.
     *
     * @return true if both newInputs and newOutput are not null, indicating pending updates
     */
    public boolean isUpdated() {
        return newInputs != null && newOutput != null;
    }

    /**
     * Checks whether this mapping item is marked for deletion.
     * An item is considered deleted if it has updates pending and the new inputs array is empty.
     *
     * @return true if the item is marked for deletion
     */
    public boolean isDeleted() {
        return isUpdated() && newInputs.length == 0;
    }

    /**
     * Sorts both the current inputs and new inputs arrays in place.
     * This ensures consistent ordering for comparison and equality operations.
     */
    public void sort() {
        if (inputs != null) {
            Arrays.sort(inputs);
        }
        if (newInputs != null) {
            Arrays.sort(newInputs);
        }
    }

    /**
     * Calculates the hash code for this CharMappingItem based on inputs and output.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(inputs), output);
    }

    /**
     * Compares this CharMappingItem with another object for equality.
     * Two CharMappingItem objects are equal if they have the same inputs and output.
     * Note: inputs arrays are sorted in the constructor, so no sorting is needed here.
     *
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CharMappingItem other = (CharMappingItem) obj;
        if (!Arrays.equals(inputs, other.inputs)) {
            return false;
        }
        return Objects.equals(output, other.output);
    }

    /**
     * Returns a string representation of this CharMappingItem including all fields.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "MappingItem [id=" + id + ", inputs=" + Arrays.toString(inputs) + ", output=" + output + ", newInputs="
                + Arrays.toString(newInputs) + ", newOutput=" + newOutput + "]";
    }

    /**
     * Returns a compact string representation of this mapping item in the format "input1,input2=>output".
     * If the item has pending updates, the new values are used; otherwise, the current values are used.
     *
     * @return compact string representation of the mapping rule
     */
    public String toLineString() {
        if (isUpdated()) {
            return StringUtils.join(newInputs, ",") + "=>" + newOutput;
        }
        return StringUtils.join(inputs, ",") + "=>" + output;
    }

}
