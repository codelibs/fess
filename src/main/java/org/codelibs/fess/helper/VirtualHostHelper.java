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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.function.Function;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.util.LaRequestUtil;

public class VirtualHostHelper {

    public HtmlNext getVirtualHostPath(final HtmlNext page) {
        return processVirtualHost(s -> {
            final String basePath = getVirtualHostBasePath(s, page);
            return new HtmlNext(basePath + page.getRoutingPath());
        }, page);
    }

    protected String getVirtualHostBasePath(final String s, final HtmlNext page) {
        return StringUtil.isBlank(s) ? StringUtil.EMPTY : "/" + s;
    }

    public String getVirtualHostKey() {
        return LaRequestUtil.getOptionalRequest().map(req -> (String) req.getAttribute(FessConfig.VIRTUAL_HOST_VALUE)).orElseGet(() -> {
            final String value = processVirtualHost(s -> s, StringUtil.EMPTY);
            LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(FessConfig.VIRTUAL_HOST_VALUE, value));
            return value;
        });
    }

    protected <T> T processVirtualHost(final Function<String, T> func, final T defaultValue) {
        final Tuple3<String, String, String>[] vHosts = ComponentUtil.getFessConfig().getVirtualHosts();
        return LaRequestUtil.getOptionalRequest().map(req -> {
            for (final Tuple3<String, String, String> host : vHosts) {
                final String headerValue = req.getHeader(host.getValue1());
                if (host.getValue2().equalsIgnoreCase(headerValue)) {
                    return func.apply(host.getValue3());
                }
            }
            return defaultValue;
        }).orElse(defaultValue);
    }

    public String[] getVirtualHostPaths() {
        return stream(ComponentUtil.getFessConfig().getVirtualHosts())
                .get(stream -> stream.map(h -> "/" + h.getValue3()).toArray(n -> new String[n]));
    }
}
