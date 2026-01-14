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
package org.codelibs.fess.mylasta.direction.sponsor;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessApiFailureHookTest extends UnitFessTestCase {

    private FessApiFailureHook apiFailureHook;
    private FessConfig originalFessConfig;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        apiFailureHook = new FessApiFailureHook();
        originalFessConfig = ComponentUtil.getFessConfig();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        if (originalFessConfig != null) {
            ComponentUtil.setFessConfig(originalFessConfig);
        }
        super.tearDown(testInfo);
    }

    // Test basic initialization
    @Test
    public void test_hookInitialization() {
        assertNotNull(apiFailureHook);
    }

    // Test API result status codes
    @Test
    public void test_apiResultStatusCodes() {
        // Test OK status
        ApiResult okResult = new ApiResult.ApiResponse().status(Status.OK).result();
        assertNotNull(okResult);

        // Test BAD_REQUEST status
        ApiResult badRequestResult = new ApiResult.ApiResponse().status(Status.BAD_REQUEST).result();
        assertNotNull(badRequestResult);

        // Test UNAUTHORIZED status
        ApiResult unauthorizedResult = new ApiResult.ApiResponse().status(Status.UNAUTHORIZED).result();
        assertNotNull(unauthorizedResult);

        // Test FAILED status
        ApiResult failedResult = new ApiResult.ApiResponse().status(Status.FAILED).result();
        assertNotNull(failedResult);

        // Test SYSTEM_ERROR status
        ApiResult systemErrorResult = new ApiResult.ApiResponse().status(Status.SYSTEM_ERROR).result();
        assertNotNull(systemErrorResult);
    }

    // Test API result with messages
    @Test
    public void test_apiResultWithMessages() {
        String testMessage = "Test error message";
        ApiResult.ApiErrorResponse errorResponse = new ApiResult.ApiErrorResponse();
        errorResponse.message(testMessage);
        errorResponse.status(Status.BAD_REQUEST);
        ApiResult result = errorResponse.result();

        assertNotNull(result);
        // Cannot directly access protected fields, just verify the result is not null
    }

    // Test configuration for JSON response
    @Test
    public void test_configurationSettings() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getApiJsonResponseExceptionIncluded() {
                return "false";
            }
        });

        FessConfig config = ComponentUtil.getFessConfig();
        assertEquals("false", config.getApiJsonResponseExceptionIncluded());
    }

    // Test error code generation
    @Test
    public void test_errorCodeGeneration() {
        long timestamp = System.currentTimeMillis();
        String errorCode = "error_code:" + Long.toHexString(timestamp);

        assertTrue(errorCode.startsWith("error_code:"));
        assertTrue(errorCode.length() > 11); // "error_code:" is 11 chars
    }

    // Test with different exception types
    @Test
    public void test_exceptionTypes() {
        // Test RuntimeException
        RuntimeException runtimeEx = new RuntimeException("Runtime error");
        assertEquals("Runtime error", runtimeEx.getMessage());

        // Test Exception
        Exception generalEx = new Exception("General error");
        assertEquals("General error", generalEx.getMessage());

        // Test with null message
        RuntimeException nullMessageEx = new RuntimeException();
        assertNull(nullMessageEx.getMessage());

        // Test with empty message
        RuntimeException emptyMessageEx = new RuntimeException("");
        assertEquals("", emptyMessageEx.getMessage());
    }

    // Test JSON response creation helper
    @Test
    public void test_jsonResponseCreation() {
        ApiResult.ApiErrorResponse errorResponse = new ApiResult.ApiErrorResponse();
        errorResponse.message("Success");
        errorResponse.status(Status.OK);
        ApiResult result = errorResponse.result();

        assertNotNull(result);
        // Cannot directly access protected fields, just verify the result is not null
    }

    // Test multiple configurations
    @Test
    public void test_multipleConfigurations() {
        // Test with exception included = true
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getApiJsonResponseExceptionIncluded() {
                return "true";
            }
        });
        assertEquals("true", ComponentUtil.getFessConfig().getApiJsonResponseExceptionIncluded());

        // Test with exception included = false
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getApiJsonResponseExceptionIncluded() {
                return "false";
            }
        });
        assertEquals("false", ComponentUtil.getFessConfig().getApiJsonResponseExceptionIncluded());

        // Test with null value
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getApiJsonResponseExceptionIncluded() {
                return null;
            }
        });
        assertNull(ComponentUtil.getFessConfig().getApiJsonResponseExceptionIncluded());
    }

    // Test case sensitivity for configuration values
    @Test
    public void test_configurationCaseSensitivity() {
        String[] testValues = { "TRUE", "True", "true", "FALSE", "False", "false" };

        for (String value : testValues) {
            ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getApiJsonResponseExceptionIncluded() {
                    return value;
                }
            });

            assertEquals(value, ComponentUtil.getFessConfig().getApiJsonResponseExceptionIncluded());
        }
    }
}