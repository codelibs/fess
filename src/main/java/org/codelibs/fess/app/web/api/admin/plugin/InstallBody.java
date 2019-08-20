package org.codelibs.fess.app.web.api.admin.plugin;

import org.lastaflute.web.validation.Required;

import javax.validation.constraints.Size;

public class InstallBody {
    @Required
    @Size(max = 100)
    public String name;

    @Required
    @Size(max = 100)
    public String version;
}
