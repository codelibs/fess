/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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
package org.codelibs.fess.lasta.web.action;

/**
 * @author jflute
 */
public class FessUserBean extends TypicalUserBaseBean {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The serial version UID for object serialization. (Default) */
    private static final long serialVersionUID = 1L;

    /** The user type for Member, e.g. used by access context. */
    public static final String USER_TYPE = "M";

    /** The application type for Dockside, e.g. used by access context. */
    public static final String DOMAIN_TYPE = "DCK";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Long memberId;
    protected final String memberName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public FessUserBean() {
        memberId = null;
        memberName = null;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    @Override
    public Long getUserId() {
        return memberId;
    }

    @Override
    public String getUserType() {
        return USER_TYPE;
    }

    @Override
    public String getDomainType() {
        return DOMAIN_TYPE;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Long getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }
}
