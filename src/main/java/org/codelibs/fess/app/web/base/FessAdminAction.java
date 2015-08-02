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
package org.codelibs.fess.app.web.base;

import javax.annotation.Resource;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.lastaflute.di.util.LdiFileUtil;
import org.lastaflute.web.servlet.session.SessionManager;
import org.lastaflute.web.validation.VaMessenger;

/**
 * @author codelibs
 * @author jflute
 */
public abstract class FessAdminAction extends FessBaseAction {

    @Resource
    private SessionManager sessionManager;

    protected void write(String path, byte[] data) {
        LdiFileUtil.write(path, data);
    }

    protected void saveInfo(VaMessenger<FessMessages> validationMessagesLambda) {
        FessMessages messages = createMessages();
        validationMessagesLambda.message(messages);
        sessionManager.info().save(messages);
    }
}
