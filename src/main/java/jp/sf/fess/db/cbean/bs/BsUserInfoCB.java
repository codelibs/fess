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
import jp.sf.fess.db.cbean.FavoriteLogCB;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.cbean.cq.UserInfoCQ;

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
 * The base condition-bean of USER_INFO.
 * @author DBFlute(AutoGenerator)
 */
public class BsUserInfoCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected UserInfoCQ _conditionQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsUserInfoCB() {
        if (DBFluteConfig.getInstance().isPagingCountLater()) {
            enablePagingCountLater();
        }
        if (DBFluteConfig.getInstance().isPagingCountLeastJoin()) {
            enablePagingCountLeastJoin();
        }
        if (DBFluteConfig.getInstance().isCheckCountBeforeQueryUpdate()) {
            enableCheckCountBeforeQueryUpdate();
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
        return "USER_INFO";
    }

    // ===================================================================================
    //                                                                 PrimaryKey Handling
    //                                                                 ===================
    public void acceptPrimaryKey(final Long id) {
        assertObjectNotNull("id", id);
        final BsUserInfoCB cb = this;
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
     * <span style="color: #3F7E5E">// ExistsReferrer: (co-related sub-query)</span>
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
     * <span style="color: #3F7E5E">// (Query)DerivedReferrer: (co-related sub-query)</span>
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
    public UserInfoCQ query() {
        assertQueryPurpose(); // assert only when user-public query 
        return getConditionQuery();
    }

    public UserInfoCQ getConditionQuery() { // public for parameter comment and internal
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected UserInfoCQ createLocalCQ() {
        return xcreateCQ(null, getSqlClause(), getSqlClause()
                .getBasePointAliasName(), 0);
    }

    protected UserInfoCQ xcreateCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        final UserInfoCQ cq = xnewCQ(childQuery, sqlClause, aliasName,
                nestLevel);
        cq.xsetBaseCB(this);
        return cq;
    }

    protected UserInfoCQ xnewCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        return new UserInfoCQ(childQuery, sqlClause, aliasName, nestLevel);
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
     * cb.query().<span style="color: #FD4747">union</span>(new UnionQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(final UnionQuery<UserInfoCB> unionQuery) {
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForUnion(this);
        xsyncUQ(cb);
        unionQuery.query(cb);
        xsaveUCB(cb);
        final UserInfoCQ cq = cb.query();
        query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all' for base-point table. <br />
     * You don't need to call SetupSelect in union-query,
     * because it inherits calls before. (Don't call SetupSelect after here)
     * <pre>
     * cb.query().<span style="color: #FD4747">unionAll</span>(new UnionQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union all'. (NotNull)
     */
    public void unionAll(final UnionQuery<UserInfoCB> unionQuery) {
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForUnion(this);
        xsyncUQ(cb);
        unionQuery.query(cb);
        xsaveUCB(cb);
        final UserInfoCQ cq = cb.query();
        query().xsetUnionAllQuery(cq);
    }

    // ===================================================================================
    //                                                                         SetupSelect
    //                                                                         ===========

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
                    new HpSpQyCall<UserInfoCQ>() {
                        @Override
                        public boolean has() {
                            return true;
                        }

                        @Override
                        public UserInfoCQ qy() {
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
            HpAbstractSpecification<UserInfoCQ> {
        public HpSpecification(final ConditionBean baseCB,
                final HpSpQyCall<UserInfoCQ> qyCall, final HpCBPurpose purpose,
                final DBMetaProvider dbmetaProvider) {
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
         * CODE: {NotNull, VARCHAR(1000)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnCode() {
            return doColumn("CODE");
        }

        /**
         * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnCreatedTime() {
            return doColumn("CREATED_TIME");
        }

        /**
         * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
         * @return The information object of specified column. (NotNull)
         */
        public HpSpecifiedColumn columnUpdatedTime() {
            return doColumn("UPDATED_TIME");
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
        }

        @Override
        protected String getTableDbName() {
            return "USER_INFO";
        }

        /**
         * Prepare for (Specify)DerivedReferrer. <br />
         * {select max(FOO) from FAVORITE_LOG where ...) as FOO_MAX} <br />
         * FAVORITE_LOG by USER_ID, named 'favoriteLogList'.
         * <pre>
         * cb.specify().<span style="color: #FD4747">derivedFavoriteLogList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;FavoriteLogCB&gt;() {
         *     public void query(FavoriteLogCB subCB) {
         *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
         *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
         *     }
         * }, FavoriteLog.<span style="color: #FD4747">ALIAS_foo...</span>);
         * </pre>
         * @return The object to set up a function for referrer table. (NotNull)
         */
        public HpSDRFunction<FavoriteLogCB, UserInfoCQ> derivedFavoriteLogList() {
            assertDerived("favoriteLogList");
            if (xhasSyncQyCall()) {
                xsyncQyCall().qy();
            } // for sync (for example, this in ColumnQuery)
            return new HpSDRFunction<FavoriteLogCB, UserInfoCQ>(_baseCB,
                    _qyCall.qy(),
                    new HpSDRSetupper<FavoriteLogCB, UserInfoCQ>() {
                        @Override
                        public void setup(final String function,
                                final SubQuery<FavoriteLogCB> subQuery,
                                final UserInfoCQ cq, final String aliasName,
                                final DerivedReferrerOption option) {
                            cq.xsderiveFavoriteLogList(function, subQuery,
                                    aliasName, option);
                        }
                    }, _dbmetaProvider);
        }

        /**
         * Prepare for (Specify)DerivedReferrer. <br />
         * {select max(FOO) from SEARCH_LOG where ...) as FOO_MAX} <br />
         * SEARCH_LOG by USER_ID, named 'searchLogList'.
         * <pre>
         * cb.specify().<span style="color: #FD4747">derivedSearchLogList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;SearchLogCB&gt;() {
         *     public void query(SearchLogCB subCB) {
         *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
         *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
         *     }
         * }, SearchLog.<span style="color: #FD4747">ALIAS_foo...</span>);
         * </pre>
         * @return The object to set up a function for referrer table. (NotNull)
         */
        public HpSDRFunction<SearchLogCB, UserInfoCQ> derivedSearchLogList() {
            assertDerived("searchLogList");
            if (xhasSyncQyCall()) {
                xsyncQyCall().qy();
            } // for sync (for example, this in ColumnQuery)
            return new HpSDRFunction<SearchLogCB, UserInfoCQ>(_baseCB,
                    _qyCall.qy(), new HpSDRSetupper<SearchLogCB, UserInfoCQ>() {
                        @Override
                        public void setup(final String function,
                                final SubQuery<SearchLogCB> subQuery,
                                final UserInfoCQ cq, final String aliasName,
                                final DerivedReferrerOption option) {
                            cq.xsderiveSearchLogList(function, subQuery,
                                    aliasName, option);
                        }
                    }, _dbmetaProvider);
        }

        /**
         * Prepare for (Specify)MyselfDerived (SubQuery).
         * @return The object to set up a function for myself table. (NotNull)
         */
        public HpSDRFunction<UserInfoCB, UserInfoCQ> myselfDerived() {
            assertDerived("myselfDerived");
            if (xhasSyncQyCall()) {
                xsyncQyCall().qy();
            } // for sync (for example, this in ColumnQuery)
            return new HpSDRFunction<UserInfoCB, UserInfoCQ>(_baseCB,
                    _qyCall.qy(), new HpSDRSetupper<UserInfoCB, UserInfoCQ>() {
                        @Override
                        public void setup(final String function,
                                final SubQuery<UserInfoCB> subQuery,
                                final UserInfoCQ cq, final String aliasName,
                                final DerivedReferrerOption option) {
                            cq.xsmyselfDerive(function, subQuery, aliasName,
                                    option);
                        }
                    }, _dbmetaProvider);
        }
    }

    // [DBFlute-0.9.5.3]
    // ===================================================================================
    //                                                                         ColumnQuery
    //                                                                         ===========
    /**
     * Set up column-query. {column1 = column2}
     * <pre>
     * <span style="color: #3F7E5E">// where FOO &lt; BAR</span>
     * cb.<span style="color: #FD4747">columnQuery</span>(new SpecifyQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     *     }
     * }).lessThan(new SpecifyQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     *     }
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param leftSpecifyQuery The specify-query for left column. (NotNull)
     * @return The object for setting up operand and right column. (NotNull)
     */
    public HpColQyOperand<UserInfoCB> columnQuery(
            final SpecifyQuery<UserInfoCB> leftSpecifyQuery) {
        return new HpColQyOperand<UserInfoCB>(new HpColQyHandler<UserInfoCB>() {
            @Override
            public HpCalculator handle(final SpecifyQuery<UserInfoCB> rightSp,
                    final String operand) {
                return xcolqy(xcreateColumnQueryCB(), xcreateColumnQueryCB(),
                        leftSpecifyQuery, rightSp, operand);
            }
        });
    }

    protected UserInfoCB xcreateColumnQueryCB() {
        final UserInfoCB cb = new UserInfoCB();
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
    public UserInfoCB dreamCruiseCB() {
        final UserInfoCB cb = new UserInfoCB();
        cb.xsetupForDreamCruise(this);
        return cb;
    }

    @Override
    protected ConditionBean xdoCreateDreamCruiseCB() {
        return dreamCruiseCB();
    }

    // [DBFlute-0.9.6.3]
    // ===================================================================================
    //                                                                        OrScopeQuery
    //                                                                        ============
    /**
     * Set up the query for or-scope. <br />
     * (Same-column-and-same-condition-key conditions are allowed in or-scope)
     * <pre>
     * <span style="color: #3F7E5E">// where (FOO = '...' or BAR = '...')</span>
     * cb.<span style="color: #FD4747">orScopeQuery</span>(new OrQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB orCB) {
     *         orCB.query().setFOO_Equal...
     *         orCB.query().setBAR_Equal...
     *     }
     * });
     * </pre>
     * @param orQuery The query for or-condition. (NotNull)
     */
    public void orScopeQuery(final OrQuery<UserInfoCB> orQuery) {
        xorSQ((UserInfoCB) this, orQuery);
    }

    /**
     * Set up the and-part of or-scope. <br />
     * (However nested or-scope query and as-or-split of like-search in and-part are unsupported)
     * <pre>
     * <span style="color: #3F7E5E">// where (FOO = '...' or (BAR = '...' and QUX = '...'))</span>
     * cb.<span style="color: #FD4747">orScopeQuery</span>(new OrQuery&lt;UserInfoCB&gt;() {
     *     public void query(UserInfoCB orCB) {
     *         orCB.query().setFOO_Equal...
     *         orCB.<span style="color: #FD4747">orScopeQueryAndPart</span>(new AndQuery&lt;UserInfoCB&gt;() {
     *             public void query(UserInfoCB andCB) {
     *                 andCB.query().setBar_...
     *                 andCB.query().setQux_...
     *             }
     *         });
     *     }
     * });
     * </pre>
     * @param andQuery The query for and-condition. (NotNull)
     */
    public void orScopeQueryAndPart(final AndQuery<UserInfoCB> andQuery) {
        xorSQAP((UserInfoCB) this, andQuery);
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
        final UserInfoCB cb;
        if (mainCB != null) {
            cb = (UserInfoCB) mainCB;
        } else {
            cb = new UserInfoCB();
        }
        specify().xsetSyncQyCall(new HpSpQyCall<UserInfoCQ>() {
            @Override
            public boolean has() {
                return true;
            }

            @Override
            public UserInfoCQ qy() {
                return cb.query();
            }
        });
    }

    // ===================================================================================
    //                                                                            Internal
    //                                                                            ========
    // very internal (for suppressing warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() {
        return UserInfoCB.class.getName();
    }

    protected String getConditionQueryClassNameInternally() {
        return UserInfoCQ.class.getName();
    }

    protected String getSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }

    protected String getConditionOptionClassNameInternally() {
        return ConditionOption.class.getName();
    }
}
