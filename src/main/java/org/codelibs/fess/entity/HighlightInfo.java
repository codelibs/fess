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
package org.codelibs.fess.entity;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class HighlightInfo {
    private String type;
    private int fragmentSize;
    private int numOfFragments;

    public HighlightInfo() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        this.type = fessConfig.getQueryHighlightType();
        this.fragmentSize = fessConfig.getQueryHighlightFragmentSizeAsInteger();
        this.numOfFragments = fessConfig.getQueryHighlightNumberOfFragmentsAsInteger();
    }

    public String getType() {
        return type;
    }

    public HighlightInfo type(String type) {
        this.type = type;
        return this;
    }

    public int getFragmentSize() {
        return fragmentSize;
    }

    public HighlightInfo fragmentSize(int fragmentSize) {
        this.fragmentSize = fragmentSize;
        return this;
    }

    public int getNumOfFragments() {
        return numOfFragments;
    }

    public HighlightInfo numOfFragments(int numOfFragments) {
        this.numOfFragments = numOfFragments;
        return this;
    }
}
