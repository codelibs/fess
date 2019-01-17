/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.curl.Curl.Method;
import org.codelibs.curl.CurlRequest;
import org.codelibs.fess.util.ResourceUtil;

public class CurlHelper {

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
        return new CurlRequest(method, ResourceUtil.getElasticsearchHttpUrl() + path);
    }
}
