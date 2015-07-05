package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.DataConfigToRoleCQ;
import org.codelibs.fess.es.cbean.cf.DataConfigToRoleCF;
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
public abstract class BsDataConfigToRoleCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "data_config_to_role";
    }

    @Override
    public String xgetAliasName() {
        return "data_config_to_role";
    }

    public void filtered(FilteredCall<DataConfigToRoleCQ, DataConfigToRoleCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<DataConfigToRoleCQ, DataConfigToRoleCF> filteredLambda,
            ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        DataConfigToRoleCQ query = new DataConfigToRoleCQ();
        DataConfigToRoleCF filter = new DataConfigToRoleCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<DataConfigToRoleCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<DataConfigToRoleCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        DataConfigToRoleCQ mustQuery = new DataConfigToRoleCQ();
        DataConfigToRoleCQ shouldQuery = new DataConfigToRoleCQ();
        DataConfigToRoleCQ mustNotQuery = new DataConfigToRoleCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
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

    public BsDataConfigToRoleCQ addOrderBy_DataConfigId_Asc() {
        regOBA("dataConfigId");
        return this;
    }

    public BsDataConfigToRoleCQ addOrderBy_DataConfigId_Desc() {
        regOBD("dataConfigId");
        return this;
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

    public BsDataConfigToRoleCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsDataConfigToRoleCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setRoleTypeId_Term(String roleTypeId) {
        setRoleTypeId_Term(roleTypeId, null);
    }

    public void setRoleTypeId_Term(String roleTypeId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Terms(Collection<String> roleTypeIdList) {
        setRoleTypeId_Terms(roleTypeIdList, null);
    }

    public void setRoleTypeId_Terms(Collection<String> roleTypeIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("roleTypeId", roleTypeIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_InScope(Collection<String> roleTypeIdList) {
        setRoleTypeId_Terms(roleTypeIdList, null);
    }

    public void setRoleTypeId_InScope(Collection<String> roleTypeIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRoleTypeId_Terms(roleTypeIdList, opLambda);
    }

    public void setRoleTypeId_Match(String roleTypeId) {
        setRoleTypeId_Match(roleTypeId, null);
    }

    public void setRoleTypeId_Match(String roleTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_MatchPhrase(String roleTypeId) {
        setRoleTypeId_MatchPhrase(roleTypeId, null);
    }

    public void setRoleTypeId_MatchPhrase(String roleTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_MatchPhrasePrefix(String roleTypeId) {
        setRoleTypeId_MatchPhrasePrefix(roleTypeId, null);
    }

    public void setRoleTypeId_MatchPhrasePrefix(String roleTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Fuzzy(String roleTypeId) {
        setRoleTypeId_Fuzzy(roleTypeId, null);
    }

    public void setRoleTypeId_Fuzzy(String roleTypeId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Prefix(String roleTypeId) {
        setRoleTypeId_Prefix(roleTypeId, null);
    }

    public void setRoleTypeId_Prefix(String roleTypeId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_GreaterThan(String roleTypeId) {
        setRoleTypeId_GreaterThan(roleTypeId, null);
    }

    public void setRoleTypeId_GreaterThan(String roleTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roleTypeId", ConditionKey.CK_GREATER_THAN, roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_LessThan(String roleTypeId) {
        setRoleTypeId_LessThan(roleTypeId, null);
    }

    public void setRoleTypeId_LessThan(String roleTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roleTypeId", ConditionKey.CK_LESS_THAN, roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_GreaterEqual(String roleTypeId) {
        setRoleTypeId_GreaterEqual(roleTypeId, null);
    }

    public void setRoleTypeId_GreaterEqual(String roleTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roleTypeId", ConditionKey.CK_GREATER_EQUAL, roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_LessEqual(String roleTypeId) {
        setRoleTypeId_LessEqual(roleTypeId, null);
    }

    public void setRoleTypeId_LessEqual(String roleTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roleTypeId", ConditionKey.CK_LESS_EQUAL, roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigToRoleCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("roleTypeId");
        return this;
    }

    public BsDataConfigToRoleCQ addOrderBy_RoleTypeId_Desc() {
        regOBD("roleTypeId");
        return this;
    }

}
