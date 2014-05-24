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

import jp.sf.fess.db.bsentity.dbmeta.DataCrawlingConfigDbm;
import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.DataCrawlingConfigCB;
import jp.sf.fess.db.exbhv.DataConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.DataConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.DataCrawlingConfigBhv;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;

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
 * The behavior of DATA_CRAWLING_CONFIG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, NAME, HANDLER_NAME, HANDLER_PARAMETER, HANDLER_SCRIPT, BOOST, AVAILABLE, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     DATA_CONFIG_TO_LABEL_TYPE_MAPPING, DATA_CONFIG_TO_ROLE_TYPE_MAPPING
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     dataConfigToLabelTypeMappingList, dataConfigToRoleTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsDataCrawlingConfigBhv extends AbstractBehaviorWritable {

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
        return "DATA_CRAWLING_CONFIG";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return DataCrawlingConfigDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public DataCrawlingConfigDbm getMyDBMeta() {
        return DataCrawlingConfigDbm.getInstance();
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
    public DataCrawlingConfig newMyEntity() {
        return new DataCrawlingConfig();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public DataCrawlingConfigCB newMyConditionBean() {
        return new DataCrawlingConfigCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * int count = dataCrawlingConfigBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final DataCrawlingConfigCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final DataCrawlingConfigCB cb) { // called by selectCount(cb)
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final DataCrawlingConfigCB cb) { // called by selectPage(cb)
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
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * DataCrawlingConfig dataCrawlingConfig = dataCrawlingConfigBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (dataCrawlingConfig != null) {
     *     ... = dataCrawlingConfig.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public DataCrawlingConfig selectEntity(final DataCrawlingConfigCB cb) {
        return doSelectEntity(cb, DataCrawlingConfig.class);
    }

    protected <ENTITY extends DataCrawlingConfig> ENTITY doSelectEntity(
            final DataCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, DataCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataCrawlingConfigCB cb,
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
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * DataCrawlingConfig dataCrawlingConfig = dataCrawlingConfigBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = dataCrawlingConfig.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public DataCrawlingConfig selectEntityWithDeletedCheck(
            final DataCrawlingConfigCB cb) {
        return doSelectEntityWithDeletedCheck(cb, DataCrawlingConfig.class);
    }

    protected <ENTITY extends DataCrawlingConfig> ENTITY doSelectEntityWithDeletedCheck(
            final DataCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, DataCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataCrawlingConfigCB cb,
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
    public DataCrawlingConfig selectByPKValue(final Long id) {
        return doSelectByPKValue(id, DataCrawlingConfig.class);
    }

    protected <ENTITY extends DataCrawlingConfig> ENTITY doSelectByPKValue(
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
    public DataCrawlingConfig selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, DataCrawlingConfig.class);
    }

    protected <ENTITY extends DataCrawlingConfig> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private DataCrawlingConfigCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final DataCrawlingConfigCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;DataCrawlingConfig&gt; dataCrawlingConfigList = dataCrawlingConfigBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (DataCrawlingConfig dataCrawlingConfig : dataCrawlingConfigList) {
     *     ... = dataCrawlingConfig.get...();
     * }
     * </pre>
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<DataCrawlingConfig> selectList(
            final DataCrawlingConfigCB cb) {
        return doSelectList(cb, DataCrawlingConfig.class);
    }

    protected <ENTITY extends DataCrawlingConfig> ListResultBean<ENTITY> doSelectList(
            final DataCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, DataCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataCrawlingConfigCB cb,
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
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;DataCrawlingConfig&gt; page = dataCrawlingConfigBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (DataCrawlingConfig dataCrawlingConfig : page) {
     *     ... = dataCrawlingConfig.get...();
     * }
     * </pre>
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<DataCrawlingConfig> selectPage(
            final DataCrawlingConfigCB cb) {
        return doSelectPage(cb, DataCrawlingConfig.class);
    }

    protected <ENTITY extends DataCrawlingConfig> PagingResultBean<ENTITY> doSelectPage(
            final DataCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, DataCrawlingConfigCB>() {
                    @Override
                    public int callbackSelectCount(final DataCrawlingConfigCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataCrawlingConfigCB cb,
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
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * dataCrawlingConfigBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;DataCrawlingConfig&gt;() {
     *     public void handle(DataCrawlingConfig entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @param entityRowHandler The handler of entity row of DataCrawlingConfig. (NotNull)
     */
    public void selectCursor(final DataCrawlingConfigCB cb,
            final EntityRowHandler<DataCrawlingConfig> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, DataCrawlingConfig.class);
    }

    protected <ENTITY extends DataCrawlingConfig> void doSelectCursor(
            final DataCrawlingConfigCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<DataCrawlingConfig>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, DataCrawlingConfigCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final DataCrawlingConfigCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final DataCrawlingConfigCB cb,
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
     * dataCrawlingConfigBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(DataCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<DataCrawlingConfigCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends DataCrawlingConfigCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param dataCrawlingConfig The entity of dataCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadDataConfigToLabelTypeMappingList(
            final DataCrawlingConfig dataCrawlingConfig,
            final ConditionBeanSetupper<DataConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(dataCrawlingConfig, conditionBeanSetupper);
        loadDataConfigToLabelTypeMappingList(xnewLRLs(dataCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of dataConfigToLabelTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToLabelTypeMappingList'.
     * <pre>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">loadDataConfigToLabelTypeMappingList</span>(dataCrawlingConfigList, new ConditionBeanSetupper&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(DataConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (DataCrawlingConfig dataCrawlingConfig : dataCrawlingConfigList) {
     *     ... = dataCrawlingConfig.<span style="color: #FD4747">getDataConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setDataConfigId_InScope(pkList);
     * cb.query().addOrderBy_DataConfigId_Asc();
     * </pre>
     * @param dataCrawlingConfigList The entity list of dataCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadDataConfigToLabelTypeMappingList(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final ConditionBeanSetupper<DataConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(dataCrawlingConfigList, conditionBeanSetupper);
        loadDataConfigToLabelTypeMappingList(
                dataCrawlingConfigList,
                new LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param dataCrawlingConfig The entity of dataCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadDataConfigToLabelTypeMappingList(
            final DataCrawlingConfig dataCrawlingConfig,
            final LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(dataCrawlingConfig, loadReferrerOption);
        loadDataConfigToLabelTypeMappingList(xnewLRLs(dataCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param dataCrawlingConfigList The entity list of dataCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadDataConfigToLabelTypeMappingList(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(dataCrawlingConfigList, loadReferrerOption);
        if (dataCrawlingConfigList.isEmpty()) {
            return;
        }
        final DataConfigToLabelTypeMappingBhv referrerBhv = xgetBSFLR().select(
                DataConfigToLabelTypeMappingBhv.class);
        helpLoadReferrerInternally(
                dataCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<DataCrawlingConfig, Long, DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping>() {
                    @Override
                    public Long getPKVal(final DataCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final DataCrawlingConfig e,
                            final List<DataConfigToLabelTypeMapping> ls) {
                        e.setDataConfigToLabelTypeMappingList(ls);
                    }

                    @Override
                    public DataConfigToLabelTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final DataConfigToLabelTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setDataConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(
                            final DataConfigToLabelTypeMappingCB cb) {
                        cb.query().addOrderBy_DataConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final DataConfigToLabelTypeMappingCB cb) {
                        cb.specify().columnDataConfigId();
                    }

                    @Override
                    public List<DataConfigToLabelTypeMapping> selRfLs(
                            final DataConfigToLabelTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final DataConfigToLabelTypeMapping e) {
                        return e.getDataConfigId();
                    }

                    @Override
                    public void setlcEt(final DataConfigToLabelTypeMapping re,
                            final DataCrawlingConfig le) {
                        re.setDataCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "dataConfigToLabelTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param dataCrawlingConfig The entity of dataCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadDataConfigToRoleTypeMappingList(
            final DataCrawlingConfig dataCrawlingConfig,
            final ConditionBeanSetupper<DataConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(dataCrawlingConfig, conditionBeanSetupper);
        loadDataConfigToRoleTypeMappingList(xnewLRLs(dataCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of dataConfigToRoleTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToRoleTypeMappingList'.
     * <pre>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">loadDataConfigToRoleTypeMappingList</span>(dataCrawlingConfigList, new ConditionBeanSetupper&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(DataConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (DataCrawlingConfig dataCrawlingConfig : dataCrawlingConfigList) {
     *     ... = dataCrawlingConfig.<span style="color: #FD4747">getDataConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setDataConfigId_InScope(pkList);
     * cb.query().addOrderBy_DataConfigId_Asc();
     * </pre>
     * @param dataCrawlingConfigList The entity list of dataCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadDataConfigToRoleTypeMappingList(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final ConditionBeanSetupper<DataConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(dataCrawlingConfigList, conditionBeanSetupper);
        loadDataConfigToRoleTypeMappingList(
                dataCrawlingConfigList,
                new LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param dataCrawlingConfig The entity of dataCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadDataConfigToRoleTypeMappingList(
            final DataCrawlingConfig dataCrawlingConfig,
            final LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(dataCrawlingConfig, loadReferrerOption);
        loadDataConfigToRoleTypeMappingList(xnewLRLs(dataCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param dataCrawlingConfigList The entity list of dataCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadDataConfigToRoleTypeMappingList(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(dataCrawlingConfigList, loadReferrerOption);
        if (dataCrawlingConfigList.isEmpty()) {
            return;
        }
        final DataConfigToRoleTypeMappingBhv referrerBhv = xgetBSFLR().select(
                DataConfigToRoleTypeMappingBhv.class);
        helpLoadReferrerInternally(
                dataCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<DataCrawlingConfig, Long, DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping>() {
                    @Override
                    public Long getPKVal(final DataCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final DataCrawlingConfig e,
                            final List<DataConfigToRoleTypeMapping> ls) {
                        e.setDataConfigToRoleTypeMappingList(ls);
                    }

                    @Override
                    public DataConfigToRoleTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final DataConfigToRoleTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setDataConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final DataConfigToRoleTypeMappingCB cb) {
                        cb.query().addOrderBy_DataConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final DataConfigToRoleTypeMappingCB cb) {
                        cb.specify().columnDataConfigId();
                    }

                    @Override
                    public List<DataConfigToRoleTypeMapping> selRfLs(
                            final DataConfigToRoleTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final DataConfigToRoleTypeMapping e) {
                        return e.getDataConfigId();
                    }

                    @Override
                    public void setlcEt(final DataConfigToRoleTypeMapping re,
                            final DataCrawlingConfig le) {
                        re.setDataCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "dataConfigToRoleTypeMappingList";
                    }
                });
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param dataCrawlingConfigList The list of dataCrawlingConfig. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<DataCrawlingConfig> dataCrawlingConfigList) {
        return helpExtractListInternally(dataCrawlingConfigList,
                new InternalExtractCallback<DataCrawlingConfig, Long>() {
                    @Override
                    public Long getCV(final DataCrawlingConfig e) {
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
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * dataCrawlingConfig.setFoo...(value);
     * dataCrawlingConfig.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.set...;</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">insert</span>(dataCrawlingConfig);
     * ... = dataCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param dataCrawlingConfig The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final DataCrawlingConfig dataCrawlingConfig) {
        doInsert(dataCrawlingConfig, null);
    }

    protected void doInsert(final DataCrawlingConfig dataCrawlingConfig,
            final InsertOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfig", dataCrawlingConfig);
        prepareInsertOption(option);
        delegateInsert(dataCrawlingConfig, option);
    }

    protected void prepareInsertOption(
            final InsertOption<DataCrawlingConfigCB> option) {
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
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * dataCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     dataCrawlingConfigBhv.<span style="color: #FD4747">update</span>(dataCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param dataCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final DataCrawlingConfig dataCrawlingConfig) {
        doUpdate(dataCrawlingConfig, null);
    }

    protected void doUpdate(final DataCrawlingConfig dataCrawlingConfig,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfig", dataCrawlingConfig);
        prepareUpdateOption(option);
        helpUpdateInternally(dataCrawlingConfig,
                new InternalUpdateCallback<DataCrawlingConfig>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final DataCrawlingConfig entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<DataCrawlingConfigCB> option) {
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

    protected DataCrawlingConfigCB createCBForVaryingUpdate() {
        final DataCrawlingConfigCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected DataCrawlingConfigCB createCBForSpecifiedUpdate() {
        final DataCrawlingConfigCB cb = newMyConditionBean();
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
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * dataCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setVersionNo(value);</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">updateNonstrict</span>(dataCrawlingConfig);
     * </pre>
     * @param dataCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final DataCrawlingConfig dataCrawlingConfig) {
        doUpdateNonstrict(dataCrawlingConfig, null);
    }

    protected void doUpdateNonstrict(
            final DataCrawlingConfig dataCrawlingConfig,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfig", dataCrawlingConfig);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(dataCrawlingConfig,
                new InternalUpdateNonstrictCallback<DataCrawlingConfig>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final DataCrawlingConfig entity) {
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
     * @param dataCrawlingConfig The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final DataCrawlingConfig dataCrawlingConfig) {
        doInesrtOrUpdate(dataCrawlingConfig, null, null);
    }

    protected void doInesrtOrUpdate(
            final DataCrawlingConfig dataCrawlingConfig,
            final InsertOption<DataCrawlingConfigCB> insertOption,
            final UpdateOption<DataCrawlingConfigCB> updateOption) {
        helpInsertOrUpdateInternally(
                dataCrawlingConfig,
                new InternalInsertOrUpdateCallback<DataCrawlingConfig, DataCrawlingConfigCB>() {
                    @Override
                    public void callbackInsert(final DataCrawlingConfig entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final DataCrawlingConfig entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public DataCrawlingConfigCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final DataCrawlingConfigCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<DataCrawlingConfigCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<DataCrawlingConfigCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param dataCrawlingConfig The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(
            final DataCrawlingConfig dataCrawlingConfig) {
        doInesrtOrUpdateNonstrict(dataCrawlingConfig, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(
            final DataCrawlingConfig dataCrawlingConfig,
            final InsertOption<DataCrawlingConfigCB> insertOption,
            final UpdateOption<DataCrawlingConfigCB> updateOption) {
        helpInsertOrUpdateInternally(
                dataCrawlingConfig,
                new InternalInsertOrUpdateNonstrictCallback<DataCrawlingConfig>() {
                    @Override
                    public void callbackInsert(final DataCrawlingConfig entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(
                            final DataCrawlingConfig entity) {
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
            insertOption = insertOption == null ? new InsertOption<DataCrawlingConfigCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<DataCrawlingConfigCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * dataCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     dataCrawlingConfigBhv.<span style="color: #FD4747">delete</span>(dataCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param dataCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final DataCrawlingConfig dataCrawlingConfig) {
        doDelete(dataCrawlingConfig, null);
    }

    protected void doDelete(final DataCrawlingConfig dataCrawlingConfig,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfig", dataCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteInternally(dataCrawlingConfig,
                new InternalDeleteCallback<DataCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDelete(
                            final DataCrawlingConfig entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<DataCrawlingConfigCB> option) {
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
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * dataCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setVersionNo(value);</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">deleteNonstrict</span>(dataCrawlingConfig);
     * </pre>
     * @param dataCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final DataCrawlingConfig dataCrawlingConfig) {
        doDeleteNonstrict(dataCrawlingConfig, null);
    }

    protected void doDeleteNonstrict(
            final DataCrawlingConfig dataCrawlingConfig,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfig", dataCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(dataCrawlingConfig,
                new InternalDeleteNonstrictCallback<DataCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final DataCrawlingConfig entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * dataCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setVersionNo(value);</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(dataCrawlingConfig);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param dataCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(
            final DataCrawlingConfig dataCrawlingConfig) {
        doDeleteNonstrictIgnoreDeleted(dataCrawlingConfig, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(
            final DataCrawlingConfig dataCrawlingConfig,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfig", dataCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(
                dataCrawlingConfig,
                new InternalDeleteNonstrictIgnoreDeletedCallback<DataCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final DataCrawlingConfig entity) {
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
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<DataCrawlingConfig> dataCrawlingConfigList) {
        return doBatchInsert(dataCrawlingConfigList, null);
    }

    protected int[] doBatchInsert(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final InsertOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfigList", dataCrawlingConfigList);
        prepareInsertOption(option);
        return delegateBatchInsert(dataCrawlingConfigList, option);
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
     * dataCrawlingConfigBhv.<span style="color: #FD4747">batchUpdate</span>(dataCrawlingConfigList, new SpecifyQuery<DataCrawlingConfigCB>() {
     *     public void specify(DataCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<DataCrawlingConfig> dataCrawlingConfigList) {
        return doBatchUpdate(dataCrawlingConfigList, null);
    }

    protected int[] doBatchUpdate(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfigList", dataCrawlingConfigList);
        prepareBatchUpdateOption(dataCrawlingConfigList, option);
        return delegateBatchUpdate(dataCrawlingConfigList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final UpdateOption<DataCrawlingConfigCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(dataCrawlingConfigList);
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
     * dataCrawlingConfigBhv.<span style="color: #FD4747">batchUpdate</span>(dataCrawlingConfigList, new SpecifyQuery<DataCrawlingConfigCB>() {
     *     public void specify(DataCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">batchUpdate</span>(dataCrawlingConfigList, new SpecifyQuery<DataCrawlingConfigCB>() {
     *     public void specify(DataCrawlingConfigCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final SpecifyQuery<DataCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdate(dataCrawlingConfigList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdateNonstrict() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(dataCrawlingConfigList, new SpecifyQuery<DataCrawlingConfigCB>() {
     *     public void specify(DataCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<DataCrawlingConfig> dataCrawlingConfigList) {
        return doBatchUpdateNonstrict(dataCrawlingConfigList, null);
    }

    protected int[] doBatchUpdateNonstrict(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfigList", dataCrawlingConfigList);
        prepareBatchUpdateOption(dataCrawlingConfigList, option);
        return delegateBatchUpdateNonstrict(dataCrawlingConfigList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(dataCrawlingConfigList, new SpecifyQuery<DataCrawlingConfigCB>() {
     *     public void specify(DataCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(dataCrawlingConfigList, new SpecifyQuery<DataCrawlingConfigCB>() {
     *     public void specify(DataCrawlingConfigCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final SpecifyQuery<DataCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(dataCrawlingConfigList,
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
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(
            final List<DataCrawlingConfig> dataCrawlingConfigList) {
        return doBatchDelete(dataCrawlingConfigList, null);
    }

    protected int[] doBatchDelete(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfigList", dataCrawlingConfigList);
        prepareDeleteOption(option);
        return delegateBatchDelete(dataCrawlingConfigList, option);
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
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<DataCrawlingConfig> dataCrawlingConfigList) {
        return doBatchDeleteNonstrict(dataCrawlingConfigList, null);
    }

    protected int[] doBatchDeleteNonstrict(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfigList", dataCrawlingConfigList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(dataCrawlingConfigList, option);
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
     * dataCrawlingConfigBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;DataCrawlingConfig, DataCrawlingConfigCB&gt;() {
     *     public ConditionBean setup(dataCrawlingConfig entity, DataCrawlingConfigCB intoCB) {
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
            final QueryInsertSetupper<DataCrawlingConfig, DataCrawlingConfigCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<DataCrawlingConfig, DataCrawlingConfigCB> setupper,
            final InsertOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final DataCrawlingConfig entity = new DataCrawlingConfig();
        final DataCrawlingConfigCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected DataCrawlingConfigCB createCBForQueryInsert() {
        final DataCrawlingConfigCB cb = newMyConditionBean();
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
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setPK...(value);</span>
     * dataCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setVersionNo(value);</span>
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * dataCrawlingConfigBhv.<span style="color: #FD4747">queryUpdate</span>(dataCrawlingConfig, cb);
     * </pre>
     * @param dataCrawlingConfig The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final DataCrawlingConfig dataCrawlingConfig,
            final DataCrawlingConfigCB cb) {
        return doQueryUpdate(dataCrawlingConfig, cb, null);
    }

    protected int doQueryUpdate(final DataCrawlingConfig dataCrawlingConfig,
            final DataCrawlingConfigCB cb,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("dataCrawlingConfig", dataCrawlingConfig);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                dataCrawlingConfig, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (DataCrawlingConfigCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (DataCrawlingConfigCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * dataCrawlingConfigBhv.<span style="color: #FD4747">queryDelete</span>(dataCrawlingConfig, cb);
     * </pre>
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final DataCrawlingConfigCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final DataCrawlingConfigCB cb,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((DataCrawlingConfigCB) cb);
        } else {
            return varyingQueryDelete((DataCrawlingConfigCB) cb,
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
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * dataCrawlingConfig.setFoo...(value);
     * dataCrawlingConfig.setBar...(value);
     * InsertOption<DataCrawlingConfigCB> option = new InsertOption<DataCrawlingConfigCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * dataCrawlingConfigBhv.<span style="color: #FD4747">varyingInsert</span>(dataCrawlingConfig, option);
     * ... = dataCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param dataCrawlingConfig The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final DataCrawlingConfig dataCrawlingConfig,
            final InsertOption<DataCrawlingConfigCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(dataCrawlingConfig, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * dataCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * dataCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;DataCrawlingConfigCB&gt; option = new UpdateOption&lt;DataCrawlingConfigCB&gt;();
     *     option.self(new SpecifyQuery&lt;DataCrawlingConfigCB&gt;() {
     *         public void specify(DataCrawlingConfigCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     dataCrawlingConfigBhv.<span style="color: #FD4747">varyingUpdate</span>(dataCrawlingConfig, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param dataCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final DataCrawlingConfig dataCrawlingConfig,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(dataCrawlingConfig, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * dataCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * dataCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setVersionNo(value);</span>
     * UpdateOption&lt;DataCrawlingConfigCB&gt; option = new UpdateOption&lt;DataCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;DataCrawlingConfigCB&gt;() {
     *     public void specify(DataCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(dataCrawlingConfig, option);
     * </pre>
     * @param dataCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(
            final DataCrawlingConfig dataCrawlingConfig,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(dataCrawlingConfig, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param dataCrawlingConfig The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final DataCrawlingConfig dataCrawlingConfig,
            final InsertOption<DataCrawlingConfigCB> insertOption,
            final UpdateOption<DataCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(dataCrawlingConfig, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param dataCrawlingConfig The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final DataCrawlingConfig dataCrawlingConfig,
            final InsertOption<DataCrawlingConfigCB> insertOption,
            final UpdateOption<DataCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(dataCrawlingConfig, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param dataCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final DataCrawlingConfig dataCrawlingConfig,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(dataCrawlingConfig, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param dataCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(
            final DataCrawlingConfig dataCrawlingConfig,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(dataCrawlingConfig, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final InsertOption<DataCrawlingConfigCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(dataCrawlingConfigList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(dataCrawlingConfigList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(dataCrawlingConfigList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(dataCrawlingConfigList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param dataCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<DataCrawlingConfig> dataCrawlingConfigList,
            final DeleteOption<DataCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(dataCrawlingConfigList, option);
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
            final QueryInsertSetupper<DataCrawlingConfig, DataCrawlingConfigCB> setupper,
            final InsertOption<DataCrawlingConfigCB> option) {
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
     * DataCrawlingConfig dataCrawlingConfig = new DataCrawlingConfig();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setPK...(value);</span>
     * dataCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//dataCrawlingConfig.setVersionNo(value);</span>
     * DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;DataCrawlingConfigCB&gt; option = new UpdateOption&lt;DataCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;DataCrawlingConfigCB&gt;() {
     *     public void specify(DataCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * dataCrawlingConfigBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(dataCrawlingConfig, cb, option);
     * </pre>
     * @param dataCrawlingConfig The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final DataCrawlingConfig dataCrawlingConfig,
            final DataCrawlingConfigCB cb,
            final UpdateOption<DataCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(dataCrawlingConfig, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of DataCrawlingConfig. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final DataCrawlingConfigCB cb,
            final DeleteOption<DataCrawlingConfigCB> option) {
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
    public OutsideSqlBasicExecutor<DataCrawlingConfigBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final DataCrawlingConfigCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final DataCrawlingConfigCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends DataCrawlingConfig> void delegateSelectCursor(
            final DataCrawlingConfigCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends DataCrawlingConfig> List<ENTITY> delegateSelectList(
            final DataCrawlingConfigCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final DataCrawlingConfig e,
            final InsertOption<DataCrawlingConfigCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final DataCrawlingConfig e,
            final UpdateOption<DataCrawlingConfigCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final DataCrawlingConfig e,
            final UpdateOption<DataCrawlingConfigCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final DataCrawlingConfig e,
            final DeleteOption<DataCrawlingConfigCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final DataCrawlingConfig e,
            final DeleteOption<DataCrawlingConfigCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<DataCrawlingConfig> ls,
            final InsertOption<DataCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<DataCrawlingConfig> ls,
            final UpdateOption<DataCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<DataCrawlingConfig> ls,
            final UpdateOption<DataCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<DataCrawlingConfig> ls,
            final DeleteOption<DataCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<DataCrawlingConfig> ls,
            final DeleteOption<DataCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final DataCrawlingConfig e,
            final DataCrawlingConfigCB inCB, final ConditionBean resCB,
            final InsertOption<DataCrawlingConfigCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final DataCrawlingConfig e,
            final DataCrawlingConfigCB cb,
            final UpdateOption<DataCrawlingConfigCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final DataCrawlingConfigCB cb,
            final DeleteOption<DataCrawlingConfigCB> op) {
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
    protected DataCrawlingConfig downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, DataCrawlingConfig.class);
    }

    protected DataCrawlingConfigCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                DataCrawlingConfigCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<DataCrawlingConfig> downcast(
            final List<? extends Entity> entityList) {
        return (List<DataCrawlingConfig>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<DataCrawlingConfigCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<DataCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<DataCrawlingConfigCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<DataCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<DataCrawlingConfigCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<DataCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<DataCrawlingConfig, DataCrawlingConfigCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<DataCrawlingConfig, DataCrawlingConfigCB>) option;
    }
}
