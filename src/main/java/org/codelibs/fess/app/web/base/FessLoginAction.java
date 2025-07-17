/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.web.base;

import org.codelibs.fess.app.web.admin.AdminAction;
import org.codelibs.fess.app.web.admin.dashboard.AdminDashboardAction;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.lastaflute.web.response.HtmlResponse;

/**
 * Abstract base action class for handling login functionality in Fess.
 * This action provides logic for redirecting authenticated users to appropriate
 * admin interfaces based on their roles and permissions.
 */
public abstract class FessLoginAction extends FessSearchAction {
    /**
     * Default constructor.
     */
    public FessLoginAction() {
        super();
    }

    /**
     * Returns the appropriate HTML response for login handling.
     * If a user is already authenticated, redirects to the appropriate admin interface.
     * Otherwise, displays the login page.
     *
     * @return HTML response for login page or redirect to admin interface
     */
    protected HtmlResponse getHtmlResponse() {
        return getUserBean().map(this::redirectByUser).orElse(asHtml(virtualHost(path_Login_IndexJsp)));
    }

    /**
     * Redirects an authenticated user to the appropriate admin interface based on their roles.
     * Users with admin roles are redirected to the dashboard, while other users are redirected
     * to their designated admin action class or to the root if no specific action is available.
     *
     * @param user the authenticated user bean containing role information
     * @return HTML response redirecting to the appropriate admin interface
     */
    protected HtmlResponse redirectByUser(final FessUserBean user) {
        if (user.hasRoles(fessConfig.getAuthenticationAdminRolesAsArray())) {
            return redirect(AdminDashboardAction.class);
        }
        final Class<? extends FessAdminAction> actionClass = AdminAction.getAdminActionClass(user);
        if (actionClass != null) {
            return redirect(actionClass);
        }
        return redirectToRoot();
    }
}
