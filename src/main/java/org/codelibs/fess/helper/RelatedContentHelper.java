/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.es.config.exbhv.RelatedContentBhv;
import org.codelibs.fess.es.config.exentity.RelatedContent;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelatedContentHelper {

    private static final Logger logger = LoggerFactory.getLogger(RelatedContentHelper.class);

    protected volatile Map<String, Pair<Map<String, String>, List<Pair<Pattern, String>>>> relatedContentMap = Collections.emptyMap();

    protected String regexPrefix = "regex:";

    protected String queryPlaceHolder = "__QUERY__";

    @PostConstruct
    public void init() {
        reload();
    }

    public void update() {
        reload();
    }

    public List<RelatedContent> getAvailableRelatedContentList() {

        return ComponentUtil.getComponent(RelatedContentBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Term_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageRelatedqueryMaxFetchSizeAsInteger());
        });
    }

    protected void reload() {
        final Map<String, Pair<Map<String, String>, List<Pair<Pattern, String>>>> relatedContentMap = new HashMap<>();
        getAvailableRelatedContentList().stream().forEach(entity -> {
            final String key = getHostKey(entity);
            Pair<Map<String, String>, List<Pair<Pattern, String>>> pair = relatedContentMap.get(key);
            if (pair == null) {
                pair = new Pair<>(new HashMap<>(), new ArrayList<>());
                relatedContentMap.put(key, pair);
            }
            if (entity.getTerm().startsWith(regexPrefix)) {
                String regex = entity.getTerm().substring(regexPrefix.length());
                if (StringUtil.isBlank(regex)) {
                    logger.warn("Unknown regex pattern: " + entity.getTerm());
                } else {
                    pair.getSecond().add(new Pair<>(Pattern.compile(regex), entity.getContent()));
                }
            } else {
                pair.getFirst().put(toLowerCase(entity.getTerm()), entity.getContent());
            }
        });
        this.relatedContentMap = relatedContentMap;
    }

    protected String getHostKey(final RelatedContent entity) {
        final String key = entity.getVirtualHost();
        return StringUtil.isBlank(key) ? StringUtil.EMPTY : key;
    }

    public String getRelatedContent(final String query) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String key = fessConfig.getVirtualHostKey();
        final Pair<Map<String, String>, List<Pair<Pattern, String>>> pair = relatedContentMap.get(key);
        if (pair != null) {
            final String content = pair.getFirst().get(toLowerCase(query));
            if (StringUtil.isNotBlank(content)) {
                return content;
            }
            for (final Pair<Pattern, String> regexData : pair.getSecond()) {
                if (regexData.getFirst().matcher(query).matches()) {
                    return regexData.getSecond().replace(queryPlaceHolder, query);
                }
            }
        }
        return StringUtil.EMPTY;
    }

    private String toLowerCase(final String term) {
        return term != null ? term.toLowerCase(Locale.ROOT) : term;
    }

    public void setRegexPrefix(String regexPrefix) {
        this.regexPrefix = regexPrefix;
    }

    public void setQueryPlaceHolder(String queryPlaceHolder) {
        this.queryPlaceHolder = queryPlaceHolder;
    }

}
