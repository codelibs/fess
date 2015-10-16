package org.codelibs.fess.app.web.cache;

import java.io.Serializable;

public class CacheForm implements Serializable {
    private static final long serialVersionUID = 1L;

    //@Required(target = "go,cache")
    //@Maxbytelength(maxbytelength = 100)
    public String docId;

    public String[] hq;

}
