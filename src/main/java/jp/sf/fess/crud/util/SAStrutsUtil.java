/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.crud.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.struts.util.ActionMessagesUtil;
import org.seasar.struts.util.RequestUtil;

public class SAStrutsUtil {
    protected SAStrutsUtil() {
        // nothing
    }

    public static void addMessage(final String key) {
        addMessage(RequestUtil.getRequest(), key);
    }

    public static void addMessage(final HttpServletRequest request,
            final String key) {
        final ActionMessages msgs = new ActionMessages();
        msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key));
        ActionMessagesUtil.saveMessages(request, msgs);
    }

    public static void addMessage(final String key, final Object... values) {
        addMessage(RequestUtil.getRequest(), key, values);
    }

    public static void addMessage(final HttpServletRequest request,
            final String key, final Object... values) {
        final ActionMessages msgs = new ActionMessages();
        msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key, values));
        ActionMessagesUtil.saveMessages(request, msgs);
    }

    public static void addSessionMessage(final String key) {
        addSessionMessage(RequestUtil.getRequest(), key);
    }

    public static void addSessionMessage(final HttpServletRequest request,
            final String key) {
        final ActionMessages msgs = new ActionMessages();
        msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key));
        ActionMessagesUtil.saveMessages(request.getSession(), msgs);
    }

    public static void addSessionMessage(final String key,
            final Object... values) {
        addSessionMessage(RequestUtil.getRequest(), key, values);
    }

    public static void addSessionMessage(final HttpServletRequest request,
            final String key, final Object... values) {
        final ActionMessages msgs = new ActionMessages();
        msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key, values));
        ActionMessagesUtil.saveMessages(request.getSession(), msgs);
    }
}
