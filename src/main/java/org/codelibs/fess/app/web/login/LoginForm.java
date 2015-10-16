package org.codelibs.fess.app.web.login;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class LoginForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    public String username;

    @NotBlank
    public String password;

    public void clearSecurityInfo() {
        password = null;
    }

}
