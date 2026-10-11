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
package org.codelibs.fess.app.web.api.admin.storage;

import java.util.Iterator;

import org.codelibs.fess.app.service.AccessTokenService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessage;

/**
 * Tests for the storage listing of {@link ApiAdminStorageAction} on an installation whose storage
 * cannot be reached.
 */
public class ApiAdminStorageActionTest extends UnitFessTestCase {

    /**
     * gcs is served by fess-storage-gcs, so on an installation without that plugin no client is
     * registered for it.
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

    private ApiAdminStorageAction injectedAction() {
        // see ApiAdminLogActionTest: only the framework fields throwValidationErrorApi() needs are wired
        suppressBindingOf(AccessTokenService.class);
        suppressBindingOf(FessLoginAssist.class);
        // the ApiResult a successful listing builds reads the registered system helper
        final SystemHelper helper = new SystemHelper();
        ComponentUtil.register(helper, "systemHelper");
        final ApiAdminStorageAction action = new ApiAdminStorageAction() {
            {
                systemHelper = helper;
            }
        };
        inject(action);
        return action;
    }

    /** An unreachable storage is an error with the reason, not an OK response with no items. */
    @Test
    public void test_list_anUnreachableStorageIsAValidationErrorThatNamesThePlugin() {
        useAStorageTypeNoPluginServes();
        final ApiAdminStorageAction action = injectedAction();

        assertValidationError(() -> action.list(OptionalThing.empty())).handle(data -> {
            data.requiredMessageOf("_global", "errors.storage_access_error");
            // the argument carries the reason, which for a missing plugin names the plugin to install
            final Iterator<UserMessage> messages = data.requiredMessages().accessByFlatIterator();
            final String reason = (String) messages.next().getValues()[0];
            assertTrue(reason.contains("fess-storage-gcs"), reason);
        });
    }

    @Test
    public void test_list_aDirectoryOfAnUnreachableStorageIsAValidationError() {
        useAStorageTypeNoPluginServes();
        final ApiAdminStorageAction action = injectedAction();

        try {
            action.list(OptionalThing.of("L2E="));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        assertValidationError(() -> action.list(OptionalThing.of("L2E="))).handle(data -> {
            data.requiredMessageOf("_global", "errors.storage_access_error");
        });
    }
}
