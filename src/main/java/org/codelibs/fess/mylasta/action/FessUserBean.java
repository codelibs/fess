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
package org.codelibs.fess.mylasta.action;

import static org.codelibs.core.stream.StreamUtil.stream;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FessUser;
import org.lastaflute.web.login.TypicalUserBean;

/**
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
        };
    }
}
