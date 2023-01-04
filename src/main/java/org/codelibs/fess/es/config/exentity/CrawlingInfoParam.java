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
package org.codelibs.fess.es.config.exentity;

import java.util.Locale;

import org.codelibs.fess.es.config.bsentity.BsCrawlingInfoParam;
import org.codelibs.fess.es.config.exbhv.CrawlingInfoBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;

/**
 * @author ESFlute (using FreeGen)
 */
public class CrawlingInfoParam extends BsCrawlingInfoParam {

    private static final long serialVersionUID = 1L;

    private OptionalEntity<CrawlingInfo> crawlingInfo;

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

    public OptionalEntity<CrawlingInfo> getCrawlingInfo() {
        if (crawlingInfo != null) {
            final CrawlingInfoBhv crawlingInfoBhv = ComponentUtil.getComponent(CrawlingInfoBhv.class);
            crawlingInfo = crawlingInfoBhv.selectEntity(cb -> {
                cb.query().docMeta().setId_Equal(getCrawlingInfoId());
            });
        }
        return crawlingInfo;
    }

    public String getKeyMsg() {
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        final String message = ComponentUtil.getMessageManager().getMessage(locale, "labels.crawling_info_" + getKey());
        if (message == null || message.startsWith("???")) {
            return getKey();
        }
        return message;
    }

    @Override
    public String toString() {
        return "CrawlingInfoParam [crawlingInfo=" + crawlingInfo + ", crawlingInfoId=" + crawlingInfoId + ", createdTime=" + createdTime
                + ", key=" + key + ", value=" + value + ", docMeta=" + docMeta + "]";
    }

}
