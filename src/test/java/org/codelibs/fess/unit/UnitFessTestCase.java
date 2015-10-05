package org.codelibs.fess.unit;

import org.dbflute.utflute.lastaflute.WebContainerTestCase;

public abstract class UnitFessTestCase extends WebContainerTestCase {
    @Override
    protected String prepareConfigFile() {
        return "test_app.xml";
    }
}
