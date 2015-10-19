package org.codelibs.fess.es.exentity;

import java.util.Map;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.NTLMScheme;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.crawler.client.http.Authentication;
import org.codelibs.fess.crawler.client.http.impl.AuthenticationImpl;
import org.codelibs.fess.crawler.client.http.ntlm.JcifsEngine;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.es.bsentity.BsWebAuthentication;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;

/**
 * @author FreeGen
 */
public class WebAuthentication extends BsWebAuthentication {

    private static final long serialVersionUID = 1L;
    private WebConfig webConfig;

    public Authentication getAuthentication() {
        return new AuthenticationImpl(getAuthScope(), getCredentials(), getAuthScheme());
    }

    private AuthScheme getAuthScheme() {
        if (Constants.BASIC.equals(getProtocolScheme())) {
            return new BasicScheme();
        } else if (Constants.DIGEST.equals(getProtocolScheme())) {
            return new DigestScheme();
        } else if (Constants.NTLM.equals(getProtocolScheme())) {
            return new NTLMScheme(new JcifsEngine());
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
            p = getPort().intValue();
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
            webConfig = webConfigService.getWebConfig(getWebConfigId()).get();
        }
        return webConfig;
    }

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }
}
