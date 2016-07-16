/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.base.login;

import java.util.Map;

import org.codelibs.fess.entity.FessUser;

public class OpenIdConnectLoginCredential implements LoginCredential {

    private final Map<String, Object> attributes;

    public OpenIdConnectLoginCredential(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void validate() {
        assertLoginAccountRequired((String) attributes.get("email"));
    }

    @Override
    public String getId() {
        return (String) attributes.get("email");
    }

    @Override
    public Object getResource() {
        return attributes;
    }

    public User getUser() {
        return new User(getId());
    }

    public static class User implements FessUser {

        private final String name;

        protected User(final String name) {
            this.name = name;
            // TODO groups and roles
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String[] getGroupNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String[] getPermissions() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
