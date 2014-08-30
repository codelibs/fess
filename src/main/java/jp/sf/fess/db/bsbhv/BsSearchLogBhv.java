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

package jp.sf.fess.db.bsbhv;

import java.util.List;

import jp.sf.fess.db.bsbhv.loader.LoaderOfSearchLog;
import jp.sf.fess.db.bsentity.dbmeta.SearchLogDbm;
import jp.sf.fess.db.cbean.ClickLogCB;
import jp.sf.fess.db.cbean.SearchFieldLogCB;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.exbhv.SearchLogBhv;
import jp.sf.fess.db.exentity.ClickLog;
import jp.sf.fess.db.exentity.SearchFieldLog;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.AbstractBehaviorWritable;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.InsertOption;
import org.seasar.dbflute.bhv.LoadReferrerOption;
import org.seasar.dbflute.bhv.NestedReferrerListGateway;
import org.seasar.dbflute.bhv.QueryInsertSetupper;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;
import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.cbean.chelper.HpSLSExecutor;
import org.seasar.dbflute.cbean.chelper.HpSLSFunction;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.exception.DangerousResultSizeException;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.dbflute.exception.EntityDuplicatedException;
import org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException;
import org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException;
import org.seasar.dbflute.exception.SelectEntityConditionNotFoundException;
import org.seasar.dbflute.optional.OptionalEntity;
import org.seasar.dbflute.outsidesql.executor.OutsideSqlBasicExecutor;

/**
 * The behavior of SEARCH_LOG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SEARCH_WORD, REQUESTED_TIME, RESPONSE_TIME, HIT_COUNT, QUERY_OFFSET, QUERY_PAGE_SIZE, USER_AGENT, REFERER, CLIENT_IP, USER_SESSION_ID, ACCESS_TYPE, USER_ID
 *
 * [sequence]
 *
 *
 * [identity]
 *     ID
 *
 * [version-no]
 *
 *
 * [foreign table]
 *     USER_INFO
 *
 * [referrer table]
 *     CLICK_LOG, SEARCH_FIELD_LOG
 *
 * [foreign property]
 *     userInfo
 *
 * [referrer property]
 *     clickLogList, searchFieldLogList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsSearchLogBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:beginQueryPath*/
    public static final String PATH_selectHotSearchWord = "selectHotSearchWord";

    public static final String PATH_selectSearchFieldRanking = "selectSearchFieldRanking";

    public static final String PATH_selectRefererRanking = "selectRefererRanking";

    public static final String PATH_selectSearchWordRanking = "selectSearchWordRanking";

    public static final String PATH_selectClientIpRanking = "selectClientIpRanking";

    public static final String PATH_selectSearchQueryRanking = "selectSearchQueryRanking";

    public static final String PATH_selectSolrQueryRanking = "selectSolrQueryRanking";

    public static final String PATH_selectUserAgentRanking = "selectUserAgentRanking";

    /*df:endQueryPath*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    @Override
    public String getTableDbName() {
        return "SEARCH_LOG";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** {@inheritDoc} */
    @Override
    public DBMeta getDBMeta() {
        return SearchLogDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public SearchLogDbm getMyDBMeta() {
        return SearchLogDbm.getInstance();
    }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    @Override
    public SearchLog newEntity() {
        return new SearchLog();
    }

    /** {@inheritDoc} */
    @Override
    public SearchLogCB newConditionBean() {
        return new SearchLogCB();
    }

    /** @return The instance of new entity as my table type. (NotNull) */
    public SearchLog newMyEntity() {
        return new SearchLog();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public SearchLogCB newMyConditionBean() {
        return new SearchLogCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * int count = searchLogBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final SearchLogCB cb) {
        return facadeSelectCount(cb);
    }

    protected int facadeSelectCount(final SearchLogCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final SearchLogCB cb) { // called by selectCount(cb)
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final SearchLogCB cb) { // called by selectPage(cb)
        assertCBStateValid(cb);
        return delegateSelectCountPlainly(cb);
    }

    @Override
    protected int doReadCount(final ConditionBean cb) {
        return facadeSelectCount(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean. #beforejava8 <br />
     * <span style="color: #AD4747; font-size: 120%">The return might be null if no data, so you should have null check.</span> <br />
     * <span style="color: #AD4747; font-size: 120%">If the data always exists as your business rule, use selectEntityWithDeletedCheck().</span>
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * SearchLog searchLog = searchLogBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (searchLog != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = searchLog.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchLog selectEntity(final SearchLogCB cb) {
        return facadeSelectEntity(cb);
    }

    protected SearchLog facadeSelectEntity(final SearchLogCB cb) {
        return doSelectEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectEntity(
            final SearchLogCB cb, final Class<ENTITY> tp) {
        return helpSelectEntityInternally(cb, tp);
    }

    protected <ENTITY extends SearchLog> OptionalEntity<ENTITY> doSelectOptionalEntity(
            final SearchLogCB cb, final Class<ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    protected Entity doReadEntity(final ConditionBean cb) {
        return facadeSelectEntity(downcast(cb));
    }

    /**
     * Select the entity by the condition-bean with deleted check. <br />
     * <span style="color: #AD4747; font-size: 120%">If the data always exists as your business rule, this method is good.</span>
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * SearchLog searchLog = searchLogBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = searchLog.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchLog selectEntityWithDeletedCheck(final SearchLogCB cb) {
        return facadeSelectEntityWithDeletedCheck(cb);
    }

    protected SearchLog facadeSelectEntityWithDeletedCheck(final SearchLogCB cb) {
        return doSelectEntityWithDeletedCheck(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectEntityWithDeletedCheck(
            final SearchLogCB cb, final Class<ENTITY> tp) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", tp);
        return helpSelectEntityWithDeletedCheckInternally(cb, tp);
    }

    @Override
    protected Entity doReadEntityWithDeletedCheck(final ConditionBean cb) {
        return facadeSelectEntityWithDeletedCheck(downcast(cb));
    }

    /**
     * Select the entity by the primary-key value.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchLog selectByPKValue(final Long id) {
        return facadeSelectByPKValue(id);
    }

    protected SearchLog facadeSelectByPKValue(final Long id) {
        return doSelectByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectByPK(final Long id,
            final Class<ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected <ENTITY extends SearchLog> OptionalEntity<ENTITY> doSelectOptionalByPK(
            final Long id, final Class<ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchLog selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKWithDeletedCheck(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectByPKWithDeletedCheck(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), tp);
    }

    protected SearchLogCB xprepareCBAsPK(final Long id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;SearchLog&gt; searchLogList = searchLogBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (SearchLog searchLog : searchLogList) {
     *     ... = searchLog.get...();
     * }
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<SearchLog> selectList(final SearchLogCB cb) {
        return facadeSelectList(cb);
    }

    protected ListResultBean<SearchLog> facadeSelectList(final SearchLogCB cb) {
        return doSelectList(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> ListResultBean<ENTITY> doSelectList(
            final SearchLogCB cb, final Class<ENTITY> tp) {
        return helpSelectListInternally(cb, tp);
    }

    @Override
    protected ListResultBean<? extends Entity> doReadList(final ConditionBean cb) {
        return facadeSelectList(downcast(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean. <br />
     * (both count-select and paging-select are executed)
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;SearchLog&gt; page = searchLogBhv.<span style="color: #DD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (SearchLog searchLog : page) {
     *     ... = searchLog.get...();
     * }
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<SearchLog> selectPage(final SearchLogCB cb) {
        return facadeSelectPage(cb);
    }

    protected PagingResultBean<SearchLog> facadeSelectPage(final SearchLogCB cb) {
        return doSelectPage(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> PagingResultBean<ENTITY> doSelectPage(
            final SearchLogCB cb, final Class<ENTITY> tp) {
        return helpSelectPageInternally(cb, tp);
    }

    @Override
    protected PagingResultBean<? extends Entity> doReadPage(
            final ConditionBean cb) {
        return facadeSelectPage(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * searchLogBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;SearchLog&gt;() {
     *     public void handle(SearchLog entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @param entityRowHandler The handler of entity row of SearchLog. (NotNull)
     */
    public void selectCursor(final SearchLogCB cb,
            final EntityRowHandler<SearchLog> entityRowHandler) {
        facadeSelectCursor(cb, entityRowHandler);
    }

    protected void facadeSelectCursor(final SearchLogCB cb,
            final EntityRowHandler<SearchLog> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> void doSelectCursor(
            final SearchLogCB cb, final EntityRowHandler<ENTITY> handler,
            final Class<ENTITY> tp) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler", handler);
        assertObjectNotNull("entityType", tp);
        assertSpecifyDerivedReferrerEntityProperty(cb, tp);
        helpSelectCursorInternally(cb, handler, tp);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * searchLogBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(SearchLogCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> HpSLSFunction<SearchLogCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return facadeScalarSelect(resultType);
    }

    protected <RESULT> HpSLSFunction<SearchLogCB, RESULT> facadeScalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newConditionBean());
    }

    protected <RESULT, CB extends SearchLogCB> HpSLSFunction<CB, RESULT> doScalarSelect(
            final Class<RESULT> tp, final CB cb) {
        assertObjectNotNull("resultType", tp);
        assertCBStateValid(cb);
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        final HpSLSExecutor<CB, RESULT> executor = createHpSLSExecutor(); // variable to resolve generic
        return createSLSFunction(cb, tp, executor);
    }

    @Override
    protected <RESULT> HpSLSFunction<? extends ConditionBean, RESULT> doReadScalar(
            final Class<RESULT> tp) {
        return facadeScalarSelect(tp);
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    @Override
    protected Number doReadNextVal() {
        final String msg = "This table is NOT related to sequence: "
                + getTableDbName();
        throw new UnsupportedOperationException(msg);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * Load referrer by the the referrer loader. <br />
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * List&lt;Member&gt; memberList = memberBhv.selectList(cb);
     * memberBhv.<span style="color: #DD4747">load</span>(memberList, loader -&gt; {
     *     loader.<span style="color: #DD4747">loadPurchaseList</span>(purchaseCB -&gt; {
     *         purchaseCB.query().set...
     *         purchaseCB.query().addOrderBy_PurchasePrice_Desc();
     *     }); <span style="color: #3F7E5E">// you can also load nested referrer from here</span>
     *     <span style="color: #3F7E5E">//}).withNestedList(purchaseLoader -&gt {</span>
     *     <span style="color: #3F7E5E">//    purchaseLoader.loadPurchasePaymentList(...);</span>
     *     <span style="color: #3F7E5E">//});</span>
     *
     *     <span style="color: #3F7E5E">// you can also pull out foreign table and load its referrer</span>
     *     <span style="color: #3F7E5E">// (setupSelect of the foreign table should be called)</span>
     *     <span style="color: #3F7E5E">//loader.pulloutMemberStatus().loadMemberLoginList(...)</span>
     * }
     * for (Member member : memberList) {
     *     List&lt;Purchase&gt; purchaseList = member.<span style="color: #DD4747">getPurchaseList()</span>;
     *     for (Purchase purchase : purchaseList) {
     *         ...
     *     }
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has order by FK before callback.
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final List<SearchLog> searchLogList,
            final ReferrerLoaderHandler<LoaderOfSearchLog> handler) {
        xassLRArg(searchLogList, handler);
        handler.handle(new LoaderOfSearchLog().ready(searchLogList,
                _behaviorSelector));
    }

    /**
     * Load referrer of ${referrer.referrerJavaBeansRulePropertyName} by the referrer loader. <br />
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * Member member = memberBhv.selectEntityWithDeletedCheck(cb);
     * memberBhv.<span style="color: #DD4747">load</span>(member, loader -&gt; {
     *     loader.<span style="color: #DD4747">loadPurchaseList</span>(purchaseCB -&gt; {
     *         purchaseCB.query().set...
     *         purchaseCB.query().addOrderBy_PurchasePrice_Desc();
     *     }); <span style="color: #3F7E5E">// you can also load nested referrer from here</span>
     *     <span style="color: #3F7E5E">//}).withNestedList(purchaseLoader -&gt {</span>
     *     <span style="color: #3F7E5E">//    purchaseLoader.loadPurchasePaymentList(...);</span>
     *     <span style="color: #3F7E5E">//});</span>
     *
     *     <span style="color: #3F7E5E">// you can also pull out foreign table and load its referrer</span>
     *     <span style="color: #3F7E5E">// (setupSelect of the foreign table should be called)</span>
     *     <span style="color: #3F7E5E">//loader.pulloutMemberStatus().loadMemberLoginList(...)</span>
     * }
     * for (Member member : memberList) {
     *     List&lt;Purchase&gt; purchaseList = member.<span style="color: #DD4747">getPurchaseList()</span>;
     *     for (Purchase purchase : purchaseList) {
     *         ...
     *     }
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has order by FK before callback.
     * @param searchLog The entity of searchLog. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final SearchLog searchLog,
            final ReferrerLoaderHandler<LoaderOfSearchLog> handler) {
        xassLRArg(searchLog, handler);
        handler.handle(new LoaderOfSearchLog().ready(xnewLRAryLs(searchLog),
                _behaviorSelector));
    }

    /**
     * Load referrer of clickLogList by the set-upper of referrer. <br />
     * CLICK_LOG by SEARCH_ID, named 'clickLogList'.
     * <pre>
     * searchLogBhv.<span style="color: #DD4747">loadClickLogList</span>(searchLogList, new ConditionBeanSetupper&lt;ClickLogCB&gt;() {
     *     public void setup(ClickLogCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (SearchLog searchLog : searchLogList) {
     *     ... = searchLog.<span style="color: #DD4747">getClickLogList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setSearchId_InScope(pkList);
     * cb.query().addOrderBy_SearchId_Asc();
     * </pre>
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<ClickLog> loadClickLogList(
            final List<SearchLog> searchLogList,
            final ConditionBeanSetupper<ClickLogCB> setupper) {
        xassLRArg(searchLogList, setupper);
        return doLoadClickLogList(searchLogList,
                new LoadReferrerOption<ClickLogCB, ClickLog>().xinit(setupper));
    }

    /**
     * Load referrer of clickLogList by the set-upper of referrer. <br />
     * CLICK_LOG by SEARCH_ID, named 'clickLogList'.
     * <pre>
     * searchLogBhv.<span style="color: #DD4747">loadClickLogList</span>(searchLogList, new ConditionBeanSetupper&lt;ClickLogCB&gt;() {
     *     public void setup(ClickLogCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = searchLog.<span style="color: #DD4747">getClickLogList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setSearchId_InScope(pkList);
     * cb.query().addOrderBy_SearchId_Asc();
     * </pre>
     * @param searchLog The entity of searchLog. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<ClickLog> loadClickLogList(
            final SearchLog searchLog,
            final ConditionBeanSetupper<ClickLogCB> setupper) {
        xassLRArg(searchLog, setupper);
        return doLoadClickLogList(xnewLRLs(searchLog),
                new LoadReferrerOption<ClickLogCB, ClickLog>().xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param searchLog The entity of searchLog. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<ClickLog> loadClickLogList(
            final SearchLog searchLog,
            final LoadReferrerOption<ClickLogCB, ClickLog> loadReferrerOption) {
        xassLRArg(searchLog, loadReferrerOption);
        return loadClickLogList(xnewLRLs(searchLog), loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<ClickLog> loadClickLogList(
            final List<SearchLog> searchLogList,
            final LoadReferrerOption<ClickLogCB, ClickLog> loadReferrerOption) {
        xassLRArg(searchLogList, loadReferrerOption);
        if (searchLogList.isEmpty()) {
            return (NestedReferrerListGateway<ClickLog>) EMPTY_NREF_LGWAY;
        }
        return doLoadClickLogList(searchLogList, loadReferrerOption);
    }

    protected NestedReferrerListGateway<ClickLog> doLoadClickLogList(
            final List<SearchLog> searchLogList,
            final LoadReferrerOption<ClickLogCB, ClickLog> option) {
        return helpLoadReferrerInternally(searchLogList, option, "clickLogList");
    }

    /**
     * Load referrer of searchFieldLogList by the set-upper of referrer. <br />
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogList'.
     * <pre>
     * searchLogBhv.<span style="color: #DD4747">loadSearchFieldLogList</span>(searchLogList, new ConditionBeanSetupper&lt;SearchFieldLogCB&gt;() {
     *     public void setup(SearchFieldLogCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (SearchLog searchLog : searchLogList) {
     *     ... = searchLog.<span style="color: #DD4747">getSearchFieldLogList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setSearchId_InScope(pkList);
     * cb.query().addOrderBy_SearchId_Asc();
     * </pre>
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<SearchFieldLog> loadSearchFieldLogList(
            final List<SearchLog> searchLogList,
            final ConditionBeanSetupper<SearchFieldLogCB> setupper) {
        xassLRArg(searchLogList, setupper);
        return doLoadSearchFieldLogList(searchLogList,
                new LoadReferrerOption<SearchFieldLogCB, SearchFieldLog>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of searchFieldLogList by the set-upper of referrer. <br />
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogList'.
     * <pre>
     * searchLogBhv.<span style="color: #DD4747">loadSearchFieldLogList</span>(searchLogList, new ConditionBeanSetupper&lt;SearchFieldLogCB&gt;() {
     *     public void setup(SearchFieldLogCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = searchLog.<span style="color: #DD4747">getSearchFieldLogList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setSearchId_InScope(pkList);
     * cb.query().addOrderBy_SearchId_Asc();
     * </pre>
     * @param searchLog The entity of searchLog. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<SearchFieldLog> loadSearchFieldLogList(
            final SearchLog searchLog,
            final ConditionBeanSetupper<SearchFieldLogCB> setupper) {
        xassLRArg(searchLog, setupper);
        return doLoadSearchFieldLogList(xnewLRLs(searchLog),
                new LoadReferrerOption<SearchFieldLogCB, SearchFieldLog>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param searchLog The entity of searchLog. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<SearchFieldLog> loadSearchFieldLogList(
            final SearchLog searchLog,
            final LoadReferrerOption<SearchFieldLogCB, SearchFieldLog> loadReferrerOption) {
        xassLRArg(searchLog, loadReferrerOption);
        return loadSearchFieldLogList(xnewLRLs(searchLog), loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<SearchFieldLog> loadSearchFieldLogList(
            final List<SearchLog> searchLogList,
            final LoadReferrerOption<SearchFieldLogCB, SearchFieldLog> loadReferrerOption) {
        xassLRArg(searchLogList, loadReferrerOption);
        if (searchLogList.isEmpty()) {
            return (NestedReferrerListGateway<SearchFieldLog>) EMPTY_NREF_LGWAY;
        }
        return doLoadSearchFieldLogList(searchLogList, loadReferrerOption);
    }

    protected NestedReferrerListGateway<SearchFieldLog> doLoadSearchFieldLogList(
            final List<SearchLog> searchLogList,
            final LoadReferrerOption<SearchFieldLogCB, SearchFieldLog> option) {
        return helpLoadReferrerInternally(searchLogList, option,
                "searchFieldLogList");
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================
    /**
     * Pull out the list of foreign table 'UserInfo'.
     * @param searchLogList The list of searchLog. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<UserInfo> pulloutUserInfo(final List<SearchLog> searchLogList) {
        return helpPulloutInternally(searchLogList, "userInfo");
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param searchLogList The list of searchLog. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(final List<SearchLog> searchLogList) {
        return helpExtractListInternally(searchLogList, "id");
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * SearchLog searchLog = new SearchLog();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * searchLog.setFoo...(value);
     * searchLog.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//searchLog.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//searchLog.set...;</span>
     * searchLogBhv.<span style="color: #DD4747">insert</span>(searchLog);
     * ... = searchLog.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param searchLog The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final SearchLog searchLog) {
        doInsert(searchLog, null);
    }

    protected void doInsert(final SearchLog et,
            final InsertOption<SearchLogCB> op) {
        assertObjectNotNull("searchLog", et);
        prepareInsertOption(op);
        delegateInsert(et, op);
    }

    protected void prepareInsertOption(final InsertOption<SearchLogCB> op) {
        if (op == null) {
            return;
        }
        assertInsertOptionStatus(op);
        if (op.hasSpecifiedInsertColumn()) {
            op.resolveInsertColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    @Override
    protected void doCreate(final Entity et,
            final InsertOption<? extends ConditionBean> op) {
        doInsert(downcast(et), downcast(op));
    }

    /**
     * Update the entity modified-only. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * SearchLog searchLog = new SearchLog();
     * searchLog.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * searchLog.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//searchLog.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//searchLog.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * searchLog.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     searchLogBhv.<span style="color: #DD4747">update</span>(searchLog);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param searchLog The entity of update. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final SearchLog searchLog) {
        doUpdate(searchLog, null);
    }

    protected void doUpdate(final SearchLog et,
            final UpdateOption<SearchLogCB> op) {
        assertObjectNotNull("searchLog", et);
        prepareUpdateOption(op);
        helpUpdateInternally(et, op);
    }

    protected void prepareUpdateOption(final UpdateOption<SearchLogCB> op) {
        if (op == null) {
            return;
        }
        assertUpdateOptionStatus(op);
        if (op.hasSelfSpecification()) {
            op.resolveSelfSpecification(createCBForVaryingUpdate());
        }
        if (op.hasSpecifiedUpdateColumn()) {
            op.resolveUpdateColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    protected SearchLogCB createCBForVaryingUpdate() {
        final SearchLogCB cb = newConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected SearchLogCB createCBForSpecifiedUpdate() {
        final SearchLogCB cb = newConditionBean();
        cb.xsetupForSpecifiedUpdate();
        return cb;
    }

    @Override
    protected void doModify(final Entity et,
            final UpdateOption<? extends ConditionBean> op) {
        doUpdate(downcast(et), downcast(op));
    }

    @Override
    protected void doModifyNonstrict(final Entity et,
            final UpdateOption<? extends ConditionBean> op) {
        doModify(et, op);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param searchLog The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final SearchLog searchLog) {
        doInsertOrUpdate(searchLog, null, null);
    }

    protected void doInsertOrUpdate(final SearchLog et,
            final InsertOption<SearchLogCB> iop,
            final UpdateOption<SearchLogCB> uop) {
        assertObjectNotNull("searchLog", et);
        helpInsertOrUpdateInternally(et, iop, uop);
    }

    @Override
    protected void doCreateOrModify(final Entity et,
            final InsertOption<? extends ConditionBean> iop,
            final UpdateOption<? extends ConditionBean> uop) {
        doInsertOrUpdate(downcast(et), downcast(iop), downcast(uop));
    }

    @Override
    protected void doCreateOrModifyNonstrict(final Entity et,
            final InsertOption<? extends ConditionBean> iop,
            final UpdateOption<? extends ConditionBean> uop) {
        doCreateOrModify(et, iop, uop);
    }

    /**
     * Delete the entity. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * SearchLog searchLog = new SearchLog();
     * searchLog.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * searchLog.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     searchLogBhv.<span style="color: #DD4747">delete</span>(searchLog);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param searchLog The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final SearchLog searchLog) {
        doDelete(searchLog, null);
    }

    protected void doDelete(final SearchLog et,
            final DeleteOption<SearchLogCB> op) {
        assertObjectNotNull("searchLog", et);
        prepareDeleteOption(op);
        helpDeleteInternally(et, op);
    }

    protected void prepareDeleteOption(final DeleteOption<SearchLogCB> op) {
        if (op != null) {
            assertDeleteOptionStatus(op);
        }
    }

    @Override
    protected void doRemove(final Entity et,
            final DeleteOption<? extends ConditionBean> op) {
        doDelete(downcast(et), downcast(op));
    }

    @Override
    protected void doRemoveNonstrict(final Entity et,
            final DeleteOption<? extends ConditionBean> op) {
        doRemove(et, op);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch-insert the entity list modified-only of same-set columns. (DefaultConstraintsEnabled) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <p><span style="color: #DD4747; font-size: 120%">The columns of least common multiple are registered like this:</span></p>
     * <pre>
     * for (... : ...) {
     *     SearchLog searchLog = new SearchLog();
     *     searchLog.setFooName("foo");
     *     if (...) {
     *         searchLog.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     searchLogList.add(searchLog);
     * }
     * searchLogBhv.<span style="color: #DD4747">batchInsert</span>(searchLogList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<SearchLog> searchLogList) {
        return doBatchInsert(searchLogList, null);
    }

    protected int[] doBatchInsert(final List<SearchLog> ls,
            final InsertOption<SearchLogCB> op) {
        assertObjectNotNull("searchLogList", ls);
        InsertOption<SearchLogCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainInsertOption();
        }
        prepareBatchInsertOption(ls, rlop); // required
        return delegateBatchInsert(ls, rlop);
    }

    protected void prepareBatchInsertOption(final List<SearchLog> ls,
            final InsertOption<SearchLogCB> op) {
        op.xallowInsertColumnModifiedPropertiesFragmented();
        op.xacceptInsertColumnModifiedPropertiesIfNeeds(ls);
        prepareInsertOption(op);
    }

    @Override
    protected int[] doLumpCreate(final List<Entity> ls,
            final InsertOption<? extends ConditionBean> op) {
        return doBatchInsert(downcast(ls), downcast(op));
    }

    /**
     * Batch-update the entity list modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     SearchLog searchLog = new SearchLog();
     *     searchLog.setFooName("foo");
     *     if (...) {
     *         searchLog.setFooPrice(123);
     *     } else {
     *         searchLog.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//searchLog.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     searchLogList.add(searchLog);
     * }
     * searchLogBhv.<span style="color: #DD4747">batchUpdate</span>(searchLogList);
     * </pre>
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(final List<SearchLog> searchLogList) {
        return doBatchUpdate(searchLogList, null);
    }

    protected int[] doBatchUpdate(final List<SearchLog> ls,
            final UpdateOption<SearchLogCB> op) {
        assertObjectNotNull("searchLogList", ls);
        UpdateOption<SearchLogCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainUpdateOption();
        }
        prepareBatchUpdateOption(ls, rlop); // required
        return delegateBatchUpdate(ls, rlop);
    }

    protected void prepareBatchUpdateOption(final List<SearchLog> ls,
            final UpdateOption<SearchLogCB> op) {
        op.xacceptUpdateColumnModifiedPropertiesIfNeeds(ls);
        prepareUpdateOption(op);
    }

    @Override
    protected int[] doLumpModify(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> op) {
        return doBatchUpdate(downcast(ls), downcast(op));
    }

    /**
     * Batch-update the entity list specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * searchLogBhv.<span style="color: #DD4747">batchUpdate</span>(searchLogList, new SpecifyQuery<SearchLogCB>() {
     *     public void specify(SearchLogCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * searchLogBhv.<span style="color: #DD4747">batchUpdate</span>(searchLogList, new SpecifyQuery<SearchLogCB>() {
     *     public void specify(SearchLogCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(final List<SearchLog> searchLogList,
            final SpecifyQuery<SearchLogCB> updateColumnSpec) {
        return doBatchUpdate(searchLogList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> op) {
        return doLumpModify(ls, op);
    }

    /**
     * Batch-delete the entity list. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(final List<SearchLog> searchLogList) {
        return doBatchDelete(searchLogList, null);
    }

    protected int[] doBatchDelete(final List<SearchLog> ls,
            final DeleteOption<SearchLogCB> op) {
        assertObjectNotNull("searchLogList", ls);
        prepareDeleteOption(op);
        return delegateBatchDelete(ls, op);
    }

    @Override
    protected int[] doLumpRemove(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> op) {
        return doBatchDelete(downcast(ls), downcast(op));
    }

    @Override
    protected int[] doLumpRemoveNonstrict(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> op) {
        return doLumpRemove(ls, op);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * searchLogBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;SearchLog, SearchLogCB&gt;() {
     *     public ConditionBean setup(searchLog entity, SearchLogCB intoCB) {
     *         FooCB cb = FooCB();
     *         cb.setupSelect_Bar();
     *
     *         <span style="color: #3F7E5E">// mapping</span>
     *         intoCB.specify().columnMyName().mappedFrom(cb.specify().columnFooName());
     *         intoCB.specify().columnMyCount().mappedFrom(cb.specify().columnFooCount());
     *         intoCB.specify().columnMyDate().mappedFrom(cb.specify().specifyBar().columnBarDate());
     *         entity.setMyFixedValue("foo"); <span style="color: #3F7E5E">// fixed value</span>
     *         <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     *         <span style="color: #3F7E5E">//entity.setRegisterUser(value);</span>
     *         <span style="color: #3F7E5E">//entity.set...;</span>
     *         <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     *         <span style="color: #3F7E5E">//entity.setVersionNo(value);</span>
     *
     *         return cb;
     *     }
     * });
     * </pre>
     * @param setupper The setup-per of query-insert. (NotNull)
     * @return The inserted count.
     */
    public int queryInsert(
            final QueryInsertSetupper<SearchLog, SearchLogCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<SearchLog, SearchLogCB> sp,
            final InsertOption<SearchLogCB> op) {
        assertObjectNotNull("setupper", sp);
        prepareInsertOption(op);
        final SearchLog et = newEntity();
        final SearchLogCB cb = createCBForQueryInsert();
        return delegateQueryInsert(et, cb, sp.setup(et, cb), op);
    }

    protected SearchLogCB createCBForQueryInsert() {
        final SearchLogCB cb = newConditionBean();
        cb.xsetupForQueryInsert();
        return cb;
    }

    @Override
    protected int doRangeCreate(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            final InsertOption<? extends ConditionBean> op) {
        return doQueryInsert(downcast(setupper), downcast(op));
    }

    /**
     * Update the several entities by query non-strictly modified-only. (NonExclusiveControl)
     * <pre>
     * SearchLog searchLog = new SearchLog();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//searchLog.setPK...(value);</span>
     * searchLog.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//searchLog.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//searchLog.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//searchLog.setVersionNo(value);</span>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * searchLogBhv.<span style="color: #DD4747">queryUpdate</span>(searchLog, cb);
     * </pre>
     * @param searchLog The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final SearchLog searchLog, final SearchLogCB cb) {
        return doQueryUpdate(searchLog, cb, null);
    }

    protected int doQueryUpdate(final SearchLog et, final SearchLogCB cb,
            final UpdateOption<SearchLogCB> op) {
        assertObjectNotNull("searchLog", et);
        assertCBStateValid(cb);
        prepareUpdateOption(op);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(et,
                cb, op) : 0;
    }

    @Override
    protected int doRangeModify(final Entity et, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> op) {
        return doQueryUpdate(downcast(et), downcast(cb), downcast(op));
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * searchLogBhv.<span style="color: #DD4747">queryDelete</span>(searchLog, cb);
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final SearchLogCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final SearchLogCB cb,
            final DeleteOption<SearchLogCB> op) {
        assertCBStateValid(cb);
        prepareDeleteOption(op);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                op) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> op) {
        return doQueryDelete(downcast(cb), downcast(op));
    }

    // ===================================================================================
    //                                                                      Varying Update
    //                                                                      ==============
    // -----------------------------------------------------
    //                                         Entity Update
    //                                         -------------
    /**
     * Insert the entity with varying requests. <br />
     * For example, disableCommonColumnAutoSetup(), disablePrimaryKeyIdentity(). <br />
     * Other specifications are same as insert(entity).
     * <pre>
     * SearchLog searchLog = new SearchLog();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * searchLog.setFoo...(value);
     * searchLog.setBar...(value);
     * InsertOption<SearchLogCB> option = new InsertOption<SearchLogCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * searchLogBhv.<span style="color: #DD4747">varyingInsert</span>(searchLog, option);
     * ... = searchLog.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param searchLog The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final SearchLog searchLog,
            final InsertOption<SearchLogCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(searchLog, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * SearchLog searchLog = new SearchLog();
     * searchLog.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * searchLog.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * searchLog.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;SearchLogCB&gt; option = new UpdateOption&lt;SearchLogCB&gt;();
     *     option.self(new SpecifyQuery&lt;SearchLogCB&gt;() {
     *         public void specify(SearchLogCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     searchLogBhv.<span style="color: #DD4747">varyingUpdate</span>(searchLog, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param searchLog The entity of update. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final SearchLog searchLog,
            final UpdateOption<SearchLogCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(searchLog, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param searchLog The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final SearchLog searchLog,
            final InsertOption<SearchLogCB> insertOption,
            final UpdateOption<SearchLogCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdate(searchLog, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param searchLog The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final SearchLog searchLog,
            final DeleteOption<SearchLogCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(searchLog, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(final List<SearchLog> searchLogList,
            final InsertOption<SearchLogCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(searchLogList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(final List<SearchLog> searchLogList,
            final UpdateOption<SearchLogCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(searchLogList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(final List<SearchLog> searchLogList,
            final DeleteOption<SearchLogCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(searchLogList, option);
    }

    // -----------------------------------------------------
    //                                          Query Update
    //                                          ------------
    /**
     * Insert the several entities by query with varying requests (modified-only for fixed value). <br />
     * For example, disableCommonColumnAutoSetup(), disablePrimaryKeyIdentity(). <br />
     * Other specifications are same as queryInsert(entity, setupper).
     * @param setupper The setup-per of query-insert. (NotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The inserted count.
     */
    public int varyingQueryInsert(
            final QueryInsertSetupper<SearchLog, SearchLogCB> setupper,
            final InsertOption<SearchLogCB> option) {
        assertInsertOptionNotNull(option);
        return doQueryInsert(setupper, option);
    }

    /**
     * Update the several entities by query with varying requests non-strictly modified-only. {NonExclusiveControl} <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), allowNonQueryUpdate(). <br />
     * Other specifications are same as queryUpdate(entity, cb).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * SearchLog searchLog = new SearchLog();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//searchLog.setPK...(value);</span>
     * searchLog.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//searchLog.setVersionNo(value);</span>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;SearchLogCB&gt; option = new UpdateOption&lt;SearchLogCB&gt;();
     * option.self(new SpecifyQuery&lt;SearchLogCB&gt;() {
     *     public void specify(SearchLogCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * searchLogBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(searchLog, cb, option);
     * </pre>
     * @param searchLog The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final SearchLog searchLog,
            final SearchLogCB cb, final UpdateOption<SearchLogCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(searchLog, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final SearchLogCB cb,
            final DeleteOption<SearchLogCB> option) {
        assertDeleteOptionNotNull(option);
        return doQueryDelete(cb, option);
    }

    // ===================================================================================
    //                                                                          OutsideSql
    //                                                                          ==========
    /**
     * Prepare the basic executor of outside-SQL to execute it. <br />
     * The invoker of behavior command should be not null when you call this method.
     * <pre>
     * You can use the methods for outside-SQL are as follows:
     * {Basic}
     *   o selectList()
     *   o execute()
     *   o call()
     *
     * {Entity}
     *   o entityHandling().selectEntity()
     *   o entityHandling().selectEntityWithDeletedCheck()
     *
     * {Paging}
     *   o autoPaging().selectList()
     *   o autoPaging().selectPage()
     *   o manualPaging().selectList()
     *   o manualPaging().selectPage()
     *
     * {Cursor}
     *   o cursorHandling().selectCursor()
     *
     * {Option}
     *   o dynamicBinding().selectList()
     *   o removeBlockComment().selectList()
     *   o removeLineComment().selectList()
     *   o formatSql().selectList()
     * </pre>
     * @return The basic executor of outside-SQL. (NotNull)
     */
    public OutsideSqlBasicExecutor<SearchLogBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected Class<SearchLog> typeOfSelectedEntity() {
        return SearchLog.class;
    }

    protected SearchLog downcast(final Entity et) {
        return helpEntityDowncastInternally(et, SearchLog.class);
    }

    protected SearchLogCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, SearchLogCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<SearchLog> downcast(final List<? extends Entity> ls) {
        return (List<SearchLog>) ls;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<SearchLogCB> downcast(
            final InsertOption<? extends ConditionBean> op) {
        return (InsertOption<SearchLogCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<SearchLogCB> downcast(
            final UpdateOption<? extends ConditionBean> op) {
        return (UpdateOption<SearchLogCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<SearchLogCB> downcast(
            final DeleteOption<? extends ConditionBean> op) {
        return (DeleteOption<SearchLogCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<SearchLog, SearchLogCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> sp) {
        return (QueryInsertSetupper<SearchLog, SearchLogCB>) sp;
    }
}
