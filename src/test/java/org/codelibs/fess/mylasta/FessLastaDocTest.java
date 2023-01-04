/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.mylasta;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.meta.DocumentGenerator;

public class FessLastaDocTest extends UnitFessTestCase {

    // ===================================================================================
    //                                                                           Component
    //                                                                           =========
    /*
    public void test_component() throws Exception {
        // ## Arrange ##
        String appWebPkg = ".app.web.";
        String actionSuffix = "Action";

        // ## Act ##
        policeStoryOfJavaClassChase((srcFile, clazz) -> {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) { // e.g. BaseAction
                return;
            }
            final String className = clazz.getName();
            if (className.contains(appWebPkg) && className.endsWith(actionSuffix)) {
                // ## Assert ##
                markHere("exists");
                getComponent(clazz); // expect no exception
            }
        });
        assertMarked("exists");
    }
    */

    // ===================================================================================
    //                                                                            Document
    //                                                                            ========
    public void test_document() throws Exception {
        saveLastaDocMeta();
        assertTrue(true);
    }

    @Override
    protected DocumentGenerator createDocumentGenerator() {
        return super.createDocumentGenerator().suppressJobDoc();
    }
}
