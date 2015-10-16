/*
 * Copyright 2012 the CodeLibs Project and the Others.
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

import java.util.stream.Stream;

import org.codelibs.fess.es.exentity.User;
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
    private User user;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public FessUserBean(final User user) {
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
    public String[] getRoles() {
        return user.getRoles();
    }

    public String[] getGroups() {
        return user.getGroups();
    }

    public boolean hasRole(String role) {
        return Stream.of(user.getRoleNames()).anyMatch(s -> s.equals(role));
    }

    public boolean hasRoles(String[] acceptedRoles) {
        return Stream.of(user.getRoleNames()).anyMatch(s1 -> Stream.of(acceptedRoles).anyMatch(s2 -> s2.equals(s1)));
    }

    public boolean hasGroup(String group) {
        return Stream.of(user.getGroupNames()).anyMatch(s -> s.equals(group));
    }

    public boolean hasGroups(String[] acceptedGroups) {
        return Stream.of(user.getGroupNames()).anyMatch(s1 -> Stream.of(acceptedGroups).anyMatch(s2 -> s2.equals(s1)));
    }
}
