/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exbhv.DataConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.FailureUrlBhv;
import org.codelibs.fess.opensearch.config.exbhv.FileConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.WebConfigBhv;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigType;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for managing crawling configurations.
 * Provides functionality to store, retrieve, and manage different types of crawling configurations
 * including web, file, and data configurations. Supports caching and session-based configuration management.
 */
public class CrawlingConfigHelper {

    /**
     * Creates a new instance of CrawlingConfigHelper.
     */
    public CrawlingConfigHelper() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(CrawlingConfigHelper.class);

    /**
     * Map storing crawling configurations by session ID.
     */
    protected final Map<String, CrawlingConfig> crawlingConfigMap = new ConcurrentHashMap<>();

    /**
     * Counter for generating unique session identifiers.
     */
    protected int count = 1;

    /**
     * Cache for storing crawling configurations to improve performance.
     */
    protected Cache<String, CrawlingConfig> crawlingConfigCache;

    /**
     * Initializes the CrawlingConfigHelper by setting up the crawling configuration cache.
     * This method is called automatically after the bean construction is complete.
     * The cache is configured with a maximum size of 100 entries and expires after 10 minutes.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        crawlingConfigCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    /**
     * Determines the configuration type from a given config ID.
     * The config type is identified by the first character of the config ID.
     *
     * @param configId the configuration ID to analyze
     * @return the ConfigType (WEB, FILE, or DATA) or null if the config ID is invalid or doesn't match any known type
     */
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

    /**
     * Extracts the actual ID from a config ID by removing the type prefix.
     * Config IDs are formatted as [type_prefix][actual_id], so this method
     * returns everything after the first character.
     *
     * @param configId the configuration ID to process
     * @return the actual ID without the type prefix, or null if the config ID is invalid
     */
    protected String getId(final String configId) {
        if (configId == null || configId.length() < 2) {
            return null;
        }
        return configId.substring(1);
    }

    /**
     * Retrieves a crawling configuration by its config ID.
     * This method uses caching to improve performance and automatically determines
     * the configuration type and delegates to the appropriate service.
     *
     * @param configId the configuration ID to retrieve
     * @return the CrawlingConfig object or null if not found or on error
     */
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

    /**
     * Retrieves the pipeline configuration parameter for a given config ID.
     * The pipeline parameter is extracted from the crawling configuration's parameter map.
     *
     * @param configId the configuration ID to get the pipeline for
     * @return an OptionalThing containing the pipeline string if found, or empty if not found or blank
     */
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

    /**
     * Refreshes the crawling configuration cache by invalidating all cached entries.
     * This forces the next access to reload configurations from the underlying services.
     */
    public void refresh() {
        crawlingConfigCache.invalidateAll();
    }

    /**
     * Stores a crawling configuration in the session-based storage with a unique identifier.
     * The generated session count ID combines the session ID with an incrementing counter.
     *
     * @param sessionId the session identifier
     * @param crawlingConfig the crawling configuration to store
     * @return the unique session count ID that can be used to retrieve the stored configuration
     */
    public synchronized String store(final String sessionId, final CrawlingConfig crawlingConfig) {
        final String sessionCountId = sessionId + "-" + count;
        crawlingConfigMap.put(sessionCountId, crawlingConfig);
        count++;
        return sessionCountId;
    }

    /**
     * Removes a stored crawling configuration from the session-based storage.
     *
     * @param sessionCountId the session count ID of the configuration to remove
     */
    public void remove(final String sessionCountId) {
        crawlingConfigMap.remove(sessionCountId);
    }

    /**
     * Retrieves a stored crawling configuration from the session-based storage.
     *
     * @param sessionCountId the session count ID of the configuration to retrieve
     * @return the stored CrawlingConfig or null if not found
     */
    public CrawlingConfig get(final String sessionCountId) {
        return crawlingConfigMap.get(sessionCountId);
    }

    /**
     * Retrieves all available web crawling configurations.
     * This is a convenience method that calls the overloaded version with default parameters
     * (withLabelType=true, withRoleType=true, available=true, idList=null).
     *
     * @return a list of all available WebConfig objects
     */
    public List<WebConfig> getAllWebConfigList() {
        return getAllWebConfigList(true, true, true, null);
    }

    /**
     * Retrieves web crawling configurations filtered by a list of IDs.
     * If the ID list is null, returns all available configurations.
     *
     * @param idList the list of configuration IDs to retrieve, or null for all configurations
     * @return a list of WebConfig objects with the specified IDs
     */
    public List<WebConfig> getWebConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllWebConfigList();
        }
        return getAllWebConfigList(true, true, false, idList);
    }

    /**
     * Retrieves web crawling configurations with various filtering options.
     *
     * @param withLabelType whether to include label type information (currently not used in implementation)
     * @param withRoleType whether to include role type information (currently not used in implementation)
     * @param available whether to filter only available configurations
     * @param idList the list of configuration IDs to retrieve, or null for no ID filtering
     * @return a list of WebConfig objects matching the criteria
     */
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

    /**
     * Retrieves all available file crawling configurations.
     * This is a convenience method that calls the overloaded version with default parameters
     * (withLabelType=true, withRoleType=true, available=true, idList=null).
     *
     * @return a list of all available FileConfig objects
     */
    public List<FileConfig> getAllFileConfigList() {
        return getAllFileConfigList(true, true, true, null);
    }

    /**
     * Retrieves file crawling configurations filtered by a list of IDs.
     * If the ID list is null, returns all available configurations.
     *
     * @param idList the list of configuration IDs to retrieve, or null for all configurations
     * @return a list of FileConfig objects with the specified IDs
     */
    public List<FileConfig> getFileConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllFileConfigList();
        }
        return getAllFileConfigList(true, true, false, idList);
    }

    /**
     * Retrieves file crawling configurations with various filtering options.
     *
     * @param withLabelType whether to include label type information (currently not used in implementation)
     * @param withRoleType whether to include role type information (currently not used in implementation)
     * @param available whether to filter only available configurations
     * @param idList the list of configuration IDs to retrieve, or null for no ID filtering
     * @return a list of FileConfig objects matching the criteria
     */
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

    /**
     * Retrieves all available data crawling configurations.
     * This is a convenience method that calls the overloaded version with default parameters
     * (withLabelType=true, withRoleType=true, available=true, idList=null).
     *
     * @return a list of all available DataConfig objects
     */
    public List<DataConfig> getAllDataConfigList() {
        return getAllDataConfigList(true, true, true, null);
    }

    /**
     * Retrieves data crawling configurations filtered by a list of IDs.
     * If the ID list is null, returns all available configurations.
     *
     * @param idList the list of configuration IDs to retrieve, or null for all configurations
     * @return a list of DataConfig objects with the specified IDs
     */
    public List<DataConfig> getDataConfigListByIds(final List<String> idList) {
        if (idList == null) {
            return getAllDataConfigList();
        }
        return getAllDataConfigList(true, true, false, idList);
    }

    /**
     * Retrieves data crawling configurations with various filtering options.
     *
     * @param withLabelType whether to include label type information (currently not used in implementation)
     * @param withRoleType whether to include role type information (currently not used in implementation)
     * @param available whether to filter only available configurations
     * @param idList the list of configuration IDs to retrieve, or null for no ID filtering
     * @return a list of DataConfig objects matching the criteria
     */
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

    /**
     * Retrieves a list of URLs that should be excluded from crawling based on failure counts.
     * URLs are excluded if they have failed more than the configured failure count threshold.
     * URLs can also be filtered by failure type using a regular expression pattern.
     *
     * @param configId the configuration ID to get excluded URLs for
     * @return a list of URLs that should be excluded from crawling, or an empty list if none
     */
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

    /**
     * Retrieves the default crawling configuration template for a given configuration type.
     * The default template is identified by the configured form admin default template name.
     *
     * @param configType the type of configuration (WEB, FILE, or DATA)
     * @return an OptionalEntity containing the default CrawlingConfig if found, or empty if not found or configType is null
     */
    public OptionalEntity<CrawlingConfig> getDefaultConfig(final ConfigType configType) {
        if (configType == null) {
            return OptionalEntity.empty();
        }

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
