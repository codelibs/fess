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

package jp.sf.fess.db.exentity;

import java.util.Locale;

import jp.sf.fess.db.bsentity.BsCrawlingSessionInfo;

import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;

/**
 * The entity of CRAWLING_SESSION_INFO.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class CrawlingSessionInfo extends BsCrawlingSessionInfo {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // MySQL: public String getKey() {return super.getIdKey();}
    // MySQL: public void setKey(String key) {super.setIdKey(key);}

    public String getKeyMsg() {
        final Locale locale = RequestUtil.getRequest().getLocale();
        final String message = MessageResourcesUtil.getMessage(locale,
                "labels.crawling_session_" + getKey());
        if (message == null || message.startsWith("???")) {
            return getKey();
        }
        return message;
    }
}
