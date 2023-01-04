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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class DocList extends ArrayList<Map<String, Object>> {

    private static final long serialVersionUID = 1L;

    private long contentSize = 0;

    private long processingTime = 0;

    @Override
    public void clear() {
        super.clear();
        contentSize = 0;
        processingTime = 0;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void addContentSize(final long contentSize) {
        this.contentSize += contentSize;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void addProcessingTime(final long processingTime) {
        this.processingTime += processingTime;
    }

    @Override
    public String toString() {
        return "DocList [contentSize=" + contentSize + ", processingTime=" + processingTime + ", elementData="
                + Arrays.toString(toArray(new Map[size()])) + "]";
    }

}
