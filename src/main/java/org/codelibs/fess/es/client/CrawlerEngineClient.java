/*
 * Copyright 2012-2021 CodeLibs Project and the Others.
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
import org.codelibs.fesen.client.Client;
import org.codelibs.fesen.client.HttpClient;
import org.codelibs.fesen.common.settings.Settings;
import org.codelibs.fesen.common.settings.Settings.Builder;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class CrawlerEngineClient extends FesenClient {
    protected Client createClient() {
        final Builder builder = Settings.builder().putList("http.hosts", address);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String username = fessConfig.getFesenUsername();
        final String password = fessConfig.getFesenPassword();
        if (StringUtil.isNotBlank(username) && StringUtil.isNotBlank(password)) {
            builder.put(Constants.FESEN_USERNAME, username);
            builder.put(Constants.FESEN_PASSWORD, password);
        }
        return new HttpClient(builder.build(), null);
    }
}
