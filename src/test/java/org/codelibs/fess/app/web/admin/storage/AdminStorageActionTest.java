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
package org.codelibs.fess.app.web.admin.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.exception.StorageException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

public class AdminStorageActionTest extends UnitFessTestCase {

    /**
     * gcs is served by fess-storage-gcs, so on an installation without that plugin no client is
     * registered for it. That is the configuration the storage screen used to render as an empty
     * list.
     */
    private void useAStorageTypeNoPluginServes() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getStorageType() {
                return "gcs";
            }
        });
    }

    @Test
    public void test_getFileItems_handsTheFailureToTheCaller() {
        useAStorageTypeNoPluginServes();

        final List<Exception> failures = new ArrayList<>();
        final List<Map<String, Object>> items = AdminStorageAction.getFileItems("", failures::add);

        // still an empty list rather than an exception: what changed is that the caller is told why
        assertTrue(items.isEmpty());
        assertEquals(1, failures.size());
        final Exception e = failures.get(0);
        assertTrue(e instanceof StorageException, "unexpected failure: " + e);
        // the message the storage screen shows names the component and the plugin to install
        assertTrue(e.getMessage().contains("gcsStorageClient"), e.getMessage());
        assertTrue(e.getMessage().contains("fess-storage-gcs"), e.getMessage());
    }

    @Test
    public void test_getFileItems_withoutAHandlerStillDiscardsTheFailure() {
        useAStorageTypeNoPluginServes();

        // the form ApiAdminStorageAction uses: unchanged, an empty list and no exception
        assertTrue(AdminStorageAction.getFileItems("").isEmpty());
    }
}
