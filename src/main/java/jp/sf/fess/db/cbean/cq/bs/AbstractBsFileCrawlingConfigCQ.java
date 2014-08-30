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
import jp.sf.fess.db.cbean.FileAuthenticationCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.FileAuthenticationCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;

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
 * The abstract condition-query of FILE_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsFileCrawlingConfigCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsFileCrawlingConfigCQ(final ConditionQuery referrerQuery,
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
        return "FILE_CRAWLING_CONFIG";
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
     * {exists (select FILE_CRAWLING_CONFIG_ID from FILE_AUTHENTICATION where ...)} <br />
     * FILE_AUTHENTICATION by FILE_CRAWLING_CONFIG_ID, named 'fileAuthenticationAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">existsFileAuthenticationList</span>(new SubQuery&lt;FileAuthenticationCB&gt;() {
     *     public void query(FileAuthenticationCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of FileAuthenticationList for 'exists'. (NotNull)
     */
    public void existsFileAuthenticationList(
            final SubQuery<FileAuthenticationCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileAuthenticationCB cb = new FileAuthenticationCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_ExistsReferrer_FileAuthenticationList(cb
                .query());
        registerExistsReferrer(cb.query(), "ID", "FILE_CRAWLING_CONFIG_ID", pp,
                "fileAuthenticationList");
    }

    public abstract String keepId_ExistsReferrer_FileAuthenticationList(
            FileAuthenticationCQ sq);

    /**
     * Set up ExistsReferrer (correlated sub-query). <br />
     * {exists (select FILE_CONFIG_ID from FILE_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">existsFileConfigToLabelTypeMappingList</span>(new SubQuery&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void query(FileConfigToLabelTypeMappingCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of FileConfigToLabelTypeMappingList for 'exists'. (NotNull)
     */
    public void existsFileConfigToLabelTypeMappingList(
            final SubQuery<FileConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_ExistsReferrer_FileConfigToLabelTypeMappingList(cb
                .query());
        registerExistsReferrer(cb.query(), "ID", "FILE_CONFIG_ID", pp,
                "fileConfigToLabelTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_FileConfigToLabelTypeMappingList(
            FileConfigToLabelTypeMappingCQ sq);

    /**
     * Set up ExistsReferrer (correlated sub-query). <br />
     * {exists (select FILE_CONFIG_ID from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">existsFileConfigToRoleTypeMappingList</span>(new SubQuery&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void query(FileConfigToRoleTypeMappingCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of FileConfigToRoleTypeMappingList for 'exists'. (NotNull)
     */
    public void existsFileConfigToRoleTypeMappingList(
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_ExistsReferrer_FileConfigToRoleTypeMappingList(cb
                .query());
        registerExistsReferrer(cb.query(), "ID", "FILE_CONFIG_ID", pp,
                "fileConfigToRoleTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ sq);

    /**
     * Set up NotExistsReferrer (correlated sub-query). <br />
     * {not exists (select FILE_CRAWLING_CONFIG_ID from FILE_AUTHENTICATION where ...)} <br />
     * FILE_AUTHENTICATION by FILE_CRAWLING_CONFIG_ID, named 'fileAuthenticationAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">notExistsFileAuthenticationList</span>(new SubQuery&lt;FileAuthenticationCB&gt;() {
     *     public void query(FileAuthenticationCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_FileAuthenticationList for 'not exists'. (NotNull)
     */
    public void notExistsFileAuthenticationList(
            final SubQuery<FileAuthenticationCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileAuthenticationCB cb = new FileAuthenticationCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotExistsReferrer_FileAuthenticationList(cb
                .query());
        registerNotExistsReferrer(cb.query(), "ID", "FILE_CRAWLING_CONFIG_ID",
                pp, "fileAuthenticationList");
    }

    public abstract String keepId_NotExistsReferrer_FileAuthenticationList(
            FileAuthenticationCQ sq);

    /**
     * Set up NotExistsReferrer (correlated sub-query). <br />
     * {not exists (select FILE_CONFIG_ID from FILE_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">notExistsFileConfigToLabelTypeMappingList</span>(new SubQuery&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void query(FileConfigToLabelTypeMappingCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_FileConfigToLabelTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsFileConfigToLabelTypeMappingList(
            final SubQuery<FileConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotExistsReferrer_FileConfigToLabelTypeMappingList(cb
                .query());
        registerNotExistsReferrer(cb.query(), "ID", "FILE_CONFIG_ID", pp,
                "fileConfigToLabelTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_FileConfigToLabelTypeMappingList(
            FileConfigToLabelTypeMappingCQ sq);

    /**
     * Set up NotExistsReferrer (correlated sub-query). <br />
     * {not exists (select FILE_CONFIG_ID from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">notExistsFileConfigToRoleTypeMappingList</span>(new SubQuery&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void query(FileConfigToRoleTypeMappingCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_FileConfigToRoleTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsFileConfigToRoleTypeMappingList(
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotExistsReferrer_FileConfigToRoleTypeMappingList(cb
                .query());
        registerNotExistsReferrer(cb.query(), "ID", "FILE_CONFIG_ID", pp,
                "fileConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ sq);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select FILE_CRAWLING_CONFIG_ID from FILE_AUTHENTICATION where ...)} <br />
     * FILE_AUTHENTICATION by FILE_CRAWLING_CONFIG_ID, named 'fileAuthenticationAsOne'.
     * @param subQuery The sub-query of FileAuthenticationList for 'in-scope'. (NotNull)
     */
    public void inScopeFileAuthenticationList(
            final SubQuery<FileAuthenticationCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileAuthenticationCB cb = new FileAuthenticationCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_InScopeRelation_FileAuthenticationList(cb
                .query());
        registerInScopeRelation(cb.query(), "ID", "FILE_CRAWLING_CONFIG_ID",
                pp, "fileAuthenticationList");
    }

    public abstract String keepId_InScopeRelation_FileAuthenticationList(
            FileAuthenticationCQ sq);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select FILE_CONFIG_ID from FILE_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToLabelTypeMappingAsOne'.
     * @param subQuery The sub-query of FileConfigToLabelTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeFileConfigToLabelTypeMappingList(
            final SubQuery<FileConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_InScopeRelation_FileConfigToLabelTypeMappingList(cb
                .query());
        registerInScopeRelation(cb.query(), "ID", "FILE_CONFIG_ID", pp,
                "fileConfigToLabelTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_FileConfigToLabelTypeMappingList(
            FileConfigToLabelTypeMappingCQ sq);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select FILE_CONFIG_ID from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of FileConfigToRoleTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeFileConfigToRoleTypeMappingList(
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_InScopeRelation_FileConfigToRoleTypeMappingList(cb
                .query());
        registerInScopeRelation(cb.query(), "ID", "FILE_CONFIG_ID", pp,
                "fileConfigToRoleTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select FILE_CRAWLING_CONFIG_ID from FILE_AUTHENTICATION where ...)} <br />
     * FILE_AUTHENTICATION by FILE_CRAWLING_CONFIG_ID, named 'fileAuthenticationAsOne'.
     * @param subQuery The sub-query of FileAuthenticationList for 'not in-scope'. (NotNull)
     */
    public void notInScopeFileAuthenticationList(
            final SubQuery<FileAuthenticationCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileAuthenticationCB cb = new FileAuthenticationCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotInScopeRelation_FileAuthenticationList(cb
                .query());
        registerNotInScopeRelation(cb.query(), "ID", "FILE_CRAWLING_CONFIG_ID",
                pp, "fileAuthenticationList");
    }

    public abstract String keepId_NotInScopeRelation_FileAuthenticationList(
            FileAuthenticationCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select FILE_CONFIG_ID from FILE_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToLabelTypeMappingAsOne'.
     * @param subQuery The sub-query of FileConfigToLabelTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeFileConfigToLabelTypeMappingList(
            final SubQuery<FileConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotInScopeRelation_FileConfigToLabelTypeMappingList(cb
                .query());
        registerNotInScopeRelation(cb.query(), "ID", "FILE_CONFIG_ID", pp,
                "fileConfigToLabelTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_FileConfigToLabelTypeMappingList(
            FileConfigToLabelTypeMappingCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select FILE_CONFIG_ID from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of FileConfigToRoleTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeFileConfigToRoleTypeMappingList(
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(cb
                .query());
        registerNotInScopeRelation(cb.query(), "ID", "FILE_CONFIG_ID", pp,
                "fileConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ sq);

    public void xsderiveFileAuthenticationList(final String fn,
            final SubQuery<FileAuthenticationCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FileAuthenticationCB cb = new FileAuthenticationCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_SpecifyDerivedReferrer_FileAuthenticationList(cb
                .query());
        registerSpecifyDerivedReferrer(fn, cb.query(), "ID",
                "FILE_CRAWLING_CONFIG_ID", pp, "fileAuthenticationList", al, op);
    }

    public abstract String keepId_SpecifyDerivedReferrer_FileAuthenticationList(
            FileAuthenticationCQ sq);

    public void xsderiveFileConfigToLabelTypeMappingList(final String fn,
            final SubQuery<FileConfigToLabelTypeMappingCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList(cb
                .query());
        registerSpecifyDerivedReferrer(fn, cb.query(), "ID", "FILE_CONFIG_ID",
                pp, "fileConfigToLabelTypeMappingList", al, op);
    }

    public abstract String keepId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList(
            FileConfigToLabelTypeMappingCQ sq);

    public void xsderiveFileConfigToRoleTypeMappingList(final String fn,
            final SubQuery<FileConfigToRoleTypeMappingCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList(cb
                .query());
        registerSpecifyDerivedReferrer(fn, cb.query(), "ID", "FILE_CONFIG_ID",
                pp, "fileConfigToRoleTypeMappingList", al, op);
    }

    public abstract String keepId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ sq);

    /**
     * Prepare for (Query)DerivedReferrer (correlated sub-query). <br />
     * {FOO &lt;= (select max(BAR) from FILE_AUTHENTICATION where ...)} <br />
     * FILE_AUTHENTICATION by FILE_CRAWLING_CONFIG_ID, named 'fileAuthenticationAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">derivedFileAuthenticationList()</span>.<span style="color: #DD4747">max</span>(new SubQuery&lt;FileAuthenticationCB&gt;() {
     *     public void query(FileAuthenticationCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #DD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<FileAuthenticationCB> derivedFileAuthenticationList() {
        return xcreateQDRFunctionFileAuthenticationList();
    }

    protected HpQDRFunction<FileAuthenticationCB> xcreateQDRFunctionFileAuthenticationList() {
        return new HpQDRFunction<FileAuthenticationCB>(
                new HpQDRSetupper<FileAuthenticationCB>() {
                    @Override
                    public void setup(final String fn,
                            final SubQuery<FileAuthenticationCB> sq,
                            final String rd, final Object vl,
                            final DerivedReferrerOption op) {
                        xqderiveFileAuthenticationList(fn, sq, rd, vl, op);
                    }
                });
    }

    public void xqderiveFileAuthenticationList(final String fn,
            final SubQuery<FileAuthenticationCB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FileAuthenticationCB cb = new FileAuthenticationCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String sqpp = keepId_QueryDerivedReferrer_FileAuthenticationList(cb
                .query());
        final String prpp = keepId_QueryDerivedReferrer_FileAuthenticationListParameter(vl);
        registerQueryDerivedReferrer(fn, cb.query(), "ID",
                "FILE_CRAWLING_CONFIG_ID", sqpp, "fileAuthenticationList", rd,
                vl, prpp, op);
    }

    public abstract String keepId_QueryDerivedReferrer_FileAuthenticationList(
            FileAuthenticationCQ sq);

    public abstract String keepId_QueryDerivedReferrer_FileAuthenticationListParameter(
            Object vl);

    /**
     * Prepare for (Query)DerivedReferrer (correlated sub-query). <br />
     * {FOO &lt;= (select max(BAR) from FILE_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">derivedFileConfigToLabelTypeMappingList()</span>.<span style="color: #DD4747">max</span>(new SubQuery&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void query(FileConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #DD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<FileConfigToLabelTypeMappingCB> derivedFileConfigToLabelTypeMappingList() {
        return xcreateQDRFunctionFileConfigToLabelTypeMappingList();
    }

    protected HpQDRFunction<FileConfigToLabelTypeMappingCB> xcreateQDRFunctionFileConfigToLabelTypeMappingList() {
        return new HpQDRFunction<FileConfigToLabelTypeMappingCB>(
                new HpQDRSetupper<FileConfigToLabelTypeMappingCB>() {
                    @Override
                    public void setup(final String fn,
                            final SubQuery<FileConfigToLabelTypeMappingCB> sq,
                            final String rd, final Object vl,
                            final DerivedReferrerOption op) {
                        xqderiveFileConfigToLabelTypeMappingList(fn, sq, rd,
                                vl, op);
                    }
                });
    }

    public void xqderiveFileConfigToLabelTypeMappingList(final String fn,
            final SubQuery<FileConfigToLabelTypeMappingCB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String sqpp = keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList(cb
                .query());
        final String prpp = keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter(vl);
        registerQueryDerivedReferrer(fn, cb.query(), "ID", "FILE_CONFIG_ID",
                sqpp, "fileConfigToLabelTypeMappingList", rd, vl, prpp, op);
    }

    public abstract String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList(
            FileConfigToLabelTypeMappingCQ sq);

    public abstract String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter(
            Object vl);

    /**
     * Prepare for (Query)DerivedReferrer (correlated sub-query). <br />
     * {FOO &lt;= (select max(BAR) from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #DD4747">derivedFileConfigToRoleTypeMappingList()</span>.<span style="color: #DD4747">max</span>(new SubQuery&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void query(FileConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #DD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<FileConfigToRoleTypeMappingCB> derivedFileConfigToRoleTypeMappingList() {
        return xcreateQDRFunctionFileConfigToRoleTypeMappingList();
    }

    protected HpQDRFunction<FileConfigToRoleTypeMappingCB> xcreateQDRFunctionFileConfigToRoleTypeMappingList() {
        return new HpQDRFunction<FileConfigToRoleTypeMappingCB>(
                new HpQDRSetupper<FileConfigToRoleTypeMappingCB>() {
                    @Override
                    public void setup(final String fn,
                            final SubQuery<FileConfigToRoleTypeMappingCB> sq,
                            final String rd, final Object vl,
                            final DerivedReferrerOption op) {
                        xqderiveFileConfigToRoleTypeMappingList(fn, sq, rd, vl,
                                op);
                    }
                });
    }

    public void xqderiveFileConfigToRoleTypeMappingList(final String fn,
            final SubQuery<FileConfigToRoleTypeMappingCB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String sqpp = keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList(cb
                .query());
        final String prpp = keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter(vl);
        registerQueryDerivedReferrer(fn, cb.query(), "ID", "FILE_CONFIG_ID",
                sqpp, "fileConfigToRoleTypeMappingList", rd, vl, prpp, op);
    }

    public abstract String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ sq);

    public abstract String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter(
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
     * <pre>e.g. setName_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
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

    protected void regName(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueName(), "NAME");
    }

    protected abstract ConditionValue getCValueName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param paths The value of paths as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_Equal(final String paths) {
        doSetPaths_Equal(fRES(paths));
    }

    protected void doSetPaths_Equal(final String paths) {
        regPaths(CK_EQ, paths);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param paths The value of paths as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_NotEqual(final String paths) {
        doSetPaths_NotEqual(fRES(paths));
    }

    protected void doSetPaths_NotEqual(final String paths) {
        regPaths(CK_NES, paths);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param paths The value of paths as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_GreaterThan(final String paths) {
        regPaths(CK_GT, fRES(paths));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param paths The value of paths as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_LessThan(final String paths) {
        regPaths(CK_LT, fRES(paths));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param paths The value of paths as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_GreaterEqual(final String paths) {
        regPaths(CK_GE, fRES(paths));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param paths The value of paths as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_LessEqual(final String paths) {
        regPaths(CK_LE, fRES(paths));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param pathsList The collection of paths as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_InScope(final Collection<String> pathsList) {
        doSetPaths_InScope(pathsList);
    }

    public void doSetPaths_InScope(final Collection<String> pathsList) {
        regINS(CK_INS, cTL(pathsList), getCValuePaths(), "PATHS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param pathsList The collection of paths as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_NotInScope(final Collection<String> pathsList) {
        doSetPaths_NotInScope(pathsList);
    }

    public void doSetPaths_NotInScope(final Collection<String> pathsList) {
        regINS(CK_NINS, cTL(pathsList), getCValuePaths(), "PATHS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param paths The value of paths as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setPaths_PrefixSearch(final String paths) {
        setPaths_LikeSearch(paths, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)} <br />
     * <pre>e.g. setPaths_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param paths The value of paths as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setPaths_LikeSearch(final String paths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(paths), getCValuePaths(), "PATHS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @param paths The value of paths as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setPaths_NotLikeSearch(final String paths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(paths), getCValuePaths(), "PATHS", likeSearchOption);
    }

    protected void regPaths(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValuePaths(), "PATHS");
    }

    protected abstract ConditionValue getCValuePaths();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPaths The value of includedPaths as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_Equal(final String includedPaths) {
        doSetIncludedPaths_Equal(fRES(includedPaths));
    }

    protected void doSetIncludedPaths_Equal(final String includedPaths) {
        regIncludedPaths(CK_EQ, includedPaths);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPaths The value of includedPaths as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_NotEqual(final String includedPaths) {
        doSetIncludedPaths_NotEqual(fRES(includedPaths));
    }

    protected void doSetIncludedPaths_NotEqual(final String includedPaths) {
        regIncludedPaths(CK_NES, includedPaths);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPaths The value of includedPaths as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_GreaterThan(final String includedPaths) {
        regIncludedPaths(CK_GT, fRES(includedPaths));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPaths The value of includedPaths as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_LessThan(final String includedPaths) {
        regIncludedPaths(CK_LT, fRES(includedPaths));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPaths The value of includedPaths as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_GreaterEqual(final String includedPaths) {
        regIncludedPaths(CK_GE, fRES(includedPaths));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPaths The value of includedPaths as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_LessEqual(final String includedPaths) {
        regIncludedPaths(CK_LE, fRES(includedPaths));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPathsList The collection of includedPaths as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_InScope(
            final Collection<String> includedPathsList) {
        doSetIncludedPaths_InScope(includedPathsList);
    }

    public void doSetIncludedPaths_InScope(
            final Collection<String> includedPathsList) {
        regINS(CK_INS, cTL(includedPathsList), getCValueIncludedPaths(),
                "INCLUDED_PATHS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPathsList The collection of includedPaths as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_NotInScope(
            final Collection<String> includedPathsList) {
        doSetIncludedPaths_NotInScope(includedPathsList);
    }

    public void doSetIncludedPaths_NotInScope(
            final Collection<String> includedPathsList) {
        regINS(CK_NINS, cTL(includedPathsList), getCValueIncludedPaths(),
                "INCLUDED_PATHS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPaths The value of includedPaths as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedPaths_PrefixSearch(final String includedPaths) {
        setIncludedPaths_LikeSearch(includedPaths, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)} <br />
     * <pre>e.g. setIncludedPaths_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param includedPaths The value of includedPaths as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setIncludedPaths_LikeSearch(final String includedPaths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(includedPaths), getCValueIncludedPaths(),
                "INCLUDED_PATHS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @param includedPaths The value of includedPaths as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setIncludedPaths_NotLikeSearch(final String includedPaths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(includedPaths), getCValueIncludedPaths(),
                "INCLUDED_PATHS", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     */
    public void setIncludedPaths_IsNull() {
        regIncludedPaths(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     */
    public void setIncludedPaths_IsNullOrEmpty() {
        regIncludedPaths(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     */
    public void setIncludedPaths_IsNotNull() {
        regIncludedPaths(CK_ISNN, DOBJ);
    }

    protected void regIncludedPaths(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueIncludedPaths(), "INCLUDED_PATHS");
    }

    protected abstract ConditionValue getCValueIncludedPaths();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPaths The value of excludedPaths as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_Equal(final String excludedPaths) {
        doSetExcludedPaths_Equal(fRES(excludedPaths));
    }

    protected void doSetExcludedPaths_Equal(final String excludedPaths) {
        regExcludedPaths(CK_EQ, excludedPaths);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPaths The value of excludedPaths as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_NotEqual(final String excludedPaths) {
        doSetExcludedPaths_NotEqual(fRES(excludedPaths));
    }

    protected void doSetExcludedPaths_NotEqual(final String excludedPaths) {
        regExcludedPaths(CK_NES, excludedPaths);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPaths The value of excludedPaths as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_GreaterThan(final String excludedPaths) {
        regExcludedPaths(CK_GT, fRES(excludedPaths));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPaths The value of excludedPaths as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_LessThan(final String excludedPaths) {
        regExcludedPaths(CK_LT, fRES(excludedPaths));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPaths The value of excludedPaths as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_GreaterEqual(final String excludedPaths) {
        regExcludedPaths(CK_GE, fRES(excludedPaths));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPaths The value of excludedPaths as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_LessEqual(final String excludedPaths) {
        regExcludedPaths(CK_LE, fRES(excludedPaths));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPathsList The collection of excludedPaths as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_InScope(
            final Collection<String> excludedPathsList) {
        doSetExcludedPaths_InScope(excludedPathsList);
    }

    public void doSetExcludedPaths_InScope(
            final Collection<String> excludedPathsList) {
        regINS(CK_INS, cTL(excludedPathsList), getCValueExcludedPaths(),
                "EXCLUDED_PATHS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPathsList The collection of excludedPaths as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_NotInScope(
            final Collection<String> excludedPathsList) {
        doSetExcludedPaths_NotInScope(excludedPathsList);
    }

    public void doSetExcludedPaths_NotInScope(
            final Collection<String> excludedPathsList) {
        regINS(CK_NINS, cTL(excludedPathsList), getCValueExcludedPaths(),
                "EXCLUDED_PATHS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPaths The value of excludedPaths as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedPaths_PrefixSearch(final String excludedPaths) {
        setExcludedPaths_LikeSearch(excludedPaths, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)} <br />
     * <pre>e.g. setExcludedPaths_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param excludedPaths The value of excludedPaths as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setExcludedPaths_LikeSearch(final String excludedPaths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(excludedPaths), getCValueExcludedPaths(),
                "EXCLUDED_PATHS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @param excludedPaths The value of excludedPaths as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setExcludedPaths_NotLikeSearch(final String excludedPaths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(excludedPaths), getCValueExcludedPaths(),
                "EXCLUDED_PATHS", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     */
    public void setExcludedPaths_IsNull() {
        regExcludedPaths(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     */
    public void setExcludedPaths_IsNullOrEmpty() {
        regExcludedPaths(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     */
    public void setExcludedPaths_IsNotNull() {
        regExcludedPaths(CK_ISNN, DOBJ);
    }

    protected void regExcludedPaths(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueExcludedPaths(), "EXCLUDED_PATHS");
    }

    protected abstract ConditionValue getCValueExcludedPaths();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPaths The value of includedDocPaths as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_Equal(final String includedDocPaths) {
        doSetIncludedDocPaths_Equal(fRES(includedDocPaths));
    }

    protected void doSetIncludedDocPaths_Equal(final String includedDocPaths) {
        regIncludedDocPaths(CK_EQ, includedDocPaths);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPaths The value of includedDocPaths as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_NotEqual(final String includedDocPaths) {
        doSetIncludedDocPaths_NotEqual(fRES(includedDocPaths));
    }

    protected void doSetIncludedDocPaths_NotEqual(final String includedDocPaths) {
        regIncludedDocPaths(CK_NES, includedDocPaths);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPaths The value of includedDocPaths as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_GreaterThan(final String includedDocPaths) {
        regIncludedDocPaths(CK_GT, fRES(includedDocPaths));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPaths The value of includedDocPaths as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_LessThan(final String includedDocPaths) {
        regIncludedDocPaths(CK_LT, fRES(includedDocPaths));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPaths The value of includedDocPaths as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_GreaterEqual(final String includedDocPaths) {
        regIncludedDocPaths(CK_GE, fRES(includedDocPaths));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPaths The value of includedDocPaths as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_LessEqual(final String includedDocPaths) {
        regIncludedDocPaths(CK_LE, fRES(includedDocPaths));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPathsList The collection of includedDocPaths as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_InScope(
            final Collection<String> includedDocPathsList) {
        doSetIncludedDocPaths_InScope(includedDocPathsList);
    }

    public void doSetIncludedDocPaths_InScope(
            final Collection<String> includedDocPathsList) {
        regINS(CK_INS, cTL(includedDocPathsList), getCValueIncludedDocPaths(),
                "INCLUDED_DOC_PATHS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPathsList The collection of includedDocPaths as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_NotInScope(
            final Collection<String> includedDocPathsList) {
        doSetIncludedDocPaths_NotInScope(includedDocPathsList);
    }

    public void doSetIncludedDocPaths_NotInScope(
            final Collection<String> includedDocPathsList) {
        regINS(CK_NINS, cTL(includedDocPathsList), getCValueIncludedDocPaths(),
                "INCLUDED_DOC_PATHS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPaths The value of includedDocPaths as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setIncludedDocPaths_PrefixSearch(final String includedDocPaths) {
        setIncludedDocPaths_LikeSearch(includedDocPaths, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)} <br />
     * <pre>e.g. setIncludedDocPaths_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param includedDocPaths The value of includedDocPaths as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setIncludedDocPaths_LikeSearch(final String includedDocPaths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(includedDocPaths), getCValueIncludedDocPaths(),
                "INCLUDED_DOC_PATHS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param includedDocPaths The value of includedDocPaths as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setIncludedDocPaths_NotLikeSearch(
            final String includedDocPaths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(includedDocPaths), getCValueIncludedDocPaths(),
                "INCLUDED_DOC_PATHS", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     */
    public void setIncludedDocPaths_IsNull() {
        regIncludedDocPaths(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     */
    public void setIncludedDocPaths_IsNullOrEmpty() {
        regIncludedDocPaths(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     */
    public void setIncludedDocPaths_IsNotNull() {
        regIncludedDocPaths(CK_ISNN, DOBJ);
    }

    protected void regIncludedDocPaths(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueIncludedDocPaths(), "INCLUDED_DOC_PATHS");
    }

    protected abstract ConditionValue getCValueIncludedDocPaths();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPaths The value of excludedDocPaths as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_Equal(final String excludedDocPaths) {
        doSetExcludedDocPaths_Equal(fRES(excludedDocPaths));
    }

    protected void doSetExcludedDocPaths_Equal(final String excludedDocPaths) {
        regExcludedDocPaths(CK_EQ, excludedDocPaths);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPaths The value of excludedDocPaths as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_NotEqual(final String excludedDocPaths) {
        doSetExcludedDocPaths_NotEqual(fRES(excludedDocPaths));
    }

    protected void doSetExcludedDocPaths_NotEqual(final String excludedDocPaths) {
        regExcludedDocPaths(CK_NES, excludedDocPaths);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPaths The value of excludedDocPaths as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_GreaterThan(final String excludedDocPaths) {
        regExcludedDocPaths(CK_GT, fRES(excludedDocPaths));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPaths The value of excludedDocPaths as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_LessThan(final String excludedDocPaths) {
        regExcludedDocPaths(CK_LT, fRES(excludedDocPaths));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPaths The value of excludedDocPaths as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_GreaterEqual(final String excludedDocPaths) {
        regExcludedDocPaths(CK_GE, fRES(excludedDocPaths));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPaths The value of excludedDocPaths as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_LessEqual(final String excludedDocPaths) {
        regExcludedDocPaths(CK_LE, fRES(excludedDocPaths));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPathsList The collection of excludedDocPaths as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_InScope(
            final Collection<String> excludedDocPathsList) {
        doSetExcludedDocPaths_InScope(excludedDocPathsList);
    }

    public void doSetExcludedDocPaths_InScope(
            final Collection<String> excludedDocPathsList) {
        regINS(CK_INS, cTL(excludedDocPathsList), getCValueExcludedDocPaths(),
                "EXCLUDED_DOC_PATHS");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPathsList The collection of excludedDocPaths as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_NotInScope(
            final Collection<String> excludedDocPathsList) {
        doSetExcludedDocPaths_NotInScope(excludedDocPathsList);
    }

    public void doSetExcludedDocPaths_NotInScope(
            final Collection<String> excludedDocPathsList) {
        regINS(CK_NINS, cTL(excludedDocPathsList), getCValueExcludedDocPaths(),
                "EXCLUDED_DOC_PATHS");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPaths The value of excludedDocPaths as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setExcludedDocPaths_PrefixSearch(final String excludedDocPaths) {
        setExcludedDocPaths_LikeSearch(excludedDocPaths, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)} <br />
     * <pre>e.g. setExcludedDocPaths_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param excludedDocPaths The value of excludedDocPaths as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setExcludedDocPaths_LikeSearch(final String excludedDocPaths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(excludedDocPaths), getCValueExcludedDocPaths(),
                "EXCLUDED_DOC_PATHS", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @param excludedDocPaths The value of excludedDocPaths as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setExcludedDocPaths_NotLikeSearch(
            final String excludedDocPaths,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(excludedDocPaths), getCValueExcludedDocPaths(),
                "EXCLUDED_DOC_PATHS", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     */
    public void setExcludedDocPaths_IsNull() {
        regExcludedDocPaths(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     */
    public void setExcludedDocPaths_IsNullOrEmpty() {
        regExcludedDocPaths(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     */
    public void setExcludedDocPaths_IsNotNull() {
        regExcludedDocPaths(CK_ISNN, DOBJ);
    }

    protected void regExcludedDocPaths(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueExcludedDocPaths(), "EXCLUDED_DOC_PATHS");
    }

    protected abstract ConditionValue getCValueExcludedDocPaths();

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
     * <pre>e.g. setConfigParameter_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
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

    protected void regConfigParameter(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueConfigParameter(), "CONFIG_PARAMETER");
    }

    protected abstract ConditionValue getCValueConfigParameter();

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

    protected void regDepth(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueDepth(), "DEPTH");
    }

    protected abstract ConditionValue getCValueDepth();

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

    protected void regMaxAccessCount(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueMaxAccessCount(), "MAX_ACCESS_COUNT");
    }

    protected abstract ConditionValue getCValueMaxAccessCount();

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

    protected void regNumOfThread(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueNumOfThread(), "NUM_OF_THREAD");
    }

    protected abstract ConditionValue getCValueNumOfThread();

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

    protected void regIntervalTime(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueIntervalTime(), "INTERVAL_TIME");
    }

    protected abstract ConditionValue getCValueIntervalTime();

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

    protected void regBoost(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueBoost(), "BOOST");
    }

    protected abstract ConditionValue getCValueBoost();

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
     * <pre>e.g. setAvailable_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
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

    protected void regAvailable(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueAvailable(), "AVAILABLE");
    }

    protected abstract ConditionValue getCValueAvailable();

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

    protected void regSortOrder(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueSortOrder(), "SORT_ORDER");
    }

    protected abstract ConditionValue getCValueSortOrder();

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
     * <pre>e.g. setCreatedBy_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
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

    protected void regCreatedBy(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueCreatedBy(), "CREATED_BY");
    }

    protected abstract ConditionValue getCValueCreatedBy();

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
     * <pre>e.g. setUpdatedBy_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
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

    protected void regUpdatedBy(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueUpdatedBy(), "UPDATED_BY");
    }

    protected abstract ConditionValue getCValueUpdatedBy();

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
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
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

    protected void regUpdatedTime(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueUpdatedTime(), "UPDATED_TIME");
    }

    protected abstract ConditionValue getCValueUpdatedTime();

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
     * <pre>e.g. setDeletedBy_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
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

    protected void regDeletedBy(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueDeletedBy(), "DELETED_BY");
    }

    protected abstract ConditionValue getCValueDeletedBy();

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
     * <pre>e.g. setDeletedTime_FromTo(fromDate, toDate, new <span style="color: #DD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of deletedTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of deletedTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setDeletedTime_FromTo(final Date fromDatetime,
            final Date toDatetime, final FromToOption fromToOption) {
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
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #DD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of deletedTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of deletedTime. (NullAllowed: if null, no to-condition)
     */
    public void setDeletedTime_DateFromTo(final Date fromDate, final Date toDate) {
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

    protected void regDeletedTime(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueDeletedTime(), "DELETED_TIME");
    }

    protected abstract ConditionValue getCValueDeletedTime();

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

    protected void regVersionNo(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueVersionNo(), "VERSION_NO");
    }

    protected abstract ConditionValue getCValueVersionNo();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_Equal()</span>.max(new SubQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void query(FileCrawlingConfigCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileCrawlingConfigCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ, FileCrawlingConfigCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void query(FileCrawlingConfigCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileCrawlingConfigCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES, FileCrawlingConfigCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void query(FileCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileCrawlingConfigCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT, FileCrawlingConfigCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessThan()</span>.max(new SubQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void query(FileCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileCrawlingConfigCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT, FileCrawlingConfigCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void query(FileCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileCrawlingConfigCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE, FileCrawlingConfigCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void query(FileCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileCrawlingConfigCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE, FileCrawlingConfigCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(final String fn,
            final SubQuery<CB> sq, final String rd, final HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        final FileCrawlingConfigCB cb = xcreateScalarConditionCB();
        sq.query((CB) cb);
        final String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB) xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }

    public abstract String keepScalarCondition(FileCrawlingConfigCQ sq);

    protected FileCrawlingConfigCB xcreateScalarConditionCB() {
        final FileCrawlingConfigCB cb = newMyCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected FileCrawlingConfigCB xcreateScalarConditionPartitionByCB() {
        final FileCrawlingConfigCB cb = newMyCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String fn,
            final SubQuery<FileCrawlingConfigCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
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

    public abstract String keepSpecifyMyselfDerived(FileCrawlingConfigCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<FileCrawlingConfigCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(FileCrawlingConfigCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(
            final String fn, final SubQuery<CB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
        cb.xsetupForDerivedReferrer(this);
        sq.query((CB) cb);
        final String pk = "ID";
        final String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp,
                "myselfDerived", rd, vl, prpp, op);
    }

    public abstract String keepQueryMyselfDerived(FileCrawlingConfigCQ sq);

    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfExists(final SubQuery<FileCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
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

    public abstract String keepMyselfExists(FileCrawlingConfigCQ sq);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfInScope(final SubQuery<FileCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
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

    public abstract String keepMyselfInScope(FileCrawlingConfigCQ sq);

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
    protected FileCrawlingConfigCB newMyCB() {
        return new FileCrawlingConfigCB();
    }

    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCQ() {
        return FileCrawlingConfigCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
