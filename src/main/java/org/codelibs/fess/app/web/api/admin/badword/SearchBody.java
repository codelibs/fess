package org.codelibs.fess.app.web.api.admin.badword;

import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;

public class SearchBody {
    @Required
    public Integer size = ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();

    @Required
    public Integer page = Constants.DEFAULT_ADMIN_PAGE_NUMBER;

}
