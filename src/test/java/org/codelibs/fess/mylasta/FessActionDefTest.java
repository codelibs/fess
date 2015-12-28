package org.codelibs.fess.mylasta;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.lastaflute.police.HotDeployDestroyerPolice;
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
    }

    public void test_nonWebHasWebReference() throws Exception {
        policeStoryOfJavaClassChase(new NonWebHasWebReferencePolice());
    }

    public void test_webPackageNinjaReferencePolice() throws Exception {
        policeStoryOfJavaClassChase(new WebPackageNinjaReferencePolice());
    }
}