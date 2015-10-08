package org.codelibs.fess.app.web.admin.dict.kuromoji;

import org.lastaflute.web.validation.Required;

public class DownloadForm {
    @Required
    public String dictId;
}
