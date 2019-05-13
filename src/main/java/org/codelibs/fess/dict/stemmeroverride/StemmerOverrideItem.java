/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.dict.DictionaryItem;

public class StemmerOverrideItem extends DictionaryItem {
    private final String input;

    private final String output;

    private String newInput;

    private String newOutput;

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

    public String getNewInput() {
        return newInput;
    }

    public void setNewInput(final String newInput) {
        this.newInput = newInput;
    }

    public String getNewOutput() {
        return newOutput;
    }

    public void setNewOutput(final String newOutputs) {
        this.newOutput = newOutputs;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public boolean isUpdated() {
        return newInput != null && newOutput != null;
    }

    public boolean isDeleted() {
        return isUpdated() && StringUtil.isBlank(newInput);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((input == null) ? 0 : input.hashCode());
        result = prime * result + ((output == null) ? 0 : output.hashCode());
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
        final StemmerOverrideItem other = (StemmerOverrideItem) obj;
        if (input == null) {
            if (other.input != null) {
                return false;
            }
        } else if (!input.equals(other.input)) {
            return false;
        }
        if (output == null) {
            if (other.output != null) {
                return false;
            }
        } else if (!output.equals(other.output)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StemmerOverrideItem [input=" + input + ", output=" + output + ", newInput=" + newInput + ", newOutput=" + newOutput + "]";
    }

    public String toLineString() {
        if (isUpdated()) {
            return newInput + "=>" + newOutput;
        } else {
            return input + "=>" + output;
        }
    }

}
