/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.user.exentity;

import java.util.Base64;

import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.es.user.bsentity.BsUser;
import org.codelibs.fess.util.StreamUtil;

/**
 * @author FreeGen
 */
public class User extends BsUser implements FessUser {

    private static final long serialVersionUID = 1L;

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public String[] getRoleNames() {
        return StreamUtil.of(getRoles()).map(role -> new String(Base64.getDecoder().decode(role), Constants.CHARSET_UTF_8))
                .toArray(n -> new String[n]);
    }

    public String[] getGroupNames() {
        return StreamUtil.of(getGroups()).map(group -> new String(Base64.getDecoder().decode(group), Constants.CHARSET_UTF_8))
                .toArray(n -> new String[n]);
    }

}
