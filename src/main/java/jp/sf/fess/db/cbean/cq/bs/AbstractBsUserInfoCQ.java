/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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
import java.util.Date;
import java.util.List;

import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.cbean.FavoriteLogCB;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.cbean.cq.FavoriteLogCQ;
import jp.sf.fess.db.cbean.cq.SearchLogCQ;
import jp.sf.fess.db.cbean.cq.UserInfoCQ;

import org.seasar.dbflute.cbean.AbstractConditionQuery;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ManualOrderBean;
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
    public AbstractBsUserInfoCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
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
     * Set up ExistsReferrer (correlated sub-query). <br />
     * {exists (select USER_ID from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">existsFavoriteLogList</span>(new SubQuery&lt;FavoriteLogCB&gt;() {
     *     public void query(FavoriteLogCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of FavoriteLogList for 'exists'. (NotNull)
     */
    public void existsFavoriteLogList(final SubQuery<FavoriteLogCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_ExistsReferrer_FavoriteLogList(cb.query());
        registerExistsReferrer(cb.query(), "ID", "USER_ID", pp,
                "favoriteLogList");
    }

    public abstract String keepId_ExistsReferrer_FavoriteLogList(
            FavoriteLogCQ sq);

    /**
     * Set up ExistsReferrer (correlated sub-query). <br />
     * {exists (select USER_ID from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">existsSearchLogList</span>(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of SearchLogList for 'exists'. (NotNull)
     */
    public void existsSearchLogList(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_ExistsReferrer_SearchLogList(cb.query());
        registerExistsReferrer(cb.query(), "ID", "USER_ID", pp, "searchLogList");
    }

    public abstract String keepId_ExistsReferrer_SearchLogList(SearchLogCQ sq);

    /**
     * Set up NotExistsReferrer (correlated sub-query). <br />
     * {not exists (select USER_ID from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">notExistsFavoriteLogList</span>(new SubQuery&lt;FavoriteLogCB&gt;() {
     *     public void query(FavoriteLogCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_FavoriteLogList for 'not exists'. (NotNull)
     */
    public void notExistsFavoriteLogList(final SubQuery<FavoriteLogCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotExistsReferrer_FavoriteLogList(cb.query());
        registerNotExistsReferrer(cb.query(), "ID", "USER_ID", pp,
                "favoriteLogList");
    }

    public abstract String keepId_NotExistsReferrer_FavoriteLogList(
            FavoriteLogCQ sq);

    /**
     * Set up NotExistsReferrer (correlated sub-query). <br />
     * {not exists (select USER_ID from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">notExistsSearchLogList</span>(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_SearchLogList for 'not exists'. (NotNull)
     */
    public void notExistsSearchLogList(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotExistsReferrer_SearchLogList(cb.query());
        registerNotExistsReferrer(cb.query(), "ID", "USER_ID", pp,
                "searchLogList");
    }

    public abstract String keepId_NotExistsReferrer_SearchLogList(SearchLogCQ sq);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select USER_ID from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * @param subQuery The sub-query of FavoriteLogList for 'in-scope'. (NotNull)
     */
    public void inScopeFavoriteLogList(final SubQuery<FavoriteLogCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_InScopeRelation_FavoriteLogList(cb.query());
        registerInScopeRelation(cb.query(), "ID", "USER_ID", pp,
                "favoriteLogList");
    }

    public abstract String keepId_InScopeRelation_FavoriteLogList(
            FavoriteLogCQ sq);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select USER_ID from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * @param subQuery The sub-query of SearchLogList for 'in-scope'. (NotNull)
     */
    public void inScopeSearchLogList(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_InScopeRelation_SearchLogList(cb.query());
        registerInScopeRelation(cb.query(), "ID", "USER_ID", pp,
                "searchLogList");
    }

    public abstract String keepId_InScopeRelation_SearchLogList(SearchLogCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select USER_ID from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * @param subQuery The sub-query of FavoriteLogList for 'not in-scope'. (NotNull)
     */
    public void notInScopeFavoriteLogList(final SubQuery<FavoriteLogCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotInScopeRelation_FavoriteLogList(cb.query());
        registerNotInScopeRelation(cb.query(), "ID", "USER_ID", pp,
                "favoriteLogList");
    }

    public abstract String keepId_NotInScopeRelation_FavoriteLogList(
            FavoriteLogCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select USER_ID from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * @param subQuery The sub-query of SearchLogList for 'not in-scope'. (NotNull)
     */
    public void notInScopeSearchLogList(final SubQuery<SearchLogCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotInScopeRelation_SearchLogList(cb.query());
        registerNotInScopeRelation(cb.query(), "ID", "USER_ID", pp,
                "searchLogList");
    }

    public abstract String keepId_NotInScopeRelation_SearchLogList(
            SearchLogCQ sq);

    public void xsderiveFavoriteLogList(final String fn,
            final SubQuery<FavoriteLogCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_SpecifyDerivedReferrer_FavoriteLogList(cb
                .query());
        registerSpecifyDerivedReferrer(fn, cb.query(), "ID", "USER_ID", pp,
                "favoriteLogList", al, op);
    }

    public abstract String keepId_SpecifyDerivedReferrer_FavoriteLogList(
            FavoriteLogCQ sq);

    public void xsderiveSearchLogList(final String fn,
            final SubQuery<SearchLogCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_SpecifyDerivedReferrer_SearchLogList(cb
                .query());
        registerSpecifyDerivedReferrer(fn, cb.query(), "ID", "USER_ID", pp,
                "searchLogList", al, op);
    }

    public abstract String keepId_SpecifyDerivedReferrer_SearchLogList(
            SearchLogCQ sq);

    /**
     * Prepare for (Query)DerivedReferrer (correlated sub-query). <br />
     * {FOO &lt;= (select max(BAR) from FAVORITE_LOG where ...)} <br />
     * FAVORITE_LOG by USER_ID, named 'favoriteLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">derivedFavoriteLogList()</span>.<span style="color: #DD4747">max</span>(new SubQuery&lt;FavoriteLogCB&gt;() {
     *     public void query(FavoriteLogCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #DD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
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
                    public void setup(final String fn,
                            final SubQuery<FavoriteLogCB> sq, final String rd,
                            final Object vl, final DerivedReferrerOption op) {
                        xqderiveFavoriteLogList(fn, sq, rd, vl, op);
                    }
                });
    }

    public void xqderiveFavoriteLogList(final String fn,
            final SubQuery<FavoriteLogCB> sq, final String rd, final Object vl,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FavoriteLogCB cb = new FavoriteLogCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String sqpp = keepId_QueryDerivedReferrer_FavoriteLogList(cb
                .query());
        final String prpp = keepId_QueryDerivedReferrer_FavoriteLogListParameter(vl);
        registerQueryDerivedReferrer(fn, cb.query(), "ID", "USER_ID", sqpp,
                "favoriteLogList", rd, vl, prpp, op);
    }

    public abstract String keepId_QueryDerivedReferrer_FavoriteLogList(
            FavoriteLogCQ sq);

    public abstract String keepId_QueryDerivedReferrer_FavoriteLogListParameter(
            Object vl);

    /**
     * Prepare for (Query)DerivedReferrer (correlated sub-query). <br />
     * {FOO &lt;= (select max(BAR) from SEARCH_LOG where ...)} <br />
     * SEARCH_LOG by USER_ID, named 'searchLogAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">derivedSearchLogList()</span>.<span style="color: #DD4747">max</span>(new SubQuery&lt;SearchLogCB&gt;() {
     *     public void query(SearchLogCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #DD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<SearchLogCB> derivedSearchLogList() {
        return xcreateQDRFunctionSearchLogList();
    }

    protected HpQDRFunction<SearchLogCB> xcreateQDRFunctionSearchLogList() {
        return new HpQDRFunction<SearchLogCB>(new HpQDRSetupper<SearchLogCB>() {
            @Override
            public void setup(final String fn, final SubQuery<SearchLogCB> sq,
                    final String rd, final Object vl,
                    final DerivedReferrerOption op) {
                xqderiveSearchLogList(fn, sq, rd, vl, op);
            }
        });
    }

    public void xqderiveSearchLogList(final String fn,
            final SubQuery<SearchLogCB> sq, final String rd, final Object vl,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final SearchLogCB cb = new SearchLogCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String sqpp = keepId_QueryDerivedReferrer_SearchLogList(cb
                .query());
        final String prpp = keepId_QueryDerivedReferrer_SearchLogListParameter(vl);
        registerQueryDerivedReferrer(fn, cb.query(), "ID", "USER_ID", sqpp,
                "searchLogList", rd, vl, prpp, op);
    }

    public abstract String keepId_QueryDerivedReferrer_SearchLogList(
            SearchLogCQ sq);

    public abstract String keepId_QueryDerivedReferrer_SearchLogListParameter(
            Object vl);

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

    protected void regId(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueId(), "ID");
    }

    protected abstract ConditionValue getCValueId();

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
     * <pre>e.g. setCode_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
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

    protected void regCode(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueCode(), "CODE");
    }

    protected abstract ConditionValue getCValueCode();

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
     * <pre>e.g. setCreatedTime_FromTo(fromDate, toDate, new <span style="color: #DD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createdTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createdTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setCreatedTime_FromTo(final Date fromDatetime,
            final Date toDatetime, final FromToOption fromToOption) {
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
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #DD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of createdTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of createdTime. (NullAllowed: if null, no to-condition)
     */
    public void setCreatedTime_DateFromTo(final Date fromDate, final Date toDate) {
        setCreatedTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    protected void regCreatedTime(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueCreatedTime(), "CREATED_TIME");
    }

    protected abstract ConditionValue getCValueCreatedTime();

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
     * <pre>e.g. setUpdatedTime_FromTo(fromDate, toDate, new <span style="color: #DD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of updatedTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of updatedTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setUpdatedTime_FromTo(final Date fromDatetime,
            final Date toDatetime, final FromToOption fromToOption) {
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
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #DD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of updatedTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of updatedTime. (NullAllowed: if null, no to-condition)
     */
    public void setUpdatedTime_DateFromTo(final Date fromDate, final Date toDate) {
        setUpdatedTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    protected void regUpdatedTime(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueUpdatedTime(), "UPDATED_TIME");
    }

    protected abstract ConditionValue getCValueUpdatedTime();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_Equal()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ, UserInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES, UserInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT, UserInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessThan()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT, UserInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE, UserInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UserInfoCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE, UserInfoCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(final String fn,
            final SubQuery<CB> sq, final String rd, final HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        final UserInfoCB cb = xcreateScalarConditionCB();
        sq.query((CB) cb);
        final String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB) xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }

    public abstract String keepScalarCondition(UserInfoCQ sq);

    protected UserInfoCB xcreateScalarConditionCB() {
        final UserInfoCB cb = newMyCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected UserInfoCB xcreateScalarConditionPartitionByCB() {
        final UserInfoCB cb = newMyCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String fn, final SubQuery<UserInfoCB> sq,
            final String al, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepSpecifyMyselfDerived(cb.query());
        final String pk = "ID";
        registerSpecifyMyselfDerived(fn, cb.query(), pk, pk, pp,
                "myselfDerived", al, op);
    }

    public abstract String keepSpecifyMyselfDerived(UserInfoCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<UserInfoCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(UserInfoCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(
            final String fn, final SubQuery<CB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForDerivedReferrer(this);
        sq.query((CB) cb);
        final String pk = "ID";
        final String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp,
                "myselfDerived", rd, vl, prpp, op);
    }

    public abstract String keepQueryMyselfDerived(UserInfoCQ sq);

    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfExists(final SubQuery<UserInfoCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForMyselfExists(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepMyselfExists(cb.query());
        registerMyselfExists(cb.query(), pp);
    }

    public abstract String keepMyselfExists(UserInfoCQ sq);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfInScope(final SubQuery<UserInfoCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForMyselfInScope(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepMyselfInScope(cb.query());
        registerMyselfInScope(cb.query(), pp);
    }

    public abstract String keepMyselfInScope(UserInfoCQ sq);

    /**
     * Order along manual ordering information.
     * <pre>
     * MemberCB cb = new MemberCB();
     * ManualOrderBean mob = new ManualOrderBean();
     * mob.<span style="color: #DD4747">when_GreaterEqual</span>(priorityDate); <span style="color: #3F7E5E">// e.g. 2000/01/01</span>
     * cb.query().addOrderBy_Birthdate_Asc().<span style="color: #DD4747">withManualOrder(mob)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when BIRTHDATE &gt;= '2000/01/01' then 0</span>
     * <span style="color: #3F7E5E">//     else 1</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     *
     * MemberCB cb = new MemberCB();
     * ManualOrderBean mob = new ManualOrderBean();
     * mob.<span style="color: #DD4747">when_Equal</span>(CDef.MemberStatus.Withdrawal);
     * mob.<span style="color: #DD4747">when_Equal</span>(CDef.MemberStatus.Formalized);
     * mob.<span style="color: #DD4747">when_Equal</span>(CDef.MemberStatus.Provisional);
     * cb.query().addOrderBy_MemberStatusCode_Asc().<span style="color: #DD4747">withManualOrder(mob)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'WDL' then 0</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'FML' then 1</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'PRV' then 2</span>
     * <span style="color: #3F7E5E">//     else 3</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     * </pre>
     * <p>This function with Union is unsupported!</p>
     * <p>The order values are bound (treated as bind parameter).</p>
     * @param mob The bean of manual order containing order values. (NotNull)
     */
    public void withManualOrder(final ManualOrderBean mob) { // is user public!
        xdoWithManualOrder(mob);
    }

    // ===================================================================================
    //                                                                          Compatible
    //                                                                          ==========
    /**
     * Order along the list of manual values. #beforejava8 <br />
     * This function with Union is unsupported! <br />
     * The order values are bound (treated as bind parameter).
     * <pre>
     * MemberCB cb = new MemberCB();
     * List&lt;CDef.MemberStatus&gt; orderValueList = new ArrayList&lt;CDef.MemberStatus&gt;();
     * orderValueList.add(CDef.MemberStatus.Withdrawal);
     * orderValueList.add(CDef.MemberStatus.Formalized);
     * orderValueList.add(CDef.MemberStatus.Provisional);
     * cb.query().addOrderBy_MemberStatusCode_Asc().<span style="color: #DD4747">withManualOrder(orderValueList)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'WDL' then 0</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'FML' then 1</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'PRV' then 2</span>
     * <span style="color: #3F7E5E">//     else 3</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     * </pre>
     * @param orderValueList The list of order values for manual ordering. (NotNull)
     */
    public void withManualOrder(final List<? extends Object> orderValueList) { // is user public!
        assertObjectNotNull("withManualOrder(orderValueList)", orderValueList);
        final ManualOrderBean manualOrderBean = new ManualOrderBean();
        manualOrderBean.acceptOrderValueList(orderValueList);
        withManualOrder(manualOrderBean);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    protected UserInfoCB newMyCB() {
        return new UserInfoCB();
    }

    // very internal (for suppressing warn about 'Not Use Import')
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
