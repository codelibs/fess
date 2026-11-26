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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LocaleUtil#toChineseRegionLocale}. Pure function; no container required.
 */
public class LocaleUtilTest {

    @Test
    public void test_toChineseRegionLocale_readsTheScript() {
        assertEquals(Locale.SIMPLIFIED_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-Hans")));
        assertEquals(Locale.SIMPLIFIED_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-Hans-CN")));
        // the script decides over the region
        assertEquals(Locale.SIMPLIFIED_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-Hans-HK")));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-Hant")));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-Hant-HK")));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-Hant-CN")));
    }

    @Test
    public void test_toChineseRegionLocale_readsAScriptGivenAsTheVariant() {
        // Tomcat's default Accept-Language parser hands zh-Hans over as the variant (zh__HANS)
        assertEquals(Locale.SIMPLIFIED_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.of("zh", "", "HANS")));
        assertEquals(Locale.SIMPLIFIED_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.of("zh", "HK", "HANS")));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.of("zh", "", "HANT")));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.of("zh", "CN", "HANT")));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.of("zh", "", "hant")));
    }

    @Test
    public void test_toChineseRegionLocale_readsHongKongAndMacaoAsTraditional() {
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-HK")));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-MO")));
        // a variant that is not a script is not read as one: Hong Kong and Macao still read as traditional
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.of("zh", "HK", "FOO")));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.of("zh", "MO", "FOO")));
    }

    @Test
    public void test_toChineseRegionLocale_leavesOtherLocalesAsTheyAre() {
        assertEquals(Locale.SIMPLIFIED_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.SIMPLIFIED_CHINESE));
        assertEquals(Locale.TRADITIONAL_CHINESE, LocaleUtil.toChineseRegionLocale(Locale.TRADITIONAL_CHINESE));
        // a bare zh names no script, and a region other than Hong Kong or Macao is not read
        assertEquals(Locale.CHINESE, LocaleUtil.toChineseRegionLocale(Locale.CHINESE));
        assertEquals(Locale.forLanguageTag("zh-SG"), LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("zh-SG")));
        assertEquals(Locale.of("zh", "", "FOO"), LocaleUtil.toChineseRegionLocale(Locale.of("zh", "", "FOO")));
        assertEquals(Locale.JAPANESE, LocaleUtil.toChineseRegionLocale(Locale.JAPANESE));
        assertEquals(Locale.ENGLISH, LocaleUtil.toChineseRegionLocale(Locale.ENGLISH));
        // a script of another language is not read
        assertEquals(Locale.forLanguageTag("sr-Latn"), LocaleUtil.toChineseRegionLocale(Locale.forLanguageTag("sr-Latn")));
        assertEquals(Locale.ROOT, LocaleUtil.toChineseRegionLocale(Locale.ROOT));
        assertNull(LocaleUtil.toChineseRegionLocale(null));
    }
}
