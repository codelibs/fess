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

import jp.sf.fess.db.bsentity.dbmeta.ScheduledJobDbm;
import jp.sf.fess.db.cbean.ScheduledJobCB;
import jp.sf.fess.db.exbhv.ScheduledJobBhv;
import jp.sf.fess.db.exentity.ScheduledJob;

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
 * The behavior of SCHEDULED_JOB as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, NAME, TARGET, CRON_EXPRESSION, SCRIPT_TYPE, SCRIPT_DATA, CRAWLER, JOB_LOGGING, AVAILABLE, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     ID
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign table]
 *     
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsScheduledJobBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:beginQueryPath*/
    /*df:endQueryPath*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    @Override
    public String getTableDbName() {
        return "SCHEDULED_JOB";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return ScheduledJobDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public ScheduledJobDbm getMyDBMeta() {
        return ScheduledJobDbm.getInstance();
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
    public ScheduledJob newMyEntity() {
        return new ScheduledJob();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public ScheduledJobCB newMyConditionBean() {
        return new ScheduledJobCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * int count = scheduledJobBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final ScheduledJobCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final ScheduledJobCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final ScheduledJobCB cb) { // called by selectPage(cb)
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
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * ScheduledJob scheduledJob = scheduledJobBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (scheduledJob != null) {
     *     ... = scheduledJob.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public ScheduledJob selectEntity(final ScheduledJobCB cb) {
        return doSelectEntity(cb, ScheduledJob.class);
    }

    protected <ENTITY extends ScheduledJob> ENTITY doSelectEntity(
            final ScheduledJobCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(cb, entityType,
                new InternalSelectEntityCallback<ENTITY, ScheduledJobCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final ScheduledJobCB cb,
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
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * ScheduledJob scheduledJob = scheduledJobBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = scheduledJob.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public ScheduledJob selectEntityWithDeletedCheck(final ScheduledJobCB cb) {
        return doSelectEntityWithDeletedCheck(cb, ScheduledJob.class);
    }

    protected <ENTITY extends ScheduledJob> ENTITY doSelectEntityWithDeletedCheck(
            final ScheduledJobCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, ScheduledJobCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final ScheduledJobCB cb,
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
    public ScheduledJob selectByPKValue(final Long id) {
        return doSelectByPKValue(id, ScheduledJob.class);
    }

    protected <ENTITY extends ScheduledJob> ENTITY doSelectByPKValue(
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
    public ScheduledJob selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, ScheduledJob.class);
    }

    protected <ENTITY extends ScheduledJob> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private ScheduledJobCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final ScheduledJobCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;ScheduledJob&gt; scheduledJobList = scheduledJobBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (ScheduledJob scheduledJob : scheduledJobList) {
     *     ... = scheduledJob.get...();
     * }
     * </pre>
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<ScheduledJob> selectList(final ScheduledJobCB cb) {
        return doSelectList(cb, ScheduledJob.class);
    }

    protected <ENTITY extends ScheduledJob> ListResultBean<ENTITY> doSelectList(
            final ScheduledJobCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, ScheduledJobCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final ScheduledJobCB cb,
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
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;ScheduledJob&gt; page = scheduledJobBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (ScheduledJob scheduledJob : page) {
     *     ... = scheduledJob.get...();
     * }
     * </pre>
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<ScheduledJob> selectPage(final ScheduledJobCB cb) {
        return doSelectPage(cb, ScheduledJob.class);
    }

    protected <ENTITY extends ScheduledJob> PagingResultBean<ENTITY> doSelectPage(
            final ScheduledJobCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, ScheduledJobCB>() {
                    @Override
                    public int callbackSelectCount(final ScheduledJobCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final ScheduledJobCB cb,
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
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * scheduledJobBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;ScheduledJob&gt;() {
     *     public void handle(ScheduledJob entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @param entityRowHandler The handler of entity row of ScheduledJob. (NotNull)
     */
    public void selectCursor(final ScheduledJobCB cb,
            final EntityRowHandler<ScheduledJob> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, ScheduledJob.class);
    }

    protected <ENTITY extends ScheduledJob> void doSelectCursor(
            final ScheduledJobCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<ScheduledJob>", entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(cb, entityRowHandler, entityType,
                new InternalSelectCursorCallback<ENTITY, ScheduledJobCB>() {
                    @Override
                    public void callbackSelectCursor(final ScheduledJobCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final ScheduledJobCB cb,
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
     * scheduledJobBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(ScheduledJobCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<ScheduledJobCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends ScheduledJobCB> SLFunction<CB, RESULT> doScalarSelect(
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

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param scheduledJobList The list of scheduledJob. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(final List<ScheduledJob> scheduledJobList) {
        return helpExtractListInternally(scheduledJobList,
                new InternalExtractCallback<ScheduledJob, Long>() {
                    @Override
                    public Long getCV(final ScheduledJob e) {
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
     * ScheduledJob scheduledJob = new ScheduledJob();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * scheduledJob.setFoo...(value);
     * scheduledJob.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//scheduledJob.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//scheduledJob.set...;</span>
     * scheduledJobBhv.<span style="color: #FD4747">insert</span>(scheduledJob);
     * ... = scheduledJob.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param scheduledJob The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final ScheduledJob scheduledJob) {
        doInsert(scheduledJob, null);
    }

    protected void doInsert(final ScheduledJob scheduledJob,
            final InsertOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJob", scheduledJob);
        prepareInsertOption(option);
        delegateInsert(scheduledJob, option);
    }

    protected void prepareInsertOption(final InsertOption<ScheduledJobCB> option) {
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
     * Update the entity modified-only. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * ScheduledJob scheduledJob = new ScheduledJob();
     * scheduledJob.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * scheduledJob.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//scheduledJob.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//scheduledJob.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * scheduledJob.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     scheduledJobBhv.<span style="color: #FD4747">update</span>(scheduledJob);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param scheduledJob The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final ScheduledJob scheduledJob) {
        doUpdate(scheduledJob, null);
    }

    protected void doUpdate(final ScheduledJob scheduledJob,
            final UpdateOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJob", scheduledJob);
        prepareUpdateOption(option);
        helpUpdateInternally(scheduledJob,
                new InternalUpdateCallback<ScheduledJob>() {
                    @Override
                    public int callbackDelegateUpdate(final ScheduledJob entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(final UpdateOption<ScheduledJobCB> option) {
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

    protected ScheduledJobCB createCBForVaryingUpdate() {
        final ScheduledJobCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected ScheduledJobCB createCBForSpecifiedUpdate() {
        final ScheduledJobCB cb = newMyConditionBean();
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

    /**
     * Update the entity non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * ScheduledJob scheduledJob = new ScheduledJob();
     * scheduledJob.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * scheduledJob.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//scheduledJob.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//scheduledJob.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//scheduledJob.setVersionNo(value);</span>
     * scheduledJobBhv.<span style="color: #FD4747">updateNonstrict</span>(scheduledJob);
     * </pre>
     * @param scheduledJob The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final ScheduledJob scheduledJob) {
        doUpdateNonstrict(scheduledJob, null);
    }

    protected void doUpdateNonstrict(final ScheduledJob scheduledJob,
            final UpdateOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJob", scheduledJob);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(scheduledJob,
                new InternalUpdateNonstrictCallback<ScheduledJob>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final ScheduledJob entity) {
                        return delegateUpdateNonstrict(entity, option);
                    }
                });
    }

    @Override
    protected void doModifyNonstrict(final Entity entity,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            updateNonstrict(downcast(entity));
        } else {
            varyingUpdateNonstrict(downcast(entity), downcast(option));
        }
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, ExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param scheduledJob The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final ScheduledJob scheduledJob) {
        doInesrtOrUpdate(scheduledJob, null, null);
    }

    protected void doInesrtOrUpdate(final ScheduledJob scheduledJob,
            final InsertOption<ScheduledJobCB> insertOption,
            final UpdateOption<ScheduledJobCB> updateOption) {
        helpInsertOrUpdateInternally(
                scheduledJob,
                new InternalInsertOrUpdateCallback<ScheduledJob, ScheduledJobCB>() {
                    @Override
                    public void callbackInsert(final ScheduledJob entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final ScheduledJob entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public ScheduledJobCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final ScheduledJobCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<ScheduledJobCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<ScheduledJobCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param scheduledJob The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(final ScheduledJob scheduledJob) {
        doInesrtOrUpdateNonstrict(scheduledJob, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(final ScheduledJob scheduledJob,
            final InsertOption<ScheduledJobCB> insertOption,
            final UpdateOption<ScheduledJobCB> updateOption) {
        helpInsertOrUpdateInternally(scheduledJob,
                new InternalInsertOrUpdateNonstrictCallback<ScheduledJob>() {
                    @Override
                    public void callbackInsert(final ScheduledJob entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(
                            final ScheduledJob entity) {
                        doUpdateNonstrict(entity, updateOption);
                    }
                });
    }

    @Override
    protected void doCreateOrModifyNonstrict(final Entity entity,
            InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption) {
        if (insertOption == null && updateOption == null) {
            insertOrUpdateNonstrict(downcast(entity));
        } else {
            insertOption = insertOption == null ? new InsertOption<ScheduledJobCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<ScheduledJobCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * ScheduledJob scheduledJob = new ScheduledJob();
     * scheduledJob.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * scheduledJob.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     scheduledJobBhv.<span style="color: #FD4747">delete</span>(scheduledJob);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param scheduledJob The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final ScheduledJob scheduledJob) {
        doDelete(scheduledJob, null);
    }

    protected void doDelete(final ScheduledJob scheduledJob,
            final DeleteOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJob", scheduledJob);
        prepareDeleteOption(option);
        helpDeleteInternally(scheduledJob,
                new InternalDeleteCallback<ScheduledJob>() {
                    @Override
                    public int callbackDelegateDelete(final ScheduledJob entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(final DeleteOption<ScheduledJobCB> option) {
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

    /**
     * Delete the entity non-strictly. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * ScheduledJob scheduledJob = new ScheduledJob();
     * scheduledJob.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//scheduledJob.setVersionNo(value);</span>
     * scheduledJobBhv.<span style="color: #FD4747">deleteNonstrict</span>(scheduledJob);
     * </pre>
     * @param scheduledJob The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final ScheduledJob scheduledJob) {
        doDeleteNonstrict(scheduledJob, null);
    }

    protected void doDeleteNonstrict(final ScheduledJob scheduledJob,
            final DeleteOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJob", scheduledJob);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(scheduledJob,
                new InternalDeleteNonstrictCallback<ScheduledJob>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final ScheduledJob entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * ScheduledJob scheduledJob = new ScheduledJob();
     * scheduledJob.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//scheduledJob.setVersionNo(value);</span>
     * scheduledJobBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(scheduledJob);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param scheduledJob The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(final ScheduledJob scheduledJob) {
        doDeleteNonstrictIgnoreDeleted(scheduledJob, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(
            final ScheduledJob scheduledJob,
            final DeleteOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJob", scheduledJob);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(
                scheduledJob,
                new InternalDeleteNonstrictIgnoreDeletedCallback<ScheduledJob>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final ScheduledJob entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    @Override
    protected void doRemoveNonstrict(final Entity entity,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            deleteNonstrict(downcast(entity));
        } else {
            varyingDeleteNonstrict(downcast(entity), downcast(option));
        }
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
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<ScheduledJob> scheduledJobList) {
        return doBatchInsert(scheduledJobList, null);
    }

    protected int[] doBatchInsert(final List<ScheduledJob> scheduledJobList,
            final InsertOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJobList", scheduledJobList);
        prepareInsertOption(option);
        return delegateBatchInsert(scheduledJobList, option);
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
     * Batch-update the entity list. (AllColumnsUpdated, ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747; font-size: 140%">Attention, all columns are update target. {NOT modified only}</span> <br />
     * So you should the other batchUpdate() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * scheduledJobBhv.<span style="color: #FD4747">batchUpdate</span>(scheduledJobList, new SpecifyQuery<ScheduledJobCB>() {
     *     public void specify(ScheduledJobCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<ScheduledJob> scheduledJobList) {
        return doBatchUpdate(scheduledJobList, null);
    }

    protected int[] doBatchUpdate(final List<ScheduledJob> scheduledJobList,
            final UpdateOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJobList", scheduledJobList);
        prepareBatchUpdateOption(scheduledJobList, option);
        return delegateBatchUpdate(scheduledJobList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<ScheduledJob> scheduledJobList,
            final UpdateOption<ScheduledJobCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(scheduledJobList);
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
     * Batch-update the entity list. (SpecifiedColumnsUpdated, ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span> 
     * scheduledJobBhv.<span style="color: #FD4747">batchUpdate</span>(scheduledJobList, new SpecifyQuery<ScheduledJobCB>() {
     *     public void specify(ScheduledJobCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * scheduledJobBhv.<span style="color: #FD4747">batchUpdate</span>(scheduledJobList, new SpecifyQuery<ScheduledJobCB>() {
     *     public void specify(ScheduledJobCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<ScheduledJob> scheduledJobList,
            final SpecifyQuery<ScheduledJobCB> updateColumnSpec) {
        return doBatchUpdate(scheduledJobList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdateNonstrict() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * scheduledJobBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(scheduledJobList, new SpecifyQuery<ScheduledJobCB>() {
     *     public void specify(ScheduledJobCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<ScheduledJob> scheduledJobList) {
        return doBatchUpdateNonstrict(scheduledJobList, null);
    }

    protected int[] doBatchUpdateNonstrict(
            final List<ScheduledJob> scheduledJobList,
            final UpdateOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJobList", scheduledJobList);
        prepareBatchUpdateOption(scheduledJobList, option);
        return delegateBatchUpdateNonstrict(scheduledJobList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span> 
     * scheduledJobBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(scheduledJobList, new SpecifyQuery<ScheduledJobCB>() {
     *     public void specify(ScheduledJobCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * scheduledJobBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(scheduledJobList, new SpecifyQuery<ScheduledJobCB>() {
     *     public void specify(ScheduledJobCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<ScheduledJob> scheduledJobList,
            final SpecifyQuery<ScheduledJobCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(scheduledJobList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchUpdateNonstrict(downcast(ls));
        } else {
            return varyingBatchUpdateNonstrict(downcast(ls), downcast(option));
        }
    }

    /**
     * Batch-delete the entity list. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<ScheduledJob> scheduledJobList) {
        return doBatchDelete(scheduledJobList, null);
    }

    protected int[] doBatchDelete(final List<ScheduledJob> scheduledJobList,
            final DeleteOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJobList", scheduledJobList);
        prepareDeleteOption(option);
        return delegateBatchDelete(scheduledJobList, option);
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

    /**
     * Batch-delete the entity list non-strictly. {NonExclusiveControl} <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(final List<ScheduledJob> scheduledJobList) {
        return doBatchDeleteNonstrict(scheduledJobList, null);
    }

    protected int[] doBatchDeleteNonstrict(
            final List<ScheduledJob> scheduledJobList,
            final DeleteOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJobList", scheduledJobList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(scheduledJobList, option);
    }

    @Override
    protected int[] doLumpRemoveNonstrict(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchDeleteNonstrict(downcast(ls));
        } else {
            return varyingBatchDeleteNonstrict(downcast(ls), downcast(option));
        }
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * scheduledJobBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;ScheduledJob, ScheduledJobCB&gt;() {
     *     public ConditionBean setup(scheduledJob entity, ScheduledJobCB intoCB) {
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
            final QueryInsertSetupper<ScheduledJob, ScheduledJobCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<ScheduledJob, ScheduledJobCB> setupper,
            final InsertOption<ScheduledJobCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final ScheduledJob entity = new ScheduledJob();
        final ScheduledJobCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected ScheduledJobCB createCBForQueryInsert() {
        final ScheduledJobCB cb = newMyConditionBean();
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
     * ScheduledJob scheduledJob = new ScheduledJob();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//scheduledJob.setPK...(value);</span>
     * scheduledJob.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//scheduledJob.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//scheduledJob.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//scheduledJob.setVersionNo(value);</span>
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * scheduledJobBhv.<span style="color: #FD4747">queryUpdate</span>(scheduledJob, cb);
     * </pre>
     * @param scheduledJob The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final ScheduledJob scheduledJob,
            final ScheduledJobCB cb) {
        return doQueryUpdate(scheduledJob, cb, null);
    }

    protected int doQueryUpdate(final ScheduledJob scheduledJob,
            final ScheduledJobCB cb, final UpdateOption<ScheduledJobCB> option) {
        assertObjectNotNull("scheduledJob", scheduledJob);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                scheduledJob, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (ScheduledJobCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity), (ScheduledJobCB) cb,
                    downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * scheduledJobBhv.<span style="color: #FD4747">queryDelete</span>(scheduledJob, cb);
     * </pre>
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final ScheduledJobCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final ScheduledJobCB cb,
            final DeleteOption<ScheduledJobCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((ScheduledJobCB) cb);
        } else {
            return varyingQueryDelete((ScheduledJobCB) cb, downcast(option));
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
     * ScheduledJob scheduledJob = new ScheduledJob();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * scheduledJob.setFoo...(value);
     * scheduledJob.setBar...(value);
     * InsertOption<ScheduledJobCB> option = new InsertOption<ScheduledJobCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * scheduledJobBhv.<span style="color: #FD4747">varyingInsert</span>(scheduledJob, option);
     * ... = scheduledJob.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param scheduledJob The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final ScheduledJob scheduledJob,
            final InsertOption<ScheduledJobCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(scheduledJob, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * ScheduledJob scheduledJob = new ScheduledJob();
     * scheduledJob.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * scheduledJob.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * scheduledJob.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;ScheduledJobCB&gt; option = new UpdateOption&lt;ScheduledJobCB&gt;();
     *     option.self(new SpecifyQuery&lt;ScheduledJobCB&gt;() {
     *         public void specify(ScheduledJobCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     scheduledJobBhv.<span style="color: #FD4747">varyingUpdate</span>(scheduledJob, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param scheduledJob The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final ScheduledJob scheduledJob,
            final UpdateOption<ScheduledJobCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(scheduledJob, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * ScheduledJob scheduledJob = new ScheduledJob();
     * scheduledJob.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * scheduledJob.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//scheduledJob.setVersionNo(value);</span>
     * UpdateOption&lt;ScheduledJobCB&gt; option = new UpdateOption&lt;ScheduledJobCB&gt;();
     * option.self(new SpecifyQuery&lt;ScheduledJobCB&gt;() {
     *     public void specify(ScheduledJobCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * scheduledJobBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(scheduledJob, option);
     * </pre>
     * @param scheduledJob The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(final ScheduledJob scheduledJob,
            final UpdateOption<ScheduledJobCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(scheduledJob, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param scheduledJob The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final ScheduledJob scheduledJob,
            final InsertOption<ScheduledJobCB> insertOption,
            final UpdateOption<ScheduledJobCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(scheduledJob, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param scheduledJob The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(final ScheduledJob scheduledJob,
            final InsertOption<ScheduledJobCB> insertOption,
            final UpdateOption<ScheduledJobCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(scheduledJob, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param scheduledJob The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final ScheduledJob scheduledJob,
            final DeleteOption<ScheduledJobCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(scheduledJob, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param scheduledJob The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(final ScheduledJob scheduledJob,
            final DeleteOption<ScheduledJobCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(scheduledJob, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(final List<ScheduledJob> scheduledJobList,
            final InsertOption<ScheduledJobCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(scheduledJobList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(final List<ScheduledJob> scheduledJobList,
            final UpdateOption<ScheduledJobCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(scheduledJobList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<ScheduledJob> scheduledJobList,
            final UpdateOption<ScheduledJobCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(scheduledJobList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(final List<ScheduledJob> scheduledJobList,
            final DeleteOption<ScheduledJobCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(scheduledJobList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param scheduledJobList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<ScheduledJob> scheduledJobList,
            final DeleteOption<ScheduledJobCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(scheduledJobList, option);
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
            final QueryInsertSetupper<ScheduledJob, ScheduledJobCB> setupper,
            final InsertOption<ScheduledJobCB> option) {
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
     * ScheduledJob scheduledJob = new ScheduledJob();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//scheduledJob.setPK...(value);</span>
     * scheduledJob.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//scheduledJob.setVersionNo(value);</span>
     * ScheduledJobCB cb = new ScheduledJobCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;ScheduledJobCB&gt; option = new UpdateOption&lt;ScheduledJobCB&gt;();
     * option.self(new SpecifyQuery&lt;ScheduledJobCB&gt;() {
     *     public void specify(ScheduledJobCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * scheduledJobBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(scheduledJob, cb, option);
     * </pre>
     * @param scheduledJob The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final ScheduledJob scheduledJob,
            final ScheduledJobCB cb, final UpdateOption<ScheduledJobCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(scheduledJob, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of ScheduledJob. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final ScheduledJobCB cb,
            final DeleteOption<ScheduledJobCB> option) {
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
    public OutsideSqlBasicExecutor<ScheduledJobBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final ScheduledJobCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final ScheduledJobCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends ScheduledJob> void delegateSelectCursor(
            final ScheduledJobCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends ScheduledJob> List<ENTITY> delegateSelectList(
            final ScheduledJobCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final ScheduledJob e,
            final InsertOption<ScheduledJobCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final ScheduledJob e,
            final UpdateOption<ScheduledJobCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final ScheduledJob e,
            final UpdateOption<ScheduledJobCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final ScheduledJob e,
            final DeleteOption<ScheduledJobCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final ScheduledJob e,
            final DeleteOption<ScheduledJobCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<ScheduledJob> ls,
            final InsertOption<ScheduledJobCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<ScheduledJob> ls,
            final UpdateOption<ScheduledJobCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(final List<ScheduledJob> ls,
            final UpdateOption<ScheduledJobCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<ScheduledJob> ls,
            final DeleteOption<ScheduledJobCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(final List<ScheduledJob> ls,
            final DeleteOption<ScheduledJobCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final ScheduledJob e,
            final ScheduledJobCB inCB, final ConditionBean resCB,
            final InsertOption<ScheduledJobCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final ScheduledJob e,
            final ScheduledJobCB cb, final UpdateOption<ScheduledJobCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final ScheduledJobCB cb,
            final DeleteOption<ScheduledJobCB> op) {
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
        return !(downcast(entity).getVersionNo() + "").equals("null");// For primitive type
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
    protected ScheduledJob downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, ScheduledJob.class);
    }

    protected ScheduledJobCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, ScheduledJobCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<ScheduledJob> downcast(
            final List<? extends Entity> entityList) {
        return (List<ScheduledJob>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<ScheduledJobCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<ScheduledJobCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<ScheduledJobCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<ScheduledJobCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<ScheduledJobCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<ScheduledJobCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<ScheduledJob, ScheduledJobCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<ScheduledJob, ScheduledJobCB>) option;
    }
}
