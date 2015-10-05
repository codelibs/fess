package org.codelibs.fess.app.web.go;

import org.codelibs.fess.app.web.RootForm;

public class GoForm extends RootForm {
    private static final long serialVersionUID = 1L;

    //@Required(target = "go,cache")
    //@Maxbytelength(maxbytelength = 100)
    public String docId;

    public String rt;

    public String hash;
}
