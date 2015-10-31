/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.user.allcommon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.exception.InvalidQueryRegisteredException;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.IdsFilterBuilder;
import org.elasticsearch.index.query.MatchAllFilterBuilder;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.PrefixFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.ScriptFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class EsAbstractConditionFilter {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<FilterBuilder> filterBuilderList;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    public boolean hasFilters() {
        return filterBuilderList != null && !filterBuilderList.isEmpty();
    }

    public FilterBuilder getFilter() {
        if (filterBuilderList == null) {
            return null;
        } else if (filterBuilderList.size() == 1) {
            return filterBuilderList.get(0);
        }
        return FilterBuilders.andFilter(filterBuilderList.toArray(new FilterBuilder[filterBuilderList.size()]));
    }

    public void addFilter(FilterBuilder filterBuilder) {
        regF(filterBuilder);
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public void setIds_Equal(Collection<String> idList) {
        checkEsInvalidQueryCollection("ids", idList);
        doSetIds_Equal(idList, null);
    }

    public void setIds_Equal(Collection<String> idList, ConditionOptionCall<IdsFilterBuilder> opLambda) {
        checkEsInvalidQueryCollection("ids", idList);
        assertObjectNotNull("opLambda", opLambda);
        doSetIds_Equal(idList, opLambda);
    }

    protected void doSetIds_Equal(Collection<String> idList, ConditionOptionCall<IdsFilterBuilder> opLambda) {
        IdsFilterBuilder builder = regIdsF(idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void matchAll() {
        doMatchAll(null);
    }

    public void matchAll(ConditionOptionCall<MatchAllFilterBuilder> opLambda) {
        assertObjectNotNull("opLambda", opLambda);
        doMatchAll(opLambda);
    }

    protected void doMatchAll(ConditionOptionCall<MatchAllFilterBuilder> opLambda) {
        MatchAllFilterBuilder builder = FilterBuilders.matchAllFilter();
        regF(builder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScript(String script) {
        checkEsInvalidQuery("script", script);
        doSetScript(script, null);
    }

    public void setScript(String script, ConditionOptionCall<ScriptFilterBuilder> opLambda) {
        checkEsInvalidQuery("script", script);
        assertObjectNotNull("opLambda", opLambda);
        doSetScript(script, opLambda);
    }

    protected void doSetScript(String script, ConditionOptionCall<ScriptFilterBuilder> opLambda) {
        ScriptFilterBuilder builder = regScriptF(script);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    // ===================================================================================
    //                                                                            Register
    //                                                                            ========
    protected void regF(FilterBuilder builder) {
        assertObjectNotNull("builder", builder);
        if (filterBuilderList == null) {
            filterBuilderList = new ArrayList<>();
        }
        filterBuilderList.add(builder);
    }

    protected TermFilterBuilder regTermF(String name, Object value) {
        checkEsInvalidQuery(name, value);
        TermFilterBuilder termFilter = FilterBuilders.termFilter(name, value);
        regF(termFilter);
        return termFilter;
    }

    protected TermsFilterBuilder regTermsF(String name, Collection<?> values) {
        checkEsInvalidQueryCollection(name, values);
        TermsFilterBuilder termsFilter = FilterBuilders.termsFilter(name, values);
        regF(termsFilter);
        return termsFilter;
    }

    protected PrefixFilterBuilder regPrefixF(String name, String value) {
        checkEsInvalidQuery(name, value);
        PrefixFilterBuilder prefixFilter = FilterBuilders.prefixFilter(name, value);
        regF(prefixFilter);
        return prefixFilter;
    }

    protected ExistsFilterBuilder regExistsF(String name) {
        ExistsFilterBuilder existsFilter = FilterBuilders.existsFilter(name);
        regF(existsFilter);
        return existsFilter;
    }

    protected MissingFilterBuilder regMissingF(String name) {
        MissingFilterBuilder missingFilter = FilterBuilders.missingFilter(name);
        regF(missingFilter);
        return missingFilter;
    }

    protected RangeFilterBuilder regRangeF(String name, ConditionKey ck, Object value) {
        checkEsInvalidQuery(name, value);
        assertObjectNotNull("ck", ck);
        for (FilterBuilder builder : filterBuilderList) {
            if (builder instanceof RangeFilterBuilder) {
                RangeFilterBuilder rangeFilterBuilder = (RangeFilterBuilder) builder;
                if (rangeFilterBuilder.toString().replaceAll("\\s", "").startsWith("{\"range\":{\"" + name + "\"")) {
                    addRangeC(rangeFilterBuilder, ck, value);
                    return rangeFilterBuilder;
                }
            }
        }
        RangeFilterBuilder rangeFilterBuilder = FilterBuilders.rangeFilter(name);
        addRangeC(rangeFilterBuilder, ck, value);
        regF(rangeFilterBuilder);
        return rangeFilterBuilder;
    }

    protected void addRangeC(RangeFilterBuilder builder, ConditionKey ck, Object value) {
        assertObjectNotNull("builder", builder);
        assertObjectNotNull("ck", ck);
        if (ck.equals(ConditionKey.CK_GREATER_THAN)) {
            builder.gt(value);
        } else if (ck.equals(ConditionKey.CK_GREATER_EQUAL)) {
            builder.gte(value);
        } else if (ck.equals(ConditionKey.CK_LESS_THAN)) {
            builder.lt(value);
        } else if (ck.equals(ConditionKey.CK_LESS_EQUAL)) {
            builder.lte(value);
        }
    }

    protected ScriptFilterBuilder regScriptF(String script) {
        checkEsInvalidQuery("script", script);
        ScriptFilterBuilder scriptFilter = FilterBuilders.scriptFilter(script);
        regF(scriptFilter);
        return scriptFilter;
    }

    protected IdsFilterBuilder regIdsF(Collection<?> values) {
        checkEsInvalidQueryCollection("idsF", values);
        IdsFilterBuilder idsFilter = FilterBuilders.idsFilter(values.toArray(new String[values.size()]));
        regF(idsFilter);
        return idsFilter;
    }

    protected BoolFilterBuilder regBoolF(List<FilterBuilder> mustList, List<FilterBuilder> shouldList, List<FilterBuilder> mustNotList) {
        assertObjectNotNull("mustList", mustList);
        assertObjectNotNull("shouldList", shouldList);
        assertObjectNotNull("mustNotList", mustNotList);
        BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();
        mustList.forEach(query -> {
            boolFilter.must(query);
        });
        shouldList.forEach(query -> {
            boolFilter.should(query);
        });
        mustNotList.forEach(query -> {
            boolFilter.mustNot(query);
        });
        return boolFilter;
    }

    protected AndFilterBuilder regAndF(List<FilterBuilder> filterList) {
        assertObjectNotNull("filterList", filterList);
        AndFilterBuilder andFilter = FilterBuilders.andFilter(filterList.toArray(new FilterBuilder[filterList.size()]));
        regF(andFilter);
        return andFilter;
    }

    protected OrFilterBuilder regOrF(List<FilterBuilder> filterList) {
        assertObjectNotNull("filterList", filterList);
        OrFilterBuilder andFilter = FilterBuilders.orFilter(filterList.toArray(new FilterBuilder[filterList.size()]));
        regF(andFilter);
        return andFilter;
    }

    protected NotFilterBuilder regNotF(FilterBuilder filter) {
        assertObjectNotNull("filter", filter);
        NotFilterBuilder notFilter = FilterBuilders.notFilter(filter);
        regF(notFilter);
        return notFilter;
    }

    protected QueryFilterBuilder regQueryF(QueryBuilder filter) {
        assertObjectNotNull("filter", filter);
        QueryFilterBuilder queryFilter = FilterBuilders.queryFilter(filter);
        regF(queryFilter);
        return queryFilter;
    }

    // ===================================================================================
    //                                                                       Invalid Query
    //                                                                       =============
    protected void checkEsInvalidQuery(String name, Object value) {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            String msg = "Cannot register null or empty query: name=" + name + " value=" + value;
            throw new InvalidQueryRegisteredException(msg);
        }
    }

    protected void checkEsInvalidQueryCollection(String name, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            String msg = "Cannot register null or empty query collection: name=" + name + " values=" + values;
            throw new InvalidQueryRegisteredException(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                        Assist Class
    //                                                                        ============
    @FunctionalInterface
    public interface ConditionOptionCall<OP extends FilterBuilder> {

        /**
         * @param op The option of condition to be set up. (NotNull)
         */
        void callback(OP op);
    }

    @FunctionalInterface
    public interface BoolCall<CF extends EsAbstractConditionFilter> {

        void callback(CF must, CF should, CF mustNot);
    }

    @FunctionalInterface
    public interface OperatorCall<CF extends EsAbstractConditionFilter> {

        void callback(CF and);
    }
}
