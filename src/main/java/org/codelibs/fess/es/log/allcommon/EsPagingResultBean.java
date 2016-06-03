/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.log.allcommon;

import org.dbflute.cbean.result.PagingResultBean;

/**
 * @param <ENTITY> The type of entity.
 * @author ESFlute (using FreeGen)
 */
public class EsPagingResultBean<ENTITY> extends PagingResultBean<ENTITY> {

    private static final long serialVersionUID = 1L;

    protected long took;
    private int totalShards;
    private int successfulShards;
    private int failedShards;

    public long getTook() {
        return took;
    }

    public void setTook(long took) {
        this.took = took;
    }

    public int getTotalShards() {
        return totalShards;
    }

    public void setTotalShards(int totalShards) {
        this.totalShards = totalShards;
    }

    public int getSuccessfulShards() {
        return successfulShards;
    }

    public void setSuccessfulShards(int successfulShards) {
        this.successfulShards = successfulShards;
    }

    public int getFailedShards() {
        return failedShards;
    }

    public void setFailedShards(int failedShards) {
        this.failedShards = failedShards;
    }
}
