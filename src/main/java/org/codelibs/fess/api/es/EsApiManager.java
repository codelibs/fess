package org.codelibs.fess.api.es;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.util.ComponentUtil;

public class EsApiManager extends BaseApiManager {
    protected String[] acceptedRoles = new String[] { "admin" };

    public EsApiManager() {
        setPathPrefix("/admin/server");
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        final String servletPath = request.getServletPath();
        if (servletPath.startsWith(pathPrefix)) {
            FessLoginAssist loginAssist = ComponentUtil.getLoginAssist();
            return loginAssist.getSessionUserBean().map(user -> user.hasRoles(acceptedRoles)).orElseGet(() -> Boolean.FALSE).booleanValue();
        }
        return false;
    }

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub

    }

    public void setAcceptedRoles(String[] acceptedRoles) {
        this.acceptedRoles = acceptedRoles;
    }

}
