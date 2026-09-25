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

import java.util.function.Function;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * Helper class for managing virtual host configurations and routing.
 * This class provides functionality to handle virtual host-based routing
 * based on HTTP headers.
 */
public class VirtualHostHelper {

    /**
     * Default constructor.
     */
    public VirtualHostHelper() {
        // Default constructor
    }

    /**
     * Gets the virtual host key from the current request.
     * The key is determined by matching HTTP headers against configured virtual hosts.
     *
     * @return The virtual host key, or empty string if no match found
     */
    public String getVirtualHostKey() {
        return LaRequestUtil.getOptionalRequest().map(req -> (String) req.getAttribute(FessConfig.VIRTUAL_HOST_VALUE)).orElseGet(() -> {
            final String value = processVirtualHost(s -> s, StringUtil.EMPTY);
            LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(FessConfig.VIRTUAL_HOST_VALUE, value));
            return value;
        });
    }

    /**
     * Processes virtual host configuration by applying a function to the matched virtual host key.
     *
     * @param <T> The return type of the function
     * @param func The function to apply to the virtual host key
     * @param defaultValue The default value to return if no virtual host matches
     * @return The result of applying the function, or the default value
     */
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
}
