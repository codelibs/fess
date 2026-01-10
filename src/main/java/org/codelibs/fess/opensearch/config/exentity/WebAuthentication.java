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
package org.codelibs.fess.opensearch.config.exentity;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig.CredentialsType;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.opensearch.config.bsentity.BsWebAuthentication;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;

/**
 * @author FreeGen
 */
public class WebAuthentication extends BsWebAuthentication {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(WebAuthentication.class);

    private WebConfig webConfig;

    public WebAuthenticationConfig getWebAuthenticationConfig() {
        if (StringUtil.isEmpty(getUsername())) {
            throw new CrawlerSystemException(
                    "Username is empty in WebAuthentication configuration. A valid username must be provided for authentication.");
        }

        final WebAuthenticationConfig config = new WebAuthenticationConfig();

        // host/port/realm: only set if not blank (null means "any" - AuthScope.ANY equivalent)
        if (StringUtil.isNotBlank(getHostname())) {
            config.setHost(getHostname());
        }
        if (getPort() != null) {
            config.setPort(getPort());
        }
        if (StringUtil.isNotBlank(getAuthRealm())) {
            config.setRealm(getAuthRealm());
        }

        // AuthSchemeType の設定
        final String scheme = getProtocolScheme();
        if (Constants.BASIC.equals(scheme)) {
            config.setAuthSchemeType(AuthSchemeType.BASIC);
        } else if (Constants.DIGEST.equals(scheme)) {
            config.setAuthSchemeType(AuthSchemeType.DIGEST);
        } else if (Constants.NTLM.equals(scheme)) {
            config.setAuthSchemeType(AuthSchemeType.NTLM);
            // Pass jcifs.* properties via formParameters for NTLM configuration
            final Map<String, String> jcifsParams = new HashMap<>();
            getWebConfig().getConfigParameterMap(ConfigName.CONFIG)
                    .entrySet()
                    .stream()
                    .filter(e -> e.getKey().startsWith(Config.JCIFS_PREFIX))
                    .forEach(e -> jcifsParams.put(e.getKey(), e.getValue()));
            if (!jcifsParams.isEmpty()) {
                config.setNtlmParameters(jcifsParams);
            }
        } else if (Constants.FORM.equals(scheme)) {
            config.setAuthSchemeType(AuthSchemeType.FORM);
            config.setFormParameters(ParameterUtil.parse(getParameters()));
        }

        // Credentials の設定
        final CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername(getUsername());
        credentials.setPassword(getPassword() == null ? StringUtil.EMPTY : getPassword());
        if (Constants.NTLM.equals(scheme)) {
            credentials.setType(CredentialsType.NTLM);
            final Map<String, String> parameterMap = ParameterUtil.parse(getParameters());
            credentials.setDomain(parameterMap.get("domain"));
            credentials.setWorkstation(parameterMap.get("workstation"));
        }
        config.setCredentials(credentials);

        return config;
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
