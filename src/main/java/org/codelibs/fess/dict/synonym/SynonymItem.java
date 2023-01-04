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
package org.codelibs.fess.dict.synonym;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.collection.ArrayUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.dict.DictionaryItem;

public class SynonymItem extends DictionaryItem {
    private final String[] inputs;

    private final String[] outputs;

    private String[] newInputs;

    private String[] newOutputs;

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

    public String[] getNewInputs() {
        return newInputs;
    }

    public void setNewInputs(final String[] newInputs) {
        this.newInputs = newInputs;
    }

    public String[] getNewOutputs() {
        return newOutputs;
    }

    public void setNewOutputs(final String[] newOutputs) {
        this.newOutputs = newOutputs;
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

    public String[] getOutputs() {
        return outputs;
    }

    public String getOutputsValue() {
        if (outputs == null) {
            return StringUtil.EMPTY;
        }
        return String.join("\n", outputs);
    }

    public boolean isUpdated() {
        return newInputs != null && newOutputs != null;
    }

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
        if ((obj == null) || (getClass() != obj.getClass())) {
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
