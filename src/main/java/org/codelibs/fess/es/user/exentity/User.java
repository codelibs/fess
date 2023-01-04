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
package org.codelibs.fess.es.user.exentity;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.es.user.bsentity.BsUser;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class User extends BsUser implements FessUser {

    private static final long serialVersionUID = 1L;

    private String originalPassword;

    private Map<String, String> attributes;

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
        return stream(getRoles()).get(stream -> stream.map(this::decode).toArray(n -> new String[n]));
    }

    @Override
    public String[] getGroupNames() {
        return stream(getGroups()).get(stream -> stream.map(this::decode).toArray(n -> new String[n]));
    }

    private String decode(final String value) {
        return new String(Base64.getDecoder().decode(value), Constants.CHARSET_UTF_8);
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
        final List<String> list = new ArrayList<>();
        list.add(fessConfig.getRoleSearchUserPrefix() + getName());
        stream(getRoles()).of(stream -> stream.forEach(s -> list.add(fessConfig.getRoleSearchRolePrefix() + decode(s))));
        stream(getGroups()).of(stream -> stream.forEach(s -> list.add(fessConfig.getRoleSearchGroupPrefix() + decode(s))));
        return list.toArray(new String[list.size()]);
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> toSource() {
        final Map<String, Object> sourceMap = new HashMap<>();
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (password != null) {
            sourceMap.put("password", password);
        }
        if (groups != null) {
            sourceMap.put("groups", groups);
        }
        if (roles != null) {
            sourceMap.put("roles", roles);
        }
        if (attributes != null) {
            sourceMap.putAll(attributes);
        }
        return sourceMap;
    }
}
