/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.user.exentity;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.bsentity.BsUser;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class User extends BsUser implements FessUser {

    private static final Logger logger = LogManager.getLogger(User.class);

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
        return stream(getRoles()).get(stream -> stream.map(this::decode).filter(Objects::nonNull).toArray(n -> new String[n]));
    }

    @Override
    public String[] getGroupNames() {
        return stream(getGroups()).get(stream -> stream.map(this::decode).filter(Objects::nonNull).toArray(n -> new String[n]));
    }

    /**
     * Decodes a stored role or group id back to its name. A value that is not an id was written by
     * a caller that sent a name instead, and decoding it throws. Skipping it keeps the rest of the
     * account readable, so it can still be listed and repaired, rather than failing every read of
     * this user and therefore every login.
     *
     * @param value the stored role or group id
     * @return the decoded name, or null when the value is not a valid id
     */
    private String decode(final String value) {
        try {
            return new String(Base64.getDecoder().decode(value), Constants.CHARSET_UTF_8);
        } catch (final IllegalArgumentException e) {
            logger.warn("Skipped an unreadable role or group id on user {}: {} is not a Base64-encoded name. "
                    + "Reassign the roles and groups of this user to repair it.", name, value);
            return null;
        }
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

    /**
     * Clears the original password from memory.
     * Should be called after the password has been used for authentication or provisioning.
     */
    public void clearOriginalPassword() {
        this.originalPassword = null;
    }

    @Override
    public String[] getPermissions() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final List<String> list = new ArrayList<>();
        list.add(fessConfig.getRoleSearchUserPrefix() + getName());
        stream(getRoles()).of(stream -> stream.map(this::decode)
                .filter(Objects::nonNull)
                .forEach(s -> list.add(fessConfig.getRoleSearchRolePrefix() + s)));
        stream(getGroups()).of(stream -> stream.map(this::decode)
                .filter(Objects::nonNull)
                .forEach(s -> list.add(fessConfig.getRoleSearchGroupPrefix() + s)));
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
