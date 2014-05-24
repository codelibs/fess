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

import jp.sf.fess.db.bsentity.dbmeta.LabelTypeToRoleTypeMappingDbm;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.LabelTypeToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
import jp.sf.fess.db.exentity.RoleType;

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
 * The behavior of LABEL_TYPE_TO_ROLE_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, LABEL_TYPE_ID, ROLE_TYPE_ID
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
 *     LABEL_TYPE, ROLE_TYPE
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     labelType, roleType
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsLabelTypeToRoleTypeMappingBhv extends
        AbstractBehaviorWritable {

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
        return "LABEL_TYPE_TO_ROLE_TYPE_MAPPING";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return LabelTypeToRoleTypeMappingDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public LabelTypeToRoleTypeMappingDbm getMyDBMeta() {
        return LabelTypeToRoleTypeMappingDbm.getInstance();
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
    public LabelTypeToRoleTypeMapping newMyEntity() {
        return new LabelTypeToRoleTypeMapping();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public LabelTypeToRoleTypeMappingCB newMyConditionBean() {
        return new LabelTypeToRoleTypeMappingCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * int count = labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final LabelTypeToRoleTypeMappingCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final LabelTypeToRoleTypeMappingCB cb) { // called by selectCount(cb)
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final LabelTypeToRoleTypeMappingCB cb) { // called by selectPage(cb)
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
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (labelTypeToRoleTypeMapping != null) {
     *     ... = labelTypeToRoleTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public LabelTypeToRoleTypeMapping selectEntity(
            final LabelTypeToRoleTypeMappingCB cb) {
        return doSelectEntity(cb, LabelTypeToRoleTypeMapping.class);
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> ENTITY doSelectEntity(
            final LabelTypeToRoleTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, LabelTypeToRoleTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeToRoleTypeMappingCB cb,
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
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = labelTypeToRoleTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public LabelTypeToRoleTypeMapping selectEntityWithDeletedCheck(
            final LabelTypeToRoleTypeMappingCB cb) {
        return doSelectEntityWithDeletedCheck(cb,
                LabelTypeToRoleTypeMapping.class);
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> ENTITY doSelectEntityWithDeletedCheck(
            final LabelTypeToRoleTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, LabelTypeToRoleTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeToRoleTypeMappingCB cb,
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
    public LabelTypeToRoleTypeMapping selectByPKValue(final Long id) {
        return doSelectByPKValue(id, LabelTypeToRoleTypeMapping.class);
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> ENTITY doSelectByPKValue(
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
    public LabelTypeToRoleTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKValueWithDeletedCheck(id,
                LabelTypeToRoleTypeMapping.class);
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private LabelTypeToRoleTypeMappingCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final LabelTypeToRoleTypeMappingCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;LabelTypeToRoleTypeMapping&gt; labelTypeToRoleTypeMappingList = labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping : labelTypeToRoleTypeMappingList) {
     *     ... = labelTypeToRoleTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<LabelTypeToRoleTypeMapping> selectList(
            final LabelTypeToRoleTypeMappingCB cb) {
        return doSelectList(cb, LabelTypeToRoleTypeMapping.class);
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> ListResultBean<ENTITY> doSelectList(
            final LabelTypeToRoleTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
                cb,
                entityType,
                new InternalSelectListCallback<ENTITY, LabelTypeToRoleTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeToRoleTypeMappingCB cb,
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
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;LabelTypeToRoleTypeMapping&gt; page = labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping : page) {
     *     ... = labelTypeToRoleTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<LabelTypeToRoleTypeMapping> selectPage(
            final LabelTypeToRoleTypeMappingCB cb) {
        return doSelectPage(cb, LabelTypeToRoleTypeMapping.class);
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> PagingResultBean<ENTITY> doSelectPage(
            final LabelTypeToRoleTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
                cb,
                entityType,
                new InternalSelectPageCallback<ENTITY, LabelTypeToRoleTypeMappingCB>() {
                    @Override
                    public int callbackSelectCount(
                            final LabelTypeToRoleTypeMappingCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeToRoleTypeMappingCB cb,
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
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;LabelTypeToRoleTypeMapping&gt;() {
     *     public void handle(LabelTypeToRoleTypeMapping entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @param entityRowHandler The handler of entity row of LabelTypeToRoleTypeMapping. (NotNull)
     */
    public void selectCursor(final LabelTypeToRoleTypeMappingCB cb,
            final EntityRowHandler<LabelTypeToRoleTypeMapping> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, LabelTypeToRoleTypeMapping.class);
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> void doSelectCursor(
            final LabelTypeToRoleTypeMappingCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<LabelTypeToRoleTypeMapping>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, LabelTypeToRoleTypeMappingCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final LabelTypeToRoleTypeMappingCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeToRoleTypeMappingCB cb,
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
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(LabelTypeToRoleTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<LabelTypeToRoleTypeMappingCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends LabelTypeToRoleTypeMappingCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * Pull out the list of foreign table 'LabelType'.
     * @param labelTypeToRoleTypeMappingList The list of labelTypeToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<LabelType> pulloutLabelType(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList) {
        return helpPulloutInternally(
                labelTypeToRoleTypeMappingList,
                new InternalPulloutCallback<LabelTypeToRoleTypeMapping, LabelType>() {
                    @Override
                    public LabelType getFr(final LabelTypeToRoleTypeMapping e) {
                        return e.getLabelType();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final LabelType e,
                            final List<LabelTypeToRoleTypeMapping> ls) {
                        e.setLabelTypeToRoleTypeMappingList(ls);
                    }
                });
    }

    /**
     * Pull out the list of foreign table 'RoleType'.
     * @param labelTypeToRoleTypeMappingList The list of labelTypeToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<RoleType> pulloutRoleType(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList) {
        return helpPulloutInternally(
                labelTypeToRoleTypeMappingList,
                new InternalPulloutCallback<LabelTypeToRoleTypeMapping, RoleType>() {
                    @Override
                    public RoleType getFr(final LabelTypeToRoleTypeMapping e) {
                        return e.getRoleType();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final RoleType e,
                            final List<LabelTypeToRoleTypeMapping> ls) {
                        e.setLabelTypeToRoleTypeMappingList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param labelTypeToRoleTypeMappingList The list of labelTypeToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList) {
        return helpExtractListInternally(
                labelTypeToRoleTypeMappingList,
                new InternalExtractCallback<LabelTypeToRoleTypeMapping, Long>() {
                    @Override
                    public Long getCV(final LabelTypeToRoleTypeMapping e) {
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
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * labelTypeToRoleTypeMapping.setFoo...(value);
     * labelTypeToRoleTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.set...;</span>
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">insert</span>(labelTypeToRoleTypeMapping);
     * ... = labelTypeToRoleTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param labelTypeToRoleTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping) {
        doInsert(labelTypeToRoleTypeMapping, null);
    }

    protected void doInsert(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final InsertOption<LabelTypeToRoleTypeMappingCB> option) {
        assertObjectNotNull("labelTypeToRoleTypeMapping",
                labelTypeToRoleTypeMapping);
        prepareInsertOption(option);
        delegateInsert(labelTypeToRoleTypeMapping, option);
    }

    protected void prepareInsertOption(
            final InsertOption<LabelTypeToRoleTypeMappingCB> option) {
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
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
     * labelTypeToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelTypeToRoleTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * labelTypeToRoleTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">update</span>(labelTypeToRoleTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param labelTypeToRoleTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping) {
        doUpdate(labelTypeToRoleTypeMapping, null);
    }

    protected void doUpdate(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> option) {
        assertObjectNotNull("labelTypeToRoleTypeMapping",
                labelTypeToRoleTypeMapping);
        prepareUpdateOption(option);
        helpUpdateInternally(labelTypeToRoleTypeMapping,
                new InternalUpdateCallback<LabelTypeToRoleTypeMapping>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final LabelTypeToRoleTypeMapping entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<LabelTypeToRoleTypeMappingCB> option) {
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

    protected LabelTypeToRoleTypeMappingCB createCBForVaryingUpdate() {
        final LabelTypeToRoleTypeMappingCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected LabelTypeToRoleTypeMappingCB createCBForSpecifiedUpdate() {
        final LabelTypeToRoleTypeMappingCB cb = newMyConditionBean();
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
     * @param labelTypeToRoleTypeMapping The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping) {
        doInesrtOrUpdate(labelTypeToRoleTypeMapping, null, null);
    }

    protected void doInesrtOrUpdate(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final InsertOption<LabelTypeToRoleTypeMappingCB> insertOption,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> updateOption) {
        helpInsertOrUpdateInternally(
                labelTypeToRoleTypeMapping,
                new InternalInsertOrUpdateCallback<LabelTypeToRoleTypeMapping, LabelTypeToRoleTypeMappingCB>() {
                    @Override
                    public void callbackInsert(
                            final LabelTypeToRoleTypeMapping entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(
                            final LabelTypeToRoleTypeMapping entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public LabelTypeToRoleTypeMappingCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(
                            final LabelTypeToRoleTypeMappingCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<LabelTypeToRoleTypeMappingCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<LabelTypeToRoleTypeMappingCB>()
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
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
     * labelTypeToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * labelTypeToRoleTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">delete</span>(labelTypeToRoleTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param labelTypeToRoleTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping) {
        doDelete(labelTypeToRoleTypeMapping, null);
    }

    protected void doDelete(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> option) {
        assertObjectNotNull("labelTypeToRoleTypeMapping",
                labelTypeToRoleTypeMapping);
        prepareDeleteOption(option);
        helpDeleteInternally(labelTypeToRoleTypeMapping,
                new InternalDeleteCallback<LabelTypeToRoleTypeMapping>() {
                    @Override
                    public int callbackDelegateDelete(
                            final LabelTypeToRoleTypeMapping entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<LabelTypeToRoleTypeMappingCB> option) {
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
     * @param labelTypeToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList) {
        return doBatchInsert(labelTypeToRoleTypeMappingList, null);
    }

    protected int[] doBatchInsert(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList,
            final InsertOption<LabelTypeToRoleTypeMappingCB> option) {
        assertObjectNotNull("labelTypeToRoleTypeMappingList",
                labelTypeToRoleTypeMappingList);
        prepareInsertOption(option);
        return delegateBatchInsert(labelTypeToRoleTypeMappingList, option);
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
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(labelTypeToRoleTypeMappingList, new SpecifyQuery<LabelTypeToRoleTypeMappingCB>() {
     *     public void specify(LabelTypeToRoleTypeMappingCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param labelTypeToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList) {
        return doBatchUpdate(labelTypeToRoleTypeMappingList, null);
    }

    protected int[] doBatchUpdate(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> option) {
        assertObjectNotNull("labelTypeToRoleTypeMappingList",
                labelTypeToRoleTypeMappingList);
        prepareBatchUpdateOption(labelTypeToRoleTypeMappingList, option);
        return delegateBatchUpdate(labelTypeToRoleTypeMappingList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(labelTypeToRoleTypeMappingList);
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
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(labelTypeToRoleTypeMappingList, new SpecifyQuery<LabelTypeToRoleTypeMappingCB>() {
     *     public void specify(LabelTypeToRoleTypeMappingCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(labelTypeToRoleTypeMappingList, new SpecifyQuery<LabelTypeToRoleTypeMappingCB>() {
     *     public void specify(LabelTypeToRoleTypeMappingCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param labelTypeToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList,
            final SpecifyQuery<LabelTypeToRoleTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(labelTypeToRoleTypeMappingList,
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
     * @param labelTypeToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList) {
        return doBatchDelete(labelTypeToRoleTypeMappingList, null);
    }

    protected int[] doBatchDelete(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> option) {
        assertObjectNotNull("labelTypeToRoleTypeMappingList",
                labelTypeToRoleTypeMappingList);
        prepareDeleteOption(option);
        return delegateBatchDelete(labelTypeToRoleTypeMappingList, option);
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
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;LabelTypeToRoleTypeMapping, LabelTypeToRoleTypeMappingCB&gt;() {
     *     public ConditionBean setup(labelTypeToRoleTypeMapping entity, LabelTypeToRoleTypeMappingCB intoCB) {
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
            final QueryInsertSetupper<LabelTypeToRoleTypeMapping, LabelTypeToRoleTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<LabelTypeToRoleTypeMapping, LabelTypeToRoleTypeMappingCB> setupper,
            final InsertOption<LabelTypeToRoleTypeMappingCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final LabelTypeToRoleTypeMapping entity = new LabelTypeToRoleTypeMapping();
        final LabelTypeToRoleTypeMappingCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected LabelTypeToRoleTypeMappingCB createCBForQueryInsert() {
        final LabelTypeToRoleTypeMappingCB cb = newMyConditionBean();
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
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.setPK...(value);</span>
     * labelTypeToRoleTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.setVersionNo(value);</span>
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">queryUpdate</span>(labelTypeToRoleTypeMapping, cb);
     * </pre>
     * @param labelTypeToRoleTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final LabelTypeToRoleTypeMappingCB cb) {
        return doQueryUpdate(labelTypeToRoleTypeMapping, cb, null);
    }

    protected int doQueryUpdate(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final LabelTypeToRoleTypeMappingCB cb,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> option) {
        assertObjectNotNull("labelTypeToRoleTypeMapping",
                labelTypeToRoleTypeMapping);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                labelTypeToRoleTypeMapping, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity),
                    (LabelTypeToRoleTypeMappingCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (LabelTypeToRoleTypeMappingCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">queryDelete</span>(labelTypeToRoleTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final LabelTypeToRoleTypeMappingCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final LabelTypeToRoleTypeMappingCB cb,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((LabelTypeToRoleTypeMappingCB) cb);
        } else {
            return varyingQueryDelete((LabelTypeToRoleTypeMappingCB) cb,
                    downcast(option));
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
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * labelTypeToRoleTypeMapping.setFoo...(value);
     * labelTypeToRoleTypeMapping.setBar...(value);
     * InsertOption<LabelTypeToRoleTypeMappingCB> option = new InsertOption<LabelTypeToRoleTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">varyingInsert</span>(labelTypeToRoleTypeMapping, option);
     * ... = labelTypeToRoleTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param labelTypeToRoleTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final InsertOption<LabelTypeToRoleTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(labelTypeToRoleTypeMapping, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
     * labelTypeToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelTypeToRoleTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * labelTypeToRoleTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;LabelTypeToRoleTypeMappingCB&gt; option = new UpdateOption&lt;LabelTypeToRoleTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *         public void specify(LabelTypeToRoleTypeMappingCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">varyingUpdate</span>(labelTypeToRoleTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param labelTypeToRoleTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(labelTypeToRoleTypeMapping, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param labelTypeToRoleTypeMapping The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final InsertOption<LabelTypeToRoleTypeMappingCB> insertOption,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(labelTypeToRoleTypeMapping, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param labelTypeToRoleTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(labelTypeToRoleTypeMapping, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param labelTypeToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList,
            final InsertOption<LabelTypeToRoleTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(labelTypeToRoleTypeMappingList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param labelTypeToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(labelTypeToRoleTypeMappingList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param labelTypeToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(labelTypeToRoleTypeMappingList, option);
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
            final QueryInsertSetupper<LabelTypeToRoleTypeMapping, LabelTypeToRoleTypeMappingCB> setupper,
            final InsertOption<LabelTypeToRoleTypeMappingCB> option) {
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
     * LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.setPK...(value);</span>
     * labelTypeToRoleTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelTypeToRoleTypeMapping.setVersionNo(value);</span>
     * LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;LabelTypeToRoleTypeMappingCB&gt; option = new UpdateOption&lt;LabelTypeToRoleTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void specify(LabelTypeToRoleTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * labelTypeToRoleTypeMappingBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(labelTypeToRoleTypeMapping, cb, option);
     * </pre>
     * @param labelTypeToRoleTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(
            final LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping,
            final LabelTypeToRoleTypeMappingCB cb,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(labelTypeToRoleTypeMapping, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of LabelTypeToRoleTypeMapping. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final LabelTypeToRoleTypeMappingCB cb,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> option) {
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
    public OutsideSqlBasicExecutor<LabelTypeToRoleTypeMappingBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(
            final LabelTypeToRoleTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(
            final LabelTypeToRoleTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> void delegateSelectCursor(
            final LabelTypeToRoleTypeMappingCB cb,
            final EntityRowHandler<ENTITY> erh, final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends LabelTypeToRoleTypeMapping> List<ENTITY> delegateSelectList(
            final LabelTypeToRoleTypeMappingCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final LabelTypeToRoleTypeMapping e,
            final InsertOption<LabelTypeToRoleTypeMappingCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final LabelTypeToRoleTypeMapping e,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final LabelTypeToRoleTypeMapping e,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final LabelTypeToRoleTypeMapping e,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final LabelTypeToRoleTypeMapping e,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(
            final List<LabelTypeToRoleTypeMapping> ls,
            final InsertOption<LabelTypeToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(
            final List<LabelTypeToRoleTypeMapping> ls,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<LabelTypeToRoleTypeMapping> ls,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(
            final List<LabelTypeToRoleTypeMapping> ls,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<LabelTypeToRoleTypeMapping> ls,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final LabelTypeToRoleTypeMapping e,
            final LabelTypeToRoleTypeMappingCB inCB, final ConditionBean resCB,
            final InsertOption<LabelTypeToRoleTypeMappingCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final LabelTypeToRoleTypeMapping e,
            final LabelTypeToRoleTypeMappingCB cb,
            final UpdateOption<LabelTypeToRoleTypeMappingCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final LabelTypeToRoleTypeMappingCB cb,
            final DeleteOption<LabelTypeToRoleTypeMappingCB> op) {
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
    protected LabelTypeToRoleTypeMapping downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity,
                LabelTypeToRoleTypeMapping.class);
    }

    protected LabelTypeToRoleTypeMappingCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                LabelTypeToRoleTypeMappingCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<LabelTypeToRoleTypeMapping> downcast(
            final List<? extends Entity> entityList) {
        return (List<LabelTypeToRoleTypeMapping>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<LabelTypeToRoleTypeMappingCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<LabelTypeToRoleTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<LabelTypeToRoleTypeMappingCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<LabelTypeToRoleTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<LabelTypeToRoleTypeMappingCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<LabelTypeToRoleTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<LabelTypeToRoleTypeMapping, LabelTypeToRoleTypeMappingCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<LabelTypeToRoleTypeMapping, LabelTypeToRoleTypeMappingCB>) option;
    }
}
