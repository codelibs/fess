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
package org.codelibs.fess.app.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.config.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.opensearch.config.exentity.WebAuthentication;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class WebAuthenticationServiceTest extends UnitFessTestCase {

    private final List<String> storedPasswords = new ArrayList<>();

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        FessProp.propMap.clear();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getAppEncryptPropertyPattern() {
                return ".*password|.*key|.*secret";
            }
        });
        storedPasswords.clear();
    }

    private WebAuthenticationService newService() throws Exception {
        final WebAuthenticationService service = new WebAuthenticationService();
        final Field field = WebAuthenticationService.class.getDeclaredField("webAuthenticationBhv");
        field.setAccessible(true);
        field.set(service, new WebAuthenticationBhv() {
            @Override
            public void insertOrUpdate(final WebAuthentication entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                storedPasswords.add(entity.getPassword());
            }
        });
        return service;
    }

    private String store(final String password) throws Exception {
        final WebAuthentication entity = new WebAuthentication();
        entity.setUsername("user");
        entity.setPassword(password);
        newService().store(entity);
        assertEquals(1, storedPasswords.size());
        return storedPasswords.get(0);
    }

    // The password is stored encrypted, in the form of the encrypted parameters.
    @Test
    public void test_store_encryptsPassword() throws Exception {
        final String stored = store("s3cret");
        assertTrue(stored, stored.startsWith("{cipher}"));
        assertFalse(stored, stored.contains("s3cret"));
        assertEquals("s3cret", ParameterUtil.decryptCredential(stored, null, "password"));
    }

    // A stored value that is saved again, as the edit screen and a restored backup do, is not encrypted twice.
    @Test
    public void test_store_doesNotEncryptTwice() throws Exception {
        final String encrypted = ParameterUtil.encryptCredential("s3cret");
        assertEquals(encrypted, store(encrypted));
    }

    @Test
    public void test_store_emptyPasswordStaysEmpty() throws Exception {
        assertTrue(StringUtil.isEmpty(store("")));
        storedPasswords.clear();
        assertNull(store(null));
    }
}
