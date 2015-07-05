package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.SearchLogCF;
import org.codelibs.fess.es.cbean.cq.SearchLogCQ;
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
public abstract class BsSearchLogCF extends AbstractConditionFilter {

    public void bool(BoolCall<SearchLogCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<SearchLogCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        SearchLogCF mustFilter = new SearchLogCF();
        SearchLogCF shouldFilter = new SearchLogCF();
        SearchLogCF mustNotFilter = new SearchLogCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<SearchLogCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<SearchLogCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        SearchLogCF andFilter = new SearchLogCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<SearchLogCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<SearchLogCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        SearchLogCF orFilter = new SearchLogCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<SearchLogCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<SearchLogCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        SearchLogCF notFilter = new SearchLogCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<SearchLogCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<SearchLogCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        SearchLogCQ query = new SearchLogCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setAccessType_Term(String accessType) {
        setAccessType_Term(accessType, null);
    }

    public void setAccessType_Term(String accessType, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Terms(Collection<String> accessTypeList) {
        setAccessType_Terms(accessTypeList, null);
    }

    public void setAccessType_Terms(Collection<String> accessTypeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("accessType", accessTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_InScope(Collection<String> accessTypeList) {
        setAccessType_Terms(accessTypeList, null);
    }

    public void setAccessType_InScope(Collection<String> accessTypeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setAccessType_Terms(accessTypeList, opLambda);
    }

    public void setAccessType_Prefix(String accessType) {
        setAccessType_Prefix(accessType, null);
    }

    public void setAccessType_Prefix(String accessType, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Exists() {
        setAccessType_Exists(null);
    }

    public void setAccessType_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Missing() {
        setAccessType_Missing(null);
    }

    public void setAccessType_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_GreaterThan(String accessType) {
        setAccessType_GreaterThan(accessType, null);
    }

    public void setAccessType_GreaterThan(String accessType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("accessType", ConditionKey.CK_GREATER_THAN, accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_LessThan(String accessType) {
        setAccessType_LessThan(accessType, null);
    }

    public void setAccessType_LessThan(String accessType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("accessType", ConditionKey.CK_LESS_THAN, accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_GreaterEqual(String accessType) {
        setAccessType_GreaterEqual(accessType, null);
    }

    public void setAccessType_GreaterEqual(String accessType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("accessType", ConditionKey.CK_GREATER_EQUAL, accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_LessEqual(String accessType) {
        setAccessType_LessEqual(accessType, null);
    }

    public void setAccessType_LessEqual(String accessType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("accessType", ConditionKey.CK_LESS_EQUAL, accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Term(String clientIp) {
        setClientIp_Term(clientIp, null);
    }

    public void setClientIp_Term(String clientIp, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Terms(Collection<String> clientIpList) {
        setClientIp_Terms(clientIpList, null);
    }

    public void setClientIp_Terms(Collection<String> clientIpList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("clientIp", clientIpList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_InScope(Collection<String> clientIpList) {
        setClientIp_Terms(clientIpList, null);
    }

    public void setClientIp_InScope(Collection<String> clientIpList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setClientIp_Terms(clientIpList, opLambda);
    }

    public void setClientIp_Prefix(String clientIp) {
        setClientIp_Prefix(clientIp, null);
    }

    public void setClientIp_Prefix(String clientIp, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Exists() {
        setClientIp_Exists(null);
    }

    public void setClientIp_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Missing() {
        setClientIp_Missing(null);
    }

    public void setClientIp_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_GreaterThan(String clientIp) {
        setClientIp_GreaterThan(clientIp, null);
    }

    public void setClientIp_GreaterThan(String clientIp, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("clientIp", ConditionKey.CK_GREATER_THAN, clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_LessThan(String clientIp) {
        setClientIp_LessThan(clientIp, null);
    }

    public void setClientIp_LessThan(String clientIp, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("clientIp", ConditionKey.CK_LESS_THAN, clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_GreaterEqual(String clientIp) {
        setClientIp_GreaterEqual(clientIp, null);
    }

    public void setClientIp_GreaterEqual(String clientIp, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("clientIp", ConditionKey.CK_GREATER_EQUAL, clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_LessEqual(String clientIp) {
        setClientIp_LessEqual(clientIp, null);
    }

    public void setClientIp_LessEqual(String clientIp, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("clientIp", ConditionKey.CK_LESS_EQUAL, clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Term(Long hitCount) {
        setHitCount_Term(hitCount, null);
    }

    public void setHitCount_Term(Long hitCount, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Terms(Collection<Long> hitCountList) {
        setHitCount_Terms(hitCountList, null);
    }

    public void setHitCount_Terms(Collection<Long> hitCountList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("hitCount", hitCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_InScope(Collection<Long> hitCountList) {
        setHitCount_Terms(hitCountList, null);
    }

    public void setHitCount_InScope(Collection<Long> hitCountList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setHitCount_Terms(hitCountList, opLambda);
    }

    public void setHitCount_Exists() {
        setHitCount_Exists(null);
    }

    public void setHitCount_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Missing() {
        setHitCount_Missing(null);
    }

    public void setHitCount_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_GreaterThan(Long hitCount) {
        setHitCount_GreaterThan(hitCount, null);
    }

    public void setHitCount_GreaterThan(Long hitCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("hitCount", ConditionKey.CK_GREATER_THAN, hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_LessThan(Long hitCount) {
        setHitCount_LessThan(hitCount, null);
    }

    public void setHitCount_LessThan(Long hitCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("hitCount", ConditionKey.CK_LESS_THAN, hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_GreaterEqual(Long hitCount) {
        setHitCount_GreaterEqual(hitCount, null);
    }

    public void setHitCount_GreaterEqual(Long hitCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("hitCount", ConditionKey.CK_GREATER_EQUAL, hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_LessEqual(Long hitCount) {
        setHitCount_LessEqual(hitCount, null);
    }

    public void setHitCount_LessEqual(Long hitCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("hitCount", ConditionKey.CK_LESS_EQUAL, hitCount);
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

    public void setQueryOffset_Term(Integer queryOffset) {
        setQueryOffset_Term(queryOffset, null);
    }

    public void setQueryOffset_Term(Integer queryOffset, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Terms(Collection<Integer> queryOffsetList) {
        setQueryOffset_Terms(queryOffsetList, null);
    }

    public void setQueryOffset_Terms(Collection<Integer> queryOffsetList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("queryOffset", queryOffsetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_InScope(Collection<Integer> queryOffsetList) {
        setQueryOffset_Terms(queryOffsetList, null);
    }

    public void setQueryOffset_InScope(Collection<Integer> queryOffsetList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setQueryOffset_Terms(queryOffsetList, opLambda);
    }

    public void setQueryOffset_Exists() {
        setQueryOffset_Exists(null);
    }

    public void setQueryOffset_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Missing() {
        setQueryOffset_Missing(null);
    }

    public void setQueryOffset_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_GreaterThan(Integer queryOffset) {
        setQueryOffset_GreaterThan(queryOffset, null);
    }

    public void setQueryOffset_GreaterThan(Integer queryOffset, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("queryOffset", ConditionKey.CK_GREATER_THAN, queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_LessThan(Integer queryOffset) {
        setQueryOffset_LessThan(queryOffset, null);
    }

    public void setQueryOffset_LessThan(Integer queryOffset, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("queryOffset", ConditionKey.CK_LESS_THAN, queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_GreaterEqual(Integer queryOffset) {
        setQueryOffset_GreaterEqual(queryOffset, null);
    }

    public void setQueryOffset_GreaterEqual(Integer queryOffset, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("queryOffset", ConditionKey.CK_GREATER_EQUAL, queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_LessEqual(Integer queryOffset) {
        setQueryOffset_LessEqual(queryOffset, null);
    }

    public void setQueryOffset_LessEqual(Integer queryOffset, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("queryOffset", ConditionKey.CK_LESS_EQUAL, queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Term(Integer queryPageSize) {
        setQueryPageSize_Term(queryPageSize, null);
    }

    public void setQueryPageSize_Term(Integer queryPageSize, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Terms(Collection<Integer> queryPageSizeList) {
        setQueryPageSize_Terms(queryPageSizeList, null);
    }

    public void setQueryPageSize_Terms(Collection<Integer> queryPageSizeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("queryPageSize", queryPageSizeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_InScope(Collection<Integer> queryPageSizeList) {
        setQueryPageSize_Terms(queryPageSizeList, null);
    }

    public void setQueryPageSize_InScope(Collection<Integer> queryPageSizeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setQueryPageSize_Terms(queryPageSizeList, opLambda);
    }

    public void setQueryPageSize_Exists() {
        setQueryPageSize_Exists(null);
    }

    public void setQueryPageSize_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Missing() {
        setQueryPageSize_Missing(null);
    }

    public void setQueryPageSize_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_GreaterThan(Integer queryPageSize) {
        setQueryPageSize_GreaterThan(queryPageSize, null);
    }

    public void setQueryPageSize_GreaterThan(Integer queryPageSize, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("queryPageSize", ConditionKey.CK_GREATER_THAN, queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_LessThan(Integer queryPageSize) {
        setQueryPageSize_LessThan(queryPageSize, null);
    }

    public void setQueryPageSize_LessThan(Integer queryPageSize, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("queryPageSize", ConditionKey.CK_LESS_THAN, queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_GreaterEqual(Integer queryPageSize) {
        setQueryPageSize_GreaterEqual(queryPageSize, null);
    }

    public void setQueryPageSize_GreaterEqual(Integer queryPageSize, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("queryPageSize", ConditionKey.CK_GREATER_EQUAL, queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_LessEqual(Integer queryPageSize) {
        setQueryPageSize_LessEqual(queryPageSize, null);
    }

    public void setQueryPageSize_LessEqual(Integer queryPageSize, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("queryPageSize", ConditionKey.CK_LESS_EQUAL, queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Term(String referer) {
        setReferer_Term(referer, null);
    }

    public void setReferer_Term(String referer, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Terms(Collection<String> refererList) {
        setReferer_Terms(refererList, null);
    }

    public void setReferer_Terms(Collection<String> refererList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("referer", refererList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_InScope(Collection<String> refererList) {
        setReferer_Terms(refererList, null);
    }

    public void setReferer_InScope(Collection<String> refererList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setReferer_Terms(refererList, opLambda);
    }

    public void setReferer_Prefix(String referer) {
        setReferer_Prefix(referer, null);
    }

    public void setReferer_Prefix(String referer, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Exists() {
        setReferer_Exists(null);
    }

    public void setReferer_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Missing() {
        setReferer_Missing(null);
    }

    public void setReferer_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_GreaterThan(String referer) {
        setReferer_GreaterThan(referer, null);
    }

    public void setReferer_GreaterThan(String referer, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("referer", ConditionKey.CK_GREATER_THAN, referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_LessThan(String referer) {
        setReferer_LessThan(referer, null);
    }

    public void setReferer_LessThan(String referer, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("referer", ConditionKey.CK_LESS_THAN, referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_GreaterEqual(String referer) {
        setReferer_GreaterEqual(referer, null);
    }

    public void setReferer_GreaterEqual(String referer, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("referer", ConditionKey.CK_GREATER_EQUAL, referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_LessEqual(String referer) {
        setReferer_LessEqual(referer, null);
    }

    public void setReferer_LessEqual(String referer, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("referer", ConditionKey.CK_LESS_EQUAL, referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_Term(Long requestedTime) {
        setRequestedTime_Term(requestedTime, null);
    }

    public void setRequestedTime_Term(Long requestedTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("requestedTime", requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_Terms(Collection<Long> requestedTimeList) {
        setRequestedTime_Terms(requestedTimeList, null);
    }

    public void setRequestedTime_Terms(Collection<Long> requestedTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("requestedTime", requestedTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_InScope(Collection<Long> requestedTimeList) {
        setRequestedTime_Terms(requestedTimeList, null);
    }

    public void setRequestedTime_InScope(Collection<Long> requestedTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setRequestedTime_Terms(requestedTimeList, opLambda);
    }

    public void setRequestedTime_Exists() {
        setRequestedTime_Exists(null);
    }

    public void setRequestedTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("requestedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_Missing() {
        setRequestedTime_Missing(null);
    }

    public void setRequestedTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("requestedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_GreaterThan(Long requestedTime) {
        setRequestedTime_GreaterThan(requestedTime, null);
    }

    public void setRequestedTime_GreaterThan(Long requestedTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("requestedTime", ConditionKey.CK_GREATER_THAN, requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_LessThan(Long requestedTime) {
        setRequestedTime_LessThan(requestedTime, null);
    }

    public void setRequestedTime_LessThan(Long requestedTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("requestedTime", ConditionKey.CK_LESS_THAN, requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_GreaterEqual(Long requestedTime) {
        setRequestedTime_GreaterEqual(requestedTime, null);
    }

    public void setRequestedTime_GreaterEqual(Long requestedTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("requestedTime", ConditionKey.CK_GREATER_EQUAL, requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_LessEqual(Long requestedTime) {
        setRequestedTime_LessEqual(requestedTime, null);
    }

    public void setRequestedTime_LessEqual(Long requestedTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("requestedTime", ConditionKey.CK_LESS_EQUAL, requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Term(Integer responseTime) {
        setResponseTime_Term(responseTime, null);
    }

    public void setResponseTime_Term(Integer responseTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Terms(Collection<Integer> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_Terms(Collection<Integer> responseTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("responseTime", responseTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_InScope(Collection<Integer> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_InScope(Collection<Integer> responseTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setResponseTime_Terms(responseTimeList, opLambda);
    }

    public void setResponseTime_Exists() {
        setResponseTime_Exists(null);
    }

    public void setResponseTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Missing() {
        setResponseTime_Missing(null);
    }

    public void setResponseTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterThan(Integer responseTime) {
        setResponseTime_GreaterThan(responseTime, null);
    }

    public void setResponseTime_GreaterThan(Integer responseTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("responseTime", ConditionKey.CK_GREATER_THAN, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessThan(Integer responseTime) {
        setResponseTime_LessThan(responseTime, null);
    }

    public void setResponseTime_LessThan(Integer responseTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("responseTime", ConditionKey.CK_LESS_THAN, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterEqual(Integer responseTime) {
        setResponseTime_GreaterEqual(responseTime, null);
    }

    public void setResponseTime_GreaterEqual(Integer responseTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("responseTime", ConditionKey.CK_GREATER_EQUAL, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessEqual(Integer responseTime) {
        setResponseTime_LessEqual(responseTime, null);
    }

    public void setResponseTime_LessEqual(Integer responseTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("responseTime", ConditionKey.CK_LESS_EQUAL, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Term(String searchWord) {
        setSearchWord_Term(searchWord, null);
    }

    public void setSearchWord_Term(String searchWord, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Terms(Collection<String> searchWordList) {
        setSearchWord_Terms(searchWordList, null);
    }

    public void setSearchWord_Terms(Collection<String> searchWordList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("searchWord", searchWordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_InScope(Collection<String> searchWordList) {
        setSearchWord_Terms(searchWordList, null);
    }

    public void setSearchWord_InScope(Collection<String> searchWordList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setSearchWord_Terms(searchWordList, opLambda);
    }

    public void setSearchWord_Prefix(String searchWord) {
        setSearchWord_Prefix(searchWord, null);
    }

    public void setSearchWord_Prefix(String searchWord, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Exists() {
        setSearchWord_Exists(null);
    }

    public void setSearchWord_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Missing() {
        setSearchWord_Missing(null);
    }

    public void setSearchWord_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_GreaterThan(String searchWord) {
        setSearchWord_GreaterThan(searchWord, null);
    }

    public void setSearchWord_GreaterThan(String searchWord, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("searchWord", ConditionKey.CK_GREATER_THAN, searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_LessThan(String searchWord) {
        setSearchWord_LessThan(searchWord, null);
    }

    public void setSearchWord_LessThan(String searchWord, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("searchWord", ConditionKey.CK_LESS_THAN, searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_GreaterEqual(String searchWord) {
        setSearchWord_GreaterEqual(searchWord, null);
    }

    public void setSearchWord_GreaterEqual(String searchWord, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("searchWord", ConditionKey.CK_GREATER_EQUAL, searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_LessEqual(String searchWord) {
        setSearchWord_LessEqual(searchWord, null);
    }

    public void setSearchWord_LessEqual(String searchWord, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("searchWord", ConditionKey.CK_LESS_EQUAL, searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Term(String userAgent) {
        setUserAgent_Term(userAgent, null);
    }

    public void setUserAgent_Term(String userAgent, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Terms(Collection<String> userAgentList) {
        setUserAgent_Terms(userAgentList, null);
    }

    public void setUserAgent_Terms(Collection<String> userAgentList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("userAgent", userAgentList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_InScope(Collection<String> userAgentList) {
        setUserAgent_Terms(userAgentList, null);
    }

    public void setUserAgent_InScope(Collection<String> userAgentList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setUserAgent_Terms(userAgentList, opLambda);
    }

    public void setUserAgent_Prefix(String userAgent) {
        setUserAgent_Prefix(userAgent, null);
    }

    public void setUserAgent_Prefix(String userAgent, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Exists() {
        setUserAgent_Exists(null);
    }

    public void setUserAgent_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Missing() {
        setUserAgent_Missing(null);
    }

    public void setUserAgent_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_GreaterThan(String userAgent) {
        setUserAgent_GreaterThan(userAgent, null);
    }

    public void setUserAgent_GreaterThan(String userAgent, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userAgent", ConditionKey.CK_GREATER_THAN, userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_LessThan(String userAgent) {
        setUserAgent_LessThan(userAgent, null);
    }

    public void setUserAgent_LessThan(String userAgent, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userAgent", ConditionKey.CK_LESS_THAN, userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_GreaterEqual(String userAgent) {
        setUserAgent_GreaterEqual(userAgent, null);
    }

    public void setUserAgent_GreaterEqual(String userAgent, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userAgent", ConditionKey.CK_GREATER_EQUAL, userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_LessEqual(String userAgent) {
        setUserAgent_LessEqual(userAgent, null);
    }

    public void setUserAgent_LessEqual(String userAgent, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userAgent", ConditionKey.CK_LESS_EQUAL, userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_Term(Long userId) {
        setUserId_Term(userId, null);
    }

    public void setUserId_Term(Long userId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("userId", userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_Terms(Collection<Long> userIdList) {
        setUserId_Terms(userIdList, null);
    }

    public void setUserId_Terms(Collection<Long> userIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("userId", userIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_InScope(Collection<Long> userIdList) {
        setUserId_Terms(userIdList, null);
    }

    public void setUserId_InScope(Collection<Long> userIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setUserId_Terms(userIdList, opLambda);
    }

    public void setUserId_Exists() {
        setUserId_Exists(null);
    }

    public void setUserId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("userId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_Missing() {
        setUserId_Missing(null);
    }

    public void setUserId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("userId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_GreaterThan(Long userId) {
        setUserId_GreaterThan(userId, null);
    }

    public void setUserId_GreaterThan(Long userId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userId", ConditionKey.CK_GREATER_THAN, userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_LessThan(Long userId) {
        setUserId_LessThan(userId, null);
    }

    public void setUserId_LessThan(Long userId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userId", ConditionKey.CK_LESS_THAN, userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_GreaterEqual(Long userId) {
        setUserId_GreaterEqual(userId, null);
    }

    public void setUserId_GreaterEqual(Long userId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userId", ConditionKey.CK_GREATER_EQUAL, userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_LessEqual(Long userId) {
        setUserId_LessEqual(userId, null);
    }

    public void setUserId_LessEqual(Long userId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userId", ConditionKey.CK_LESS_EQUAL, userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Term(String userSessionId) {
        setUserSessionId_Term(userSessionId, null);
    }

    public void setUserSessionId_Term(String userSessionId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Terms(Collection<String> userSessionIdList) {
        setUserSessionId_Terms(userSessionIdList, null);
    }

    public void setUserSessionId_Terms(Collection<String> userSessionIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("userSessionId", userSessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_InScope(Collection<String> userSessionIdList) {
        setUserSessionId_Terms(userSessionIdList, null);
    }

    public void setUserSessionId_InScope(Collection<String> userSessionIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setUserSessionId_Terms(userSessionIdList, opLambda);
    }

    public void setUserSessionId_Prefix(String userSessionId) {
        setUserSessionId_Prefix(userSessionId, null);
    }

    public void setUserSessionId_Prefix(String userSessionId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Exists() {
        setUserSessionId_Exists(null);
    }

    public void setUserSessionId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Missing() {
        setUserSessionId_Missing(null);
    }

    public void setUserSessionId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_GreaterThan(String userSessionId) {
        setUserSessionId_GreaterThan(userSessionId, null);
    }

    public void setUserSessionId_GreaterThan(String userSessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userSessionId", ConditionKey.CK_GREATER_THAN, userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_LessThan(String userSessionId) {
        setUserSessionId_LessThan(userSessionId, null);
    }

    public void setUserSessionId_LessThan(String userSessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userSessionId", ConditionKey.CK_LESS_THAN, userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_GreaterEqual(String userSessionId) {
        setUserSessionId_GreaterEqual(userSessionId, null);
    }

    public void setUserSessionId_GreaterEqual(String userSessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userSessionId", ConditionKey.CK_GREATER_EQUAL, userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_LessEqual(String userSessionId) {
        setUserSessionId_LessEqual(userSessionId, null);
    }

    public void setUserSessionId_LessEqual(String userSessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("userSessionId", ConditionKey.CK_LESS_EQUAL, userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
