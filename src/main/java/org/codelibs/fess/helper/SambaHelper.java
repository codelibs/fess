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
package org.codelibs.fess.helper;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jcifs.SID;

public class SambaHelper {

    private static final Logger logger = LogManager.getLogger(SambaHelper.class);

    public static final int SID_TYPE_ALIAS = 4;

    public static final int SID_TYPE_DELETED = 6;

    public static final int SID_TYPE_DOM_GRP = 2;

    public static final int SID_TYPE_DOMAIN = 3;

    public static final int SID_TYPE_INVALID = 7;

    public static final int SID_TYPE_UNKNOWN = 8;

    public static final int SID_TYPE_USE_NONE = 0;

    public static final int SID_TYPE_USER = 1;

    public static final int SID_TYPE_WKN_GRP = 5;

    protected FessConfig fessConfig;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
    }

    public String getAccountId(final SID sid) {
        final int type = sid.getType();
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Processing SID: {} {} {}", type, sid, sid.toDisplayString());
            } catch (final Exception e) {
                // ignore
            }
        }
        final Integer id = fessConfig.getAvailableSmbSidType(type);
        if (id != null) {
            return createSearchRole(id, sid.getAccountName());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Ignored SID: {} {}", type, sid);
        }
        return null;
    }

    public String getAccountId(final jcifs.smb1.smb1.SID sid) {
        final int type = sid.getType();
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Processing SID: {} {} {}", type, sid, sid.toDisplayString());
            } catch (final Exception e) {
                // ignore
            }
        }
        final Integer id = fessConfig.getAvailableSmbSidType(type);
        if (id != null) {
            return createSearchRole(id, sid.getAccountName());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Ignored SID: {} {}", type, sid);
        }
        return null;
    }

    protected String createSearchRole(final int type, final String name) {
        return type + fessConfig.getCanonicalLdapName(name);
    }
}
