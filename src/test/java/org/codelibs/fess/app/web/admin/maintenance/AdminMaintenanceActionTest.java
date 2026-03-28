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
package org.codelibs.fess.app.web.admin.maintenance;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class AdminMaintenanceActionTest extends UnitFessTestCase {

    // ===================================================================================
    //                                                                     isLogFilename
    //                                                                     ==============

    @Test
    public void test_isLogFilename_logExtension() {
        final AdminMaintenanceAction action = new AdminMaintenanceAction();
        assertTrue(action.isLogFilename("fess.log"));
        assertTrue(action.isLogFilename("crawler.log"));
        assertTrue(action.isLogFilename("audit.log"));
    }

    @Test
    public void test_isLogFilename_logGzExtension() {
        final AdminMaintenanceAction action = new AdminMaintenanceAction();
        assertTrue(action.isLogFilename("fess.log.gz"));
        assertTrue(action.isLogFilename("crawler.log.gz"));
    }

    @Test
    public void test_isLogFilename_invalidExtension() {
        final AdminMaintenanceAction action = new AdminMaintenanceAction();
        assertFalse(action.isLogFilename("fess.txt"));
        assertFalse(action.isLogFilename("fess.xml"));
        assertFalse(action.isLogFilename("fess.log.bak"));
    }

    // ===================================================================================
    //                                                                     FessMessages
    //                                                                     ==============

    @Test
    public void test_operationAlreadyRunningMessageKey() {
        assertEquals("{errors.operation_already_running}", FessMessages.ERRORS_operation_already_running);
    }

    @Test
    public void test_addErrorsOperationAlreadyRunning() {
        final FessMessages messages = new FessMessages();
        final FessMessages result = messages.addErrorsOperationAlreadyRunning("testProperty", "host1");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.hasMessageOf("testProperty"));
    }

    @Test
    public void test_addErrorsOperationAlreadyRunning_withDifferentHosts() {
        final FessMessages messages1 = new FessMessages();
        messages1.addErrorsOperationAlreadyRunning("prop", "host1");
        assertFalse(messages1.isEmpty());

        final FessMessages messages2 = new FessMessages();
        messages2.addErrorsOperationAlreadyRunning("prop", "node1@host2");
        assertFalse(messages2.isEmpty());
    }

    // ===================================================================================
    //                                                                         Constants
    //                                                                         ==========

    @Test
    public void test_roleConstant() {
        assertEquals("admin-maintenance", AdminMaintenanceAction.ROLE);
    }

    // ===================================================================================
    //                                                                         ActionForm
    //                                                                         ===========

    @Test
    public void test_actionForm_defaults() {
        final ActionForm form = new ActionForm();
        assertNull(form.replaceAliases);
        assertNull(form.resetDictionaries);
        // numberOfShardsForDoc and autoExpandReplicasForDoc have defaults from FessConfig
        assertNotNull(form.numberOfShardsForDoc);
        assertNotNull(form.autoExpandReplicasForDoc);
        assertNull(form.loadBulkData);
        // rebuildConfigIndex defaults to Constants.ON
        assertEquals("on", form.rebuildConfigIndex);
        assertNull(form.rebuildUserIndex);
        assertNull(form.rebuildLogIndex);
    }

    @Test
    public void test_actionForm_setValues() {
        final ActionForm form = new ActionForm();
        form.replaceAliases = "on";
        form.resetDictionaries = "on";
        form.numberOfShardsForDoc = "5";
        form.autoExpandReplicasForDoc = "0-1";
        form.loadBulkData = "on";
        form.rebuildConfigIndex = "on";
        form.rebuildUserIndex = "on";
        form.rebuildLogIndex = "on";

        assertEquals("on", form.replaceAliases);
        assertEquals("on", form.resetDictionaries);
        assertEquals("5", form.numberOfShardsForDoc);
        assertEquals("0-1", form.autoExpandReplicasForDoc);
        assertEquals("on", form.loadBulkData);
        assertEquals("on", form.rebuildConfigIndex);
        assertEquals("on", form.rebuildUserIndex);
        assertEquals("on", form.rebuildLogIndex);
    }
}
