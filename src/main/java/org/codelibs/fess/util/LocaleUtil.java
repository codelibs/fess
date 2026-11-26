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
package org.codelibs.fess.util;

import java.util.Locale;

/**
 * Utility methods for the locale of a request.
 */
public final class LocaleUtil {

    private LocaleUtil() {
    }

    /**
     * Reads a Chinese locale as simplified ({@code zh_CN}) or traditional ({@code zh_TW}) Chinese. The message
     * bundles are named by language and country ({@code fess_message_zh_CN}, {@code fess_message_zh_TW}), and so are
     * the locales of the label types, so a Chinese locale that names its script ({@code zh-Hans}, {@code zh-Hant},
     * also with a region such as {@code zh-Hans-HK}) or only Hong Kong or Macao ({@code zh-HK}, {@code zh-MO}) would
     * miss both and fall back to English. The script decides when there is one: simplified is {@code zh_CN} and
     * traditional is {@code zh_TW}, as {@code SystemHelper#normalizeLang} reads {@code zh-Hans} and {@code zh-Hant}.
     * Without a script, {@code zh-HK} and {@code zh-MO} are traditional ({@code normalizeLang} reads {@code zh-HK} so
     * too, but leaves {@code zh-MO} as {@code zh}). The servlet container may hand the script over as the variant
     * ({@code zh-Hans} as {@code zh__HANS}, the form Tomcat's default Accept-Language parser gives), so a variant that
     * is {@code Hans} or {@code Hant} is read as the script when the locale has none; any other variant is not. Any
     * other locale is returned as it is, a bare {@code zh} included: it names no script, and as there is no
     * {@code fess_message_zh} bundle it is answered from the default one.
     *
     * @param locale the locale of the request, may be null
     * @return {@link Locale#SIMPLIFIED_CHINESE} or {@link Locale#TRADITIONAL_CHINESE} for such a locale, otherwise the
     *         given locale itself (null for null)
     */
    public static Locale toChineseRegionLocale(final Locale locale) {
        if (locale == null || !"zh".equals(locale.getLanguage())) {
            return locale;
        }
        String script = locale.getScript();
        if (script.isEmpty()) {
            final String variant = locale.getVariant();
            if ("Hans".equalsIgnoreCase(variant) || "Hant".equalsIgnoreCase(variant)) {
                script = variant;
            }
        }
        final String country = locale.getCountry();
        if ("Hans".equalsIgnoreCase(script)) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        if ("Hant".equalsIgnoreCase(script) || script.isEmpty() && ("HK".equals(country) || "MO".equals(country))) {
            return Locale.TRADITIONAL_CHINESE;
        }
        return locale;
    }
}
