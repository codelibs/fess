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
import jp.sf.fess.db.cbean.JobLogCB;
import jp.sf.fess.db.cbean.cq.JobLogCQ;

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
 * The abstract condition-query of JOB_LOG.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsJobLogCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsJobLogCQ(final ConditionQuery childQuery,
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
        return "JOB_LOG";
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
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobName The value of jobName as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_Equal(final String jobName) {
        doSetJobName_Equal(fRES(jobName));
    }

    protected void doSetJobName_Equal(final String jobName) {
        regJobName(CK_EQ, jobName);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobName The value of jobName as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_NotEqual(final String jobName) {
        doSetJobName_NotEqual(fRES(jobName));
    }

    protected void doSetJobName_NotEqual(final String jobName) {
        regJobName(CK_NES, jobName);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobName The value of jobName as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_GreaterThan(final String jobName) {
        regJobName(CK_GT, fRES(jobName));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobName The value of jobName as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_LessThan(final String jobName) {
        regJobName(CK_LT, fRES(jobName));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobName The value of jobName as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_GreaterEqual(final String jobName) {
        regJobName(CK_GE, fRES(jobName));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobName The value of jobName as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_LessEqual(final String jobName) {
        regJobName(CK_LE, fRES(jobName));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobNameList The collection of jobName as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_InScope(final Collection<String> jobNameList) {
        doSetJobName_InScope(jobNameList);
    }

    public void doSetJobName_InScope(final Collection<String> jobNameList) {
        regINS(CK_INS, cTL(jobNameList), getCValueJobName(), "JOB_NAME");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobNameList The collection of jobName as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_NotInScope(final Collection<String> jobNameList) {
        doSetJobName_NotInScope(jobNameList);
    }

    public void doSetJobName_NotInScope(final Collection<String> jobNameList) {
        regINS(CK_NINS, cTL(jobNameList), getCValueJobName(), "JOB_NAME");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobName The value of jobName as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobName_PrefixSearch(final String jobName) {
        setJobName_LikeSearch(jobName, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)} <br />
     * <pre>e.g. setJobName_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param jobName The value of jobName as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setJobName_LikeSearch(final String jobName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(jobName), getCValueJobName(), "JOB_NAME",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @param jobName The value of jobName as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setJobName_NotLikeSearch(final String jobName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(jobName), getCValueJobName(), "JOB_NAME",
                likeSearchOption);
    }

    protected void regJobName(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueJobName(), "JOB_NAME");
    }

    abstract protected ConditionValue getCValueJobName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatus The value of jobStatus as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_Equal(final String jobStatus) {
        doSetJobStatus_Equal(fRES(jobStatus));
    }

    protected void doSetJobStatus_Equal(final String jobStatus) {
        regJobStatus(CK_EQ, jobStatus);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatus The value of jobStatus as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_NotEqual(final String jobStatus) {
        doSetJobStatus_NotEqual(fRES(jobStatus));
    }

    protected void doSetJobStatus_NotEqual(final String jobStatus) {
        regJobStatus(CK_NES, jobStatus);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatus The value of jobStatus as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_GreaterThan(final String jobStatus) {
        regJobStatus(CK_GT, fRES(jobStatus));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatus The value of jobStatus as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_LessThan(final String jobStatus) {
        regJobStatus(CK_LT, fRES(jobStatus));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatus The value of jobStatus as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_GreaterEqual(final String jobStatus) {
        regJobStatus(CK_GE, fRES(jobStatus));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatus The value of jobStatus as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_LessEqual(final String jobStatus) {
        regJobStatus(CK_LE, fRES(jobStatus));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatusList The collection of jobStatus as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_InScope(final Collection<String> jobStatusList) {
        doSetJobStatus_InScope(jobStatusList);
    }

    public void doSetJobStatus_InScope(final Collection<String> jobStatusList) {
        regINS(CK_INS, cTL(jobStatusList), getCValueJobStatus(), "JOB_STATUS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatusList The collection of jobStatus as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_NotInScope(final Collection<String> jobStatusList) {
        doSetJobStatus_NotInScope(jobStatusList);
    }

    public void doSetJobStatus_NotInScope(final Collection<String> jobStatusList) {
        regINS(CK_NINS, cTL(jobStatusList), getCValueJobStatus(), "JOB_STATUS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatus The value of jobStatus as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobStatus_PrefixSearch(final String jobStatus) {
        setJobStatus_LikeSearch(jobStatus, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)} <br />
     * <pre>e.g. setJobStatus_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param jobStatus The value of jobStatus as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setJobStatus_LikeSearch(final String jobStatus,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(jobStatus), getCValueJobStatus(), "JOB_STATUS",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @param jobStatus The value of jobStatus as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setJobStatus_NotLikeSearch(final String jobStatus,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(jobStatus), getCValueJobStatus(), "JOB_STATUS",
                likeSearchOption);
    }

    protected void regJobStatus(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueJobStatus(), "JOB_STATUS");
    }

    abstract protected ConditionValue getCValueJobStatus();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param target The value of target as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_Equal(final String target) {
        doSetTarget_Equal(fRES(target));
    }

    protected void doSetTarget_Equal(final String target) {
        regTarget(CK_EQ, target);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param target The value of target as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_NotEqual(final String target) {
        doSetTarget_NotEqual(fRES(target));
    }

    protected void doSetTarget_NotEqual(final String target) {
        regTarget(CK_NES, target);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param target The value of target as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_GreaterThan(final String target) {
        regTarget(CK_GT, fRES(target));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param target The value of target as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_LessThan(final String target) {
        regTarget(CK_LT, fRES(target));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param target The value of target as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_GreaterEqual(final String target) {
        regTarget(CK_GE, fRES(target));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param target The value of target as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_LessEqual(final String target) {
        regTarget(CK_LE, fRES(target));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param targetList The collection of target as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_InScope(final Collection<String> targetList) {
        doSetTarget_InScope(targetList);
    }

    public void doSetTarget_InScope(final Collection<String> targetList) {
        regINS(CK_INS, cTL(targetList), getCValueTarget(), "TARGET");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param targetList The collection of target as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_NotInScope(final Collection<String> targetList) {
        doSetTarget_NotInScope(targetList);
    }

    public void doSetTarget_NotInScope(final Collection<String> targetList) {
        regINS(CK_NINS, cTL(targetList), getCValueTarget(), "TARGET");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param target The value of target as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setTarget_PrefixSearch(final String target) {
        setTarget_LikeSearch(target, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)} <br />
     * <pre>e.g. setTarget_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param target The value of target as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setTarget_LikeSearch(final String target,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(target), getCValueTarget(), "TARGET",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @param target The value of target as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setTarget_NotLikeSearch(final String target,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(target), getCValueTarget(), "TARGET",
                likeSearchOption);
    }

    protected void regTarget(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueTarget(), "TARGET");
    }

    abstract protected ConditionValue getCValueTarget();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptType The value of scriptType as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_Equal(final String scriptType) {
        doSetScriptType_Equal(fRES(scriptType));
    }

    protected void doSetScriptType_Equal(final String scriptType) {
        regScriptType(CK_EQ, scriptType);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptType The value of scriptType as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_NotEqual(final String scriptType) {
        doSetScriptType_NotEqual(fRES(scriptType));
    }

    protected void doSetScriptType_NotEqual(final String scriptType) {
        regScriptType(CK_NES, scriptType);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptType The value of scriptType as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_GreaterThan(final String scriptType) {
        regScriptType(CK_GT, fRES(scriptType));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptType The value of scriptType as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_LessThan(final String scriptType) {
        regScriptType(CK_LT, fRES(scriptType));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptType The value of scriptType as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_GreaterEqual(final String scriptType) {
        regScriptType(CK_GE, fRES(scriptType));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptType The value of scriptType as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_LessEqual(final String scriptType) {
        regScriptType(CK_LE, fRES(scriptType));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptTypeList The collection of scriptType as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_InScope(final Collection<String> scriptTypeList) {
        doSetScriptType_InScope(scriptTypeList);
    }

    public void doSetScriptType_InScope(final Collection<String> scriptTypeList) {
        regINS(CK_INS, cTL(scriptTypeList), getCValueScriptType(),
                "SCRIPT_TYPE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptTypeList The collection of scriptType as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_NotInScope(final Collection<String> scriptTypeList) {
        doSetScriptType_NotInScope(scriptTypeList);
    }

    public void doSetScriptType_NotInScope(
            final Collection<String> scriptTypeList) {
        regINS(CK_NINS, cTL(scriptTypeList), getCValueScriptType(),
                "SCRIPT_TYPE");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptType The value of scriptType as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptType_PrefixSearch(final String scriptType) {
        setScriptType_LikeSearch(scriptType, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)} <br />
     * <pre>e.g. setScriptType_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param scriptType The value of scriptType as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setScriptType_LikeSearch(final String scriptType,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(scriptType), getCValueScriptType(), "SCRIPT_TYPE",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @param scriptType The value of scriptType as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setScriptType_NotLikeSearch(final String scriptType,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(scriptType), getCValueScriptType(), "SCRIPT_TYPE",
                likeSearchOption);
    }

    protected void regScriptType(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueScriptType(), "SCRIPT_TYPE");
    }

    abstract protected ConditionValue getCValueScriptType();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptData The value of scriptData as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_Equal(final String scriptData) {
        doSetScriptData_Equal(fRES(scriptData));
    }

    protected void doSetScriptData_Equal(final String scriptData) {
        regScriptData(CK_EQ, scriptData);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptData The value of scriptData as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_NotEqual(final String scriptData) {
        doSetScriptData_NotEqual(fRES(scriptData));
    }

    protected void doSetScriptData_NotEqual(final String scriptData) {
        regScriptData(CK_NES, scriptData);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptData The value of scriptData as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_GreaterThan(final String scriptData) {
        regScriptData(CK_GT, fRES(scriptData));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptData The value of scriptData as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_LessThan(final String scriptData) {
        regScriptData(CK_LT, fRES(scriptData));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptData The value of scriptData as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_GreaterEqual(final String scriptData) {
        regScriptData(CK_GE, fRES(scriptData));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptData The value of scriptData as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_LessEqual(final String scriptData) {
        regScriptData(CK_LE, fRES(scriptData));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptDataList The collection of scriptData as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_InScope(final Collection<String> scriptDataList) {
        doSetScriptData_InScope(scriptDataList);
    }

    public void doSetScriptData_InScope(final Collection<String> scriptDataList) {
        regINS(CK_INS, cTL(scriptDataList), getCValueScriptData(),
                "SCRIPT_DATA");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptDataList The collection of scriptData as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_NotInScope(final Collection<String> scriptDataList) {
        doSetScriptData_NotInScope(scriptDataList);
    }

    public void doSetScriptData_NotInScope(
            final Collection<String> scriptDataList) {
        regINS(CK_NINS, cTL(scriptDataList), getCValueScriptData(),
                "SCRIPT_DATA");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptData The value of scriptData as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptData_PrefixSearch(final String scriptData) {
        setScriptData_LikeSearch(scriptData, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)} <br />
     * <pre>e.g. setScriptData_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param scriptData The value of scriptData as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setScriptData_LikeSearch(final String scriptData,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(scriptData), getCValueScriptData(), "SCRIPT_DATA",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @param scriptData The value of scriptData as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setScriptData_NotLikeSearch(final String scriptData,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(scriptData), getCValueScriptData(), "SCRIPT_DATA",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     */
    public void setScriptData_IsNull() {
        regScriptData(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     */
    public void setScriptData_IsNullOrEmpty() {
        regScriptData(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     */
    public void setScriptData_IsNotNull() {
        regScriptData(CK_ISNN, DOBJ);
    }

    protected void regScriptData(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueScriptData(), "SCRIPT_DATA");
    }

    abstract protected ConditionValue getCValueScriptData();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResult The value of scriptResult as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_Equal(final String scriptResult) {
        doSetScriptResult_Equal(fRES(scriptResult));
    }

    protected void doSetScriptResult_Equal(final String scriptResult) {
        regScriptResult(CK_EQ, scriptResult);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResult The value of scriptResult as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_NotEqual(final String scriptResult) {
        doSetScriptResult_NotEqual(fRES(scriptResult));
    }

    protected void doSetScriptResult_NotEqual(final String scriptResult) {
        regScriptResult(CK_NES, scriptResult);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResult The value of scriptResult as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_GreaterThan(final String scriptResult) {
        regScriptResult(CK_GT, fRES(scriptResult));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResult The value of scriptResult as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_LessThan(final String scriptResult) {
        regScriptResult(CK_LT, fRES(scriptResult));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResult The value of scriptResult as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_GreaterEqual(final String scriptResult) {
        regScriptResult(CK_GE, fRES(scriptResult));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResult The value of scriptResult as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_LessEqual(final String scriptResult) {
        regScriptResult(CK_LE, fRES(scriptResult));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResultList The collection of scriptResult as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_InScope(
            final Collection<String> scriptResultList) {
        doSetScriptResult_InScope(scriptResultList);
    }

    public void doSetScriptResult_InScope(
            final Collection<String> scriptResultList) {
        regINS(CK_INS, cTL(scriptResultList), getCValueScriptResult(),
                "SCRIPT_RESULT");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResultList The collection of scriptResult as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_NotInScope(
            final Collection<String> scriptResultList) {
        doSetScriptResult_NotInScope(scriptResultList);
    }

    public void doSetScriptResult_NotInScope(
            final Collection<String> scriptResultList) {
        regINS(CK_NINS, cTL(scriptResultList), getCValueScriptResult(),
                "SCRIPT_RESULT");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResult The value of scriptResult as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setScriptResult_PrefixSearch(final String scriptResult) {
        setScriptResult_LikeSearch(scriptResult, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)} <br />
     * <pre>e.g. setScriptResult_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param scriptResult The value of scriptResult as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setScriptResult_LikeSearch(final String scriptResult,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(scriptResult), getCValueScriptResult(),
                "SCRIPT_RESULT", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @param scriptResult The value of scriptResult as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setScriptResult_NotLikeSearch(final String scriptResult,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(scriptResult), getCValueScriptResult(),
                "SCRIPT_RESULT", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     */
    public void setScriptResult_IsNull() {
        regScriptResult(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     */
    public void setScriptResult_IsNullOrEmpty() {
        regScriptResult(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     */
    public void setScriptResult_IsNotNull() {
        regScriptResult(CK_ISNN, DOBJ);
    }

    protected void regScriptResult(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueScriptResult(), "SCRIPT_RESULT");
    }

    abstract protected ConditionValue getCValueScriptResult();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param startTime The value of startTime as equal. (NullAllowed: if null, no condition)
     */
    public void setStartTime_Equal(final java.sql.Timestamp startTime) {
        regStartTime(CK_EQ, startTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param startTime The value of startTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setStartTime_GreaterThan(final java.sql.Timestamp startTime) {
        regStartTime(CK_GT, startTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param startTime The value of startTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setStartTime_LessThan(final java.sql.Timestamp startTime) {
        regStartTime(CK_LT, startTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param startTime The value of startTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setStartTime_GreaterEqual(final java.sql.Timestamp startTime) {
        regStartTime(CK_GE, startTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param startTime The value of startTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setStartTime_LessEqual(final java.sql.Timestamp startTime) {
        regStartTime(CK_LE, startTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * <pre>e.g. setStartTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of startTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of startTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setStartTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueStartTime(), "START_TIME",
                fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of startTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of startTime. (NullAllowed: if null, no to-condition)
     */
    public void setStartTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setStartTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    protected void regStartTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueStartTime(), "START_TIME");
    }

    abstract protected ConditionValue getCValueStartTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * @param endTime The value of endTime as equal. (NullAllowed: if null, no condition)
     */
    public void setEndTime_Equal(final java.sql.Timestamp endTime) {
        regEndTime(CK_EQ, endTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * @param endTime The value of endTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setEndTime_GreaterThan(final java.sql.Timestamp endTime) {
        regEndTime(CK_GT, endTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * @param endTime The value of endTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setEndTime_LessThan(final java.sql.Timestamp endTime) {
        regEndTime(CK_LT, endTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * @param endTime The value of endTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setEndTime_GreaterEqual(final java.sql.Timestamp endTime) {
        regEndTime(CK_GE, endTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * @param endTime The value of endTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setEndTime_LessEqual(final java.sql.Timestamp endTime) {
        regEndTime(CK_LE, endTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * <pre>e.g. setEndTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of endTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of endTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setEndTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueEndTime(), "END_TIME",
                fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of endTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of endTime. (NullAllowed: if null, no to-condition)
     */
    public void setEndTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setEndTime_FromTo(fromDate, toDate, new FromToOption().compareAsDate());
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     */
    public void setEndTime_IsNull() {
        regEndTime(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     */
    public void setEndTime_IsNotNull() {
        regEndTime(CK_ISNN, DOBJ);
    }

    protected void regEndTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueEndTime(), "END_TIME");
    }

    abstract protected ConditionValue getCValueEndTime();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;JobLogCB&gt;() {
     *     public void query(JobLogCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<JobLogCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;JobLogCB&gt;() {
     *     public void query(JobLogCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<JobLogCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;JobLogCB&gt;() {
     *     public void query(JobLogCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<JobLogCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;JobLogCB&gt;() {
     *     public void query(JobLogCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<JobLogCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;JobLogCB&gt;() {
     *     public void query(JobLogCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<JobLogCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;JobLogCB&gt;() {
     *     public void query(JobLogCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<JobLogCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<JobLogCB> xcreateSSQFunction(final String operand) {
        return new HpSSQFunction<JobLogCB>(new HpSSQSetupper<JobLogCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<JobLogCB> subQuery,
                    final HpSSQOption<JobLogCB> option) {
                xscalarCondition(function, subQuery, operand, option);
            }
        });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<JobLogCB> subQuery, final String operand,
            final HpSSQOption<JobLogCB> option) {
        assertObjectNotNull("subQuery<JobLogCB>", subQuery);
        final JobLogCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(JobLogCQ subQuery);

    protected JobLogCB xcreateScalarConditionCB() {
        final JobLogCB cb = new JobLogCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected JobLogCB xcreateScalarConditionPartitionByCB() {
        final JobLogCB cb = new JobLogCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<JobLogCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<JobLogCB>", subQuery);
        final JobLogCB cb = new JobLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(JobLogCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<JobLogCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<JobLogCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<JobLogCB>(new HpQDRSetupper<JobLogCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<JobLogCB> subQuery, final String operand,
                    final Object value, final DerivedReferrerOption option) {
                xqderiveMyselfDerived(function, subQuery, operand, value,
                        option);
            }
        });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<JobLogCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<JobLogCB>", subQuery);
        final JobLogCB cb = new JobLogCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(JobLogCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<JobLogCB> subQuery) {
        assertObjectNotNull("subQuery<JobLogCB>", subQuery);
        final JobLogCB cb = new JobLogCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(JobLogCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<JobLogCB> subQuery) {
        assertObjectNotNull("subQuery<JobLogCB>", subQuery);
        final JobLogCB cb = new JobLogCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(JobLogCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return JobLogCB.class.getName();
    }

    protected String xabCQ() {
        return JobLogCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
