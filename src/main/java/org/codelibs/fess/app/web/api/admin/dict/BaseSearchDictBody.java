package org.codelibs.fess.app.web.api.admin.dict;

import org.codelibs.fess.app.web.api.admin.BaseSearchBody;
import org.lastaflute.web.validation.Required;

public class BaseSearchDictBody extends BaseSearchBody {
    @Required
    public String dictId;
}
