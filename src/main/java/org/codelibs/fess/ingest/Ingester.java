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
package org.codelibs.fess.ingest;

import java.util.Map;

import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.util.ComponentUtil;

public abstract class Ingester {

    protected int priority = 99;

    public int getPriority() {
        return priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    public void register() {
        ComponentUtil.getIngestFactory().add(this);
    }

    // web/file
    public ResultData process(final ResultData target, final ResponseData responseData) {
        return target;
    }

    // web/file
    public Map<String, Object> process(final Map<String, Object> target, final AccessResult<String> accessResult) {
        return process(target);
    }

    // datastore
    public Map<String, Object> process(final Map<String, Object> target, final DataStoreParams params) {
        return process(target);
    }

    protected Map<String, Object> process(final Map<String, Object> target) {
        return target;
    }

}
