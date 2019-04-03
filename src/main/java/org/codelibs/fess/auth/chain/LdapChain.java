/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.auth.chain;

import org.codelibs.fess.es.user.exentity.User;
import org.codelibs.fess.util.ComponentUtil;

public class LdapChain implements AuthenticationChain {

    @Override
    public void update(final User user) {
        ComponentUtil.getLdapManager().insert(user);
    }

    @Override
    public void delete(final User user) {
        ComponentUtil.getLdapManager().delete(user);
    }

    @Override
    public boolean changePassword(final String username, final String password) {
        final boolean changed = ComponentUtil.getLdapManager().changePassword(username, password);
        return !changed || ComponentUtil.getFessConfig().isLdapAdminSyncPassword();
    }

    @Override
    public User load(final User user) {
        ComponentUtil.getLdapManager().apply(user);
        return user;
    }

}
