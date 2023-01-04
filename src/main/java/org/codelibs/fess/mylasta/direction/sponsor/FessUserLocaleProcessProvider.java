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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalObject;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.servlet.request.UserLocaleProcessProvider;

/**
 * @author jflute
 */
public class FessUserLocaleProcessProvider implements UserLocaleProcessProvider {
    private static final Logger logger = LogManager.getLogger(FessUserLocaleProcessProvider.class);

    @Override
    public boolean isAcceptCookieLocale() {
        return false;
    }

    @Override
    public OptionalThing<Locale> findBusinessLocale(final ActionRuntime runtimeMeta, final RequestManager requestManager) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String name = fessConfig.getQueryBrowserLangParameterName();
        if (StringUtil.isNotBlank(name)) {
            try {
                return requestManager.getParameter(name).filter(StringUtil::isNotBlank).map(LocaleUtils::toLocale);
            } catch (final Exception e) {
                logger.debug("Failed to parse a value of {}.", name, e);
            }
        }
        return OptionalObject.empty();
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
