package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.UserCF;
import org.codelibs.fess.es.cbean.cq.UserCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.exception.IllegalConditionBeanOperationException;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.PrefixFilterBuilder;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;

/**
 * @author FreeGen
 */
public abstract class BsUserCF extends AbstractConditionFilter {

    public void bool(BoolCall<UserCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<UserCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        UserCF mustFilter = new UserCF();
        UserCF shouldFilter = new UserCF();
        UserCF mustNotFilter = new UserCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<UserCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<UserCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        UserCF andFilter = new UserCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<UserCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<UserCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        UserCF orFilter = new UserCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<UserCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<UserCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        UserCF notFilter = new UserCF();
        notLambda.callback(notFilter);
        if (notFilter.hasFilters()) {
            if (notFilter.filterBuilderList.size() > 1) {
                final String msg = "not filter must be one filter.";
                throw new IllegalConditionBeanOperationException(msg);
            }
            NotFilterBuilder builder = regNotF(notFilter.filterBuilderList.get(0));
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<UserCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<UserCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        UserCQ query = new UserCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setGroup_NotEqual(String group) {
        setGroup_NotEqual(group, null, null);
    }

    public void setGroup_NotEqual(String group, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setGroup_Equal(group, eqOpLambda);
        }, notOpLambda);
    }

    public void setGroup_Equal(String group) {
        setGroup_Term(group, null);
    }

    public void setGroup_Equal(String group, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setGroup_Term(group, opLambda);
    }

    public void setGroup_Term(String group) {
        setGroup_Term(group, null);
    }

    public void setGroup_Term(String group, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("group", group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_Terms(Collection<String> groupList) {
        setGroup_Terms(groupList, null);
    }

    public void setGroup_Terms(Collection<String> groupList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("group", groupList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_InScope(Collection<String> groupList) {
        setGroup_Terms(groupList, null);
    }

    public void setGroup_InScope(Collection<String> groupList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setGroup_Terms(groupList, opLambda);
    }

    public void setGroup_Prefix(String group) {
        setGroup_Prefix(group, null);
    }

    public void setGroup_Prefix(String group, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("group", group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_Exists() {
        setGroup_Exists(null);
    }

    public void setGroup_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("group");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_Missing() {
        setGroup_Missing(null);
    }

    public void setGroup_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("group");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_GreaterThan(String group) {
        setGroup_GreaterThan(group, null);
    }

    public void setGroup_GreaterThan(String group, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("group", ConditionKey.CK_GREATER_THAN, group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_LessThan(String group) {
        setGroup_LessThan(group, null);
    }

    public void setGroup_LessThan(String group, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("group", ConditionKey.CK_LESS_THAN, group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_GreaterEqual(String group) {
        setGroup_GreaterEqual(group, null);
    }

    public void setGroup_GreaterEqual(String group, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("group", ConditionKey.CK_GREATER_EQUAL, group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroup_LessEqual(String group) {
        setGroup_LessEqual(group, null);
    }

    public void setGroup_LessEqual(String group, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("group", ConditionKey.CK_LESS_EQUAL, group);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_NotEqual(String id) {
        setId_NotEqual(id, null, null);
    }

    public void setId_NotEqual(String id, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setId_Equal(id, eqOpLambda);
        }, notOpLambda);
    }

    public void setId_Equal(String id) {
        setId_Term(id, null);
    }

    public void setId_Equal(String id, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setId_Term(id, opLambda);
    }

    public void setId_Term(String id) {
        setId_Term(id, null);
    }

    public void setId_Term(String id, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Terms(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_Terms(Collection<String> idList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("id", idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setId_Terms(idList, opLambda);
    }

    public void setId_Prefix(String id) {
        setId_Prefix(id, null);
    }

    public void setId_Prefix(String id, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Exists() {
        setId_Exists(null);
    }

    public void setId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Missing() {
        setId_Missing(null);
    }

    public void setId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterThan(String id) {
        setId_GreaterThan(id, null);
    }

    public void setId_GreaterThan(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_GREATER_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessThan(String id) {
        setId_LessThan(id, null);
    }

    public void setId_LessThan(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_LESS_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterEqual(String id) {
        setId_GreaterEqual(id, null);
    }

    public void setId_GreaterEqual(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_GREATER_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessEqual(String id) {
        setId_LessEqual(id, null);
    }

    public void setId_LessEqual(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_LESS_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_NotEqual(String name) {
        setName_NotEqual(name, null, null);
    }

    public void setName_NotEqual(String name, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setName_Equal(name, eqOpLambda);
        }, notOpLambda);
    }

    public void setName_Equal(String name) {
        setName_Term(name, null);
    }

    public void setName_Equal(String name, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setName_Term(name, opLambda);
    }

    public void setName_Term(String name) {
        setName_Term(name, null);
    }

    public void setName_Term(String name, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Terms(Collection<String> nameList) {
        setName_Terms(nameList, null);
    }

    public void setName_Terms(Collection<String> nameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("name", nameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_InScope(Collection<String> nameList) {
        setName_Terms(nameList, null);
    }

    public void setName_InScope(Collection<String> nameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setName_Terms(nameList, opLambda);
    }

    public void setName_Prefix(String name) {
        setName_Prefix(name, null);
    }

    public void setName_Prefix(String name, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Exists() {
        setName_Exists(null);
    }

    public void setName_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Missing() {
        setName_Missing(null);
    }

    public void setName_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterThan(String name) {
        setName_GreaterThan(name, null);
    }

    public void setName_GreaterThan(String name, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("name", ConditionKey.CK_GREATER_THAN, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessThan(String name) {
        setName_LessThan(name, null);
    }

    public void setName_LessThan(String name, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("name", ConditionKey.CK_LESS_THAN, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterEqual(String name) {
        setName_GreaterEqual(name, null);
    }

    public void setName_GreaterEqual(String name, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("name", ConditionKey.CK_GREATER_EQUAL, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessEqual(String name) {
        setName_LessEqual(name, null);
    }

    public void setName_LessEqual(String name, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("name", ConditionKey.CK_LESS_EQUAL, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_NotEqual(String password) {
        setPassword_NotEqual(password, null, null);
    }

    public void setPassword_NotEqual(String password, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setPassword_Equal(password, eqOpLambda);
        }, notOpLambda);
    }

    public void setPassword_Equal(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Equal(String password, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setPassword_Term(password, opLambda);
    }

    public void setPassword_Term(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Term(String password, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Terms(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_Terms(Collection<String> passwordList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("password", passwordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_InScope(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_InScope(Collection<String> passwordList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setPassword_Terms(passwordList, opLambda);
    }

    public void setPassword_Prefix(String password) {
        setPassword_Prefix(password, null);
    }

    public void setPassword_Prefix(String password, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Exists() {
        setPassword_Exists(null);
    }

    public void setPassword_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Missing() {
        setPassword_Missing(null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterThan(String password) {
        setPassword_GreaterThan(password, null);
    }

    public void setPassword_GreaterThan(String password, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("password", ConditionKey.CK_GREATER_THAN, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessThan(String password) {
        setPassword_LessThan(password, null);
    }

    public void setPassword_LessThan(String password, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("password", ConditionKey.CK_LESS_THAN, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterEqual(String password) {
        setPassword_GreaterEqual(password, null);
    }

    public void setPassword_GreaterEqual(String password, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("password", ConditionKey.CK_GREATER_EQUAL, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessEqual(String password) {
        setPassword_LessEqual(password, null);
    }

    public void setPassword_LessEqual(String password, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("password", ConditionKey.CK_LESS_EQUAL, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_NotEqual(String role) {
        setRole_NotEqual(role, null, null);
    }

    public void setRole_NotEqual(String role, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setRole_Equal(role, eqOpLambda);
        }, notOpLambda);
    }

    public void setRole_Equal(String role) {
        setRole_Term(role, null);
    }

    public void setRole_Equal(String role, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setRole_Term(role, opLambda);
    }

    public void setRole_Term(String role) {
        setRole_Term(role, null);
    }

    public void setRole_Term(String role, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("role", role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_Terms(Collection<String> roleList) {
        setRole_Terms(roleList, null);
    }

    public void setRole_Terms(Collection<String> roleList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("role", roleList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_InScope(Collection<String> roleList) {
        setRole_Terms(roleList, null);
    }

    public void setRole_InScope(Collection<String> roleList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setRole_Terms(roleList, opLambda);
    }

    public void setRole_Prefix(String role) {
        setRole_Prefix(role, null);
    }

    public void setRole_Prefix(String role, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("role", role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_Exists() {
        setRole_Exists(null);
    }

    public void setRole_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("role");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_Missing() {
        setRole_Missing(null);
    }

    public void setRole_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("role");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_GreaterThan(String role) {
        setRole_GreaterThan(role, null);
    }

    public void setRole_GreaterThan(String role, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("role", ConditionKey.CK_GREATER_THAN, role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_LessThan(String role) {
        setRole_LessThan(role, null);
    }

    public void setRole_LessThan(String role, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("role", ConditionKey.CK_LESS_THAN, role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_GreaterEqual(String role) {
        setRole_GreaterEqual(role, null);
    }

    public void setRole_GreaterEqual(String role, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("role", ConditionKey.CK_GREATER_EQUAL, role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRole_LessEqual(String role) {
        setRole_LessEqual(role, null);
    }

    public void setRole_LessEqual(String role, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("role", ConditionKey.CK_LESS_EQUAL, role);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
