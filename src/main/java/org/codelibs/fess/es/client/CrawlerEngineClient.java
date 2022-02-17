/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.client;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fesen.client.HttpClient;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.client.Client;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.Settings.Builder;

public class CrawlerEngineClient extends FesenClient {
    @Override
    protected Client createClient() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Builder builder = Settings.builder().putList("http.hosts", address).put("processors", fessConfig.getCrawlerHttpProcessors());
        final String username = fessConfig.getOpenSearchUsername();
        final String password = fessConfig.getOpenSearchPassword();
        if (StringUtil.isNotBlank(username) && StringUtil.isNotBlank(password)) {
            builder.put(Constants.FESEN_USERNAME, username);
            builder.put(Constants.FESEN_PASSWORD, password);
        }
        final String authorities = fessConfig.getOpenSearchHttpSslCertificateAuthorities();
        if (StringUtil.isNotBlank(authorities)) {
            builder.put("http.ssl.certificate_authorities", authorities);
        }
        return new HttpClient(builder.build(), null);
    }
}
