package org.codelibs.fess.mylasta;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.core.document.DocumentGenerator;

public class FessActionDefTest extends UnitFessTestCase {

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
        DocumentGenerator documentGenerator = new DocumentGenerator();
        documentGenerator.saveLastaDocMeta();
    }

}
