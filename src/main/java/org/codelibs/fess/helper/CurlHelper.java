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

import static org.codelibs.core.stream.StreamUtil.split;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.Curl.Method;
import org.codelibs.curl.CurlRequest;
import org.codelibs.fesen.client.curl.FesenRequest;
import org.codelibs.fesen.client.node.NodeManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ResourceUtil;

public class CurlHelper {
    private static final Logger logger = LogManager.getLogger(CurlHelper.class);

    private SSLSocketFactory sslSocketFactory;

    private NodeManager nodeManager;

    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String authorities = fessConfig.getFesenHttpSslCertificateAuthorities();
        if (StringUtil.isNotBlank(authorities)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Loading certificate_authorities: {}", authorities);
            }
            try (final InputStream in = new FileInputStream(authorities)) {
                final Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(in);

                final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("server", certificate);

                final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
                sslSocketFactory = sslContext.getSocketFactory();
            } catch (final Exception e) {
                logger.warn("Failed to load {}", authorities, e);
            }
        }

        final String[] hosts = split(ResourceUtil.getFesenHttpUrl(), ",")
                .get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).toArray(n -> new String[n]));
        nodeManager = new NodeManager(hosts, node -> request(new CurlRequest(Method.GET, node.getUrl("/"))));
        nodeManager.setHeartbeatInterval(fessConfig.getFesenHeartbeatInterval());
    }

    public CurlRequest get(final String path) {
        return request(Method.GET, path).header("Content-Type", "application/json");
    }

    public CurlRequest post(final String path) {
        return request(Method.POST, path).header("Content-Type", "application/json");
    }

    public CurlRequest put(final String path) {
        return request(Method.PUT, path).header("Content-Type", "application/json");
    }

    public CurlRequest delete(final String path) {
        return request(Method.DELETE, path).header("Content-Type", "application/json");
    }

    public CurlRequest request(final Method method, final String path) {
        return request(new FesenRequest(new CurlRequest(method, null), nodeManager, path));
    }

    protected CurlRequest request(final CurlRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String username = fessConfig.getFesenUsername();
        final String password = fessConfig.getFesenPassword();
        if (StringUtil.isNotBlank(username) && StringUtil.isNotBlank(password)) {
            final String value = username + ":" + password;
            final String basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
            request.header("Authorization", basicAuth);
        }
        if (sslSocketFactory != null) {
            request.sslSocketFactory(sslSocketFactory);
        }
        return request;
    }
}
