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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.web.path.ActionAdjustmentProvider;
import org.lastaflute.web.path.FormMappingOption;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.response.XmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * The provider of action adjustment.
 *
 * @author jflute
 */
public class FessActionAdjustmentProvider implements ActionAdjustmentProvider {

    private static final Logger logger = LogManager.getLogger(FessActionAdjustmentProvider.class);

    // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
    // you can adjust your actions by overriding
    // default methods defined at the interface
    // _/_/_/_/_/_/_/_/_/_/

    protected Map<String, List<Pair<String, String>>> responseHeaderMap = new HashMap<>();

    protected List<Pair<String, String>> defaultResponseHeaders;

    public FessActionAdjustmentProvider(final FessConfig fessConfig) {
        parseResponseHeaderConfig(fessConfig.getResponseHeaders());
    }

    private void parseResponseHeaderConfig(final String value) {
        if (StringUtil.isBlank(value)) {
            defaultResponseHeaders = Collections.emptyList();
            return;
        }

        StreamUtil.split(value, "\n").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(s -> {
            final String[] values = StringUtils.split(s, "=", 2);
            List<Pair<String, String>> list = responseHeaderMap.get(values[0]);
            if (list == null) {
                list = new ArrayList<>();
                responseHeaderMap.put(values[0], list);
            }
            final String[] keyValue = StringUtils.split(values[1], ":", 2);
            if (keyValue.length == 2) {
                list.add(new Pair<>(keyValue[0].trim(), keyValue[1].trim()));
            } else if (keyValue.length == 1) {
                list.add(new Pair<>(keyValue[0].trim(), StringUtil.EMPTY));
            } else {
                logger.warn("Unexpected value: {}", s);
            }
        }));

        final List<Pair<String, String>> headerList = responseHeaderMap.remove("*");
        if (headerList != null) {
            defaultResponseHeaders = headerList;
        } else {
            defaultResponseHeaders = Collections.emptyList();
        }
    }

    @Override
    public FormMappingOption adjustFormMapping() {
        return new FormMappingOption()
                .filterSimpleTextParameter((parameter, meta) -> parameter.trim().replace("\r\n", "\n").replace('\r', '\n'));
    }

    @Override
    public String customizeActionMappingRequestPath(final String requestPath) {
        if (StringUtil.isBlank(requestPath)) {
            return null;
        }
        final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        if (StringUtil.isBlank(virtualHostKey)) {
            return null;
        }
        final String prefix = "/" + virtualHostKey;
        if (requestPath.startsWith(prefix)) {
            return requestPath.substring(prefix.length());
        }
        return null;
    }

    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{}";
    }

    @Override
    public void adjustActionResponseJustBefore(final ActionRuntime runtime, final ActionResponse response) {
        final String mimeType;
        if (response instanceof HtmlResponse) {
            mimeType = "text/html";
        } else if (response instanceof JsonResponse) {
            mimeType = "application/json";
        } else if (response instanceof XmlResponse) {
            mimeType = "text/xml";
        } else if (response instanceof StreamResponse) {
            mimeType = "application/octet-stream";
        } else {
            logger.debug("Unknown response: {}", response);
            return;
        }
        adjustActionResponseHeaders(mimeType, (k, v) -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Apply header {}:{} to response of {}", k, v, mimeType);
            }
            response.header(k, v);
        });
    }

    protected void adjustActionResponseHeaders(final String mimeType, final BiConsumer<String, String> callback) {
        defaultResponseHeaders.forEach(header -> callback.accept(header.getFirst(), header.getSecond()));
        final List<Pair<String, String>> headers = responseHeaderMap.get(mimeType);
        if (headers != null) {
            headers.forEach(header -> callback.accept(header.getFirst(), header.getSecond()));
        }
    }
}
