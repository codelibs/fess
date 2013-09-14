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

import jp.sf.fess.db.bsentity.dbmeta.RoleTypeDbm;
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.DataConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.LabelTypeToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.RoleTypeBhv;
import jp.sf.fess.db.exbhv.WebConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.db.exentity.WebConfigToRoleTypeMapping;

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
 * The behavior of ROLE_TYPE as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, NAME, VALUE, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     DATA_CONFIG_TO_ROLE_TYPE_MAPPING, FILE_CONFIG_TO_ROLE_TYPE_MAPPING, LABEL_TYPE_TO_ROLE_TYPE_MAPPING, WEB_CONFIG_TO_ROLE_TYPE_MAPPING
 * 
 * [foreign property]
 *     
 * 
 * [referrer property]
 *     dataConfigToRoleTypeMappingList, fileConfigToRoleTypeMappingList, labelTypeToRoleTypeMappingList, webConfigToRoleTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsRoleTypeBhv extends AbstractBehaviorWritable {

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
        return "ROLE_TYPE";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return RoleTypeDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public RoleTypeDbm getMyDBMeta() {
        return RoleTypeDbm.getInstance();
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
    public RoleType newMyEntity() {
        return new RoleType();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public RoleTypeCB newMyConditionBean() {
        return new RoleTypeCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * int count = roleTypeBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final RoleTypeCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final RoleTypeCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final RoleTypeCB cb) { // called by selectPage(cb)
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
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * RoleType roleType = roleTypeBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (roleType != null) {
     *     ... = roleType.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public RoleType selectEntity(final RoleTypeCB cb) {
        return doSelectEntity(cb, RoleType.class);
    }

    protected <ENTITY extends RoleType> ENTITY doSelectEntity(
            final RoleTypeCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(cb, entityType,
                new InternalSelectEntityCallback<ENTITY, RoleTypeCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(final RoleTypeCB cb,
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
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * RoleType roleType = roleTypeBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = roleType.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public RoleType selectEntityWithDeletedCheck(final RoleTypeCB cb) {
        return doSelectEntityWithDeletedCheck(cb, RoleType.class);
    }

    protected <ENTITY extends RoleType> ENTITY doSelectEntityWithDeletedCheck(
            final RoleTypeCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, RoleTypeCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(final RoleTypeCB cb,
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
    public RoleType selectByPKValue(final Long id) {
        return doSelectByPKValue(id, RoleType.class);
    }

    protected <ENTITY extends RoleType> ENTITY doSelectByPKValue(final Long id,
            final Class<ENTITY> entityType) {
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
    public RoleType selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, RoleType.class);
    }

    protected <ENTITY extends RoleType> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private RoleTypeCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final RoleTypeCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;RoleType&gt; roleTypeList = roleTypeBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.get...();
     * }
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<RoleType> selectList(final RoleTypeCB cb) {
        return doSelectList(cb, RoleType.class);
    }

    protected <ENTITY extends RoleType> ListResultBean<ENTITY> doSelectList(
            final RoleTypeCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, RoleTypeCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(final RoleTypeCB cb,
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
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;RoleType&gt; page = roleTypeBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (RoleType roleType : page) {
     *     ... = roleType.get...();
     * }
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<RoleType> selectPage(final RoleTypeCB cb) {
        return doSelectPage(cb, RoleType.class);
    }

    protected <ENTITY extends RoleType> PagingResultBean<ENTITY> doSelectPage(
            final RoleTypeCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, RoleTypeCB>() {
                    @Override
                    public int callbackSelectCount(final RoleTypeCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(final RoleTypeCB cb,
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
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * roleTypeBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;RoleType&gt;() {
     *     public void handle(RoleType entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @param entityRowHandler The handler of entity row of RoleType. (NotNull)
     */
    public void selectCursor(final RoleTypeCB cb,
            final EntityRowHandler<RoleType> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, RoleType.class);
    }

    protected <ENTITY extends RoleType> void doSelectCursor(
            final RoleTypeCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<RoleType>", entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(cb, entityRowHandler, entityType,
                new InternalSelectCursorCallback<ENTITY, RoleTypeCB>() {
                    @Override
                    public void callbackSelectCursor(final RoleTypeCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(final RoleTypeCB cb,
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
     * roleTypeBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(RoleTypeCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<RoleTypeCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends RoleTypeCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param roleType The entity of roleType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadDataConfigToRoleTypeMappingList(
            final RoleType roleType,
            final ConditionBeanSetupper<DataConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(roleType, conditionBeanSetupper);
        loadDataConfigToRoleTypeMappingList(xnewLRLs(roleType),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of dataConfigToRoleTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #FD4747">loadDataConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(DataConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.<span style="color: #FD4747">getDataConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadDataConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final ConditionBeanSetupper<DataConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(roleTypeList, conditionBeanSetupper);
        loadDataConfigToRoleTypeMappingList(
                roleTypeList,
                new LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param roleType The entity of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadDataConfigToRoleTypeMappingList(
            final RoleType roleType,
            final LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleType, loadReferrerOption);
        loadDataConfigToRoleTypeMappingList(xnewLRLs(roleType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadDataConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleTypeList, loadReferrerOption);
        if (roleTypeList.isEmpty()) {
            return;
        }
        final DataConfigToRoleTypeMappingBhv referrerBhv = xgetBSFLR().select(
                DataConfigToRoleTypeMappingBhv.class);
        helpLoadReferrerInternally(
                roleTypeList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<RoleType, Long, DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping>() {
                    @Override
                    public Long getPKVal(final RoleType e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final RoleType e,
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
                        cb.query().setRoleTypeId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final DataConfigToRoleTypeMappingCB cb) {
                        cb.query().addOrderBy_RoleTypeId_Asc();
                    }

                    @Override
                    public void spFKCol(final DataConfigToRoleTypeMappingCB cb) {
                        cb.specify().columnRoleTypeId();
                    }

                    @Override
                    public List<DataConfigToRoleTypeMapping> selRfLs(
                            final DataConfigToRoleTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final DataConfigToRoleTypeMapping e) {
                        return e.getRoleTypeId();
                    }

                    @Override
                    public void setlcEt(final DataConfigToRoleTypeMapping re,
                            final RoleType le) {
                        re.setRoleType(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "dataConfigToRoleTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param roleType The entity of roleType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToRoleTypeMappingList(
            final RoleType roleType,
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(roleType, conditionBeanSetupper);
        loadFileConfigToRoleTypeMappingList(xnewLRLs(roleType),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of fileConfigToRoleTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #FD4747">loadFileConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(FileConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.<span style="color: #FD4747">getFileConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(roleTypeList, conditionBeanSetupper);
        loadFileConfigToRoleTypeMappingList(
                roleTypeList,
                new LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param roleType The entity of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToRoleTypeMappingList(
            final RoleType roleType,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleType, loadReferrerOption);
        loadFileConfigToRoleTypeMappingList(xnewLRLs(roleType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleTypeList, loadReferrerOption);
        if (roleTypeList.isEmpty()) {
            return;
        }
        final FileConfigToRoleTypeMappingBhv referrerBhv = xgetBSFLR().select(
                FileConfigToRoleTypeMappingBhv.class);
        helpLoadReferrerInternally(
                roleTypeList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<RoleType, Long, FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping>() {
                    @Override
                    public Long getPKVal(final RoleType e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final RoleType e,
                            final List<FileConfigToRoleTypeMapping> ls) {
                        e.setFileConfigToRoleTypeMappingList(ls);
                    }

                    @Override
                    public FileConfigToRoleTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final FileConfigToRoleTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setRoleTypeId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final FileConfigToRoleTypeMappingCB cb) {
                        cb.query().addOrderBy_RoleTypeId_Asc();
                    }

                    @Override
                    public void spFKCol(final FileConfigToRoleTypeMappingCB cb) {
                        cb.specify().columnRoleTypeId();
                    }

                    @Override
                    public List<FileConfigToRoleTypeMapping> selRfLs(
                            final FileConfigToRoleTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final FileConfigToRoleTypeMapping e) {
                        return e.getRoleTypeId();
                    }

                    @Override
                    public void setlcEt(final FileConfigToRoleTypeMapping re,
                            final RoleType le) {
                        re.setRoleType(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "fileConfigToRoleTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param roleType The entity of roleType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadLabelTypeToRoleTypeMappingList(
            final RoleType roleType,
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(roleType, conditionBeanSetupper);
        loadLabelTypeToRoleTypeMappingList(xnewLRLs(roleType),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of labelTypeToRoleTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #FD4747">loadLabelTypeToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void setup(LabelTypeToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.<span style="color: #FD4747">getLabelTypeToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadLabelTypeToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(roleTypeList, conditionBeanSetupper);
        loadLabelTypeToRoleTypeMappingList(
                roleTypeList,
                new LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param roleType The entity of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadLabelTypeToRoleTypeMappingList(
            final RoleType roleType,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleType, loadReferrerOption);
        loadLabelTypeToRoleTypeMappingList(xnewLRLs(roleType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadLabelTypeToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleTypeList, loadReferrerOption);
        if (roleTypeList.isEmpty()) {
            return;
        }
        final LabelTypeToRoleTypeMappingBhv referrerBhv = xgetBSFLR().select(
                LabelTypeToRoleTypeMappingBhv.class);
        helpLoadReferrerInternally(
                roleTypeList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<RoleType, Long, LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping>() {
                    @Override
                    public Long getPKVal(final RoleType e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final RoleType e,
                            final List<LabelTypeToRoleTypeMapping> ls) {
                        e.setLabelTypeToRoleTypeMappingList(ls);
                    }

                    @Override
                    public LabelTypeToRoleTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final LabelTypeToRoleTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setRoleTypeId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final LabelTypeToRoleTypeMappingCB cb) {
                        cb.query().addOrderBy_RoleTypeId_Asc();
                    }

                    @Override
                    public void spFKCol(final LabelTypeToRoleTypeMappingCB cb) {
                        cb.specify().columnRoleTypeId();
                    }

                    @Override
                    public List<LabelTypeToRoleTypeMapping> selRfLs(
                            final LabelTypeToRoleTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final LabelTypeToRoleTypeMapping e) {
                        return e.getRoleTypeId();
                    }

                    @Override
                    public void setlcEt(final LabelTypeToRoleTypeMapping re,
                            final RoleType le) {
                        re.setRoleType(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "labelTypeToRoleTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param roleType The entity of roleType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToRoleTypeMappingList(
            final RoleType roleType,
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(roleType, conditionBeanSetupper);
        loadWebConfigToRoleTypeMappingList(xnewLRLs(roleType),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of webConfigToRoleTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #FD4747">loadWebConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(WebConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.<span style="color: #FD4747">getWebConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(roleTypeList, conditionBeanSetupper);
        loadWebConfigToRoleTypeMappingList(
                roleTypeList,
                new LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param roleType The entity of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToRoleTypeMappingList(
            final RoleType roleType,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleType, loadReferrerOption);
        loadWebConfigToRoleTypeMappingList(xnewLRLs(roleType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleTypeList, loadReferrerOption);
        if (roleTypeList.isEmpty()) {
            return;
        }
        final WebConfigToRoleTypeMappingBhv referrerBhv = xgetBSFLR().select(
                WebConfigToRoleTypeMappingBhv.class);
        helpLoadReferrerInternally(
                roleTypeList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<RoleType, Long, WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping>() {
                    @Override
                    public Long getPKVal(final RoleType e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final RoleType e,
                            final List<WebConfigToRoleTypeMapping> ls) {
                        e.setWebConfigToRoleTypeMappingList(ls);
                    }

                    @Override
                    public WebConfigToRoleTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final WebConfigToRoleTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setRoleTypeId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final WebConfigToRoleTypeMappingCB cb) {
                        cb.query().addOrderBy_RoleTypeId_Asc();
                    }

                    @Override
                    public void spFKCol(final WebConfigToRoleTypeMappingCB cb) {
                        cb.specify().columnRoleTypeId();
                    }

                    @Override
                    public List<WebConfigToRoleTypeMapping> selRfLs(
                            final WebConfigToRoleTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final WebConfigToRoleTypeMapping e) {
                        return e.getRoleTypeId();
                    }

                    @Override
                    public void setlcEt(final WebConfigToRoleTypeMapping re,
                            final RoleType le) {
                        re.setRoleType(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "webConfigToRoleTypeMappingList";
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
     * @param roleTypeList The list of roleType. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(final List<RoleType> roleTypeList) {
        return helpExtractListInternally(roleTypeList,
                new InternalExtractCallback<RoleType, Long>() {
                    @Override
                    public Long getCV(final RoleType e) {
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
     * RoleType roleType = new RoleType();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * roleType.setFoo...(value);
     * roleType.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//roleType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//roleType.set...;</span>
     * roleTypeBhv.<span style="color: #FD4747">insert</span>(roleType);
     * ... = roleType.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param roleType The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final RoleType roleType) {
        doInsert(roleType, null);
    }

    protected void doInsert(final RoleType roleType,
            final InsertOption<RoleTypeCB> option) {
        assertObjectNotNull("roleType", roleType);
        prepareInsertOption(option);
        delegateInsert(roleType, option);
    }

    protected void prepareInsertOption(final InsertOption<RoleTypeCB> option) {
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
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * roleType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//roleType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//roleType.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * roleType.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     roleTypeBhv.<span style="color: #FD4747">update</span>(roleType);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param roleType The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final RoleType roleType) {
        doUpdate(roleType, null);
    }

    protected void doUpdate(final RoleType roleType,
            final UpdateOption<RoleTypeCB> option) {
        assertObjectNotNull("roleType", roleType);
        prepareUpdateOption(option);
        helpUpdateInternally(roleType, new InternalUpdateCallback<RoleType>() {
            @Override
            public int callbackDelegateUpdate(final RoleType entity) {
                return delegateUpdate(entity, option);
            }
        });
    }

    protected void prepareUpdateOption(final UpdateOption<RoleTypeCB> option) {
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

    protected RoleTypeCB createCBForVaryingUpdate() {
        final RoleTypeCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected RoleTypeCB createCBForSpecifiedUpdate() {
        final RoleTypeCB cb = newMyConditionBean();
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
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * roleType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//roleType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//roleType.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * roleTypeBhv.<span style="color: #FD4747">updateNonstrict</span>(roleType);
     * </pre>
     * @param roleType The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final RoleType roleType) {
        doUpdateNonstrict(roleType, null);
    }

    protected void doUpdateNonstrict(final RoleType roleType,
            final UpdateOption<RoleTypeCB> option) {
        assertObjectNotNull("roleType", roleType);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(roleType,
                new InternalUpdateNonstrictCallback<RoleType>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final RoleType entity) {
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
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, ExclusiveControl)
     * @param roleType The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final RoleType roleType) {
        doInesrtOrUpdate(roleType, null, null);
    }

    protected void doInesrtOrUpdate(final RoleType roleType,
            final InsertOption<RoleTypeCB> insertOption,
            final UpdateOption<RoleTypeCB> updateOption) {
        helpInsertOrUpdateInternally(roleType,
                new InternalInsertOrUpdateCallback<RoleType, RoleTypeCB>() {
                    @Override
                    public void callbackInsert(final RoleType entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final RoleType entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public RoleTypeCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final RoleTypeCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<RoleTypeCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<RoleTypeCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl)
     * @param roleType The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(final RoleType roleType) {
        doInesrtOrUpdateNonstrict(roleType, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(final RoleType roleType,
            final InsertOption<RoleTypeCB> insertOption,
            final UpdateOption<RoleTypeCB> updateOption) {
        helpInsertOrUpdateInternally(roleType,
                new InternalInsertOrUpdateNonstrictCallback<RoleType>() {
                    @Override
                    public void callbackInsert(final RoleType entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(final RoleType entity) {
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
            insertOption = insertOption == null ? new InsertOption<RoleTypeCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<RoleTypeCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * roleType.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     roleTypeBhv.<span style="color: #FD4747">delete</span>(roleType);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param roleType The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final RoleType roleType) {
        doDelete(roleType, null);
    }

    protected void doDelete(final RoleType roleType,
            final DeleteOption<RoleTypeCB> option) {
        assertObjectNotNull("roleType", roleType);
        prepareDeleteOption(option);
        helpDeleteInternally(roleType, new InternalDeleteCallback<RoleType>() {
            @Override
            public int callbackDelegateDelete(final RoleType entity) {
                return delegateDelete(entity, option);
            }
        });
    }

    protected void prepareDeleteOption(final DeleteOption<RoleTypeCB> option) {
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
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * roleTypeBhv.<span style="color: #FD4747">deleteNonstrict</span>(roleType);
     * </pre>
     * @param roleType The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final RoleType roleType) {
        doDeleteNonstrict(roleType, null);
    }

    protected void doDeleteNonstrict(final RoleType roleType,
            final DeleteOption<RoleTypeCB> option) {
        assertObjectNotNull("roleType", roleType);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(roleType,
                new InternalDeleteNonstrictCallback<RoleType>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final RoleType entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * roleTypeBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(roleType);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param roleType The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(final RoleType roleType) {
        doDeleteNonstrictIgnoreDeleted(roleType, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(final RoleType roleType,
            final DeleteOption<RoleTypeCB> option) {
        assertObjectNotNull("roleType", roleType);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(roleType,
                new InternalDeleteNonstrictIgnoreDeletedCallback<RoleType>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final RoleType entity) {
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
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are insert target. (so default constraints are not available in this method)</span> <br />
     * And if the table has an identity, entities after the process don't have incremented values.
     * When you use the (normal) insert(), an entity after the process has an incremented value.
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<RoleType> roleTypeList) {
        return doBatchInsert(roleTypeList, null);
    }

    protected int[] doBatchInsert(final List<RoleType> roleTypeList,
            final InsertOption<RoleTypeCB> option) {
        assertObjectNotNull("roleTypeList", roleTypeList);
        prepareInsertOption(option);
        return delegateBatchInsert(roleTypeList, option);
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
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span> <br />
     * So you should the other batchUpdate() method, which you can specify update columns like this:
     * <pre>
     * roleTypeBhv.<span style="color: #FD4747">batchUpdate</span>(roleTypeList, new SpecifyQuery<RoleTypeCB>() {
     *     public void specify(RoleTypeCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<RoleType> roleTypeList) {
        return doBatchUpdate(roleTypeList, null);
    }

    protected int[] doBatchUpdate(final List<RoleType> roleTypeList,
            final UpdateOption<RoleTypeCB> option) {
        assertObjectNotNull("roleTypeList", roleTypeList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(roleTypeList, option);
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
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistick lock column because they are specified implicitly.
     * <pre>
     * roleTypeBhv.<span style="color: #FD4747">batchUpdate</span>(roleTypeList, new SpecifyQuery<RoleTypeCB>() {
     *     public void specify(RoleTypeCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<RoleType> roleTypeList,
            final SpecifyQuery<RoleTypeCB> updateColumnSpec) {
        return doBatchUpdate(roleTypeList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdate() method, which you can specify update columns like this:
     * <pre>
     * roleTypeBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(roleTypeList, new SpecifyQuery<RoleTypeCB>() {
     *     public void specify(RoleTypeCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<RoleType> roleTypeList) {
        return doBatchUpdateNonstrict(roleTypeList, null);
    }

    protected int[] doBatchUpdateNonstrict(final List<RoleType> roleTypeList,
            final UpdateOption<RoleTypeCB> option) {
        assertObjectNotNull("roleTypeList", roleTypeList);
        prepareUpdateOption(option);
        return delegateBatchUpdateNonstrict(roleTypeList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistick lock column because they are specified implicitly.
     * <pre>
     * roleTypeBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(roleTypeList, new SpecifyQuery<RoleTypeCB>() {
     *     public void specify(RoleTypeCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<RoleType> roleTypeList,
            final SpecifyQuery<RoleTypeCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(roleTypeList,
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
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<RoleType> roleTypeList) {
        return doBatchDelete(roleTypeList, null);
    }

    protected int[] doBatchDelete(final List<RoleType> roleTypeList,
            final DeleteOption<RoleTypeCB> option) {
        assertObjectNotNull("roleTypeList", roleTypeList);
        prepareDeleteOption(option);
        return delegateBatchDelete(roleTypeList, option);
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
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(final List<RoleType> roleTypeList) {
        return doBatchDeleteNonstrict(roleTypeList, null);
    }

    protected int[] doBatchDeleteNonstrict(final List<RoleType> roleTypeList,
            final DeleteOption<RoleTypeCB> option) {
        assertObjectNotNull("roleTypeList", roleTypeList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(roleTypeList, option);
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
     * roleTypeBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;RoleType, RoleTypeCB&gt;() {
     *     public ConditionBean setup(roleType entity, RoleTypeCB intoCB) {
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
            final QueryInsertSetupper<RoleType, RoleTypeCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<RoleType, RoleTypeCB> setupper,
            final InsertOption<RoleTypeCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final RoleType entity = new RoleType();
        final RoleTypeCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected RoleTypeCB createCBForQueryInsert() {
        final RoleTypeCB cb = newMyConditionBean();
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
     * RoleType roleType = new RoleType();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//roleType.setPK...(value);</span>
     * roleType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//roleType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//roleType.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * roleTypeBhv.<span style="color: #FD4747">queryUpdate</span>(roleType, cb);
     * </pre>
     * @param roleType The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final RoleType roleType, final RoleTypeCB cb) {
        return doQueryUpdate(roleType, cb, null);
    }

    protected int doQueryUpdate(final RoleType roleType, final RoleTypeCB cb,
            final UpdateOption<RoleTypeCB> option) {
        assertObjectNotNull("roleType", roleType);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                roleType, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (RoleTypeCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity), (RoleTypeCB) cb,
                    downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * roleTypeBhv.<span style="color: #FD4747">queryDelete</span>(roleType, cb);
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final RoleTypeCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final RoleTypeCB cb,
            final DeleteOption<RoleTypeCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((RoleTypeCB) cb);
        } else {
            return varyingQueryDelete((RoleTypeCB) cb, downcast(option));
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
     * RoleType roleType = new RoleType();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * roleType.setFoo...(value);
     * roleType.setBar...(value);
     * InsertOption<RoleTypeCB> option = new InsertOption<RoleTypeCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * roleTypeBhv.<span style="color: #FD4747">varyingInsert</span>(roleType, option);
     * ... = roleType.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param roleType The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final RoleType roleType,
            final InsertOption<RoleTypeCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(roleType, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * roleType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * roleType.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;RoleTypeCB&gt; option = new UpdateOption&lt;RoleTypeCB&gt;();
     *     option.self(new SpecifyQuery&lt;RoleTypeCB&gt;() {
     *         public void specify(RoleTypeCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     roleTypeBhv.<span style="color: #FD4747">varyingUpdate</span>(roleType, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param roleType The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final RoleType roleType,
            final UpdateOption<RoleTypeCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(roleType, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * roleType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * UpdateOption&lt;RoleTypeCB&gt; option = new UpdateOption&lt;RoleTypeCB&gt;();
     * option.self(new SpecifyQuery&lt;RoleTypeCB&gt;() {
     *     public void specify(RoleTypeCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * roleTypeBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(roleType, option);
     * </pre>
     * @param roleType The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(final RoleType roleType,
            final UpdateOption<RoleTypeCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(roleType, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param roleType The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final RoleType roleType,
            final InsertOption<RoleTypeCB> insertOption,
            final UpdateOption<RoleTypeCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(roleType, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param roleType The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(final RoleType roleType,
            final InsertOption<RoleTypeCB> insertOption,
            final UpdateOption<RoleTypeCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(roleType, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param roleType The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final RoleType roleType,
            final DeleteOption<RoleTypeCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(roleType, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param roleType The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(final RoleType roleType,
            final DeleteOption<RoleTypeCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(roleType, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(final List<RoleType> roleTypeList,
            final InsertOption<RoleTypeCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(roleTypeList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(final List<RoleType> roleTypeList,
            final UpdateOption<RoleTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(roleTypeList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(final List<RoleType> roleTypeList,
            final UpdateOption<RoleTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(roleTypeList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(final List<RoleType> roleTypeList,
            final DeleteOption<RoleTypeCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(roleTypeList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(final List<RoleType> roleTypeList,
            final DeleteOption<RoleTypeCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(roleTypeList, option);
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
            final QueryInsertSetupper<RoleType, RoleTypeCB> setupper,
            final InsertOption<RoleTypeCB> option) {
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
     * RoleType roleType = new RoleType();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//roleType.setPK...(value);</span>
     * roleType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;RoleTypeCB&gt; option = new UpdateOption&lt;RoleTypeCB&gt;();
     * option.self(new SpecifyQuery&lt;RoleTypeCB&gt;() {
     *     public void specify(RoleTypeCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * roleTypeBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(roleType, cb, option);
     * </pre>
     * @param roleType The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of RoleType. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final RoleType roleType, final RoleTypeCB cb,
            final UpdateOption<RoleTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(roleType, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of RoleType. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final RoleTypeCB cb,
            final DeleteOption<RoleTypeCB> option) {
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
    public OutsideSqlBasicExecutor<RoleTypeBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final RoleTypeCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final RoleTypeCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends RoleType> void delegateSelectCursor(
            final RoleTypeCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends RoleType> List<ENTITY> delegateSelectList(
            final RoleTypeCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final RoleType e,
            final InsertOption<RoleTypeCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final RoleType e,
            final UpdateOption<RoleTypeCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final RoleType e,
            final UpdateOption<RoleTypeCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final RoleType e,
            final DeleteOption<RoleTypeCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final RoleType e,
            final DeleteOption<RoleTypeCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<RoleType> ls,
            final InsertOption<RoleTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<RoleType> ls,
            final UpdateOption<RoleTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(final List<RoleType> ls,
            final UpdateOption<RoleTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<RoleType> ls,
            final DeleteOption<RoleTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(final List<RoleType> ls,
            final DeleteOption<RoleTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final RoleType e, final RoleTypeCB inCB,
            final ConditionBean resCB, final InsertOption<RoleTypeCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final RoleType e, final RoleTypeCB cb,
            final UpdateOption<RoleTypeCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final RoleTypeCB cb,
            final DeleteOption<RoleTypeCB> op) {
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
    protected RoleType downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, RoleType.class);
    }

    protected RoleTypeCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, RoleTypeCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<RoleType> downcast(final List<? extends Entity> entityList) {
        return (List<RoleType>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<RoleTypeCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<RoleTypeCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<RoleTypeCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<RoleTypeCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<RoleTypeCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<RoleTypeCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<RoleType, RoleTypeCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<RoleType, RoleTypeCB>) option;
    }
}
