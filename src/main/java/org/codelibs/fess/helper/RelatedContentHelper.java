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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.es.config.exbhv.RelatedContentBhv;
import org.codelibs.fess.es.config.exentity.RelatedContent;
import org.codelibs.fess.util.ComponentUtil;

public class RelatedContentHelper extends AbstractConfigHelper {

    private static final Logger logger = LogManager.getLogger(RelatedContentHelper.class);

    protected Map<String, Pair<Map<String, String>, List<Pair<Pattern, String>>>> relatedContentMap = Collections.emptyMap();

    protected String regexPrefix = "regex:";

    protected String queryPlaceHolder = "__QUERY__";

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        load();
    }

    public List<RelatedContent> getAvailableRelatedContentList() {
        return ComponentUtil.getComponent(RelatedContentBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Term_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageRelatedcontentMaxFetchSizeAsInteger());
        });
    }

    @Override
    public int load() {
        final Map<String, Pair<Map<String, String>, List<Pair<Pattern, String>>>> relatedContentMap = new HashMap<>();
        getAvailableRelatedContentList().stream().forEach(entity -> {
            final String key = getHostKey(entity);
            Pair<Map<String, String>, List<Pair<Pattern, String>>> pair = relatedContentMap.get(key);
            if (pair == null) {
                pair = new Pair<>(new HashMap<>(), new ArrayList<>());
                relatedContentMap.put(key, pair);
            }
            if (entity.getTerm().startsWith(regexPrefix)) {
                final String regex = entity.getTerm().substring(regexPrefix.length());
                if (StringUtil.isBlank(regex)) {
                    logger.warn("Unknown regex pattern: {}", entity.getTerm());
                } else {
                    pair.getSecond().add(new Pair<>(Pattern.compile(regex), entity.getContent()));
                }
            } else {
                pair.getFirst().put(toLowerCase(entity.getTerm()), entity.getContent());
            }
        });
        this.relatedContentMap = relatedContentMap;
        return relatedContentMap.size();
    }

    protected String getHostKey(final RelatedContent entity) {
        final String key = entity.getVirtualHost();
        return StringUtil.isBlank(key) ? StringUtil.EMPTY : key;
    }

    public String[] getRelatedContents(final String query) {
        final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        final Pair<Map<String, String>, List<Pair<Pattern, String>>> pair = relatedContentMap.get(key);
        if (pair != null) {
            final List<String> contentList = new ArrayList<>();
            final String content = pair.getFirst().get(toLowerCase(query));
            if (StringUtil.isNotBlank(content)) {
                contentList.add(content);
            }
            for (final Pair<Pattern, String> regexData : pair.getSecond()) {
                if (regexData.getFirst().matcher(query).matches()) {
                    contentList.add(regexData.getSecond().replace(queryPlaceHolder, query));
                }
            }
            return contentList.toArray(new String[contentList.size()]);
        }
        return StringUtil.EMPTY_STRINGS;
    }

    private String toLowerCase(final String term) {
        return term != null ? term.toLowerCase(Locale.ROOT) : term;
    }

    public void setRegexPrefix(final String regexPrefix) {
        this.regexPrefix = regexPrefix;
    }

    public void setQueryPlaceHolder(final String queryPlaceHolder) {
        this.queryPlaceHolder = queryPlaceHolder;
    }

}
