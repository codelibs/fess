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
package org.codelibs.fess.query;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.lang.Character.UnicodeBlock;

import org.apache.lucene.search.Query;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.util.LaRequestUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.sort.SortBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

public abstract class QueryCommand {

    public abstract QueryBuilder execute(final QueryContext context, final Query query, final float boost);

    protected abstract String getQueryClassName();

    public void register() {
        ComponentUtil.getQueryProcessor().add(getQueryClassName(), this);
    }

    protected QueryFieldConfig getQueryFieldConfig() {
        return ComponentUtil.getQueryFieldConfig();
    }

    protected QueryProcessor getQueryProcessor() {
        return ComponentUtil.getQueryProcessor();
    }

    protected SortBuilder<?> createFieldSortBuilder(final String field, final SortOrder order) {
        if (QueryFieldConfig.SCORE_FIELD.equals(field) || QueryFieldConfig.DOC_SCORE_FIELD.equals(field)) {
            return SortBuilders.scoreSort().order(order);
        }
        return SortBuilders.fieldSort(field).order(order);
    }

    protected boolean isSearchField(final String field) {
        for (final String searchField : getQueryFieldConfig().searchFields) {
            if (searchField.equals(field)) {
                return true;
            }
        }
        return false;
    }

    protected OptionalThing<String[]> getQueryLanguages() {
        return LaRequestUtil.getOptionalRequest().map(request -> ComponentUtil.getFessConfig().getQueryLanguages(request.getLocales(),
                (String[]) request.getAttribute(Constants.REQUEST_LANGUAGES)));
    }

    protected BoolQueryBuilder buildDefaultQueryBuilder(final FessConfig fessConfig, final QueryContext context,
            final DefaultQueryBuilderFunction builder) {
        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(builder.apply(fessConfig.getIndexFieldTitle(), fessConfig.getQueryBoostTitleAsDecimal().floatValue()));
        boolQuery.should(builder.apply(fessConfig.getIndexFieldContent(), fessConfig.getQueryBoostContentAsDecimal().floatValue()));
        final float importantContentBoost = fessConfig.getQueryBoostImportantContentAsDecimal().floatValue();
        if (importantContentBoost >= 0.0f) {
            boolQuery.should(builder.apply(fessConfig.getIndexFieldImportantContent(), importantContentBoost));
        }
        final float importantContantLangBoost = fessConfig.getQueryBoostImportantContentLangAsDecimal().floatValue();
        getQueryLanguages().ifPresent(langs -> stream(langs).of(stream -> stream.forEach(lang -> {
            boolQuery.should(
                    builder.apply(fessConfig.getIndexFieldTitle() + "_" + lang, fessConfig.getQueryBoostTitleLangAsDecimal().floatValue()));
            boolQuery.should(builder.apply(fessConfig.getIndexFieldContent() + "_" + lang,
                    fessConfig.getQueryBoostContentLangAsDecimal().floatValue()));
            if (importantContantLangBoost >= 0.0f) {
                boolQuery.should(builder.apply(fessConfig.getIndexFieldImportantContent() + "_" + lang, importantContantLangBoost));
            }
        })));
        getQueryFieldConfig().additionalDefaultList.stream().forEach(f -> {
            final QueryBuilder query = builder.apply(f.getFirst(), f.getSecond());
            boolQuery.should(query);
        });
        return boolQuery;
    }

    protected QueryBuilder buildMatchPhraseQuery(final String f, final String text) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (text == null || text.length() != 1
                || (!fessConfig.getIndexFieldTitle().equals(f) && !fessConfig.getIndexFieldContent().equals(f))) {
            return QueryBuilders.matchPhraseQuery(f, text);
        }

        final UnicodeBlock block = UnicodeBlock.of(text.codePointAt(0));
        if (block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS //
                || block == UnicodeBlock.HIRAGANA //
                || block == UnicodeBlock.KATAKANA //
                || block == UnicodeBlock.HANGUL_SYLLABLES //
        ) {
            return QueryBuilders.prefixQuery(f, text);
        }
        return QueryBuilders.matchPhraseQuery(f, text);
    }

    protected String getSearchField(final String defaultField, final String field) {
        if (Constants.DEFAULT_FIELD.equals(field) && defaultField != null) {
            return defaultField;
        }
        return field;
    }

    protected interface DefaultQueryBuilderFunction {
        QueryBuilder apply(String field, float boost);
    }
}
