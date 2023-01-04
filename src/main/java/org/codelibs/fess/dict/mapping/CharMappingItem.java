/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class CharMappingItem extends DictionaryItem {
    private final String[] inputs;

    private final String output;

    private String[] newInputs;

    private String newOutput;

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

    public String[] getNewInputs() {
        return newInputs;
    }

    public void setNewInputs(final String[] newInputs) {
        this.newInputs = newInputs;
    }

    public String getNewOutput() {
        return newOutput;
    }

    public void setNewOutput(final String newOutput) {
        this.newOutput = newOutput == null ? null : newOutput.replace("\n", " ");
    }

    public String[] getInputs() {
        return inputs;
    }

    public String getInputsValue() {
        if (inputs == null) {
            return StringUtil.EMPTY;
        }
        return String.join("\n", inputs);
    }

    public String getOutput() {
        return output;
    }

    public boolean isUpdated() {
        return newInputs != null && newOutput != null;
    }

    public boolean isDeleted() {
        return isUpdated() && newInputs.length == 0;
    }

    public void sort() {
        if (inputs != null) {
            Arrays.sort(inputs);
        }
        if (newInputs != null) {
            Arrays.sort(newInputs);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(inputs), output);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final CharMappingItem other = (CharMappingItem) obj;
        sort();
        other.sort();
        if (!Arrays.equals(inputs, other.inputs) || !output.equals(other.getOutput())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MappingItem [id=" + id + ", inputs=" + Arrays.toString(inputs) + ", output=" + output + ", newInputs="
                + Arrays.toString(newInputs) + ", newOutput=" + newOutput + "]";
    }

    public String toLineString() {
        if (isUpdated()) {
            return StringUtils.join(newInputs, ",") + "=>" + newOutput;
        }
        return StringUtils.join(inputs, ",") + "=>" + output;
    }

}
