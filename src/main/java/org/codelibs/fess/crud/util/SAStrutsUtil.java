/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.crud.util;

import javax.servlet.http.HttpServletRequest;

import org.codelibs.sastruts.core.util.ActionMessagesUtil;
import org.lastaflute.web.ruts.message.ActionMessage;
import org.lastaflute.web.ruts.message.ActionMessages;
import org.lastaflute.web.util.LaRequestUtil;

public class SAStrutsUtil {
    protected SAStrutsUtil() {
        // nothing
    }

    public static void addMessage(final String key) {
        addMessage(LaRequestUtil.getRequest(), key);
    }

    public static void addMessage(final HttpServletRequest request, final String key) {
        final ActionMessages msgs = new ActionMessages();
        msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key));
        ActionMessagesUtil.saveMessages(request, msgs);
    }

    public static void addMessage(final String key, final Object... values) {
        addMessage(LaRequestUtil.getRequest(), key, values);
    }

    public static void addMessage(final HttpServletRequest request, final String key, final Object... values) {
        final ActionMessages msgs = new ActionMessages();
        msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key, values));
        ActionMessagesUtil.saveMessages(request, msgs);
    }

    public static void addSessionMessage(final String key) {
        addSessionMessage(LaRequestUtil.getRequest(), key);
    }

    public static void addSessionMessage(final HttpServletRequest request, final String key) {
        final ActionMessages msgs = new ActionMessages();
        msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key));
        ActionMessagesUtil.saveMessages(request.getSession(), msgs);
    }

    public static void addSessionMessage(final String key, final Object... values) {
        addSessionMessage(LaRequestUtil.getRequest(), key, values);
    }

    public static void addSessionMessage(final HttpServletRequest request, final String key, final Object... values) {
        final ActionMessages msgs = new ActionMessages();
        msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key, values));
        ActionMessagesUtil.saveMessages(request.getSession(), msgs);
    }
}
