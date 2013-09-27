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
import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.FailureUrlCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;
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
 * The abstract condition-query of FAILURE_URL.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsFailureUrlCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsFailureUrlCQ(final ConditionQuery childQuery,
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
        return "FAILURE_URL";
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

    protected void regId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueId(), "ID");
    }

    abstract protected ConditionValue getCValueId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param url The value of url as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_Equal(final String url) {
        doSetUrl_Equal(fRES(url));
    }

    protected void doSetUrl_Equal(final String url) {
        regUrl(CK_EQ, url);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param url The value of url as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_NotEqual(final String url) {
        doSetUrl_NotEqual(fRES(url));
    }

    protected void doSetUrl_NotEqual(final String url) {
        regUrl(CK_NES, url);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param url The value of url as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_GreaterThan(final String url) {
        regUrl(CK_GT, fRES(url));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param url The value of url as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_LessThan(final String url) {
        regUrl(CK_LT, fRES(url));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param url The value of url as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_GreaterEqual(final String url) {
        regUrl(CK_GE, fRES(url));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param url The value of url as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_LessEqual(final String url) {
        regUrl(CK_LE, fRES(url));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param urlList The collection of url as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_InScope(final Collection<String> urlList) {
        doSetUrl_InScope(urlList);
    }

    public void doSetUrl_InScope(final Collection<String> urlList) {
        regINS(CK_INS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param urlList The collection of url as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_NotInScope(final Collection<String> urlList) {
        doSetUrl_NotInScope(urlList);
    }

    public void doSetUrl_NotInScope(final Collection<String> urlList) {
        regINS(CK_NINS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param url The value of url as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_PrefixSearch(final String url) {
        setUrl_LikeSearch(url, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)} <br />
     * <pre>e.g. setUrl_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param url The value of url as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUrl_LikeSearch(final String url,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @param url The value of url as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUrl_NotLikeSearch(final String url,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    protected void regUrl(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUrl(), "URL");
    }

    abstract protected ConditionValue getCValueUrl();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadName The value of threadName as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_Equal(final String threadName) {
        doSetThreadName_Equal(fRES(threadName));
    }

    protected void doSetThreadName_Equal(final String threadName) {
        regThreadName(CK_EQ, threadName);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadName The value of threadName as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_NotEqual(final String threadName) {
        doSetThreadName_NotEqual(fRES(threadName));
    }

    protected void doSetThreadName_NotEqual(final String threadName) {
        regThreadName(CK_NES, threadName);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadName The value of threadName as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_GreaterThan(final String threadName) {
        regThreadName(CK_GT, fRES(threadName));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadName The value of threadName as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_LessThan(final String threadName) {
        regThreadName(CK_LT, fRES(threadName));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadName The value of threadName as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_GreaterEqual(final String threadName) {
        regThreadName(CK_GE, fRES(threadName));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadName The value of threadName as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_LessEqual(final String threadName) {
        regThreadName(CK_LE, fRES(threadName));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadNameList The collection of threadName as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_InScope(final Collection<String> threadNameList) {
        doSetThreadName_InScope(threadNameList);
    }

    public void doSetThreadName_InScope(final Collection<String> threadNameList) {
        regINS(CK_INS, cTL(threadNameList), getCValueThreadName(),
                "THREAD_NAME");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadNameList The collection of threadName as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_NotInScope(final Collection<String> threadNameList) {
        doSetThreadName_NotInScope(threadNameList);
    }

    public void doSetThreadName_NotInScope(
            final Collection<String> threadNameList) {
        regINS(CK_NINS, cTL(threadNameList), getCValueThreadName(),
                "THREAD_NAME");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadName The value of threadName as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setThreadName_PrefixSearch(final String threadName) {
        setThreadName_LikeSearch(threadName, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)} <br />
     * <pre>e.g. setThreadName_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param threadName The value of threadName as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setThreadName_LikeSearch(final String threadName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(threadName), getCValueThreadName(), "THREAD_NAME",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @param threadName The value of threadName as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setThreadName_NotLikeSearch(final String threadName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(threadName), getCValueThreadName(), "THREAD_NAME",
                likeSearchOption);
    }

    protected void regThreadName(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueThreadName(), "THREAD_NAME");
    }

    abstract protected ConditionValue getCValueThreadName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorName The value of errorName as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_Equal(final String errorName) {
        doSetErrorName_Equal(fRES(errorName));
    }

    protected void doSetErrorName_Equal(final String errorName) {
        regErrorName(CK_EQ, errorName);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorName The value of errorName as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_NotEqual(final String errorName) {
        doSetErrorName_NotEqual(fRES(errorName));
    }

    protected void doSetErrorName_NotEqual(final String errorName) {
        regErrorName(CK_NES, errorName);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorName The value of errorName as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_GreaterThan(final String errorName) {
        regErrorName(CK_GT, fRES(errorName));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorName The value of errorName as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_LessThan(final String errorName) {
        regErrorName(CK_LT, fRES(errorName));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorName The value of errorName as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_GreaterEqual(final String errorName) {
        regErrorName(CK_GE, fRES(errorName));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorName The value of errorName as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_LessEqual(final String errorName) {
        regErrorName(CK_LE, fRES(errorName));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorNameList The collection of errorName as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_InScope(final Collection<String> errorNameList) {
        doSetErrorName_InScope(errorNameList);
    }

    public void doSetErrorName_InScope(final Collection<String> errorNameList) {
        regINS(CK_INS, cTL(errorNameList), getCValueErrorName(), "ERROR_NAME");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorNameList The collection of errorName as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_NotInScope(final Collection<String> errorNameList) {
        doSetErrorName_NotInScope(errorNameList);
    }

    public void doSetErrorName_NotInScope(final Collection<String> errorNameList) {
        regINS(CK_NINS, cTL(errorNameList), getCValueErrorName(), "ERROR_NAME");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorName The value of errorName as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorName_PrefixSearch(final String errorName) {
        setErrorName_LikeSearch(errorName, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)} <br />
     * <pre>e.g. setErrorName_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param errorName The value of errorName as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setErrorName_LikeSearch(final String errorName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(errorName), getCValueErrorName(), "ERROR_NAME",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @param errorName The value of errorName as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setErrorName_NotLikeSearch(final String errorName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(errorName), getCValueErrorName(), "ERROR_NAME",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     */
    public void setErrorName_IsNull() {
        regErrorName(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     */
    public void setErrorName_IsNullOrEmpty() {
        regErrorName(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
     */
    public void setErrorName_IsNotNull() {
        regErrorName(CK_ISNN, DOBJ);
    }

    protected void regErrorName(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueErrorName(), "ERROR_NAME");
    }

    abstract protected ConditionValue getCValueErrorName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLog The value of errorLog as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_Equal(final String errorLog) {
        doSetErrorLog_Equal(fRES(errorLog));
    }

    protected void doSetErrorLog_Equal(final String errorLog) {
        regErrorLog(CK_EQ, errorLog);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLog The value of errorLog as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_NotEqual(final String errorLog) {
        doSetErrorLog_NotEqual(fRES(errorLog));
    }

    protected void doSetErrorLog_NotEqual(final String errorLog) {
        regErrorLog(CK_NES, errorLog);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLog The value of errorLog as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_GreaterThan(final String errorLog) {
        regErrorLog(CK_GT, fRES(errorLog));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLog The value of errorLog as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_LessThan(final String errorLog) {
        regErrorLog(CK_LT, fRES(errorLog));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLog The value of errorLog as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_GreaterEqual(final String errorLog) {
        regErrorLog(CK_GE, fRES(errorLog));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLog The value of errorLog as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_LessEqual(final String errorLog) {
        regErrorLog(CK_LE, fRES(errorLog));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLogList The collection of errorLog as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_InScope(final Collection<String> errorLogList) {
        doSetErrorLog_InScope(errorLogList);
    }

    public void doSetErrorLog_InScope(final Collection<String> errorLogList) {
        regINS(CK_INS, cTL(errorLogList), getCValueErrorLog(), "ERROR_LOG");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLogList The collection of errorLog as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_NotInScope(final Collection<String> errorLogList) {
        doSetErrorLog_NotInScope(errorLogList);
    }

    public void doSetErrorLog_NotInScope(final Collection<String> errorLogList) {
        regINS(CK_NINS, cTL(errorLogList), getCValueErrorLog(), "ERROR_LOG");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLog The value of errorLog as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorLog_PrefixSearch(final String errorLog) {
        setErrorLog_LikeSearch(errorLog, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)} <br />
     * <pre>e.g. setErrorLog_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param errorLog The value of errorLog as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setErrorLog_LikeSearch(final String errorLog,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(errorLog), getCValueErrorLog(), "ERROR_LOG",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @param errorLog The value of errorLog as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setErrorLog_NotLikeSearch(final String errorLog,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(errorLog), getCValueErrorLog(), "ERROR_LOG",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     */
    public void setErrorLog_IsNull() {
        regErrorLog(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     */
    public void setErrorLog_IsNullOrEmpty() {
        regErrorLog(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     */
    public void setErrorLog_IsNotNull() {
        regErrorLog(CK_ISNN, DOBJ);
    }

    protected void regErrorLog(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueErrorLog(), "ERROR_LOG");
    }

    abstract protected ConditionValue getCValueErrorLog();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param errorCount The value of errorCount as equal. (NullAllowed: if null, no condition)
     */
    public void setErrorCount_Equal(final Integer errorCount) {
        doSetErrorCount_Equal(errorCount);
    }

    protected void doSetErrorCount_Equal(final Integer errorCount) {
        regErrorCount(CK_EQ, errorCount);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param errorCount The value of errorCount as notEqual. (NullAllowed: if null, no condition)
     */
    public void setErrorCount_NotEqual(final Integer errorCount) {
        doSetErrorCount_NotEqual(errorCount);
    }

    protected void doSetErrorCount_NotEqual(final Integer errorCount) {
        regErrorCount(CK_NES, errorCount);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param errorCount The value of errorCount as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setErrorCount_GreaterThan(final Integer errorCount) {
        regErrorCount(CK_GT, errorCount);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param errorCount The value of errorCount as lessThan. (NullAllowed: if null, no condition)
     */
    public void setErrorCount_LessThan(final Integer errorCount) {
        regErrorCount(CK_LT, errorCount);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param errorCount The value of errorCount as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setErrorCount_GreaterEqual(final Integer errorCount) {
        regErrorCount(CK_GE, errorCount);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param errorCount The value of errorCount as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setErrorCount_LessEqual(final Integer errorCount) {
        regErrorCount(CK_LE, errorCount);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param minNumber The min number of errorCount. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of errorCount. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setErrorCount_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueErrorCount(), "ERROR_COUNT",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param errorCountList The collection of errorCount as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorCount_InScope(final Collection<Integer> errorCountList) {
        doSetErrorCount_InScope(errorCountList);
    }

    protected void doSetErrorCount_InScope(
            final Collection<Integer> errorCountList) {
        regINS(CK_INS, cTL(errorCountList), getCValueErrorCount(),
                "ERROR_COUNT");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @param errorCountList The collection of errorCount as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setErrorCount_NotInScope(
            final Collection<Integer> errorCountList) {
        doSetErrorCount_NotInScope(errorCountList);
    }

    protected void doSetErrorCount_NotInScope(
            final Collection<Integer> errorCountList) {
        regINS(CK_NINS, cTL(errorCountList), getCValueErrorCount(),
                "ERROR_COUNT");
    }

    protected void regErrorCount(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueErrorCount(), "ERROR_COUNT");
    }

    abstract protected ConditionValue getCValueErrorCount();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @param lastAccessTime The value of lastAccessTime as equal. (NullAllowed: if null, no condition)
     */
    public void setLastAccessTime_Equal(final java.sql.Timestamp lastAccessTime) {
        regLastAccessTime(CK_EQ, lastAccessTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @param lastAccessTime The value of lastAccessTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setLastAccessTime_GreaterThan(
            final java.sql.Timestamp lastAccessTime) {
        regLastAccessTime(CK_GT, lastAccessTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @param lastAccessTime The value of lastAccessTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setLastAccessTime_LessThan(
            final java.sql.Timestamp lastAccessTime) {
        regLastAccessTime(CK_LT, lastAccessTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @param lastAccessTime The value of lastAccessTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setLastAccessTime_GreaterEqual(
            final java.sql.Timestamp lastAccessTime) {
        regLastAccessTime(CK_GE, lastAccessTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @param lastAccessTime The value of lastAccessTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setLastAccessTime_LessEqual(
            final java.sql.Timestamp lastAccessTime) {
        regLastAccessTime(CK_LE, lastAccessTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * <pre>e.g. setLastAccessTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of lastAccessTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of lastAccessTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setLastAccessTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueLastAccessTime(),
                "LAST_ACCESS_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of lastAccessTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of lastAccessTime. (NullAllowed: if null, no to-condition)
     */
    public void setLastAccessTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setLastAccessTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    protected void regLastAccessTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueLastAccessTime(), "LAST_ACCESS_TIME");
    }

    abstract protected ConditionValue getCValueLastAccessTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as equal. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_Equal(final Long webConfigId) {
        doSetWebConfigId_Equal(webConfigId);
    }

    protected void doSetWebConfigId_Equal(final Long webConfigId) {
        regWebConfigId(CK_EQ, webConfigId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_NotEqual(final Long webConfigId) {
        doSetWebConfigId_NotEqual(webConfigId);
    }

    protected void doSetWebConfigId_NotEqual(final Long webConfigId) {
        regWebConfigId(CK_NES, webConfigId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_GreaterThan(final Long webConfigId) {
        regWebConfigId(CK_GT, webConfigId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_LessThan(final Long webConfigId) {
        regWebConfigId(CK_LT, webConfigId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_GreaterEqual(final Long webConfigId) {
        regWebConfigId(CK_GE, webConfigId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_LessEqual(final Long webConfigId) {
        regWebConfigId(CK_LE, webConfigId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param minNumber The min number of webConfigId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of webConfigId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setWebConfigId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueWebConfigId(), "WEB_CONFIG_ID",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigIdList The collection of webConfigId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setWebConfigId_InScope(final Collection<Long> webConfigIdList) {
        doSetWebConfigId_InScope(webConfigIdList);
    }

    protected void doSetWebConfigId_InScope(
            final Collection<Long> webConfigIdList) {
        regINS(CK_INS, cTL(webConfigIdList), getCValueWebConfigId(),
                "WEB_CONFIG_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigIdList The collection of webConfigId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setWebConfigId_NotInScope(final Collection<Long> webConfigIdList) {
        doSetWebConfigId_NotInScope(webConfigIdList);
    }

    protected void doSetWebConfigId_NotInScope(
            final Collection<Long> webConfigIdList) {
        regINS(CK_NINS, cTL(webConfigIdList), getCValueWebConfigId(),
                "WEB_CONFIG_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select WEB_CONFIG_ID from WEB_CRAWLING_CONFIG where ...)} <br />
     * WEB_CRAWLING_CONFIG by my WEB_CONFIG_ID, named 'webCrawlingConfig'.
     * @param subQuery The sub-query of WebCrawlingConfig for 'in-scope'. (NotNull)
     */
    public void inScopeWebCrawlingConfig(
            final SubQuery<WebCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<WebCrawlingConfigCB>", subQuery);
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepWebConfigId_InScopeRelation_WebCrawlingConfig(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "WEB_CONFIG_ID", "ID",
                subQueryPropertyName, "webCrawlingConfig");
    }

    public abstract String keepWebConfigId_InScopeRelation_WebCrawlingConfig(
            WebCrawlingConfigCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select WEB_CONFIG_ID from WEB_CRAWLING_CONFIG where ...)} <br />
     * WEB_CRAWLING_CONFIG by my WEB_CONFIG_ID, named 'webCrawlingConfig'.
     * @param subQuery The sub-query of WebCrawlingConfig for 'not in-scope'. (NotNull)
     */
    public void notInScopeWebCrawlingConfig(
            final SubQuery<WebCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<WebCrawlingConfigCB>", subQuery);
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepWebConfigId_NotInScopeRelation_WebCrawlingConfig(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "WEB_CONFIG_ID", "ID",
                subQueryPropertyName, "webCrawlingConfig");
    }

    public abstract String keepWebConfigId_NotInScopeRelation_WebCrawlingConfig(
            WebCrawlingConfigCQ subQuery);

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     */
    public void setWebConfigId_IsNull() {
        regWebConfigId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     */
    public void setWebConfigId_IsNotNull() {
        regWebConfigId(CK_ISNN, DOBJ);
    }

    protected void regWebConfigId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueWebConfigId(), "WEB_CONFIG_ID");
    }

    abstract protected ConditionValue getCValueWebConfigId();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as equal. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_Equal(final Long fileConfigId) {
        doSetFileConfigId_Equal(fileConfigId);
    }

    protected void doSetFileConfigId_Equal(final Long fileConfigId) {
        regFileConfigId(CK_EQ, fileConfigId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_NotEqual(final Long fileConfigId) {
        doSetFileConfigId_NotEqual(fileConfigId);
    }

    protected void doSetFileConfigId_NotEqual(final Long fileConfigId) {
        regFileConfigId(CK_NES, fileConfigId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_GreaterThan(final Long fileConfigId) {
        regFileConfigId(CK_GT, fileConfigId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_LessThan(final Long fileConfigId) {
        regFileConfigId(CK_LT, fileConfigId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_GreaterEqual(final Long fileConfigId) {
        regFileConfigId(CK_GE, fileConfigId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_LessEqual(final Long fileConfigId) {
        regFileConfigId(CK_LE, fileConfigId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param minNumber The min number of fileConfigId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of fileConfigId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setFileConfigId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueFileConfigId(), "FILE_CONFIG_ID",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigIdList The collection of fileConfigId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setFileConfigId_InScope(final Collection<Long> fileConfigIdList) {
        doSetFileConfigId_InScope(fileConfigIdList);
    }

    protected void doSetFileConfigId_InScope(
            final Collection<Long> fileConfigIdList) {
        regINS(CK_INS, cTL(fileConfigIdList), getCValueFileConfigId(),
                "FILE_CONFIG_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigIdList The collection of fileConfigId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setFileConfigId_NotInScope(
            final Collection<Long> fileConfigIdList) {
        doSetFileConfigId_NotInScope(fileConfigIdList);
    }

    protected void doSetFileConfigId_NotInScope(
            final Collection<Long> fileConfigIdList) {
        regINS(CK_NINS, cTL(fileConfigIdList), getCValueFileConfigId(),
                "FILE_CONFIG_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select FILE_CONFIG_ID from FILE_CRAWLING_CONFIG where ...)} <br />
     * FILE_CRAWLING_CONFIG by my FILE_CONFIG_ID, named 'fileCrawlingConfig'.
     * @param subQuery The sub-query of FileCrawlingConfig for 'in-scope'. (NotNull)
     */
    public void inScopeFileCrawlingConfig(
            final SubQuery<FileCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<FileCrawlingConfigCB>", subQuery);
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepFileConfigId_InScopeRelation_FileCrawlingConfig(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "FILE_CONFIG_ID", "ID",
                subQueryPropertyName, "fileCrawlingConfig");
    }

    public abstract String keepFileConfigId_InScopeRelation_FileCrawlingConfig(
            FileCrawlingConfigCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select FILE_CONFIG_ID from FILE_CRAWLING_CONFIG where ...)} <br />
     * FILE_CRAWLING_CONFIG by my FILE_CONFIG_ID, named 'fileCrawlingConfig'.
     * @param subQuery The sub-query of FileCrawlingConfig for 'not in-scope'. (NotNull)
     */
    public void notInScopeFileCrawlingConfig(
            final SubQuery<FileCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<FileCrawlingConfigCB>", subQuery);
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "FILE_CONFIG_ID", "ID",
                subQueryPropertyName, "fileCrawlingConfig");
    }

    public abstract String keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(
            FileCrawlingConfigCQ subQuery);

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     */
    public void setFileConfigId_IsNull() {
        regFileConfigId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     */
    public void setFileConfigId_IsNotNull() {
        regFileConfigId(CK_ISNN, DOBJ);
    }

    protected void regFileConfigId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueFileConfigId(), "FILE_CONFIG_ID");
    }

    abstract protected ConditionValue getCValueFileConfigId();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(FailureUrlCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FailureUrlCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(FailureUrlCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FailureUrlCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(FailureUrlCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FailureUrlCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(FailureUrlCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FailureUrlCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(FailureUrlCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FailureUrlCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;FailureUrlCB&gt;() {
     *     public void query(FailureUrlCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FailureUrlCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<FailureUrlCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<FailureUrlCB>(
                new HpSSQSetupper<FailureUrlCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<FailureUrlCB> subQuery,
                            final HpSSQOption<FailureUrlCB> option) {
                        xscalarCondition(function, subQuery, operand, option);
                    }
                });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<FailureUrlCB> subQuery, final String operand,
            final HpSSQOption<FailureUrlCB> option) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(FailureUrlCQ subQuery);

    protected FailureUrlCB xcreateScalarConditionCB() {
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected FailureUrlCB xcreateScalarConditionPartitionByCB() {
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<FailureUrlCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(FailureUrlCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<FailureUrlCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<FailureUrlCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<FailureUrlCB>(
                new HpQDRSetupper<FailureUrlCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<FailureUrlCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveMyselfDerived(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<FailureUrlCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(FailureUrlCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<FailureUrlCB> subQuery) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(FailureUrlCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<FailureUrlCB> subQuery) {
        assertObjectNotNull("subQuery<FailureUrlCB>", subQuery);
        final FailureUrlCB cb = new FailureUrlCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(FailureUrlCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return FailureUrlCB.class.getName();
    }

    protected String xabCQ() {
        return FailureUrlCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
