package org.codelibs.fess.app.web.login;

import org.codelibs.fess.app.web.RootForm;
import org.hibernate.validator.constraints.NotBlank;

public class LoginForm extends RootForm {
    private static final long serialVersionUID = 1L;

    @NotBlank
    public String username;

    @NotBlank
    public String password;

    public void clearSecurityInfo() {
        password = null;
    }

}
