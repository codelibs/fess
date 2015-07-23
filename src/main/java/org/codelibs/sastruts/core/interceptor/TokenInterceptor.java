/*
 * Copyright 2012 the CodeLibs Project and the Others.
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
package org.codelibs.sastruts.core.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.codelibs.sastruts.core.annotation.Token;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.core.aop.interceptors.AbstractInterceptor;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author shinsuke
 * 
 */
public class TokenInterceptor extends AbstractInterceptor {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept
     * .MethodInvocation)
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Token token = invocation.getMethod().getAnnotation(Token.class);
        if (token != null) {
            final TokenProcessor processor = TokenProcessor.getInstance();
            final HttpServletRequest request = LaRequestUtil.getRequest();
            if (token.save()) {
                processor.saveToken(request);
            } else if (token.validate() && !processor.isTokenValid(request, !token.keep())) {
                processor.resetToken(request);
                throw new ActionMessagesException("errors.token", new Object[0]);
            }
        }

        return invocation.proceed();
    }
}
