/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.ds.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.ds.DataStore;
import jp.sf.fess.ds.IndexUpdateCallback;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.fess.helper.FieldHelper;
import jp.sf.fess.taglib.FessFunctions;
import jp.sf.fess.util.ComponentUtil;

import org.codelibs.core.util.StringUtil;
import org.seasar.framework.util.OgnlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataStoreImpl implements DataStore {

    private static final Logger logger = LoggerFactory
            .getLogger(AbstractDataStoreImpl.class);

    public String mimeType = "application/datastore";

    protected boolean alive = true;

    @Override
    public void stop() {
        alive = false;
    }

    @Override
    public void store(final DataCrawlingConfig config,
            final IndexUpdateCallback callback,
            final Map<String, String> initParamMap) {
        final Map<String, String> configParamMap = config
                .getHandlerParameterMap();
        final Map<String, String> configScriptMap = config
                .getHandlerScriptMap();
        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil
                .getCrawlingSessionHelper();
        final Date documentExpires = crawlingSessionHelper.getDocumentExpires();
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();

        initParamMap.putAll(configParamMap);
        final Map<String, String> paramMap = initParamMap;

        // default values
        final Map<String, Object> defaultDataMap = new HashMap<String, Object>();

        // cid
        final String configId = config.getConfigId();
        if (configId != null) {
            defaultDataMap.put(fieldHelper.configIdField, configId);
        }
        //  expires
        if (documentExpires != null) {
            defaultDataMap.put(fieldHelper.expiresField,
                    FessFunctions.formatDate(documentExpires));
        }
        // segment
        defaultDataMap.put(fieldHelper.segmentField,
                initParamMap.get(Constants.SESSION_ID));
        // created
        defaultDataMap.put(fieldHelper.createdField, "NOW");
        // boost
        defaultDataMap
                .put(fieldHelper.boostField, config.getBoost().toString());
        // label: labelType
        final List<String> labelTypeList = new ArrayList<String>();
        for (final String labelType : config.getLabelTypeValues()) {
            labelTypeList.add(labelType);
        }
        defaultDataMap.put(fieldHelper.labelField, labelTypeList);
        // role: roleType
        final List<String> roleTypeList = new ArrayList<String>();
        for (final String roleType : config.getRoleTypeValues()) {
            roleTypeList.add(roleType);
        }
        defaultDataMap.put(fieldHelper.roleField, roleTypeList);
        // mimetype
        defaultDataMap.put(fieldHelper.mimetypeField, mimeType);
        // title
        // content
        // cache
        // digest
        // host
        // site
        // url
        // anchor
        // contentLength
        // lastModified
        // id

        storeData(config, callback, paramMap, configScriptMap, defaultDataMap);

    }

    protected Object convertValue(final String template,
            final Map<String, String> paramMap) {
        if (StringUtil.isEmpty(template)) {
            return StringUtil.EMPTY;
        }

        try {
            final Object exp = OgnlUtil.parseExpression(template);
            final Object value = OgnlUtil.getValue(exp, paramMap);
            if (value == null) {
                return null;
            }
            return value;
        } catch (final Exception e) {
            logger.warn("Invalid value format: " + template, e);
            return null;
        }
    }

    protected long getReadInterval(final Map<String, String> paramMap) {
        long readInterval = 0;
        final String value = paramMap.get("readInterval");
        if (StringUtil.isNotBlank(value)) {
            try {
                readInterval = Long.parseLong(value);
            } catch (final NumberFormatException e) {
                logger.warn("Invalid read interval: " + value);
            }
        }
        return readInterval;
    }

    protected void sleep(final long interval) {
        try {
            Thread.sleep(interval);
        } catch (final Exception e) {
        }
    }

    protected abstract void storeData(DataCrawlingConfig dataConfig,
            IndexUpdateCallback callback, Map<String, String> paramMap,
            Map<String, String> scriptMap, Map<String, Object> defaultDataMap);
}
