package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.UserCF;
import org.codelibs.fess.es.cbean.cq.UserCQ;
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
public abstract class BsUserCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "user";
    }

    @Override
    public String xgetAliasName() {
        return "user";
    }

    public void filtered(FilteredCall<UserCQ, UserCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<UserCQ, UserCF> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        UserCQ query = new UserCQ();
        UserCF filter = new UserCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<UserCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<UserCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        UserCQ mustQuery = new UserCQ();
        UserCQ shouldQuery = new UserCQ();
        UserCQ mustNotQuery = new UserCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setGroup_Equal(String group) {
        setGroup_Term(group, null);
    }

    public void setGroup_Equal(String group, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setGroup_Term(group, opLambda);
    }

    public void setGroup_Term(String group) {
        setGroup_Term(group, null);
    }

    public void setGroup_Term(String group, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("group", group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_Terms(Collection<String> groupList) {
        setGroup_Terms(groupList, null);
    }

    public void setGroup_Terms(Collection<String> groupList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("group", groupList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_InScope(Collection<String> groupList) {
        setGroup_Terms(groupList, null);
    }

    public void setGroup_InScope(Collection<String> groupList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setGroup_Terms(groupList, opLambda);
    }

    public void setGroup_Match(String group) {
        setGroup_Match(group, null);
    }

    public void setGroup_Match(String group, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("group", group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_MatchPhrase(String group) {
        setGroup_MatchPhrase(group, null);
    }

    public void setGroup_MatchPhrase(String group, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("group", group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_MatchPhrasePrefix(String group) {
        setGroup_MatchPhrasePrefix(group, null);
    }

    public void setGroup_MatchPhrasePrefix(String group, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("group", group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_Fuzzy(String group) {
        setGroup_Fuzzy(group, null);
    }

    public void setGroup_Fuzzy(String group, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("group", group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_Prefix(String group) {
        setGroup_Prefix(group, null);
    }

    public void setGroup_Prefix(String group, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("group", group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_GreaterThan(String group) {
        setGroup_GreaterThan(group, null);
    }

    public void setGroup_GreaterThan(String group, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("group", ConditionKey.CK_GREATER_THAN, group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_LessThan(String group) {
        setGroup_LessThan(group, null);
    }

    public void setGroup_LessThan(String group, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("group", ConditionKey.CK_LESS_THAN, group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_GreaterEqual(String group) {
        setGroup_GreaterEqual(group, null);
    }

    public void setGroup_GreaterEqual(String group, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("group", ConditionKey.CK_GREATER_EQUAL, group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_LessEqual(String group) {
        setGroup_LessEqual(group, null);
    }

    public void setGroup_LessEqual(String group, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("group", ConditionKey.CK_LESS_EQUAL, group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Group_Asc() {
        regOBA("group");
        return this;
    }

    public BsUserCQ addOrderBy_Group_Desc() {
        regOBD("group");
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

    public BsUserCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsUserCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setName_Equal(String name) {
        setName_Term(name, null);
    }

    public void setName_Equal(String name, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setName_Term(name, opLambda);
    }

    public void setName_Term(String name) {
        setName_Term(name, null);
    }

    public void setName_Term(String name, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Terms(Collection<String> nameList) {
        setName_Terms(nameList, null);
    }

    public void setName_Terms(Collection<String> nameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("name", nameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_InScope(Collection<String> nameList) {
        setName_Terms(nameList, null);
    }

    public void setName_InScope(Collection<String> nameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setName_Terms(nameList, opLambda);
    }

    public void setName_Match(String name) {
        setName_Match(name, null);
    }

    public void setName_Match(String name, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_MatchPhrase(String name) {
        setName_MatchPhrase(name, null);
    }

    public void setName_MatchPhrase(String name, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_MatchPhrasePrefix(String name) {
        setName_MatchPhrasePrefix(name, null);
    }

    public void setName_MatchPhrasePrefix(String name, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Fuzzy(String name) {
        setName_Fuzzy(name, null);
    }

    public void setName_Fuzzy(String name, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Prefix(String name) {
        setName_Prefix(name, null);
    }

    public void setName_Prefix(String name, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterThan(String name) {
        setName_GreaterThan(name, null);
    }

    public void setName_GreaterThan(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_GREATER_THAN, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessThan(String name) {
        setName_LessThan(name, null);
    }

    public void setName_LessThan(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_LESS_THAN, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterEqual(String name) {
        setName_GreaterEqual(name, null);
    }

    public void setName_GreaterEqual(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_GREATER_EQUAL, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessEqual(String name) {
        setName_LessEqual(name, null);
    }

    public void setName_LessEqual(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_LESS_EQUAL, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsUserCQ addOrderBy_Name_Desc() {
        regOBD("name");
        return this;
    }

    public void setPassword_Equal(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Equal(String password, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPassword_Term(password, opLambda);
    }

    public void setPassword_Term(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Term(String password, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Terms(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_Terms(Collection<String> passwordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("password", passwordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_InScope(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_InScope(Collection<String> passwordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPassword_Terms(passwordList, opLambda);
    }

    public void setPassword_Match(String password) {
        setPassword_Match(password, null);
    }

    public void setPassword_Match(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_MatchPhrase(String password) {
        setPassword_MatchPhrase(password, null);
    }

    public void setPassword_MatchPhrase(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_MatchPhrasePrefix(String password) {
        setPassword_MatchPhrasePrefix(password, null);
    }

    public void setPassword_MatchPhrasePrefix(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Fuzzy(String password) {
        setPassword_Fuzzy(password, null);
    }

    public void setPassword_Fuzzy(String password, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Prefix(String password) {
        setPassword_Prefix(password, null);
    }

    public void setPassword_Prefix(String password, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterThan(String password) {
        setPassword_GreaterThan(password, null);
    }

    public void setPassword_GreaterThan(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_GREATER_THAN, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessThan(String password) {
        setPassword_LessThan(password, null);
    }

    public void setPassword_LessThan(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_LESS_THAN, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterEqual(String password) {
        setPassword_GreaterEqual(password, null);
    }

    public void setPassword_GreaterEqual(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_GREATER_EQUAL, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessEqual(String password) {
        setPassword_LessEqual(password, null);
    }

    public void setPassword_LessEqual(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_LESS_EQUAL, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Password_Asc() {
        regOBA("password");
        return this;
    }

    public BsUserCQ addOrderBy_Password_Desc() {
        regOBD("password");
        return this;
    }

    public void setRole_Equal(String role) {
        setRole_Term(role, null);
    }

    public void setRole_Equal(String role, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRole_Term(role, opLambda);
    }

    public void setRole_Term(String role) {
        setRole_Term(role, null);
    }

    public void setRole_Term(String role, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("role", role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_Terms(Collection<String> roleList) {
        setRole_Terms(roleList, null);
    }

    public void setRole_Terms(Collection<String> roleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("role", roleList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_InScope(Collection<String> roleList) {
        setRole_Terms(roleList, null);
    }

    public void setRole_InScope(Collection<String> roleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRole_Terms(roleList, opLambda);
    }

    public void setRole_Match(String role) {
        setRole_Match(role, null);
    }

    public void setRole_Match(String role, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("role", role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_MatchPhrase(String role) {
        setRole_MatchPhrase(role, null);
    }

    public void setRole_MatchPhrase(String role, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("role", role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_MatchPhrasePrefix(String role) {
        setRole_MatchPhrasePrefix(role, null);
    }

    public void setRole_MatchPhrasePrefix(String role, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("role", role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_Fuzzy(String role) {
        setRole_Fuzzy(role, null);
    }

    public void setRole_Fuzzy(String role, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("role", role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_Prefix(String role) {
        setRole_Prefix(role, null);
    }

    public void setRole_Prefix(String role, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("role", role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_GreaterThan(String role) {
        setRole_GreaterThan(role, null);
    }

    public void setRole_GreaterThan(String role, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("role", ConditionKey.CK_GREATER_THAN, role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_LessThan(String role) {
        setRole_LessThan(role, null);
    }

    public void setRole_LessThan(String role, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("role", ConditionKey.CK_LESS_THAN, role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_GreaterEqual(String role) {
        setRole_GreaterEqual(role, null);
    }

    public void setRole_GreaterEqual(String role, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("role", ConditionKey.CK_GREATER_EQUAL, role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_LessEqual(String role) {
        setRole_LessEqual(role, null);
    }

    public void setRole_LessEqual(String role, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("role", ConditionKey.CK_LESS_EQUAL, role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Role_Asc() {
        regOBA("role");
        return this;
    }

    public BsUserCQ addOrderBy_Role_Desc() {
        regOBD("role");
        return this;
    }

}
