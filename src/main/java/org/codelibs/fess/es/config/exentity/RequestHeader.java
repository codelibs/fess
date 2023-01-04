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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.es.config.bsentity.BsRequestHeader;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class RequestHeader extends BsRequestHeader {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(RequestHeader.class);

    private WebConfig webConfig;

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

    public org.codelibs.fess.crawler.client.http.RequestHeader getCrawlerRequestHeader() {
        return new org.codelibs.fess.crawler.client.http.RequestHeader(getName(), getValue());
    }

    public WebConfig getWebConfig() {
        if (webConfig == null) {
            final WebConfigService webConfigService = ComponentUtil.getComponent(WebConfigService.class);
            try {
                webConfig = webConfigService.getWebConfig(getWebConfigId()).get();
            } catch (final Exception e) {
                logger.warn("Web Config {} does not exist.", getWebConfigId(), e);
            }
        }
        return webConfig;
    }

    @Override
    public String toString() {
        return "RequestHeader [webConfig=" + webConfig + ", createdBy=" + createdBy + ", createdTime=" + createdTime + ", name=" + name
                + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + ", value=" + value + ", webConfigId=" + webConfigId
                + ", docMeta=" + docMeta + "]";
    }
}
