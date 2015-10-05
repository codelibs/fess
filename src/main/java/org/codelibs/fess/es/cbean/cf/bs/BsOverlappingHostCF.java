package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.OverlappingHostCF;
import org.codelibs.fess.es.cbean.cq.OverlappingHostCQ;
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
public abstract class BsOverlappingHostCF extends AbstractConditionFilter {

    public void bool(BoolCall<OverlappingHostCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<OverlappingHostCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        OverlappingHostCF mustFilter = new OverlappingHostCF();
        OverlappingHostCF shouldFilter = new OverlappingHostCF();
        OverlappingHostCF mustNotFilter = new OverlappingHostCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<OverlappingHostCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<OverlappingHostCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        OverlappingHostCF andFilter = new OverlappingHostCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<OverlappingHostCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<OverlappingHostCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        OverlappingHostCF orFilter = new OverlappingHostCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<OverlappingHostCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<OverlappingHostCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        OverlappingHostCF notFilter = new OverlappingHostCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<OverlappingHostCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<OverlappingHostCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        OverlappingHostCQ query = new OverlappingHostCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
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

    public void setOverlappingName_NotEqual(String overlappingName) {
        setOverlappingName_NotEqual(overlappingName, null, null);
    }

    public void setOverlappingName_NotEqual(String overlappingName, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setOverlappingName_Equal(overlappingName, eqOpLambda);
        }, notOpLambda);
    }

    public void setOverlappingName_Equal(String overlappingName) {
        setOverlappingName_Term(overlappingName, null);
    }

    public void setOverlappingName_Equal(String overlappingName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setOverlappingName_Term(overlappingName, opLambda);
    }

    public void setOverlappingName_Term(String overlappingName) {
        setOverlappingName_Term(overlappingName, null);
    }

    public void setOverlappingName_Term(String overlappingName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("overlappingName", overlappingName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOverlappingName_Terms(Collection<String> overlappingNameList) {
        setOverlappingName_Terms(overlappingNameList, null);
    }

    public void setOverlappingName_Terms(Collection<String> overlappingNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("overlappingName", overlappingNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOverlappingName_InScope(Collection<String> overlappingNameList) {
        setOverlappingName_Terms(overlappingNameList, null);
    }

    public void setOverlappingName_InScope(Collection<String> overlappingNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setOverlappingName_Terms(overlappingNameList, opLambda);
    }

    public void setOverlappingName_Prefix(String overlappingName) {
        setOverlappingName_Prefix(overlappingName, null);
    }

    public void setOverlappingName_Prefix(String overlappingName, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("overlappingName", overlappingName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOverlappingName_Exists() {
        setOverlappingName_Exists(null);
    }

    public void setOverlappingName_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("overlappingName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOverlappingName_Missing() {
        setOverlappingName_Missing(null);
    }

    public void setOverlappingName_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("overlappingName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOverlappingName_GreaterThan(String overlappingName) {
        setOverlappingName_GreaterThan(overlappingName, null);
    }

    public void setOverlappingName_GreaterThan(String overlappingName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("overlappingName", ConditionKey.CK_GREATER_THAN, overlappingName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOverlappingName_LessThan(String overlappingName) {
        setOverlappingName_LessThan(overlappingName, null);
    }

    public void setOverlappingName_LessThan(String overlappingName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("overlappingName", ConditionKey.CK_LESS_THAN, overlappingName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOverlappingName_GreaterEqual(String overlappingName) {
        setOverlappingName_GreaterEqual(overlappingName, null);
    }

    public void setOverlappingName_GreaterEqual(String overlappingName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("overlappingName", ConditionKey.CK_GREATER_EQUAL, overlappingName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOverlappingName_LessEqual(String overlappingName) {
        setOverlappingName_LessEqual(overlappingName, null);
    }

    public void setOverlappingName_LessEqual(String overlappingName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("overlappingName", ConditionKey.CK_LESS_EQUAL, overlappingName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_NotEqual(String regularName) {
        setRegularName_NotEqual(regularName, null, null);
    }

    public void setRegularName_NotEqual(String regularName, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setRegularName_Equal(regularName, eqOpLambda);
        }, notOpLambda);
    }

    public void setRegularName_Equal(String regularName) {
        setRegularName_Term(regularName, null);
    }

    public void setRegularName_Equal(String regularName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setRegularName_Term(regularName, opLambda);
    }

    public void setRegularName_Term(String regularName) {
        setRegularName_Term(regularName, null);
    }

    public void setRegularName_Term(String regularName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("regularName", regularName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_Terms(Collection<String> regularNameList) {
        setRegularName_Terms(regularNameList, null);
    }

    public void setRegularName_Terms(Collection<String> regularNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("regularName", regularNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_InScope(Collection<String> regularNameList) {
        setRegularName_Terms(regularNameList, null);
    }

    public void setRegularName_InScope(Collection<String> regularNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setRegularName_Terms(regularNameList, opLambda);
    }

    public void setRegularName_Prefix(String regularName) {
        setRegularName_Prefix(regularName, null);
    }

    public void setRegularName_Prefix(String regularName, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("regularName", regularName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_Exists() {
        setRegularName_Exists(null);
    }

    public void setRegularName_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("regularName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_Missing() {
        setRegularName_Missing(null);
    }

    public void setRegularName_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("regularName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_GreaterThan(String regularName) {
        setRegularName_GreaterThan(regularName, null);
    }

    public void setRegularName_GreaterThan(String regularName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("regularName", ConditionKey.CK_GREATER_THAN, regularName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_LessThan(String regularName) {
        setRegularName_LessThan(regularName, null);
    }

    public void setRegularName_LessThan(String regularName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("regularName", ConditionKey.CK_LESS_THAN, regularName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_GreaterEqual(String regularName) {
        setRegularName_GreaterEqual(regularName, null);
    }

    public void setRegularName_GreaterEqual(String regularName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("regularName", ConditionKey.CK_GREATER_EQUAL, regularName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegularName_LessEqual(String regularName) {
        setRegularName_LessEqual(regularName, null);
    }

    public void setRegularName_LessEqual(String regularName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("regularName", ConditionKey.CK_LESS_EQUAL, regularName);
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

}
