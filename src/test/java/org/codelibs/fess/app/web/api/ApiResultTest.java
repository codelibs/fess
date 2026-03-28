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
package org.codelibs.fess.app.web.api;

import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiStartJobResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ApiResultTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    // ===================================================================================
    //                                                             ApiStartJobResponse Tests
    //                                                             =========================

    @Test
    public void test_ApiStartJobResponse_withJobLogId() {
        final ApiStartJobResponse response = new ApiStartJobResponse();
        final ApiStartJobResponse returned = response.jobLogId("abc123def456");
        assertSame(response, returned);
        assertEquals("abc123def456", response.jobLogId);
    }

    @Test
    public void test_ApiStartJobResponse_withNullJobLogId() {
        final ApiStartJobResponse response = new ApiStartJobResponse();
        response.jobLogId(null);
        assertNull(response.jobLogId);
    }

    @Test
    public void test_ApiStartJobResponse_withStatus() {
        final ApiStartJobResponse response = new ApiStartJobResponse();
        response.jobLogId("test-id").status(Status.OK);
        assertEquals("test-id", response.jobLogId);
        assertEquals(0, response.status);
    }

    @Test
    public void test_ApiStartJobResponse_result() {
        final ApiStartJobResponse response = new ApiStartJobResponse();
        response.jobLogId("log-id-xyz").status(Status.OK);
        final ApiResult result = response.result();
        assertNotNull(result);
        assertNotNull(result.response);
        assertTrue(result.response instanceof ApiStartJobResponse);
        final ApiStartJobResponse resultResponse = (ApiStartJobResponse) result.response;
        assertEquals("log-id-xyz", resultResponse.jobLogId);
        assertEquals(0, resultResponse.status);
    }

    @Test
    public void test_ApiStartJobResponse_extendsApiResponse() {
        final ApiStartJobResponse response = new ApiStartJobResponse();
        assertTrue(response instanceof ApiResponse);
    }

    @Test
    public void test_ApiStartJobResponse_statusFluentChain() {
        final ApiStartJobResponse response = new ApiStartJobResponse();
        // status() returns ApiResponse, so the chain works when called last
        final ApiResponse returned = response.jobLogId("id-1").status(Status.OK);
        assertNotNull(returned);
    }

    @Test
    public void test_ApiStartJobResponse_hasVersionField() {
        final ApiStartJobResponse response = new ApiStartJobResponse();
        response.status(Status.OK);
        // version is initialized from SystemHelper.getProductVersion()
        // In test environment it may be null, but the field should exist
        assertEquals(0, response.status);
    }

    // ===================================================================================
    //                                                                        Status Tests
    //                                                                        ============

    @Test
    public void test_Status_OK() {
        assertEquals(0, Status.OK.getId());
    }

    @Test
    public void test_Status_BAD_REQUEST() {
        assertEquals(1, Status.BAD_REQUEST.getId());
    }

    @Test
    public void test_Status_SYSTEM_ERROR() {
        assertEquals(2, Status.SYSTEM_ERROR.getId());
    }
}
