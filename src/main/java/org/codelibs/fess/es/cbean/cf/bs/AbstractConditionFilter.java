package org.codelibs.fess.es.cbean.cf.bs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dbflute.cbean.ckey.ConditionKey;
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

public class AbstractConditionFilter {
    protected List<FilterBuilder> filterBuilderList;

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

    public void setIds_Equal(Collection<String> idList) {
        setIds_Equal(idList, null);
    }

    public void setIds_Equal(Collection<String> idList, ConditionOptionCall<IdsFilterBuilder> opLambda) {
        IdsFilterBuilder builder = regIdsF(idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void matchAll() {
        matchAll(null);
    }

    public void matchAll(ConditionOptionCall<MatchAllFilterBuilder> opLambda) {
        MatchAllFilterBuilder builder = FilterBuilders.matchAllFilter();
        regF(builder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScript(String script) {
        setScript(script, null);
    }

    public void setScript(String script, ConditionOptionCall<ScriptFilterBuilder> opLambda) {
        ScriptFilterBuilder builder = regScriptF(script);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    protected void regF(FilterBuilder builder) {
        if (filterBuilderList == null) {
            filterBuilderList = new ArrayList<>();
        }
        filterBuilderList.add(builder);
    }

    protected TermFilterBuilder regTermF(String name, Object value) {
        TermFilterBuilder termFilter = FilterBuilders.termFilter(name, value);
        regF(termFilter);
        return termFilter;
    }

    protected TermsFilterBuilder regTermsF(String name, Collection<?> value) {
        TermsFilterBuilder termsFilter = FilterBuilders.termsFilter(name, value);
        regF(termsFilter);
        return termsFilter;
    }

    protected PrefixFilterBuilder regPrefixF(String name, String value) {
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
        ScriptFilterBuilder scriptFilter = FilterBuilders.scriptFilter(script);
        regF(scriptFilter);
        return scriptFilter;
    }

    protected IdsFilterBuilder regIdsF(Collection<?> value) {
        IdsFilterBuilder idsFilter = FilterBuilders.idsFilter(value.toArray(new String[value.size()]));
        regF(idsFilter);
        return idsFilter;
    }

    protected BoolFilterBuilder regBoolF(List<FilterBuilder> mustList, List<FilterBuilder> shouldList, List<FilterBuilder> mustNotList) {
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
        AndFilterBuilder andFilter = FilterBuilders.andFilter(filterList.toArray(new FilterBuilder[filterList.size()]));
        regF(andFilter);
        return andFilter;
    }

    protected OrFilterBuilder regOrF(List<FilterBuilder> filterList) {
        OrFilterBuilder andFilter = FilterBuilders.orFilter(filterList.toArray(new FilterBuilder[filterList.size()]));
        regF(andFilter);
        return andFilter;
    }

    protected NotFilterBuilder regNotF(FilterBuilder filter) {
        NotFilterBuilder notFilter = FilterBuilders.notFilter(filter);
        regF(notFilter);
        return notFilter;
    }

    protected QueryFilterBuilder regQueryF(QueryBuilder filter) {
        QueryFilterBuilder queryFilter = FilterBuilders.queryFilter(filter);
        regF(queryFilter);
        return queryFilter;
    }

    @FunctionalInterface
    public interface ConditionOptionCall<OP extends FilterBuilder> {

        /**
         * @param op The option of condition to be set up. (NotNull)
         */
        void callback(OP op);
    }

    @FunctionalInterface
    public interface BoolCall<CF extends AbstractConditionFilter> {

        void callback(CF must, CF should, CF mustNot);
    }

    @FunctionalInterface
    public interface OperatorCall<CF extends AbstractConditionFilter> {

        void callback(CF and);
    }
}
