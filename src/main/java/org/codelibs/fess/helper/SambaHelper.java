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
package org.codelibs.fess.helper;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.jcifs.smb.SID;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for Samba-related operations.
 */
public class SambaHelper {

    private static final Logger logger = LogManager.getLogger(SambaHelper.class);

    /**
     * SID type for an alias.
     */
    public static final int SID_TYPE_ALIAS = 4;

    /**
     * SID type for a deleted account.
     */
    public static final int SID_TYPE_DELETED = 6;

    /**
     * SID type for a domain group.
     */
    public static final int SID_TYPE_DOM_GRP = 2;

    /**
     * SID type for a domain.
     */
    public static final int SID_TYPE_DOMAIN = 3;

    /**
     * SID type for an invalid SID.
     */
    public static final int SID_TYPE_INVALID = 7;

    /**
     * SID type for an unknown SID.
     */
    public static final int SID_TYPE_UNKNOWN = 8;

    /**
     * SID type for a non-use SID.
     */
    public static final int SID_TYPE_USE_NONE = 0;

    /**
     * SID type for a user.
     */
    public static final int SID_TYPE_USER = 1;

    /**
     * SID type for a well-known group.
     */
    public static final int SID_TYPE_WKN_GRP = 5;

    /**
     * The Fess configuration.
     */
    protected FessConfig fessConfig;

    /**
     * Constructor.
     */
    public SambaHelper() {
        super();
    }

    /**
     * Initializes the SambaHelper.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
    }

    /**
     * Gets the account ID from a SID.
     * @param sid The SID.
     * @return The account ID.
     */
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

    /**
     * Gets the account ID from a SID.
     * @param sid The SID.
     * @return The account ID.
     */
    public String getAccountId(final org.codelibs.jcifs.smb1.SID sid) {
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

    /**
     * Creates a search role.
     * @param type The SID type.
     * @param name The account name.
     * @return The search role.
     */
    protected String createSearchRole(final int type, final String name) {
        if (fessConfig.isLdapLowercasePermissionName()) {
            return type + fessConfig.getCanonicalLdapName(name).toLowerCase(Locale.ROOT);
        }
        return type + fessConfig.getCanonicalLdapName(name);
    }
}
