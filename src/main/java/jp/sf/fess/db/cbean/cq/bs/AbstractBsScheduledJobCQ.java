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
import jp.sf.fess.db.cbean.ScheduledJobCB;
import jp.sf.fess.db.cbean.cq.ScheduledJobCQ;

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
 * The abstract condition-query of SCHEDULED_JOB.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsScheduledJobCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsScheduledJobCQ(final ConditionQuery childQuery,
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
        return "SCHEDULED_JOB";
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
     * NAME: {NotNull, VARCHAR(100)}
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
     * NAME: {NotNull, VARCHAR(100)}
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
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_GreaterThan(final String name) {
        regName(CK_GT, fRES(name));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_LessThan(final String name) {
        regName(CK_LT, fRES(name));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_GreaterEqual(final String name) {
        regName(CK_GE, fRES(name));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_LessEqual(final String name) {
        regName(CK_LE, fRES(name));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)}
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
     * NAME: {NotNull, VARCHAR(100)}
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
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_PrefixSearch(final String name) {
        setName_LikeSearch(name, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)} <br />
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
     * NAME: {NotNull, VARCHAR(100)}
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
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpression The value of cronExpression as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_Equal(final String cronExpression) {
        doSetCronExpression_Equal(fRES(cronExpression));
    }

    protected void doSetCronExpression_Equal(final String cronExpression) {
        regCronExpression(CK_EQ, cronExpression);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpression The value of cronExpression as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_NotEqual(final String cronExpression) {
        doSetCronExpression_NotEqual(fRES(cronExpression));
    }

    protected void doSetCronExpression_NotEqual(final String cronExpression) {
        regCronExpression(CK_NES, cronExpression);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpression The value of cronExpression as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_GreaterThan(final String cronExpression) {
        regCronExpression(CK_GT, fRES(cronExpression));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpression The value of cronExpression as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_LessThan(final String cronExpression) {
        regCronExpression(CK_LT, fRES(cronExpression));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpression The value of cronExpression as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_GreaterEqual(final String cronExpression) {
        regCronExpression(CK_GE, fRES(cronExpression));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpression The value of cronExpression as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_LessEqual(final String cronExpression) {
        regCronExpression(CK_LE, fRES(cronExpression));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpressionList The collection of cronExpression as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_InScope(
            final Collection<String> cronExpressionList) {
        doSetCronExpression_InScope(cronExpressionList);
    }

    public void doSetCronExpression_InScope(
            final Collection<String> cronExpressionList) {
        regINS(CK_INS, cTL(cronExpressionList), getCValueCronExpression(),
                "CRON_EXPRESSION");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpressionList The collection of cronExpression as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_NotInScope(
            final Collection<String> cronExpressionList) {
        doSetCronExpression_NotInScope(cronExpressionList);
    }

    public void doSetCronExpression_NotInScope(
            final Collection<String> cronExpressionList) {
        regINS(CK_NINS, cTL(cronExpressionList), getCValueCronExpression(),
                "CRON_EXPRESSION");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpression The value of cronExpression as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setCronExpression_PrefixSearch(final String cronExpression) {
        setCronExpression_LikeSearch(cronExpression, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)} <br />
     * <pre>e.g. setCronExpression_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param cronExpression The value of cronExpression as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setCronExpression_LikeSearch(final String cronExpression,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(cronExpression), getCValueCronExpression(),
                "CRON_EXPRESSION", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @param cronExpression The value of cronExpression as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setCronExpression_NotLikeSearch(final String cronExpression,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(cronExpression), getCValueCronExpression(),
                "CRON_EXPRESSION", likeSearchOption);
    }

    protected void regCronExpression(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueCronExpression(), "CRON_EXPRESSION");
    }

    abstract protected ConditionValue getCValueCronExpression();

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
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawler The value of crawler as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_Equal(final String crawler) {
        doSetCrawler_Equal(fRES(crawler));
    }

    protected void doSetCrawler_Equal(final String crawler) {
        regCrawler(CK_EQ, crawler);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawler The value of crawler as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_NotEqual(final String crawler) {
        doSetCrawler_NotEqual(fRES(crawler));
    }

    protected void doSetCrawler_NotEqual(final String crawler) {
        regCrawler(CK_NES, crawler);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawler The value of crawler as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_GreaterThan(final String crawler) {
        regCrawler(CK_GT, fRES(crawler));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawler The value of crawler as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_LessThan(final String crawler) {
        regCrawler(CK_LT, fRES(crawler));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawler The value of crawler as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_GreaterEqual(final String crawler) {
        regCrawler(CK_GE, fRES(crawler));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawler The value of crawler as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_LessEqual(final String crawler) {
        regCrawler(CK_LE, fRES(crawler));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawlerList The collection of crawler as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_InScope(final Collection<String> crawlerList) {
        doSetCrawler_InScope(crawlerList);
    }

    public void doSetCrawler_InScope(final Collection<String> crawlerList) {
        regINS(CK_INS, cTL(crawlerList), getCValueCrawler(), "CRAWLER");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawlerList The collection of crawler as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_NotInScope(final Collection<String> crawlerList) {
        doSetCrawler_NotInScope(crawlerList);
    }

    public void doSetCrawler_NotInScope(final Collection<String> crawlerList) {
        regINS(CK_NINS, cTL(crawlerList), getCValueCrawler(), "CRAWLER");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawler The value of crawler as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setCrawler_PrefixSearch(final String crawler) {
        setCrawler_LikeSearch(crawler, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)} <br />
     * <pre>e.g. setCrawler_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param crawler The value of crawler as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setCrawler_LikeSearch(final String crawler,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(crawler), getCValueCrawler(), "CRAWLER",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @param crawler The value of crawler as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setCrawler_NotLikeSearch(final String crawler,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(crawler), getCValueCrawler(), "CRAWLER",
                likeSearchOption);
    }

    protected void regCrawler(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueCrawler(), "CRAWLER");
    }

    abstract protected ConditionValue getCValueCrawler();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLogging The value of jobLogging as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_Equal(final String jobLogging) {
        doSetJobLogging_Equal(fRES(jobLogging));
    }

    protected void doSetJobLogging_Equal(final String jobLogging) {
        regJobLogging(CK_EQ, jobLogging);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLogging The value of jobLogging as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_NotEqual(final String jobLogging) {
        doSetJobLogging_NotEqual(fRES(jobLogging));
    }

    protected void doSetJobLogging_NotEqual(final String jobLogging) {
        regJobLogging(CK_NES, jobLogging);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLogging The value of jobLogging as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_GreaterThan(final String jobLogging) {
        regJobLogging(CK_GT, fRES(jobLogging));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLogging The value of jobLogging as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_LessThan(final String jobLogging) {
        regJobLogging(CK_LT, fRES(jobLogging));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLogging The value of jobLogging as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_GreaterEqual(final String jobLogging) {
        regJobLogging(CK_GE, fRES(jobLogging));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLogging The value of jobLogging as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_LessEqual(final String jobLogging) {
        regJobLogging(CK_LE, fRES(jobLogging));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLoggingList The collection of jobLogging as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_InScope(final Collection<String> jobLoggingList) {
        doSetJobLogging_InScope(jobLoggingList);
    }

    public void doSetJobLogging_InScope(final Collection<String> jobLoggingList) {
        regINS(CK_INS, cTL(jobLoggingList), getCValueJobLogging(),
                "JOB_LOGGING");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLoggingList The collection of jobLogging as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_NotInScope(final Collection<String> jobLoggingList) {
        doSetJobLogging_NotInScope(jobLoggingList);
    }

    public void doSetJobLogging_NotInScope(
            final Collection<String> jobLoggingList) {
        regINS(CK_NINS, cTL(jobLoggingList), getCValueJobLogging(),
                "JOB_LOGGING");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLogging The value of jobLogging as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setJobLogging_PrefixSearch(final String jobLogging) {
        setJobLogging_LikeSearch(jobLogging, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)} <br />
     * <pre>e.g. setJobLogging_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param jobLogging The value of jobLogging as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setJobLogging_LikeSearch(final String jobLogging,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(jobLogging), getCValueJobLogging(), "JOB_LOGGING",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @param jobLogging The value of jobLogging as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setJobLogging_NotLikeSearch(final String jobLogging,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(jobLogging), getCValueJobLogging(), "JOB_LOGGING",
                likeSearchOption);
    }

    protected void regJobLogging(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueJobLogging(), "JOB_LOGGING");
    }

    abstract protected ConditionValue getCValueJobLogging();

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
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;ScheduledJobCB&gt;() {
     *     public void query(ScheduledJobCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<ScheduledJobCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;ScheduledJobCB&gt;() {
     *     public void query(ScheduledJobCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<ScheduledJobCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;ScheduledJobCB&gt;() {
     *     public void query(ScheduledJobCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<ScheduledJobCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;ScheduledJobCB&gt;() {
     *     public void query(ScheduledJobCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<ScheduledJobCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;ScheduledJobCB&gt;() {
     *     public void query(ScheduledJobCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<ScheduledJobCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;ScheduledJobCB&gt;() {
     *     public void query(ScheduledJobCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<ScheduledJobCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<ScheduledJobCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<ScheduledJobCB>(
                new HpSSQSetupper<ScheduledJobCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<ScheduledJobCB> subQuery,
                            final HpSSQOption<ScheduledJobCB> option) {
                        xscalarCondition(function, subQuery, operand, option);
                    }
                });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<ScheduledJobCB> subQuery, final String operand,
            final HpSSQOption<ScheduledJobCB> option) {
        assertObjectNotNull("subQuery<ScheduledJobCB>", subQuery);
        final ScheduledJobCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(ScheduledJobCQ subQuery);

    protected ScheduledJobCB xcreateScalarConditionCB() {
        final ScheduledJobCB cb = new ScheduledJobCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected ScheduledJobCB xcreateScalarConditionPartitionByCB() {
        final ScheduledJobCB cb = new ScheduledJobCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<ScheduledJobCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<ScheduledJobCB>", subQuery);
        final ScheduledJobCB cb = new ScheduledJobCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(ScheduledJobCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<ScheduledJobCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<ScheduledJobCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<ScheduledJobCB>(
                new HpQDRSetupper<ScheduledJobCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<ScheduledJobCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveMyselfDerived(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<ScheduledJobCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<ScheduledJobCB>", subQuery);
        final ScheduledJobCB cb = new ScheduledJobCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(ScheduledJobCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<ScheduledJobCB> subQuery) {
        assertObjectNotNull("subQuery<ScheduledJobCB>", subQuery);
        final ScheduledJobCB cb = new ScheduledJobCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(ScheduledJobCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<ScheduledJobCB> subQuery) {
        assertObjectNotNull("subQuery<ScheduledJobCB>", subQuery);
        final ScheduledJobCB cb = new ScheduledJobCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(ScheduledJobCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return ScheduledJobCB.class.getName();
    }

    protected String xabCQ() {
        return ScheduledJobCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
