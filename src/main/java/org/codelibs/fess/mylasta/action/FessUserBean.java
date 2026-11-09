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
package org.codelibs.fess.mylasta.action;

import static org.codelibs.core.stream.StreamUtil.stream;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FessUser;
import org.lastaflute.web.login.TypicalUserBean;

/**
 * The user bean for Fess.
 *
 * @author jflute
 */
public class FessUserBean extends TypicalUserBean<String> { // #change_it also LoginAssist

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The serial version UID for object serialization. (Default) */
    private static final long serialVersionUID = 1L;
    private final FessUser user;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /**
     * The password change time (epoch milliseconds) of the local user document that this login
     * session was opened with, 0 when that document had none. Null when the session does not
     * track it: a session of a user who is not a local user, or one opened before it was tracked.
     */
    private Long passwordChangedAt;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public FessUserBean(final FessUser user) {
        this.user = user;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    @Override
    public String getUserId() {
        return user.getName();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String[] getPermissions() {
        return user.getPermissions();
    }

    public String[] getRoles() {
        return user.getRoleNames();
    }

    public String[] getGroups() {
        return user.getGroupNames();
    }

    public boolean isEditable() {
        return user.isEditable();
    }

    public boolean hasRole(final String role) {
        return stream(user.getRoleNames()).get(stream -> stream.anyMatch(s -> s.equals(role)));
    }

    public boolean hasRoles(final String[] acceptedRoles) {
        return stream(user.getRoleNames())
                .get(stream -> stream.anyMatch(s1 -> stream(acceptedRoles).get(s3 -> s3.anyMatch(s2 -> s2.equals(s1)))));
    }

    public boolean hasGroup(final String group) {
        return stream(user.getGroupNames()).get(stream -> stream.anyMatch(s -> s.equals(group)));
    }

    public boolean hasGroups(final String[] acceptedGroups) {
        return stream(user.getGroupNames())
                .get(stream -> stream.anyMatch(s1 -> stream(acceptedGroups).get(s3 -> s3.anyMatch(s2 -> s2.equals(s1)))));
    }

    public FessUser getFessUser() {
        return user;
    }

    /**
     * Gets the password change time this login session was opened with.
     *
     * @return epoch milliseconds, 0 when the user had none, or null when the session does not track it
     */
    public Long getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(final Long passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }

    public static FessUserBean empty() {
        return new FessUserBean(null) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getUserId() {
                return Constants.EMPTY_USER_ID;
            }

            @Override
            public boolean hasRoles(final String[] acceptedRoles) {
                return true;
            }

            @Override
            public String[] getRoles() {
                return StringUtil.EMPTY_STRINGS;
            }

            @Override
            public boolean isEditable() {
                return false;
            }

            @Override
            public String[] getPermissions() {
                return StringUtil.EMPTY_STRINGS;
            }

            @Override
            public String[] getGroups() {
                return StringUtil.EMPTY_STRINGS;
            }

            @Override
            public boolean hasRole(final String role) {
                return false;
            }

            @Override
            public boolean hasGroup(final String group) {
                return false;
            }

            @Override
            public boolean hasGroups(final String[] acceptedGroups) {
                return false;
            }
        };
    }
}
