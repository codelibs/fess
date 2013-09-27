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
import jp.sf.fess.db.cbean.FavoriteLogCB;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.cbean.cq.FavoriteLogCQ;
import jp.sf.fess.db.cbean.cq.SearchLogCQ;
import jp.sf.fess.db.cbean.cq.UserInfoCQ;

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
 * The abstract condition-query of USER_INFO.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsUserInfoCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsUserInfoCQ(final ConditionQuery childQuery,
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
        return "USER_INFO";
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
     * {exists (select USER_ID from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsFavoriteLogList</span>(new SubQuery&lt;FavoriteLogCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of FavoriteLogList for 'exists'. (NotNull)
     */
    public void existsFavoriteLogList(final SubQuery<FavoriteLogCB> subQuery) {
        assertObjectNotNull("subQuery<FavoriteLogCB>", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_FavoriteLogList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "favoriteLogList");
    }

    public abstract String keepId_ExistsReferrer_FavoriteLogList(
            FavoriteLogCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select USER_ID from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsSearchLogList</span>(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of SearchLogList for 'exists'. (NotNull)
     */
    public void existsSearchLogList(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_SearchLogList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "searchLogList");
    }

    public abstract String keepId_ExistsReferrer_SearchLogList(
            SearchLogCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select USER_ID from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsFavoriteLogList</span>(new SubQuery&lt;FavoriteLogCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_FavoriteLogList for 'not exists'. (NotNull)
     */
    public void notExistsFavoriteLogList(final SubQuery<FavoriteLogCB> subQuery) {
        assertObjectNotNull("subQuery<FavoriteLogCB>", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_FavoriteLogList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "favoriteLogList");
    }

    public abstract String keepId_NotExistsReferrer_FavoriteLogList(
            FavoriteLogCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select USER_ID from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsSearchLogList</span>(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_SearchLogList for 'not exists'. (NotNull)
     */
    public void notExistsSearchLogList(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_SearchLogList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "searchLogList");
    }

    public abstract String keepId_NotExistsReferrer_SearchLogList(
            SearchLogCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select USER_ID from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * @param subQuery The sub-query of FavoriteLogList for 'in-scope'. (NotNull)
     */
    public void inScopeFavoriteLogList(final SubQuery<FavoriteLogCB> subQuery) {
        assertObjectNotNull("subQuery<FavoriteLogCB>", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_FavoriteLogList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "favoriteLogList");
    }

    public abstract String keepId_InScopeRelation_FavoriteLogList(
            FavoriteLogCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select USER_ID from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * @param subQuery The sub-query of SearchLogList for 'in-scope'. (NotNull)
     */
    public void inScopeSearchLogList(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_SearchLogList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "searchLogList");
    }

    public abstract String keepId_InScopeRelation_SearchLogList(
            SearchLogCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select USER_ID from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * @param subQuery The sub-query of FavoriteLogList for 'not in-scope'. (NotNull)
     */
    public void notInScopeFavoriteLogList(final SubQuery<FavoriteLogCB> subQuery) {
        assertObjectNotNull("subQuery<FavoriteLogCB>", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_FavoriteLogList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "favoriteLogList");
    }

    public abstract String keepId_NotInScopeRelation_FavoriteLogList(
            FavoriteLogCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select USER_ID from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * @param subQuery The sub-query of SearchLogList for 'not in-scope'. (NotNull)
     */
    public void notInScopeSearchLogList(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_SearchLogList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "searchLogList");
    }

    public abstract String keepId_NotInScopeRelation_SearchLogList(
            SearchLogCQ subQuery);

    public void xsderiveFavoriteLogList(final String function,
            final SubQuery<FavoriteLogCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FavoriteLogCB>", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_FavoriteLogList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "favoriteLogList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_FavoriteLogList(
            FavoriteLogCQ subQuery);

    public void xsderiveSearchLogList(final String function,
            final SubQuery<SearchLogCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_SearchLogList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "searchLogList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_SearchLogList(
            SearchLogCQ subQuery);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedFavoriteLogList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;FavoriteLogCB&gt;() {
     *     public void query(FavoriteLogCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<FavoriteLogCB> derivedFavoriteLogList() {
        return xcreateQDRFunctionFavoriteLogList();
    }

    protected HpQDRFunction<FavoriteLogCB> xcreateQDRFunctionFavoriteLogList() {
        return new HpQDRFunction<FavoriteLogCB>(
                new HpQDRSetupper<FavoriteLogCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<FavoriteLogCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveFavoriteLogList(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveFavoriteLogList(final String function,
            final SubQuery<FavoriteLogCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FavoriteLogCB>", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_FavoriteLogList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_FavoriteLogListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "favoriteLogList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_FavoriteLogList(
            FavoriteLogCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_FavoriteLogListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedSearchLogList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<SearchLogCB> derivedSearchLogList() {
        return xcreateQDRFunctionSearchLogList();
    }

    protected HpQDRFunction<SearchLogCB> xcreateQDRFunctionSearchLogList() {
        return new HpQDRFunction<SearchLogCB>(new HpQDRSetupper<SearchLogCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<SearchLogCB> subQuery, final String operand,
                    final Object value, final DerivedReferrerOption option) {
                xqderiveSearchLogList(function, subQuery, operand, value,
                        option);
            }
        });
    }

    public void xqderiveSearchLogList(final String function,
            final SubQuery<SearchLogCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_SearchLogList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_SearchLogListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID", "USER_ID",
                subQueryPropertyName, "searchLogList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_SearchLogList(
            SearchLogCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_SearchLogListParameter(
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
     * CODE: {NotNull, VARCHAR(1000)}
     * @param code The value of code as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_Equal(final String code) {
        doSetCode_Equal(fRES(code));
    }

    protected void doSetCode_Equal(final String code) {
        regCode(CK_EQ, code);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param code The value of code as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_NotEqual(final String code) {
        doSetCode_NotEqual(fRES(code));
    }

    protected void doSetCode_NotEqual(final String code) {
        regCode(CK_NES, code);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param code The value of code as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_GreaterThan(final String code) {
        regCode(CK_GT, fRES(code));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param code The value of code as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_LessThan(final String code) {
        regCode(CK_LT, fRES(code));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param code The value of code as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_GreaterEqual(final String code) {
        regCode(CK_GE, fRES(code));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param code The value of code as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_LessEqual(final String code) {
        regCode(CK_LE, fRES(code));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param codeList The collection of code as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_InScope(final Collection<String> codeList) {
        doSetCode_InScope(codeList);
    }

    public void doSetCode_InScope(final Collection<String> codeList) {
        regINS(CK_INS, cTL(codeList), getCValueCode(), "CODE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param codeList The collection of code as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_NotInScope(final Collection<String> codeList) {
        doSetCode_NotInScope(codeList);
    }

    public void doSetCode_NotInScope(final Collection<String> codeList) {
        regINS(CK_NINS, cTL(codeList), getCValueCode(), "CODE");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param code The value of code as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setCode_PrefixSearch(final String code) {
        setCode_LikeSearch(code, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)} <br />
     * <pre>e.g. setCode_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param code The value of code as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setCode_LikeSearch(final String code,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(code), getCValueCode(), "CODE", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @param code The value of code as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setCode_NotLikeSearch(final String code,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(code), getCValueCode(), "CODE", likeSearchOption);
    }

    protected void regCode(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueCode(), "CODE");
    }

    abstract protected ConditionValue getCValueCode();

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
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as equal. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_Equal(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_EQ, updatedTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_GreaterThan(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_GT, updatedTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_LessThan(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_LT, updatedTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_GreaterEqual(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_GE, updatedTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_LessEqual(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_LE, updatedTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
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
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
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

    protected void regUpdatedTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUpdatedTime(), "UPDATED_TIME");
    }

    abstract protected ConditionValue getCValueUpdatedTime();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<UserInfoCB> xcreateSSQFunction(final String operand) {
        return new HpSSQFunction<UserInfoCB>(new HpSSQSetupper<UserInfoCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<UserInfoCB> subQuery,
                    final HpSSQOption<UserInfoCB> option) {
                xscalarCondition(function, subQuery, operand, option);
            }
        });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<UserInfoCB> subQuery, final String operand,
            final HpSSQOption<UserInfoCB> option) {
        assertObjectNotNull("subQuery<UserInfoCB>", subQuery);
        final UserInfoCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(UserInfoCQ subQuery);

    protected UserInfoCB xcreateScalarConditionCB() {
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected UserInfoCB xcreateScalarConditionPartitionByCB() {
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<UserInfoCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<UserInfoCB>", subQuery);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(UserInfoCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<UserInfoCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<UserInfoCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<UserInfoCB>(new HpQDRSetupper<UserInfoCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<UserInfoCB> subQuery, final String operand,
                    final Object value, final DerivedReferrerOption option) {
                xqderiveMyselfDerived(function, subQuery, operand, value,
                        option);
            }
        });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<UserInfoCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<UserInfoCB>", subQuery);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(UserInfoCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<UserInfoCB> subQuery) {
        assertObjectNotNull("subQuery<UserInfoCB>", subQuery);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(UserInfoCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<UserInfoCB> subQuery) {
        assertObjectNotNull("subQuery<UserInfoCB>", subQuery);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(UserInfoCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return UserInfoCB.class.getName();
    }

    protected String xabCQ() {
        return UserInfoCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
