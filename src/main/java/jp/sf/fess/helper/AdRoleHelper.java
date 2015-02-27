/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdRoleHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(AdRoleHelper.class);

    public String domain;

    public Properties adProperties;

    public void setAdProperties(final Properties adProperties) {
        this.adProperties = adProperties;
    }

    public List<String> getRoleList(final String userId) {
        final List<String> rolelist = new ArrayList<String>();

        DirContext ctx = null;
        try {
            ctx = new InitialDirContext(adProperties);

            //set search conditions
            final String filter = "cn=" + userId;
            final SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String name = domain;
            name = "dc=" + name;
            name = name.replace(".", ",dc=");

            //search
            final NamingEnumeration<SearchResult> rslt = ctx.search(name,
                    filter, controls);
            while (rslt.hasMoreElements()) {
                final SearchResult srcrslt = rslt.next();
                final Attributes attrs = srcrslt.getAttributes();

                //get group attr
                final Attribute attr = attrs.get("memberOf");
                if (attr == null) {
                    continue;
                }

                for (int i = 0; i < attr.size(); i++) {
                    String strTmp = (String) attr.get(i);

                    int strStart = 0;
                    int strEnd = 0;

                    strStart = strTmp.indexOf("CN=");
                    strStart += "CN=".length();
                    strEnd = strTmp.indexOf(',');

                    strTmp = strTmp.substring(strStart, strEnd);

                    rolelist.add(strTmp);
                }
            }

        } catch (final Exception e) {
            logger.warn("Failed to resolve roles: " + userId, e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (final NamingException e) {
                    // ignored
                    logger.warn("Failed to close: " + userId, e);
                }
            }
        }

        logger.debug("ADGroup:" + rolelist.toString());
        return rolelist;
    }
}
