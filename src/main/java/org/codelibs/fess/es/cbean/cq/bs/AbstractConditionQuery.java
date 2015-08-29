package org.codelibs.fess.es.cbean.cq.bs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.codelibs.fess.es.cbean.cf.bs.AbstractConditionFilter;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.coption.ParameterOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.name.ColumnRealName;
import org.dbflute.dbmeta.name.ColumnSqlName;
import org.dbflute.util.Srl;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author FreeGen
 */
public abstract class AbstractConditionQuery implements ConditionQuery {

    protected static final String CQ_PROPERTY = "conditionQuery";

    protected List<QueryBuilder> queryBuilderList;

    protected List<FieldSortBuilder> fieldSortBuilderList;

    private DocMetaCQ docMetaCQ;

    public DocMetaCQ docMeta() {
        if (docMetaCQ == null) {
            docMetaCQ = new DocMetaCQ();
        }
        return docMetaCQ;
    }

    public List<FieldSortBuilder> getFieldSortBuilderList() {
        return fieldSortBuilderList == null ? Collections.emptyList() : fieldSortBuilderList;
    }

    public boolean hasQueries() {
        return queryBuilderList != null && !queryBuilderList.isEmpty();
    }

    public QueryBuilder getQuery() {
        if (queryBuilderList == null) {
            return null;
        } else if (queryBuilderList.size() == 1) {
            return queryBuilderList.get(0);
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        queryBuilderList.forEach(query -> {
            boolQuery.must(query);
        });
        return boolQuery;
    }

    public void addQuery(QueryBuilder queryBuilder) {
        regQ(queryBuilder);
    }

    public void queryString(String queryString) {
        queryString(queryString, null);
    }

    public void queryString(String queryString, ConditionOptionCall<QueryStringQueryBuilder> opLambda) {
        QueryStringQueryBuilder queryStringQuery = QueryBuilders.queryStringQuery(queryString);
        regQ(queryStringQuery);
        if (opLambda != null) {
            opLambda.callback(queryStringQuery);
        }
    }

    public void matchAll() {
        matchAll(null);
    }

    public void matchAll(ConditionOptionCall<MatchAllQueryBuilder> opLambda) {
        MatchAllQueryBuilder builder = QueryBuilders.matchAllQuery();
        regQ(builder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    protected FilteredQueryBuilder regFilteredQ(QueryBuilder queryBuilder, FilterBuilder filterBuilder) {
        return QueryBuilders.filteredQuery(queryBuilder, filterBuilder);
    }

    protected BoolQueryBuilder regBoolCQ(List<QueryBuilder> mustList, List<QueryBuilder> shouldList, List<QueryBuilder> mustNotList) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        mustList.forEach(query -> {
            boolQuery.must(query);
        });
        shouldList.forEach(query -> {
            boolQuery.should(query);
        });
        mustNotList.forEach(query -> {
            boolQuery.mustNot(query);
        });
        return boolQuery;
    }

    protected TermQueryBuilder regTermQ(String name, Object value) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery(name, value);
        regQ(termQuery);
        return termQuery;
    }

    protected TermsQueryBuilder regTermsQ(String name, Collection<?> value) {
        TermsQueryBuilder termsQuery = QueryBuilders.termsQuery(name, value);
        regQ(termsQuery);
        return termsQuery;
    }

    protected MatchQueryBuilder regMatchQ(String name, Object value) {
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(name, value);
        regQ(matchQuery);
        return matchQuery;
    }

    protected MatchQueryBuilder regMatchPhraseQ(String name, Object value) {
        MatchQueryBuilder matchQuery = QueryBuilders.matchPhraseQuery(name, value);
        regQ(matchQuery);
        return matchQuery;
    }

    protected MatchQueryBuilder regMatchPhrasePrefixQ(String name, Object value) {
        MatchQueryBuilder matchQuery = QueryBuilders.matchPhrasePrefixQuery(name, value);
        regQ(matchQuery);
        return matchQuery;
    }

    protected FuzzyQueryBuilder regFuzzyQ(String name, Object value) {
        FuzzyQueryBuilder fuzzyQuery = QueryBuilders.fuzzyQuery(name, value);
        regQ(fuzzyQuery);
        return fuzzyQuery;
    }

    protected PrefixQueryBuilder regPrefixQ(String name, String prefix) {
        PrefixQueryBuilder prefixQuery = QueryBuilders.prefixQuery(name, prefix);
        regQ(prefixQuery);
        return prefixQuery;
    }

    protected RangeQueryBuilder regRangeQ(String name, ConditionKey ck, Object value) {
        if (queryBuilderList != null) {
            for (QueryBuilder builder : queryBuilderList) {
                if (builder instanceof RangeQueryBuilder) {
                    RangeQueryBuilder rangeQueryBuilder = (RangeQueryBuilder) builder;
                    if (rangeQueryBuilder.toString().replaceAll("\\s", "").startsWith("{\"range\":{\"" + name + "\"")) {
                        addRangeC(rangeQueryBuilder, ck, value);
                        return rangeQueryBuilder;
                    }
                }
            }
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
        addRangeC(rangeQueryBuilder, ck, value);
        regQ(rangeQueryBuilder);
        return rangeQueryBuilder;
    }

    protected void addRangeC(RangeQueryBuilder builder, ConditionKey ck, Object value) {
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

    protected void regQ(QueryBuilder builder) {
        if (queryBuilderList == null) {
            queryBuilderList = new ArrayList<>();
        }
        queryBuilderList.add(builder);
    }

    protected void regOBA(String field) {
        registerOrderBy(field, true);
    }

    protected void regOBD(String field) {
        registerOrderBy(field, false);
    }

    protected void registerOrderBy(String field, boolean ascOrDesc) {
        if (fieldSortBuilderList == null) {
            fieldSortBuilderList = new ArrayList<>();
        }
        fieldSortBuilderList.add(SortBuilders.fieldSort(field).order(ascOrDesc ? SortOrder.ASC : SortOrder.DESC));
    }

    @Override
    public ColumnRealName toColumnRealName(String columnDbName) {
        return ColumnRealName.create(xgetAliasName(), toColumnSqlName(columnDbName));
    }

    @Override
    public ColumnRealName toColumnRealName(ColumnInfo columnInfo) {
        return ColumnRealName.create(xgetAliasName(), columnInfo.getColumnSqlName());
    }

    @Override
    public ColumnSqlName toColumnSqlName(String columnDbName) {
        return new ColumnSqlName(columnDbName);
    }

    @Override
    public ConditionBean xgetBaseCB() {
        return null;
    }

    @Override
    public ConditionQuery xgetBaseQuery() {
        return null;
    }

    @Override
    public ConditionQuery xgetReferrerQuery() {
        return null;
    }

    @Override
    public SqlClause xgetSqlClause() {
        return null;
    }

    @Override
    public int xgetNestLevel() {
        return 0;
    }

    @Override
    public int xgetNextNestLevel() {
        return 0;
    }

    @Override
    public boolean isBaseQuery() {
        return false;
    }

    @Override
    public String xgetForeignPropertyName() {
        // TODO 
        return null;
    }

    @Override
    public String xgetRelationPath() {
        // TODO 
        return null;
    }

    @Override
    public String xgetLocationBase() {
        final StringBuilder sb = new StringBuilder();
        ConditionQuery query = this;
        while (true) {
            if (query.isBaseQuery()) {
                sb.insert(0, CQ_PROPERTY + ".");
                break;
            } else {
                final String foreignPropertyName = query.xgetForeignPropertyName();
                if (foreignPropertyName == null) {
                    String msg = "The foreignPropertyName of the query should not be null:";
                    msg = msg + " query=" + query;
                    throw new IllegalStateException(msg);
                }
                sb.insert(0, CQ_PROPERTY + initCap(foreignPropertyName) + ".");
            }
            query = query.xgetReferrerQuery();
        }
        return sb.toString();
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    @Override
    public ConditionValue invokeValue(String columnFlexibleName) {
        return null;
    }

    @Override
    public void invokeQuery(String columnFlexibleName, String conditionKeyName, Object conditionValue) {
        // nothing
    }

    @Override
    public void invokeQuery(String columnFlexibleName, String conditionKeyName, Object conditionValue, ConditionOption conditionOption) {
        // nothing
    }

    @Override
    public void invokeQueryEqual(String columnFlexibleName, Object conditionValue) {
        // nothing
    }

    @Override
    public void invokeQueryNotEqual(String columnFlexibleName, Object conditionValue) {
        // nothing
    }

    @Override
    public void invokeOrderBy(String columnFlexibleName, boolean isAsc) {
        // nothing
    }

    @Override
    public ConditionQuery invokeForeignCQ(String foreignPropertyName) {
        // TODO
        return null;
    }

    @Override
    public boolean invokeHasForeignCQ(String foreignPropertyName) {
        // TODO
        return false;
    }

    @Override
    public void xregisterParameterOption(ParameterOption option) {
        // nothing
    }

    public class DocMetaCQ {

        public void setId_Equal(String id) {
            regQ(QueryBuilders.idsQuery(asTableDbName()).addIds(id));
        }
    }

    @FunctionalInterface
    public interface ConditionOptionCall<OP extends QueryBuilder> {

        /**
         * @param op The option of condition to be set up. (NotNull)
         */
        void callback(OP op);
    }

    @FunctionalInterface
    public interface BoolCall<CQ extends AbstractConditionQuery> {

        void callback(CQ must, CQ should, CQ mustNot);
    }

    @FunctionalInterface
    public interface FilteredCall<CQ extends AbstractConditionQuery, CF extends AbstractConditionFilter> {

        void callback(CQ query, CF filter);
    }

    @FunctionalInterface
    public interface OperatorCall<CQ extends AbstractConditionQuery> {

        void callback(CQ and);
    }
}
