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
package org.codelibs.fess.dict.stopwords;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.dict.DictionaryItem;

public class StopwordsItem extends DictionaryItem {
    private final String input;

    private String newInput;

    public StopwordsItem(final long id, final String input) {
        this.id = id;
        this.input = input;

        if (id == 0) {
            // create
            newInput = input;
        }
    }

    public String getNewInput() {
        return newInput;
    }

    public void setNewInput(final String newInput) {
        this.newInput = newInput;
    }

    public String getInput() {
        return input;
    }

    public String getInputValue() {
        if (input == null) {
            return StringUtil.EMPTY;
        }
        return input;
    }

    public boolean isUpdated() {
        return newInput != null;
    }

    public boolean isDeleted() {
        return isUpdated() && newInput.length() == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = 1;
        return prime * result + input.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final StopwordsItem other = (StopwordsItem) obj;
        if (!input.equals(other.input)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StopwordsItem [id=" + id + ", inputs=" + input + ", newInputs=" + newInput + "]";
    }

    public String toLineString() {
        if (isUpdated()) {
            return StringUtils.join(newInput);
        }
        return StringUtils.join(input);
    }

}
