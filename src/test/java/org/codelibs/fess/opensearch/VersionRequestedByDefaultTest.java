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
package org.codelibs.fess.opensearch;

import java.lang.reflect.Field;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * The admin API refuses an update whose {@code version_no} is not the version of the stored document, and the
 * {@code version_no} it hands out comes from the {@code _version} that a search request returns only when it
 * asks for it. The request parameters of the condition beans of the configuration and the user indices ask for
 * it by default, in {@code EsAbstractConditionBean}, which is generated code. If the code is generated again
 * without that default, the entities carry no version, and the check would let every update through without a
 * word: this test fails then.
 */
public class VersionRequestedByDefaultTest extends UnitFessTestCase {

    private static Object defaultVersion(final Class<?> paramsClass) throws Exception {
        final java.lang.reflect.Constructor<?> constructor = paramsClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Field field = paramsClass.getDeclaredField("version");
        field.setAccessible(true);
        return field.get(constructor.newInstance());
    }

    @Test
    public void test_configIndexRequestsTheVersionOfADocument() throws Exception {
        assertEquals(Boolean.TRUE,
                defaultVersion(org.codelibs.fess.opensearch.config.allcommon.EsAbstractConditionBean.SearchRequestParams.class));
    }

    @Test
    public void test_userIndexRequestsTheVersionOfADocument() throws Exception {
        assertEquals(Boolean.TRUE,
                defaultVersion(org.codelibs.fess.opensearch.user.allcommon.EsAbstractConditionBean.SearchRequestParams.class));
    }
}
