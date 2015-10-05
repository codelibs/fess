package org.codelibs.fess.app.web.cache;

import org.codelibs.fess.app.web.RootForm;

public class CacheForm extends RootForm {
    private static final long serialVersionUID = 1L;

    //@Required(target = "go,cache")
    //@Maxbytelength(maxbytelength = 100)
    public String docId;
    public String[] hq;

}
