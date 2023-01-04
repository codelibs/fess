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

import java.io.File;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.lastaflute.police.NonActionExtendsActionPolice;
import org.dbflute.utflute.lastaflute.police.NonWebHasWebReferencePolice;
import org.dbflute.utflute.lastaflute.police.WebPackageNinjaReferencePolice;

public class FessActionDefTest extends UnitFessTestCase {

    //    public void test_component() throws Exception {
    //        policeStoryOfJavaClassChase(new ActionComponentPolice(tp -> getComponent(tp)));
    //    }

    // TODO
    //    public void test_hotDeployDestroyer() throws Exception {
    //        policeStoryOfJavaClassChase(new HotDeployDestroyerPolice(tp -> getComponent(tp)));
    //    }

    public void test_nonActionExtendsAction() throws Exception {
        policeStoryOfJavaClassChase(new NonActionExtendsActionPolice());
        assertTrue(true);
    }

    public void test_nonWebHasWebReference() throws Exception {
        policeStoryOfJavaClassChase(new NonWebHasWebReferencePolice());
        assertTrue(true);
    }

    public void test_webPackageNinjaReferencePolice() throws Exception {
        policeStoryOfJavaClassChase(new WebPackageNinjaReferencePolice() {
            public void handle(File srcFile, Class<?> clazz) {
                final String webPackageKeyword = getWebPackageKeyword();
                if (!clazz.getName().contains(webPackageKeyword) ||
                // exclude app.web.api.admin packages
                        clazz.getName().contains(".app.web.api.admin.")) {
                    return;
                }
                check(srcFile, clazz, webPackageKeyword);
            }
        });
    }
}