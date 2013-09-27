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

package jp.sf.fess.db.bsbhv;

import java.util.List;

import jp.sf.fess.db.bsentity.dbmeta.SearchFieldLogDbm;
import jp.sf.fess.db.cbean.SearchFieldLogCB;
import jp.sf.fess.db.exbhv.SearchFieldLogBhv;
import jp.sf.fess.db.exentity.SearchFieldLog;
import jp.sf.fess.db.exentity.SearchLog;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.AbstractBehaviorWritable;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.InsertOption;
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
 * The behavior of SEARCH_FIELD_LOG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, SEARCH_ID, NAME, VALUE
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
 *     SEARCH_LOG
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     searchLog
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsSearchFieldLogBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:beginQueryPath*/
    public static final String PATH_selectGroupedFieldName = "selectGroupedFieldName";

    /*df:endQueryPath*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    @Override
    public String getTableDbName() {
        return "SEARCH_FIELD_LOG";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return SearchFieldLogDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public SearchFieldLogDbm getMyDBMeta() {
        return SearchFieldLogDbm.getInstance();
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
    public SearchFieldLog newMyEntity() {
        return new SearchFieldLog();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public SearchFieldLogCB newMyConditionBean() {
        return new SearchFieldLogCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * int count = searchFieldLogBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final SearchFieldLogCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final SearchFieldLogCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final SearchFieldLogCB cb) { // called by selectPage(cb)
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
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * SearchFieldLog searchFieldLog = searchFieldLogBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (searchFieldLog != null) {
     *     ... = searchFieldLog.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchFieldLog selectEntity(final SearchFieldLogCB cb) {
        return doSelectEntity(cb, SearchFieldLog.class);
    }

    protected <ENTITY extends SearchFieldLog> ENTITY doSelectEntity(
            final SearchFieldLogCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(cb, entityType,
                new InternalSelectEntityCallback<ENTITY, SearchFieldLogCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchFieldLogCB cb,
                            final Class<ENTITY> entityType) {
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
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * SearchFieldLog searchFieldLog = searchFieldLogBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = searchFieldLog.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public SearchFieldLog selectEntityWithDeletedCheck(final SearchFieldLogCB cb) {
        return doSelectEntityWithDeletedCheck(cb, SearchFieldLog.class);
    }

    protected <ENTITY extends SearchFieldLog> ENTITY doSelectEntityWithDeletedCheck(
            final SearchFieldLogCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, SearchFieldLogCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchFieldLogCB cb,
                            final Class<ENTITY> entityType) {
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
    public SearchFieldLog selectByPKValue(final Long id) {
        return doSelectByPKValue(id, SearchFieldLog.class);
    }

    protected <ENTITY extends SearchFieldLog> ENTITY doSelectByPKValue(
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
    public SearchFieldLog selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, SearchFieldLog.class);
    }

    protected <ENTITY extends SearchFieldLog> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private SearchFieldLogCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final SearchFieldLogCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;SearchFieldLog&gt; searchFieldLogList = searchFieldLogBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (SearchFieldLog searchFieldLog : searchFieldLogList) {
     *     ... = searchFieldLog.get...();
     * }
     * </pre>
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<SearchFieldLog> selectList(final SearchFieldLogCB cb) {
        return doSelectList(cb, SearchFieldLog.class);
    }

    protected <ENTITY extends SearchFieldLog> ListResultBean<ENTITY> doSelectList(
            final SearchFieldLogCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, SearchFieldLogCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchFieldLogCB cb,
                            final Class<ENTITY> entityType) {
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
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;SearchFieldLog&gt; page = searchFieldLogBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (SearchFieldLog searchFieldLog : page) {
     *     ... = searchFieldLog.get...();
     * }
     * </pre>
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<SearchFieldLog> selectPage(final SearchFieldLogCB cb) {
        return doSelectPage(cb, SearchFieldLog.class);
    }

    protected <ENTITY extends SearchFieldLog> PagingResultBean<ENTITY> doSelectPage(
            final SearchFieldLogCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, SearchFieldLogCB>() {
                    @Override
                    public int callbackSelectCount(final SearchFieldLogCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchFieldLogCB cb,
                            final Class<ENTITY> entityType) {
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
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * searchFieldLogBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;SearchFieldLog&gt;() {
     *     public void handle(SearchFieldLog entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @param entityRowHandler The handler of entity row of SearchFieldLog. (NotNull)
     */
    public void selectCursor(final SearchFieldLogCB cb,
            final EntityRowHandler<SearchFieldLog> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, SearchFieldLog.class);
    }

    protected <ENTITY extends SearchFieldLog> void doSelectCursor(
            final SearchFieldLogCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<SearchFieldLog>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(cb, entityRowHandler, entityType,
                new InternalSelectCursorCallback<ENTITY, SearchFieldLogCB>() {
                    @Override
                    public void callbackSelectCursor(final SearchFieldLogCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final SearchFieldLogCB cb,
                            final Class<ENTITY> entityType) {
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
     * searchFieldLogBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(SearchFieldLogCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<SearchFieldLogCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends SearchFieldLogCB> SLFunction<CB, RESULT> doScalarSelect(
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
    //                                                                   Pull out Relation
    //                                                                   =================
    /**
     * Pull out the list of foreign table 'SearchLog'.
     * @param searchFieldLogList The list of searchFieldLog. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<SearchLog> pulloutSearchLog(
            final List<SearchFieldLog> searchFieldLogList) {
        return helpPulloutInternally(searchFieldLogList,
                new InternalPulloutCallback<SearchFieldLog, SearchLog>() {
                    @Override
                    public SearchLog getFr(final SearchFieldLog e) {
                        return e.getSearchLog();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final SearchLog e,
                            final List<SearchFieldLog> ls) {
                        e.setSearchFieldLogList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param searchFieldLogList The list of searchFieldLog. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<SearchFieldLog> searchFieldLogList) {
        return helpExtractListInternally(searchFieldLogList,
                new InternalExtractCallback<SearchFieldLog, Long>() {
                    @Override
                    public Long getCV(final SearchFieldLog e) {
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
     * SearchFieldLog searchFieldLog = new SearchFieldLog();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * searchFieldLog.setFoo...(value);
     * searchFieldLog.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//searchFieldLog.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//searchFieldLog.set...;</span>
     * searchFieldLogBhv.<span style="color: #FD4747">insert</span>(searchFieldLog);
     * ... = searchFieldLog.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param searchFieldLog The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final SearchFieldLog searchFieldLog) {
        doInsert(searchFieldLog, null);
    }

    protected void doInsert(final SearchFieldLog searchFieldLog,
            final InsertOption<SearchFieldLogCB> option) {
        assertObjectNotNull("searchFieldLog", searchFieldLog);
        prepareInsertOption(option);
        delegateInsert(searchFieldLog, option);
    }

    protected void prepareInsertOption(
            final InsertOption<SearchFieldLogCB> option) {
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
     * SearchFieldLog searchFieldLog = new SearchFieldLog();
     * searchFieldLog.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * searchFieldLog.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//searchFieldLog.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//searchFieldLog.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * searchFieldLog.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     searchFieldLogBhv.<span style="color: #FD4747">update</span>(searchFieldLog);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param searchFieldLog The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final SearchFieldLog searchFieldLog) {
        doUpdate(searchFieldLog, null);
    }

    protected void doUpdate(final SearchFieldLog searchFieldLog,
            final UpdateOption<SearchFieldLogCB> option) {
        assertObjectNotNull("searchFieldLog", searchFieldLog);
        prepareUpdateOption(option);
        helpUpdateInternally(searchFieldLog,
                new InternalUpdateCallback<SearchFieldLog>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final SearchFieldLog entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<SearchFieldLogCB> option) {
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

    protected SearchFieldLogCB createCBForVaryingUpdate() {
        final SearchFieldLogCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected SearchFieldLogCB createCBForSpecifiedUpdate() {
        final SearchFieldLogCB cb = newMyConditionBean();
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
     * @param searchFieldLog The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final SearchFieldLog searchFieldLog) {
        doInesrtOrUpdate(searchFieldLog, null, null);
    }

    protected void doInesrtOrUpdate(final SearchFieldLog searchFieldLog,
            final InsertOption<SearchFieldLogCB> insertOption,
            final UpdateOption<SearchFieldLogCB> updateOption) {
        helpInsertOrUpdateInternally(
                searchFieldLog,
                new InternalInsertOrUpdateCallback<SearchFieldLog, SearchFieldLogCB>() {
                    @Override
                    public void callbackInsert(final SearchFieldLog entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final SearchFieldLog entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public SearchFieldLogCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final SearchFieldLogCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<SearchFieldLogCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<SearchFieldLogCB>()
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
     * SearchFieldLog searchFieldLog = new SearchFieldLog();
     * searchFieldLog.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * searchFieldLog.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     searchFieldLogBhv.<span style="color: #FD4747">delete</span>(searchFieldLog);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param searchFieldLog The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final SearchFieldLog searchFieldLog) {
        doDelete(searchFieldLog, null);
    }

    protected void doDelete(final SearchFieldLog searchFieldLog,
            final DeleteOption<SearchFieldLogCB> option) {
        assertObjectNotNull("searchFieldLog", searchFieldLog);
        prepareDeleteOption(option);
        helpDeleteInternally(searchFieldLog,
                new InternalDeleteCallback<SearchFieldLog>() {
                    @Override
                    public int callbackDelegateDelete(
                            final SearchFieldLog entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<SearchFieldLogCB> option) {
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
     * @param searchFieldLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<SearchFieldLog> searchFieldLogList) {
        return doBatchInsert(searchFieldLogList, null);
    }

    protected int[] doBatchInsert(
            final List<SearchFieldLog> searchFieldLogList,
            final InsertOption<SearchFieldLogCB> option) {
        assertObjectNotNull("searchFieldLogList", searchFieldLogList);
        prepareInsertOption(option);
        return delegateBatchInsert(searchFieldLogList, option);
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
     * searchFieldLogBhv.<span style="color: #FD4747">batchUpdate</span>(searchFieldLogList, new SpecifyQuery<SearchFieldLogCB>() {
     *     public void specify(SearchFieldLogCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param searchFieldLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(final List<SearchFieldLog> searchFieldLogList) {
        return doBatchUpdate(searchFieldLogList, null);
    }

    protected int[] doBatchUpdate(
            final List<SearchFieldLog> searchFieldLogList,
            final UpdateOption<SearchFieldLogCB> option) {
        assertObjectNotNull("searchFieldLogList", searchFieldLogList);
        prepareBatchUpdateOption(searchFieldLogList, option);
        return delegateBatchUpdate(searchFieldLogList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<SearchFieldLog> searchFieldLogList,
            final UpdateOption<SearchFieldLogCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(searchFieldLogList);
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
     * searchFieldLogBhv.<span style="color: #FD4747">batchUpdate</span>(searchFieldLogList, new SpecifyQuery<SearchFieldLogCB>() {
     *     public void specify(SearchFieldLogCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * searchFieldLogBhv.<span style="color: #FD4747">batchUpdate</span>(searchFieldLogList, new SpecifyQuery<SearchFieldLogCB>() {
     *     public void specify(SearchFieldLogCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param searchFieldLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(final List<SearchFieldLog> searchFieldLogList,
            final SpecifyQuery<SearchFieldLogCB> updateColumnSpec) {
        return doBatchUpdate(searchFieldLogList,
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
     * @param searchFieldLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(final List<SearchFieldLog> searchFieldLogList) {
        return doBatchDelete(searchFieldLogList, null);
    }

    protected int[] doBatchDelete(
            final List<SearchFieldLog> searchFieldLogList,
            final DeleteOption<SearchFieldLogCB> option) {
        assertObjectNotNull("searchFieldLogList", searchFieldLogList);
        prepareDeleteOption(option);
        return delegateBatchDelete(searchFieldLogList, option);
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
     * searchFieldLogBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;SearchFieldLog, SearchFieldLogCB&gt;() {
     *     public ConditionBean setup(searchFieldLog entity, SearchFieldLogCB intoCB) {
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
            final QueryInsertSetupper<SearchFieldLog, SearchFieldLogCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<SearchFieldLog, SearchFieldLogCB> setupper,
            final InsertOption<SearchFieldLogCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final SearchFieldLog entity = new SearchFieldLog();
        final SearchFieldLogCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected SearchFieldLogCB createCBForQueryInsert() {
        final SearchFieldLogCB cb = newMyConditionBean();
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
     * SearchFieldLog searchFieldLog = new SearchFieldLog();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//searchFieldLog.setPK...(value);</span>
     * searchFieldLog.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//searchFieldLog.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//searchFieldLog.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//searchFieldLog.setVersionNo(value);</span>
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * searchFieldLogBhv.<span style="color: #FD4747">queryUpdate</span>(searchFieldLog, cb);
     * </pre>
     * @param searchFieldLog The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final SearchFieldLog searchFieldLog,
            final SearchFieldLogCB cb) {
        return doQueryUpdate(searchFieldLog, cb, null);
    }

    protected int doQueryUpdate(final SearchFieldLog searchFieldLog,
            final SearchFieldLogCB cb,
            final UpdateOption<SearchFieldLogCB> option) {
        assertObjectNotNull("searchFieldLog", searchFieldLog);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                searchFieldLog, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (SearchFieldLogCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity), (SearchFieldLogCB) cb,
                    downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * searchFieldLogBhv.<span style="color: #FD4747">queryDelete</span>(searchFieldLog, cb);
     * </pre>
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final SearchFieldLogCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final SearchFieldLogCB cb,
            final DeleteOption<SearchFieldLogCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((SearchFieldLogCB) cb);
        } else {
            return varyingQueryDelete((SearchFieldLogCB) cb, downcast(option));
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
     * SearchFieldLog searchFieldLog = new SearchFieldLog();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * searchFieldLog.setFoo...(value);
     * searchFieldLog.setBar...(value);
     * InsertOption<SearchFieldLogCB> option = new InsertOption<SearchFieldLogCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * searchFieldLogBhv.<span style="color: #FD4747">varyingInsert</span>(searchFieldLog, option);
     * ... = searchFieldLog.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param searchFieldLog The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final SearchFieldLog searchFieldLog,
            final InsertOption<SearchFieldLogCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(searchFieldLog, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * SearchFieldLog searchFieldLog = new SearchFieldLog();
     * searchFieldLog.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * searchFieldLog.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * searchFieldLog.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;SearchFieldLogCB&gt; option = new UpdateOption&lt;SearchFieldLogCB&gt;();
     *     option.self(new SpecifyQuery&lt;SearchFieldLogCB&gt;() {
     *         public void specify(SearchFieldLogCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     searchFieldLogBhv.<span style="color: #FD4747">varyingUpdate</span>(searchFieldLog, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param searchFieldLog The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final SearchFieldLog searchFieldLog,
            final UpdateOption<SearchFieldLogCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(searchFieldLog, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param searchFieldLog The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final SearchFieldLog searchFieldLog,
            final InsertOption<SearchFieldLogCB> insertOption,
            final UpdateOption<SearchFieldLogCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(searchFieldLog, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param searchFieldLog The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final SearchFieldLog searchFieldLog,
            final DeleteOption<SearchFieldLogCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(searchFieldLog, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param searchFieldLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<SearchFieldLog> searchFieldLogList,
            final InsertOption<SearchFieldLogCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(searchFieldLogList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param searchFieldLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<SearchFieldLog> searchFieldLogList,
            final UpdateOption<SearchFieldLogCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(searchFieldLogList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param searchFieldLogList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<SearchFieldLog> searchFieldLogList,
            final DeleteOption<SearchFieldLogCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(searchFieldLogList, option);
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
            final QueryInsertSetupper<SearchFieldLog, SearchFieldLogCB> setupper,
            final InsertOption<SearchFieldLogCB> option) {
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
     * SearchFieldLog searchFieldLog = new SearchFieldLog();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//searchFieldLog.setPK...(value);</span>
     * searchFieldLog.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//searchFieldLog.setVersionNo(value);</span>
     * SearchFieldLogCB cb = new SearchFieldLogCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;SearchFieldLogCB&gt; option = new UpdateOption&lt;SearchFieldLogCB&gt;();
     * option.self(new SpecifyQuery&lt;SearchFieldLogCB&gt;() {
     *     public void specify(SearchFieldLogCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * searchFieldLogBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(searchFieldLog, cb, option);
     * </pre>
     * @param searchFieldLog The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final SearchFieldLog searchFieldLog,
            final SearchFieldLogCB cb,
            final UpdateOption<SearchFieldLogCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(searchFieldLog, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of SearchFieldLog. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final SearchFieldLogCB cb,
            final DeleteOption<SearchFieldLogCB> option) {
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
    public OutsideSqlBasicExecutor<SearchFieldLogBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final SearchFieldLogCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final SearchFieldLogCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends SearchFieldLog> void delegateSelectCursor(
            final SearchFieldLogCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends SearchFieldLog> List<ENTITY> delegateSelectList(
            final SearchFieldLogCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final SearchFieldLog e,
            final InsertOption<SearchFieldLogCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final SearchFieldLog e,
            final UpdateOption<SearchFieldLogCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final SearchFieldLog e,
            final UpdateOption<SearchFieldLogCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final SearchFieldLog e,
            final DeleteOption<SearchFieldLogCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final SearchFieldLog e,
            final DeleteOption<SearchFieldLogCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<SearchFieldLog> ls,
            final InsertOption<SearchFieldLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<SearchFieldLog> ls,
            final UpdateOption<SearchFieldLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(final List<SearchFieldLog> ls,
            final UpdateOption<SearchFieldLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<SearchFieldLog> ls,
            final DeleteOption<SearchFieldLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(final List<SearchFieldLog> ls,
            final DeleteOption<SearchFieldLogCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final SearchFieldLog e,
            final SearchFieldLogCB inCB, final ConditionBean resCB,
            final InsertOption<SearchFieldLogCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final SearchFieldLog e,
            final SearchFieldLogCB cb, final UpdateOption<SearchFieldLogCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final SearchFieldLogCB cb,
            final DeleteOption<SearchFieldLogCB> op) {
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
    protected SearchFieldLog downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, SearchFieldLog.class);
    }

    protected SearchFieldLogCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, SearchFieldLogCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<SearchFieldLog> downcast(
            final List<? extends Entity> entityList) {
        return (List<SearchFieldLog>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<SearchFieldLogCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<SearchFieldLogCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<SearchFieldLogCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<SearchFieldLogCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<SearchFieldLogCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<SearchFieldLogCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<SearchFieldLog, SearchFieldLogCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<SearchFieldLog, SearchFieldLogCB>) option;
    }
}
