package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.DataConfigToLabelCF;
import org.codelibs.fess.es.cbean.cq.DataConfigToLabelCQ;
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
public abstract class BsDataConfigToLabelCF extends AbstractConditionFilter {

    public void bool(BoolCall<DataConfigToLabelCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<DataConfigToLabelCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        DataConfigToLabelCF mustFilter = new DataConfigToLabelCF();
        DataConfigToLabelCF shouldFilter = new DataConfigToLabelCF();
        DataConfigToLabelCF mustNotFilter = new DataConfigToLabelCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<DataConfigToLabelCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<DataConfigToLabelCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        DataConfigToLabelCF andFilter = new DataConfigToLabelCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<DataConfigToLabelCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<DataConfigToLabelCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        DataConfigToLabelCF orFilter = new DataConfigToLabelCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<DataConfigToLabelCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<DataConfigToLabelCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        DataConfigToLabelCF notFilter = new DataConfigToLabelCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<DataConfigToLabelCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<DataConfigToLabelCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        DataConfigToLabelCQ query = new DataConfigToLabelCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setDataConfigId_Term(String dataConfigId) {
        setDataConfigId_Term(dataConfigId, null);
    }

    public void setDataConfigId_Term(String dataConfigId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Terms(Collection<String> dataConfigIdList) {
        setDataConfigId_Terms(dataConfigIdList, null);
    }

    public void setDataConfigId_Terms(Collection<String> dataConfigIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("dataConfigId", dataConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_InScope(Collection<String> dataConfigIdList) {
        setDataConfigId_Terms(dataConfigIdList, null);
    }

    public void setDataConfigId_InScope(Collection<String> dataConfigIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setDataConfigId_Terms(dataConfigIdList, opLambda);
    }

    public void setDataConfigId_Prefix(String dataConfigId) {
        setDataConfigId_Prefix(dataConfigId, null);
    }

    public void setDataConfigId_Prefix(String dataConfigId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Exists() {
        setDataConfigId_Exists(null);
    }

    public void setDataConfigId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Missing() {
        setDataConfigId_Missing(null);
    }

    public void setDataConfigId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_GreaterThan(String dataConfigId) {
        setDataConfigId_GreaterThan(dataConfigId, null);
    }

    public void setDataConfigId_GreaterThan(String dataConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("dataConfigId", ConditionKey.CK_GREATER_THAN, dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_LessThan(String dataConfigId) {
        setDataConfigId_LessThan(dataConfigId, null);
    }

    public void setDataConfigId_LessThan(String dataConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("dataConfigId", ConditionKey.CK_LESS_THAN, dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_GreaterEqual(String dataConfigId) {
        setDataConfigId_GreaterEqual(dataConfigId, null);
    }

    public void setDataConfigId_GreaterEqual(String dataConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("dataConfigId", ConditionKey.CK_GREATER_EQUAL, dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_LessEqual(String dataConfigId) {
        setDataConfigId_LessEqual(dataConfigId, null);
    }

    public void setDataConfigId_LessEqual(String dataConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("dataConfigId", ConditionKey.CK_LESS_EQUAL, dataConfigId);
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

    public void setLabelTypeId_Term(String labelTypeId) {
        setLabelTypeId_Term(labelTypeId, null);
    }

    public void setLabelTypeId_Term(String labelTypeId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Terms(Collection<String> labelTypeIdList) {
        setLabelTypeId_Terms(labelTypeIdList, null);
    }

    public void setLabelTypeId_Terms(Collection<String> labelTypeIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("labelTypeId", labelTypeIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_InScope(Collection<String> labelTypeIdList) {
        setLabelTypeId_Terms(labelTypeIdList, null);
    }

    public void setLabelTypeId_InScope(Collection<String> labelTypeIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setLabelTypeId_Terms(labelTypeIdList, opLambda);
    }

    public void setLabelTypeId_Prefix(String labelTypeId) {
        setLabelTypeId_Prefix(labelTypeId, null);
    }

    public void setLabelTypeId_Prefix(String labelTypeId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Exists() {
        setLabelTypeId_Exists(null);
    }

    public void setLabelTypeId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Missing() {
        setLabelTypeId_Missing(null);
    }

    public void setLabelTypeId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_GreaterThan(String labelTypeId) {
        setLabelTypeId_GreaterThan(labelTypeId, null);
    }

    public void setLabelTypeId_GreaterThan(String labelTypeId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("labelTypeId", ConditionKey.CK_GREATER_THAN, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_LessThan(String labelTypeId) {
        setLabelTypeId_LessThan(labelTypeId, null);
    }

    public void setLabelTypeId_LessThan(String labelTypeId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("labelTypeId", ConditionKey.CK_LESS_THAN, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_GreaterEqual(String labelTypeId) {
        setLabelTypeId_GreaterEqual(labelTypeId, null);
    }

    public void setLabelTypeId_GreaterEqual(String labelTypeId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("labelTypeId", ConditionKey.CK_GREATER_EQUAL, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_LessEqual(String labelTypeId) {
        setLabelTypeId_LessEqual(labelTypeId, null);
    }

    public void setLabelTypeId_LessEqual(String labelTypeId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("labelTypeId", ConditionKey.CK_LESS_EQUAL, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
