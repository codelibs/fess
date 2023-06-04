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
package org.codelibs.fess.cors;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class CorsHandler {

    protected static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    protected static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    protected static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    protected static final String ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK = "Access-Control-Allow-Private-Network";

    protected static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    protected static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    public abstract void process(String origin, ServletRequest request, ServletResponse response);

}
