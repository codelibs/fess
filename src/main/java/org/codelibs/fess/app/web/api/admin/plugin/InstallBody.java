package org.codelibs.fess.app.web.api.admin.plugin;

import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;

public class InstallBody {
    @Required
    @Size(max = 100)
    public String name;

    @Required
    @Size(max = 100)
    public String version;
}
