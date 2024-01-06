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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exbhv.FailureUrlBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigType;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.es.config.exentity.FailureUrl;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.es.config.exentity.WebConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CrawlingConfigHelper {

    private static final Logger logger = LogManager.getLogger(CrawlingConfigHelper.class);

    protected final Map<String, CrawlingConfig> crawlingConfigMap = new ConcurrentHashMap<>();

    protected int count = 1;

    protected Cache<String, CrawlingConfig> crawlingConfigCache;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        crawlingConfigCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    public ConfigType getConfigType(final String configId) {
        if (configId == null || configId.length() < 2) {
            return null;
        }
        final String configType = configId.substring(0, 1);
        if (ConfigType.WEB.getTypePrefix().equals(configType)) {
            return ConfigType.WEB;
        }
        if (ConfigType.FILE.getTypePrefix().equals(configType)) {
            return ConfigType.FILE;
        }
        if (ConfigType.DATA.getTypePrefix().equals(configType)) {
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
                return switch (configType) {
                case WEB -> {
                    final WebConfigService webConfigService = ComponentUtil.getComponent(WebConfigService.class);
                    yield webConfigService.getWebConfig(id).get();
                }
                case FILE -> {
                    final FileConfigService fileConfigService = ComponentUtil.getComponent(FileConfigService.class);
                    yield fileConfigService.getFileConfig(id).get();
                }
                case DATA -> {
                    final DataConfigService dataConfigService = ComponentUtil.getComponent(DataConfigService.class);
                    yield dataConfigService.getDataConfig(id).get();
                }
                default -> null;
                };
            });
        } catch (final Exception e) {
            logger.warn("Failed to access a crawling config cache: {}", configId, e);
            return null;
        }
    }

    public OptionalThing<String> getPipeline(final String configId) {
        final CrawlingConfig config = getCrawlingConfig(configId);
        if (config == null) {
            return OptionalThing.empty();
        }
        final String pipeline = config.getConfigParameterMap(ConfigName.CONFIG).get(Config.PIPELINE);
        if (StringUtil.isBlank(pipeline)) {
            return OptionalThing.empty();
        }
        return OptionalThing.of(pipeline);
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

    public List<WebConfig> getAllWebConfigList() {
        return getAllWebConfigList(true, true, true, null);
    }

    public List<WebConfig> getWebConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllWebConfigList();
        }
        return getAllWebConfigList(true, true, false, idList);
    }

    public List<WebConfig> getAllWebConfigList(final boolean withLabelType, final boolean withRoleType, final boolean available,
            final List<String> idList) {
        return ComponentUtil.getComponent(WebConfigBhv.class).selectList(cb -> {
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
            cb.query().setName_NotEqual(ComponentUtil.getFessConfig().getFormAdminDefaultTemplateName());
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageWebConfigMaxFetchSizeAsInteger());
        });
    }

    public List<FileConfig> getAllFileConfigList() {
        return getAllFileConfigList(true, true, true, null);
    }

    public List<FileConfig> getFileConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllFileConfigList();
        }
        return getAllFileConfigList(true, true, false, idList);
    }

    public List<FileConfig> getAllFileConfigList(final boolean withLabelType, final boolean withRoleType, final boolean available,
            final List<String> idList) {
        return ComponentUtil.getComponent(FileConfigBhv.class).selectList(cb -> {
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
            cb.query().setName_NotEqual(ComponentUtil.getFessConfig().getFormAdminDefaultTemplateName());
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageFileConfigMaxFetchSizeAsInteger());
        });
    }

    public List<DataConfig> getAllDataConfigList() {
        return getAllDataConfigList(true, true, true, null);
    }

    public List<DataConfig> getDataConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllDataConfigList();
        }
        return getAllDataConfigList(true, true, false, idList);
    }

    public List<DataConfig> getAllDataConfigList(final boolean withLabelType, final boolean withRoleType, final boolean available,
            final List<String> idList) {
        return ComponentUtil.getComponent(DataConfigBhv.class).selectList(cb -> {
            if (available) {
                cb.query().setAvailable_Equal(Constants.T);
            }
            if (idList != null) {
                cb.query().setId_InScope(idList);
            }
            cb.query().setName_NotEqual(ComponentUtil.getFessConfig().getFormAdminDefaultTemplateName());
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageDataConfigMaxFetchSizeAsInteger());
        });
    }

    public List<String> getExcludedUrlList(final String configId) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int failureCount = fessConfig.getFailureCountThreshold();
        final String ignoreFailureType = fessConfig.getIgnoreFailureType();

        if (failureCount < 0) {
            return Collections.emptyList();
        }

        final int count = failureCount;
        final ListResultBean<FailureUrl> list = ComponentUtil.getComponent(FailureUrlBhv.class).selectList(cb -> {
            cb.query().setConfigId_Equal(configId);
            cb.query().setErrorCount_GreaterEqual(count);
            cb.fetchFirst(fessConfig.getPageFailureUrlMaxFetchSizeAsInteger());
        });
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        Pattern pattern = null;
        if (StringUtil.isNotBlank(ignoreFailureType)) {
            pattern = Pattern.compile(ignoreFailureType);
        }
        final List<String> urlList = new ArrayList<>();
        for (final FailureUrl failureUrl : list) {
            if (pattern != null) {
                if (!pattern.matcher(failureUrl.getErrorName()).matches()) {
                    urlList.add(failureUrl.getUrl());
                }
            } else {
                urlList.add(failureUrl.getUrl());
            }
        }
        return urlList;
    }

    public OptionalEntity<CrawlingConfig> getDefaultConfig(final ConfigType configType) {
        final String name = ComponentUtil.getFessConfig().getFormAdminDefaultTemplateName();

        return switch (configType) {
        case WEB -> {
            final WebConfigService webConfigService = ComponentUtil.getComponent(WebConfigService.class);
            yield webConfigService.getWebConfigByName(name).map(o -> (CrawlingConfig) o);
        }
        case FILE -> {
            final FileConfigService fileConfigService = ComponentUtil.getComponent(FileConfigService.class);
            yield fileConfigService.getFileConfigByName(name).map(o -> (CrawlingConfig) o);
        }
        case DATA -> {
            final DataConfigService dataConfigService = ComponentUtil.getComponent(DataConfigService.class);
            yield dataConfigService.getDataConfigByName(name).map(o -> (CrawlingConfig) o);
        }
        default -> OptionalEntity.empty();
        };
    }
}
