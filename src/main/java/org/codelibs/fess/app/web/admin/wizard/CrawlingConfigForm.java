package org.codelibs.fess.app.web.admin.wizard;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;

public class CrawlingConfigForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Required
    @Size(max = 200)
    public String crawlingConfigName;

    @Required
    @Size(max = 1000)
    public String crawlingConfigPath;

    // TODO
    //    @Min(0)
    //    @Max(Integer.MAX_VALUE)
    public String depth;

    // TODO
    @Size(max = 100)
    //    @Min(0)
    //    @Max(Long.MAX_VALUE)
    public String maxAccessCount;

}
