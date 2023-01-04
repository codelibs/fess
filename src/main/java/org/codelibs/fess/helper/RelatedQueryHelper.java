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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.config.exbhv.RelatedQueryBhv;
import org.codelibs.fess.es.config.exentity.RelatedQuery;
import org.codelibs.fess.util.ComponentUtil;

public class RelatedQueryHelper extends AbstractConfigHelper {
    private static final Logger logger = LogManager.getLogger(RelatedQueryHelper.class);

    protected volatile Map<String, Map<String, String[]>> relatedQueryMap = Collections.emptyMap();

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        load();
    }

    public List<RelatedQuery> getAvailableRelatedQueryList() {

        return ComponentUtil.getComponent(RelatedQueryBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Term_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageRelatedqueryMaxFetchSizeAsInteger());
        });
    }

    @Override
    public int load() {
        final Map<String, Map<String, String[]>> relatedQueryMap = new HashMap<>();
        getAvailableRelatedQueryList().stream().forEach(entity -> {
            final String key = getHostKey(entity);
            Map<String, String[]> map = relatedQueryMap.get(key);
            if (map == null) {
                map = new HashMap<>();
                relatedQueryMap.put(key, map);
            }
            map.put(toLowerCase(entity.getTerm()), entity.getQueries());
        });
        this.relatedQueryMap = relatedQueryMap;
        return relatedQueryMap.size();
    }

    protected String getHostKey(final RelatedQuery entity) {
        final String key = entity.getVirtualHost();
        return StringUtil.isBlank(key) ? StringUtil.EMPTY : key;
    }

    public String[] getRelatedQueries(final String query) {
        final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        final Map<String, String[]> map = relatedQueryMap.get(key);
        if (map != null) {
            final String[] queries = map.get(toLowerCase(query));
            if (queries != null) {
                return queries;
            }
        }
        return StringUtil.EMPTY_STRINGS;
    }

    private String toLowerCase(final String term) {
        return term != null ? term.toLowerCase(Locale.ROOT) : term;
    }

}
