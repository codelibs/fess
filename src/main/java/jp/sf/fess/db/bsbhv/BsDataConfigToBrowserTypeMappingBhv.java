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

import jp.sf.fess.db.bsentity.dbmeta.DataConfigToBrowserTypeMappingDbm;
import jp.sf.fess.db.cbean.DataConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.exbhv.DataConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exentity.BrowserType;
import jp.sf.fess.db.exentity.DataConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;

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
 * The behavior of DATA_CONFIG_TO_BROWSER_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, DATA_CONFIG_ID, BROWSER_TYPE_ID
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
 *     DATA_CRAWLING_CONFIG, BROWSER_TYPE
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     dataCrawlingConfig, browserType
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsDataConfigToBrowserTypeMappingBhv extends
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
        return "DATA_CONFIG_TO_BROWSER_TYPE_MAPPING";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return DataConfigToBrowserTypeMappingDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public DataConfigToBrowserTypeMappingDbm getMyDBMeta() {
        return DataConfigToBrowserTypeMappingDbm.getInstance();
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
    public DataConfigToBrowserTypeMapping newMyEntity() {
        return new DataConfigToBrowserTypeMapping();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public DataConfigToBrowserTypeMappingCB newMyConditionBean() {
        return new DataConfigToBrowserTypeMappingCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * int count = dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final DataConfigToBrowserTypeMappingCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(
            final DataConfigToBrowserTypeMappingCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final DataConfigToBrowserTypeMappingCB cb) { // called by selectPage(cb)
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
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (dataConfigToBrowserTypeMapping != null) {
     *     ... = dataConfigToBrowserTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public DataConfigToBrowserTypeMapping selectEntity(
            final DataConfigToBrowserTypeMappingCB cb) {
        return doSelectEntity(cb, DataConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> ENTITY doSelectEntity(
            final DataConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, DataConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToBrowserTypeMappingCB cb,
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
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = dataConfigToBrowserTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public DataConfigToBrowserTypeMapping selectEntityWithDeletedCheck(
            final DataConfigToBrowserTypeMappingCB cb) {
        return doSelectEntityWithDeletedCheck(cb,
                DataConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> ENTITY doSelectEntityWithDeletedCheck(
            final DataConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, DataConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToBrowserTypeMappingCB cb,
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
    public DataConfigToBrowserTypeMapping selectByPKValue(final Long id) {
        return doSelectByPKValue(id, DataConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> ENTITY doSelectByPKValue(
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
    public DataConfigToBrowserTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKValueWithDeletedCheck(id,
                DataConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private DataConfigToBrowserTypeMappingCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final DataConfigToBrowserTypeMappingCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;DataConfigToBrowserTypeMapping&gt; dataConfigToBrowserTypeMappingList = dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping : dataConfigToBrowserTypeMappingList) {
     *     ... = dataConfigToBrowserTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<DataConfigToBrowserTypeMapping> selectList(
            final DataConfigToBrowserTypeMappingCB cb) {
        return doSelectList(cb, DataConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> ListResultBean<ENTITY> doSelectList(
            final DataConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
                cb,
                entityType,
                new InternalSelectListCallback<ENTITY, DataConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToBrowserTypeMappingCB cb,
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
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;DataConfigToBrowserTypeMapping&gt; page = dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping : page) {
     *     ... = dataConfigToBrowserTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<DataConfigToBrowserTypeMapping> selectPage(
            final DataConfigToBrowserTypeMappingCB cb) {
        return doSelectPage(cb, DataConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> PagingResultBean<ENTITY> doSelectPage(
            final DataConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
                cb,
                entityType,
                new InternalSelectPageCallback<ENTITY, DataConfigToBrowserTypeMappingCB>() {
                    @Override
                    public int callbackSelectCount(
                            final DataConfigToBrowserTypeMappingCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToBrowserTypeMappingCB cb,
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
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;DataConfigToBrowserTypeMapping&gt;() {
     *     public void handle(DataConfigToBrowserTypeMapping entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @param entityRowHandler The handler of entity row of DataConfigToBrowserTypeMapping. (NotNull)
     */
    public void selectCursor(
            final DataConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<DataConfigToBrowserTypeMapping> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler,
                DataConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> void doSelectCursor(
            final DataConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<DataConfigToBrowserTypeMapping>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, DataConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final DataConfigToBrowserTypeMappingCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataConfigToBrowserTypeMappingCB cb,
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
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(DataConfigToBrowserTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<DataConfigToBrowserTypeMappingCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends DataConfigToBrowserTypeMappingCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param dataConfigToBrowserTypeMappingList The list of dataConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<DataCrawlingConfig> pulloutDataCrawlingConfig(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList) {
        return helpPulloutInternally(
                dataConfigToBrowserTypeMappingList,
                new InternalPulloutCallback<DataConfigToBrowserTypeMapping, DataCrawlingConfig>() {
                    @Override
                    public DataCrawlingConfig getFr(
                            final DataConfigToBrowserTypeMapping e) {
                        return e.getDataCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final DataCrawlingConfig e,
                            final List<DataConfigToBrowserTypeMapping> ls) {
                        e.setDataConfigToBrowserTypeMappingList(ls);
                    }
                });
    }

    /**
     * Pull out the list of foreign table 'BrowserType'.
     * @param dataConfigToBrowserTypeMappingList The list of dataConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<BrowserType> pulloutBrowserType(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList) {
        return helpPulloutInternally(
                dataConfigToBrowserTypeMappingList,
                new InternalPulloutCallback<DataConfigToBrowserTypeMapping, BrowserType>() {
                    @Override
                    public BrowserType getFr(
                            final DataConfigToBrowserTypeMapping e) {
                        return e.getBrowserType();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final BrowserType e,
                            final List<DataConfigToBrowserTypeMapping> ls) {
                        e.setDataConfigToBrowserTypeMappingList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param dataConfigToBrowserTypeMappingList The list of dataConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList) {
        return helpExtractListInternally(
                dataConfigToBrowserTypeMappingList,
                new InternalExtractCallback<DataConfigToBrowserTypeMapping, Long>() {
                    @Override
                    public Long getCV(final DataConfigToBrowserTypeMapping e) {
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
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = new DataConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * dataConfigToBrowserTypeMapping.setFoo...(value);
     * dataConfigToBrowserTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.set...;</span>
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">insert</span>(dataConfigToBrowserTypeMapping);
     * ... = dataConfigToBrowserTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param dataConfigToBrowserTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping) {
        doInsert(dataConfigToBrowserTypeMapping, null);
    }

    protected void doInsert(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final InsertOption<DataConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToBrowserTypeMapping",
                dataConfigToBrowserTypeMapping);
        prepareInsertOption(option);
        delegateInsert(dataConfigToBrowserTypeMapping, option);
    }

    protected void prepareInsertOption(
            final InsertOption<DataConfigToBrowserTypeMappingCB> option) {
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
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = new DataConfigToBrowserTypeMapping();
     * dataConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataConfigToBrowserTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">update</span>(dataConfigToBrowserTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param dataConfigToBrowserTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping) {
        doUpdate(dataConfigToBrowserTypeMapping, null);
    }

    protected void doUpdate(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToBrowserTypeMapping",
                dataConfigToBrowserTypeMapping);
        prepareUpdateOption(option);
        helpUpdateInternally(dataConfigToBrowserTypeMapping,
                new InternalUpdateCallback<DataConfigToBrowserTypeMapping>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final DataConfigToBrowserTypeMapping entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<DataConfigToBrowserTypeMappingCB> option) {
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

    protected DataConfigToBrowserTypeMappingCB createCBForVaryingUpdate() {
        final DataConfigToBrowserTypeMappingCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected DataConfigToBrowserTypeMappingCB createCBForSpecifiedUpdate() {
        final DataConfigToBrowserTypeMappingCB cb = newMyConditionBean();
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
     * @param dataConfigToBrowserTypeMapping The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping) {
        doInesrtOrUpdate(dataConfigToBrowserTypeMapping, null, null);
    }

    protected void doInesrtOrUpdate(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final InsertOption<DataConfigToBrowserTypeMappingCB> insertOption,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> updateOption) {
        helpInsertOrUpdateInternally(
                dataConfigToBrowserTypeMapping,
                new InternalInsertOrUpdateCallback<DataConfigToBrowserTypeMapping, DataConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void callbackInsert(
                            final DataConfigToBrowserTypeMapping entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(
                            final DataConfigToBrowserTypeMapping entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public DataConfigToBrowserTypeMappingCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(
                            final DataConfigToBrowserTypeMappingCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<DataConfigToBrowserTypeMappingCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<DataConfigToBrowserTypeMappingCB>()
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
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = new DataConfigToBrowserTypeMapping();
     * dataConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">delete</span>(dataConfigToBrowserTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param dataConfigToBrowserTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping) {
        doDelete(dataConfigToBrowserTypeMapping, null);
    }

    protected void doDelete(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToBrowserTypeMapping",
                dataConfigToBrowserTypeMapping);
        prepareDeleteOption(option);
        helpDeleteInternally(dataConfigToBrowserTypeMapping,
                new InternalDeleteCallback<DataConfigToBrowserTypeMapping>() {
                    @Override
                    public int callbackDelegateDelete(
                            final DataConfigToBrowserTypeMapping entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<DataConfigToBrowserTypeMappingCB> option) {
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
     * @param dataConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList) {
        return doBatchInsert(dataConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchInsert(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList,
            final InsertOption<DataConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToBrowserTypeMappingList",
                dataConfigToBrowserTypeMappingList);
        prepareInsertOption(option);
        return delegateBatchInsert(dataConfigToBrowserTypeMappingList, option);
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
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(dataConfigToBrowserTypeMappingList, new SpecifyQuery<DataConfigToBrowserTypeMappingCB>() {
     *     public void specify(DataConfigToBrowserTypeMappingCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param dataConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList) {
        return doBatchUpdate(dataConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchUpdate(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToBrowserTypeMappingList",
                dataConfigToBrowserTypeMappingList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(dataConfigToBrowserTypeMappingList, option);
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
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(dataConfigToBrowserTypeMappingList, new SpecifyQuery<DataConfigToBrowserTypeMappingCB>() {
     *     public void specify(DataConfigToBrowserTypeMappingCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param dataConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList,
            final SpecifyQuery<DataConfigToBrowserTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(dataConfigToBrowserTypeMappingList,
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
     * @param dataConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList) {
        return doBatchDelete(dataConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchDelete(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToBrowserTypeMappingList",
                dataConfigToBrowserTypeMappingList);
        prepareDeleteOption(option);
        return delegateBatchDelete(dataConfigToBrowserTypeMappingList, option);
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
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;DataConfigToBrowserTypeMapping, DataConfigToBrowserTypeMappingCB&gt;() {
     *     public ConditionBean setup(dataConfigToBrowserTypeMapping entity, DataConfigToBrowserTypeMappingCB intoCB) {
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
            final QueryInsertSetupper<DataConfigToBrowserTypeMapping, DataConfigToBrowserTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<DataConfigToBrowserTypeMapping, DataConfigToBrowserTypeMappingCB> setupper,
            final InsertOption<DataConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final DataConfigToBrowserTypeMapping entity = new DataConfigToBrowserTypeMapping();
        final DataConfigToBrowserTypeMappingCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected DataConfigToBrowserTypeMappingCB createCBForQueryInsert() {
        final DataConfigToBrowserTypeMappingCB cb = newMyConditionBean();
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
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = new DataConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.setPK...(value);</span>
     * dataConfigToBrowserTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.setVersionNo(value);</span>
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryUpdate</span>(dataConfigToBrowserTypeMapping, cb);
     * </pre>
     * @param dataConfigToBrowserTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final DataConfigToBrowserTypeMappingCB cb) {
        return doQueryUpdate(dataConfigToBrowserTypeMapping, cb, null);
    }

    protected int doQueryUpdate(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final DataConfigToBrowserTypeMappingCB cb,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("dataConfigToBrowserTypeMapping",
                dataConfigToBrowserTypeMapping);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                dataConfigToBrowserTypeMapping, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity),
                    (DataConfigToBrowserTypeMappingCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (DataConfigToBrowserTypeMappingCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryDelete</span>(dataConfigToBrowserTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final DataConfigToBrowserTypeMappingCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final DataConfigToBrowserTypeMappingCB cb,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((DataConfigToBrowserTypeMappingCB) cb);
        } else {
            return varyingQueryDelete((DataConfigToBrowserTypeMappingCB) cb,
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
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = new DataConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * dataConfigToBrowserTypeMapping.setFoo...(value);
     * dataConfigToBrowserTypeMapping.setBar...(value);
     * InsertOption<DataConfigToBrowserTypeMappingCB> option = new InsertOption<DataConfigToBrowserTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingInsert</span>(dataConfigToBrowserTypeMapping, option);
     * ... = dataConfigToBrowserTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param dataConfigToBrowserTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final InsertOption<DataConfigToBrowserTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(dataConfigToBrowserTypeMapping, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = new DataConfigToBrowserTypeMapping();
     * dataConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataConfigToBrowserTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;DataConfigToBrowserTypeMappingCB&gt; option = new UpdateOption&lt;DataConfigToBrowserTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;DataConfigToBrowserTypeMappingCB&gt;() {
     *         public void specify(DataConfigToBrowserTypeMappingCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingUpdate</span>(dataConfigToBrowserTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param dataConfigToBrowserTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(dataConfigToBrowserTypeMapping, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param dataConfigToBrowserTypeMapping The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final InsertOption<DataConfigToBrowserTypeMappingCB> insertOption,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(dataConfigToBrowserTypeMapping, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param dataConfigToBrowserTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(dataConfigToBrowserTypeMapping, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param dataConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList,
            final InsertOption<DataConfigToBrowserTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(dataConfigToBrowserTypeMappingList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param dataConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(dataConfigToBrowserTypeMappingList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param dataConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(dataConfigToBrowserTypeMappingList, option);
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
            final QueryInsertSetupper<DataConfigToBrowserTypeMapping, DataConfigToBrowserTypeMappingCB> setupper,
            final InsertOption<DataConfigToBrowserTypeMappingCB> option) {
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
     * DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping = new DataConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.setPK...(value);</span>
     * dataConfigToBrowserTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataConfigToBrowserTypeMapping.setVersionNo(value);</span>
     * DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;DataConfigToBrowserTypeMappingCB&gt; option = new UpdateOption&lt;DataConfigToBrowserTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;DataConfigToBrowserTypeMappingCB&gt;() {
     *     public void specify(DataConfigToBrowserTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * dataConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(dataConfigToBrowserTypeMapping, cb, option);
     * </pre>
     * @param dataConfigToBrowserTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(
            final DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping,
            final DataConfigToBrowserTypeMappingCB cb,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(dataConfigToBrowserTypeMapping, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of DataConfigToBrowserTypeMapping. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final DataConfigToBrowserTypeMappingCB cb,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> option) {
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
    public OutsideSqlBasicExecutor<DataConfigToBrowserTypeMappingBhv> outsideSql() {
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
            final DataConfigToBrowserTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(
            final DataConfigToBrowserTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> void delegateSelectCursor(
            final DataConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<ENTITY> erh, final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends DataConfigToBrowserTypeMapping> List<ENTITY> delegateSelectList(
            final DataConfigToBrowserTypeMappingCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final DataConfigToBrowserTypeMapping e,
            final InsertOption<DataConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final DataConfigToBrowserTypeMapping e,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(
            final DataConfigToBrowserTypeMapping e,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final DataConfigToBrowserTypeMapping e,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(
            final DataConfigToBrowserTypeMapping e,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(
            final List<DataConfigToBrowserTypeMapping> ls,
            final InsertOption<DataConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(
            final List<DataConfigToBrowserTypeMapping> ls,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<DataConfigToBrowserTypeMapping> ls,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(
            final List<DataConfigToBrowserTypeMapping> ls,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<DataConfigToBrowserTypeMapping> ls,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final DataConfigToBrowserTypeMapping e,
            final DataConfigToBrowserTypeMappingCB inCB,
            final ConditionBean resCB,
            final InsertOption<DataConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final DataConfigToBrowserTypeMapping e,
            final DataConfigToBrowserTypeMappingCB cb,
            final UpdateOption<DataConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(
            final DataConfigToBrowserTypeMappingCB cb,
            final DeleteOption<DataConfigToBrowserTypeMappingCB> op) {
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
    protected DataConfigToBrowserTypeMapping downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity,
                DataConfigToBrowserTypeMapping.class);
    }

    protected DataConfigToBrowserTypeMappingCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                DataConfigToBrowserTypeMappingCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<DataConfigToBrowserTypeMapping> downcast(
            final List<? extends Entity> entityList) {
        return (List<DataConfigToBrowserTypeMapping>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<DataConfigToBrowserTypeMappingCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<DataConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<DataConfigToBrowserTypeMappingCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<DataConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<DataConfigToBrowserTypeMappingCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<DataConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<DataConfigToBrowserTypeMapping, DataConfigToBrowserTypeMappingCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<DataConfigToBrowserTypeMapping, DataConfigToBrowserTypeMappingCB>) option;
    }
}
