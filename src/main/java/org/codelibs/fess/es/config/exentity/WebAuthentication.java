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
package org.codelibs.fess.es.config.exentity;

import java.util.Map;
import java.util.Properties;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.crawler.client.http.Authentication;
import org.codelibs.fess.crawler.client.http.form.FormScheme;
import org.codelibs.fess.crawler.client.http.impl.AuthenticationImpl;
import org.codelibs.fess.crawler.client.http.ntlm.JcifsEngine;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.es.config.bsentity.BsWebAuthentication;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;

/**
 * @author FreeGen
 */
public class WebAuthentication extends BsWebAuthentication {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(WebAuthentication.class);

    private WebConfig webConfig;

    public Authentication getAuthentication() {
        return new AuthenticationImpl(getAuthScope(), getCredentials(), getAuthScheme());
    }

    private AuthScheme getAuthScheme() {
        final String scheme = getProtocolScheme();
        if (Constants.BASIC.equals(scheme)) {
            return new BasicScheme();
        }
        if (Constants.DIGEST.equals(scheme)) {
            return new DigestScheme();
        }
        if (Constants.NTLM.equals(scheme)) {
            final Properties props = new Properties();
            getWebConfig().getConfigParameterMap(ConfigName.CONFIG).entrySet().stream()
                    .filter(e -> e.getKey().startsWith(Config.JCIFS_PREFIX)).forEach(e -> {
                        props.setProperty(e.getKey(), e.getValue());
                    });
            return new NTLMScheme(new JcifsEngine(props));
        }
        if (Constants.FORM.equals(scheme)) {
            final Map<String, String> parameterMap = ParameterUtil.parse(getParameters());
            return new FormScheme(parameterMap);
        }
        return null;
    }

    private AuthScope getAuthScope() {
        if (StringUtil.isBlank(getHostname())) {
            return AuthScope.ANY;
        }

        int p;
        if (getPort() == null) {
            p = AuthScope.ANY_PORT;
        } else {
            p = getPort();
        }

        String r = getAuthRealm();
        if (StringUtil.isBlank(r)) {
            r = AuthScope.ANY_REALM;
        }

        String s = getProtocolScheme();
        if (StringUtil.isBlank(s) || Constants.NTLM.equals(s)) {
            s = AuthScope.ANY_SCHEME;
        }

        return new AuthScope(getHostname(), p, r, s);
    }

    private Credentials getCredentials() {
        if (StringUtil.isEmpty(getUsername())) {
            throw new CrawlerSystemException("username is empty.");
        }

        if (Constants.NTLM.equals(getProtocolScheme())) {
            final Map<String, String> parameterMap = ParameterUtil.parse(getParameters());
            final String workstation = parameterMap.get("workstation");
            final String domain = parameterMap.get("domain");
            return new NTCredentials(getUsername(), getPassword(), workstation == null ? StringUtil.EMPTY : workstation,
                    domain == null ? StringUtil.EMPTY : domain);
        }

        return new UsernamePasswordCredentials(getUsername(), getPassword() == null ? StringUtil.EMPTY : getPassword());
    }

    public WebConfig getWebConfig() {
        if (webConfig == null) {
            final WebConfigService webConfigService = ComponentUtil.getComponent(WebConfigService.class);
            try {
                webConfig = webConfigService.getWebConfig(getWebConfigId()).get();
            } catch (final Exception e) {
                logger.warn("Web Config {} does not exist.", getWebConfigId(), e);
            }
        }
        return webConfig;
    }

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    @Override
    public String toString() {
        return "WebAuthentication [webConfig=" + webConfig + ", authRealm=" + authRealm + ", createdBy=" + createdBy + ", createdTime="
                + createdTime + ", hostname=" + hostname + ", parameters=" + parameters + ", port=" + port + ", protocolScheme="
                + protocolScheme + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + ", username=" + username + ", webConfigId="
                + webConfigId + ", docMeta=" + docMeta + "]";
    }
}
