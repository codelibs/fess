package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.CrawlingSessionInfoCF;
import org.codelibs.fess.es.cbean.cq.CrawlingSessionInfoCQ;
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
public abstract class BsCrawlingSessionInfoCF extends AbstractConditionFilter {

    public void bool(BoolCall<CrawlingSessionInfoCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<CrawlingSessionInfoCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        CrawlingSessionInfoCF mustFilter = new CrawlingSessionInfoCF();
        CrawlingSessionInfoCF shouldFilter = new CrawlingSessionInfoCF();
        CrawlingSessionInfoCF mustNotFilter = new CrawlingSessionInfoCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<CrawlingSessionInfoCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<CrawlingSessionInfoCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        CrawlingSessionInfoCF andFilter = new CrawlingSessionInfoCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<CrawlingSessionInfoCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<CrawlingSessionInfoCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        CrawlingSessionInfoCF orFilter = new CrawlingSessionInfoCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<CrawlingSessionInfoCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<CrawlingSessionInfoCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        CrawlingSessionInfoCF notFilter = new CrawlingSessionInfoCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<CrawlingSessionInfoCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<CrawlingSessionInfoCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        CrawlingSessionInfoCQ query = new CrawlingSessionInfoCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setCrawlingSessionId_NotEqual(String crawlingSessionId) {
        setCrawlingSessionId_NotEqual(crawlingSessionId, null, null);
    }

    public void setCrawlingSessionId_NotEqual(String crawlingSessionId, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setCrawlingSessionId_Equal(crawlingSessionId, eqOpLambda);
        }, notOpLambda);
    }

    public void setCrawlingSessionId_Equal(String crawlingSessionId) {
        setCrawlingSessionId_Term(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Equal(String crawlingSessionId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setCrawlingSessionId_Term(crawlingSessionId, opLambda);
    }

    public void setCrawlingSessionId_Term(String crawlingSessionId) {
        setCrawlingSessionId_Term(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Term(String crawlingSessionId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Terms(Collection<String> crawlingSessionIdList) {
        setCrawlingSessionId_Terms(crawlingSessionIdList, null);
    }

    public void setCrawlingSessionId_Terms(Collection<String> crawlingSessionIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("crawlingSessionId", crawlingSessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_InScope(Collection<String> crawlingSessionIdList) {
        setCrawlingSessionId_Terms(crawlingSessionIdList, null);
    }

    public void setCrawlingSessionId_InScope(Collection<String> crawlingSessionIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setCrawlingSessionId_Terms(crawlingSessionIdList, opLambda);
    }

    public void setCrawlingSessionId_Prefix(String crawlingSessionId) {
        setCrawlingSessionId_Prefix(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Prefix(String crawlingSessionId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Exists() {
        setCrawlingSessionId_Exists(null);
    }

    public void setCrawlingSessionId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("crawlingSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Missing() {
        setCrawlingSessionId_Missing(null);
    }

    public void setCrawlingSessionId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("crawlingSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_GreaterThan(String crawlingSessionId) {
        setCrawlingSessionId_GreaterThan(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_GreaterThan(String crawlingSessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("crawlingSessionId", ConditionKey.CK_GREATER_THAN, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_LessThan(String crawlingSessionId) {
        setCrawlingSessionId_LessThan(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_LessThan(String crawlingSessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("crawlingSessionId", ConditionKey.CK_LESS_THAN, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_GreaterEqual(String crawlingSessionId) {
        setCrawlingSessionId_GreaterEqual(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_GreaterEqual(String crawlingSessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("crawlingSessionId", ConditionKey.CK_GREATER_EQUAL, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_LessEqual(String crawlingSessionId) {
        setCrawlingSessionId_LessEqual(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_LessEqual(String crawlingSessionId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("crawlingSessionId", ConditionKey.CK_LESS_EQUAL, crawlingSessionId);
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

    public void setKey_NotEqual(String key) {
        setKey_NotEqual(key, null, null);
    }

    public void setKey_NotEqual(String key, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setKey_Equal(key, eqOpLambda);
        }, notOpLambda);
    }

    public void setKey_Equal(String key) {
        setKey_Term(key, null);
    }

    public void setKey_Equal(String key, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setKey_Term(key, opLambda);
    }

    public void setKey_Term(String key) {
        setKey_Term(key, null);
    }

    public void setKey_Term(String key, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Terms(Collection<String> keyList) {
        setKey_Terms(keyList, null);
    }

    public void setKey_Terms(Collection<String> keyList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("key", keyList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_InScope(Collection<String> keyList) {
        setKey_Terms(keyList, null);
    }

    public void setKey_InScope(Collection<String> keyList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setKey_Terms(keyList, opLambda);
    }

    public void setKey_Prefix(String key) {
        setKey_Prefix(key, null);
    }

    public void setKey_Prefix(String key, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Exists() {
        setKey_Exists(null);
    }

    public void setKey_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Missing() {
        setKey_Missing(null);
    }

    public void setKey_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_GreaterThan(String key) {
        setKey_GreaterThan(key, null);
    }

    public void setKey_GreaterThan(String key, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("key", ConditionKey.CK_GREATER_THAN, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_LessThan(String key) {
        setKey_LessThan(key, null);
    }

    public void setKey_LessThan(String key, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("key", ConditionKey.CK_LESS_THAN, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_GreaterEqual(String key) {
        setKey_GreaterEqual(key, null);
    }

    public void setKey_GreaterEqual(String key, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("key", ConditionKey.CK_GREATER_EQUAL, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_LessEqual(String key) {
        setKey_LessEqual(key, null);
    }

    public void setKey_LessEqual(String key, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("key", ConditionKey.CK_LESS_EQUAL, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_NotEqual(String value) {
        setValue_NotEqual(value, null, null);
    }

    public void setValue_NotEqual(String value, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setValue_Equal(value, eqOpLambda);
        }, notOpLambda);
    }

    public void setValue_Equal(String value) {
        setValue_Term(value, null);
    }

    public void setValue_Equal(String value, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setValue_Term(value, opLambda);
    }

    public void setValue_Term(String value) {
        setValue_Term(value, null);
    }

    public void setValue_Term(String value, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Terms(Collection<String> valueList) {
        setValue_Terms(valueList, null);
    }

    public void setValue_Terms(Collection<String> valueList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("value", valueList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_InScope(Collection<String> valueList) {
        setValue_Terms(valueList, null);
    }

    public void setValue_InScope(Collection<String> valueList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setValue_Terms(valueList, opLambda);
    }

    public void setValue_Prefix(String value) {
        setValue_Prefix(value, null);
    }

    public void setValue_Prefix(String value, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Exists() {
        setValue_Exists(null);
    }

    public void setValue_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Missing() {
        setValue_Missing(null);
    }

    public void setValue_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_GreaterThan(String value) {
        setValue_GreaterThan(value, null);
    }

    public void setValue_GreaterThan(String value, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("value", ConditionKey.CK_GREATER_THAN, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_LessThan(String value) {
        setValue_LessThan(value, null);
    }

    public void setValue_LessThan(String value, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("value", ConditionKey.CK_LESS_THAN, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_GreaterEqual(String value) {
        setValue_GreaterEqual(value, null);
    }

    public void setValue_GreaterEqual(String value, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("value", ConditionKey.CK_GREATER_EQUAL, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_LessEqual(String value) {
        setValue_LessEqual(value, null);
    }

    public void setValue_LessEqual(String value, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("value", ConditionKey.CK_LESS_EQUAL, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
