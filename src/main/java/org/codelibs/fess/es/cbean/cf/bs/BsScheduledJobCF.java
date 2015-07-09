package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.ScheduledJobCF;
import org.codelibs.fess.es.cbean.cq.ScheduledJobCQ;
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
public abstract class BsScheduledJobCF extends AbstractConditionFilter {

    public void bool(BoolCall<ScheduledJobCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<ScheduledJobCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        ScheduledJobCF mustFilter = new ScheduledJobCF();
        ScheduledJobCF shouldFilter = new ScheduledJobCF();
        ScheduledJobCF mustNotFilter = new ScheduledJobCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<ScheduledJobCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<ScheduledJobCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        ScheduledJobCF andFilter = new ScheduledJobCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<ScheduledJobCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<ScheduledJobCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        ScheduledJobCF orFilter = new ScheduledJobCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<ScheduledJobCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<ScheduledJobCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        ScheduledJobCF notFilter = new ScheduledJobCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<ScheduledJobCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<ScheduledJobCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        ScheduledJobCQ query = new ScheduledJobCQ();
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

    public void setCrawler_NotEqual(Boolean crawler) {
        setCrawler_NotEqual(crawler, null, null);
    }

    public void setCrawler_NotEqual(Boolean crawler, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setCrawler_Equal(crawler, eqOpLambda);
        }, notOpLambda);
    }

    public void setCrawler_Equal(Boolean crawler) {
        setCrawler_Term(crawler, null);
    }

    public void setCrawler_Equal(Boolean crawler, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setCrawler_Term(crawler, opLambda);
    }

    public void setCrawler_Term(Boolean crawler) {
        setCrawler_Term(crawler, null);
    }

    public void setCrawler_Term(Boolean crawler, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("crawler", crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Terms(Collection<Boolean> crawlerList) {
        setCrawler_Terms(crawlerList, null);
    }

    public void setCrawler_Terms(Collection<Boolean> crawlerList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("crawler", crawlerList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_InScope(Collection<Boolean> crawlerList) {
        setCrawler_Terms(crawlerList, null);
    }

    public void setCrawler_InScope(Collection<Boolean> crawlerList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setCrawler_Terms(crawlerList, opLambda);
    }

    public void setCrawler_Exists() {
        setCrawler_Exists(null);
    }

    public void setCrawler_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Missing() {
        setCrawler_Missing(null);
    }

    public void setCrawler_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_GreaterThan(Boolean crawler) {
        setCrawler_GreaterThan(crawler, null);
    }

    public void setCrawler_GreaterThan(Boolean crawler, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("crawler", ConditionKey.CK_GREATER_THAN, crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_LessThan(Boolean crawler) {
        setCrawler_LessThan(crawler, null);
    }

    public void setCrawler_LessThan(Boolean crawler, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("crawler", ConditionKey.CK_LESS_THAN, crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_GreaterEqual(Boolean crawler) {
        setCrawler_GreaterEqual(crawler, null);
    }

    public void setCrawler_GreaterEqual(Boolean crawler, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("crawler", ConditionKey.CK_GREATER_EQUAL, crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_LessEqual(Boolean crawler) {
        setCrawler_LessEqual(crawler, null);
    }

    public void setCrawler_LessEqual(Boolean crawler, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("crawler", ConditionKey.CK_LESS_EQUAL, crawler);
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

    public void setCronExpression_NotEqual(String cronExpression) {
        setCronExpression_NotEqual(cronExpression, null, null);
    }

    public void setCronExpression_NotEqual(String cronExpression, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setCronExpression_Equal(cronExpression, eqOpLambda);
        }, notOpLambda);
    }

    public void setCronExpression_Equal(String cronExpression) {
        setCronExpression_Term(cronExpression, null);
    }

    public void setCronExpression_Equal(String cronExpression, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setCronExpression_Term(cronExpression, opLambda);
    }

    public void setCronExpression_Term(String cronExpression) {
        setCronExpression_Term(cronExpression, null);
    }

    public void setCronExpression_Term(String cronExpression, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("cronExpression", cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_Terms(Collection<String> cronExpressionList) {
        setCronExpression_Terms(cronExpressionList, null);
    }

    public void setCronExpression_Terms(Collection<String> cronExpressionList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("cronExpression", cronExpressionList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_InScope(Collection<String> cronExpressionList) {
        setCronExpression_Terms(cronExpressionList, null);
    }

    public void setCronExpression_InScope(Collection<String> cronExpressionList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setCronExpression_Terms(cronExpressionList, opLambda);
    }

    public void setCronExpression_Prefix(String cronExpression) {
        setCronExpression_Prefix(cronExpression, null);
    }

    public void setCronExpression_Prefix(String cronExpression, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("cronExpression", cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_Exists() {
        setCronExpression_Exists(null);
    }

    public void setCronExpression_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("cronExpression");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_Missing() {
        setCronExpression_Missing(null);
    }

    public void setCronExpression_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("cronExpression");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_GreaterThan(String cronExpression) {
        setCronExpression_GreaterThan(cronExpression, null);
    }

    public void setCronExpression_GreaterThan(String cronExpression, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("cronExpression", ConditionKey.CK_GREATER_THAN, cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_LessThan(String cronExpression) {
        setCronExpression_LessThan(cronExpression, null);
    }

    public void setCronExpression_LessThan(String cronExpression, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("cronExpression", ConditionKey.CK_LESS_THAN, cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_GreaterEqual(String cronExpression) {
        setCronExpression_GreaterEqual(cronExpression, null);
    }

    public void setCronExpression_GreaterEqual(String cronExpression, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("cronExpression", ConditionKey.CK_GREATER_EQUAL, cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_LessEqual(String cronExpression) {
        setCronExpression_LessEqual(cronExpression, null);
    }

    public void setCronExpression_LessEqual(String cronExpression, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("cronExpression", ConditionKey.CK_LESS_EQUAL, cronExpression);
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

    public void setJobLogging_NotEqual(Boolean jobLogging) {
        setJobLogging_NotEqual(jobLogging, null, null);
    }

    public void setJobLogging_NotEqual(Boolean jobLogging, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setJobLogging_Equal(jobLogging, eqOpLambda);
        }, notOpLambda);
    }

    public void setJobLogging_Equal(Boolean jobLogging) {
        setJobLogging_Term(jobLogging, null);
    }

    public void setJobLogging_Equal(Boolean jobLogging, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setJobLogging_Term(jobLogging, opLambda);
    }

    public void setJobLogging_Term(Boolean jobLogging) {
        setJobLogging_Term(jobLogging, null);
    }

    public void setJobLogging_Term(Boolean jobLogging, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("jobLogging", jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Terms(Collection<Boolean> jobLoggingList) {
        setJobLogging_Terms(jobLoggingList, null);
    }

    public void setJobLogging_Terms(Collection<Boolean> jobLoggingList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("jobLogging", jobLoggingList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_InScope(Collection<Boolean> jobLoggingList) {
        setJobLogging_Terms(jobLoggingList, null);
    }

    public void setJobLogging_InScope(Collection<Boolean> jobLoggingList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setJobLogging_Terms(jobLoggingList, opLambda);
    }

    public void setJobLogging_Exists() {
        setJobLogging_Exists(null);
    }

    public void setJobLogging_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Missing() {
        setJobLogging_Missing(null);
    }

    public void setJobLogging_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_GreaterThan(Boolean jobLogging) {
        setJobLogging_GreaterThan(jobLogging, null);
    }

    public void setJobLogging_GreaterThan(Boolean jobLogging, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobLogging", ConditionKey.CK_GREATER_THAN, jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_LessThan(Boolean jobLogging) {
        setJobLogging_LessThan(jobLogging, null);
    }

    public void setJobLogging_LessThan(Boolean jobLogging, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobLogging", ConditionKey.CK_LESS_THAN, jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_GreaterEqual(Boolean jobLogging) {
        setJobLogging_GreaterEqual(jobLogging, null);
    }

    public void setJobLogging_GreaterEqual(Boolean jobLogging, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobLogging", ConditionKey.CK_GREATER_EQUAL, jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_LessEqual(Boolean jobLogging) {
        setJobLogging_LessEqual(jobLogging, null);
    }

    public void setJobLogging_LessEqual(Boolean jobLogging, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobLogging", ConditionKey.CK_LESS_EQUAL, jobLogging);
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

    public void setScriptData_NotEqual(String scriptData) {
        setScriptData_NotEqual(scriptData, null, null);
    }

    public void setScriptData_NotEqual(String scriptData, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setScriptData_Equal(scriptData, eqOpLambda);
        }, notOpLambda);
    }

    public void setScriptData_Equal(String scriptData) {
        setScriptData_Term(scriptData, null);
    }

    public void setScriptData_Equal(String scriptData, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setScriptData_Term(scriptData, opLambda);
    }

    public void setScriptData_Term(String scriptData) {
        setScriptData_Term(scriptData, null);
    }

    public void setScriptData_Term(String scriptData, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Terms(Collection<String> scriptDataList) {
        setScriptData_Terms(scriptDataList, null);
    }

    public void setScriptData_Terms(Collection<String> scriptDataList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("scriptData", scriptDataList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_InScope(Collection<String> scriptDataList) {
        setScriptData_Terms(scriptDataList, null);
    }

    public void setScriptData_InScope(Collection<String> scriptDataList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setScriptData_Terms(scriptDataList, opLambda);
    }

    public void setScriptData_Prefix(String scriptData) {
        setScriptData_Prefix(scriptData, null);
    }

    public void setScriptData_Prefix(String scriptData, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Exists() {
        setScriptData_Exists(null);
    }

    public void setScriptData_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Missing() {
        setScriptData_Missing(null);
    }

    public void setScriptData_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterThan(String scriptData) {
        setScriptData_GreaterThan(scriptData, null);
    }

    public void setScriptData_GreaterThan(String scriptData, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptData", ConditionKey.CK_GREATER_THAN, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessThan(String scriptData) {
        setScriptData_LessThan(scriptData, null);
    }

    public void setScriptData_LessThan(String scriptData, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptData", ConditionKey.CK_LESS_THAN, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterEqual(String scriptData) {
        setScriptData_GreaterEqual(scriptData, null);
    }

    public void setScriptData_GreaterEqual(String scriptData, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptData", ConditionKey.CK_GREATER_EQUAL, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessEqual(String scriptData) {
        setScriptData_LessEqual(scriptData, null);
    }

    public void setScriptData_LessEqual(String scriptData, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptData", ConditionKey.CK_LESS_EQUAL, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_NotEqual(String scriptType) {
        setScriptType_NotEqual(scriptType, null, null);
    }

    public void setScriptType_NotEqual(String scriptType, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setScriptType_Equal(scriptType, eqOpLambda);
        }, notOpLambda);
    }

    public void setScriptType_Equal(String scriptType) {
        setScriptType_Term(scriptType, null);
    }

    public void setScriptType_Equal(String scriptType, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setScriptType_Term(scriptType, opLambda);
    }

    public void setScriptType_Term(String scriptType) {
        setScriptType_Term(scriptType, null);
    }

    public void setScriptType_Term(String scriptType, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList) {
        setScriptType_Terms(scriptTypeList, null);
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("scriptType", scriptTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList) {
        setScriptType_Terms(scriptTypeList, null);
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setScriptType_Terms(scriptTypeList, opLambda);
    }

    public void setScriptType_Prefix(String scriptType) {
        setScriptType_Prefix(scriptType, null);
    }

    public void setScriptType_Prefix(String scriptType, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Exists() {
        setScriptType_Exists(null);
    }

    public void setScriptType_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Missing() {
        setScriptType_Missing(null);
    }

    public void setScriptType_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterThan(String scriptType) {
        setScriptType_GreaterThan(scriptType, null);
    }

    public void setScriptType_GreaterThan(String scriptType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptType", ConditionKey.CK_GREATER_THAN, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessThan(String scriptType) {
        setScriptType_LessThan(scriptType, null);
    }

    public void setScriptType_LessThan(String scriptType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptType", ConditionKey.CK_LESS_THAN, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterEqual(String scriptType) {
        setScriptType_GreaterEqual(scriptType, null);
    }

    public void setScriptType_GreaterEqual(String scriptType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptType", ConditionKey.CK_GREATER_EQUAL, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessEqual(String scriptType) {
        setScriptType_LessEqual(scriptType, null);
    }

    public void setScriptType_LessEqual(String scriptType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptType", ConditionKey.CK_LESS_EQUAL, scriptType);
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

    public void setTarget_NotEqual(String target) {
        setTarget_NotEqual(target, null, null);
    }

    public void setTarget_NotEqual(String target, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setTarget_Equal(target, eqOpLambda);
        }, notOpLambda);
    }

    public void setTarget_Equal(String target) {
        setTarget_Term(target, null);
    }

    public void setTarget_Equal(String target, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setTarget_Term(target, opLambda);
    }

    public void setTarget_Term(String target) {
        setTarget_Term(target, null);
    }

    public void setTarget_Term(String target, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Terms(Collection<String> targetList) {
        setTarget_Terms(targetList, null);
    }

    public void setTarget_Terms(Collection<String> targetList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("target", targetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_InScope(Collection<String> targetList) {
        setTarget_Terms(targetList, null);
    }

    public void setTarget_InScope(Collection<String> targetList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setTarget_Terms(targetList, opLambda);
    }

    public void setTarget_Prefix(String target) {
        setTarget_Prefix(target, null);
    }

    public void setTarget_Prefix(String target, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Exists() {
        setTarget_Exists(null);
    }

    public void setTarget_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Missing() {
        setTarget_Missing(null);
    }

    public void setTarget_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterThan(String target) {
        setTarget_GreaterThan(target, null);
    }

    public void setTarget_GreaterThan(String target, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("target", ConditionKey.CK_GREATER_THAN, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessThan(String target) {
        setTarget_LessThan(target, null);
    }

    public void setTarget_LessThan(String target, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("target", ConditionKey.CK_LESS_THAN, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterEqual(String target) {
        setTarget_GreaterEqual(target, null);
    }

    public void setTarget_GreaterEqual(String target, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("target", ConditionKey.CK_GREATER_EQUAL, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessEqual(String target) {
        setTarget_LessEqual(target, null);
    }

    public void setTarget_LessEqual(String target, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("target", ConditionKey.CK_LESS_EQUAL, target);
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
