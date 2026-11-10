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
package org.codelibs.fess.tomcat.session;

import java.io.IOException;

import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import jakarta.servlet.ServletException;

/**
 * A valve for the Context pipeline that writes the sessions a request touched to the shared session store.
 *
 * <p>It saves after the rest of the chain has run, which for a response that fits the buffer is before Tomcat
 * commits it, so the next request of the client, possibly on another node, sees the change. The sessions to save are
 * collected by {@link FessSession#access()} instead of asking the request for its session, which would read the
 * store for requests that never use the session. It does nothing if the Context does not use a
 * {@link FessSessionManager}.</p>
 */
public class FessSessionValve extends ValveBase {

    /**
     * Creates a valve that supports asynchronous requests.
     */
    public FessSessionValve() {
        super(true);
    }

    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        final Context context = request.getContext();
        final Manager manager = context != null ? context.getManager() : null;
        if (!(manager instanceof final FessSessionManager fessManager) || !fessManager.beginRequest()) {
            getNext().invoke(request, response);
            return;
        }
        try {
            getNext().invoke(request, response);
        } finally {
            fessManager.endRequest();
        }
    }
}
