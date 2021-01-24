/*
 * Copyright 2012-2021 CodeLibs Project and the Others.
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
import org.codelibs.fess.Constants;
import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.GroovyUtil;

public abstract class AbstractDataStore implements DataStore {

    private static final Logger logger = LogManager.getLogger(AbstractDataStore.class);

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
    public void store(final DataConfig config, final IndexUpdateCallback callback, final Map<String, String> initParamMap) {
        final Map<String, String> configParamMap = config.getHandlerParameterMap();
        final Map<String, String> configScriptMap = config.getHandlerScriptMap();
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final Date documentExpires = crawlingInfoHelper.getDocumentExpires(config);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        initParamMap.putAll(configParamMap);
        final Map<String, String> paramMap = initParamMap;

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
        defaultDataMap.put(fessConfig.getIndexFieldSegment(), initParamMap.get(Constants.SESSION_ID));
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

        storeData(config, callback, paramMap, configScriptMap, defaultDataMap);

    }

    protected Object convertValue(final String template, final Map<String, Object> paramMap) {
        if (StringUtil.isEmpty(template)) {
            return StringUtil.EMPTY;
        }

        if (paramMap.containsKey(template)) {
            return paramMap.get(template);
        }

        return GroovyUtil.evaluate(template, paramMap);
    }

    protected long getReadInterval(final Map<String, String> paramMap) {
        long readInterval = 0;
        final String value = paramMap.get("readInterval");
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

    protected abstract void storeData(DataConfig dataConfig, IndexUpdateCallback callback, Map<String, String> paramMap,
            Map<String, String> scriptMap, Map<String, Object> defaultDataMap);
}
