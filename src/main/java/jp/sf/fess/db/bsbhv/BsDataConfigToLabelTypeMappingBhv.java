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

import jp.sf.fess.db.bsentity.dbmeta.DataConfigToLabelTypeMappingDbm;
import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.exbhv.DataConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.db.exentity.LabelType;

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
 * The behavior of DATA_CONFIG_TO_LABEL_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, DATA_CONFIG_ID, LABEL_TYPE_ID
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
 *     DATA_CRAWLING_CONFIG, LABEL_TYPE
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     dataCrawlingConfig, labelType
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsDataConfigToLabelTypeMappingBhv extends
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
        return "DATA_CONFIG_TO_LABEL_TYPE_MAPPING";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return DataConfigToLabelTypeMappingDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public DataConfigToLabelTypeMappingDbm getMyDBMeta() {
        return DataConfigToLabelTypeMappingDbm.getInstance();
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
    public DataConfigToLabelTypeMapping newMyEntity() {
        return new DataConfigToLabelTypeMapping();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public DataConfigToLabelTypeMappingCB newMyConditionBean() {
        return new DataConfigToLabelTypeMappingCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * int count = dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final DataConfigToLabelTypeMappingCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final DataConfigToLabelTypeMappingCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final DataConfigToLabelTypeMappingCB cb) { // called by selectPage(cb)
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
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (dataConfigToLabelTypeMapping != null) {
     *     ... = dataConfigToLabelTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public DataConfigToLabelTypeMapping selectEntity(
            final DataConfigToLabelTypeMappingCB cb) {
        return doSelectEntity(cb, DataConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> ENTITY doSelectEntity(
            final DataConfigToLabelTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, DataConfigToLabelTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToLabelTypeMappingCB cb,
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
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = dataConfigToLabelTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public DataConfigToLabelTypeMapping selectEntityWithDeletedCheck(
            final DataConfigToLabelTypeMappingCB cb) {
        return doSelectEntityWithDeletedCheck(cb,
                DataConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> ENTITY doSelectEntityWithDeletedCheck(
            final DataConfigToLabelTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, DataConfigToLabelTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToLabelTypeMappingCB cb,
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
    public DataConfigToLabelTypeMapping selectByPKValue(final Long id) {
        return doSelectByPKValue(id, DataConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> ENTITY doSelectByPKValue(
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
    public DataConfigToLabelTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKValueWithDeletedCheck(id,
                DataConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private DataConfigToLabelTypeMappingCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final DataConfigToLabelTypeMappingCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;DataConfigToLabelTypeMapping&gt; dataConfigToLabelTypeMappingList = dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping : dataConfigToLabelTypeMappingList) {
     *     ... = dataConfigToLabelTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<DataConfigToLabelTypeMapping> selectList(
            final DataConfigToLabelTypeMappingCB cb) {
        return doSelectList(cb, DataConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> ListResultBean<ENTITY> doSelectList(
            final DataConfigToLabelTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
                cb,
                entityType,
                new InternalSelectListCallback<ENTITY, DataConfigToLabelTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToLabelTypeMappingCB cb,
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
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;DataConfigToLabelTypeMapping&gt; page = dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping : page) {
     *     ... = dataConfigToLabelTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<DataConfigToLabelTypeMapping> selectPage(
            final DataConfigToLabelTypeMappingCB cb) {
        return doSelectPage(cb, DataConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> PagingResultBean<ENTITY> doSelectPage(
            final DataConfigToLabelTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
                cb,
                entityType,
                new InternalSelectPageCallback<ENTITY, DataConfigToLabelTypeMappingCB>() {
                    @Override
                    public int callbackSelectCount(
                            final DataConfigToLabelTypeMappingCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToLabelTypeMappingCB cb,
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
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;DataConfigToLabelTypeMapping&gt;() {
     *     public void handle(DataConfigToLabelTypeMapping entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @param entityRowHandler The handler of entity row of DataConfigToLabelTypeMapping. (NotNull)
     */
    public void selectCursor(
            final DataConfigToLabelTypeMappingCB cb,
            final EntityRowHandler<DataConfigToLabelTypeMapping> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, DataConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> void doSelectCursor(
            final DataConfigToLabelTypeMappingCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<DataConfigToLabelTypeMapping>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, DataConfigToLabelTypeMappingCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final DataConfigToLabelTypeMappingCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToLabelTypeMappingCB cb,
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
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(DataConfigToLabelTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<DataConfigToLabelTypeMappingCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends DataConfigToLabelTypeMappingCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param dataConfigToLabelTypeMappingList The list of dataConfigToLabelTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<DataCrawlingConfig> pulloutDataCrawlingConfig(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList) {
        return helpPulloutInternally(
                dataConfigToLabelTypeMappingList,
                new InternalPulloutCallback<DataConfigToLabelTypeMapping, DataCrawlingConfig>() {
                    @Override
                    public DataCrawlingConfig getFr(
                            final DataConfigToLabelTypeMapping e) {
                        return e.getDataCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final DataCrawlingConfig e,
                            final List<DataConfigToLabelTypeMapping> ls) {
                        e.setDataConfigToLabelTypeMappingList(ls);
                    }
                });
    }

    /**
     * Pull out the list of foreign table 'LabelType'.
     * @param dataConfigToLabelTypeMappingList The list of dataConfigToLabelTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<LabelType> pulloutLabelType(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList) {
        return helpPulloutInternally(
                dataConfigToLabelTypeMappingList,
                new InternalPulloutCallback<DataConfigToLabelTypeMapping, LabelType>() {
                    @Override
                    public LabelType getFr(final DataConfigToLabelTypeMapping e) {
                        return e.getLabelType();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final LabelType e,
                            final List<DataConfigToLabelTypeMapping> ls) {
                        e.setDataConfigToLabelTypeMappingList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param dataConfigToLabelTypeMappingList The list of dataConfigToLabelTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList) {
        return helpExtractListInternally(
                dataConfigToLabelTypeMappingList,
                new InternalExtractCallback<DataConfigToLabelTypeMapping, Long>() {
                    @Override
                    public Long getCV(final DataConfigToLabelTypeMapping e) {
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
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * dataConfigToLabelTypeMapping.setFoo...(value);
     * dataConfigToLabelTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.set...;</span>
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">insert</span>(dataConfigToLabelTypeMapping);
     * ... = dataConfigToLabelTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param dataConfigToLabelTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping) {
        doInsert(dataConfigToLabelTypeMapping, null);
    }

    protected void doInsert(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final InsertOption<DataConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToLabelTypeMapping",
                dataConfigToLabelTypeMapping);
        prepareInsertOption(option);
        delegateInsert(dataConfigToLabelTypeMapping, option);
    }

    protected void prepareInsertOption(
            final InsertOption<DataConfigToLabelTypeMappingCB> option) {
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
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
     * dataConfigToLabelTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataConfigToLabelTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToLabelTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">update</span>(dataConfigToLabelTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param dataConfigToLabelTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping) {
        doUpdate(dataConfigToLabelTypeMapping, null);
    }

    protected void doUpdate(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final UpdateOption<DataConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToLabelTypeMapping",
                dataConfigToLabelTypeMapping);
        prepareUpdateOption(option);
        helpUpdateInternally(dataConfigToLabelTypeMapping,
                new InternalUpdateCallback<DataConfigToLabelTypeMapping>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final DataConfigToLabelTypeMapping entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<DataConfigToLabelTypeMappingCB> option) {
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

    protected DataConfigToLabelTypeMappingCB createCBForVaryingUpdate() {
        final DataConfigToLabelTypeMappingCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected DataConfigToLabelTypeMappingCB createCBForSpecifiedUpdate() {
        final DataConfigToLabelTypeMappingCB cb = newMyConditionBean();
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
     * @param dataConfigToLabelTypeMapping The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping) {
        doInesrtOrUpdate(dataConfigToLabelTypeMapping, null, null);
    }

    protected void doInesrtOrUpdate(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final InsertOption<DataConfigToLabelTypeMappingCB> insertOption,
            final UpdateOption<DataConfigToLabelTypeMappingCB> updateOption) {
        helpInsertOrUpdateInternally(
                dataConfigToLabelTypeMapping,
                new InternalInsertOrUpdateCallback<DataConfigToLabelTypeMapping, DataConfigToLabelTypeMappingCB>() {
                    @Override
                    public void callbackInsert(
                            final DataConfigToLabelTypeMapping entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(
                            final DataConfigToLabelTypeMapping entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public DataConfigToLabelTypeMappingCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(
                            final DataConfigToLabelTypeMappingCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<DataConfigToLabelTypeMappingCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<DataConfigToLabelTypeMappingCB>()
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
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
     * dataConfigToLabelTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToLabelTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">delete</span>(dataConfigToLabelTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param dataConfigToLabelTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping) {
        doDelete(dataConfigToLabelTypeMapping, null);
    }

    protected void doDelete(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final DeleteOption<DataConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToLabelTypeMapping",
                dataConfigToLabelTypeMapping);
        prepareDeleteOption(option);
        helpDeleteInternally(dataConfigToLabelTypeMapping,
                new InternalDeleteCallback<DataConfigToLabelTypeMapping>() {
                    @Override
                    public int callbackDelegateDelete(
                            final DataConfigToLabelTypeMapping entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<DataConfigToLabelTypeMappingCB> option) {
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
     * @param dataConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList) {
        return doBatchInsert(dataConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchInsert(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList,
            final InsertOption<DataConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToLabelTypeMappingList",
                dataConfigToLabelTypeMappingList);
        prepareInsertOption(option);
        return delegateBatchInsert(dataConfigToLabelTypeMappingList, option);
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
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(dataConfigToLabelTypeMappingList, new SpecifyQuery<DataConfigToLabelTypeMappingCB>() {
     *     public void specify(DataConfigToLabelTypeMappingCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param dataConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList) {
        return doBatchUpdate(dataConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchUpdate(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList,
            final UpdateOption<DataConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToLabelTypeMappingList",
                dataConfigToLabelTypeMappingList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(dataConfigToLabelTypeMappingList, option);
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
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(dataConfigToLabelTypeMappingList, new SpecifyQuery<DataConfigToLabelTypeMappingCB>() {
     *     public void specify(DataConfigToLabelTypeMappingCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param dataConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList,
            final SpecifyQuery<DataConfigToLabelTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(dataConfigToLabelTypeMappingList,
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
     * @param dataConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList) {
        return doBatchDelete(dataConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchDelete(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList,
            final DeleteOption<DataConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToLabelTypeMappingList",
                dataConfigToLabelTypeMappingList);
        prepareDeleteOption(option);
        return delegateBatchDelete(dataConfigToLabelTypeMappingList, option);
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
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;DataConfigToLabelTypeMapping, DataConfigToLabelTypeMappingCB&gt;() {
     *     public ConditionBean setup(dataConfigToLabelTypeMapping entity, DataConfigToLabelTypeMappingCB intoCB) {
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
            final QueryInsertSetupper<DataConfigToLabelTypeMapping, DataConfigToLabelTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<DataConfigToLabelTypeMapping, DataConfigToLabelTypeMappingCB> setupper,
            final InsertOption<DataConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final DataConfigToLabelTypeMapping entity = new DataConfigToLabelTypeMapping();
        final DataConfigToLabelTypeMappingCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected DataConfigToLabelTypeMappingCB createCBForQueryInsert() {
        final DataConfigToLabelTypeMappingCB cb = newMyConditionBean();
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
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.setPK...(value);</span>
     * dataConfigToLabelTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.setVersionNo(value);</span>
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">queryUpdate</span>(dataConfigToLabelTypeMapping, cb);
     * </pre>
     * @param dataConfigToLabelTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final DataConfigToLabelTypeMappingCB cb) {
        return doQueryUpdate(dataConfigToLabelTypeMapping, cb, null);
    }

    protected int doQueryUpdate(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final DataConfigToLabelTypeMappingCB cb,
            final UpdateOption<DataConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToLabelTypeMapping",
                dataConfigToLabelTypeMapping);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                dataConfigToLabelTypeMapping, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity),
                    (DataConfigToLabelTypeMappingCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (DataConfigToLabelTypeMappingCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">queryDelete</span>(dataConfigToLabelTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final DataConfigToLabelTypeMappingCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final DataConfigToLabelTypeMappingCB cb,
            final DeleteOption<DataConfigToLabelTypeMappingCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((DataConfigToLabelTypeMappingCB) cb);
        } else {
            return varyingQueryDelete((DataConfigToLabelTypeMappingCB) cb,
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
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * dataConfigToLabelTypeMapping.setFoo...(value);
     * dataConfigToLabelTypeMapping.setBar...(value);
     * InsertOption<DataConfigToLabelTypeMappingCB> option = new InsertOption<DataConfigToLabelTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">varyingInsert</span>(dataConfigToLabelTypeMapping, option);
     * ... = dataConfigToLabelTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param dataConfigToLabelTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final InsertOption<DataConfigToLabelTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(dataConfigToLabelTypeMapping, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
     * dataConfigToLabelTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataConfigToLabelTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToLabelTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;DataConfigToLabelTypeMappingCB&gt; option = new UpdateOption&lt;DataConfigToLabelTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *         public void specify(DataConfigToLabelTypeMappingCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">varyingUpdate</span>(dataConfigToLabelTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param dataConfigToLabelTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final UpdateOption<DataConfigToLabelTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(dataConfigToLabelTypeMapping, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param dataConfigToLabelTypeMapping The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final InsertOption<DataConfigToLabelTypeMappingCB> insertOption,
            final UpdateOption<DataConfigToLabelTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(dataConfigToLabelTypeMapping, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param dataConfigToLabelTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final DeleteOption<DataConfigToLabelTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(dataConfigToLabelTypeMapping, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param dataConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList,
            final InsertOption<DataConfigToLabelTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(dataConfigToLabelTypeMappingList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param dataConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList,
            final UpdateOption<DataConfigToLabelTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(dataConfigToLabelTypeMappingList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param dataConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList,
            final DeleteOption<DataConfigToLabelTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(dataConfigToLabelTypeMappingList, option);
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
            final QueryInsertSetupper<DataConfigToLabelTypeMapping, DataConfigToLabelTypeMappingCB> setupper,
            final InsertOption<DataConfigToLabelTypeMappingCB> option) {
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
     * DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.setPK...(value);</span>
     * dataConfigToLabelTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataConfigToLabelTypeMapping.setVersionNo(value);</span>
     * DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;DataConfigToLabelTypeMappingCB&gt; option = new UpdateOption&lt;DataConfigToLabelTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *     public void specify(DataConfigToLabelTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * dataConfigToLabelTypeMappingBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(dataConfigToLabelTypeMapping, cb, option);
     * </pre>
     * @param dataConfigToLabelTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(
            final DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping,
            final DataConfigToLabelTypeMappingCB cb,
            final UpdateOption<DataConfigToLabelTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(dataConfigToLabelTypeMapping, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of DataConfigToLabelTypeMapping. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final DataConfigToLabelTypeMappingCB cb,
            final DeleteOption<DataConfigToLabelTypeMappingCB> option) {
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
    public OutsideSqlBasicExecutor<DataConfigToLabelTypeMappingBhv> outsideSql() {
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
            final DataConfigToLabelTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(
            final DataConfigToLabelTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> void delegateSelectCursor(
            final DataConfigToLabelTypeMappingCB cb,
            final EntityRowHandler<ENTITY> erh, final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends DataConfigToLabelTypeMapping> List<ENTITY> delegateSelectList(
            final DataConfigToLabelTypeMappingCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final DataConfigToLabelTypeMapping e,
            final InsertOption<DataConfigToLabelTypeMappingCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final DataConfigToLabelTypeMapping e,
            final UpdateOption<DataConfigToLabelTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final DataConfigToLabelTypeMapping e,
            final UpdateOption<DataConfigToLabelTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final DataConfigToLabelTypeMapping e,
            final DeleteOption<DataConfigToLabelTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final DataConfigToLabelTypeMapping e,
            final DeleteOption<DataConfigToLabelTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(
            final List<DataConfigToLabelTypeMapping> ls,
            final InsertOption<DataConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(
            final List<DataConfigToLabelTypeMapping> ls,
            final UpdateOption<DataConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<DataConfigToLabelTypeMapping> ls,
            final UpdateOption<DataConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(
            final List<DataConfigToLabelTypeMapping> ls,
            final DeleteOption<DataConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<DataConfigToLabelTypeMapping> ls,
            final DeleteOption<DataConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final DataConfigToLabelTypeMapping e,
            final DataConfigToLabelTypeMappingCB inCB,
            final ConditionBean resCB,
            final InsertOption<DataConfigToLabelTypeMappingCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final DataConfigToLabelTypeMapping e,
            final DataConfigToLabelTypeMappingCB cb,
            final UpdateOption<DataConfigToLabelTypeMappingCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final DataConfigToLabelTypeMappingCB cb,
            final DeleteOption<DataConfigToLabelTypeMappingCB> op) {
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
    protected DataConfigToLabelTypeMapping downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity,
                DataConfigToLabelTypeMapping.class);
    }

    protected DataConfigToLabelTypeMappingCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                DataConfigToLabelTypeMappingCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<DataConfigToLabelTypeMapping> downcast(
            final List<? extends Entity> entityList) {
        return (List<DataConfigToLabelTypeMapping>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<DataConfigToLabelTypeMappingCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<DataConfigToLabelTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<DataConfigToLabelTypeMappingCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<DataConfigToLabelTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<DataConfigToLabelTypeMappingCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<DataConfigToLabelTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<DataConfigToLabelTypeMapping, DataConfigToLabelTypeMappingCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<DataConfigToLabelTypeMapping, DataConfigToLabelTypeMappingCB>) option;
    }
}
