package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.WebConfigCF;
import org.codelibs.fess.es.cbean.cq.WebConfigCQ;
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
public abstract class BsWebConfigCF extends AbstractConditionFilter {

    public void bool(BoolCall<WebConfigCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<WebConfigCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        WebConfigCF mustFilter = new WebConfigCF();
        WebConfigCF shouldFilter = new WebConfigCF();
        WebConfigCF mustNotFilter = new WebConfigCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<WebConfigCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<WebConfigCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        WebConfigCF andFilter = new WebConfigCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<WebConfigCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<WebConfigCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        WebConfigCF orFilter = new WebConfigCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<WebConfigCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<WebConfigCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        WebConfigCF notFilter = new WebConfigCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<WebConfigCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<WebConfigCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        WebConfigCQ query = new WebConfigCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setAvailable_NotEqual(Boolean available) {
        setAvailable_NotEqual(available, null, null);
    }

    public void setAvailable_NotEqual(Boolean available, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setAvailable_Equal(available, eqOpLambda);
        }, notOpLambda);
    }

    public void setAvailable_Equal(Boolean available) {
        setAvailable_Term(available, null);
    }

    public void setAvailable_Equal(Boolean available, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setAvailable_Term(available, opLambda);
    }

    public void setAvailable_Term(Boolean available) {
        setAvailable_Term(available, null);
    }

    public void setAvailable_Term(Boolean available, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Terms(Collection<Boolean> availableList) {
        setAvailable_Terms(availableList, null);
    }

    public void setAvailable_Terms(Collection<Boolean> availableList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("available", availableList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_InScope(Collection<Boolean> availableList) {
        setAvailable_Terms(availableList, null);
    }

    public void setAvailable_InScope(Collection<Boolean> availableList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setAvailable_Terms(availableList, opLambda);
    }

    public void setAvailable_Exists() {
        setAvailable_Exists(null);
    }

    public void setAvailable_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Missing() {
        setAvailable_Missing(null);
    }

    public void setAvailable_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_GreaterThan(Boolean available) {
        setAvailable_GreaterThan(available, null);
    }

    public void setAvailable_GreaterThan(Boolean available, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("available", ConditionKey.CK_GREATER_THAN, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_LessThan(Boolean available) {
        setAvailable_LessThan(available, null);
    }

    public void setAvailable_LessThan(Boolean available, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("available", ConditionKey.CK_LESS_THAN, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_GreaterEqual(Boolean available) {
        setAvailable_GreaterEqual(available, null);
    }

    public void setAvailable_GreaterEqual(Boolean available, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("available", ConditionKey.CK_GREATER_EQUAL, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_LessEqual(Boolean available) {
        setAvailable_LessEqual(available, null);
    }

    public void setAvailable_LessEqual(Boolean available, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("available", ConditionKey.CK_LESS_EQUAL, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_NotEqual(Float boost) {
        setBoost_NotEqual(boost, null, null);
    }

    public void setBoost_NotEqual(Float boost, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setBoost_Equal(boost, eqOpLambda);
        }, notOpLambda);
    }

    public void setBoost_Equal(Float boost) {
        setBoost_Term(boost, null);
    }

    public void setBoost_Equal(Float boost, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setBoost_Term(boost, opLambda);
    }

    public void setBoost_Term(Float boost) {
        setBoost_Term(boost, null);
    }

    public void setBoost_Term(Float boost, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Terms(Collection<Float> boostList) {
        setBoost_Terms(boostList, null);
    }

    public void setBoost_Terms(Collection<Float> boostList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("boost", boostList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_InScope(Collection<Float> boostList) {
        setBoost_Terms(boostList, null);
    }

    public void setBoost_InScope(Collection<Float> boostList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setBoost_Terms(boostList, opLambda);
    }

    public void setBoost_Exists() {
        setBoost_Exists(null);
    }

    public void setBoost_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Missing() {
        setBoost_Missing(null);
    }

    public void setBoost_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_GreaterThan(Float boost) {
        setBoost_GreaterThan(boost, null);
    }

    public void setBoost_GreaterThan(Float boost, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("boost", ConditionKey.CK_GREATER_THAN, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_LessThan(Float boost) {
        setBoost_LessThan(boost, null);
    }

    public void setBoost_LessThan(Float boost, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("boost", ConditionKey.CK_LESS_THAN, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_GreaterEqual(Float boost) {
        setBoost_GreaterEqual(boost, null);
    }

    public void setBoost_GreaterEqual(Float boost, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("boost", ConditionKey.CK_GREATER_EQUAL, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_LessEqual(Float boost) {
        setBoost_LessEqual(boost, null);
    }

    public void setBoost_LessEqual(Float boost, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("boost", ConditionKey.CK_LESS_EQUAL, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_NotEqual(String configParameter) {
        setConfigParameter_NotEqual(configParameter, null, null);
    }

    public void setConfigParameter_NotEqual(String configParameter, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setConfigParameter_Equal(configParameter, eqOpLambda);
        }, notOpLambda);
    }

    public void setConfigParameter_Equal(String configParameter) {
        setConfigParameter_Term(configParameter, null);
    }

    public void setConfigParameter_Equal(String configParameter, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setConfigParameter_Term(configParameter, opLambda);
    }

    public void setConfigParameter_Term(String configParameter) {
        setConfigParameter_Term(configParameter, null);
    }

    public void setConfigParameter_Term(String configParameter, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("configParameter", configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Terms(Collection<String> configParameterList) {
        setConfigParameter_Terms(configParameterList, null);
    }

    public void setConfigParameter_Terms(Collection<String> configParameterList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("configParameter", configParameterList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_InScope(Collection<String> configParameterList) {
        setConfigParameter_Terms(configParameterList, null);
    }

    public void setConfigParameter_InScope(Collection<String> configParameterList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setConfigParameter_Terms(configParameterList, opLambda);
    }

    public void setConfigParameter_Prefix(String configParameter) {
        setConfigParameter_Prefix(configParameter, null);
    }

    public void setConfigParameter_Prefix(String configParameter, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("configParameter", configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Exists() {
        setConfigParameter_Exists(null);
    }

    public void setConfigParameter_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Missing() {
        setConfigParameter_Missing(null);
    }

    public void setConfigParameter_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_GreaterThan(String configParameter) {
        setConfigParameter_GreaterThan(configParameter, null);
    }

    public void setConfigParameter_GreaterThan(String configParameter, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("configParameter", ConditionKey.CK_GREATER_THAN, configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_LessThan(String configParameter) {
        setConfigParameter_LessThan(configParameter, null);
    }

    public void setConfigParameter_LessThan(String configParameter, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("configParameter", ConditionKey.CK_LESS_THAN, configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_GreaterEqual(String configParameter) {
        setConfigParameter_GreaterEqual(configParameter, null);
    }

    public void setConfigParameter_GreaterEqual(String configParameter, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("configParameter", ConditionKey.CK_GREATER_EQUAL, configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_LessEqual(String configParameter) {
        setConfigParameter_LessEqual(configParameter, null);
    }

    public void setConfigParameter_LessEqual(String configParameter, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("configParameter", ConditionKey.CK_LESS_EQUAL, configParameter);
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

    public void setDepth_NotEqual(Integer depth) {
        setDepth_NotEqual(depth, null, null);
    }

    public void setDepth_NotEqual(Integer depth, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setDepth_Equal(depth, eqOpLambda);
        }, notOpLambda);
    }

    public void setDepth_Equal(Integer depth) {
        setDepth_Term(depth, null);
    }

    public void setDepth_Equal(Integer depth, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setDepth_Term(depth, opLambda);
    }

    public void setDepth_Term(Integer depth) {
        setDepth_Term(depth, null);
    }

    public void setDepth_Term(Integer depth, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("depth", depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Terms(Collection<Integer> depthList) {
        setDepth_Terms(depthList, null);
    }

    public void setDepth_Terms(Collection<Integer> depthList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("depth", depthList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_InScope(Collection<Integer> depthList) {
        setDepth_Terms(depthList, null);
    }

    public void setDepth_InScope(Collection<Integer> depthList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setDepth_Terms(depthList, opLambda);
    }

    public void setDepth_Exists() {
        setDepth_Exists(null);
    }

    public void setDepth_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Missing() {
        setDepth_Missing(null);
    }

    public void setDepth_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_GreaterThan(Integer depth) {
        setDepth_GreaterThan(depth, null);
    }

    public void setDepth_GreaterThan(Integer depth, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("depth", ConditionKey.CK_GREATER_THAN, depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_LessThan(Integer depth) {
        setDepth_LessThan(depth, null);
    }

    public void setDepth_LessThan(Integer depth, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("depth", ConditionKey.CK_LESS_THAN, depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_GreaterEqual(Integer depth) {
        setDepth_GreaterEqual(depth, null);
    }

    public void setDepth_GreaterEqual(Integer depth, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("depth", ConditionKey.CK_GREATER_EQUAL, depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_LessEqual(Integer depth) {
        setDepth_LessEqual(depth, null);
    }

    public void setDepth_LessEqual(Integer depth, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("depth", ConditionKey.CK_LESS_EQUAL, depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_NotEqual(String excludedDocUrls) {
        setExcludedDocUrls_NotEqual(excludedDocUrls, null, null);
    }

    public void setExcludedDocUrls_NotEqual(String excludedDocUrls, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setExcludedDocUrls_Equal(excludedDocUrls, eqOpLambda);
        }, notOpLambda);
    }

    public void setExcludedDocUrls_Equal(String excludedDocUrls) {
        setExcludedDocUrls_Term(excludedDocUrls, null);
    }

    public void setExcludedDocUrls_Equal(String excludedDocUrls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setExcludedDocUrls_Term(excludedDocUrls, opLambda);
    }

    public void setExcludedDocUrls_Term(String excludedDocUrls) {
        setExcludedDocUrls_Term(excludedDocUrls, null);
    }

    public void setExcludedDocUrls_Term(String excludedDocUrls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("excludedDocUrls", excludedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_Terms(Collection<String> excludedDocUrlsList) {
        setExcludedDocUrls_Terms(excludedDocUrlsList, null);
    }

    public void setExcludedDocUrls_Terms(Collection<String> excludedDocUrlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("excludedDocUrls", excludedDocUrlsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_InScope(Collection<String> excludedDocUrlsList) {
        setExcludedDocUrls_Terms(excludedDocUrlsList, null);
    }

    public void setExcludedDocUrls_InScope(Collection<String> excludedDocUrlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setExcludedDocUrls_Terms(excludedDocUrlsList, opLambda);
    }

    public void setExcludedDocUrls_Prefix(String excludedDocUrls) {
        setExcludedDocUrls_Prefix(excludedDocUrls, null);
    }

    public void setExcludedDocUrls_Prefix(String excludedDocUrls, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("excludedDocUrls", excludedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_Exists() {
        setExcludedDocUrls_Exists(null);
    }

    public void setExcludedDocUrls_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("excludedDocUrls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_Missing() {
        setExcludedDocUrls_Missing(null);
    }

    public void setExcludedDocUrls_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("excludedDocUrls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_GreaterThan(String excludedDocUrls) {
        setExcludedDocUrls_GreaterThan(excludedDocUrls, null);
    }

    public void setExcludedDocUrls_GreaterThan(String excludedDocUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("excludedDocUrls", ConditionKey.CK_GREATER_THAN, excludedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_LessThan(String excludedDocUrls) {
        setExcludedDocUrls_LessThan(excludedDocUrls, null);
    }

    public void setExcludedDocUrls_LessThan(String excludedDocUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("excludedDocUrls", ConditionKey.CK_LESS_THAN, excludedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_GreaterEqual(String excludedDocUrls) {
        setExcludedDocUrls_GreaterEqual(excludedDocUrls, null);
    }

    public void setExcludedDocUrls_GreaterEqual(String excludedDocUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("excludedDocUrls", ConditionKey.CK_GREATER_EQUAL, excludedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocUrls_LessEqual(String excludedDocUrls) {
        setExcludedDocUrls_LessEqual(excludedDocUrls, null);
    }

    public void setExcludedDocUrls_LessEqual(String excludedDocUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("excludedDocUrls", ConditionKey.CK_LESS_EQUAL, excludedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_NotEqual(String excludedUrls) {
        setExcludedUrls_NotEqual(excludedUrls, null, null);
    }

    public void setExcludedUrls_NotEqual(String excludedUrls, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setExcludedUrls_Equal(excludedUrls, eqOpLambda);
        }, notOpLambda);
    }

    public void setExcludedUrls_Equal(String excludedUrls) {
        setExcludedUrls_Term(excludedUrls, null);
    }

    public void setExcludedUrls_Equal(String excludedUrls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setExcludedUrls_Term(excludedUrls, opLambda);
    }

    public void setExcludedUrls_Term(String excludedUrls) {
        setExcludedUrls_Term(excludedUrls, null);
    }

    public void setExcludedUrls_Term(String excludedUrls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("excludedUrls", excludedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_Terms(Collection<String> excludedUrlsList) {
        setExcludedUrls_Terms(excludedUrlsList, null);
    }

    public void setExcludedUrls_Terms(Collection<String> excludedUrlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("excludedUrls", excludedUrlsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_InScope(Collection<String> excludedUrlsList) {
        setExcludedUrls_Terms(excludedUrlsList, null);
    }

    public void setExcludedUrls_InScope(Collection<String> excludedUrlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setExcludedUrls_Terms(excludedUrlsList, opLambda);
    }

    public void setExcludedUrls_Prefix(String excludedUrls) {
        setExcludedUrls_Prefix(excludedUrls, null);
    }

    public void setExcludedUrls_Prefix(String excludedUrls, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("excludedUrls", excludedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_Exists() {
        setExcludedUrls_Exists(null);
    }

    public void setExcludedUrls_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("excludedUrls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_Missing() {
        setExcludedUrls_Missing(null);
    }

    public void setExcludedUrls_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("excludedUrls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_GreaterThan(String excludedUrls) {
        setExcludedUrls_GreaterThan(excludedUrls, null);
    }

    public void setExcludedUrls_GreaterThan(String excludedUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("excludedUrls", ConditionKey.CK_GREATER_THAN, excludedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_LessThan(String excludedUrls) {
        setExcludedUrls_LessThan(excludedUrls, null);
    }

    public void setExcludedUrls_LessThan(String excludedUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("excludedUrls", ConditionKey.CK_LESS_THAN, excludedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_GreaterEqual(String excludedUrls) {
        setExcludedUrls_GreaterEqual(excludedUrls, null);
    }

    public void setExcludedUrls_GreaterEqual(String excludedUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("excludedUrls", ConditionKey.CK_GREATER_EQUAL, excludedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedUrls_LessEqual(String excludedUrls) {
        setExcludedUrls_LessEqual(excludedUrls, null);
    }

    public void setExcludedUrls_LessEqual(String excludedUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("excludedUrls", ConditionKey.CK_LESS_EQUAL, excludedUrls);
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

    public void setIncludedDocUrls_NotEqual(String includedDocUrls) {
        setIncludedDocUrls_NotEqual(includedDocUrls, null, null);
    }

    public void setIncludedDocUrls_NotEqual(String includedDocUrls, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setIncludedDocUrls_Equal(includedDocUrls, eqOpLambda);
        }, notOpLambda);
    }

    public void setIncludedDocUrls_Equal(String includedDocUrls) {
        setIncludedDocUrls_Term(includedDocUrls, null);
    }

    public void setIncludedDocUrls_Equal(String includedDocUrls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setIncludedDocUrls_Term(includedDocUrls, opLambda);
    }

    public void setIncludedDocUrls_Term(String includedDocUrls) {
        setIncludedDocUrls_Term(includedDocUrls, null);
    }

    public void setIncludedDocUrls_Term(String includedDocUrls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("includedDocUrls", includedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocUrls_Terms(Collection<String> includedDocUrlsList) {
        setIncludedDocUrls_Terms(includedDocUrlsList, null);
    }

    public void setIncludedDocUrls_Terms(Collection<String> includedDocUrlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("includedDocUrls", includedDocUrlsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocUrls_InScope(Collection<String> includedDocUrlsList) {
        setIncludedDocUrls_Terms(includedDocUrlsList, null);
    }

    public void setIncludedDocUrls_InScope(Collection<String> includedDocUrlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setIncludedDocUrls_Terms(includedDocUrlsList, opLambda);
    }

    public void setIncludedDocUrls_Prefix(String includedDocUrls) {
        setIncludedDocUrls_Prefix(includedDocUrls, null);
    }

    public void setIncludedDocUrls_Prefix(String includedDocUrls, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("includedDocUrls", includedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocUrls_Exists() {
        setIncludedDocUrls_Exists(null);
    }

    public void setIncludedDocUrls_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("includedDocUrls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocUrls_Missing() {
        setIncludedDocUrls_Missing(null);
    }

    public void setIncludedDocUrls_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("includedDocUrls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocUrls_GreaterThan(String includedDocUrls) {
        setIncludedDocUrls_GreaterThan(includedDocUrls, null);
    }

    public void setIncludedDocUrls_GreaterThan(String includedDocUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("includedDocUrls", ConditionKey.CK_GREATER_THAN, includedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocUrls_LessThan(String includedDocUrls) {
        setIncludedDocUrls_LessThan(includedDocUrls, null);
    }

    public void setIncludedDocUrls_LessThan(String includedDocUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("includedDocUrls", ConditionKey.CK_LESS_THAN, includedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocUrls_GreaterEqual(String includedDocUrls) {
        setIncludedDocUrls_GreaterEqual(includedDocUrls, null);
    }

    public void setIncludedDocUrls_GreaterEqual(String includedDocUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("includedDocUrls", ConditionKey.CK_GREATER_EQUAL, includedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocUrls_LessEqual(String includedDocUrls) {
        setIncludedDocUrls_LessEqual(includedDocUrls, null);
    }

    public void setIncludedDocUrls_LessEqual(String includedDocUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("includedDocUrls", ConditionKey.CK_LESS_EQUAL, includedDocUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_NotEqual(String includedUrls) {
        setIncludedUrls_NotEqual(includedUrls, null, null);
    }

    public void setIncludedUrls_NotEqual(String includedUrls, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setIncludedUrls_Equal(includedUrls, eqOpLambda);
        }, notOpLambda);
    }

    public void setIncludedUrls_Equal(String includedUrls) {
        setIncludedUrls_Term(includedUrls, null);
    }

    public void setIncludedUrls_Equal(String includedUrls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setIncludedUrls_Term(includedUrls, opLambda);
    }

    public void setIncludedUrls_Term(String includedUrls) {
        setIncludedUrls_Term(includedUrls, null);
    }

    public void setIncludedUrls_Term(String includedUrls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("includedUrls", includedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_Terms(Collection<String> includedUrlsList) {
        setIncludedUrls_Terms(includedUrlsList, null);
    }

    public void setIncludedUrls_Terms(Collection<String> includedUrlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("includedUrls", includedUrlsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_InScope(Collection<String> includedUrlsList) {
        setIncludedUrls_Terms(includedUrlsList, null);
    }

    public void setIncludedUrls_InScope(Collection<String> includedUrlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setIncludedUrls_Terms(includedUrlsList, opLambda);
    }

    public void setIncludedUrls_Prefix(String includedUrls) {
        setIncludedUrls_Prefix(includedUrls, null);
    }

    public void setIncludedUrls_Prefix(String includedUrls, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("includedUrls", includedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_Exists() {
        setIncludedUrls_Exists(null);
    }

    public void setIncludedUrls_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("includedUrls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_Missing() {
        setIncludedUrls_Missing(null);
    }

    public void setIncludedUrls_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("includedUrls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_GreaterThan(String includedUrls) {
        setIncludedUrls_GreaterThan(includedUrls, null);
    }

    public void setIncludedUrls_GreaterThan(String includedUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("includedUrls", ConditionKey.CK_GREATER_THAN, includedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_LessThan(String includedUrls) {
        setIncludedUrls_LessThan(includedUrls, null);
    }

    public void setIncludedUrls_LessThan(String includedUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("includedUrls", ConditionKey.CK_LESS_THAN, includedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_GreaterEqual(String includedUrls) {
        setIncludedUrls_GreaterEqual(includedUrls, null);
    }

    public void setIncludedUrls_GreaterEqual(String includedUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("includedUrls", ConditionKey.CK_GREATER_EQUAL, includedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedUrls_LessEqual(String includedUrls) {
        setIncludedUrls_LessEqual(includedUrls, null);
    }

    public void setIncludedUrls_LessEqual(String includedUrls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("includedUrls", ConditionKey.CK_LESS_EQUAL, includedUrls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_NotEqual(Integer intervalTime) {
        setIntervalTime_NotEqual(intervalTime, null, null);
    }

    public void setIntervalTime_NotEqual(Integer intervalTime, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setIntervalTime_Equal(intervalTime, eqOpLambda);
        }, notOpLambda);
    }

    public void setIntervalTime_Equal(Integer intervalTime) {
        setIntervalTime_Term(intervalTime, null);
    }

    public void setIntervalTime_Equal(Integer intervalTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setIntervalTime_Term(intervalTime, opLambda);
    }

    public void setIntervalTime_Term(Integer intervalTime) {
        setIntervalTime_Term(intervalTime, null);
    }

    public void setIntervalTime_Term(Integer intervalTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("intervalTime", intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Terms(Collection<Integer> intervalTimeList) {
        setIntervalTime_Terms(intervalTimeList, null);
    }

    public void setIntervalTime_Terms(Collection<Integer> intervalTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("intervalTime", intervalTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_InScope(Collection<Integer> intervalTimeList) {
        setIntervalTime_Terms(intervalTimeList, null);
    }

    public void setIntervalTime_InScope(Collection<Integer> intervalTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setIntervalTime_Terms(intervalTimeList, opLambda);
    }

    public void setIntervalTime_Exists() {
        setIntervalTime_Exists(null);
    }

    public void setIntervalTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Missing() {
        setIntervalTime_Missing(null);
    }

    public void setIntervalTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_GreaterThan(Integer intervalTime) {
        setIntervalTime_GreaterThan(intervalTime, null);
    }

    public void setIntervalTime_GreaterThan(Integer intervalTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("intervalTime", ConditionKey.CK_GREATER_THAN, intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_LessThan(Integer intervalTime) {
        setIntervalTime_LessThan(intervalTime, null);
    }

    public void setIntervalTime_LessThan(Integer intervalTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("intervalTime", ConditionKey.CK_LESS_THAN, intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_GreaterEqual(Integer intervalTime) {
        setIntervalTime_GreaterEqual(intervalTime, null);
    }

    public void setIntervalTime_GreaterEqual(Integer intervalTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("intervalTime", ConditionKey.CK_GREATER_EQUAL, intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_LessEqual(Integer intervalTime) {
        setIntervalTime_LessEqual(intervalTime, null);
    }

    public void setIntervalTime_LessEqual(Integer intervalTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("intervalTime", ConditionKey.CK_LESS_EQUAL, intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_NotEqual(Long maxAccessCount) {
        setMaxAccessCount_NotEqual(maxAccessCount, null, null);
    }

    public void setMaxAccessCount_NotEqual(Long maxAccessCount, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setMaxAccessCount_Equal(maxAccessCount, eqOpLambda);
        }, notOpLambda);
    }

    public void setMaxAccessCount_Equal(Long maxAccessCount) {
        setMaxAccessCount_Term(maxAccessCount, null);
    }

    public void setMaxAccessCount_Equal(Long maxAccessCount, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setMaxAccessCount_Term(maxAccessCount, opLambda);
    }

    public void setMaxAccessCount_Term(Long maxAccessCount) {
        setMaxAccessCount_Term(maxAccessCount, null);
    }

    public void setMaxAccessCount_Term(Long maxAccessCount, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("maxAccessCount", maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Terms(Collection<Long> maxAccessCountList) {
        setMaxAccessCount_Terms(maxAccessCountList, null);
    }

    public void setMaxAccessCount_Terms(Collection<Long> maxAccessCountList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("maxAccessCount", maxAccessCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_InScope(Collection<Long> maxAccessCountList) {
        setMaxAccessCount_Terms(maxAccessCountList, null);
    }

    public void setMaxAccessCount_InScope(Collection<Long> maxAccessCountList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setMaxAccessCount_Terms(maxAccessCountList, opLambda);
    }

    public void setMaxAccessCount_Exists() {
        setMaxAccessCount_Exists(null);
    }

    public void setMaxAccessCount_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Missing() {
        setMaxAccessCount_Missing(null);
    }

    public void setMaxAccessCount_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_GreaterThan(Long maxAccessCount) {
        setMaxAccessCount_GreaterThan(maxAccessCount, null);
    }

    public void setMaxAccessCount_GreaterThan(Long maxAccessCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("maxAccessCount", ConditionKey.CK_GREATER_THAN, maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_LessThan(Long maxAccessCount) {
        setMaxAccessCount_LessThan(maxAccessCount, null);
    }

    public void setMaxAccessCount_LessThan(Long maxAccessCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("maxAccessCount", ConditionKey.CK_LESS_THAN, maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_GreaterEqual(Long maxAccessCount) {
        setMaxAccessCount_GreaterEqual(maxAccessCount, null);
    }

    public void setMaxAccessCount_GreaterEqual(Long maxAccessCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("maxAccessCount", ConditionKey.CK_GREATER_EQUAL, maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_LessEqual(Long maxAccessCount) {
        setMaxAccessCount_LessEqual(maxAccessCount, null);
    }

    public void setMaxAccessCount_LessEqual(Long maxAccessCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("maxAccessCount", ConditionKey.CK_LESS_EQUAL, maxAccessCount);
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

    public void setNumOfThread_NotEqual(Integer numOfThread) {
        setNumOfThread_NotEqual(numOfThread, null, null);
    }

    public void setNumOfThread_NotEqual(Integer numOfThread, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setNumOfThread_Equal(numOfThread, eqOpLambda);
        }, notOpLambda);
    }

    public void setNumOfThread_Equal(Integer numOfThread) {
        setNumOfThread_Term(numOfThread, null);
    }

    public void setNumOfThread_Equal(Integer numOfThread, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setNumOfThread_Term(numOfThread, opLambda);
    }

    public void setNumOfThread_Term(Integer numOfThread) {
        setNumOfThread_Term(numOfThread, null);
    }

    public void setNumOfThread_Term(Integer numOfThread, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("numOfThread", numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Terms(Collection<Integer> numOfThreadList) {
        setNumOfThread_Terms(numOfThreadList, null);
    }

    public void setNumOfThread_Terms(Collection<Integer> numOfThreadList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("numOfThread", numOfThreadList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_InScope(Collection<Integer> numOfThreadList) {
        setNumOfThread_Terms(numOfThreadList, null);
    }

    public void setNumOfThread_InScope(Collection<Integer> numOfThreadList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setNumOfThread_Terms(numOfThreadList, opLambda);
    }

    public void setNumOfThread_Exists() {
        setNumOfThread_Exists(null);
    }

    public void setNumOfThread_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Missing() {
        setNumOfThread_Missing(null);
    }

    public void setNumOfThread_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_GreaterThan(Integer numOfThread) {
        setNumOfThread_GreaterThan(numOfThread, null);
    }

    public void setNumOfThread_GreaterThan(Integer numOfThread, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("numOfThread", ConditionKey.CK_GREATER_THAN, numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_LessThan(Integer numOfThread) {
        setNumOfThread_LessThan(numOfThread, null);
    }

    public void setNumOfThread_LessThan(Integer numOfThread, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("numOfThread", ConditionKey.CK_LESS_THAN, numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_GreaterEqual(Integer numOfThread) {
        setNumOfThread_GreaterEqual(numOfThread, null);
    }

    public void setNumOfThread_GreaterEqual(Integer numOfThread, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("numOfThread", ConditionKey.CK_GREATER_EQUAL, numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_LessEqual(Integer numOfThread) {
        setNumOfThread_LessEqual(numOfThread, null);
    }

    public void setNumOfThread_LessEqual(Integer numOfThread, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("numOfThread", ConditionKey.CK_LESS_EQUAL, numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_NotEqual(Integer sortOrder) {
        setSortOrder_NotEqual(sortOrder, null, null);
    }

    public void setSortOrder_NotEqual(Integer sortOrder, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setSortOrder_Equal(sortOrder, eqOpLambda);
        }, notOpLambda);
    }

    public void setSortOrder_Equal(Integer sortOrder) {
        setSortOrder_Term(sortOrder, null);
    }

    public void setSortOrder_Equal(Integer sortOrder, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setSortOrder_Term(sortOrder, opLambda);
    }

    public void setSortOrder_Term(Integer sortOrder) {
        setSortOrder_Term(sortOrder, null);
    }

    public void setSortOrder_Term(Integer sortOrder, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Terms(Collection<Integer> sortOrderList) {
        setSortOrder_Terms(sortOrderList, null);
    }

    public void setSortOrder_Terms(Collection<Integer> sortOrderList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("sortOrder", sortOrderList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_InScope(Collection<Integer> sortOrderList) {
        setSortOrder_Terms(sortOrderList, null);
    }

    public void setSortOrder_InScope(Collection<Integer> sortOrderList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setSortOrder_Terms(sortOrderList, opLambda);
    }

    public void setSortOrder_Exists() {
        setSortOrder_Exists(null);
    }

    public void setSortOrder_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Missing() {
        setSortOrder_Missing(null);
    }

    public void setSortOrder_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_GreaterThan(Integer sortOrder) {
        setSortOrder_GreaterThan(sortOrder, null);
    }

    public void setSortOrder_GreaterThan(Integer sortOrder, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("sortOrder", ConditionKey.CK_GREATER_THAN, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_LessThan(Integer sortOrder) {
        setSortOrder_LessThan(sortOrder, null);
    }

    public void setSortOrder_LessThan(Integer sortOrder, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("sortOrder", ConditionKey.CK_LESS_THAN, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_GreaterEqual(Integer sortOrder) {
        setSortOrder_GreaterEqual(sortOrder, null);
    }

    public void setSortOrder_GreaterEqual(Integer sortOrder, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("sortOrder", ConditionKey.CK_GREATER_EQUAL, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_LessEqual(Integer sortOrder) {
        setSortOrder_LessEqual(sortOrder, null);
    }

    public void setSortOrder_LessEqual(Integer sortOrder, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("sortOrder", ConditionKey.CK_LESS_EQUAL, sortOrder);
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

    public void setUrls_NotEqual(String urls) {
        setUrls_NotEqual(urls, null, null);
    }

    public void setUrls_NotEqual(String urls, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setUrls_Equal(urls, eqOpLambda);
        }, notOpLambda);
    }

    public void setUrls_Equal(String urls) {
        setUrls_Term(urls, null);
    }

    public void setUrls_Equal(String urls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setUrls_Term(urls, opLambda);
    }

    public void setUrls_Term(String urls) {
        setUrls_Term(urls, null);
    }

    public void setUrls_Term(String urls, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("urls", urls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrls_Terms(Collection<String> urlsList) {
        setUrls_Terms(urlsList, null);
    }

    public void setUrls_Terms(Collection<String> urlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("urls", urlsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrls_InScope(Collection<String> urlsList) {
        setUrls_Terms(urlsList, null);
    }

    public void setUrls_InScope(Collection<String> urlsList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setUrls_Terms(urlsList, opLambda);
    }

    public void setUrls_Prefix(String urls) {
        setUrls_Prefix(urls, null);
    }

    public void setUrls_Prefix(String urls, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("urls", urls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrls_Exists() {
        setUrls_Exists(null);
    }

    public void setUrls_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("urls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrls_Missing() {
        setUrls_Missing(null);
    }

    public void setUrls_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("urls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrls_GreaterThan(String urls) {
        setUrls_GreaterThan(urls, null);
    }

    public void setUrls_GreaterThan(String urls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("urls", ConditionKey.CK_GREATER_THAN, urls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrls_LessThan(String urls) {
        setUrls_LessThan(urls, null);
    }

    public void setUrls_LessThan(String urls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("urls", ConditionKey.CK_LESS_THAN, urls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrls_GreaterEqual(String urls) {
        setUrls_GreaterEqual(urls, null);
    }

    public void setUrls_GreaterEqual(String urls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("urls", ConditionKey.CK_GREATER_EQUAL, urls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrls_LessEqual(String urls) {
        setUrls_LessEqual(urls, null);
    }

    public void setUrls_LessEqual(String urls, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("urls", ConditionKey.CK_LESS_EQUAL, urls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_NotEqual(String userAgent) {
        setUserAgent_NotEqual(userAgent, null, null);
    }

    public void setUserAgent_NotEqual(String userAgent, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setUserAgent_Equal(userAgent, eqOpLambda);
        }, notOpLambda);
    }

    public void setUserAgent_Equal(String userAgent) {
        setUserAgent_Term(userAgent, null);
    }

    public void setUserAgent_Equal(String userAgent, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setUserAgent_Term(userAgent, opLambda);
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

}
