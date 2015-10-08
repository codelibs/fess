package org.codelibs.fess.app.web.admin.dict.kuromoji;

import org.lastaflute.web.validation.Required;

public class EditForm extends CreateForm {

    private static final long serialVersionUID = 1L;

    @Required
    public Long id;

    public String getDisplayId() {
        return dictId + ":" + id;
    }
}
