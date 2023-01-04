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

import java.util.List;

import org.codelibs.fess.es.config.bsentity.BsCrawlingInfo;

/**
 * @author ESFlute (using FreeGen)
 */
public class CrawlingInfo extends BsCrawlingInfo {

    private static final long serialVersionUID = 1L;

    private List<CrawlingInfoParam> crawlingInfoParamList;

    public CrawlingInfo() {
    }

    public CrawlingInfo(final String sessionId) {
        setSessionId(sessionId);
    }

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

    public void setCrawlingInfoInfoList(final List<CrawlingInfoParam> crawlingInfoParamList) {
        this.crawlingInfoParamList = crawlingInfoParamList;
    }

    public List<CrawlingInfoParam> getCrawlingInfoParamList() {
        return crawlingInfoParamList;
    }

    @Override
    public String toString() {
        return "CrawlingInfo [crawlingInfoParamList=" + crawlingInfoParamList + ", createdTime=" + createdTime + ", expiredTime="
                + expiredTime + ", name=" + name + ", sessionId=" + sessionId + ", docMeta=" + docMeta + "]";
    }
}
