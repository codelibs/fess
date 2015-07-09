package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.FileConfigToRoleCF;
import org.codelibs.fess.es.cbean.cq.FileConfigToRoleCQ;
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
public abstract class BsFileConfigToRoleCF extends AbstractConditionFilter {

    public void bool(BoolCall<FileConfigToRoleCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<FileConfigToRoleCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        FileConfigToRoleCF mustFilter = new FileConfigToRoleCF();
        FileConfigToRoleCF shouldFilter = new FileConfigToRoleCF();
        FileConfigToRoleCF mustNotFilter = new FileConfigToRoleCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<FileConfigToRoleCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<FileConfigToRoleCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        FileConfigToRoleCF andFilter = new FileConfigToRoleCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<FileConfigToRoleCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<FileConfigToRoleCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        FileConfigToRoleCF orFilter = new FileConfigToRoleCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<FileConfigToRoleCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<FileConfigToRoleCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        FileConfigToRoleCF notFilter = new FileConfigToRoleCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<FileConfigToRoleCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<FileConfigToRoleCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        FileConfigToRoleCQ query = new FileConfigToRoleCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setFileConfigId_NotEqual(String fileConfigId) {
        setFileConfigId_NotEqual(fileConfigId, null, null);
    }

    public void setFileConfigId_NotEqual(String fileConfigId, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setFileConfigId_Equal(fileConfigId, eqOpLambda);
        }, notOpLambda);
    }

    public void setFileConfigId_Equal(String fileConfigId) {
        setFileConfigId_Term(fileConfigId, null);
    }

    public void setFileConfigId_Equal(String fileConfigId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setFileConfigId_Term(fileConfigId, opLambda);
    }

    public void setFileConfigId_Term(String fileConfigId) {
        setFileConfigId_Term(fileConfigId, null);
    }

    public void setFileConfigId_Term(String fileConfigId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Terms(Collection<String> fileConfigIdList) {
        setFileConfigId_Terms(fileConfigIdList, null);
    }

    public void setFileConfigId_Terms(Collection<String> fileConfigIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("fileConfigId", fileConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_InScope(Collection<String> fileConfigIdList) {
        setFileConfigId_Terms(fileConfigIdList, null);
    }

    public void setFileConfigId_InScope(Collection<String> fileConfigIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setFileConfigId_Terms(fileConfigIdList, opLambda);
    }

    public void setFileConfigId_Prefix(String fileConfigId) {
        setFileConfigId_Prefix(fileConfigId, null);
    }

    public void setFileConfigId_Prefix(String fileConfigId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Exists() {
        setFileConfigId_Exists(null);
    }

    public void setFileConfigId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Missing() {
        setFileConfigId_Missing(null);
    }

    public void setFileConfigId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_GreaterThan(String fileConfigId) {
        setFileConfigId_GreaterThan(fileConfigId, null);
    }

    public void setFileConfigId_GreaterThan(String fileConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("fileConfigId", ConditionKey.CK_GREATER_THAN, fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_LessThan(String fileConfigId) {
        setFileConfigId_LessThan(fileConfigId, null);
    }

    public void setFileConfigId_LessThan(String fileConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("fileConfigId", ConditionKey.CK_LESS_THAN, fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_GreaterEqual(String fileConfigId) {
        setFileConfigId_GreaterEqual(fileConfigId, null);
    }

    public void setFileConfigId_GreaterEqual(String fileConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("fileConfigId", ConditionKey.CK_GREATER_EQUAL, fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_LessEqual(String fileConfigId) {
        setFileConfigId_LessEqual(fileConfigId, null);
    }

    public void setFileConfigId_LessEqual(String fileConfigId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("fileConfigId", ConditionKey.CK_LESS_EQUAL, fileConfigId);
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

    public void setRoleTypeId_NotEqual(String roleTypeId) {
        setRoleTypeId_NotEqual(roleTypeId, null, null);
    }

    public void setRoleTypeId_NotEqual(String roleTypeId, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setRoleTypeId_Equal(roleTypeId, eqOpLambda);
        }, notOpLambda);
    }

    public void setRoleTypeId_Equal(String roleTypeId) {
        setRoleTypeId_Term(roleTypeId, null);
    }

    public void setRoleTypeId_Equal(String roleTypeId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setRoleTypeId_Term(roleTypeId, opLambda);
    }

    public void setRoleTypeId_Term(String roleTypeId) {
        setRoleTypeId_Term(roleTypeId, null);
    }

    public void setRoleTypeId_Term(String roleTypeId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Terms(Collection<String> roleTypeIdList) {
        setRoleTypeId_Terms(roleTypeIdList, null);
    }

    public void setRoleTypeId_Terms(Collection<String> roleTypeIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("roleTypeId", roleTypeIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_InScope(Collection<String> roleTypeIdList) {
        setRoleTypeId_Terms(roleTypeIdList, null);
    }

    public void setRoleTypeId_InScope(Collection<String> roleTypeIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setRoleTypeId_Terms(roleTypeIdList, opLambda);
    }

    public void setRoleTypeId_Prefix(String roleTypeId) {
        setRoleTypeId_Prefix(roleTypeId, null);
    }

    public void setRoleTypeId_Prefix(String roleTypeId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Exists() {
        setRoleTypeId_Exists(null);
    }

    public void setRoleTypeId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Missing() {
        setRoleTypeId_Missing(null);
    }

    public void setRoleTypeId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_GreaterThan(String roleTypeId) {
        setRoleTypeId_GreaterThan(roleTypeId, null);
    }

    public void setRoleTypeId_GreaterThan(String roleTypeId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("roleTypeId", ConditionKey.CK_GREATER_THAN, roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_LessThan(String roleTypeId) {
        setRoleTypeId_LessThan(roleTypeId, null);
    }

    public void setRoleTypeId_LessThan(String roleTypeId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("roleTypeId", ConditionKey.CK_LESS_THAN, roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_GreaterEqual(String roleTypeId) {
        setRoleTypeId_GreaterEqual(roleTypeId, null);
    }

    public void setRoleTypeId_GreaterEqual(String roleTypeId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("roleTypeId", ConditionKey.CK_GREATER_EQUAL, roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_LessEqual(String roleTypeId) {
        setRoleTypeId_LessEqual(roleTypeId, null);
    }

    public void setRoleTypeId_LessEqual(String roleTypeId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("roleTypeId", ConditionKey.CK_LESS_EQUAL, roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
