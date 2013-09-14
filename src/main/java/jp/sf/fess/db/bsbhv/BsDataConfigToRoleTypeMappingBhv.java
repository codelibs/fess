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

import jp.sf.fess.db.bsentity.dbmeta.DataConfigToRoleTypeMappingDbm;
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.DataConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
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
 * The behavior of DATA_CONFIG_TO_ROLE_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, DATA_CONFIG_ID, ROLE_TYPE_ID
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
 *     DATA_CRAWLING_CONFIG, ROLE_TYPE
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     dataCrawlingConfig, roleType
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsDataConfigToRoleTypeMappingBhv extends
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
        return "DATA_CONFIG_TO_ROLE_TYPE_MAPPING";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return DataConfigToRoleTypeMappingDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public DataConfigToRoleTypeMappingDbm getMyDBMeta() {
        return DataConfigToRoleTypeMappingDbm.getInstance();
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
    public DataConfigToRoleTypeMapping newMyEntity() {
        return new DataConfigToRoleTypeMapping();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public DataConfigToRoleTypeMappingCB newMyConditionBean() {
        return new DataConfigToRoleTypeMappingCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * int count = dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final DataConfigToRoleTypeMappingCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final DataConfigToRoleTypeMappingCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final DataConfigToRoleTypeMappingCB cb) { // called by selectPage(cb)
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
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (dataConfigToRoleTypeMapping != null) {
     *     ... = dataConfigToRoleTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public DataConfigToRoleTypeMapping selectEntity(
            final DataConfigToRoleTypeMappingCB cb) {
        return doSelectEntity(cb, DataConfigToRoleTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> ENTITY doSelectEntity(
            final DataConfigToRoleTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToRoleTypeMappingCB cb,
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
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = dataConfigToRoleTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public DataConfigToRoleTypeMapping selectEntityWithDeletedCheck(
            final DataConfigToRoleTypeMappingCB cb) {
        return doSelectEntityWithDeletedCheck(cb,
                DataConfigToRoleTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> ENTITY doSelectEntityWithDeletedCheck(
            final DataConfigToRoleTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToRoleTypeMappingCB cb,
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
    public DataConfigToRoleTypeMapping selectByPKValue(final Long id) {
        return doSelectByPKValue(id, DataConfigToRoleTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> ENTITY doSelectByPKValue(
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
    public DataConfigToRoleTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKValueWithDeletedCheck(id,
                DataConfigToRoleTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private DataConfigToRoleTypeMappingCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final DataConfigToRoleTypeMappingCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;DataConfigToRoleTypeMapping&gt; dataConfigToRoleTypeMappingList = dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping : dataConfigToRoleTypeMappingList) {
     *     ... = dataConfigToRoleTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<DataConfigToRoleTypeMapping> selectList(
            final DataConfigToRoleTypeMappingCB cb) {
        return doSelectList(cb, DataConfigToRoleTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> ListResultBean<ENTITY> doSelectList(
            final DataConfigToRoleTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
                cb,
                entityType,
                new InternalSelectListCallback<ENTITY, DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToRoleTypeMappingCB cb,
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
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;DataConfigToRoleTypeMapping&gt; page = dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping : page) {
     *     ... = dataConfigToRoleTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<DataConfigToRoleTypeMapping> selectPage(
            final DataConfigToRoleTypeMappingCB cb) {
        return doSelectPage(cb, DataConfigToRoleTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> PagingResultBean<ENTITY> doSelectPage(
            final DataConfigToRoleTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
                cb,
                entityType,
                new InternalSelectPageCallback<ENTITY, DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public int callbackSelectCount(
                            final DataConfigToRoleTypeMappingCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToRoleTypeMappingCB cb,
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
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;DataConfigToRoleTypeMapping&gt;() {
     *     public void handle(DataConfigToRoleTypeMapping entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @param entityRowHandler The handler of entity row of DataConfigToRoleTypeMapping. (NotNull)
     */
    public void selectCursor(final DataConfigToRoleTypeMappingCB cb,
            final EntityRowHandler<DataConfigToRoleTypeMapping> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, DataConfigToRoleTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> void doSelectCursor(
            final DataConfigToRoleTypeMappingCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<DataConfigToRoleTypeMapping>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final DataConfigToRoleTypeMappingCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToRoleTypeMappingCB cb,
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
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(DataConfigToRoleTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<DataConfigToRoleTypeMappingCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends DataConfigToRoleTypeMappingCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * Pull out the list of foreign table 'DataCrawlingConfig'.
     * @param dataConfigToRoleTypeMappingList The list of dataConfigToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<DataCrawlingConfig> pulloutDataCrawlingConfig(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList) {
        return helpPulloutInternally(
                dataConfigToRoleTypeMappingList,
                new InternalPulloutCallback<DataConfigToRoleTypeMapping, DataCrawlingConfig>() {
                    @Override
                    public DataCrawlingConfig getFr(
                            final DataConfigToRoleTypeMapping e) {
                        return e.getDataCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final DataCrawlingConfig e,
                            final List<DataConfigToRoleTypeMapping> ls) {
                        e.setDataConfigToRoleTypeMappingList(ls);
                    }
                });
    }

    /**
     * Pull out the list of foreign table 'RoleType'.
     * @param dataConfigToRoleTypeMappingList The list of dataConfigToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<RoleType> pulloutRoleType(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList) {
        return helpPulloutInternally(
                dataConfigToRoleTypeMappingList,
                new InternalPulloutCallback<DataConfigToRoleTypeMapping, RoleType>() {
                    @Override
                    public RoleType getFr(final DataConfigToRoleTypeMapping e) {
                        return e.getRoleType();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final RoleType e,
                            final List<DataConfigToRoleTypeMapping> ls) {
                        e.setDataConfigToRoleTypeMappingList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param dataConfigToRoleTypeMappingList The list of dataConfigToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList) {
        return helpExtractListInternally(
                dataConfigToRoleTypeMappingList,
                new InternalExtractCallback<DataConfigToRoleTypeMapping, Long>() {
                    @Override
                    public Long getCV(final DataConfigToRoleTypeMapping e) {
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
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * dataConfigToRoleTypeMapping.setFoo...(value);
     * dataConfigToRoleTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.set...;</span>
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">insert</span>(dataConfigToRoleTypeMapping);
     * ... = dataConfigToRoleTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param dataConfigToRoleTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping) {
        doInsert(dataConfigToRoleTypeMapping, null);
    }

    protected void doInsert(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final InsertOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToRoleTypeMapping",
                dataConfigToRoleTypeMapping);
        prepareInsertOption(option);
        delegateInsert(dataConfigToRoleTypeMapping, option);
    }

    protected void prepareInsertOption(
            final InsertOption<DataConfigToRoleTypeMappingCB> option) {
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
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
     * dataConfigToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataConfigToRoleTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToRoleTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">update</span>(dataConfigToRoleTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param dataConfigToRoleTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping) {
        doUpdate(dataConfigToRoleTypeMapping, null);
    }

    protected void doUpdate(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final UpdateOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToRoleTypeMapping",
                dataConfigToRoleTypeMapping);
        prepareUpdateOption(option);
        helpUpdateInternally(dataConfigToRoleTypeMapping,
                new InternalUpdateCallback<DataConfigToRoleTypeMapping>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final DataConfigToRoleTypeMapping entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<DataConfigToRoleTypeMappingCB> option) {
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

    protected DataConfigToRoleTypeMappingCB createCBForVaryingUpdate() {
        final DataConfigToRoleTypeMappingCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected DataConfigToRoleTypeMappingCB createCBForSpecifiedUpdate() {
        final DataConfigToRoleTypeMappingCB cb = newMyConditionBean();
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
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, NonExclusiveControl)
     * @param dataConfigToRoleTypeMapping The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping) {
        doInesrtOrUpdate(dataConfigToRoleTypeMapping, null, null);
    }

    protected void doInesrtOrUpdate(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final InsertOption<DataConfigToRoleTypeMappingCB> insertOption,
            final UpdateOption<DataConfigToRoleTypeMappingCB> updateOption) {
        helpInsertOrUpdateInternally(
                dataConfigToRoleTypeMapping,
                new InternalInsertOrUpdateCallback<DataConfigToRoleTypeMapping, DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public void callbackInsert(
                            final DataConfigToRoleTypeMapping entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(
                            final DataConfigToRoleTypeMapping entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public DataConfigToRoleTypeMappingCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(
                            final DataConfigToRoleTypeMappingCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<DataConfigToRoleTypeMappingCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<DataConfigToRoleTypeMappingCB>()
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
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
     * dataConfigToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToRoleTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">delete</span>(dataConfigToRoleTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param dataConfigToRoleTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping) {
        doDelete(dataConfigToRoleTypeMapping, null);
    }

    protected void doDelete(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final DeleteOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToRoleTypeMapping",
                dataConfigToRoleTypeMapping);
        prepareDeleteOption(option);
        helpDeleteInternally(dataConfigToRoleTypeMapping,
                new InternalDeleteCallback<DataConfigToRoleTypeMapping>() {
                    @Override
                    public int callbackDelegateDelete(
                            final DataConfigToRoleTypeMapping entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<DataConfigToRoleTypeMappingCB> option) {
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
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are insert target. (so default constraints are not available in this method)</span> <br />
     * And if the table has an identity, entities after the process don't have incremented values.
     * When you use the (normal) insert(), an entity after the process has an incremented value.
     * @param dataConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList) {
        return doBatchInsert(dataConfigToRoleTypeMappingList, null);
    }

    protected int[] doBatchInsert(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList,
            final InsertOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToRoleTypeMappingList",
                dataConfigToRoleTypeMappingList);
        prepareInsertOption(option);
        return delegateBatchInsert(dataConfigToRoleTypeMappingList, option);
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
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span> <br />
     * So you should the other batchUpdate() method, which you can specify update columns like this:
     * <pre>
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(dataConfigToRoleTypeMappingList, new SpecifyQuery<DataConfigToRoleTypeMappingCB>() {
     *     public void specify(DataConfigToRoleTypeMappingCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param dataConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList) {
        return doBatchUpdate(dataConfigToRoleTypeMappingList, null);
    }

    protected int[] doBatchUpdate(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList,
            final UpdateOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToRoleTypeMappingList",
                dataConfigToRoleTypeMappingList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(dataConfigToRoleTypeMappingList, option);
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
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistick lock column because they are specified implicitly.
     * <pre>
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(dataConfigToRoleTypeMappingList, new SpecifyQuery<DataConfigToRoleTypeMappingCB>() {
     *     public void specify(DataConfigToRoleTypeMappingCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param dataConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList,
            final SpecifyQuery<DataConfigToRoleTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(dataConfigToRoleTypeMappingList,
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
     * @param dataConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList) {
        return doBatchDelete(dataConfigToRoleTypeMappingList, null);
    }

    protected int[] doBatchDelete(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList,
            final DeleteOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToRoleTypeMappingList",
                dataConfigToRoleTypeMappingList);
        prepareDeleteOption(option);
        return delegateBatchDelete(dataConfigToRoleTypeMappingList, option);
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
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;DataConfigToRoleTypeMapping, DataConfigToRoleTypeMappingCB&gt;() {
     *     public ConditionBean setup(dataConfigToRoleTypeMapping entity, DataConfigToRoleTypeMappingCB intoCB) {
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
            final QueryInsertSetupper<DataConfigToRoleTypeMapping, DataConfigToRoleTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<DataConfigToRoleTypeMapping, DataConfigToRoleTypeMappingCB> setupper,
            final InsertOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final DataConfigToRoleTypeMapping entity = new DataConfigToRoleTypeMapping();
        final DataConfigToRoleTypeMappingCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected DataConfigToRoleTypeMappingCB createCBForQueryInsert() {
        final DataConfigToRoleTypeMappingCB cb = newMyConditionBean();
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
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.setPK...(value);</span>
     * dataConfigToRoleTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.setVersionNo(value);</span>
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">queryUpdate</span>(dataConfigToRoleTypeMapping, cb);
     * </pre>
     * @param dataConfigToRoleTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final DataConfigToRoleTypeMappingCB cb) {
        return doQueryUpdate(dataConfigToRoleTypeMapping, cb, null);
    }

    protected int doQueryUpdate(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final DataConfigToRoleTypeMappingCB cb,
            final UpdateOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToRoleTypeMapping",
                dataConfigToRoleTypeMapping);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                dataConfigToRoleTypeMapping, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity),
                    (DataConfigToRoleTypeMappingCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (DataConfigToRoleTypeMappingCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">queryDelete</span>(dataConfigToRoleTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final DataConfigToRoleTypeMappingCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final DataConfigToRoleTypeMappingCB cb,
            final DeleteOption<DataConfigToRoleTypeMappingCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((DataConfigToRoleTypeMappingCB) cb);
        } else {
            return varyingQueryDelete((DataConfigToRoleTypeMappingCB) cb,
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
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * dataConfigToRoleTypeMapping.setFoo...(value);
     * dataConfigToRoleTypeMapping.setBar...(value);
     * InsertOption<DataConfigToRoleTypeMappingCB> option = new InsertOption<DataConfigToRoleTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">varyingInsert</span>(dataConfigToRoleTypeMapping, option);
     * ... = dataConfigToRoleTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param dataConfigToRoleTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final InsertOption<DataConfigToRoleTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(dataConfigToRoleTypeMapping, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
     * dataConfigToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataConfigToRoleTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToRoleTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;DataConfigToRoleTypeMappingCB&gt; option = new UpdateOption&lt;DataConfigToRoleTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *         public void specify(DataConfigToRoleTypeMappingCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">varyingUpdate</span>(dataConfigToRoleTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param dataConfigToRoleTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final UpdateOption<DataConfigToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(dataConfigToRoleTypeMapping, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param dataConfigToRoleTypeMapping The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final InsertOption<DataConfigToRoleTypeMappingCB> insertOption,
            final UpdateOption<DataConfigToRoleTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(dataConfigToRoleTypeMapping, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param dataConfigToRoleTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final DeleteOption<DataConfigToRoleTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(dataConfigToRoleTypeMapping, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param dataConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList,
            final InsertOption<DataConfigToRoleTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(dataConfigToRoleTypeMappingList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param dataConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList,
            final UpdateOption<DataConfigToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(dataConfigToRoleTypeMappingList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param dataConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList,
            final DeleteOption<DataConfigToRoleTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(dataConfigToRoleTypeMappingList, option);
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
            final QueryInsertSetupper<DataConfigToRoleTypeMapping, DataConfigToRoleTypeMappingCB> setupper,
            final InsertOption<DataConfigToRoleTypeMappingCB> option) {
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
     * DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.setPK...(value);</span>
     * dataConfigToRoleTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataConfigToRoleTypeMapping.setVersionNo(value);</span>
     * DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;DataConfigToRoleTypeMappingCB&gt; option = new UpdateOption&lt;DataConfigToRoleTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void specify(DataConfigToRoleTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * dataConfigToRoleTypeMappingBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(dataConfigToRoleTypeMapping, cb, option);
     * </pre>
     * @param dataConfigToRoleTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(
            final DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping,
            final DataConfigToRoleTypeMappingCB cb,
            final UpdateOption<DataConfigToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(dataConfigToRoleTypeMapping, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of DataConfigToRoleTypeMapping. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final DataConfigToRoleTypeMappingCB cb,
            final DeleteOption<DataConfigToRoleTypeMappingCB> option) {
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
    public OutsideSqlBasicExecutor<DataConfigToRoleTypeMappingBhv> outsideSql() {
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
            final DataConfigToRoleTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(
            final DataConfigToRoleTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> void delegateSelectCursor(
            final DataConfigToRoleTypeMappingCB cb,
            final EntityRowHandler<ENTITY> erh, final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends DataConfigToRoleTypeMapping> List<ENTITY> delegateSelectList(
            final DataConfigToRoleTypeMappingCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final DataConfigToRoleTypeMapping e,
            final InsertOption<DataConfigToRoleTypeMappingCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final DataConfigToRoleTypeMapping e,
            final UpdateOption<DataConfigToRoleTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final DataConfigToRoleTypeMapping e,
            final UpdateOption<DataConfigToRoleTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final DataConfigToRoleTypeMapping e,
            final DeleteOption<DataConfigToRoleTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final DataConfigToRoleTypeMapping e,
            final DeleteOption<DataConfigToRoleTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(
            final List<DataConfigToRoleTypeMapping> ls,
            final InsertOption<DataConfigToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(
            final List<DataConfigToRoleTypeMapping> ls,
            final UpdateOption<DataConfigToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<DataConfigToRoleTypeMapping> ls,
            final UpdateOption<DataConfigToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(
            final List<DataConfigToRoleTypeMapping> ls,
            final DeleteOption<DataConfigToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<DataConfigToRoleTypeMapping> ls,
            final DeleteOption<DataConfigToRoleTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final DataConfigToRoleTypeMapping e,
            final DataConfigToRoleTypeMappingCB inCB,
            final ConditionBean resCB,
            final InsertOption<DataConfigToRoleTypeMappingCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final DataConfigToRoleTypeMapping e,
            final DataConfigToRoleTypeMappingCB cb,
            final UpdateOption<DataConfigToRoleTypeMappingCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final DataConfigToRoleTypeMappingCB cb,
            final DeleteOption<DataConfigToRoleTypeMappingCB> op) {
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
    protected DataConfigToRoleTypeMapping downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity,
                DataConfigToRoleTypeMapping.class);
    }

    protected DataConfigToRoleTypeMappingCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                DataConfigToRoleTypeMappingCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<DataConfigToRoleTypeMapping> downcast(
            final List<? extends Entity> entityList) {
        return (List<DataConfigToRoleTypeMapping>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<DataConfigToRoleTypeMappingCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<DataConfigToRoleTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<DataConfigToRoleTypeMappingCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<DataConfigToRoleTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<DataConfigToRoleTypeMappingCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<DataConfigToRoleTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<DataConfigToRoleTypeMapping, DataConfigToRoleTypeMappingCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<DataConfigToRoleTypeMapping, DataConfigToRoleTypeMappingCB>) option;
    }
}
