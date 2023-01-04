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
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.es.config.bsentity.BsFileAuthentication;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class FileAuthentication extends BsFileAuthentication {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(FileAuthentication.class);

    private FileConfig fileConfig;

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

    public FileConfig getFileConfig() {
        if (fileConfig == null) {
            final FileConfigService fileConfigService = ComponentUtil.getComponent(FileConfigService.class);
            try {
                fileConfig = fileConfigService.getFileConfig(getFileConfigId()).get();
            } catch (final Exception e) {
                logger.warn("File Config {} does not exist.", getFileConfigId(), e);
            }
        }
        return fileConfig;
    }

    @Override
    public String toString() {
        return "FileAuthentication [fileConfig=" + fileConfig + ", createdBy=" + createdBy + ", createdTime=" + createdTime
                + ", fileConfigId=" + fileConfigId + ", hostname=" + hostname + ", parameters=" + parameters + ", port=" + port
                + ", protocolScheme=" + protocolScheme + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + ", username="
                + username + ", docMeta=" + docMeta + "]";
    }
}
