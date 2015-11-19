package org.codelibs.fess.app.web.admin.design;

import java.io.Serializable;

import org.lastaflute.web.validation.Required;

public class FileAccessForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Required
    public String fileName;
}
