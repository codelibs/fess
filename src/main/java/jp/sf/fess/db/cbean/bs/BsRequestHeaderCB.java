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

package jp.sf.fess.db.cbean.bs;

import jp.sf.fess.db.allcommon.DBFluteConfig;
import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.allcommon.ImplementedInvokerAssistant;
import jp.sf.fess.db.allcommon.ImplementedSqlClauseCreator;
import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.RequestHeaderCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;

import org.seasar.dbflute.cbean.AbstractConditionBean;
import org.seasar.dbflute.cbean.AndQuery;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.OrQuery;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.UnionQuery;
import org.seasar.dbflute.cbean.chelper.HpAbstractSpecification;
import org.seasar.dbflute.cbean.chelper.HpCBPurpose;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.chelper.HpColQyHandler;
import org.seasar.dbflute.cbean.chelper.HpColQyOperand;
import org.seasar.dbflute.cbean.chelper.HpColumnSpHandler;
import org.seasar.dbflute.cbean.chelper.HpSDRFunction;
import org.seasar.dbflute.cbean.chelper.HpSDRSetupper;
import org.seasar.dbflute.cbean.chelper.HpSpQyCall;
import org.seasar.dbflute.cbean.chelper.HpSpecifiedColumn;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.twowaysql.factory.SqlAnalyzerFactory;

/**
 * The base condition-bean of REQUEST_HEADER.
 * @author DBFlute(AutoGenerator)
 */
public class BsRequestHeaderCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected RequestHeaderCQ _conditionQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsRequestHeaderCB() {
        if (DBFluteConfig.getInstance().isPagingCountLater()) {
            enablePagingCountLater();
        }
        if (DBFluteConfig.getInstance().isPagingCountLeastJoin()) {
            enablePagingCountLeastJoin();
        }
        if (DBFluteConfig.getInstance().isQueryUpdateCountPreCheck()) {
            enableQueryUpdateCountPreCheck();
        }
    }

    // ===================================================================================
    //                                                                           SqlClause
    //                                                                           =========
    @Override
    protected SqlClause createSqlClause() {
        final SqlClauseCreator creator = DBFluteConfig.getInstance()
                .getSqlClauseCreator();
        if (creator != null) {
            return creator.createSqlClause(this);
        }
        return new ImplementedSqlClauseCreator().createSqlClause(this); // as default
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    @Override
    protected DBMetaProvider getDBMetaProvider() {
        return DBMetaInstanceHandler.getProvider(); // as default
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    @Override
    public String getTableDbName() {
        return "REQUEST_HEADER";
    }

    // ===================================================================================
    //                                                                 PrimaryKey Handling
    //                                                                 ===================
    /**
     * Accept the query condition of primary key as equal.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return this. (NotNull)
     */
    public RequestHeaderCB acceptPK(final Long id) {
        assertObjectNotNull("id", id);
        final BsRequestHeaderCB cb = this;
        cb.query().setId_Equal(id);
        return (RequestHeaderCB) this;
    }

    /**
     * Accept the query condition of primary key as equal. (old style)
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     */
    public void acceptPrimaryKey(final Long id) {
        assertObjectNotNull("id", id);
        final BsRequestHeaderCB cb = this;
        cb.query().setId_Equal(id);
    }

    @Override
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_Id_Asc();
        return this;
    }

    @Override
    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_Id_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    /**
     * Prepare for various queries. <br />
     * Examples of main functions are following:
     * <pre>
     * <span style="color: #3F7E5E">// Basic Queries</span>
     * cb.query().setMemberId_Equal(value);        <span style="color: #3F7E5E">// =</span>
     * cb.query().setMemberId_NotEqual(value);     <span style="color: #3F7E5E">// !=</span>
     * cb.query().setMemberId_GreaterThan(value);  <span style="color: #3F7E5E">// &gt;</span>
     * cb.query().setMemberId_LessThan(value);     <span style="color: #3F7E5E">// &lt;</span>
     * cb.query().setMemberId_GreaterEqual(value); <span style="color: #3F7E5E">// &gt;=</span>
     * cb.query().setMemberId_LessEqual(value);    <span style="color: #3F7E5E">// &lt;=</span>
     * cb.query().setMemberName_InScope(valueList);    <span style="color: #3F7E5E">// in ('a', 'b')</span>
     * cb.query().setMemberName_NotInScope(valueList); <span style="color: #3F7E5E">// not in ('a', 'b')</span>
     * cb.query().setMemberName_PrefixSearch(value);   <span style="color: #3F7E5E">// like 'a%' escape '|'</span>
     * <span style="color: #3F7E5E">// LikeSearch with various options: (versatile)</span>
     * <span style="color: #3F7E5E">// {like ... [options]}</span>
     * cb.query().setMemberName_LikeSearch(value, option);
     * cb.query().setMemberName_NotLikeSearch(value, option); <span style="color: #3F7E5E">// not like ...</span>
     * <span style="color: #3F7E5E">// FromTo with various options: (versatile)</span>
     * <span style="color: #3F7E5E">// {(default) fromDatetime &lt;= BIRTHDATE &lt;= toDatetime}</span>
     * cb.query().setBirthdate_FromTo(fromDatetime, toDatetime, option);
     * <span style="color: #3F7E5E">// DateFromTo: (Date means yyyy/MM/dd)</span>
     * <span style="color: #3F7E5E">// {fromDate &lt;= BIRTHDATE &lt; toDate + 1 day}</span>
     * cb.query().setBirthdate_DateFromTo(fromDate, toDate);
     * cb.query().setBirthdate_IsNull();    <span style="color: #3F7E5E">// is null</span>
     * cb.query().setBirthdate_IsNotNull(); <span style="color: #3F7E5E">// is not null</span>
     *
     * <span style="color: #3F7E5E">// ExistsReferrer: (correlated sub-query)</span>
     * <span style="color: #3F7E5E">// {where exists (select PURCHASE_ID from PURCHASE where ...)}</span>
     * cb.query().existsPurchaseList(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.query().setXxx... <span style="color: #3F7E5E">// referrer sub-query condition</span>
     *     }
     * });
     * cb.query().notExistsPurchaseList...
     *
     * <span style="color: #3F7E5E">// InScopeRelation: (sub-query)</span>
     * <span style="color: #3F7E5E">// {where MEMBER_STATUS_CODE in (select MEMBER_STATUS_CODE from MEMBER_STATUS where ...)}</span>
     * cb.query().inScopeMemberStatus(new SubQuery&lt;MemberStatusCB&gt;() {
     *     public void query(MemberStatusCB subCB) {
     *         subCB.query().setXxx... <span style="color: #3F7E5E">// relation sub-query condition</span>
     *     }
     * });
     * cb.query().notInScopeMemberStatus...
     *
     * <span style="color: #3F7E5E">// (Query)DerivedReferrer: (correlated sub-query)</span>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setXxx... <span style="color: #3F7E5E">// referrer sub-query condition</span>
     *     }
     * }).greaterEqual(value);
     *
     * <span style="color: #3F7E5E">// ScalarCondition: (self-table sub-query)</span>
     * cb.query().scalar_Equal().max(new SubQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB subCB) {
     *         subCB.specify().columnBirthdate(); <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setXxx... <span style="color: #3F7E5E">// scalar sub-query condition</span>
     *     }
     * });
     *
     * <span style="color: #3F7E5E">// OrderBy</span>
     * cb.query().addOrderBy_MemberName_Asc();
     * cb.query().addOrderBy_MemberName_Desc().withManualOrder(valueList);
     * cb.query().addOrderBy_MemberName_Desc().withNullsFirst();
     * cb.query().addOrderBy_MemberName_Desc().withNullsLast();
     * cb.query().addSpecifiedDerivedOrderBy_Desc(aliasName);
     *
     * <span style="color: #3F7E5E">// Query(Relation)</span>
     * cb.query().queryMemberStatus()...;
     * cb.query().queryMemberAddressAsValid(targetDate)...;
     * </pre>
     * @return The instance of condition-query for base-point table to set up query. (NotNull)
     */
    public RequestHeaderCQ query() {
        assertQueryPurpose(); // assert only when user-public query
        return getConditionQuery();
    }

    public RequestHeaderCQ getConditionQuery() { // public for parameter comment and internal
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected RequestHeaderCQ createLocalCQ() {
        return xcreateCQ(null, getSqlClause(), getSqlClause()
                .getBasePointAliasName(), 0);
    }

    protected RequestHeaderCQ xcreateCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        final RequestHeaderCQ cq = xnewCQ(childQuery, sqlClause, aliasName,
                nestLevel);
        cq.xsetBaseCB(this);
        return cq;
    }

    protected RequestHeaderCQ xnewCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        return new RequestHeaderCQ(childQuery, sqlClause, aliasName, nestLevel);
    }

    @Override
    public ConditionQuery localCQ() {
        return getConditionQuery();
    }

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    /**
     * Set up 'union' for base-point table. <br />
     * You don't need to call SetupSelect in union-query,
     * because it inherits calls before. (Don't call SetupSelect after here)
     * <pre>
     * cb.query().<span style="color: #DD4747">union</span>(new UnionQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(RequestHeaderCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(final UnionQuery<RequestHeaderCB> unionQuery) {
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForUnion(this);
        xsyncUQ(cb);
        try {
            lock();
            unionQuery.query(cb);
        } finally {
            unlock();
        }
        xsaveUCB(cb);
        final RequestHeaderCQ cq = cb.query();
        query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all' for base-point table. <br />
     * You don't need to call SetupSelect in union-query,
     * because it inherits calls before. (Don't call SetupSelect after here)
     * <pre>
     * cb.query().<span style="color: #DD4747">unionAll</span>(new UnionQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(RequestHeaderCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union all'. (NotNull)
     */
    public void unionAll(final UnionQuery<RequestHeaderCB> unionQuery) {
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForUnion(this);
        xsyncUQ(cb);
        try {
            lock();
            unionQuery.query(cb);
        } finally {
            unlock();
        }
        xsaveUCB(cb);
        final RequestHeaderCQ cq = cb.query();
        query().xsetUnionAllQuery(cq);
    }

    // ===================================================================================
    //                                                                         SetupSelect
    //                                                                         ===========
    /**
     * Set up relation columns to select clause. <br />
     * WEB_CRAWLING_CONFIG by my WEB_CRAWLING_CONFIG_ID, named 'webCrawlingConfig'.
     * <pre>
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.<span style="color: #DD4747">setupSelect_WebCrawlingConfig()</span>; <span style="color: #3F7E5E">// ...().with[nested-relation]()</span>
     * cb.query().setFoo...(value);
     * RequestHeader requestHeader = requestHeaderBhv.selectEntityWithDeletedCheck(cb);
     * ... = requestHeader.<span style="color: #DD4747">getWebCrawlingConfig()</span>; <span style="color: #3F7E5E">// you can get by using SetupSelect</span>
     * </pre>
     */
    public void setupSelect_WebCrawlingConfig() {
        assertSetupSelectPurpose("webCrawlingConfig");
        if (hasSpecifiedColumn()) { // if reverse call
            specify().columnWebCrawlingConfigId();
        }
        doSetupSelect(new SsCall() {
            @Override
            public ConditionQuery qf() {
                return query().queryWebCrawlingConfig();
            }
        });
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected HpSpecification _specification;

    /**
     * Prepare for SpecifyColumn, (Specify)DerivedReferrer. <br />
     * This method should be called after SetupSelect.
     * <pre>
     * cb.setupSelect_MemberStatus(); <span style="color: #3F7E5E">// should be called before specify()</span>
     * cb.specify().columnMemberName();
     * cb.specify().specifyMemberStatus().columnMemberStatusName();
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *         subCB.query().set...
     *     }
     * }, aliasName);
     * </pre>
     * @return The instance of specification. (NotNull)
     */
    public HpSpecification specify() {
        assertSpecifyPurpose();
        if (_specification == null) {
            _specification = new HpSpecification(this,
                    new HpSpQyCall<RequestHeaderCQ>() {
                        @Override
                        public boolean has() {
                            return true;
                        }

                        @Override
                        public RequestHeaderCQ qy() {
                            return getConditionQuery();
                        }
                    }, _purpose, getDBMetaProvider());
        }
        return _specification;
    }

    @Override
    public HpColumnSpHandler localSp() {
        return specify();
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null
                && _specification.isAlreadySpecifiedRequiredColumn();
    }

    public static class HpSpecification extends
            HpAbstractSpecification<RequestHeaderCQ> {
        protected WebCrawlingConfigCB.HpSpecification _webCrawlingConfig;

        public HpSpecification(final ConditionBean baseCB,
                final HpSpQyCall<RequestHeaderCQ> qyCall,
                final HpCBPurpose purpose, final DBMetaProvider dbmetaProvider) {
            super(baseCB, qyCall, purpose, dbmetaProvider);
        }

        /**
         * ID: {PK, ID, NotNull, BIGINT(19)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnId() {
            return doColumn("ID");
        }

        /**
         * NAME: {NotNull, VARCHAR(100)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnName() {
            return doColumn("NAME");
        }

        /**
         * VALUE: {NotNull, VARCHAR(1000)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnValue() {
            return doColumn("VALUE");
        }

        /**
         * WEB_CRAWLING_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnWebCrawlingConfigId() {
            return doColumn("WEB_CRAWLING_CONFIG_ID");
        }

        /**
         * CREATED_BY: {NotNull, VARCHAR(255)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnCreatedBy() {
            return doColumn("CREATED_BY");
        }

        /**
         * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnCreatedTime() {
            return doColumn("CREATED_TIME");
        }

        /**
         * UPDATED_BY: {VARCHAR(255)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnUpdatedBy() {
            return doColumn("UPDATED_BY");
        }

        /**
         * UPDATED_TIME: {TIMESTAMP(23, 10)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnUpdatedTime() {
            return doColumn("UPDATED_TIME");
        }

        /**
         * DELETED_BY: {VARCHAR(255)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnDeletedBy() {
            return doColumn("DELETED_BY");
        }

        /**
         * DELETED_TIME: {TIMESTAMP(23, 10)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnDeletedTime() {
            return doColumn("DELETED_TIME");
        }

        /**
         * VERSION_NO: {NotNull, INTEGER(10)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnVersionNo() {
            return doColumn("VERSION_NO");
        }

        @Override
        public void everyColumn() {
            doEveryColumn();
        }

        @Override
        public void exceptRecordMetaColumn() {
            doExceptRecordMetaColumn();
        }

        @Override
        protected void doSpecifyRequiredColumn() {
            columnId(); // PK
            if (qyCall().qy().hasConditionQueryWebCrawlingConfig()
                    || qyCall().qy().xgetReferrerQuery() instanceof WebCrawlingConfigCQ) {
                columnWebCrawlingConfigId(); // FK or one-to-one referrer
            }
        }

        @Override
        protected String getTableDbName() {
            return "REQUEST_HEADER";
        }

        /**
         * Prepare to specify functions about relation table. <br />
         * WEB_CRAWLING_CONFIG by my WEB_CRAWLING_CONFIG_ID, named 'webCrawlingConfig'.
         * @return The instance for specification for relation table to specify. (NotNull)
         */
        public WebCrawlingConfigCB.HpSpecification specifyWebCrawlingConfig() {
            assertRelation("webCrawlingConfig");
            if (_webCrawlingConfig == null) {
                _webCrawlingConfig = new WebCrawlingConfigCB.HpSpecification(
                        _baseCB, new HpSpQyCall<WebCrawlingConfigCQ>() {
                            @Override
                            public boolean has() {
                                return _qyCall.has()
                                        && _qyCall
                                                .qy()
                                                .hasConditionQueryWebCrawlingConfig();
                            }

                            @Override
                            public WebCrawlingConfigCQ qy() {
                                return _qyCall.qy().queryWebCrawlingConfig();
                            }
                        }, _purpose, _dbmetaProvider);
                if (xhasSyncQyCall()) { // inherits it
                    _webCrawlingConfig
                            .xsetSyncQyCall(new HpSpQyCall<WebCrawlingConfigCQ>() {
                                @Override
                                public boolean has() {
                                    return xsyncQyCall().has()
                                            && xsyncQyCall()
                                                    .qy()
                                                    .hasConditionQueryWebCrawlingConfig();
                                }

                                @Override
                                public WebCrawlingConfigCQ qy() {
                                    return xsyncQyCall().qy()
                                            .queryWebCrawlingConfig();
                                }
                            });
                }
            }
            return _webCrawlingConfig;
        }

        /**
         * Prepare for (Specify)MyselfDerived (SubQuery).
         * @return The object to set up a function for myself table. (NotNull)
         */
        public HpSDRFunction<RequestHeaderCB, RequestHeaderCQ> myselfDerived() {
            assertDerived("myselfDerived");
            if (xhasSyncQyCall()) {
                xsyncQyCall().qy();
            } // for sync (for example, this in ColumnQuery)
            return new HpSDRFunction<RequestHeaderCB, RequestHeaderCQ>(_baseCB,
                    _qyCall.qy(),
                    new HpSDRSetupper<RequestHeaderCB, RequestHeaderCQ>() {
                        @Override
                        public void setup(final String fn,
                                final SubQuery<RequestHeaderCB> sq,
                                final RequestHeaderCQ cq, final String al,
                                final DerivedReferrerOption op) {
                            cq.xsmyselfDerive(fn, sq, al, op);
                        }
                    }, _dbmetaProvider);
        }
    }

    // [DBFlute-0.9.5.3]
    // ===================================================================================
    //                                                                        Column Query
    //                                                                        ============
    /**
     * Set up column-query. {column1 = column2}
     * <pre>
     * <span style="color: #3F7E5E">// where FOO &lt; BAR</span>
     * cb.<span style="color: #DD4747">columnQuery</span>(new SpecifyQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(RequestHeaderCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     *     }
     * }).lessThan(new SpecifyQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(RequestHeaderCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     *     }
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param leftSpecifyQuery The specify-query for left column. (NotNull)
     * @return The object for setting up operand and right column. (NotNull)
     */
    public HpColQyOperand<RequestHeaderCB> columnQuery(
            final SpecifyQuery<RequestHeaderCB> leftSpecifyQuery) {
        return xcreateColQyOperand(new HpColQyHandler<RequestHeaderCB>() {
            @Override
            public HpCalculator handle(
                    final SpecifyQuery<RequestHeaderCB> rightSp,
                    final String operand) {
                return xcolqy(xcreateColumnQueryCB(), xcreateColumnQueryCB(),
                        leftSpecifyQuery, rightSp, operand);
            }
        });
    }

    protected RequestHeaderCB xcreateColumnQueryCB() {
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForColumnQuery(this);
        return cb;
    }

    // ===================================================================================
    //                                                                        Dream Cruise
    //                                                                        ============
    /**
     * Welcome to the Dream Cruise for condition-bean deep world. <br />
     * This is very specialty so you can get the frontier spirit. Bon voyage!
     * @return The condition-bean for dream cruise, which is linked to main condition-bean.
     */
    public RequestHeaderCB dreamCruiseCB() {
        final RequestHeaderCB cb = new RequestHeaderCB();
        cb.xsetupForDreamCruise(this);
        return cb;
    }

    @Override
    protected ConditionBean xdoCreateDreamCruiseCB() {
        return dreamCruiseCB();
    }

    // [DBFlute-0.9.6.3]
    // ===================================================================================
    //                                                                       OrScope Query
    //                                                                       =============
    /**
     * Set up the query for or-scope. <br />
     * (Same-column-and-same-condition-key conditions are allowed in or-scope)
     * <pre>
     * <span style="color: #3F7E5E">// where (FOO = '...' or BAR = '...')</span>
     * cb.<span style="color: #DD4747">orScopeQuery</span>(new OrQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(RequestHeaderCB orCB) {
     *         orCB.query().setFOO_Equal...
     *         orCB.query().setBAR_Equal...
     *     }
     * });
     * </pre>
     * @param orQuery The query for or-condition. (NotNull)
     */
    public void orScopeQuery(final OrQuery<RequestHeaderCB> orQuery) {
        xorSQ((RequestHeaderCB) this, orQuery);
    }

    @Override
    protected HpCBPurpose xhandleOrSQPurposeChange() {
        return null; // means no check
    }

    /**
     * Set up the and-part of or-scope. <br />
     * (However nested or-scope query and as-or-split of like-search in and-part are unsupported)
     * <pre>
     * <span style="color: #3F7E5E">// where (FOO = '...' or (BAR = '...' and QUX = '...'))</span>
     * cb.<span style="color: #DD4747">orScopeQuery</span>(new OrQuery&lt;RequestHeaderCB&gt;() {
     *     public void query(RequestHeaderCB orCB) {
     *         orCB.query().setFOO_Equal...
     *         orCB.<span style="color: #DD4747">orScopeQueryAndPart</span>(new AndQuery&lt;RequestHeaderCB&gt;() {
     *             public void query(RequestHeaderCB andCB) {
     *                 andCB.query().setBar_...
     *                 andCB.query().setQux_...
     *             }
     *         });
     *     }
     * });
     * </pre>
     * @param andQuery The query for and-condition. (NotNull)
     */
    public void orScopeQueryAndPart(final AndQuery<RequestHeaderCB> andQuery) {
        xorSQAP((RequestHeaderCB) this, andQuery);
    }

    /**
     * Check invalid query when query is set. <br />
     * (it throws an exception if set query is invalid) <br />
     * You should call this before registrations of where clause and other queries. <br />
     * Union and SubQuery and other sub condition-bean inherit this. <br />
     *
     * <p>renamed to checkNullOrEmptyQuery() since 1.1,
     * but not deprecated because it might have many use.</p>
     *
     * #java8 compatible option
     */
    public void checkInvalidQuery() {
        checkNullOrEmptyQuery();
    }

    /**
     * Accept (no check) an invalid query when a query is set. <br />
     * (no condition if a set query is invalid) <br />
     * You should call this before registrations of where clause and other queries. <br />
     * Union and SubQuery and other sub condition-bean inherit this.
     * @deprecated use ignoreNullOrEmptyQuery()
     */
    @Deprecated
    public void acceptInvalidQuery() {
        getSqlClause().ignoreNullOrEmptyQuery();
    }

    /**
     * Allow to auto-detect joins that can be inner-join. <br />
     * <pre>
     * o You should call this before registrations of where clause.
     * o Union and SubQuery and other sub condition-bean inherit this.
     * o You should confirm your SQL on the log to be tuned by inner-join correctly.
     * </pre>
     * @deprecated use enableInnerJoinAutoDetect()
     */
    @Deprecated
    public void allowInnerJoinAutoDetect() {
        enableInnerJoinAutoDetect();
    }

    /**
     * Suppress auto-detecting inner-join. <br />
     * You should call this before registrations of where clause.
     * @deprecated use disableInnerJoinAutoDetect()
     */
    @Deprecated
    public void suppressInnerJoinAutoDetect() {
        disableInnerJoinAutoDetect();
    }

    /**
     * Allow an empty string for query. <br />
     * (you can use an empty string as condition) <br />
     * You should call this before registrations of where clause and other queries. <br />
     * Union and SubQuery and other sub condition-bean inherit this.
     * @deprecated use enableEmptyStringQuery()
     */
    @Deprecated
    public void allowEmptyStringQuery() {
        enableEmptyStringQuery();
    }

    /**
     * Enable checking record count before QueryUpdate (contains QueryDelete). (default is disabled) <br />
     * No query update if zero count. (basically for MySQL's deadlock by next-key lock)
     * @deprecated use enableQueryUpdateCountPreCheck()
     */
    @Deprecated
    public void enableCheckCountBeforeQueryUpdate() {
        enableQueryUpdateCountPreCheck();
    }

    /**
     * Disable checking record count before QueryUpdate (contains QueryDelete). (back to default) <br />
     * Executes query update even if zero count. (normal specification)
     * @deprecated use disableQueryUpdateCountPreCheck()
     */
    @Deprecated
    public void disableCheckCountBeforeQueryUpdate() {
        disableQueryUpdateCountPreCheck();
    }

    /**
     * Allow "that's bad timing" check.
     * @deprecated use enableThatsBadTiming()
     */
    @Deprecated
    public void allowThatsBadTiming() {
        enableThatsBadTiming();
    }

    /**
     * Suppress "that's bad timing" check.
     * @deprecated use disableThatsBadTiming()
     */
    @Deprecated
    public void suppressThatsBadTiming() {
        disableThatsBadTiming();
    }

    // ===================================================================================
    //                                                                          DisplaySQL
    //                                                                          ==========
    @Override
    protected SqlAnalyzerFactory getSqlAnalyzerFactory() {
        return new ImplementedInvokerAssistant().assistSqlAnalyzerFactory();
    }

    @Override
    protected String getLogDateFormat() {
        return DBFluteConfig.getInstance().getLogDateFormat();
    }

    @Override
    protected String getLogTimestampFormat() {
        return DBFluteConfig.getInstance().getLogTimestampFormat();
    }

    // ===================================================================================
    //                                                                       Meta Handling
    //                                                                       =============
    @Override
    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Purpose Type
    //                                                                        ============
    @Override
    protected void xprepareSyncQyCall(final ConditionBean mainCB) {
        final RequestHeaderCB cb;
        if (mainCB != null) {
            cb = (RequestHeaderCB) mainCB;
        } else {
            cb = new RequestHeaderCB();
        }
        specify().xsetSyncQyCall(new HpSpQyCall<RequestHeaderCQ>() {
            @Override
            public boolean has() {
                return true;
            }

            @Override
            public RequestHeaderCQ qy() {
                return cb.query();
            }
        });
    }

    // ===================================================================================
    //                                                                            Internal
    //                                                                            ========
    // very internal (for suppressing warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() {
        return RequestHeaderCB.class.getName();
    }

    protected String getConditionQueryClassNameInternally() {
        return RequestHeaderCQ.class.getName();
    }

    protected String getSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }

    protected String getConditionOptionClassNameInternally() {
        return ConditionOption.class.getName();
    }
}
