package org.codelibs.fess.app.web.admin.dict.kuromoji;

import javax.validation.constraints.Digits;

import org.lastaflute.web.validation.Required;

public class EditForm extends CreateForm {

    private static final long serialVersionUID = 1L;

    @Required
    @Digits(integer = 19, fraction = 0)
    public Long id;

    public String getDisplayId() {
        return dictId + ":" + id;
    }
}
