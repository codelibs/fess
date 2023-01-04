/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.auth;

import static org.codelibs.core.stream.StreamUtil.stream;

import org.apache.commons.lang3.ArrayUtils;
import org.codelibs.core.stream.StreamUtil.StreamOf;
import org.codelibs.fess.auth.chain.AuthenticationChain;
import org.codelibs.fess.es.user.exentity.User;

public class AuthenticationManager {

    protected AuthenticationChain[] chains = {};

    public void insert(final User user) {
        chains().of(stream -> stream.forEach(c -> c.update(user)));
    }

    public boolean changePassword(final String username, final String password) {
        return chains().get(stream -> stream.allMatch(c -> c.changePassword(username, password)));
    }

    public void delete(final User user) {
        chains().of(stream -> stream.forEach(c -> c.delete(user)));
    }

    public User load(final User user) {
        User u = user;
        for (final AuthenticationChain chain : chains) {
            u = chain.load(u);
        }
        return u;
    }

    public void addChain(final AuthenticationChain chain) {
        chains = ArrayUtils.addAll(chains, chain);
    }

    protected StreamOf<AuthenticationChain> chains() {
        return stream(chains);
    }

}
