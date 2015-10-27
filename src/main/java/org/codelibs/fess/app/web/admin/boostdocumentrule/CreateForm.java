package org.codelibs.fess.app.web.admin.boostdocumentrule;

import java.io.Serializable;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;

public class CreateForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Digits(integer = 10, fraction = 0)
    public Integer crudMode;

    @Required
    @Size(max = 10000)
    public String urlExpr;

    @Required
    @Size(max = 10000)
    public String boostExpr;

    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @Digits(integer = 10, fraction = 0)
    public Integer sortOrder;

    @Required
    @Size(max = 1000)
    public String createdBy;

    @Required
    @Digits(integer = 19, fraction = 0)
    public Long createdTime;

    public void initialize() {
        crudMode = CrudMode.CREATE;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }
}