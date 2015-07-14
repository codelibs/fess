package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.DataConfigToLabelCQ;
import org.codelibs.fess.es.cbean.cf.DataConfigToLabelCF;
import org.dbflute.cbean.ckey.ConditionKey;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

/**
 * @author FreeGen
 */
public abstract class BsDataConfigToLabelCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "data_config_to_label";
    }

    @Override
    public String xgetAliasName() {
        return "data_config_to_label";
    }

    public void filtered(FilteredCall<DataConfigToLabelCQ, DataConfigToLabelCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<DataConfigToLabelCQ, DataConfigToLabelCF> filteredLambda,
            ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        DataConfigToLabelCQ query = new DataConfigToLabelCQ();
        DataConfigToLabelCF filter = new DataConfigToLabelCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<DataConfigToLabelCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<DataConfigToLabelCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        DataConfigToLabelCQ mustQuery = new DataConfigToLabelCQ();
        DataConfigToLabelCQ shouldQuery = new DataConfigToLabelCQ();
        DataConfigToLabelCQ mustNotQuery = new DataConfigToLabelCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setDataConfigId_Equal(String dataConfigId) {
        setDataConfigId_Term(dataConfigId, null);
    }

    public void setDataConfigId_Equal(String dataConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDataConfigId_Term(dataConfigId, opLambda);
    }

    public void setDataConfigId_Term(String dataConfigId) {
        setDataConfigId_Term(dataConfigId, null);
    }

    public void setDataConfigId_Term(String dataConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Terms(Collection<String> dataConfigIdList) {
        setDataConfigId_Terms(dataConfigIdList, null);
    }

    public void setDataConfigId_Terms(Collection<String> dataConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("dataConfigId", dataConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_InScope(Collection<String> dataConfigIdList) {
        setDataConfigId_Terms(dataConfigIdList, null);
    }

    public void setDataConfigId_InScope(Collection<String> dataConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDataConfigId_Terms(dataConfigIdList, opLambda);
    }

    public void setDataConfigId_Match(String dataConfigId) {
        setDataConfigId_Match(dataConfigId, null);
    }

    public void setDataConfigId_Match(String dataConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_MatchPhrase(String dataConfigId) {
        setDataConfigId_MatchPhrase(dataConfigId, null);
    }

    public void setDataConfigId_MatchPhrase(String dataConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_MatchPhrasePrefix(String dataConfigId) {
        setDataConfigId_MatchPhrasePrefix(dataConfigId, null);
    }

    public void setDataConfigId_MatchPhrasePrefix(String dataConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Fuzzy(String dataConfigId) {
        setDataConfigId_Fuzzy(dataConfigId, null);
    }

    public void setDataConfigId_Fuzzy(String dataConfigId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Prefix(String dataConfigId) {
        setDataConfigId_Prefix(dataConfigId, null);
    }

    public void setDataConfigId_Prefix(String dataConfigId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_GreaterThan(String dataConfigId) {
        setDataConfigId_GreaterThan(dataConfigId, null);
    }

    public void setDataConfigId_GreaterThan(String dataConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("dataConfigId", ConditionKey.CK_GREATER_THAN, dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_LessThan(String dataConfigId) {
        setDataConfigId_LessThan(dataConfigId, null);
    }

    public void setDataConfigId_LessThan(String dataConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("dataConfigId", ConditionKey.CK_LESS_THAN, dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_GreaterEqual(String dataConfigId) {
        setDataConfigId_GreaterEqual(dataConfigId, null);
    }

    public void setDataConfigId_GreaterEqual(String dataConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("dataConfigId", ConditionKey.CK_GREATER_EQUAL, dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_LessEqual(String dataConfigId) {
        setDataConfigId_LessEqual(dataConfigId, null);
    }

    public void setDataConfigId_LessEqual(String dataConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("dataConfigId", ConditionKey.CK_LESS_EQUAL, dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigToLabelCQ addOrderBy_DataConfigId_Asc() {
        regOBA("dataConfigId");
        return this;
    }

    public BsDataConfigToLabelCQ addOrderBy_DataConfigId_Desc() {
        regOBD("dataConfigId");
        return this;
    }

    public void setId_Equal(String id) {
        setId_Term(id, null);
    }

    public void setId_Equal(String id, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setId_Term(id, opLambda);
    }

    public void setId_Term(String id) {
        setId_Term(id, null);
    }

    public void setId_Term(String id, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Terms(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_Terms(Collection<String> idList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("id", idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setId_Terms(idList, opLambda);
    }

    public void setId_Match(String id) {
        setId_Match(id, null);
    }

    public void setId_Match(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_MatchPhrase(String id) {
        setId_MatchPhrase(id, null);
    }

    public void setId_MatchPhrase(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_MatchPhrasePrefix(String id) {
        setId_MatchPhrasePrefix(id, null);
    }

    public void setId_MatchPhrasePrefix(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Fuzzy(String id) {
        setId_Fuzzy(id, null);
    }

    public void setId_Fuzzy(String id, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Prefix(String id) {
        setId_Prefix(id, null);
    }

    public void setId_Prefix(String id, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterThan(String id) {
        setId_GreaterThan(id, null);
    }

    public void setId_GreaterThan(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("id", ConditionKey.CK_GREATER_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessThan(String id) {
        setId_LessThan(id, null);
    }

    public void setId_LessThan(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("id", ConditionKey.CK_LESS_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterEqual(String id) {
        setId_GreaterEqual(id, null);
    }

    public void setId_GreaterEqual(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("id", ConditionKey.CK_GREATER_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessEqual(String id) {
        setId_LessEqual(id, null);
    }

    public void setId_LessEqual(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("id", ConditionKey.CK_LESS_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigToLabelCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsDataConfigToLabelCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setLabelTypeId_Equal(String labelTypeId) {
        setLabelTypeId_Term(labelTypeId, null);
    }

    public void setLabelTypeId_Equal(String labelTypeId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLabelTypeId_Term(labelTypeId, opLambda);
    }

    public void setLabelTypeId_Term(String labelTypeId) {
        setLabelTypeId_Term(labelTypeId, null);
    }

    public void setLabelTypeId_Term(String labelTypeId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Terms(Collection<String> labelTypeIdList) {
        setLabelTypeId_Terms(labelTypeIdList, null);
    }

    public void setLabelTypeId_Terms(Collection<String> labelTypeIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("labelTypeId", labelTypeIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_InScope(Collection<String> labelTypeIdList) {
        setLabelTypeId_Terms(labelTypeIdList, null);
    }

    public void setLabelTypeId_InScope(Collection<String> labelTypeIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLabelTypeId_Terms(labelTypeIdList, opLambda);
    }

    public void setLabelTypeId_Match(String labelTypeId) {
        setLabelTypeId_Match(labelTypeId, null);
    }

    public void setLabelTypeId_Match(String labelTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_MatchPhrase(String labelTypeId) {
        setLabelTypeId_MatchPhrase(labelTypeId, null);
    }

    public void setLabelTypeId_MatchPhrase(String labelTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_MatchPhrasePrefix(String labelTypeId) {
        setLabelTypeId_MatchPhrasePrefix(labelTypeId, null);
    }

    public void setLabelTypeId_MatchPhrasePrefix(String labelTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Fuzzy(String labelTypeId) {
        setLabelTypeId_Fuzzy(labelTypeId, null);
    }

    public void setLabelTypeId_Fuzzy(String labelTypeId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Prefix(String labelTypeId) {
        setLabelTypeId_Prefix(labelTypeId, null);
    }

    public void setLabelTypeId_Prefix(String labelTypeId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_GreaterThan(String labelTypeId) {
        setLabelTypeId_GreaterThan(labelTypeId, null);
    }

    public void setLabelTypeId_GreaterThan(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_GREATER_THAN, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_LessThan(String labelTypeId) {
        setLabelTypeId_LessThan(labelTypeId, null);
    }

    public void setLabelTypeId_LessThan(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_LESS_THAN, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_GreaterEqual(String labelTypeId) {
        setLabelTypeId_GreaterEqual(labelTypeId, null);
    }

    public void setLabelTypeId_GreaterEqual(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_GREATER_EQUAL, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_LessEqual(String labelTypeId) {
        setLabelTypeId_LessEqual(labelTypeId, null);
    }

    public void setLabelTypeId_LessEqual(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_LESS_EQUAL, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigToLabelCQ addOrderBy_LabelTypeId_Asc() {
        regOBA("labelTypeId");
        return this;
    }

    public BsDataConfigToLabelCQ addOrderBy_LabelTypeId_Desc() {
        regOBD("labelTypeId");
        return this;
    }

}
