package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.WebAuthenticationCF;
import org.codelibs.fess.es.cbean.cq.WebAuthenticationCQ;
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
public abstract class BsWebAuthenticationCF extends AbstractConditionFilter {

    public void bool(BoolCall<WebAuthenticationCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<WebAuthenticationCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        WebAuthenticationCF mustFilter = new WebAuthenticationCF();
        WebAuthenticationCF shouldFilter = new WebAuthenticationCF();
        WebAuthenticationCF mustNotFilter = new WebAuthenticationCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<WebAuthenticationCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<WebAuthenticationCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        WebAuthenticationCF andFilter = new WebAuthenticationCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<WebAuthenticationCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<WebAuthenticationCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        WebAuthenticationCF orFilter = new WebAuthenticationCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<WebAuthenticationCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<WebAuthenticationCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        WebAuthenticationCF notFilter = new WebAuthenticationCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<WebAuthenticationCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<WebAuthenticationCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        WebAuthenticationCQ query = new WebAuthenticationCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setAuthRealm_NotEqual(String authRealm) {
        setAuthRealm_NotEqual(authRealm, null, null);
    }

    public void setAuthRealm_NotEqual(String authRealm, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setAuthRealm_Equal(authRealm, eqOpLambda);
        }, notOpLambda);
    }

    public void setAuthRealm_Equal(String authRealm) {
        setAuthRealm_Term(authRealm, null);
    }

    public void setAuthRealm_Equal(String authRealm, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setAuthRealm_Term(authRealm, opLambda);
    }

    public void setAuthRealm_Term(String authRealm) {
        setAuthRealm_Term(authRealm, null);
    }

    public void setAuthRealm_Term(String authRealm, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Terms(Collection<String> authRealmList) {
        setAuthRealm_Terms(authRealmList, null);
    }

    public void setAuthRealm_Terms(Collection<String> authRealmList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("authRealm", authRealmList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_InScope(Collection<String> authRealmList) {
        setAuthRealm_Terms(authRealmList, null);
    }

    public void setAuthRealm_InScope(Collection<String> authRealmList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setAuthRealm_Terms(authRealmList, opLambda);
    }

    public void setAuthRealm_Prefix(String authRealm) {
        setAuthRealm_Prefix(authRealm, null);
    }

    public void setAuthRealm_Prefix(String authRealm, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Exists() {
        setAuthRealm_Exists(null);
    }

    public void setAuthRealm_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("authRealm");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Missing() {
        setAuthRealm_Missing(null);
    }

    public void setAuthRealm_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("authRealm");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_GreaterThan(String authRealm) {
        setAuthRealm_GreaterThan(authRealm, null);
    }

    public void setAuthRealm_GreaterThan(String authRealm, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("authRealm", ConditionKey.CK_GREATER_THAN, authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_LessThan(String authRealm) {
        setAuthRealm_LessThan(authRealm, null);
    }

    public void setAuthRealm_LessThan(String authRealm, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("authRealm", ConditionKey.CK_LESS_THAN, authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_GreaterEqual(String authRealm) {
        setAuthRealm_GreaterEqual(authRealm, null);
    }

    public void setAuthRealm_GreaterEqual(String authRealm, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("authRealm", ConditionKey.CK_GREATER_EQUAL, authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_LessEqual(String authRealm) {
        setAuthRealm_LessEqual(authRealm, null);
    }

    public void setAuthRealm_LessEqual(String authRealm, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("authRealm", ConditionKey.CK_LESS_EQUAL, authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_NotEqual(String createdBy) {
        setCreatedBy_NotEqual(createdBy, null, null);
    }

    public void setCreatedBy_NotEqual(String createdBy, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setCreatedBy_Equal(createdBy, eqOpLambda);
        }, notOpLambda);
    }

    public void setCreatedBy_Equal(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Equal(String createdBy, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setCreatedBy_Term(createdBy, opLambda);
    }

    public void setCreatedBy_Term(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Term(String createdBy, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Terms(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_Terms(Collection<String> createdByList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("createdBy", createdByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_InScope(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_InScope(Collection<String> createdByList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setCreatedBy_Terms(createdByList, opLambda);
    }

    public void setCreatedBy_Prefix(String createdBy) {
        setCreatedBy_Prefix(createdBy, null);
    }

    public void setCreatedBy_Prefix(String createdBy, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Exists() {
        setCreatedBy_Exists(null);
    }

    public void setCreatedBy_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Missing() {
        setCreatedBy_Missing(null);
    }

    public void setCreatedBy_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterThan(String createdBy) {
        setCreatedBy_GreaterThan(createdBy, null);
    }

    public void setCreatedBy_GreaterThan(String createdBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdBy", ConditionKey.CK_GREATER_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessThan(String createdBy) {
        setCreatedBy_LessThan(createdBy, null);
    }

    public void setCreatedBy_LessThan(String createdBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdBy", ConditionKey.CK_LESS_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterEqual(String createdBy) {
        setCreatedBy_GreaterEqual(createdBy, null);
    }

    public void setCreatedBy_GreaterEqual(String createdBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdBy", ConditionKey.CK_GREATER_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessEqual(String createdBy) {
        setCreatedBy_LessEqual(createdBy, null);
    }

    public void setCreatedBy_LessEqual(String createdBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdBy", ConditionKey.CK_LESS_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_NotEqual(Long createdTime) {
        setCreatedTime_NotEqual(createdTime, null, null);
    }

    public void setCreatedTime_NotEqual(Long createdTime, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setCreatedTime_Equal(createdTime, eqOpLambda);
        }, notOpLambda);
    }

    public void setCreatedTime_Equal(Long createdTime) {
        setCreatedTime_Term(createdTime, null);
    }

    public void setCreatedTime_Equal(Long createdTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setCreatedTime_Term(createdTime, opLambda);
    }

    public void setCreatedTime_Term(Long createdTime) {
        setCreatedTime_Term(createdTime, null);
    }

    public void setCreatedTime_Term(Long createdTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Terms(Collection<Long> createdTimeList) {
        setCreatedTime_Terms(createdTimeList, null);
    }

    public void setCreatedTime_Terms(Collection<Long> createdTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("createdTime", createdTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList) {
        setCreatedTime_Terms(createdTimeList, null);
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setCreatedTime_Terms(createdTimeList, opLambda);
    }

    public void setCreatedTime_Exists() {
        setCreatedTime_Exists(null);
    }

    public void setCreatedTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Missing() {
        setCreatedTime_Missing(null);
    }

    public void setCreatedTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterThan(Long createdTime) {
        setCreatedTime_GreaterThan(createdTime, null);
    }

    public void setCreatedTime_GreaterThan(Long createdTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdTime", ConditionKey.CK_GREATER_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessThan(Long createdTime) {
        setCreatedTime_LessThan(createdTime, null);
    }

    public void setCreatedTime_LessThan(Long createdTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdTime", ConditionKey.CK_LESS_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterEqual(Long createdTime) {
        setCreatedTime_GreaterEqual(createdTime, null);
    }

    public void setCreatedTime_GreaterEqual(Long createdTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdTime", ConditionKey.CK_GREATER_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessEqual(Long createdTime) {
        setCreatedTime_LessEqual(createdTime, null);
    }

    public void setCreatedTime_LessEqual(Long createdTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdTime", ConditionKey.CK_LESS_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_NotEqual(String hostname) {
        setHostname_NotEqual(hostname, null, null);
    }

    public void setHostname_NotEqual(String hostname, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setHostname_Equal(hostname, eqOpLambda);
        }, notOpLambda);
    }

    public void setHostname_Equal(String hostname) {
        setHostname_Term(hostname, null);
    }

    public void setHostname_Equal(String hostname, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setHostname_Term(hostname, opLambda);
    }

    public void setHostname_Term(String hostname) {
        setHostname_Term(hostname, null);
    }

    public void setHostname_Term(String hostname, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Terms(Collection<String> hostnameList) {
        setHostname_Terms(hostnameList, null);
    }

    public void setHostname_Terms(Collection<String> hostnameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("hostname", hostnameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_InScope(Collection<String> hostnameList) {
        setHostname_Terms(hostnameList, null);
    }

    public void setHostname_InScope(Collection<String> hostnameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setHostname_Terms(hostnameList, opLambda);
    }

    public void setHostname_Prefix(String hostname) {
        setHostname_Prefix(hostname, null);
    }

    public void setHostname_Prefix(String hostname, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Exists() {
        setHostname_Exists(null);
    }

    public void setHostname_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Missing() {
        setHostname_Missing(null);
    }

    public void setHostname_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_GreaterThan(String hostname) {
        setHostname_GreaterThan(hostname, null);
    }

    public void setHostname_GreaterThan(String hostname, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("hostname", ConditionKey.CK_GREATER_THAN, hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_LessThan(String hostname) {
        setHostname_LessThan(hostname, null);
    }

    public void setHostname_LessThan(String hostname, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("hostname", ConditionKey.CK_LESS_THAN, hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_GreaterEqual(String hostname) {
        setHostname_GreaterEqual(hostname, null);
    }

    public void setHostname_GreaterEqual(String hostname, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("hostname", ConditionKey.CK_GREATER_EQUAL, hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_LessEqual(String hostname) {
        setHostname_LessEqual(hostname, null);
    }

    public void setHostname_LessEqual(String hostname, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("hostname", ConditionKey.CK_LESS_EQUAL, hostname);
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

    public void setParameters_NotEqual(String parameters) {
        setParameters_NotEqual(parameters, null, null);
    }

    public void setParameters_NotEqual(String parameters, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setParameters_Equal(parameters, eqOpLambda);
        }, notOpLambda);
    }

    public void setParameters_Equal(String parameters) {
        setParameters_Term(parameters, null);
    }

    public void setParameters_Equal(String parameters, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setParameters_Term(parameters, opLambda);
    }

    public void setParameters_Term(String parameters) {
        setParameters_Term(parameters, null);
    }

    public void setParameters_Term(String parameters, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Terms(Collection<String> parametersList) {
        setParameters_Terms(parametersList, null);
    }

    public void setParameters_Terms(Collection<String> parametersList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("parameters", parametersList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_InScope(Collection<String> parametersList) {
        setParameters_Terms(parametersList, null);
    }

    public void setParameters_InScope(Collection<String> parametersList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setParameters_Terms(parametersList, opLambda);
    }

    public void setParameters_Prefix(String parameters) {
        setParameters_Prefix(parameters, null);
    }

    public void setParameters_Prefix(String parameters, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Exists() {
        setParameters_Exists(null);
    }

    public void setParameters_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Missing() {
        setParameters_Missing(null);
    }

    public void setParameters_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_GreaterThan(String parameters) {
        setParameters_GreaterThan(parameters, null);
    }

    public void setParameters_GreaterThan(String parameters, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("parameters", ConditionKey.CK_GREATER_THAN, parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_LessThan(String parameters) {
        setParameters_LessThan(parameters, null);
    }

    public void setParameters_LessThan(String parameters, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("parameters", ConditionKey.CK_LESS_THAN, parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_GreaterEqual(String parameters) {
        setParameters_GreaterEqual(parameters, null);
    }

    public void setParameters_GreaterEqual(String parameters, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("parameters", ConditionKey.CK_GREATER_EQUAL, parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_LessEqual(String parameters) {
        setParameters_LessEqual(parameters, null);
    }

    public void setParameters_LessEqual(String parameters, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("parameters", ConditionKey.CK_LESS_EQUAL, parameters);
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

    public void setPort_NotEqual(Integer port) {
        setPort_NotEqual(port, null, null);
    }

    public void setPort_NotEqual(Integer port, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setPort_Equal(port, eqOpLambda);
        }, notOpLambda);
    }

    public void setPort_Equal(Integer port) {
        setPort_Term(port, null);
    }

    public void setPort_Equal(Integer port, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setPort_Term(port, opLambda);
    }

    public void setPort_Term(Integer port) {
        setPort_Term(port, null);
    }

    public void setPort_Term(Integer port, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Terms(Collection<Integer> portList) {
        setPort_Terms(portList, null);
    }

    public void setPort_Terms(Collection<Integer> portList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("port", portList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_InScope(Collection<Integer> portList) {
        setPort_Terms(portList, null);
    }

    public void setPort_InScope(Collection<Integer> portList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setPort_Terms(portList, opLambda);
    }

    public void setPort_Exists() {
        setPort_Exists(null);
    }

    public void setPort_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Missing() {
        setPort_Missing(null);
    }

    public void setPort_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_GreaterThan(Integer port) {
        setPort_GreaterThan(port, null);
    }

    public void setPort_GreaterThan(Integer port, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("port", ConditionKey.CK_GREATER_THAN, port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_LessThan(Integer port) {
        setPort_LessThan(port, null);
    }

    public void setPort_LessThan(Integer port, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("port", ConditionKey.CK_LESS_THAN, port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_GreaterEqual(Integer port) {
        setPort_GreaterEqual(port, null);
    }

    public void setPort_GreaterEqual(Integer port, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("port", ConditionKey.CK_GREATER_EQUAL, port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_LessEqual(Integer port) {
        setPort_LessEqual(port, null);
    }

    public void setPort_LessEqual(Integer port, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("port", ConditionKey.CK_LESS_EQUAL, port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_NotEqual(String protocolScheme) {
        setProtocolScheme_NotEqual(protocolScheme, null, null);
    }

    public void setProtocolScheme_NotEqual(String protocolScheme, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setProtocolScheme_Equal(protocolScheme, eqOpLambda);
        }, notOpLambda);
    }

    public void setProtocolScheme_Equal(String protocolScheme) {
        setProtocolScheme_Term(protocolScheme, null);
    }

    public void setProtocolScheme_Equal(String protocolScheme, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setProtocolScheme_Term(protocolScheme, opLambda);
    }

    public void setProtocolScheme_Term(String protocolScheme) {
        setProtocolScheme_Term(protocolScheme, null);
    }

    public void setProtocolScheme_Term(String protocolScheme, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Terms(Collection<String> protocolSchemeList) {
        setProtocolScheme_Terms(protocolSchemeList, null);
    }

    public void setProtocolScheme_Terms(Collection<String> protocolSchemeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("protocolScheme", protocolSchemeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_InScope(Collection<String> protocolSchemeList) {
        setProtocolScheme_Terms(protocolSchemeList, null);
    }

    public void setProtocolScheme_InScope(Collection<String> protocolSchemeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setProtocolScheme_Terms(protocolSchemeList, opLambda);
    }

    public void setProtocolScheme_Prefix(String protocolScheme) {
        setProtocolScheme_Prefix(protocolScheme, null);
    }

    public void setProtocolScheme_Prefix(String protocolScheme, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Exists() {
        setProtocolScheme_Exists(null);
    }

    public void setProtocolScheme_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Missing() {
        setProtocolScheme_Missing(null);
    }

    public void setProtocolScheme_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_GreaterThan(String protocolScheme) {
        setProtocolScheme_GreaterThan(protocolScheme, null);
    }

    public void setProtocolScheme_GreaterThan(String protocolScheme, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("protocolScheme", ConditionKey.CK_GREATER_THAN, protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_LessThan(String protocolScheme) {
        setProtocolScheme_LessThan(protocolScheme, null);
    }

    public void setProtocolScheme_LessThan(String protocolScheme, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("protocolScheme", ConditionKey.CK_LESS_THAN, protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_GreaterEqual(String protocolScheme) {
        setProtocolScheme_GreaterEqual(protocolScheme, null);
    }

    public void setProtocolScheme_GreaterEqual(String protocolScheme, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("protocolScheme", ConditionKey.CK_GREATER_EQUAL, protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_LessEqual(String protocolScheme) {
        setProtocolScheme_LessEqual(protocolScheme, null);
    }

    public void setProtocolScheme_LessEqual(String protocolScheme, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("protocolScheme", ConditionKey.CK_LESS_EQUAL, protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_NotEqual(String updatedBy) {
        setUpdatedBy_NotEqual(updatedBy, null, null);
    }

    public void setUpdatedBy_NotEqual(String updatedBy, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setUpdatedBy_Equal(updatedBy, eqOpLambda);
        }, notOpLambda);
    }

    public void setUpdatedBy_Equal(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Equal(String updatedBy, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setUpdatedBy_Term(updatedBy, opLambda);
    }

    public void setUpdatedBy_Term(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Term(String updatedBy, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList) {
        setUpdatedBy_Terms(updatedByList, null);
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("updatedBy", updatedByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList) {
        setUpdatedBy_Terms(updatedByList, null);
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setUpdatedBy_Terms(updatedByList, opLambda);
    }

    public void setUpdatedBy_Prefix(String updatedBy) {
        setUpdatedBy_Prefix(updatedBy, null);
    }

    public void setUpdatedBy_Prefix(String updatedBy, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Exists() {
        setUpdatedBy_Exists(null);
    }

    public void setUpdatedBy_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Missing() {
        setUpdatedBy_Missing(null);
    }

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterThan(String updatedBy) {
        setUpdatedBy_GreaterThan(updatedBy, null);
    }

    public void setUpdatedBy_GreaterThan(String updatedBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("updatedBy", ConditionKey.CK_GREATER_THAN, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessThan(String updatedBy) {
        setUpdatedBy_LessThan(updatedBy, null);
    }

    public void setUpdatedBy_LessThan(String updatedBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("updatedBy", ConditionKey.CK_LESS_THAN, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy) {
        setUpdatedBy_GreaterEqual(updatedBy, null);
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("updatedBy", ConditionKey.CK_GREATER_EQUAL, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessEqual(String updatedBy) {
        setUpdatedBy_LessEqual(updatedBy, null);
    }

    public void setUpdatedBy_LessEqual(String updatedBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("updatedBy", ConditionKey.CK_LESS_EQUAL, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_NotEqual(Long updatedTime) {
        setUpdatedTime_NotEqual(updatedTime, null, null);
    }

    public void setUpdatedTime_NotEqual(Long updatedTime, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setUpdatedTime_Equal(updatedTime, eqOpLambda);
        }, notOpLambda);
    }

    public void setUpdatedTime_Equal(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Equal(Long updatedTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setUpdatedTime_Term(updatedTime, opLambda);
    }

    public void setUpdatedTime_Term(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Term(Long updatedTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList) {
        setUpdatedTime_Terms(updatedTimeList, null);
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("updatedTime", updatedTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList) {
        setUpdatedTime_Terms(updatedTimeList, null);
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setUpdatedTime_Terms(updatedTimeList, opLambda);
    }

    public void setUpdatedTime_Exists() {
        setUpdatedTime_Exists(null);
    }

    public void setUpdatedTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Missing() {
        setUpdatedTime_Missing(null);
    }

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime) {
        setUpdatedTime_GreaterThan(updatedTime, null);
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("updatedTime", ConditionKey.CK_GREATER_THAN, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessThan(Long updatedTime) {
        setUpdatedTime_LessThan(updatedTime, null);
    }

    public void setUpdatedTime_LessThan(Long updatedTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("updatedTime", ConditionKey.CK_LESS_THAN, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime) {
        setUpdatedTime_GreaterEqual(updatedTime, null);
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("updatedTime", ConditionKey.CK_GREATER_EQUAL, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessEqual(Long updatedTime) {
        setUpdatedTime_LessEqual(updatedTime, null);
    }

    public void setUpdatedTime_LessEqual(Long updatedTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("updatedTime", ConditionKey.CK_LESS_EQUAL, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_NotEqual(String username) {
        setUsername_NotEqual(username, null, null);
    }

    public void setUsername_NotEqual(String username, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setUsername_Equal(username, eqOpLambda);
        }, notOpLambda);
    }

    public void setUsername_Equal(String username) {
        setUsername_Term(username, null);
    }

    public void setUsername_Equal(String username, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setUsername_Term(username, opLambda);
    }

    public void setUsername_Term(String username) {
        setUsername_Term(username, null);
    }

    public void setUsername_Term(String username, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Terms(Collection<String> usernameList) {
        setUsername_Terms(usernameList, null);
    }

    public void setUsername_Terms(Collection<String> usernameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("username", usernameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_InScope(Collection<String> usernameList) {
        setUsername_Terms(usernameList, null);
    }

    public void setUsername_InScope(Collection<String> usernameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setUsername_Terms(usernameList, opLambda);
    }

    public void setUsername_Prefix(String username) {
        setUsername_Prefix(username, null);
    }

    public void setUsername_Prefix(String username, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Exists() {
        setUsername_Exists(null);
    }

    public void setUsername_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Missing() {
        setUsername_Missing(null);
    }

    public void setUsername_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_GreaterThan(String username) {
        setUsername_GreaterThan(username, null);
    }

    public void setUsername_GreaterThan(String username, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("username", ConditionKey.CK_GREATER_THAN, username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_LessThan(String username) {
        setUsername_LessThan(username, null);
    }

    public void setUsername_LessThan(String username, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("username", ConditionKey.CK_LESS_THAN, username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_GreaterEqual(String username) {
        setUsername_GreaterEqual(username, null);
    }

    public void setUsername_GreaterEqual(String username, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("username", ConditionKey.CK_GREATER_EQUAL, username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_LessEqual(String username) {
        setUsername_LessEqual(username, null);
    }

    public void setUsername_LessEqual(String username, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("username", ConditionKey.CK_LESS_EQUAL, username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_NotEqual(String webConfigId) {
        setWebConfigId_NotEqual(webConfigId, null, null);
    }

    public void setWebConfigId_NotEqual(String webConfigId, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setWebConfigId_Equal(webConfigId, eqOpLambda);
        }, notOpLambda);
    }

    public void setWebConfigId_Equal(String webConfigId) {
        setWebConfigId_Term(webConfigId, null);
    }

    public void setWebConfigId_Equal(String webConfigId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setWebConfigId_Term(webConfigId, opLambda);
    }

    public void setWebConfigId_Term(String webConfigId) {
        setWebConfigId_Term(webConfigId, null);
    }

    public void setWebConfigId_Term(String webConfigId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Terms(Collection<String> webConfigIdList) {
        setWebConfigId_Terms(webConfigIdList, null);
    }

    public void setWebConfigId_Terms(Collection<String> webConfigIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("webConfigId", webConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_InScope(Collection<String> webConfigIdList) {
        setWebConfigId_Terms(webConfigIdList, null);
    }

    public void setWebConfigId_InScope(Collection<String> webConfigIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setWebConfigId_Terms(webConfigIdList, opLambda);
    }

    public void setWebConfigId_Prefix(String webConfigId) {
        setWebConfigId_Prefix(webConfigId, null);
    }

    public void setWebConfigId_Prefix(String webConfigId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Exists() {
        setWebConfigId_Exists(null);
    }

    public void setWebConfigId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Missing() {
        setWebConfigId_Missing(null);
    }

    public void setWebConfigId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_GreaterThan(String webConfigId) {
        setWebConfigId_GreaterThan(webConfigId, null);
    }

    public void setWebConfigId_GreaterThan(String webConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("webConfigId", ConditionKey.CK_GREATER_THAN, webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_LessThan(String webConfigId) {
        setWebConfigId_LessThan(webConfigId, null);
    }

    public void setWebConfigId_LessThan(String webConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("webConfigId", ConditionKey.CK_LESS_THAN, webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_GreaterEqual(String webConfigId) {
        setWebConfigId_GreaterEqual(webConfigId, null);
    }

    public void setWebConfigId_GreaterEqual(String webConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("webConfigId", ConditionKey.CK_GREATER_EQUAL, webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_LessEqual(String webConfigId) {
        setWebConfigId_LessEqual(webConfigId, null);
    }

    public void setWebConfigId_LessEqual(String webConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("webConfigId", ConditionKey.CK_LESS_EQUAL, webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
