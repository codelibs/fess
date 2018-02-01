/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigType;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CrawlingConfigHelper {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingConfigHelper.class);

    protected final Map<String, CrawlingConfig> crawlingConfigMap = new ConcurrentHashMap<>();

    protected int count = 1;

    protected Cache<String, CrawlingConfig> crawlingConfigCache;

    @PostConstruct
    public void init() {
        crawlingConfigCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    public ConfigType getConfigType(final String configId) {
        if (configId == null || configId.length() < 2) {
            return null;
        }
        final String configType = configId.substring(0, 1);
        if (ConfigType.WEB.getTypePrefix().equals(configType)) {
            return ConfigType.WEB;
        } else if (ConfigType.FILE.getTypePrefix().equals(configType)) {
            return ConfigType.FILE;
        } else if (ConfigType.DATA.getTypePrefix().equals(configType)) {
            return ConfigType.DATA;
        }
        return null;
    }

    protected String getId(final String configId) {
        if (configId == null || configId.length() < 2) {
            return null;
        }
        return configId.substring(1);
    }

    public CrawlingConfig getCrawlingConfig(final String configId) {
        try {
            return crawlingConfigCache.get(configId, () -> {
                final ConfigType configType = getConfigType(configId);
                if (configType == null) {
                    return null;
                }
                final String id = getId(configId);
                if (id == null) {
                    return null;
                }
                switch (configType) {
                case WEB:
                    final WebConfigService webConfigService = ComponentUtil.getComponent(WebConfigService.class);
                    return webConfigService.getWebConfig(id).get();
                case FILE:
                    final FileConfigService fileConfigService = ComponentUtil.getComponent(FileConfigService.class);
                    return fileConfigService.getFileConfig(id).get();
                case DATA:
                    final DataConfigService dataConfigService = ComponentUtil.getComponent(DataConfigService.class);
                    return dataConfigService.getDataConfig(id).get();
                default:
                    return null;
                }
            });
        } catch (final ExecutionException e) {
            logger.warn("Failed to access a crawling config cache: " + configId, e);
            return null;
        }
    }

    public void refresh() {
        crawlingConfigCache.invalidateAll();
    }

    public synchronized String store(final String sessionId, final CrawlingConfig crawlingConfig) {
        final String sessionCountId = sessionId + "-" + count;
        crawlingConfigMap.put(sessionCountId, crawlingConfig);
        count++;
        return sessionCountId;
    }

    public void remove(final String sessionId) {
        crawlingConfigMap.remove(sessionId);
    }

    public CrawlingConfig get(final String sessionId) {
        return crawlingConfigMap.get(sessionId);
    }

}
