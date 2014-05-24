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

import jp.sf.fess.db.bsentity.dbmeta.SearchLogDbm;
import jp.sf.fess.db.cbean.ClickLogCB;
import jp.sf.fess.db.cbean.SearchFieldLogCB;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.exbhv.ClickLogBhv;
import jp.sf.fess.db.exbhv.SearchFieldLogBhv;
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
import org.seasar.dbflute.bhv.QueryInsertSetupper;
import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.dbmeta.DBMeta;
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
    public static final String PATH_selectClientIpRanking = "selectClientIpRanking";

    public static final String PATH_selectHotSearchWord = "selectHotSearchWord";

    public static final String PATH_selectRefererRanking = "selectRefererRanking";

    public static final String PATH_selectSearchFieldRanking = "selectSearchFieldRanking";

    public static final String PATH_selectSearchQueryRanking = "selectSearchQueryRanking";

    public static final String PATH_selectSearchWordRanking = "selectSearchWordRanking";

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
    /** @return The instance of DBMeta. (NotNull) */
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
    public Entity newEntity() {
        return newMyEntity();
    }

    /** {@inheritDoc} */
    @Override
    public ConditionBean newConditionBean() {
        return newMyConditionBean();
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
     * int count = searchLogBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final SearchLogCB cb) {
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
        return selectCount(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * SearchLog searchLog = searchLogBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (searchLog != null) {
     *     ... = searchLog.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchLog selectEntity(final SearchLogCB cb) {
        return doSelectEntity(cb, SearchLog.class);
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectEntity(
            final SearchLogCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(cb, entityType,
                new InternalSelectEntityCallback<ENTITY, SearchLogCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchLogCB cb, final Class<ENTITY> entityType) {
                        return doSelectList(cb, entityType);
                    }
                });
    }

    @Override
    protected Entity doReadEntity(final ConditionBean cb) {
        return selectEntity(downcast(cb));
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * SearchLog searchLog = searchLogBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = searchLog.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchLog selectEntityWithDeletedCheck(final SearchLogCB cb) {
        return doSelectEntityWithDeletedCheck(cb, SearchLog.class);
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectEntityWithDeletedCheck(
            final SearchLogCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, SearchLogCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchLogCB cb, final Class<ENTITY> entityType) {
                        return doSelectList(cb, entityType);
                    }
                });
    }

    @Override
    protected Entity doReadEntityWithDeletedCheck(final ConditionBean cb) {
        return selectEntityWithDeletedCheck(downcast(cb));
    }

    /**
     * Select the entity by the primary-key value.
     * @param id The one of primary key. (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchLog selectByPKValue(final Long id) {
        return doSelectByPKValue(id, SearchLog.class);
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectByPKValue(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntity(buildPKCB(id), entityType);
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * @param id The one of primary key. (NotNull)
     * @return The entity selected by the PK. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchLog selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, SearchLog.class);
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private SearchLogCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final SearchLogCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
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
     * ListResultBean&lt;SearchLog&gt; searchLogList = searchLogBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (SearchLog searchLog : searchLogList) {
     *     ... = searchLog.get...();
     * }
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<SearchLog> selectList(final SearchLogCB cb) {
        return doSelectList(cb, SearchLog.class);
    }

    protected <ENTITY extends SearchLog> ListResultBean<ENTITY> doSelectList(
            final SearchLogCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, SearchLogCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchLogCB cb, final Class<ENTITY> entityType) {
                        return delegateSelectList(cb, entityType);
                    }
                });
    }

    @Override
    protected ListResultBean<? extends Entity> doReadList(final ConditionBean cb) {
        return selectList(downcast(cb));
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
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;SearchLog&gt; page = searchLogBhv.<span style="color: #FD4747">selectPage</span>(cb);
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
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<SearchLog> selectPage(final SearchLogCB cb) {
        return doSelectPage(cb, SearchLog.class);
    }

    protected <ENTITY extends SearchLog> PagingResultBean<ENTITY> doSelectPage(
            final SearchLogCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, SearchLogCB>() {
                    @Override
                    public int callbackSelectCount(final SearchLogCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchLogCB cb, final Class<ENTITY> entityType) {
                        return doSelectList(cb, entityType);
                    }
                });
    }

    @Override
    protected PagingResultBean<? extends Entity> doReadPage(
            final ConditionBean cb) {
        return selectPage(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * searchLogBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;SearchLog&gt;() {
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
        doSelectCursor(cb, entityRowHandler, SearchLog.class);
    }

    protected <ENTITY extends SearchLog> void doSelectCursor(
            final SearchLogCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<SearchLog>", entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(cb, entityRowHandler, entityType,
                new InternalSelectCursorCallback<ENTITY, SearchLogCB>() {
                    @Override
                    public void callbackSelectCursor(final SearchLogCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchLogCB cb, final Class<ENTITY> entityType) {
                        return doSelectList(cb, entityType);
                    }
                });
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * searchLogBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(SearchLogCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<SearchLogCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends SearchLogCB> SLFunction<CB, RESULT> doScalarSelect(
            final Class<RESULT> resultType, final CB cb) {
        assertObjectNotNull("resultType", resultType);
        assertCBStateValid(cb);
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        return new SLFunction<CB, RESULT>(cb, resultType);
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
     * {Refer to overload method that has an argument of the list of entity.}
     * @param searchLog The entity of searchLog. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadClickLogList(final SearchLog searchLog,
            final ConditionBeanSetupper<ClickLogCB> conditionBeanSetupper) {
        xassLRArg(searchLog, conditionBeanSetupper);
        loadClickLogList(xnewLRLs(searchLog), conditionBeanSetupper);
    }

    /**
     * Load referrer of clickLogList with the set-upper for condition-bean of referrer. <br />
     * CLICK_LOG by SEARCH_ID, named 'clickLogList'.
     * <pre>
     * searchLogBhv.<span style="color: #FD4747">loadClickLogList</span>(searchLogList, new ConditionBeanSetupper&lt;ClickLogCB&gt;() {
     *     public void setup(ClickLogCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (SearchLog searchLog : searchLogList) {
     *     ... = searchLog.<span style="color: #FD4747">getClickLogList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setSearchId_InScope(pkList);
     * cb.query().addOrderBy_SearchId_Asc();
     * </pre>
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadClickLogList(final List<SearchLog> searchLogList,
            final ConditionBeanSetupper<ClickLogCB> conditionBeanSetupper) {
        xassLRArg(searchLogList, conditionBeanSetupper);
        loadClickLogList(searchLogList,
                new LoadReferrerOption<ClickLogCB, ClickLog>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param searchLog The entity of searchLog. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadClickLogList(final SearchLog searchLog,
            final LoadReferrerOption<ClickLogCB, ClickLog> loadReferrerOption) {
        xassLRArg(searchLog, loadReferrerOption);
        loadClickLogList(xnewLRLs(searchLog), loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadClickLogList(final List<SearchLog> searchLogList,
            final LoadReferrerOption<ClickLogCB, ClickLog> loadReferrerOption) {
        xassLRArg(searchLogList, loadReferrerOption);
        if (searchLogList.isEmpty()) {
            return;
        }
        final ClickLogBhv referrerBhv = xgetBSFLR().select(ClickLogBhv.class);
        helpLoadReferrerInternally(
                searchLogList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<SearchLog, Long, ClickLogCB, ClickLog>() {
                    @Override
                    public Long getPKVal(final SearchLog e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final SearchLog e,
                            final List<ClickLog> ls) {
                        e.setClickLogList(ls);
                    }

                    @Override
                    public ClickLogCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final ClickLogCB cb, final List<Long> ls) {
                        cb.query().setSearchId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final ClickLogCB cb) {
                        cb.query().addOrderBy_SearchId_Asc();
                    }

                    @Override
                    public void spFKCol(final ClickLogCB cb) {
                        cb.specify().columnSearchId();
                    }

                    @Override
                    public List<ClickLog> selRfLs(final ClickLogCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final ClickLog e) {
                        return e.getSearchId();
                    }

                    @Override
                    public void setlcEt(final ClickLog re, final SearchLog le) {
                        re.setSearchLog(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "clickLogList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param searchLog The entity of searchLog. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadSearchFieldLogList(final SearchLog searchLog,
            final ConditionBeanSetupper<SearchFieldLogCB> conditionBeanSetupper) {
        xassLRArg(searchLog, conditionBeanSetupper);
        loadSearchFieldLogList(xnewLRLs(searchLog), conditionBeanSetupper);
    }

    /**
     * Load referrer of searchFieldLogList with the set-upper for condition-bean of referrer. <br />
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogList'.
     * <pre>
     * searchLogBhv.<span style="color: #FD4747">loadSearchFieldLogList</span>(searchLogList, new ConditionBeanSetupper&lt;SearchFieldLogCB&gt;() {
     *     public void setup(SearchFieldLogCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (SearchLog searchLog : searchLogList) {
     *     ... = searchLog.<span style="color: #FD4747">getSearchFieldLogList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setSearchId_InScope(pkList);
     * cb.query().addOrderBy_SearchId_Asc();
     * </pre>
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadSearchFieldLogList(final List<SearchLog> searchLogList,
            final ConditionBeanSetupper<SearchFieldLogCB> conditionBeanSetupper) {
        xassLRArg(searchLogList, conditionBeanSetupper);
        loadSearchFieldLogList(searchLogList,
                new LoadReferrerOption<SearchFieldLogCB, SearchFieldLog>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param searchLog The entity of searchLog. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadSearchFieldLogList(
            final SearchLog searchLog,
            final LoadReferrerOption<SearchFieldLogCB, SearchFieldLog> loadReferrerOption) {
        xassLRArg(searchLog, loadReferrerOption);
        loadSearchFieldLogList(xnewLRLs(searchLog), loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param searchLogList The entity list of searchLog. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadSearchFieldLogList(
            final List<SearchLog> searchLogList,
            final LoadReferrerOption<SearchFieldLogCB, SearchFieldLog> loadReferrerOption) {
        xassLRArg(searchLogList, loadReferrerOption);
        if (searchLogList.isEmpty()) {
            return;
        }
        final SearchFieldLogBhv referrerBhv = xgetBSFLR().select(
                SearchFieldLogBhv.class);
        helpLoadReferrerInternally(
                searchLogList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<SearchLog, Long, SearchFieldLogCB, SearchFieldLog>() {
                    @Override
                    public Long getPKVal(final SearchLog e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final SearchLog e,
                            final List<SearchFieldLog> ls) {
                        e.setSearchFieldLogList(ls);
                    }

                    @Override
                    public SearchFieldLogCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final SearchFieldLogCB cb,
                            final List<Long> ls) {
                        cb.query().setSearchId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final SearchFieldLogCB cb) {
                        cb.query().addOrderBy_SearchId_Asc();
                    }

                    @Override
                    public void spFKCol(final SearchFieldLogCB cb) {
                        cb.specify().columnSearchId();
                    }

                    @Override
                    public List<SearchFieldLog> selRfLs(
                            final SearchFieldLogCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final SearchFieldLog e) {
                        return e.getSearchId();
                    }

                    @Override
                    public void setlcEt(final SearchFieldLog re,
                            final SearchLog le) {
                        re.setSearchLog(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "searchFieldLogList";
                    }
                });
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
        return helpPulloutInternally(searchLogList,
                new InternalPulloutCallback<SearchLog, UserInfo>() {
                    @Override
                    public UserInfo getFr(final SearchLog e) {
                        return e.getUserInfo();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final UserInfo e,
                            final List<SearchLog> ls) {
                        e.setSearchLogList(ls);
                    }
                });
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
        return helpExtractListInternally(searchLogList,
                new InternalExtractCallback<SearchLog, Long>() {
                    @Override
                    public Long getCV(final SearchLog e) {
                        return e.getId();
                    }
                });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity. (DefaultConstraintsEnabled)
     * <pre>
     * SearchLog searchLog = new SearchLog();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * searchLog.setFoo...(value);
     * searchLog.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//searchLog.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//searchLog.set...;</span>
     * searchLogBhv.<span style="color: #FD4747">insert</span>(searchLog);
     * ... = searchLog.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param searchLog The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final SearchLog searchLog) {
        doInsert(searchLog, null);
    }

    protected void doInsert(final SearchLog searchLog,
            final InsertOption<SearchLogCB> option) {
        assertObjectNotNull("searchLog", searchLog);
        prepareInsertOption(option);
        delegateInsert(searchLog, option);
    }

    protected void prepareInsertOption(final InsertOption<SearchLogCB> option) {
        if (option == null) {
            return;
        }
        assertInsertOptionStatus(option);
    }

    @Override
    protected void doCreate(final Entity entity,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            insert(downcast(entity));
        } else {
            varyingInsert(downcast(entity), downcast(option));
        }
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
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * searchLog.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     searchLogBhv.<span style="color: #FD4747">update</span>(searchLog);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param searchLog The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final SearchLog searchLog) {
        doUpdate(searchLog, null);
    }

    protected void doUpdate(final SearchLog searchLog,
            final UpdateOption<SearchLogCB> option) {
        assertObjectNotNull("searchLog", searchLog);
        prepareUpdateOption(option);
        helpUpdateInternally(searchLog,
                new InternalUpdateCallback<SearchLog>() {
                    @Override
                    public int callbackDelegateUpdate(final SearchLog entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(final UpdateOption<SearchLogCB> option) {
        if (option == null) {
            return;
        }
        assertUpdateOptionStatus(option);
        if (option.hasSelfSpecification()) {
            option.resolveSelfSpecification(createCBForVaryingUpdate());
        }
        if (option.hasSpecifiedUpdateColumn()) {
            option.resolveUpdateColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    protected SearchLogCB createCBForVaryingUpdate() {
        final SearchLogCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected SearchLogCB createCBForSpecifiedUpdate() {
        final SearchLogCB cb = newMyConditionBean();
        cb.xsetupForSpecifiedUpdate();
        return cb;
    }

    @Override
    protected void doModify(final Entity entity,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            update(downcast(entity));
        } else {
            varyingUpdate(downcast(entity), downcast(option));
        }
    }

    @Override
    protected void doModifyNonstrict(final Entity entity,
            final UpdateOption<? extends ConditionBean> option) {
        doModify(entity, option);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param searchLog The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final SearchLog searchLog) {
        doInesrtOrUpdate(searchLog, null, null);
    }

    protected void doInesrtOrUpdate(final SearchLog searchLog,
            final InsertOption<SearchLogCB> insertOption,
            final UpdateOption<SearchLogCB> updateOption) {
        helpInsertOrUpdateInternally(searchLog,
                new InternalInsertOrUpdateCallback<SearchLog, SearchLogCB>() {
                    @Override
                    public void callbackInsert(final SearchLog entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final SearchLog entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public SearchLogCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final SearchLogCB cb) {
                        return selectCount(cb);
                    }
                });
    }

    @Override
    protected void doCreateOrModify(final Entity entity,
            InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption) {
        if (insertOption == null && updateOption == null) {
            insertOrUpdate(downcast(entity));
        } else {
            insertOption = insertOption == null ? new InsertOption<SearchLogCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<SearchLogCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    @Override
    protected void doCreateOrModifyNonstrict(final Entity entity,
            final InsertOption<? extends ConditionBean> insertOption,
            final UpdateOption<? extends ConditionBean> updateOption) {
        doCreateOrModify(entity, insertOption, updateOption);
    }

    /**
     * Delete the entity. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * SearchLog searchLog = new SearchLog();
     * searchLog.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * searchLog.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     searchLogBhv.<span style="color: #FD4747">delete</span>(searchLog);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param searchLog The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final SearchLog searchLog) {
        doDelete(searchLog, null);
    }

    protected void doDelete(final SearchLog searchLog,
            final DeleteOption<SearchLogCB> option) {
        assertObjectNotNull("searchLog", searchLog);
        prepareDeleteOption(option);
        helpDeleteInternally(searchLog,
                new InternalDeleteCallback<SearchLog>() {
                    @Override
                    public int callbackDelegateDelete(final SearchLog entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(final DeleteOption<SearchLogCB> option) {
        if (option == null) {
            return;
        }
        assertDeleteOptionStatus(option);
    }

    @Override
    protected void doRemove(final Entity entity,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            delete(downcast(entity));
        } else {
            varyingDelete(downcast(entity), downcast(option));
        }
    }

    @Override
    protected void doRemoveNonstrict(final Entity entity,
            final DeleteOption<? extends ConditionBean> option) {
        doRemove(entity, option);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch-insert the entity list. (DefaultConstraintsDisabled) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <p><span style="color: #FD4747; font-size: 120%">Attention, all columns are insert target. (so default constraints are not available)</span></p>
     * And if the table has an identity, entities after the process don't have incremented values.
     * When you use the (normal) insert(), an entity after the process has an incremented value.
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<SearchLog> searchLogList) {
        return doBatchInsert(searchLogList, null);
    }

    protected int[] doBatchInsert(final List<SearchLog> searchLogList,
            final InsertOption<SearchLogCB> option) {
        assertObjectNotNull("searchLogList", searchLogList);
        prepareInsertOption(option);
        return delegateBatchInsert(searchLogList, option);
    }

    @Override
    protected int[] doLumpCreate(final List<Entity> ls,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchInsert(downcast(ls));
        } else {
            return varyingBatchInsert(downcast(ls), downcast(option));
        }
    }

    /**
     * Batch-update the entity list. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747; font-size: 140%">Attention, all columns are update target. {NOT modified only}</span> <br />
     * So you should the other batchUpdate() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * searchLogBhv.<span style="color: #FD4747">batchUpdate</span>(searchLogList, new SpecifyQuery<SearchLogCB>() {
     *     public void specify(SearchLogCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(final List<SearchLog> searchLogList) {
        return doBatchUpdate(searchLogList, null);
    }

    protected int[] doBatchUpdate(final List<SearchLog> searchLogList,
            final UpdateOption<SearchLogCB> option) {
        assertObjectNotNull("searchLogList", searchLogList);
        prepareBatchUpdateOption(searchLogList, option);
        return delegateBatchUpdate(searchLogList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<SearchLog> searchLogList,
            final UpdateOption<SearchLogCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(searchLogList);
        //}
    }

    @Override
    protected int[] doLumpModify(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchUpdate(downcast(ls));
        } else {
            return varyingBatchUpdate(downcast(ls), downcast(option));
        }
    }

    /**
     * Batch-update the entity list. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * searchLogBhv.<span style="color: #FD4747">batchUpdate</span>(searchLogList, new SpecifyQuery<SearchLogCB>() {
     *     public void specify(SearchLogCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * searchLogBhv.<span style="color: #FD4747">batchUpdate</span>(searchLogList, new SpecifyQuery<SearchLogCB>() {
     *     public void specify(SearchLogCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
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
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(final List<SearchLog> searchLogList,
            final SpecifyQuery<SearchLogCB> updateColumnSpec) {
        return doBatchUpdate(searchLogList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> option) {
        return doLumpModify(ls, option);
    }

    /**
     * Batch-delete the entity list. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param searchLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(final List<SearchLog> searchLogList) {
        return doBatchDelete(searchLogList, null);
    }

    protected int[] doBatchDelete(final List<SearchLog> searchLogList,
            final DeleteOption<SearchLogCB> option) {
        assertObjectNotNull("searchLogList", searchLogList);
        prepareDeleteOption(option);
        return delegateBatchDelete(searchLogList, option);
    }

    @Override
    protected int[] doLumpRemove(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchDelete(downcast(ls));
        } else {
            return varyingBatchDelete(downcast(ls), downcast(option));
        }
    }

    @Override
    protected int[] doLumpRemoveNonstrict(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> option) {
        return doLumpRemove(ls, option);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * searchLogBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;SearchLog, SearchLogCB&gt;() {
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
     *         <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
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
            final QueryInsertSetupper<SearchLog, SearchLogCB> setupper,
            final InsertOption<SearchLogCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final SearchLog entity = new SearchLog();
        final SearchLogCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected SearchLogCB createCBForQueryInsert() {
        final SearchLogCB cb = newMyConditionBean();
        cb.xsetupForQueryInsert();
        return cb;
    }

    @Override
    protected int doRangeCreate(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryInsert(downcast(setupper));
        } else {
            return varyingQueryInsert(downcast(setupper), downcast(option));
        }
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
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//searchLog.setVersionNo(value);</span>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * searchLogBhv.<span style="color: #FD4747">queryUpdate</span>(searchLog, cb);
     * </pre>
     * @param searchLog The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final SearchLog searchLog, final SearchLogCB cb) {
        return doQueryUpdate(searchLog, cb, null);
    }

    protected int doQueryUpdate(final SearchLog searchLog,
            final SearchLogCB cb, final UpdateOption<SearchLogCB> option) {
        assertObjectNotNull("searchLog", searchLog);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                searchLog, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (SearchLogCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity), (SearchLogCB) cb,
                    downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * searchLogBhv.<span style="color: #FD4747">queryDelete</span>(searchLog, cb);
     * </pre>
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final SearchLogCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final SearchLogCB cb,
            final DeleteOption<SearchLogCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((SearchLogCB) cb);
        } else {
            return varyingQueryDelete((SearchLogCB) cb, downcast(option));
        }
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
     * searchLogBhv.<span style="color: #FD4747">varyingInsert</span>(searchLog, option);
     * ... = searchLog.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param searchLog The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * searchLog.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;SearchLogCB&gt; option = new UpdateOption&lt;SearchLogCB&gt;();
     *     option.self(new SpecifyQuery&lt;SearchLogCB&gt;() {
     *         public void specify(SearchLogCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     searchLogBhv.<span style="color: #FD4747">varyingUpdate</span>(searchLog, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param searchLog The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final SearchLog searchLog,
            final UpdateOption<SearchLogCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(searchLog, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param searchLog The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final SearchLog searchLog,
            final InsertOption<SearchLogCB> insertOption,
            final UpdateOption<SearchLogCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(searchLog, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param searchLog The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//searchLog.setVersionNo(value);</span>
     * SearchLogCB cb = new SearchLogCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;SearchLogCB&gt; option = new UpdateOption&lt;SearchLogCB&gt;();
     * option.self(new SpecifyQuery&lt;SearchLogCB&gt;() {
     *     public void specify(SearchLogCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * searchLogBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(searchLog, cb, option);
     * </pre>
     * @param searchLog The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of SearchLog. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
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
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
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
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final SearchLogCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final SearchLogCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends SearchLog> void delegateSelectCursor(
            final SearchLogCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends SearchLog> List<ENTITY> delegateSelectList(
            final SearchLogCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final SearchLog e,
            final InsertOption<SearchLogCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final SearchLog e,
            final UpdateOption<SearchLogCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final SearchLog e,
            final UpdateOption<SearchLogCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final SearchLog e,
            final DeleteOption<SearchLogCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final SearchLog e,
            final DeleteOption<SearchLogCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<SearchLog> ls,
            final InsertOption<SearchLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<SearchLog> ls,
            final UpdateOption<SearchLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(final List<SearchLog> ls,
            final UpdateOption<SearchLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<SearchLog> ls,
            final DeleteOption<SearchLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(final List<SearchLog> ls,
            final DeleteOption<SearchLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final SearchLog e,
            final SearchLogCB inCB, final ConditionBean resCB,
            final InsertOption<SearchLogCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final SearchLog e, final SearchLogCB cb,
            final UpdateOption<SearchLogCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final SearchLogCB cb,
            final DeleteOption<SearchLogCB> op) {
        if (!processBeforeQueryDelete(cb, op)) {
            return 0;
        }
        return invoke(createQueryDeleteCBCommand(cb, op));
    }

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasVersionNoValue(final Entity entity) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasUpdateDateValue(final Entity entity) {
        return false;
    }

    // ===================================================================================
    //                                                                     Downcast Helper
    //                                                                     ===============
    protected SearchLog downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, SearchLog.class);
    }

    protected SearchLogCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, SearchLogCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<SearchLog> downcast(final List<? extends Entity> entityList) {
        return (List<SearchLog>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<SearchLogCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<SearchLogCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<SearchLogCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<SearchLogCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<SearchLogCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<SearchLogCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<SearchLog, SearchLogCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<SearchLog, SearchLogCB>) option;
    }
}
