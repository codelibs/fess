package org.codelibs.fess.app.web.admin.design;

import java.io.Serializable;

import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.validation.Required;

public class UploadForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Required
    public MultipartFormFile designFile;

    public String designFileName;
}
