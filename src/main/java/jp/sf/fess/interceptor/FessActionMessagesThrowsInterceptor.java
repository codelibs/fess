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

package jp.sf.fess.interceptor;

import jp.sf.fess.UnsupportedSearchException;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.struts.interceptor.ActionMessagesThrowsInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FessActionMessagesThrowsInterceptor extends
        ActionMessagesThrowsInterceptor {
    private static final long serialVersionUID = 1L;

    private static final String SYSTEM_ERROR_JSP = "/error/system.jsp";

    private static final Logger logger = LoggerFactory
            .getLogger(FessActionMessagesThrowsInterceptor.class);

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        try {
            return super.invoke(invocation);
        } catch (final Throwable t) { // NOPMD
            printLog(t);
            return SYSTEM_ERROR_JSP;
        }
    }

    private void printLog(final Throwable t) {
        if (t.getClass().getName().endsWith("ClientAbortException")) {
            // ignore
            if (logger.isDebugEnabled()) {
                logger.debug(t.getMessage());
            }
        } else if (t instanceof UnsupportedSearchException) {
            logger.warn(t.getMessage());
        } else {
            logger.error("System error occured.", t);
        }
    }
}
