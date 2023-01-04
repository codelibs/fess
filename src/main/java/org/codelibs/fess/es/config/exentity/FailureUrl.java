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
package org.codelibs.fess.es.config.exentity;

import org.codelibs.fess.es.config.bsentity.BsFailureUrl;

/**
 * @author FreeGen
 */
public class FailureUrl extends BsFailureUrl {

    private static final long serialVersionUID = 1L;

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    @Override
    public String toString() {
        return "FailureUrl [configId=" + configId + ", errorCount=" + errorCount + ", errorLog=" + errorLog + ", errorName=" + errorName
                + ", lastAccessTime=" + lastAccessTime + ", threadName=" + threadName + ", url=" + url + ", docMeta=" + docMeta + "]";
    }

}
