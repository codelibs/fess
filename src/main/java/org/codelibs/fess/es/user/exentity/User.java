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
package org.codelibs.fess.es.user.exentity;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.es.user.bsentity.BsUser;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;

/**
 * @author FreeGen
 */
public class User extends BsUser implements FessUser {

    private static final long serialVersionUID = 1L;

    private String originalPassword;

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

    @Override
    public String[] getRoleNames() {
        return getRoleStream().toArray(n -> new String[n]);
    }

    protected Stream<String> getRoleStream() {
        return StreamUtil.of(getRoles()).map(role -> new String(Base64.getDecoder().decode(role), Constants.CHARSET_UTF_8));
    }

    @Override
    public String[] getGroupNames() {
        return getGroupStream().toArray(n -> new String[n]);
    }

    private Stream<String> getGroupStream() {
        return StreamUtil.of(getGroups()).map(group -> new String(Base64.getDecoder().decode(group), Constants.CHARSET_UTF_8));
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", roles=" + Arrays.toString(roles) + ", groups=" + Arrays.toString(groups) + "]";
    }

    public void setOriginalPassword(final String originalPassword) {
        this.originalPassword = originalPassword;

    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    @Override
    public String[] getPermissions() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return Stream.concat(
                Stream.of(fessConfig.getRoleSearchUserPrefix() + getName()),
                Stream.concat(getRoleStream().map(s -> fessConfig.getRoleSearchRolePrefix() + s),
                        getGroupStream().map(s -> fessConfig.getRoleSearchGroupPrefix() + s))).toArray(n -> new String[n]);
    }

}
