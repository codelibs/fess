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

import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.cbean.ClickLogCB;
import jp.sf.fess.db.cbean.SearchFieldLogCB;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.cbean.cq.ClickLogCQ;
import jp.sf.fess.db.cbean.cq.SearchFieldLogCQ;
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
import org.seasar.dbflute.cbean.coption.DateFromToOption;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.coption.FromToOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.cbean.coption.RangeOfOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

/**
 * The abstract condition-query of SEARCH_LOG.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsSearchLogCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsSearchLogCQ(final ConditionQuery childQuery,
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
        return "SEARCH_LOG";
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
     * {exists (select SEARCH_ID from CLICK_LOG where ...)} <br />
     * CLICK_LOG by SEARCH_ID, named 'clickLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsClickLogList</span>(new SubQuery&lt;ClickLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of ClickLogList for 'exists'. (NotNull)
     */
    public void existsClickLogList(final SubQuery<ClickLogCB> subQuery) {
        assertObjectNotNull("subQuery<ClickLogCB>", subQuery);
        final ClickLogCB cb = new ClickLogCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_ClickLogList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "clickLogList");
    }

    public abstract String keepId_ExistsReferrer_ClickLogList(
            ClickLogCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select SEARCH_ID from SEARCH_FIELD_LOG where ...)} <br />
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsSearchFieldLogList</span>(new SubQuery&lt;SearchFieldLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of SearchFieldLogList for 'exists'. (NotNull)
     */
    public void existsSearchFieldLogList(
            final SubQuery<SearchFieldLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchFieldLogCB>", subQuery);
        final SearchFieldLogCB cb = new SearchFieldLogCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_SearchFieldLogList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "searchFieldLogList");
    }

    public abstract String keepId_ExistsReferrer_SearchFieldLogList(
            SearchFieldLogCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select SEARCH_ID from CLICK_LOG where ...)} <br />
     * CLICK_LOG by SEARCH_ID, named 'clickLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsClickLogList</span>(new SubQuery&lt;ClickLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_ClickLogList for 'not exists'. (NotNull)
     */
    public void notExistsClickLogList(final SubQuery<ClickLogCB> subQuery) {
        assertObjectNotNull("subQuery<ClickLogCB>", subQuery);
        final ClickLogCB cb = new ClickLogCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_ClickLogList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "clickLogList");
    }

    public abstract String keepId_NotExistsReferrer_ClickLogList(
            ClickLogCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select SEARCH_ID from SEARCH_FIELD_LOG where ...)} <br />
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsSearchFieldLogList</span>(new SubQuery&lt;SearchFieldLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_SearchFieldLogList for 'not exists'. (NotNull)
     */
    public void notExistsSearchFieldLogList(
            final SubQuery<SearchFieldLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchFieldLogCB>", subQuery);
        final SearchFieldLogCB cb = new SearchFieldLogCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_SearchFieldLogList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "searchFieldLogList");
    }

    public abstract String keepId_NotExistsReferrer_SearchFieldLogList(
            SearchFieldLogCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select SEARCH_ID from CLICK_LOG where ...)} <br />
     * CLICK_LOG by SEARCH_ID, named 'clickLogAsOne'.
     * @param subQuery The sub-query of ClickLogList for 'in-scope'. (NotNull)
     */
    public void inScopeClickLogList(final SubQuery<ClickLogCB> subQuery) {
        assertObjectNotNull("subQuery<ClickLogCB>", subQuery);
        final ClickLogCB cb = new ClickLogCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_ClickLogList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "clickLogList");
    }

    public abstract String keepId_InScopeRelation_ClickLogList(
            ClickLogCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select SEARCH_ID from SEARCH_FIELD_LOG where ...)} <br />
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogAsOne'.
     * @param subQuery The sub-query of SearchFieldLogList for 'in-scope'. (NotNull)
     */
    public void inScopeSearchFieldLogList(
            final SubQuery<SearchFieldLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchFieldLogCB>", subQuery);
        final SearchFieldLogCB cb = new SearchFieldLogCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_SearchFieldLogList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "searchFieldLogList");
    }

    public abstract String keepId_InScopeRelation_SearchFieldLogList(
            SearchFieldLogCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select SEARCH_ID from CLICK_LOG where ...)} <br />
     * CLICK_LOG by SEARCH_ID, named 'clickLogAsOne'.
     * @param subQuery The sub-query of ClickLogList for 'not in-scope'. (NotNull)
     */
    public void notInScopeClickLogList(final SubQuery<ClickLogCB> subQuery) {
        assertObjectNotNull("subQuery<ClickLogCB>", subQuery);
        final ClickLogCB cb = new ClickLogCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_ClickLogList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "clickLogList");
    }

    public abstract String keepId_NotInScopeRelation_ClickLogList(
            ClickLogCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select SEARCH_ID from SEARCH_FIELD_LOG where ...)} <br />
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogAsOne'.
     * @param subQuery The sub-query of SearchFieldLogList for 'not in-scope'. (NotNull)
     */
    public void notInScopeSearchFieldLogList(
            final SubQuery<SearchFieldLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchFieldLogCB>", subQuery);
        final SearchFieldLogCB cb = new SearchFieldLogCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_SearchFieldLogList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "searchFieldLogList");
    }

    public abstract String keepId_NotInScopeRelation_SearchFieldLogList(
            SearchFieldLogCQ subQuery);

    public void xsderiveClickLogList(final String function,
            final SubQuery<ClickLogCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<ClickLogCB>", subQuery);
        final ClickLogCB cb = new ClickLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_ClickLogList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "clickLogList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_ClickLogList(
            ClickLogCQ subQuery);

    public void xsderiveSearchFieldLogList(final String function,
            final SubQuery<SearchFieldLogCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<SearchFieldLogCB>", subQuery);
        final SearchFieldLogCB cb = new SearchFieldLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_SearchFieldLogList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "searchFieldLogList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_SearchFieldLogList(
            SearchFieldLogCQ subQuery);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from CLICK_LOG where ...)} <br />
     * CLICK_LOG by SEARCH_ID, named 'clickLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedClickLogList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;ClickLogCB&gt;() {
     *     public void query(ClickLogCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<ClickLogCB> derivedClickLogList() {
        return xcreateQDRFunctionClickLogList();
    }

    protected HpQDRFunction<ClickLogCB> xcreateQDRFunctionClickLogList() {
        return new HpQDRFunction<ClickLogCB>(new HpQDRSetupper<ClickLogCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<ClickLogCB> subQuery, final String operand,
                    final Object value, final DerivedReferrerOption option) {
                xqderiveClickLogList(function, subQuery, operand, value, option);
            }
        });
    }

    public void xqderiveClickLogList(final String function,
            final SubQuery<ClickLogCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<ClickLogCB>", subQuery);
        final ClickLogCB cb = new ClickLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_ClickLogList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_ClickLogListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "clickLogList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_ClickLogList(
            ClickLogCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_ClickLogListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from SEARCH_FIELD_LOG where ...)} <br />
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedSearchFieldLogList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;SearchFieldLogCB&gt;() {
     *     public void query(SearchFieldLogCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<SearchFieldLogCB> derivedSearchFieldLogList() {
        return xcreateQDRFunctionSearchFieldLogList();
    }

    protected HpQDRFunction<SearchFieldLogCB> xcreateQDRFunctionSearchFieldLogList() {
        return new HpQDRFunction<SearchFieldLogCB>(
                new HpQDRSetupper<SearchFieldLogCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<SearchFieldLogCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveSearchFieldLogList(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveSearchFieldLogList(final String function,
            final SubQuery<SearchFieldLogCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<SearchFieldLogCB>", subQuery);
        final SearchFieldLogCB cb = new SearchFieldLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_SearchFieldLogList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_SearchFieldLogListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID", "SEARCH_ID",
                subQueryPropertyName, "searchFieldLogList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_SearchFieldLogList(
            SearchFieldLogCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_SearchFieldLogListParameter(
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
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWord The value of searchWord as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_Equal(final String searchWord) {
        doSetSearchWord_Equal(fRES(searchWord));
    }

    protected void doSetSearchWord_Equal(final String searchWord) {
        regSearchWord(CK_EQ, searchWord);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWord The value of searchWord as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_NotEqual(final String searchWord) {
        doSetSearchWord_NotEqual(fRES(searchWord));
    }

    protected void doSetSearchWord_NotEqual(final String searchWord) {
        regSearchWord(CK_NES, searchWord);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWord The value of searchWord as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_GreaterThan(final String searchWord) {
        regSearchWord(CK_GT, fRES(searchWord));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWord The value of searchWord as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_LessThan(final String searchWord) {
        regSearchWord(CK_LT, fRES(searchWord));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWord The value of searchWord as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_GreaterEqual(final String searchWord) {
        regSearchWord(CK_GE, fRES(searchWord));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWord The value of searchWord as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_LessEqual(final String searchWord) {
        regSearchWord(CK_LE, fRES(searchWord));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWordList The collection of searchWord as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_InScope(final Collection<String> searchWordList) {
        doSetSearchWord_InScope(searchWordList);
    }

    public void doSetSearchWord_InScope(final Collection<String> searchWordList) {
        regINS(CK_INS, cTL(searchWordList), getCValueSearchWord(),
                "SEARCH_WORD");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWordList The collection of searchWord as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_NotInScope(final Collection<String> searchWordList) {
        doSetSearchWord_NotInScope(searchWordList);
    }

    public void doSetSearchWord_NotInScope(
            final Collection<String> searchWordList) {
        regINS(CK_NINS, cTL(searchWordList), getCValueSearchWord(),
                "SEARCH_WORD");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWord The value of searchWord as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setSearchWord_PrefixSearch(final String searchWord) {
        setSearchWord_LikeSearch(searchWord, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)} <br />
     * <pre>e.g. setSearchWord_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param searchWord The value of searchWord as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setSearchWord_LikeSearch(final String searchWord,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(searchWord), getCValueSearchWord(), "SEARCH_WORD",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @param searchWord The value of searchWord as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setSearchWord_NotLikeSearch(final String searchWord,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(searchWord), getCValueSearchWord(), "SEARCH_WORD",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     */
    public void setSearchWord_IsNull() {
        regSearchWord(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     */
    public void setSearchWord_IsNullOrEmpty() {
        regSearchWord(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     */
    public void setSearchWord_IsNotNull() {
        regSearchWord(CK_ISNN, DOBJ);
    }

    protected void regSearchWord(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueSearchWord(), "SEARCH_WORD");
    }

    abstract protected ConditionValue getCValueSearchWord();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
     * @param requestedTime The value of requestedTime as equal. (NullAllowed: if null, no condition)
     */
    public void setRequestedTime_Equal(final java.sql.Timestamp requestedTime) {
        regRequestedTime(CK_EQ, requestedTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
     * @param requestedTime The value of requestedTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setRequestedTime_GreaterThan(
            final java.sql.Timestamp requestedTime) {
        regRequestedTime(CK_GT, requestedTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
     * @param requestedTime The value of requestedTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setRequestedTime_LessThan(final java.sql.Timestamp requestedTime) {
        regRequestedTime(CK_LT, requestedTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
     * @param requestedTime The value of requestedTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setRequestedTime_GreaterEqual(
            final java.sql.Timestamp requestedTime) {
        regRequestedTime(CK_GE, requestedTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
     * @param requestedTime The value of requestedTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setRequestedTime_LessEqual(
            final java.sql.Timestamp requestedTime) {
        regRequestedTime(CK_LE, requestedTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
     * <pre>e.g. setRequestedTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of requestedTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of requestedTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setRequestedTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueRequestedTime(),
                "REQUESTED_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of requestedTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of requestedTime. (NullAllowed: if null, no to-condition)
     */
    public void setRequestedTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setRequestedTime_FromTo(fromDate, toDate, new DateFromToOption());
    }

    protected void regRequestedTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueRequestedTime(), "REQUESTED_TIME");
    }

    abstract protected ConditionValue getCValueRequestedTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param responseTime The value of responseTime as equal. (NullAllowed: if null, no condition)
     */
    public void setResponseTime_Equal(final Integer responseTime) {
        doSetResponseTime_Equal(responseTime);
    }

    protected void doSetResponseTime_Equal(final Integer responseTime) {
        regResponseTime(CK_EQ, responseTime);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param responseTime The value of responseTime as notEqual. (NullAllowed: if null, no condition)
     */
    public void setResponseTime_NotEqual(final Integer responseTime) {
        doSetResponseTime_NotEqual(responseTime);
    }

    protected void doSetResponseTime_NotEqual(final Integer responseTime) {
        regResponseTime(CK_NES, responseTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param responseTime The value of responseTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setResponseTime_GreaterThan(final Integer responseTime) {
        regResponseTime(CK_GT, responseTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param responseTime The value of responseTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setResponseTime_LessThan(final Integer responseTime) {
        regResponseTime(CK_LT, responseTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param responseTime The value of responseTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setResponseTime_GreaterEqual(final Integer responseTime) {
        regResponseTime(CK_GE, responseTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param responseTime The value of responseTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setResponseTime_LessEqual(final Integer responseTime) {
        regResponseTime(CK_LE, responseTime);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param minNumber The min number of responseTime. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of responseTime. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setResponseTime_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueResponseTime(), "RESPONSE_TIME",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param responseTimeList The collection of responseTime as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setResponseTime_InScope(
            final Collection<Integer> responseTimeList) {
        doSetResponseTime_InScope(responseTimeList);
    }

    protected void doSetResponseTime_InScope(
            final Collection<Integer> responseTimeList) {
        regINS(CK_INS, cTL(responseTimeList), getCValueResponseTime(),
                "RESPONSE_TIME");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @param responseTimeList The collection of responseTime as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setResponseTime_NotInScope(
            final Collection<Integer> responseTimeList) {
        doSetResponseTime_NotInScope(responseTimeList);
    }

    protected void doSetResponseTime_NotInScope(
            final Collection<Integer> responseTimeList) {
        regINS(CK_NINS, cTL(responseTimeList), getCValueResponseTime(),
                "RESPONSE_TIME");
    }

    protected void regResponseTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueResponseTime(), "RESPONSE_TIME");
    }

    abstract protected ConditionValue getCValueResponseTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param hitCount The value of hitCount as equal. (NullAllowed: if null, no condition)
     */
    public void setHitCount_Equal(final Long hitCount) {
        doSetHitCount_Equal(hitCount);
    }

    protected void doSetHitCount_Equal(final Long hitCount) {
        regHitCount(CK_EQ, hitCount);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param hitCount The value of hitCount as notEqual. (NullAllowed: if null, no condition)
     */
    public void setHitCount_NotEqual(final Long hitCount) {
        doSetHitCount_NotEqual(hitCount);
    }

    protected void doSetHitCount_NotEqual(final Long hitCount) {
        regHitCount(CK_NES, hitCount);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param hitCount The value of hitCount as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setHitCount_GreaterThan(final Long hitCount) {
        regHitCount(CK_GT, hitCount);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param hitCount The value of hitCount as lessThan. (NullAllowed: if null, no condition)
     */
    public void setHitCount_LessThan(final Long hitCount) {
        regHitCount(CK_LT, hitCount);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param hitCount The value of hitCount as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setHitCount_GreaterEqual(final Long hitCount) {
        regHitCount(CK_GE, hitCount);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param hitCount The value of hitCount as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setHitCount_LessEqual(final Long hitCount) {
        regHitCount(CK_LE, hitCount);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param minNumber The min number of hitCount. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of hitCount. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setHitCount_RangeOf(final Long minNumber, final Long maxNumber,
            final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueHitCount(), "HIT_COUNT",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param hitCountList The collection of hitCount as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHitCount_InScope(final Collection<Long> hitCountList) {
        doSetHitCount_InScope(hitCountList);
    }

    protected void doSetHitCount_InScope(final Collection<Long> hitCountList) {
        regINS(CK_INS, cTL(hitCountList), getCValueHitCount(), "HIT_COUNT");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @param hitCountList The collection of hitCount as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHitCount_NotInScope(final Collection<Long> hitCountList) {
        doSetHitCount_NotInScope(hitCountList);
    }

    protected void doSetHitCount_NotInScope(final Collection<Long> hitCountList) {
        regINS(CK_NINS, cTL(hitCountList), getCValueHitCount(), "HIT_COUNT");
    }

    protected void regHitCount(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueHitCount(), "HIT_COUNT");
    }

    abstract protected ConditionValue getCValueHitCount();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param queryOffset The value of queryOffset as equal. (NullAllowed: if null, no condition)
     */
    public void setQueryOffset_Equal(final Integer queryOffset) {
        doSetQueryOffset_Equal(queryOffset);
    }

    protected void doSetQueryOffset_Equal(final Integer queryOffset) {
        regQueryOffset(CK_EQ, queryOffset);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param queryOffset The value of queryOffset as notEqual. (NullAllowed: if null, no condition)
     */
    public void setQueryOffset_NotEqual(final Integer queryOffset) {
        doSetQueryOffset_NotEqual(queryOffset);
    }

    protected void doSetQueryOffset_NotEqual(final Integer queryOffset) {
        regQueryOffset(CK_NES, queryOffset);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param queryOffset The value of queryOffset as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setQueryOffset_GreaterThan(final Integer queryOffset) {
        regQueryOffset(CK_GT, queryOffset);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param queryOffset The value of queryOffset as lessThan. (NullAllowed: if null, no condition)
     */
    public void setQueryOffset_LessThan(final Integer queryOffset) {
        regQueryOffset(CK_LT, queryOffset);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param queryOffset The value of queryOffset as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setQueryOffset_GreaterEqual(final Integer queryOffset) {
        regQueryOffset(CK_GE, queryOffset);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param queryOffset The value of queryOffset as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setQueryOffset_LessEqual(final Integer queryOffset) {
        regQueryOffset(CK_LE, queryOffset);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param minNumber The min number of queryOffset. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of queryOffset. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setQueryOffset_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueQueryOffset(), "QUERY_OFFSET",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param queryOffsetList The collection of queryOffset as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setQueryOffset_InScope(final Collection<Integer> queryOffsetList) {
        doSetQueryOffset_InScope(queryOffsetList);
    }

    protected void doSetQueryOffset_InScope(
            final Collection<Integer> queryOffsetList) {
        regINS(CK_INS, cTL(queryOffsetList), getCValueQueryOffset(),
                "QUERY_OFFSET");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @param queryOffsetList The collection of queryOffset as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setQueryOffset_NotInScope(
            final Collection<Integer> queryOffsetList) {
        doSetQueryOffset_NotInScope(queryOffsetList);
    }

    protected void doSetQueryOffset_NotInScope(
            final Collection<Integer> queryOffsetList) {
        regINS(CK_NINS, cTL(queryOffsetList), getCValueQueryOffset(),
                "QUERY_OFFSET");
    }

    protected void regQueryOffset(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueQueryOffset(), "QUERY_OFFSET");
    }

    abstract protected ConditionValue getCValueQueryOffset();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param queryPageSize The value of queryPageSize as equal. (NullAllowed: if null, no condition)
     */
    public void setQueryPageSize_Equal(final Integer queryPageSize) {
        doSetQueryPageSize_Equal(queryPageSize);
    }

    protected void doSetQueryPageSize_Equal(final Integer queryPageSize) {
        regQueryPageSize(CK_EQ, queryPageSize);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param queryPageSize The value of queryPageSize as notEqual. (NullAllowed: if null, no condition)
     */
    public void setQueryPageSize_NotEqual(final Integer queryPageSize) {
        doSetQueryPageSize_NotEqual(queryPageSize);
    }

    protected void doSetQueryPageSize_NotEqual(final Integer queryPageSize) {
        regQueryPageSize(CK_NES, queryPageSize);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param queryPageSize The value of queryPageSize as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setQueryPageSize_GreaterThan(final Integer queryPageSize) {
        regQueryPageSize(CK_GT, queryPageSize);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param queryPageSize The value of queryPageSize as lessThan. (NullAllowed: if null, no condition)
     */
    public void setQueryPageSize_LessThan(final Integer queryPageSize) {
        regQueryPageSize(CK_LT, queryPageSize);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param queryPageSize The value of queryPageSize as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setQueryPageSize_GreaterEqual(final Integer queryPageSize) {
        regQueryPageSize(CK_GE, queryPageSize);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param queryPageSize The value of queryPageSize as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setQueryPageSize_LessEqual(final Integer queryPageSize) {
        regQueryPageSize(CK_LE, queryPageSize);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param minNumber The min number of queryPageSize. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of queryPageSize. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setQueryPageSize_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueQueryPageSize(),
                "QUERY_PAGE_SIZE", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param queryPageSizeList The collection of queryPageSize as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setQueryPageSize_InScope(
            final Collection<Integer> queryPageSizeList) {
        doSetQueryPageSize_InScope(queryPageSizeList);
    }

    protected void doSetQueryPageSize_InScope(
            final Collection<Integer> queryPageSizeList) {
        regINS(CK_INS, cTL(queryPageSizeList), getCValueQueryPageSize(),
                "QUERY_PAGE_SIZE");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @param queryPageSizeList The collection of queryPageSize as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setQueryPageSize_NotInScope(
            final Collection<Integer> queryPageSizeList) {
        doSetQueryPageSize_NotInScope(queryPageSizeList);
    }

    protected void doSetQueryPageSize_NotInScope(
            final Collection<Integer> queryPageSizeList) {
        regINS(CK_NINS, cTL(queryPageSizeList), getCValueQueryPageSize(),
                "QUERY_PAGE_SIZE");
    }

    protected void regQueryPageSize(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueQueryPageSize(), "QUERY_PAGE_SIZE");
    }

    abstract protected ConditionValue getCValueQueryPageSize();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {VARCHAR(255)}
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
     * USER_AGENT: {VARCHAR(255)}
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
     * USER_AGENT: {VARCHAR(255)}
     * @param userAgent The value of userAgent as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_GreaterThan(final String userAgent) {
        regUserAgent(CK_GT, fRES(userAgent));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {VARCHAR(255)}
     * @param userAgent The value of userAgent as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_LessThan(final String userAgent) {
        regUserAgent(CK_LT, fRES(userAgent));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {VARCHAR(255)}
     * @param userAgent The value of userAgent as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_GreaterEqual(final String userAgent) {
        regUserAgent(CK_GE, fRES(userAgent));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_AGENT: {VARCHAR(255)}
     * @param userAgent The value of userAgent as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_LessEqual(final String userAgent) {
        regUserAgent(CK_LE, fRES(userAgent));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * USER_AGENT: {VARCHAR(255)}
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
     * USER_AGENT: {VARCHAR(255)}
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
     * USER_AGENT: {VARCHAR(255)}
     * @param userAgent The value of userAgent as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserAgent_PrefixSearch(final String userAgent) {
        setUserAgent_LikeSearch(userAgent, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * USER_AGENT: {VARCHAR(255)} <br />
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
     * USER_AGENT: {VARCHAR(255)}
     * @param userAgent The value of userAgent as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUserAgent_NotLikeSearch(final String userAgent,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(userAgent), getCValueUserAgent(), "USER_AGENT",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * USER_AGENT: {VARCHAR(255)}
     */
    public void setUserAgent_IsNull() {
        regUserAgent(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * USER_AGENT: {VARCHAR(255)}
     */
    public void setUserAgent_IsNullOrEmpty() {
        regUserAgent(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * USER_AGENT: {VARCHAR(255)}
     */
    public void setUserAgent_IsNotNull() {
        regUserAgent(CK_ISNN, DOBJ);
    }

    protected void regUserAgent(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUserAgent(), "USER_AGENT");
    }

    abstract protected ConditionValue getCValueUserAgent();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param referer The value of referer as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_Equal(final String referer) {
        doSetReferer_Equal(fRES(referer));
    }

    protected void doSetReferer_Equal(final String referer) {
        regReferer(CK_EQ, referer);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param referer The value of referer as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_NotEqual(final String referer) {
        doSetReferer_NotEqual(fRES(referer));
    }

    protected void doSetReferer_NotEqual(final String referer) {
        regReferer(CK_NES, referer);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param referer The value of referer as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_GreaterThan(final String referer) {
        regReferer(CK_GT, fRES(referer));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param referer The value of referer as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_LessThan(final String referer) {
        regReferer(CK_LT, fRES(referer));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param referer The value of referer as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_GreaterEqual(final String referer) {
        regReferer(CK_GE, fRES(referer));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param referer The value of referer as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_LessEqual(final String referer) {
        regReferer(CK_LE, fRES(referer));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param refererList The collection of referer as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_InScope(final Collection<String> refererList) {
        doSetReferer_InScope(refererList);
    }

    public void doSetReferer_InScope(final Collection<String> refererList) {
        regINS(CK_INS, cTL(refererList), getCValueReferer(), "REFERER");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param refererList The collection of referer as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_NotInScope(final Collection<String> refererList) {
        doSetReferer_NotInScope(refererList);
    }

    public void doSetReferer_NotInScope(final Collection<String> refererList) {
        regINS(CK_NINS, cTL(refererList), getCValueReferer(), "REFERER");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param referer The value of referer as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setReferer_PrefixSearch(final String referer) {
        setReferer_LikeSearch(referer, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * REFERER: {VARCHAR(1000)} <br />
     * <pre>e.g. setReferer_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param referer The value of referer as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setReferer_LikeSearch(final String referer,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(referer), getCValueReferer(), "REFERER",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     * @param referer The value of referer as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setReferer_NotLikeSearch(final String referer,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(referer), getCValueReferer(), "REFERER",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     */
    public void setReferer_IsNull() {
        regReferer(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     */
    public void setReferer_IsNullOrEmpty() {
        regReferer(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * REFERER: {VARCHAR(1000)}
     */
    public void setReferer_IsNotNull() {
        regReferer(CK_ISNN, DOBJ);
    }

    protected void regReferer(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueReferer(), "REFERER");
    }

    abstract protected ConditionValue getCValueReferer();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIp The value of clientIp as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_Equal(final String clientIp) {
        doSetClientIp_Equal(fRES(clientIp));
    }

    protected void doSetClientIp_Equal(final String clientIp) {
        regClientIp(CK_EQ, clientIp);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIp The value of clientIp as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_NotEqual(final String clientIp) {
        doSetClientIp_NotEqual(fRES(clientIp));
    }

    protected void doSetClientIp_NotEqual(final String clientIp) {
        regClientIp(CK_NES, clientIp);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIp The value of clientIp as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_GreaterThan(final String clientIp) {
        regClientIp(CK_GT, fRES(clientIp));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIp The value of clientIp as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_LessThan(final String clientIp) {
        regClientIp(CK_LT, fRES(clientIp));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIp The value of clientIp as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_GreaterEqual(final String clientIp) {
        regClientIp(CK_GE, fRES(clientIp));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIp The value of clientIp as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_LessEqual(final String clientIp) {
        regClientIp(CK_LE, fRES(clientIp));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIpList The collection of clientIp as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_InScope(final Collection<String> clientIpList) {
        doSetClientIp_InScope(clientIpList);
    }

    public void doSetClientIp_InScope(final Collection<String> clientIpList) {
        regINS(CK_INS, cTL(clientIpList), getCValueClientIp(), "CLIENT_IP");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIpList The collection of clientIp as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_NotInScope(final Collection<String> clientIpList) {
        doSetClientIp_NotInScope(clientIpList);
    }

    public void doSetClientIp_NotInScope(final Collection<String> clientIpList) {
        regINS(CK_NINS, cTL(clientIpList), getCValueClientIp(), "CLIENT_IP");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIp The value of clientIp as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setClientIp_PrefixSearch(final String clientIp) {
        setClientIp_LikeSearch(clientIp, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)} <br />
     * <pre>e.g. setClientIp_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param clientIp The value of clientIp as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setClientIp_LikeSearch(final String clientIp,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(clientIp), getCValueClientIp(), "CLIENT_IP",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @param clientIp The value of clientIp as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setClientIp_NotLikeSearch(final String clientIp,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(clientIp), getCValueClientIp(), "CLIENT_IP",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     */
    public void setClientIp_IsNull() {
        regClientIp(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     */
    public void setClientIp_IsNullOrEmpty() {
        regClientIp(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * CLIENT_IP: {VARCHAR(50)}
     */
    public void setClientIp_IsNotNull() {
        regClientIp(CK_ISNN, DOBJ);
    }

    protected void regClientIp(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueClientIp(), "CLIENT_IP");
    }

    abstract protected ConditionValue getCValueClientIp();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionId The value of userSessionId as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_Equal(final String userSessionId) {
        doSetUserSessionId_Equal(fRES(userSessionId));
    }

    protected void doSetUserSessionId_Equal(final String userSessionId) {
        regUserSessionId(CK_EQ, userSessionId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionId The value of userSessionId as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_NotEqual(final String userSessionId) {
        doSetUserSessionId_NotEqual(fRES(userSessionId));
    }

    protected void doSetUserSessionId_NotEqual(final String userSessionId) {
        regUserSessionId(CK_NES, userSessionId);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionId The value of userSessionId as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_GreaterThan(final String userSessionId) {
        regUserSessionId(CK_GT, fRES(userSessionId));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionId The value of userSessionId as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_LessThan(final String userSessionId) {
        regUserSessionId(CK_LT, fRES(userSessionId));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionId The value of userSessionId as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_GreaterEqual(final String userSessionId) {
        regUserSessionId(CK_GE, fRES(userSessionId));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionId The value of userSessionId as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_LessEqual(final String userSessionId) {
        regUserSessionId(CK_LE, fRES(userSessionId));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionIdList The collection of userSessionId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_InScope(
            final Collection<String> userSessionIdList) {
        doSetUserSessionId_InScope(userSessionIdList);
    }

    public void doSetUserSessionId_InScope(
            final Collection<String> userSessionIdList) {
        regINS(CK_INS, cTL(userSessionIdList), getCValueUserSessionId(),
                "USER_SESSION_ID");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionIdList The collection of userSessionId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_NotInScope(
            final Collection<String> userSessionIdList) {
        doSetUserSessionId_NotInScope(userSessionIdList);
    }

    public void doSetUserSessionId_NotInScope(
            final Collection<String> userSessionIdList) {
        regINS(CK_NINS, cTL(userSessionIdList), getCValueUserSessionId(),
                "USER_SESSION_ID");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionId The value of userSessionId as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserSessionId_PrefixSearch(final String userSessionId) {
        setUserSessionId_LikeSearch(userSessionId, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)} <br />
     * <pre>e.g. setUserSessionId_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param userSessionId The value of userSessionId as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUserSessionId_LikeSearch(final String userSessionId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(userSessionId), getCValueUserSessionId(),
                "USER_SESSION_ID", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @param userSessionId The value of userSessionId as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUserSessionId_NotLikeSearch(final String userSessionId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(userSessionId), getCValueUserSessionId(),
                "USER_SESSION_ID", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     */
    public void setUserSessionId_IsNull() {
        regUserSessionId(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     */
    public void setUserSessionId_IsNullOrEmpty() {
        regUserSessionId(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     */
    public void setUserSessionId_IsNotNull() {
        regUserSessionId(CK_ISNN, DOBJ);
    }

    protected void regUserSessionId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUserSessionId(), "USER_SESSION_ID");
    }

    abstract protected ConditionValue getCValueUserSessionId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType}
     * @param accessType The value of accessType as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setAccessType_Equal(final String accessType) {
        doSetAccessType_Equal(fRES(accessType));
    }

    /**
     * Equal(=). As AccessType. And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} <br />
     * Access Type
     * @param cdef The instance of classification definition (as ENUM type). (NullAllowed: if null, no condition)
     */
    public void setAccessType_Equal_AsAccessType(final CDef.AccessType cdef) {
        doSetAccessType_Equal(cdef != null ? cdef.code() : null);
    }

    /**
     * Equal(=). As Web (W). And OnlyOnceRegistered. <br />
     * Web: Web
     */
    public void setAccessType_Equal_Web() {
        setAccessType_Equal_AsAccessType(CDef.AccessType.Web);
    }

    /**
     * Equal(=). As Mobile (M). And OnlyOnceRegistered. <br />
     * Mobile: Mobile
     */
    public void setAccessType_Equal_Mobile() {
        setAccessType_Equal_AsAccessType(CDef.AccessType.Mobile);
    }

    /**
     * Equal(=). As Xml (X). And OnlyOnceRegistered. <br />
     * Xml: Xml
     */
    public void setAccessType_Equal_Xml() {
        setAccessType_Equal_AsAccessType(CDef.AccessType.Xml);
    }

    /**
     * Equal(=). As Json (J). And OnlyOnceRegistered. <br />
     * Json: Json
     */
    public void setAccessType_Equal_Json() {
        setAccessType_Equal_AsAccessType(CDef.AccessType.Json);
    }

    protected void doSetAccessType_Equal(final String accessType) {
        regAccessType(CK_EQ, accessType);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType}
     * @param accessType The value of accessType as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setAccessType_NotEqual(final String accessType) {
        doSetAccessType_NotEqual(fRES(accessType));
    }

    /**
     * NotEqual(&lt;&gt;). As AccessType. And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} <br />
     * Access Type
     * @param cdef The instance of classification definition (as ENUM type). (NullAllowed: if null, no condition)
     */
    public void setAccessType_NotEqual_AsAccessType(final CDef.AccessType cdef) {
        doSetAccessType_NotEqual(cdef != null ? cdef.code() : null);
    }

    /**
     * NotEqual(&lt;&gt;). As Web (W). And OnlyOnceRegistered. <br />
     * Web: Web
     */
    public void setAccessType_NotEqual_Web() {
        setAccessType_NotEqual_AsAccessType(CDef.AccessType.Web);
    }

    /**
     * NotEqual(&lt;&gt;). As Mobile (M). And OnlyOnceRegistered. <br />
     * Mobile: Mobile
     */
    public void setAccessType_NotEqual_Mobile() {
        setAccessType_NotEqual_AsAccessType(CDef.AccessType.Mobile);
    }

    /**
     * NotEqual(&lt;&gt;). As Xml (X). And OnlyOnceRegistered. <br />
     * Xml: Xml
     */
    public void setAccessType_NotEqual_Xml() {
        setAccessType_NotEqual_AsAccessType(CDef.AccessType.Xml);
    }

    /**
     * NotEqual(&lt;&gt;). As Json (J). And OnlyOnceRegistered. <br />
     * Json: Json
     */
    public void setAccessType_NotEqual_Json() {
        setAccessType_NotEqual_AsAccessType(CDef.AccessType.Json);
    }

    protected void doSetAccessType_NotEqual(final String accessType) {
        regAccessType(CK_NES, accessType);
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType}
     * @param accessTypeList The collection of accessType as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setAccessType_InScope(final Collection<String> accessTypeList) {
        doSetAccessType_InScope(accessTypeList);
    }

    /**
     * InScope {in ('a', 'b')}. As AccessType. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} <br />
     * Access Type
     * @param cdefList The list of classification definition (as ENUM type). (NullAllowed: if null (or empty), no condition)
     */
    public void setAccessType_InScope_AsAccessType(
            final Collection<CDef.AccessType> cdefList) {
        doSetAccessType_InScope(cTStrL(cdefList));
    }

    public void doSetAccessType_InScope(final Collection<String> accessTypeList) {
        regINS(CK_INS, cTL(accessTypeList), getCValueAccessType(),
                "ACCESS_TYPE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType}
     * @param accessTypeList The collection of accessType as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setAccessType_NotInScope(final Collection<String> accessTypeList) {
        doSetAccessType_NotInScope(accessTypeList);
    }

    /**
     * NotInScope {not in ('a', 'b')}. As AccessType. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} <br />
     * Access Type
     * @param cdefList The list of classification definition (as ENUM type). (NullAllowed: if null (or empty), no condition)
     */
    public void setAccessType_NotInScope_AsAccessType(
            final Collection<CDef.AccessType> cdefList) {
        doSetAccessType_NotInScope(cTStrL(cdefList));
    }

    public void doSetAccessType_NotInScope(
            final Collection<String> accessTypeList) {
        regINS(CK_NINS, cTL(accessTypeList), getCValueAccessType(),
                "ACCESS_TYPE");
    }

    protected void regAccessType(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueAccessType(), "ACCESS_TYPE");
    }

    abstract protected ConditionValue getCValueAccessType();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param userId The value of userId as equal. (NullAllowed: if null, no condition)
     */
    public void setUserId_Equal(final Long userId) {
        doSetUserId_Equal(userId);
    }

    protected void doSetUserId_Equal(final Long userId) {
        regUserId(CK_EQ, userId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param userId The value of userId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setUserId_NotEqual(final Long userId) {
        doSetUserId_NotEqual(userId);
    }

    protected void doSetUserId_NotEqual(final Long userId) {
        regUserId(CK_NES, userId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param userId The value of userId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setUserId_GreaterThan(final Long userId) {
        regUserId(CK_GT, userId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param userId The value of userId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setUserId_LessThan(final Long userId) {
        regUserId(CK_LT, userId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param userId The value of userId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setUserId_GreaterEqual(final Long userId) {
        regUserId(CK_GE, userId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param userId The value of userId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setUserId_LessEqual(final Long userId) {
        regUserId(CK_LE, userId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param minNumber The min number of userId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of userId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setUserId_RangeOf(final Long minNumber, final Long maxNumber,
            final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueUserId(), "USER_ID",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param userIdList The collection of userId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserId_InScope(final Collection<Long> userIdList) {
        doSetUserId_InScope(userIdList);
    }

    protected void doSetUserId_InScope(final Collection<Long> userIdList) {
        regINS(CK_INS, cTL(userIdList), getCValueUserId(), "USER_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @param userIdList The collection of userId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUserId_NotInScope(final Collection<Long> userIdList) {
        doSetUserId_NotInScope(userIdList);
    }

    protected void doSetUserId_NotInScope(final Collection<Long> userIdList) {
        regINS(CK_NINS, cTL(userIdList), getCValueUserId(), "USER_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select USER_ID from USER_INFO where ...)} <br />
     * USER_INFO by my USER_ID, named 'userInfo'.
     * @param subQuery The sub-query of UserInfo for 'in-scope'. (NotNull)
     */
    public void inScopeUserInfo(final SubQuery<UserInfoCB> subQuery) {
        assertObjectNotNull("subQuery<UserInfoCB>", subQuery);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepUserId_InScopeRelation_UserInfo(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "USER_ID", "ID",
                subQueryPropertyName, "userInfo");
    }

    public abstract String keepUserId_InScopeRelation_UserInfo(
            UserInfoCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select USER_ID from USER_INFO where ...)} <br />
     * USER_INFO by my USER_ID, named 'userInfo'.
     * @param subQuery The sub-query of UserInfo for 'not in-scope'. (NotNull)
     */
    public void notInScopeUserInfo(final SubQuery<UserInfoCB> subQuery) {
        assertObjectNotNull("subQuery<UserInfoCB>", subQuery);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepUserId_NotInScopeRelation_UserInfo(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "USER_ID", "ID",
                subQueryPropertyName, "userInfo");
    }

    public abstract String keepUserId_NotInScopeRelation_UserInfo(
            UserInfoCQ subQuery);

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     */
    public void setUserId_IsNull() {
        regUserId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     */
    public void setUserId_IsNotNull() {
        regUserId(CK_ISNN, DOBJ);
    }

    protected void regUserId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUserId(), "USER_ID");
    }

    abstract protected ConditionValue getCValueUserId();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<SearchLogCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<SearchLogCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<SearchLogCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<SearchLogCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<SearchLogCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<SearchLogCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<SearchLogCB> xcreateSSQFunction(final String operand) {
        return new HpSSQFunction<SearchLogCB>(new HpSSQSetupper<SearchLogCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<SearchLogCB> subQuery,
                    final HpSSQOption<SearchLogCB> option) {
                xscalarCondition(function, subQuery, operand, option);
            }
        });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<SearchLogCB> subQuery, final String operand,
            final HpSSQOption<SearchLogCB> option) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(SearchLogCQ subQuery);

    protected SearchLogCB xcreateScalarConditionCB() {
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected SearchLogCB xcreateScalarConditionPartitionByCB() {
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<SearchLogCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(SearchLogCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<SearchLogCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<SearchLogCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<SearchLogCB>(new HpQDRSetupper<SearchLogCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<SearchLogCB> subQuery, final String operand,
                    final Object value, final DerivedReferrerOption option) {
                xqderiveMyselfDerived(function, subQuery, operand, value,
                        option);
            }
        });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<SearchLogCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(SearchLogCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(SearchLogCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery<SearchLogCB>", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(SearchLogCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return SearchLogCB.class.getName();
    }

    protected String xabCQ() {
        return SearchLogCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
