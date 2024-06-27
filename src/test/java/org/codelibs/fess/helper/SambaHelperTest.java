/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

import jcifs.SID;
import jcifs.smb.SmbException;

public class SambaHelperTest extends UnitFessTestCase {

    public SambaHelper sambaHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        sambaHelper = new SambaHelper();
    }

    public void test_smb_account() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2,4:2,5:1";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }
        });
        sambaHelper.init();

        assertEquals("1Test User", sambaHelper.getAccountId(USER_SID));

    }

    public void test_smb_account_lowercase() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2,4:2,5:1";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return true;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }
        });
        sambaHelper.init();

        assertEquals("1test user", sambaHelper.getAccountId(USER_SID));

    }

    private static final SID USER_SID = new SID() {

        @Override
        public SID getDomainSid() {
            return null;
        }

        @Override
        public int getRid() {
            return 0;
        }

        @Override
        public String toDisplayString() {
            return getDomainName() + "\\" + getAccountName();
        }

        @Override
        public String getAccountName() {
            return "Test User";
        }

        @Override
        public String getDomainName() {
            return "WORKGROUP";
        }

        @Override
        public String getTypeText() {
            return "User";
        }

        @Override
        public int getType() {
            return 1;
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }
    };

}
