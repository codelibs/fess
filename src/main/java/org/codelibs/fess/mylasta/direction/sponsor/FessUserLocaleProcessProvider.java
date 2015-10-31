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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.Locale;

import org.dbflute.optional.OptionalObject;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.servlet.request.UserLocaleProcessProvider;

/**
 * @author jflute
 */
public class FessUserLocaleProcessProvider implements UserLocaleProcessProvider {

    @Override
    public boolean isAcceptCookieLocale() {
        return false;
    }

    @Override
    public OptionalThing<Locale> findBusinessLocale(final ActionRuntime runtimeMeta, final RequestManager requestManager) {
        return OptionalObject.empty(); // to next determination
    }

    @Override
    public OptionalThing<Locale> getRequestedLocale(final RequestManager requestManager) {
        return OptionalObject.empty(); // means browser default
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String hash = Integer.toHexString(hashCode());
        return DfTypeUtil.toClassTitle(this) + ":{acceptCookieLocale=" + isAcceptCookieLocale() + "}@" + hash;
    }
}
