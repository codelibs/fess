/*
 * Copyright 2009-2013 the Fess Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sf.fess.db.cbean.cq.bs;

import java.util.Collection;

import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.cbean.FailureUrlCB;
import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.cbean.WebAuthenticationCB;
import jp.sf.fess.db.cbean.WebConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.FailureUrlCQ;
import jp.sf.fess.db.cbean.cq.RequestHeaderCQ;
import jp.sf.fess.db.cbean.cq.WebAuthenticationCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;

import org.seasar.dbflute.cbean.AbstractConditionQuery;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.chelper.HpQDRFunction;
import org.seasar.dbflute.cbean.chelper.HpQDRSetupper;
import org.seasar.dbflute.cbean.chelper.HpSSQFunction;
import org.seasar.dbflute.cbean.chelper.HpSSQOption;
import org.seasar.dbflute.cbean.chelper.HpSSQSetupper;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.coption.FromToOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.cbean.coption.RangeOfOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

/**
 * The abstract condition-query of WEB_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsWebCrawlingConfigCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsWebCrawlingConfigCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    @Override
    protected DBMetaProvider xgetDBMetaProvider() {
        return DBMetaInstanceHandler.getProvider();
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    @Override
    public String getTableDbName() {
        return "WEB_CRAWLING_CONFIG";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as equal. (NullAllowed: if null, no condition)
     */
    public void setId_Equal(final Long id) {
        doSetId_Equal(id);
    }

    protected void doSetId_Equal(final Long id) {
        regId(CK_EQ, id);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as notEqual. (NullAllowed: if null, no condition)
     */
    public void setId_NotEqual(final Long id) {
        doSetId_NotEqual(id);
    }

    protected void doSetId_NotEqual(final Long id) {
        regId(CK_NES, id);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterThan(final Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as lessThan. (NullAllowed: if null, no condition)
     */
    public void setId_LessThan(final Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterEqual(final Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setId_LessEqual(final Long id) {
        regId(CK_LE, id);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param minNumber The min number of id. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of id. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setId_RangeOf(final Long minNumber, final Long maxNumber,
            final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueId(), "ID", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param idList The collection of id as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_InScope(final Collection<Long> idList) {
        doSetId_InScope(idList);
    }

    protected void doSetId_InScope(final Collection<Long> idList) {
        regINS(CK_INS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param idList The collection of id as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_NotInScope(final Collection<Long> idList) {
        doSetId_NotInScope(idList);
    }

    protected void doSetId_NotInScope(final Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select WEB_CONFIG_ID from FAILURE_URL where ...)} <br />
     * FAILURE_URL by WEB_CONFIG_ID, named 'failureUrlAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsFailureUrlList</span>(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of FailureUrlList for 'exists'. (NotNull)
     */
    public void existsFailureUrlList(final SubQuery<FailureUrlCB> subQuery) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_FailureUrlList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "failureUrlList");
    }

    public abstract String keepId_ExistsReferrer_FailureUrlList(
            FailureUrlCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select WEB_CRAWLING_CONFIG_ID from REQUEST_HEADER where ...)} <br />
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsRequestHeaderList</span>(new SubQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of RequestHeaderList for 'exists'. (NotNull)
     */
    public void existsRequestHeaderList(final SubQuery<RequestHeaderCB> subQuery) {
        assertObjectNotNull("subQuery<RequestHeaderCB>", subQuery);
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_RequestHeaderList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "WEB_CRAWLING_CONFIG_ID",
                subQueryPropertyName, "requestHeaderList");
    }

    public abstract String keepId_ExistsReferrer_RequestHeaderList(
            RequestHeaderCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select WEB_CRAWLING_CONFIG_ID from WEB_AUTHENTICATION where ...)} <br />
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsWebAuthenticationList</span>(new SubQuery&lt;WebAuthenticationCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of WebAuthenticationList for 'exists'. (NotNull)
     */
    public void existsWebAuthenticationList(
            final SubQuery<WebAuthenticationCB> subQuery) {
        assertObjectNotNull("subQuery<WebAuthenticationCB>", subQuery);
        final WebAuthenticationCB cb = new WebAuthenticationCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_WebAuthenticationList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "WEB_CRAWLING_CONFIG_ID",
                subQueryPropertyName, "webAuthenticationList");
    }

    public abstract String keepId_ExistsReferrer_WebAuthenticationList(
            WebAuthenticationCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select WEB_CONFIG_ID from WEB_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_BROWSER_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToBrowserTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsWebConfigToBrowserTypeMappingList</span>(new SubQuery&lt;WebConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of WebConfigToBrowserTypeMappingList for 'exists'. (NotNull)
     */
    public void existsWebConfigToBrowserTypeMappingList(
            final SubQuery<WebConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToBrowserTypeMappingCB>",
                subQuery);
        final WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_WebConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToBrowserTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_WebConfigToBrowserTypeMappingList(
            WebConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select WEB_CONFIG_ID from WEB_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsWebConfigToLabelTypeMappingList</span>(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of WebConfigToLabelTypeMappingList for 'exists'. (NotNull)
     */
    public void existsWebConfigToLabelTypeMappingList(
            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToLabelTypeMappingCB>", subQuery);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_WebConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToLabelTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_WebConfigToLabelTypeMappingList(
            WebConfigToLabelTypeMappingCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select WEB_CONFIG_ID from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsWebConfigToRoleTypeMappingList</span>(new SubQuery&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of WebConfigToRoleTypeMappingList for 'exists'. (NotNull)
     */
    public void existsWebConfigToRoleTypeMappingList(
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToRoleTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select WEB_CONFIG_ID from FAILURE_URL where ...)} <br />
     * FAILURE_URL by WEB_CONFIG_ID, named 'failureUrlAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsFailureUrlList</span>(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_FailureUrlList for 'not exists'. (NotNull)
     */
    public void notExistsFailureUrlList(final SubQuery<FailureUrlCB> subQuery) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_FailureUrlList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "failureUrlList");
    }

    public abstract String keepId_NotExistsReferrer_FailureUrlList(
            FailureUrlCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select WEB_CRAWLING_CONFIG_ID from REQUEST_HEADER where ...)} <br />
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsRequestHeaderList</span>(new SubQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_RequestHeaderList for 'not exists'. (NotNull)
     */
    public void notExistsRequestHeaderList(
            final SubQuery<RequestHeaderCB> subQuery) {
        assertObjectNotNull("subQuery<RequestHeaderCB>", subQuery);
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_RequestHeaderList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "WEB_CRAWLING_CONFIG_ID",
                subQueryPropertyName, "requestHeaderList");
    }

    public abstract String keepId_NotExistsReferrer_RequestHeaderList(
            RequestHeaderCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select WEB_CRAWLING_CONFIG_ID from WEB_AUTHENTICATION where ...)} <br />
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsWebAuthenticationList</span>(new SubQuery&lt;WebAuthenticationCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_WebAuthenticationList for 'not exists'. (NotNull)
     */
    public void notExistsWebAuthenticationList(
            final SubQuery<WebAuthenticationCB> subQuery) {
        assertObjectNotNull("subQuery<WebAuthenticationCB>", subQuery);
        final WebAuthenticationCB cb = new WebAuthenticationCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_WebAuthenticationList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "WEB_CRAWLING_CONFIG_ID",
                subQueryPropertyName, "webAuthenticationList");
    }

    public abstract String keepId_NotExistsReferrer_WebAuthenticationList(
            WebAuthenticationCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select WEB_CONFIG_ID from WEB_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_BROWSER_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToBrowserTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsWebConfigToBrowserTypeMappingList</span>(new SubQuery&lt;WebConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_WebConfigToBrowserTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsWebConfigToBrowserTypeMappingList(
            final SubQuery<WebConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToBrowserTypeMappingCB>",
                subQuery);
        final WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_WebConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToBrowserTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_WebConfigToBrowserTypeMappingList(
            WebConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select WEB_CONFIG_ID from WEB_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsWebConfigToLabelTypeMappingList</span>(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_WebConfigToLabelTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsWebConfigToLabelTypeMappingList(
            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToLabelTypeMappingCB>", subQuery);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_WebConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToLabelTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_WebConfigToLabelTypeMappingList(
            WebConfigToLabelTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select WEB_CONFIG_ID from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsWebConfigToRoleTypeMappingList</span>(new SubQuery&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_WebConfigToRoleTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsWebConfigToRoleTypeMappingList(
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select WEB_CONFIG_ID from FAILURE_URL where ...)} <br />
     * FAILURE_URL by WEB_CONFIG_ID, named 'failureUrlAsOne'.
     * @param subQuery The sub-query of FailureUrlList for 'in-scope'. (NotNull)
     */
    public void inScopeFailureUrlList(final SubQuery<FailureUrlCB> subQuery) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_FailureUrlList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "failureUrlList");
    }

    public abstract String keepId_InScopeRelation_FailureUrlList(
            FailureUrlCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select WEB_CRAWLING_CONFIG_ID from REQUEST_HEADER where ...)} <br />
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderAsOne'.
     * @param subQuery The sub-query of RequestHeaderList for 'in-scope'. (NotNull)
     */
    public void inScopeRequestHeaderList(
            final SubQuery<RequestHeaderCB> subQuery) {
        assertObjectNotNull("subQuery<RequestHeaderCB>", subQuery);
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_RequestHeaderList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "WEB_CRAWLING_CONFIG_ID",
                subQueryPropertyName, "requestHeaderList");
    }

    public abstract String keepId_InScopeRelation_RequestHeaderList(
            RequestHeaderCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select WEB_CRAWLING_CONFIG_ID from WEB_AUTHENTICATION where ...)} <br />
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationAsOne'.
     * @param subQuery The sub-query of WebAuthenticationList for 'in-scope'. (NotNull)
     */
    public void inScopeWebAuthenticationList(
            final SubQuery<WebAuthenticationCB> subQuery) {
        assertObjectNotNull("subQuery<WebAuthenticationCB>", subQuery);
        final WebAuthenticationCB cb = new WebAuthenticationCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_WebAuthenticationList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "WEB_CRAWLING_CONFIG_ID",
                subQueryPropertyName, "webAuthenticationList");
    }

    public abstract String keepId_InScopeRelation_WebAuthenticationList(
            WebAuthenticationCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select WEB_CONFIG_ID from WEB_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_BROWSER_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToBrowserTypeMappingAsOne'.
     * @param subQuery The sub-query of WebConfigToBrowserTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeWebConfigToBrowserTypeMappingList(
            final SubQuery<WebConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToBrowserTypeMappingCB>",
                subQuery);
        final WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_WebConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToBrowserTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_WebConfigToBrowserTypeMappingList(
            WebConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select WEB_CONFIG_ID from WEB_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingAsOne'.
     * @param subQuery The sub-query of WebConfigToLabelTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeWebConfigToLabelTypeMappingList(
            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToLabelTypeMappingCB>", subQuery);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_WebConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToLabelTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_WebConfigToLabelTypeMappingList(
            WebConfigToLabelTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select WEB_CONFIG_ID from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of WebConfigToRoleTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeWebConfigToRoleTypeMappingList(
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToRoleTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select WEB_CONFIG_ID from FAILURE_URL where ...)} <br />
     * FAILURE_URL by WEB_CONFIG_ID, named 'failureUrlAsOne'.
     * @param subQuery The sub-query of FailureUrlList for 'not in-scope'. (NotNull)
     */
    public void notInScopeFailureUrlList(final SubQuery<FailureUrlCB> subQuery) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_FailureUrlList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "failureUrlList");
    }

    public abstract String keepId_NotInScopeRelation_FailureUrlList(
            FailureUrlCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select WEB_CRAWLING_CONFIG_ID from REQUEST_HEADER where ...)} <br />
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderAsOne'.
     * @param subQuery The sub-query of RequestHeaderList for 'not in-scope'. (NotNull)
     */
    public void notInScopeRequestHeaderList(
            final SubQuery<RequestHeaderCB> subQuery) {
        assertObjectNotNull("subQuery<RequestHeaderCB>", subQuery);
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_RequestHeaderList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "WEB_CRAWLING_CONFIG_ID",
                subQueryPropertyName, "requestHeaderList");
    }

    public abstract String keepId_NotInScopeRelation_RequestHeaderList(
            RequestHeaderCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select WEB_CRAWLING_CONFIG_ID from WEB_AUTHENTICATION where ...)} <br />
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationAsOne'.
     * @param subQuery The sub-query of WebAuthenticationList for 'not in-scope'. (NotNull)
     */
    public void notInScopeWebAuthenticationList(
            final SubQuery<WebAuthenticationCB> subQuery) {
        assertObjectNotNull("subQuery<WebAuthenticationCB>", subQuery);
        final WebAuthenticationCB cb = new WebAuthenticationCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_WebAuthenticationList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "WEB_CRAWLING_CONFIG_ID",
                subQueryPropertyName, "webAuthenticationList");
    }

    public abstract String keepId_NotInScopeRelation_WebAuthenticationList(
            WebAuthenticationCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select WEB_CONFIG_ID from WEB_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_BROWSER_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToBrowserTypeMappingAsOne'.
     * @param subQuery The sub-query of WebConfigToBrowserTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeWebConfigToBrowserTypeMappingList(
            final SubQuery<WebConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToBrowserTypeMappingCB>",
                subQuery);
        final WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_WebConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToBrowserTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_WebConfigToBrowserTypeMappingList(
            WebConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select WEB_CONFIG_ID from WEB_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingAsOne'.
     * @param subQuery The sub-query of WebConfigToLabelTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeWebConfigToLabelTypeMappingList(
            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToLabelTypeMappingCB>", subQuery);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToLabelTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(
            WebConfigToLabelTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select WEB_CONFIG_ID from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of WebConfigToRoleTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeWebConfigToRoleTypeMappingList(
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "WEB_CONFIG_ID",
                subQueryPropertyName, "webConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    public void xsderiveFailureUrlList(final String function,
            final SubQuery<FailureUrlCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_FailureUrlList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "WEB_CONFIG_ID", subQueryPropertyName, "failureUrlList",
                aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_FailureUrlList(
            FailureUrlCQ subQuery);

    public void xsderiveRequestHeaderList(final String function,
            final SubQuery<RequestHeaderCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<RequestHeaderCB>", subQuery);
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_RequestHeaderList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "WEB_CRAWLING_CONFIG_ID", subQueryPropertyName,
                "requestHeaderList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_RequestHeaderList(
            RequestHeaderCQ subQuery);

    public void xsderiveWebAuthenticationList(final String function,
            final SubQuery<WebAuthenticationCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebAuthenticationCB>", subQuery);
        final WebAuthenticationCB cb = new WebAuthenticationCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_WebAuthenticationList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "WEB_CRAWLING_CONFIG_ID", subQueryPropertyName,
                "webAuthenticationList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_WebAuthenticationList(
            WebAuthenticationCQ subQuery);

    public void xsderiveWebConfigToBrowserTypeMappingList(
            final String function,
            final SubQuery<WebConfigToBrowserTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebConfigToBrowserTypeMappingCB>",
                subQuery);
        final WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "WEB_CONFIG_ID", subQueryPropertyName,
                "webConfigToBrowserTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingList(
            WebConfigToBrowserTypeMappingCQ subQuery);

    public void xsderiveWebConfigToLabelTypeMappingList(final String function,
            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebConfigToLabelTypeMappingCB>", subQuery);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "WEB_CONFIG_ID", subQueryPropertyName,
                "webConfigToLabelTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList(
            WebConfigToLabelTypeMappingCQ subQuery);

    public void xsderiveWebConfigToRoleTypeMappingList(final String function,
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "WEB_CONFIG_ID", subQueryPropertyName,
                "webConfigToRoleTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from FAILURE_URL where ...)} <br />
     * FAILURE_URL by WEB_CONFIG_ID, named 'failureUrlAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedFailureUrlList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(FailureUrlCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<FailureUrlCB> derivedFailureUrlList() {
        return xcreateQDRFunctionFailureUrlList();
    }

    protected HpQDRFunction<FailureUrlCB> xcreateQDRFunctionFailureUrlList() {
        return new HpQDRFunction<FailureUrlCB>(
                new HpQDRSetupper<FailureUrlCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<FailureUrlCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveFailureUrlList(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveFailureUrlList(final String function,
            final SubQuery<FailureUrlCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_FailureUrlList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_FailureUrlListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "WEB_CONFIG_ID", subQueryPropertyName, "failureUrlList",
                operand, value, parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_FailureUrlList(
            FailureUrlCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_FailureUrlListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from REQUEST_HEADER where ...)} <br />
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedRequestHeaderList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(RequestHeaderCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<RequestHeaderCB> derivedRequestHeaderList() {
        return xcreateQDRFunctionRequestHeaderList();
    }

    protected HpQDRFunction<RequestHeaderCB> xcreateQDRFunctionRequestHeaderList() {
        return new HpQDRFunction<RequestHeaderCB>(
                new HpQDRSetupper<RequestHeaderCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<RequestHeaderCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveRequestHeaderList(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveRequestHeaderList(final String function,
            final SubQuery<RequestHeaderCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<RequestHeaderCB>", subQuery);
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_RequestHeaderList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_RequestHeaderListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "WEB_CRAWLING_CONFIG_ID", subQueryPropertyName,
                "requestHeaderList", operand, value, parameterPropertyName,
                option);
    }

    public abstract String keepId_QueryDerivedReferrer_RequestHeaderList(
            RequestHeaderCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_RequestHeaderListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from WEB_AUTHENTICATION where ...)} <br />
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedWebAuthenticationList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;WebAuthenticationCB&gt;() {
     *     public void query(WebAuthenticationCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<WebAuthenticationCB> derivedWebAuthenticationList() {
        return xcreateQDRFunctionWebAuthenticationList();
    }

    protected HpQDRFunction<WebAuthenticationCB> xcreateQDRFunctionWebAuthenticationList() {
        return new HpQDRFunction<WebAuthenticationCB>(
                new HpQDRSetupper<WebAuthenticationCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<WebAuthenticationCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveWebAuthenticationList(function, subQuery,
                                operand, value, option);
                    }
                });
    }

    public void xqderiveWebAuthenticationList(final String function,
            final SubQuery<WebAuthenticationCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebAuthenticationCB>", subQuery);
        final WebAuthenticationCB cb = new WebAuthenticationCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_WebAuthenticationList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_WebAuthenticationListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "WEB_CRAWLING_CONFIG_ID", subQueryPropertyName,
                "webAuthenticationList", operand, value, parameterPropertyName,
                option);
    }

    public abstract String keepId_QueryDerivedReferrer_WebAuthenticationList(
            WebAuthenticationCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_WebAuthenticationListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from WEB_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_BROWSER_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToBrowserTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedWebConfigToBrowserTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;WebConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(WebConfigToBrowserTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<WebConfigToBrowserTypeMappingCB> derivedWebConfigToBrowserTypeMappingList() {
        return xcreateQDRFunctionWebConfigToBrowserTypeMappingList();
    }

    protected HpQDRFunction<WebConfigToBrowserTypeMappingCB> xcreateQDRFunctionWebConfigToBrowserTypeMappingList() {
        return new HpQDRFunction<WebConfigToBrowserTypeMappingCB>(
                new HpQDRSetupper<WebConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<WebConfigToBrowserTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveWebConfigToBrowserTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveWebConfigToBrowserTypeMappingList(
            final String function,
            final SubQuery<WebConfigToBrowserTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebConfigToBrowserTypeMappingCB>",
                subQuery);
        final WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "WEB_CONFIG_ID", subQueryPropertyName,
                "webConfigToBrowserTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingList(
            WebConfigToBrowserTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from WEB_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedWebConfigToLabelTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<WebConfigToLabelTypeMappingCB> derivedWebConfigToLabelTypeMappingList() {
        return xcreateQDRFunctionWebConfigToLabelTypeMappingList();
    }

    protected HpQDRFunction<WebConfigToLabelTypeMappingCB> xcreateQDRFunctionWebConfigToLabelTypeMappingList() {
        return new HpQDRFunction<WebConfigToLabelTypeMappingCB>(
                new HpQDRSetupper<WebConfigToLabelTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveWebConfigToLabelTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveWebConfigToLabelTypeMappingList(final String function,
            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebConfigToLabelTypeMappingCB>", subQuery);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "WEB_CONFIG_ID", subQueryPropertyName,
                "webConfigToLabelTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList(
            WebConfigToLabelTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedWebConfigToRoleTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void query(WebConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<WebConfigToRoleTypeMappingCB> derivedWebConfigToRoleTypeMappingList() {
        return xcreateQDRFunctionWebConfigToRoleTypeMappingList();
    }

    protected HpQDRFunction<WebConfigToRoleTypeMappingCB> xcreateQDRFunctionWebConfigToRoleTypeMappingList() {
        return new HpQDRFunction<WebConfigToRoleTypeMappingCB>(
                new HpQDRSetupper<WebConfigToRoleTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveWebConfigToRoleTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveWebConfigToRoleTypeMappingList(final String function,
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "WEB_CONFIG_ID", subQueryPropertyName,
                "webConfigToRoleTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter(
            Object parameterValue);

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     */
    public void setId_IsNull() {
        regId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     */
    public void setId_IsNotNull() {
        regId(CK_ISNN, DOBJ);
    }

    protected void regId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueId(), "ID");
    }

    abstract protected ConditionValue getCValueId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_Equal(final String name) {
        doSetName_Equal(fRES(name));
    }

    protected void doSetName_Equal(final String name) {
        regName(CK_EQ, name);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_NotEqual(final String name) {
        doSetName_NotEqual(fRES(name));
    }

    protected void doSetName_NotEqual(final String name) {
        regName(CK_NES, name);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_GreaterThan(final String name) {
        regName(CK_GT, fRES(name));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_LessThan(final String name) {
        regName(CK_LT, fRES(name));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_GreaterEqual(final String name) {
        regName(CK_GE, fRES(name));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_LessEqual(final String name) {
        regName(CK_LE, fRES(name));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param nameList The collection of name as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_InScope(final Collection<String> nameList) {
        doSetName_InScope(nameList);
    }

    public void doSetName_InScope(final Collection<String> nameList) {
        regINS(CK_INS, cTL(nameList), getCValueName(), "NAME");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param nameList The collection of name as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_NotInScope(final Collection<String> nameList) {
        doSetName_NotInScope(nameList);
    }

    public void doSetName_NotInScope(final Collection<String> nameList) {
        regINS(CK_NINS, cTL(nameList), getCValueName(), "NAME");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_PrefixSearch(final String name) {
        setName_LikeSearch(name, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)} <br />
     * <pre>e.g. setName_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param name The value of name as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setName_LikeSearch(final String name,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(name), getCValueName(), "NAME", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setName_NotLikeSearch(final String name,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(name), getCValueName(), "NAME", likeSearchOption);
    }

    protected void regName(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueName(), "NAME");
    }

    abstract protected ConditionValue getCValueName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urls The value of urls as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_Equal(final String urls) {
        doSetUrls_Equal(fRES(urls));
    }

    protected void doSetUrls_Equal(final String urls) {
        regUrls(CK_EQ, urls);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urls The value of urls as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_NotEqual(final String urls) {
        doSetUrls_NotEqual(fRES(urls));
    }

    protected void doSetUrls_NotEqual(final String urls) {
        regUrls(CK_NES, urls);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urls The value of urls as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_GreaterThan(final String urls) {
        regUrls(CK_GT, fRES(urls));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urls The value of urls as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_LessThan(final String urls) {
        regUrls(CK_LT, fRES(urls));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urls The value of urls as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_GreaterEqual(final String urls) {
        regUrls(CK_GE, fRES(urls));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urls The value of urls as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_LessEqual(final String urls) {
        regUrls(CK_LE, fRES(urls));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urlsList The collection of urls as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_InScope(final Collection<String> urlsList) {
        doSetUrls_InScope(urlsList);
    }

    public void doSetUrls_InScope(final Collection<String> urlsList) {
        regINS(CK_INS, cTL(urlsList), getCValueUrls(), "URLS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urlsList The collection of urls as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_NotInScope(final Collection<String> urlsList) {
        doSetUrls_NotInScope(urlsList);
    }

    public void doSetUrls_NotInScope(final Collection<String> urlsList) {
        regINS(CK_NINS, cTL(urlsList), getCValueUrls(), "URLS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urls The value of urls as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrls_PrefixSearch(final String urls) {
        setUrls_LikeSearch(urls, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)} <br />
     * <pre>e.g. setUrls_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param urls The value of urls as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUrls_LikeSearch(final String urls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(urls), getCValueUrls(), "URLS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @param urls The value of urls as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUrls_NotLikeSearch(final String urls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(urls), getCValueUrls(), "URLS", likeSearchOption);
    }

    protected void regUrls(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUrls(), "URLS");
    }

    abstract protected ConditionValue getCValueUrls();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrls The value of includedUrls as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_Equal(final String includedUrls) {
        doSetIncludedUrls_Equal(fRES(includedUrls));
    }

    protected void doSetIncludedUrls_Equal(final String includedUrls) {
        regIncludedUrls(CK_EQ, includedUrls);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrls The value of includedUrls as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_NotEqual(final String includedUrls) {
        doSetIncludedUrls_NotEqual(fRES(includedUrls));
    }

    protected void doSetIncludedUrls_NotEqual(final String includedUrls) {
        regIncludedUrls(CK_NES, includedUrls);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrls The value of includedUrls as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_GreaterThan(final String includedUrls) {
        regIncludedUrls(CK_GT, fRES(includedUrls));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrls The value of includedUrls as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_LessThan(final String includedUrls) {
        regIncludedUrls(CK_LT, fRES(includedUrls));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrls The value of includedUrls as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_GreaterEqual(final String includedUrls) {
        regIncludedUrls(CK_GE, fRES(includedUrls));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrls The value of includedUrls as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_LessEqual(final String includedUrls) {
        regIncludedUrls(CK_LE, fRES(includedUrls));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrlsList The collection of includedUrls as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_InScope(
            final Collection<String> includedUrlsList) {
        doSetIncludedUrls_InScope(includedUrlsList);
    }

    public void doSetIncludedUrls_InScope(
            final Collection<String> includedUrlsList) {
        regINS(CK_INS, cTL(includedUrlsList), getCValueIncludedUrls(),
                "INCLUDED_URLS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrlsList The collection of includedUrls as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_NotInScope(
            final Collection<String> includedUrlsList) {
        doSetIncludedUrls_NotInScope(includedUrlsList);
    }

    public void doSetIncludedUrls_NotInScope(
            final Collection<String> includedUrlsList) {
        regINS(CK_NINS, cTL(includedUrlsList), getCValueIncludedUrls(),
                "INCLUDED_URLS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrls The value of includedUrls as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedUrls_PrefixSearch(final String includedUrls) {
        setIncludedUrls_LikeSearch(includedUrls, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)} <br />
     * <pre>e.g. setIncludedUrls_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param includedUrls The value of includedUrls as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setIncludedUrls_LikeSearch(final String includedUrls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(includedUrls), getCValueIncludedUrls(),
                "INCLUDED_URLS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @param includedUrls The value of includedUrls as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setIncludedUrls_NotLikeSearch(final String includedUrls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(includedUrls), getCValueIncludedUrls(),
                "INCLUDED_URLS", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     */
    public void setIncludedUrls_IsNull() {
        regIncludedUrls(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     */
    public void setIncludedUrls_IsNullOrEmpty() {
        regIncludedUrls(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     */
    public void setIncludedUrls_IsNotNull() {
        regIncludedUrls(CK_ISNN, DOBJ);
    }

    protected void regIncludedUrls(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueIncludedUrls(), "INCLUDED_URLS");
    }

    abstract protected ConditionValue getCValueIncludedUrls();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrls The value of excludedUrls as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_Equal(final String excludedUrls) {
        doSetExcludedUrls_Equal(fRES(excludedUrls));
    }

    protected void doSetExcludedUrls_Equal(final String excludedUrls) {
        regExcludedUrls(CK_EQ, excludedUrls);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrls The value of excludedUrls as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_NotEqual(final String excludedUrls) {
        doSetExcludedUrls_NotEqual(fRES(excludedUrls));
    }

    protected void doSetExcludedUrls_NotEqual(final String excludedUrls) {
        regExcludedUrls(CK_NES, excludedUrls);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrls The value of excludedUrls as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_GreaterThan(final String excludedUrls) {
        regExcludedUrls(CK_GT, fRES(excludedUrls));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrls The value of excludedUrls as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_LessThan(final String excludedUrls) {
        regExcludedUrls(CK_LT, fRES(excludedUrls));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrls The value of excludedUrls as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_GreaterEqual(final String excludedUrls) {
        regExcludedUrls(CK_GE, fRES(excludedUrls));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrls The value of excludedUrls as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_LessEqual(final String excludedUrls) {
        regExcludedUrls(CK_LE, fRES(excludedUrls));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrlsList The collection of excludedUrls as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_InScope(
            final Collection<String> excludedUrlsList) {
        doSetExcludedUrls_InScope(excludedUrlsList);
    }

    public void doSetExcludedUrls_InScope(
            final Collection<String> excludedUrlsList) {
        regINS(CK_INS, cTL(excludedUrlsList), getCValueExcludedUrls(),
                "EXCLUDED_URLS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrlsList The collection of excludedUrls as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_NotInScope(
            final Collection<String> excludedUrlsList) {
        doSetExcludedUrls_NotInScope(excludedUrlsList);
    }

    public void doSetExcludedUrls_NotInScope(
            final Collection<String> excludedUrlsList) {
        regINS(CK_NINS, cTL(excludedUrlsList), getCValueExcludedUrls(),
                "EXCLUDED_URLS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrls The value of excludedUrls as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedUrls_PrefixSearch(final String excludedUrls) {
        setExcludedUrls_LikeSearch(excludedUrls, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)} <br />
     * <pre>e.g. setExcludedUrls_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param excludedUrls The value of excludedUrls as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setExcludedUrls_LikeSearch(final String excludedUrls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(excludedUrls), getCValueExcludedUrls(),
                "EXCLUDED_URLS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @param excludedUrls The value of excludedUrls as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setExcludedUrls_NotLikeSearch(final String excludedUrls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(excludedUrls), getCValueExcludedUrls(),
                "EXCLUDED_URLS", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     */
    public void setExcludedUrls_IsNull() {
        regExcludedUrls(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     */
    public void setExcludedUrls_IsNullOrEmpty() {
        regExcludedUrls(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     */
    public void setExcludedUrls_IsNotNull() {
        regExcludedUrls(CK_ISNN, DOBJ);
    }

    protected void regExcludedUrls(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueExcludedUrls(), "EXCLUDED_URLS");
    }

    abstract protected ConditionValue getCValueExcludedUrls();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrls The value of includedDocUrls as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_Equal(final String includedDocUrls) {
        doSetIncludedDocUrls_Equal(fRES(includedDocUrls));
    }

    protected void doSetIncludedDocUrls_Equal(final String includedDocUrls) {
        regIncludedDocUrls(CK_EQ, includedDocUrls);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrls The value of includedDocUrls as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_NotEqual(final String includedDocUrls) {
        doSetIncludedDocUrls_NotEqual(fRES(includedDocUrls));
    }

    protected void doSetIncludedDocUrls_NotEqual(final String includedDocUrls) {
        regIncludedDocUrls(CK_NES, includedDocUrls);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrls The value of includedDocUrls as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_GreaterThan(final String includedDocUrls) {
        regIncludedDocUrls(CK_GT, fRES(includedDocUrls));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrls The value of includedDocUrls as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_LessThan(final String includedDocUrls) {
        regIncludedDocUrls(CK_LT, fRES(includedDocUrls));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrls The value of includedDocUrls as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_GreaterEqual(final String includedDocUrls) {
        regIncludedDocUrls(CK_GE, fRES(includedDocUrls));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrls The value of includedDocUrls as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_LessEqual(final String includedDocUrls) {
        regIncludedDocUrls(CK_LE, fRES(includedDocUrls));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrlsList The collection of includedDocUrls as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_InScope(
            final Collection<String> includedDocUrlsList) {
        doSetIncludedDocUrls_InScope(includedDocUrlsList);
    }

    public void doSetIncludedDocUrls_InScope(
            final Collection<String> includedDocUrlsList) {
        regINS(CK_INS, cTL(includedDocUrlsList), getCValueIncludedDocUrls(),
                "INCLUDED_DOC_URLS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrlsList The collection of includedDocUrls as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_NotInScope(
            final Collection<String> includedDocUrlsList) {
        doSetIncludedDocUrls_NotInScope(includedDocUrlsList);
    }

    public void doSetIncludedDocUrls_NotInScope(
            final Collection<String> includedDocUrlsList) {
        regINS(CK_NINS, cTL(includedDocUrlsList), getCValueIncludedDocUrls(),
                "INCLUDED_DOC_URLS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrls The value of includedDocUrls as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocUrls_PrefixSearch(final String includedDocUrls) {
        setIncludedDocUrls_LikeSearch(includedDocUrls, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)} <br />
     * <pre>e.g. setIncludedDocUrls_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param includedDocUrls The value of includedDocUrls as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setIncludedDocUrls_LikeSearch(final String includedDocUrls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(includedDocUrls), getCValueIncludedDocUrls(),
                "INCLUDED_DOC_URLS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param includedDocUrls The value of includedDocUrls as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setIncludedDocUrls_NotLikeSearch(final String includedDocUrls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(includedDocUrls), getCValueIncludedDocUrls(),
                "INCLUDED_DOC_URLS", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     */
    public void setIncludedDocUrls_IsNull() {
        regIncludedDocUrls(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     */
    public void setIncludedDocUrls_IsNullOrEmpty() {
        regIncludedDocUrls(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     */
    public void setIncludedDocUrls_IsNotNull() {
        regIncludedDocUrls(CK_ISNN, DOBJ);
    }

    protected void regIncludedDocUrls(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueIncludedDocUrls(), "INCLUDED_DOC_URLS");
    }

    abstract protected ConditionValue getCValueIncludedDocUrls();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrls The value of excludedDocUrls as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_Equal(final String excludedDocUrls) {
        doSetExcludedDocUrls_Equal(fRES(excludedDocUrls));
    }

    protected void doSetExcludedDocUrls_Equal(final String excludedDocUrls) {
        regExcludedDocUrls(CK_EQ, excludedDocUrls);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrls The value of excludedDocUrls as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_NotEqual(final String excludedDocUrls) {
        doSetExcludedDocUrls_NotEqual(fRES(excludedDocUrls));
    }

    protected void doSetExcludedDocUrls_NotEqual(final String excludedDocUrls) {
        regExcludedDocUrls(CK_NES, excludedDocUrls);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrls The value of excludedDocUrls as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_GreaterThan(final String excludedDocUrls) {
        regExcludedDocUrls(CK_GT, fRES(excludedDocUrls));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrls The value of excludedDocUrls as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_LessThan(final String excludedDocUrls) {
        regExcludedDocUrls(CK_LT, fRES(excludedDocUrls));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrls The value of excludedDocUrls as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_GreaterEqual(final String excludedDocUrls) {
        regExcludedDocUrls(CK_GE, fRES(excludedDocUrls));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrls The value of excludedDocUrls as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_LessEqual(final String excludedDocUrls) {
        regExcludedDocUrls(CK_LE, fRES(excludedDocUrls));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrlsList The collection of excludedDocUrls as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_InScope(
            final Collection<String> excludedDocUrlsList) {
        doSetExcludedDocUrls_InScope(excludedDocUrlsList);
    }

    public void doSetExcludedDocUrls_InScope(
            final Collection<String> excludedDocUrlsList) {
        regINS(CK_INS, cTL(excludedDocUrlsList), getCValueExcludedDocUrls(),
                "EXCLUDED_DOC_URLS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrlsList The collection of excludedDocUrls as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_NotInScope(
            final Collection<String> excludedDocUrlsList) {
        doSetExcludedDocUrls_NotInScope(excludedDocUrlsList);
    }

    public void doSetExcludedDocUrls_NotInScope(
            final Collection<String> excludedDocUrlsList) {
        regINS(CK_NINS, cTL(excludedDocUrlsList), getCValueExcludedDocUrls(),
                "EXCLUDED_DOC_URLS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrls The value of excludedDocUrls as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocUrls_PrefixSearch(final String excludedDocUrls) {
        setExcludedDocUrls_LikeSearch(excludedDocUrls, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)} <br />
     * <pre>e.g. setExcludedDocUrls_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param excludedDocUrls The value of excludedDocUrls as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setExcludedDocUrls_LikeSearch(final String excludedDocUrls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(excludedDocUrls), getCValueExcludedDocUrls(),
                "EXCLUDED_DOC_URLS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @param excludedDocUrls The value of excludedDocUrls as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setExcludedDocUrls_NotLikeSearch(final String excludedDocUrls,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(excludedDocUrls), getCValueExcludedDocUrls(),
                "EXCLUDED_DOC_URLS", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     */
    public void setExcludedDocUrls_IsNull() {
        regExcludedDocUrls(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     */
    public void setExcludedDocUrls_IsNullOrEmpty() {
        regExcludedDocUrls(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     */
    public void setExcludedDocUrls_IsNotNull() {
        regExcludedDocUrls(CK_ISNN, DOBJ);
    }

    protected void regExcludedDocUrls(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueExcludedDocUrls(), "EXCLUDED_DOC_URLS");
    }

    abstract protected ConditionValue getCValueExcludedDocUrls();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameter The value of configParameter as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_Equal(final String configParameter) {
        doSetConfigParameter_Equal(fRES(configParameter));
    }

    protected void doSetConfigParameter_Equal(final String configParameter) {
        regConfigParameter(CK_EQ, configParameter);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameter The value of configParameter as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_NotEqual(final String configParameter) {
        doSetConfigParameter_NotEqual(fRES(configParameter));
    }

    protected void doSetConfigParameter_NotEqual(final String configParameter) {
        regConfigParameter(CK_NES, configParameter);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameter The value of configParameter as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_GreaterThan(final String configParameter) {
        regConfigParameter(CK_GT, fRES(configParameter));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameter The value of configParameter as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_LessThan(final String configParameter) {
        regConfigParameter(CK_LT, fRES(configParameter));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameter The value of configParameter as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_GreaterEqual(final String configParameter) {
        regConfigParameter(CK_GE, fRES(configParameter));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameter The value of configParameter as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_LessEqual(final String configParameter) {
        regConfigParameter(CK_LE, fRES(configParameter));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameterList The collection of configParameter as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_InScope(
            final Collection<String> configParameterList) {
        doSetConfigParameter_InScope(configParameterList);
    }

    public void doSetConfigParameter_InScope(
            final Collection<String> configParameterList) {
        regINS(CK_INS, cTL(configParameterList), getCValueConfigParameter(),
                "CONFIG_PARAMETER");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameterList The collection of configParameter as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_NotInScope(
            final Collection<String> configParameterList) {
        doSetConfigParameter_NotInScope(configParameterList);
    }

    public void doSetConfigParameter_NotInScope(
            final Collection<String> configParameterList) {
        regINS(CK_NINS, cTL(configParameterList), getCValueConfigParameter(),
                "CONFIG_PARAMETER");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameter The value of configParameter as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setConfigParameter_PrefixSearch(final String configParameter) {
        setConfigParameter_LikeSearch(configParameter, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)} <br />
     * <pre>e.g. setConfigParameter_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param configParameter The value of configParameter as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setConfigParameter_LikeSearch(final String configParameter,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(configParameter), getCValueConfigParameter(),
                "CONFIG_PARAMETER", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @param configParameter The value of configParameter as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setConfigParameter_NotLikeSearch(final String configParameter,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(configParameter), getCValueConfigParameter(),
                "CONFIG_PARAMETER", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     */
    public void setConfigParameter_IsNull() {
        regConfigParameter(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     */
    public void setConfigParameter_IsNullOrEmpty() {
        regConfigParameter(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     */
    public void setConfigParameter_IsNotNull() {
        regConfigParameter(CK_ISNN, DOBJ);
    }

    protected void regConfigParameter(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueConfigParameter(), "CONFIG_PARAMETER");
    }

    abstract protected ConditionValue getCValueConfigParameter();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param depth The value of depth as equal. (NullAllowed: if null, no condition)
     */
    public void setDepth_Equal(final Integer depth) {
        doSetDepth_Equal(depth);
    }

    protected void doSetDepth_Equal(final Integer depth) {
        regDepth(CK_EQ, depth);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param depth The value of depth as notEqual. (NullAllowed: if null, no condition)
     */
    public void setDepth_NotEqual(final Integer depth) {
        doSetDepth_NotEqual(depth);
    }

    protected void doSetDepth_NotEqual(final Integer depth) {
        regDepth(CK_NES, depth);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param depth The value of depth as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setDepth_GreaterThan(final Integer depth) {
        regDepth(CK_GT, depth);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param depth The value of depth as lessThan. (NullAllowed: if null, no condition)
     */
    public void setDepth_LessThan(final Integer depth) {
        regDepth(CK_LT, depth);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param depth The value of depth as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setDepth_GreaterEqual(final Integer depth) {
        regDepth(CK_GE, depth);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param depth The value of depth as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setDepth_LessEqual(final Integer depth) {
        regDepth(CK_LE, depth);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param minNumber The min number of depth. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of depth. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setDepth_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueDepth(), "DEPTH", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param depthList The collection of depth as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setDepth_InScope(final Collection<Integer> depthList) {
        doSetDepth_InScope(depthList);
    }

    protected void doSetDepth_InScope(final Collection<Integer> depthList) {
        regINS(CK_INS, cTL(depthList), getCValueDepth(), "DEPTH");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * DEPTH: {INTEGER(10)}
     * @param depthList The collection of depth as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setDepth_NotInScope(final Collection<Integer> depthList) {
        doSetDepth_NotInScope(depthList);
    }

    protected void doSetDepth_NotInScope(final Collection<Integer> depthList) {
        regINS(CK_NINS, cTL(depthList), getCValueDepth(), "DEPTH");
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     */
    public void setDepth_IsNull() {
        regDepth(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * DEPTH: {INTEGER(10)}
     */
    public void setDepth_IsNotNull() {
        regDepth(CK_ISNN, DOBJ);
    }

    protected void regDepth(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueDepth(), "DEPTH");
    }

    abstract protected ConditionValue getCValueDepth();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param maxAccessCount The value of maxAccessCount as equal. (NullAllowed: if null, no condition)
     */
    public void setMaxAccessCount_Equal(final Long maxAccessCount) {
        doSetMaxAccessCount_Equal(maxAccessCount);
    }

    protected void doSetMaxAccessCount_Equal(final Long maxAccessCount) {
        regMaxAccessCount(CK_EQ, maxAccessCount);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param maxAccessCount The value of maxAccessCount as notEqual. (NullAllowed: if null, no condition)
     */
    public void setMaxAccessCount_NotEqual(final Long maxAccessCount) {
        doSetMaxAccessCount_NotEqual(maxAccessCount);
    }

    protected void doSetMaxAccessCount_NotEqual(final Long maxAccessCount) {
        regMaxAccessCount(CK_NES, maxAccessCount);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param maxAccessCount The value of maxAccessCount as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setMaxAccessCount_GreaterThan(final Long maxAccessCount) {
        regMaxAccessCount(CK_GT, maxAccessCount);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param maxAccessCount The value of maxAccessCount as lessThan. (NullAllowed: if null, no condition)
     */
    public void setMaxAccessCount_LessThan(final Long maxAccessCount) {
        regMaxAccessCount(CK_LT, maxAccessCount);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param maxAccessCount The value of maxAccessCount as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setMaxAccessCount_GreaterEqual(final Long maxAccessCount) {
        regMaxAccessCount(CK_GE, maxAccessCount);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param maxAccessCount The value of maxAccessCount as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setMaxAccessCount_LessEqual(final Long maxAccessCount) {
        regMaxAccessCount(CK_LE, maxAccessCount);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param minNumber The min number of maxAccessCount. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of maxAccessCount. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setMaxAccessCount_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueMaxAccessCount(),
                "MAX_ACCESS_COUNT", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param maxAccessCountList The collection of maxAccessCount as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setMaxAccessCount_InScope(
            final Collection<Long> maxAccessCountList) {
        doSetMaxAccessCount_InScope(maxAccessCountList);
    }

    protected void doSetMaxAccessCount_InScope(
            final Collection<Long> maxAccessCountList) {
        regINS(CK_INS, cTL(maxAccessCountList), getCValueMaxAccessCount(),
                "MAX_ACCESS_COUNT");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @param maxAccessCountList The collection of maxAccessCount as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setMaxAccessCount_NotInScope(
            final Collection<Long> maxAccessCountList) {
        doSetMaxAccessCount_NotInScope(maxAccessCountList);
    }

    protected void doSetMaxAccessCount_NotInScope(
            final Collection<Long> maxAccessCountList) {
        regINS(CK_NINS, cTL(maxAccessCountList), getCValueMaxAccessCount(),
                "MAX_ACCESS_COUNT");
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     */
    public void setMaxAccessCount_IsNull() {
        regMaxAccessCount(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     */
    public void setMaxAccessCount_IsNotNull() {
        regMaxAccessCount(CK_ISNN, DOBJ);
    }

    protected void regMaxAccessCount(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueMaxAccessCount(), "MAX_ACCESS_COUNT");
    }

    abstract protected ConditionValue getCValueMaxAccessCount();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgent The value of userAgent as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_Equal(final String userAgent) {
        doSetUserAgent_Equal(fRES(userAgent));
    }

    protected void doSetUserAgent_Equal(final String userAgent) {
        regUserAgent(CK_EQ, userAgent);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgent The value of userAgent as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_NotEqual(final String userAgent) {
        doSetUserAgent_NotEqual(fRES(userAgent));
    }

    protected void doSetUserAgent_NotEqual(final String userAgent) {
        regUserAgent(CK_NES, userAgent);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgent The value of userAgent as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_GreaterThan(final String userAgent) {
        regUserAgent(CK_GT, fRES(userAgent));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgent The value of userAgent as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_LessThan(final String userAgent) {
        regUserAgent(CK_LT, fRES(userAgent));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgent The value of userAgent as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_GreaterEqual(final String userAgent) {
        regUserAgent(CK_GE, fRES(userAgent));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgent The value of userAgent as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_LessEqual(final String userAgent) {
        regUserAgent(CK_LE, fRES(userAgent));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgentList The collection of userAgent as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_InScope(final Collection<String> userAgentList) {
        doSetUserAgent_InScope(userAgentList);
    }

    public void doSetUserAgent_InScope(final Collection<String> userAgentList) {
        regINS(CK_INS, cTL(userAgentList), getCValueUserAgent(), "USER_AGENT");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgentList The collection of userAgent as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_NotInScope(final Collection<String> userAgentList) {
        doSetUserAgent_NotInScope(userAgentList);
    }

    public void doSetUserAgent_NotInScope(final Collection<String> userAgentList) {
        regINS(CK_NINS, cTL(userAgentList), getCValueUserAgent(), "USER_AGENT");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgent The value of userAgent as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_PrefixSearch(final String userAgent) {
        setUserAgent_LikeSearch(userAgent, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)} <br />
     * <pre>e.g. setUserAgent_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param userAgent The value of userAgent as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUserAgent_LikeSearch(final String userAgent,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(userAgent), getCValueUserAgent(), "USER_AGENT",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @param userAgent The value of userAgent as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUserAgent_NotLikeSearch(final String userAgent,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(userAgent), getCValueUserAgent(), "USER_AGENT",
                likeSearchOption);
    }

    protected void regUserAgent(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUserAgent(), "USER_AGENT");
    }

    abstract protected ConditionValue getCValueUserAgent();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param numOfThread The value of numOfThread as equal. (NullAllowed: if null, no condition)
     */
    public void setNumOfThread_Equal(final Integer numOfThread) {
        doSetNumOfThread_Equal(numOfThread);
    }

    protected void doSetNumOfThread_Equal(final Integer numOfThread) {
        regNumOfThread(CK_EQ, numOfThread);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param numOfThread The value of numOfThread as notEqual. (NullAllowed: if null, no condition)
     */
    public void setNumOfThread_NotEqual(final Integer numOfThread) {
        doSetNumOfThread_NotEqual(numOfThread);
    }

    protected void doSetNumOfThread_NotEqual(final Integer numOfThread) {
        regNumOfThread(CK_NES, numOfThread);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param numOfThread The value of numOfThread as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setNumOfThread_GreaterThan(final Integer numOfThread) {
        regNumOfThread(CK_GT, numOfThread);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param numOfThread The value of numOfThread as lessThan. (NullAllowed: if null, no condition)
     */
    public void setNumOfThread_LessThan(final Integer numOfThread) {
        regNumOfThread(CK_LT, numOfThread);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param numOfThread The value of numOfThread as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setNumOfThread_GreaterEqual(final Integer numOfThread) {
        regNumOfThread(CK_GE, numOfThread);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param numOfThread The value of numOfThread as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setNumOfThread_LessEqual(final Integer numOfThread) {
        regNumOfThread(CK_LE, numOfThread);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param minNumber The min number of numOfThread. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of numOfThread. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setNumOfThread_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueNumOfThread(), "NUM_OF_THREAD",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param numOfThreadList The collection of numOfThread as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setNumOfThread_InScope(final Collection<Integer> numOfThreadList) {
        doSetNumOfThread_InScope(numOfThreadList);
    }

    protected void doSetNumOfThread_InScope(
            final Collection<Integer> numOfThreadList) {
        regINS(CK_INS, cTL(numOfThreadList), getCValueNumOfThread(),
                "NUM_OF_THREAD");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @param numOfThreadList The collection of numOfThread as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setNumOfThread_NotInScope(
            final Collection<Integer> numOfThreadList) {
        doSetNumOfThread_NotInScope(numOfThreadList);
    }

    protected void doSetNumOfThread_NotInScope(
            final Collection<Integer> numOfThreadList) {
        regINS(CK_NINS, cTL(numOfThreadList), getCValueNumOfThread(),
                "NUM_OF_THREAD");
    }

    protected void regNumOfThread(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueNumOfThread(), "NUM_OF_THREAD");
    }

    abstract protected ConditionValue getCValueNumOfThread();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param intervalTime The value of intervalTime as equal. (NullAllowed: if null, no condition)
     */
    public void setIntervalTime_Equal(final Integer intervalTime) {
        doSetIntervalTime_Equal(intervalTime);
    }

    protected void doSetIntervalTime_Equal(final Integer intervalTime) {
        regIntervalTime(CK_EQ, intervalTime);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param intervalTime The value of intervalTime as notEqual. (NullAllowed: if null, no condition)
     */
    public void setIntervalTime_NotEqual(final Integer intervalTime) {
        doSetIntervalTime_NotEqual(intervalTime);
    }

    protected void doSetIntervalTime_NotEqual(final Integer intervalTime) {
        regIntervalTime(CK_NES, intervalTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param intervalTime The value of intervalTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setIntervalTime_GreaterThan(final Integer intervalTime) {
        regIntervalTime(CK_GT, intervalTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param intervalTime The value of intervalTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setIntervalTime_LessThan(final Integer intervalTime) {
        regIntervalTime(CK_LT, intervalTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param intervalTime The value of intervalTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setIntervalTime_GreaterEqual(final Integer intervalTime) {
        regIntervalTime(CK_GE, intervalTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param intervalTime The value of intervalTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setIntervalTime_LessEqual(final Integer intervalTime) {
        regIntervalTime(CK_LE, intervalTime);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param minNumber The min number of intervalTime. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of intervalTime. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setIntervalTime_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueIntervalTime(), "INTERVAL_TIME",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param intervalTimeList The collection of intervalTime as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIntervalTime_InScope(
            final Collection<Integer> intervalTimeList) {
        doSetIntervalTime_InScope(intervalTimeList);
    }

    protected void doSetIntervalTime_InScope(
            final Collection<Integer> intervalTimeList) {
        regINS(CK_INS, cTL(intervalTimeList), getCValueIntervalTime(),
                "INTERVAL_TIME");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @param intervalTimeList The collection of intervalTime as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIntervalTime_NotInScope(
            final Collection<Integer> intervalTimeList) {
        doSetIntervalTime_NotInScope(intervalTimeList);
    }

    protected void doSetIntervalTime_NotInScope(
            final Collection<Integer> intervalTimeList) {
        regINS(CK_NINS, cTL(intervalTimeList), getCValueIntervalTime(),
                "INTERVAL_TIME");
    }

    protected void regIntervalTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueIntervalTime(), "INTERVAL_TIME");
    }

    abstract protected ConditionValue getCValueIntervalTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as equal. (NullAllowed: if null, no condition)
     */
    public void setBoost_Equal(final java.math.BigDecimal boost) {
        doSetBoost_Equal(boost);
    }

    protected void doSetBoost_Equal(final java.math.BigDecimal boost) {
        regBoost(CK_EQ, boost);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as notEqual. (NullAllowed: if null, no condition)
     */
    public void setBoost_NotEqual(final java.math.BigDecimal boost) {
        doSetBoost_NotEqual(boost);
    }

    protected void doSetBoost_NotEqual(final java.math.BigDecimal boost) {
        regBoost(CK_NES, boost);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setBoost_GreaterThan(final java.math.BigDecimal boost) {
        regBoost(CK_GT, boost);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as lessThan. (NullAllowed: if null, no condition)
     */
    public void setBoost_LessThan(final java.math.BigDecimal boost) {
        regBoost(CK_LT, boost);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setBoost_GreaterEqual(final java.math.BigDecimal boost) {
        regBoost(CK_GE, boost);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setBoost_LessEqual(final java.math.BigDecimal boost) {
        regBoost(CK_LE, boost);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param minNumber The min number of boost. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of boost. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setBoost_RangeOf(final java.math.BigDecimal minNumber,
            final java.math.BigDecimal maxNumber,
            final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueBoost(), "BOOST", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boostList The collection of boost as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setBoost_InScope(
            final Collection<java.math.BigDecimal> boostList) {
        doSetBoost_InScope(boostList);
    }

    protected void doSetBoost_InScope(
            final Collection<java.math.BigDecimal> boostList) {
        regINS(CK_INS, cTL(boostList), getCValueBoost(), "BOOST");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boostList The collection of boost as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setBoost_NotInScope(
            final Collection<java.math.BigDecimal> boostList) {
        doSetBoost_NotInScope(boostList);
    }

    protected void doSetBoost_NotInScope(
            final Collection<java.math.BigDecimal> boostList) {
        regINS(CK_NINS, cTL(boostList), getCValueBoost(), "BOOST");
    }

    protected void regBoost(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueBoost(), "BOOST");
    }

    abstract protected ConditionValue getCValueBoost();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_Equal(final String available) {
        doSetAvailable_Equal(fRES(available));
    }

    protected void doSetAvailable_Equal(final String available) {
        regAvailable(CK_EQ, available);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_NotEqual(final String available) {
        doSetAvailable_NotEqual(fRES(available));
    }

    protected void doSetAvailable_NotEqual(final String available) {
        regAvailable(CK_NES, available);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_GreaterThan(final String available) {
        regAvailable(CK_GT, fRES(available));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_LessThan(final String available) {
        regAvailable(CK_LT, fRES(available));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_GreaterEqual(final String available) {
        regAvailable(CK_GE, fRES(available));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_LessEqual(final String available) {
        regAvailable(CK_LE, fRES(available));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param availableList The collection of available as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_InScope(final Collection<String> availableList) {
        doSetAvailable_InScope(availableList);
    }

    public void doSetAvailable_InScope(final Collection<String> availableList) {
        regINS(CK_INS, cTL(availableList), getCValueAvailable(), "AVAILABLE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param availableList The collection of available as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_NotInScope(final Collection<String> availableList) {
        doSetAvailable_NotInScope(availableList);
    }

    public void doSetAvailable_NotInScope(final Collection<String> availableList) {
        regINS(CK_NINS, cTL(availableList), getCValueAvailable(), "AVAILABLE");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_PrefixSearch(final String available) {
        setAvailable_LikeSearch(available, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)} <br />
     * <pre>e.g. setAvailable_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param available The value of available as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setAvailable_LikeSearch(final String available,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(available), getCValueAvailable(), "AVAILABLE",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setAvailable_NotLikeSearch(final String available,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(available), getCValueAvailable(), "AVAILABLE",
                likeSearchOption);
    }

    protected void regAvailable(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueAvailable(), "AVAILABLE");
    }

    abstract protected ConditionValue getCValueAvailable();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as equal. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_Equal(final Integer sortOrder) {
        doSetSortOrder_Equal(sortOrder);
    }

    protected void doSetSortOrder_Equal(final Integer sortOrder) {
        regSortOrder(CK_EQ, sortOrder);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as notEqual. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_NotEqual(final Integer sortOrder) {
        doSetSortOrder_NotEqual(sortOrder);
    }

    protected void doSetSortOrder_NotEqual(final Integer sortOrder) {
        regSortOrder(CK_NES, sortOrder);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_GreaterThan(final Integer sortOrder) {
        regSortOrder(CK_GT, sortOrder);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as lessThan. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_LessThan(final Integer sortOrder) {
        regSortOrder(CK_LT, sortOrder);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_GreaterEqual(final Integer sortOrder) {
        regSortOrder(CK_GE, sortOrder);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_LessEqual(final Integer sortOrder) {
        regSortOrder(CK_LE, sortOrder);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param minNumber The min number of sortOrder. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of sortOrder. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setSortOrder_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueSortOrder(), "SORT_ORDER",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrderList The collection of sortOrder as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSortOrder_InScope(final Collection<Integer> sortOrderList) {
        doSetSortOrder_InScope(sortOrderList);
    }

    protected void doSetSortOrder_InScope(
            final Collection<Integer> sortOrderList) {
        regINS(CK_INS, cTL(sortOrderList), getCValueSortOrder(), "SORT_ORDER");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrderList The collection of sortOrder as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSortOrder_NotInScope(final Collection<Integer> sortOrderList) {
        doSetSortOrder_NotInScope(sortOrderList);
    }

    protected void doSetSortOrder_NotInScope(
            final Collection<Integer> sortOrderList) {
        regINS(CK_NINS, cTL(sortOrderList), getCValueSortOrder(), "SORT_ORDER");
    }

    protected void regSortOrder(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueSortOrder(), "SORT_ORDER");
    }

    abstract protected ConditionValue getCValueSortOrder();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_Equal(final String createdBy) {
        doSetCreatedBy_Equal(fRES(createdBy));
    }

    protected void doSetCreatedBy_Equal(final String createdBy) {
        regCreatedBy(CK_EQ, createdBy);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_NotEqual(final String createdBy) {
        doSetCreatedBy_NotEqual(fRES(createdBy));
    }

    protected void doSetCreatedBy_NotEqual(final String createdBy) {
        regCreatedBy(CK_NES, createdBy);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_GreaterThan(final String createdBy) {
        regCreatedBy(CK_GT, fRES(createdBy));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_LessThan(final String createdBy) {
        regCreatedBy(CK_LT, fRES(createdBy));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_GreaterEqual(final String createdBy) {
        regCreatedBy(CK_GE, fRES(createdBy));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_LessEqual(final String createdBy) {
        regCreatedBy(CK_LE, fRES(createdBy));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdByList The collection of createdBy as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_InScope(final Collection<String> createdByList) {
        doSetCreatedBy_InScope(createdByList);
    }

    public void doSetCreatedBy_InScope(final Collection<String> createdByList) {
        regINS(CK_INS, cTL(createdByList), getCValueCreatedBy(), "CREATED_BY");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdByList The collection of createdBy as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_NotInScope(final Collection<String> createdByList) {
        doSetCreatedBy_NotInScope(createdByList);
    }

    public void doSetCreatedBy_NotInScope(final Collection<String> createdByList) {
        regINS(CK_NINS, cTL(createdByList), getCValueCreatedBy(), "CREATED_BY");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_PrefixSearch(final String createdBy) {
        setCreatedBy_LikeSearch(createdBy, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)} <br />
     * <pre>e.g. setCreatedBy_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param createdBy The value of createdBy as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setCreatedBy_LikeSearch(final String createdBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(createdBy), getCValueCreatedBy(), "CREATED_BY",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setCreatedBy_NotLikeSearch(final String createdBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(createdBy), getCValueCreatedBy(), "CREATED_BY",
                likeSearchOption);
    }

    protected void regCreatedBy(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueCreatedBy(), "CREATED_BY");
    }

    abstract protected ConditionValue getCValueCreatedBy();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as equal. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_Equal(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_EQ, createdTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_GreaterThan(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_GT, createdTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_LessThan(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_LT, createdTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_GreaterEqual(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_GE, createdTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_LessEqual(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_LE, createdTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * <pre>e.g. setCreatedTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createdTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createdTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setCreatedTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueCreatedTime(),
                "CREATED_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of createdTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of createdTime. (NullAllowed: if null, no to-condition)
     */
    public void setCreatedTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setCreatedTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    protected void regCreatedTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueCreatedTime(), "CREATED_TIME");
    }

    abstract protected ConditionValue getCValueCreatedTime();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_Equal(final String updatedBy) {
        doSetUpdatedBy_Equal(fRES(updatedBy));
    }

    protected void doSetUpdatedBy_Equal(final String updatedBy) {
        regUpdatedBy(CK_EQ, updatedBy);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_NotEqual(final String updatedBy) {
        doSetUpdatedBy_NotEqual(fRES(updatedBy));
    }

    protected void doSetUpdatedBy_NotEqual(final String updatedBy) {
        regUpdatedBy(CK_NES, updatedBy);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_GreaterThan(final String updatedBy) {
        regUpdatedBy(CK_GT, fRES(updatedBy));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_LessThan(final String updatedBy) {
        regUpdatedBy(CK_LT, fRES(updatedBy));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_GreaterEqual(final String updatedBy) {
        regUpdatedBy(CK_GE, fRES(updatedBy));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_LessEqual(final String updatedBy) {
        regUpdatedBy(CK_LE, fRES(updatedBy));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedByList The collection of updatedBy as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_InScope(final Collection<String> updatedByList) {
        doSetUpdatedBy_InScope(updatedByList);
    }

    public void doSetUpdatedBy_InScope(final Collection<String> updatedByList) {
        regINS(CK_INS, cTL(updatedByList), getCValueUpdatedBy(), "UPDATED_BY");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedByList The collection of updatedBy as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_NotInScope(final Collection<String> updatedByList) {
        doSetUpdatedBy_NotInScope(updatedByList);
    }

    public void doSetUpdatedBy_NotInScope(final Collection<String> updatedByList) {
        regINS(CK_NINS, cTL(updatedByList), getCValueUpdatedBy(), "UPDATED_BY");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_PrefixSearch(final String updatedBy) {
        setUpdatedBy_LikeSearch(updatedBy, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)} <br />
     * <pre>e.g. setUpdatedBy_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param updatedBy The value of updatedBy as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUpdatedBy_LikeSearch(final String updatedBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(updatedBy), getCValueUpdatedBy(), "UPDATED_BY",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUpdatedBy_NotLikeSearch(final String updatedBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(updatedBy), getCValueUpdatedBy(), "UPDATED_BY",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     */
    public void setUpdatedBy_IsNull() {
        regUpdatedBy(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     */
    public void setUpdatedBy_IsNullOrEmpty() {
        regUpdatedBy(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     */
    public void setUpdatedBy_IsNotNull() {
        regUpdatedBy(CK_ISNN, DOBJ);
    }

    protected void regUpdatedBy(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUpdatedBy(), "UPDATED_BY");
    }

    abstract protected ConditionValue getCValueUpdatedBy();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as equal. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_Equal(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_EQ, updatedTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_GreaterThan(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_GT, updatedTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_LessThan(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_LT, updatedTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_GreaterEqual(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_GE, updatedTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_LessEqual(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_LE, updatedTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * <pre>e.g. setUpdatedTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of updatedTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of updatedTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setUpdatedTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueUpdatedTime(),
                "UPDATED_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of updatedTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of updatedTime. (NullAllowed: if null, no to-condition)
     */
    public void setUpdatedTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setUpdatedTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     */
    public void setUpdatedTime_IsNull() {
        regUpdatedTime(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     */
    public void setUpdatedTime_IsNotNull() {
        regUpdatedTime(CK_ISNN, DOBJ);
    }

    protected void regUpdatedTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUpdatedTime(), "UPDATED_TIME");
    }

    abstract protected ConditionValue getCValueUpdatedTime();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_Equal(final String deletedBy) {
        doSetDeletedBy_Equal(fRES(deletedBy));
    }

    protected void doSetDeletedBy_Equal(final String deletedBy) {
        regDeletedBy(CK_EQ, deletedBy);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_NotEqual(final String deletedBy) {
        doSetDeletedBy_NotEqual(fRES(deletedBy));
    }

    protected void doSetDeletedBy_NotEqual(final String deletedBy) {
        regDeletedBy(CK_NES, deletedBy);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_GreaterThan(final String deletedBy) {
        regDeletedBy(CK_GT, fRES(deletedBy));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_LessThan(final String deletedBy) {
        regDeletedBy(CK_LT, fRES(deletedBy));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_GreaterEqual(final String deletedBy) {
        regDeletedBy(CK_GE, fRES(deletedBy));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_LessEqual(final String deletedBy) {
        regDeletedBy(CK_LE, fRES(deletedBy));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedByList The collection of deletedBy as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_InScope(final Collection<String> deletedByList) {
        doSetDeletedBy_InScope(deletedByList);
    }

    public void doSetDeletedBy_InScope(final Collection<String> deletedByList) {
        regINS(CK_INS, cTL(deletedByList), getCValueDeletedBy(), "DELETED_BY");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedByList The collection of deletedBy as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_NotInScope(final Collection<String> deletedByList) {
        doSetDeletedBy_NotInScope(deletedByList);
    }

    public void doSetDeletedBy_NotInScope(final Collection<String> deletedByList) {
        regINS(CK_NINS, cTL(deletedByList), getCValueDeletedBy(), "DELETED_BY");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_PrefixSearch(final String deletedBy) {
        setDeletedBy_LikeSearch(deletedBy, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)} <br />
     * <pre>e.g. setDeletedBy_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param deletedBy The value of deletedBy as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setDeletedBy_LikeSearch(final String deletedBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(deletedBy), getCValueDeletedBy(), "DELETED_BY",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setDeletedBy_NotLikeSearch(final String deletedBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(deletedBy), getCValueDeletedBy(), "DELETED_BY",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     */
    public void setDeletedBy_IsNull() {
        regDeletedBy(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     */
    public void setDeletedBy_IsNullOrEmpty() {
        regDeletedBy(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     */
    public void setDeletedBy_IsNotNull() {
        regDeletedBy(CK_ISNN, DOBJ);
    }

    protected void regDeletedBy(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueDeletedBy(), "DELETED_BY");
    }

    abstract protected ConditionValue getCValueDeletedBy();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as equal. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_Equal(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_EQ, deletedTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_GreaterThan(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_GT, deletedTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_LessThan(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_LT, deletedTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_GreaterEqual(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_GE, deletedTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_LessEqual(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_LE, deletedTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * <pre>e.g. setDeletedTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of deletedTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of deletedTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setDeletedTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueDeletedTime(),
                "DELETED_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of deletedTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of deletedTime. (NullAllowed: if null, no to-condition)
     */
    public void setDeletedTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setDeletedTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     */
    public void setDeletedTime_IsNull() {
        regDeletedTime(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     */
    public void setDeletedTime_IsNotNull() {
        regDeletedTime(CK_ISNN, DOBJ);
    }

    protected void regDeletedTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueDeletedTime(), "DELETED_TIME");
    }

    abstract protected ConditionValue getCValueDeletedTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as equal. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_Equal(final Integer versionNo) {
        doSetVersionNo_Equal(versionNo);
    }

    protected void doSetVersionNo_Equal(final Integer versionNo) {
        regVersionNo(CK_EQ, versionNo);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as notEqual. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_NotEqual(final Integer versionNo) {
        doSetVersionNo_NotEqual(versionNo);
    }

    protected void doSetVersionNo_NotEqual(final Integer versionNo) {
        regVersionNo(CK_NES, versionNo);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_GreaterThan(final Integer versionNo) {
        regVersionNo(CK_GT, versionNo);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as lessThan. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_LessThan(final Integer versionNo) {
        regVersionNo(CK_LT, versionNo);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_GreaterEqual(final Integer versionNo) {
        regVersionNo(CK_GE, versionNo);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_LessEqual(final Integer versionNo) {
        regVersionNo(CK_LE, versionNo);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param minNumber The min number of versionNo. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of versionNo. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setVersionNo_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueVersionNo(), "VERSION_NO",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNoList The collection of versionNo as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setVersionNo_InScope(final Collection<Integer> versionNoList) {
        doSetVersionNo_InScope(versionNoList);
    }

    protected void doSetVersionNo_InScope(
            final Collection<Integer> versionNoList) {
        regINS(CK_INS, cTL(versionNoList), getCValueVersionNo(), "VERSION_NO");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNoList The collection of versionNo as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setVersionNo_NotInScope(final Collection<Integer> versionNoList) {
        doSetVersionNo_NotInScope(versionNoList);
    }

    protected void doSetVersionNo_NotInScope(
            final Collection<Integer> versionNoList) {
        regINS(CK_NINS, cTL(versionNoList), getCValueVersionNo(), "VERSION_NO");
    }

    protected void regVersionNo(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueVersionNo(), "VERSION_NO");
    }

    abstract protected ConditionValue getCValueVersionNo();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebCrawlingConfigCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebCrawlingConfigCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebCrawlingConfigCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebCrawlingConfigCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebCrawlingConfigCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void query(WebCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebCrawlingConfigCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<WebCrawlingConfigCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<WebCrawlingConfigCB>(
                new HpSSQSetupper<WebCrawlingConfigCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<WebCrawlingConfigCB> subQuery,
                            final HpSSQOption<WebCrawlingConfigCB> option) {
                        xscalarCondition(function, subQuery, operand, option);
                    }
                });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<WebCrawlingConfigCB> subQuery, final String operand,
            final HpSSQOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("subQuery<WebCrawlingConfigCB>", subQuery);
        final WebCrawlingConfigCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(WebCrawlingConfigCQ subQuery);

    protected WebCrawlingConfigCB xcreateScalarConditionCB() {
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected WebCrawlingConfigCB xcreateScalarConditionPartitionByCB() {
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<WebCrawlingConfigCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebCrawlingConfigCB>", subQuery);
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(WebCrawlingConfigCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<WebCrawlingConfigCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<WebCrawlingConfigCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<WebCrawlingConfigCB>(
                new HpQDRSetupper<WebCrawlingConfigCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<WebCrawlingConfigCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveMyselfDerived(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<WebCrawlingConfigCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebCrawlingConfigCB>", subQuery);
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(WebCrawlingConfigCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<WebCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<WebCrawlingConfigCB>", subQuery);
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(WebCrawlingConfigCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<WebCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<WebCrawlingConfigCB>", subQuery);
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(WebCrawlingConfigCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return WebCrawlingConfigCB.class.getName();
    }

    protected String xabCQ() {
        return WebCrawlingConfigCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
