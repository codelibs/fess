/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.dict.seunjeon;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.dict.DictionaryItem;

public class SeunjeonItem extends DictionaryItem {
    private final String[] inputs;

    private String[] newInputs;

    public SeunjeonItem(final long id, final String[] inputs) {
        this.id = id;
        this.inputs = inputs;

        if (id == 0) {
            // create
            newInputs = inputs;
        }
    }

    public String[] getNewInputs() {
        return newInputs;
    }

    public void setNewInputs(final String[] newInputs) {
        this.newInputs = newInputs;
    }

    public String[] getInputs() {
        return inputs;
    }

    public String getInputsValue() {
        if (inputs == null) {
            return StringUtil.EMPTY;
        }
        return String.join(",", inputs);
    }

    public boolean isUpdated() {
        return newInputs != null;
    }

    public boolean isDeleted() {
        return isUpdated() && newInputs.length == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(inputs);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SeunjeonItem other = (SeunjeonItem) obj;
        if (!Arrays.equals(inputs, other.inputs)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SynonymItem [id=" + id + ", inputs=" + Arrays.toString(inputs) + ", newInputs=" + Arrays.toString(newInputs) + "]";
    }

    public String toLineString() {
        if (isUpdated()) {
            return StringUtils.join(newInputs, ",");
        } else {
            return StringUtils.join(inputs, ",");
        }
    }

}
