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
import jp.sf.fess.db.cbean.CrawlingSessionCB;
import jp.sf.fess.db.cbean.CrawlingSessionInfoCB;
import jp.sf.fess.db.cbean.cq.CrawlingSessionCQ;
import jp.sf.fess.db.cbean.cq.CrawlingSessionInfoCQ;

import org.seasar.dbflute.cbean.AbstractConditionQuery;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ManualOrderBean;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.chelper.HpQDRFunction;
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
 * The abstract condition-query of CRAWLING_SESSION_INFO.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsCrawlingSessionInfoCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsCrawlingSessionInfoCQ(final ConditionQuery referrerQuery,
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
        return "CRAWLING_SESSION_INFO";
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
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param crawlingSessionId The value of crawlingSessionId as equal. (NullAllowed: if null, no condition)
     */
    public void setCrawlingSessionId_Equal(final Long crawlingSessionId) {
        doSetCrawlingSessionId_Equal(crawlingSessionId);
    }

    protected void doSetCrawlingSessionId_Equal(final Long crawlingSessionId) {
        regCrawlingSessionId(CK_EQ, crawlingSessionId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param crawlingSessionId The value of crawlingSessionId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setCrawlingSessionId_NotEqual(final Long crawlingSessionId) {
        doSetCrawlingSessionId_NotEqual(crawlingSessionId);
    }

    protected void doSetCrawlingSessionId_NotEqual(final Long crawlingSessionId) {
        regCrawlingSessionId(CK_NES, crawlingSessionId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param crawlingSessionId The value of crawlingSessionId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setCrawlingSessionId_GreaterThan(final Long crawlingSessionId) {
        regCrawlingSessionId(CK_GT, crawlingSessionId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param crawlingSessionId The value of crawlingSessionId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setCrawlingSessionId_LessThan(final Long crawlingSessionId) {
        regCrawlingSessionId(CK_LT, crawlingSessionId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param crawlingSessionId The value of crawlingSessionId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setCrawlingSessionId_GreaterEqual(final Long crawlingSessionId) {
        regCrawlingSessionId(CK_GE, crawlingSessionId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param crawlingSessionId The value of crawlingSessionId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setCrawlingSessionId_LessEqual(final Long crawlingSessionId) {
        regCrawlingSessionId(CK_LE, crawlingSessionId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param minNumber The min number of crawlingSessionId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of crawlingSessionId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setCrawlingSessionId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueCrawlingSessionId(),
                "CRAWLING_SESSION_ID", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param crawlingSessionIdList The collection of crawlingSessionId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawlingSessionId_InScope(
            final Collection<Long> crawlingSessionIdList) {
        doSetCrawlingSessionId_InScope(crawlingSessionIdList);
    }

    protected void doSetCrawlingSessionId_InScope(
            final Collection<Long> crawlingSessionIdList) {
        regINS(CK_INS, cTL(crawlingSessionIdList),
                getCValueCrawlingSessionId(), "CRAWLING_SESSION_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @param crawlingSessionIdList The collection of crawlingSessionId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawlingSessionId_NotInScope(
            final Collection<Long> crawlingSessionIdList) {
        doSetCrawlingSessionId_NotInScope(crawlingSessionIdList);
    }

    protected void doSetCrawlingSessionId_NotInScope(
            final Collection<Long> crawlingSessionIdList) {
        regINS(CK_NINS, cTL(crawlingSessionIdList),
                getCValueCrawlingSessionId(), "CRAWLING_SESSION_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select CRAWLING_SESSION_ID from CRAWLING_SESSION where ...)} <br />
     * CRAWLING_SESSION by my CRAWLING_SESSION_ID, named 'crawlingSession'.
     * @param subQuery The sub-query of CrawlingSession for 'in-scope'. (NotNull)
     */
    public void inScopeCrawlingSession(
            final SubQuery<CrawlingSessionCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final CrawlingSessionCB cb = new CrawlingSessionCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepCrawlingSessionId_InScopeRelation_CrawlingSession(cb
                .query());
        registerInScopeRelation(cb.query(), "CRAWLING_SESSION_ID", "ID", pp,
                "crawlingSession");
    }

    public abstract String keepCrawlingSessionId_InScopeRelation_CrawlingSession(
            CrawlingSessionCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select CRAWLING_SESSION_ID from CRAWLING_SESSION where ...)} <br />
     * CRAWLING_SESSION by my CRAWLING_SESSION_ID, named 'crawlingSession'.
     * @param subQuery The sub-query of CrawlingSession for 'not in-scope'. (NotNull)
     */
    public void notInScopeCrawlingSession(
            final SubQuery<CrawlingSessionCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final CrawlingSessionCB cb = new CrawlingSessionCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepCrawlingSessionId_NotInScopeRelation_CrawlingSession(cb
                .query());
        registerNotInScopeRelation(cb.query(), "CRAWLING_SESSION_ID", "ID", pp,
                "crawlingSession");
    }

    public abstract String keepCrawlingSessionId_NotInScopeRelation_CrawlingSession(
            CrawlingSessionCQ sq);

    protected void regCrawlingSessionId(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueCrawlingSessionId(), "CRAWLING_SESSION_ID");
    }

    protected abstract ConditionValue getCValueCrawlingSessionId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param key The value of key as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_Equal(final String key) {
        doSetKey_Equal(fRES(key));
    }

    protected void doSetKey_Equal(final String key) {
        regKey(CK_EQ, key);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param key The value of key as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_NotEqual(final String key) {
        doSetKey_NotEqual(fRES(key));
    }

    protected void doSetKey_NotEqual(final String key) {
        regKey(CK_NES, key);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param key The value of key as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_GreaterThan(final String key) {
        regKey(CK_GT, fRES(key));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param key The value of key as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_LessThan(final String key) {
        regKey(CK_LT, fRES(key));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param key The value of key as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_GreaterEqual(final String key) {
        regKey(CK_GE, fRES(key));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param key The value of key as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_LessEqual(final String key) {
        regKey(CK_LE, fRES(key));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param keyList The collection of key as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_InScope(final Collection<String> keyList) {
        doSetKey_InScope(keyList);
    }

    public void doSetKey_InScope(final Collection<String> keyList) {
        regINS(CK_INS, cTL(keyList), getCValueKey(), "KEY");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param keyList The collection of key as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_NotInScope(final Collection<String> keyList) {
        doSetKey_NotInScope(keyList);
    }

    public void doSetKey_NotInScope(final Collection<String> keyList) {
        regINS(CK_NINS, cTL(keyList), getCValueKey(), "KEY");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param key The value of key as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setKey_PrefixSearch(final String key) {
        setKey_LikeSearch(key, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)} <br />
     * <pre>e.g. setKey_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param key The value of key as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setKey_LikeSearch(final String key,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(key), getCValueKey(), "KEY", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @param key The value of key as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setKey_NotLikeSearch(final String key,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(key), getCValueKey(), "KEY", likeSearchOption);
    }

    protected void regKey(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueKey(), "KEY");
    }

    protected abstract ConditionValue getCValueKey();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param value The value of value as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_Equal(final String value) {
        doSetValue_Equal(fRES(value));
    }

    protected void doSetValue_Equal(final String value) {
        regValue(CK_EQ, value);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param value The value of value as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_NotEqual(final String value) {
        doSetValue_NotEqual(fRES(value));
    }

    protected void doSetValue_NotEqual(final String value) {
        regValue(CK_NES, value);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param value The value of value as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_GreaterThan(final String value) {
        regValue(CK_GT, fRES(value));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param value The value of value as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_LessThan(final String value) {
        regValue(CK_LT, fRES(value));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param value The value of value as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_GreaterEqual(final String value) {
        regValue(CK_GE, fRES(value));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param value The value of value as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_LessEqual(final String value) {
        regValue(CK_LE, fRES(value));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param valueList The collection of value as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_InScope(final Collection<String> valueList) {
        doSetValue_InScope(valueList);
    }

    public void doSetValue_InScope(final Collection<String> valueList) {
        regINS(CK_INS, cTL(valueList), getCValueValue(), "VALUE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param valueList The collection of value as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_NotInScope(final Collection<String> valueList) {
        doSetValue_NotInScope(valueList);
    }

    public void doSetValue_NotInScope(final Collection<String> valueList) {
        regINS(CK_NINS, cTL(valueList), getCValueValue(), "VALUE");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param value The value of value as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_PrefixSearch(final String value) {
        setValue_LikeSearch(value, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)} <br />
     * <pre>e.g. setValue_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param value The value of value as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setValue_LikeSearch(final String value,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(value), getCValueValue(), "VALUE", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @param value The value of value as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setValue_NotLikeSearch(final String value,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(value), getCValueValue(), "VALUE", likeSearchOption);
    }

    protected void regValue(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueValue(), "VALUE");
    }

    protected abstract ConditionValue getCValueValue();

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

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_Equal()</span>.max(new SubQuery&lt;CrawlingSessionInfoCB&gt;() {
     *     public void query(CrawlingSessionInfoCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<CrawlingSessionInfoCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ, CrawlingSessionInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;CrawlingSessionInfoCB&gt;() {
     *     public void query(CrawlingSessionInfoCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<CrawlingSessionInfoCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES, CrawlingSessionInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;CrawlingSessionInfoCB&gt;() {
     *     public void query(CrawlingSessionInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<CrawlingSessionInfoCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT, CrawlingSessionInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessThan()</span>.max(new SubQuery&lt;CrawlingSessionInfoCB&gt;() {
     *     public void query(CrawlingSessionInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<CrawlingSessionInfoCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT, CrawlingSessionInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;CrawlingSessionInfoCB&gt;() {
     *     public void query(CrawlingSessionInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<CrawlingSessionInfoCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE, CrawlingSessionInfoCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;CrawlingSessionInfoCB&gt;() {
     *     public void query(CrawlingSessionInfoCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<CrawlingSessionInfoCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE, CrawlingSessionInfoCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(final String fn,
            final SubQuery<CB> sq, final String rd, final HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        final CrawlingSessionInfoCB cb = xcreateScalarConditionCB();
        sq.query((CB) cb);
        final String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB) xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }

    public abstract String keepScalarCondition(CrawlingSessionInfoCQ sq);

    protected CrawlingSessionInfoCB xcreateScalarConditionCB() {
        final CrawlingSessionInfoCB cb = newMyCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected CrawlingSessionInfoCB xcreateScalarConditionPartitionByCB() {
        final CrawlingSessionInfoCB cb = newMyCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String fn,
            final SubQuery<CrawlingSessionInfoCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final CrawlingSessionInfoCB cb = new CrawlingSessionInfoCB();
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

    public abstract String keepSpecifyMyselfDerived(CrawlingSessionInfoCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<CrawlingSessionInfoCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(CrawlingSessionInfoCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(
            final String fn, final SubQuery<CB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final CrawlingSessionInfoCB cb = new CrawlingSessionInfoCB();
        cb.xsetupForDerivedReferrer(this);
        sq.query((CB) cb);
        final String pk = "ID";
        final String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp,
                "myselfDerived", rd, vl, prpp, op);
    }

    public abstract String keepQueryMyselfDerived(CrawlingSessionInfoCQ sq);

    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfExists(final SubQuery<CrawlingSessionInfoCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final CrawlingSessionInfoCB cb = new CrawlingSessionInfoCB();
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

    public abstract String keepMyselfExists(CrawlingSessionInfoCQ sq);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfInScope(final SubQuery<CrawlingSessionInfoCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final CrawlingSessionInfoCB cb = new CrawlingSessionInfoCB();
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

    public abstract String keepMyselfInScope(CrawlingSessionInfoCQ sq);

    // ===================================================================================
    //                                                                        Manual Order
    //                                                                        ============
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
    //                                                                    Small Adjustment
    //                                                                    ================
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

    @Override
    protected void filterFromToOption(final FromToOption option) {
        option.allowOneSide();
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    protected CrawlingSessionInfoCB newMyCB() {
        return new CrawlingSessionInfoCB();
    }

    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabUDT() {
        return Date.class.getName();
    }

    protected String xabCQ() {
        return CrawlingSessionInfoCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
