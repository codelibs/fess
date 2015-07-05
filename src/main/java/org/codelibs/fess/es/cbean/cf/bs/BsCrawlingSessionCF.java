package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.CrawlingSessionCF;
import org.codelibs.fess.es.cbean.cq.CrawlingSessionCQ;
import org.dbflute.exception.IllegalConditionBeanOperationException;
import org.dbflute.cbean.ckey.ConditionKey;
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
public abstract class BsCrawlingSessionCF extends AbstractConditionFilter {

    public void bool(BoolCall<CrawlingSessionCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<CrawlingSessionCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        CrawlingSessionCF mustFilter = new CrawlingSessionCF();
        CrawlingSessionCF shouldFilter = new CrawlingSessionCF();
        CrawlingSessionCF mustNotFilter = new CrawlingSessionCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<CrawlingSessionCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<CrawlingSessionCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        CrawlingSessionCF andFilter = new CrawlingSessionCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<CrawlingSessionCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<CrawlingSessionCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        CrawlingSessionCF orFilter = new CrawlingSessionCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<CrawlingSessionCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<CrawlingSessionCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        CrawlingSessionCF notFilter = new CrawlingSessionCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<CrawlingSessionCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<CrawlingSessionCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        CrawlingSessionCQ query = new CrawlingSessionCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
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

    public void setExpiredTime_Term(Long expiredTime) {
        setExpiredTime_Term(expiredTime, null);
    }

    public void setExpiredTime_Term(Long expiredTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Terms(Collection<Long> expiredTimeList) {
        setExpiredTime_Terms(expiredTimeList, null);
    }

    public void setExpiredTime_Terms(Collection<Long> expiredTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("expiredTime", expiredTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_InScope(Collection<Long> expiredTimeList) {
        setExpiredTime_Terms(expiredTimeList, null);
    }

    public void setExpiredTime_InScope(Collection<Long> expiredTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setExpiredTime_Terms(expiredTimeList, opLambda);
    }

    public void setExpiredTime_Exists() {
        setExpiredTime_Exists(null);
    }

    public void setExpiredTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Missing() {
        setExpiredTime_Missing(null);
    }

    public void setExpiredTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_GreaterThan(Long expiredTime) {
        setExpiredTime_GreaterThan(expiredTime, null);
    }

    public void setExpiredTime_GreaterThan(Long expiredTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("expiredTime", ConditionKey.CK_GREATER_THAN, expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_LessThan(Long expiredTime) {
        setExpiredTime_LessThan(expiredTime, null);
    }

    public void setExpiredTime_LessThan(Long expiredTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("expiredTime", ConditionKey.CK_LESS_THAN, expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_GreaterEqual(Long expiredTime) {
        setExpiredTime_GreaterEqual(expiredTime, null);
    }

    public void setExpiredTime_GreaterEqual(Long expiredTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("expiredTime", ConditionKey.CK_GREATER_EQUAL, expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_LessEqual(Long expiredTime) {
        setExpiredTime_LessEqual(expiredTime, null);
    }

    public void setExpiredTime_LessEqual(Long expiredTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("expiredTime", ConditionKey.CK_LESS_EQUAL, expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
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

    public void setSessionId_Term(String sessionId) {
        setSessionId_Term(sessionId, null);
    }

    public void setSessionId_Term(String sessionId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Terms(Collection<String> sessionIdList) {
        setSessionId_Terms(sessionIdList, null);
    }

    public void setSessionId_Terms(Collection<String> sessionIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("sessionId", sessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_InScope(Collection<String> sessionIdList) {
        setSessionId_Terms(sessionIdList, null);
    }

    public void setSessionId_InScope(Collection<String> sessionIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setSessionId_Terms(sessionIdList, opLambda);
    }

    public void setSessionId_Prefix(String sessionId) {
        setSessionId_Prefix(sessionId, null);
    }

    public void setSessionId_Prefix(String sessionId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Exists() {
        setSessionId_Exists(null);
    }

    public void setSessionId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Missing() {
        setSessionId_Missing(null);
    }

    public void setSessionId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_GreaterThan(String sessionId) {
        setSessionId_GreaterThan(sessionId, null);
    }

    public void setSessionId_GreaterThan(String sessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("sessionId", ConditionKey.CK_GREATER_THAN, sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_LessThan(String sessionId) {
        setSessionId_LessThan(sessionId, null);
    }

    public void setSessionId_LessThan(String sessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("sessionId", ConditionKey.CK_LESS_THAN, sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_GreaterEqual(String sessionId) {
        setSessionId_GreaterEqual(sessionId, null);
    }

    public void setSessionId_GreaterEqual(String sessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("sessionId", ConditionKey.CK_GREATER_EQUAL, sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_LessEqual(String sessionId) {
        setSessionId_LessEqual(sessionId, null);
    }

    public void setSessionId_LessEqual(String sessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("sessionId", ConditionKey.CK_LESS_EQUAL, sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
