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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

/**
 * The feature gate of the passkey login: it is on only when it is enabled and both the relying
 * party ID and the allowed origins are set.
 */
public class PasskeyServiceTest extends UnitFessTestCase {

    private PasskeyService createService(final boolean enabled, final String rpId, final String origins) {
        final PasskeyService service = new PasskeyService();
        service.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isPasskeyEnabled() {
                return enabled;
            }

            @Override
            public String getPasskeyRpId() {
                return rpId;
            }

            @Override
            public String getPasskeyOrigins() {
                return origins;
            }
        };
        return service;
    }

    @Test
    public void test_isEnabled_falseByDefault() {
        final PasskeyService service = new PasskeyService();
        service.fessConfig = ComponentUtil.getFessConfig();
        assertFalse(service.isEnabled());
    }

    @Test
    public void test_isEnabled_falseWhenDisabled() {
        assertFalse(createService(false, "example.com", "https://example.com").isEnabled());
    }

    @Test
    public void test_isEnabled_falseWhenRpIdBlank() {
        assertFalse(createService(true, "", "https://example.com").isEnabled());
        assertFalse(createService(true, "  ", "https://example.com").isEnabled());
    }

    @Test
    public void test_isEnabled_falseWhenOriginsBlank() {
        assertFalse(createService(true, "example.com", "").isEnabled());
        assertFalse(createService(true, "example.com", "  ").isEnabled());
    }

    @Test
    public void test_isEnabled_true() {
        assertTrue(createService(true, "example.com", "https://example.com").isEnabled());
    }
}
