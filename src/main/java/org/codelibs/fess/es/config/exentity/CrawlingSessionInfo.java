/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.config.exentity;

import java.util.Locale;

import org.codelibs.fess.es.config.bsentity.BsCrawlingSessionInfo;
import org.codelibs.fess.es.config.exbhv.CrawlingSessionBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author FreeGen
 */
public class CrawlingSessionInfo extends BsCrawlingSessionInfo {

    private static final long serialVersionUID = 1L;

    private OptionalEntity<CrawlingSession> crawlingSession;

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public OptionalEntity<CrawlingSession> getCrawlingSession() {
        if (crawlingSession != null) {
            final CrawlingSessionBhv crawlingSessionBhv = ComponentUtil.getComponent(CrawlingSessionBhv.class);
            crawlingSession = crawlingSessionBhv.selectEntity(cb -> {
                cb.query().docMeta().setId_Equal(getCrawlingSessionId());
            });
        }
        return crawlingSession;
    }

    public String getKeyMsg() {
        final Locale locale = LaRequestUtil.getRequest().getLocale();
        final String message = ComponentUtil.getMessageManager().getMessage(locale, "labels.crawling_session_" + getKey());
        if (message == null || message.startsWith("???")) {
            return getKey();
        }
        return message;
    }

}
