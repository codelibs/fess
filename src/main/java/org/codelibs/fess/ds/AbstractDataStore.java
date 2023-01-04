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
package org.codelibs.fess.ds;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public abstract class AbstractDataStore implements DataStore {

    private static final Logger logger = LogManager.getLogger(AbstractDataStore.class);

    protected static final String SCRIPT_TYPE = "script_type";

    public String mimeType = "application/datastore";

    protected boolean alive = true;

    public void register() {
        ComponentUtil.getDataStoreFactory().add(getName(), this);
    }

    protected abstract String getName();

    @Override
    public void stop() {
        alive = false;
    }

    @Override
    public void store(final DataConfig config, final IndexUpdateCallback callback, final DataStoreParams initParamMap) {
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final Date documentExpires = crawlingInfoHelper.getDocumentExpires(config);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, String> paramEnvMap = systemHelper.getFilteredEnvMap(fessConfig.getCrawlerDataEnvParamKeyPattern());
        final Map<String, String> configParamMap = config.getHandlerParameterMap().entrySet().stream().map(e -> {
            final String key = e.getKey();
            String value = e.getValue();
            for (final Map.Entry<String, String> entry : paramEnvMap.entrySet()) {
                value = value.replace("${" + entry.getKey() + "}", entry.getValue());
            }
            return new Pair<>(key, value);
        }).collect(Collectors.toMap(Pair<String, String>::getFirst, Pair<String, String>::getSecond));
        final Map<String, String> configScriptMap = config.getHandlerScriptMap();

        initParamMap.putAll(configParamMap);
        final DataStoreParams paramMap = initParamMap;

        // default values
        final Map<String, Object> defaultDataMap = new HashMap<>();

        // cid
        final String configId = config.getConfigId();
        if (configId != null) {
            defaultDataMap.put(fessConfig.getIndexFieldConfigId(), configId);
        }
        //  expires
        if (documentExpires != null) {
            defaultDataMap.put(fessConfig.getIndexFieldExpires(), documentExpires);
        }
        // segment
        defaultDataMap.put(fessConfig.getIndexFieldSegment(), initParamMap.getAsString(Constants.SESSION_ID));
        // created
        defaultDataMap.put(fessConfig.getIndexFieldCreated(), systemHelper.getCurrentTime());
        // boost
        defaultDataMap.put(fessConfig.getIndexFieldBoost(), config.getBoost().toString());
        // label: labelType
        // role: roleType
        final List<String> roleTypeList = new ArrayList<>();
        stream(config.getPermissions()).of(stream -> stream.forEach(p -> roleTypeList.add(p)));
        defaultDataMap.put(fessConfig.getIndexFieldRole(), roleTypeList);
        // mimetype
        defaultDataMap.put(fessConfig.getIndexFieldMimetype(), mimeType);
        // title
        // content
        // cache
        // digest
        // host
        // site
        // url
        // anchor
        // content_length
        // last_modified
        // id
        // virtual_host
        defaultDataMap.put(fessConfig.getIndexFieldVirtualHost(),
                stream(config.getVirtualHosts()).get(stream -> stream.filter(StringUtil::isNotBlank).collect(Collectors.toList())));

        storeData(config, callback, paramMap.newInstance(), configScriptMap, defaultDataMap);

    }

    protected String getScriptType(final DataStoreParams paramMap) {
        final String value = paramMap.getAsString(SCRIPT_TYPE);
        if (StringUtil.isBlank(value)) {
            return Constants.DEFAULT_SCRIPT;
        }
        return value;
    }

    protected Object convertValue(final String scriptType, final String template, final Map<String, Object> paramMap) {
        if (StringUtil.isEmpty(template)) {
            return StringUtil.EMPTY;
        }

        if (paramMap.containsKey(template)) {
            return paramMap.get(template);
        }

        return ComponentUtil.getScriptEngineFactory().getScriptEngine(scriptType).evaluate(template, paramMap);
    }

    protected long getReadInterval(final DataStoreParams paramMap) {
        long readInterval = 0;
        final String value = paramMap.getAsString("readInterval");
        if (StringUtil.isNotBlank(value)) {
            try {
                readInterval = Long.parseLong(value);
            } catch (final NumberFormatException e) {
                logger.warn("Invalid read interval: {}", value);
            }
        }
        return readInterval;
    }

    protected void sleep(final long interval) {
        ThreadUtil.sleepQuietly(interval);
    }

    protected abstract void storeData(DataConfig dataConfig, IndexUpdateCallback callback, DataStoreParams paramMap,
            Map<String, String> scriptMap, Map<String, Object> defaultDataMap);
}
