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
package org.codelibs.fess.es.config.exentity;

import java.util.Arrays;
import java.util.Date;

import org.codelibs.fess.es.config.bsentity.BsAccessToken;

/**
 * @author ESFlute (using FreeGen)
 */
public class AccessToken extends BsAccessToken {

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

    public Date getExpires() {
        if (getExpiredTime() == null) {
            return null;
        }
        return new Date(getExpiredTime().longValue());
    }

    public void setExpires(final Date date) {
        setExpiredTime(date != null ? date.getTime() : null);
    }

    @Override
    public String toString() {
        return "AccessToken [name=" + name + ", token=" + token + ", permissions=" + Arrays.toString(permissions) + ", parameterName="
                + parameterName + ", createdBy=" + createdBy + ", createdTime=" + createdTime + ", updatedBy=" + updatedBy
                + ", updatedTime=" + updatedTime + ", docMeta=" + docMeta + "]";
    }

}
