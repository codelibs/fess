/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import jp.sf.fess.db.bsbhv.loader.LoaderOfRoleType;
import jp.sf.fess.db.bsentity.dbmeta.RoleTypeDbm;
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.RoleTypeBhv;
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
import org.seasar.dbflute.bhv.NestedReferrerListGateway;
import org.seasar.dbflute.bhv.QueryInsertSetupper;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;
import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.cbean.chelper.HpSLSFunction;
import org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException;
import org.seasar.dbflute.exception.DangerousResultSizeException;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.dbflute.exception.EntityDuplicatedException;
import org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException;
import org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException;
import org.seasar.dbflute.exception.SelectEntityConditionNotFoundException;
import org.seasar.dbflute.optional.OptionalEntity;
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
public abstract class BsRoleTypeBhv extends
        AbstractBehaviorWritable<RoleType, RoleTypeCB> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:beginQueryPath*/
    /*df:endQueryPath*/

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** {@inheritDoc} */
    @Override
    public RoleTypeDbm getDBMeta() {
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
    public RoleTypeCB newConditionBean() {
        return new RoleTypeCB();
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
     * int count = roleTypeBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final RoleTypeCB cb) {
        return facadeSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean. #beforejava8 <br />
     * <span style="color: #AD4747; font-size: 120%">The return might be null if no data, so you should have null check.</span> <br />
     * <span style="color: #AD4747; font-size: 120%">If the data always exists as your business rule, use selectEntityWithDeletedCheck().</span>
     * <pre>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * RoleType roleType = roleTypeBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (roleType != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = roleType.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public RoleType selectEntity(final RoleTypeCB cb) {
        return facadeSelectEntity(cb);
    }

    protected RoleType facadeSelectEntity(final RoleTypeCB cb) {
        return doSelectEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends RoleType> OptionalEntity<ENTITY> doSelectOptionalEntity(
            final RoleTypeCB cb, final Class<? extends ENTITY> tp) {
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
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * RoleType roleType = roleTypeBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = roleType.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public RoleType selectEntityWithDeletedCheck(final RoleTypeCB cb) {
        return facadeSelectEntityWithDeletedCheck(cb);
    }

    /**
     * Select the entity by the primary-key value.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public RoleType selectByPKValue(final Long id) {
        return facadeSelectByPKValue(id);
    }

    protected RoleType facadeSelectByPKValue(final Long id) {
        return doSelectByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends RoleType> ENTITY doSelectByPK(final Long id,
            final Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected <ENTITY extends RoleType> OptionalEntity<ENTITY> doSelectOptionalByPK(
            final Long id, final Class<? extends ENTITY> tp) {
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
    public RoleType selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKWithDeletedCheck(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends RoleType> ENTITY doSelectByPKWithDeletedCheck(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), tp);
    }

    protected RoleTypeCB xprepareCBAsPK(final Long id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
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
     * ListResultBean&lt;RoleType&gt; roleTypeList = roleTypeBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.get...();
     * }
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<RoleType> selectList(final RoleTypeCB cb) {
        return facadeSelectList(cb);
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
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;RoleType&gt; page = roleTypeBhv.<span style="color: #DD4747">selectPage</span>(cb);
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
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<RoleType> selectPage(final RoleTypeCB cb) {
        return facadeSelectPage(cb);
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * roleTypeBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;RoleType&gt;() {
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
        facadeSelectCursor(cb, entityRowHandler);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(RoleTypeCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> HpSLSFunction<RoleTypeCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return facadeScalarSelect(resultType);
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
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final List<RoleType> roleTypeList,
            final ReferrerLoaderHandler<LoaderOfRoleType> handler) {
        xassLRArg(roleTypeList, handler);
        handler.handle(new LoaderOfRoleType().ready(roleTypeList,
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
     * @param roleType The entity of roleType. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final RoleType roleType,
            final ReferrerLoaderHandler<LoaderOfRoleType> handler) {
        xassLRArg(roleType, handler);
        handler.handle(new LoaderOfRoleType().ready(xnewLRAryLs(roleType),
                _behaviorSelector));
    }

    /**
     * Load referrer of dataConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">loadDataConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(DataConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.<span style="color: #DD4747">getDataConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<DataConfigToRoleTypeMapping> loadDataConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final ConditionBeanSetupper<DataConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(roleTypeList, setupper);
        return doLoadDataConfigToRoleTypeMappingList(
                roleTypeList,
                new LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of dataConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">loadDataConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(DataConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = roleType.<span style="color: #DD4747">getDataConfigToRoleTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleType The entity of roleType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<DataConfigToRoleTypeMapping> loadDataConfigToRoleTypeMappingList(
            final RoleType roleType,
            final ConditionBeanSetupper<DataConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(roleType, setupper);
        return doLoadDataConfigToRoleTypeMappingList(
                xnewLRLs(roleType),
                new LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param roleType The entity of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<DataConfigToRoleTypeMapping> loadDataConfigToRoleTypeMappingList(
            final RoleType roleType,
            final LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleType, loadReferrerOption);
        return loadDataConfigToRoleTypeMappingList(xnewLRLs(roleType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean set-upper} #beforejava8
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<DataConfigToRoleTypeMapping> loadDataConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleTypeList, loadReferrerOption);
        if (roleTypeList.isEmpty()) {
            return (NestedReferrerListGateway<DataConfigToRoleTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadDataConfigToRoleTypeMappingList(roleTypeList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<DataConfigToRoleTypeMapping> doLoadDataConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<DataConfigToRoleTypeMappingCB, DataConfigToRoleTypeMapping> option) {
        return helpLoadReferrerInternally(roleTypeList, option,
                "dataConfigToRoleTypeMappingList");
    }

    /**
     * Load referrer of fileConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">loadFileConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(FileConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.<span style="color: #DD4747">getFileConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(roleTypeList, setupper);
        return doLoadFileConfigToRoleTypeMappingList(
                roleTypeList,
                new LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of fileConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">loadFileConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(FileConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = roleType.<span style="color: #DD4747">getFileConfigToRoleTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleType The entity of roleType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final RoleType roleType,
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(roleType, setupper);
        return doLoadFileConfigToRoleTypeMappingList(
                xnewLRLs(roleType),
                new LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param roleType The entity of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final RoleType roleType,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleType, loadReferrerOption);
        return loadFileConfigToRoleTypeMappingList(xnewLRLs(roleType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean set-upper} #beforejava8
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<FileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleTypeList, loadReferrerOption);
        if (roleTypeList.isEmpty()) {
            return (NestedReferrerListGateway<FileConfigToRoleTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadFileConfigToRoleTypeMappingList(roleTypeList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<FileConfigToRoleTypeMapping> doLoadFileConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> option) {
        return helpLoadReferrerInternally(roleTypeList, option,
                "fileConfigToRoleTypeMappingList");
    }

    /**
     * Load referrer of labelTypeToRoleTypeMappingList by the set-upper of referrer. <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">loadLabelTypeToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void setup(LabelTypeToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.<span style="color: #DD4747">getLabelTypeToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<LabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> setupper) {
        xassLRArg(roleTypeList, setupper);
        return doLoadLabelTypeToRoleTypeMappingList(
                roleTypeList,
                new LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of labelTypeToRoleTypeMappingList by the set-upper of referrer. <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">loadLabelTypeToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void setup(LabelTypeToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = roleType.<span style="color: #DD4747">getLabelTypeToRoleTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleType The entity of roleType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<LabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final RoleType roleType,
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> setupper) {
        xassLRArg(roleType, setupper);
        return doLoadLabelTypeToRoleTypeMappingList(
                xnewLRLs(roleType),
                new LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param roleType The entity of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<LabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final RoleType roleType,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleType, loadReferrerOption);
        return loadLabelTypeToRoleTypeMappingList(xnewLRLs(roleType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean set-upper} #beforejava8
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<LabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleTypeList, loadReferrerOption);
        if (roleTypeList.isEmpty()) {
            return (NestedReferrerListGateway<LabelTypeToRoleTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadLabelTypeToRoleTypeMappingList(roleTypeList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<LabelTypeToRoleTypeMapping> doLoadLabelTypeToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> option) {
        return helpLoadReferrerInternally(roleTypeList, option,
                "labelTypeToRoleTypeMappingList");
    }

    /**
     * Load referrer of webConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">loadWebConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(WebConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (RoleType roleType : roleTypeList) {
     *     ... = roleType.<span style="color: #DD4747">getWebConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(roleTypeList, setupper);
        return doLoadWebConfigToRoleTypeMappingList(
                roleTypeList,
                new LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of webConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingList'.
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">loadWebConfigToRoleTypeMappingList</span>(roleTypeList, new ConditionBeanSetupper&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(WebConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = roleType.<span style="color: #DD4747">getWebConfigToRoleTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setRoleTypeId_InScope(pkList);
     * cb.query().addOrderBy_RoleTypeId_Asc();
     * </pre>
     * @param roleType The entity of roleType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final RoleType roleType,
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(roleType, setupper);
        return doLoadWebConfigToRoleTypeMappingList(
                xnewLRLs(roleType),
                new LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param roleType The entity of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final RoleType roleType,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleType, loadReferrerOption);
        return loadWebConfigToRoleTypeMappingList(xnewLRLs(roleType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean set-upper} #beforejava8
     * @param roleTypeList The entity list of roleType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<WebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(roleTypeList, loadReferrerOption);
        if (roleTypeList.isEmpty()) {
            return (NestedReferrerListGateway<WebConfigToRoleTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadWebConfigToRoleTypeMappingList(roleTypeList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<WebConfigToRoleTypeMapping> doLoadWebConfigToRoleTypeMappingList(
            final List<RoleType> roleTypeList,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> option) {
        return helpLoadReferrerInternally(roleTypeList, option,
                "webConfigToRoleTypeMappingList");
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
        return helpExtractListInternally(roleTypeList, "id");
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * RoleType roleType = new RoleType();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * roleType.setFoo...(value);
     * roleType.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//roleType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//roleType.set...;</span>
     * roleTypeBhv.<span style="color: #DD4747">insert</span>(roleType);
     * ... = roleType.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param roleType The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final RoleType roleType) {
        doInsert(roleType, null);
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
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * roleType.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     roleTypeBhv.<span style="color: #DD4747">update</span>(roleType);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param roleType The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final RoleType roleType) {
        doUpdate(roleType, null);
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
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * roleTypeBhv.<span style="color: #DD4747">updateNonstrict</span>(roleType);
     * </pre>
     * @param roleType The entity of update. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final RoleType roleType) {
        doUpdateNonstrict(roleType, null);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, ExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param roleType The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final RoleType roleType) {
        doInsertOrUpdate(roleType, null, null);
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param roleType The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(final RoleType roleType) {
        doInsertOrUpdateNonstrict(roleType, null, null);
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * roleType.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     roleTypeBhv.<span style="color: #DD4747">delete</span>(roleType);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param roleType The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final RoleType roleType) {
        doDelete(roleType, null);
    }

    /**
     * Delete the entity non-strictly. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * roleTypeBhv.<span style="color: #DD4747">deleteNonstrict</span>(roleType);
     * </pre>
     * @param roleType The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final RoleType roleType) {
        doDeleteNonstrict(roleType, null);
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * RoleType roleType = new RoleType();
     * roleType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * roleTypeBhv.<span style="color: #DD4747">deleteNonstrictIgnoreDeleted</span>(roleType);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param roleType The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(final RoleType roleType) {
        doDeleteNonstrictIgnoreDeleted(roleType, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(final RoleType et,
            final DeleteOption<RoleTypeCB> op) {
        assertObjectNotNull("roleType", et);
        prepareDeleteOption(op);
        helpDeleteNonstrictIgnoreDeletedInternally(et, op);
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
     *     RoleType roleType = new RoleType();
     *     roleType.setFooName("foo");
     *     if (...) {
     *         roleType.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     roleTypeList.add(roleType);
     * }
     * roleTypeBhv.<span style="color: #DD4747">batchInsert</span>(roleTypeList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<RoleType> roleTypeList) {
        return doBatchInsert(roleTypeList, null);
    }

    /**
     * Batch-update the entity list modified-only of same-set columns. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     RoleType roleType = new RoleType();
     *     roleType.setFooName("foo");
     *     if (...) {
     *         roleType.setFooPrice(123);
     *     } else {
     *         roleType.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//roleType.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     roleTypeList.add(roleType);
     * }
     * roleTypeBhv.<span style="color: #DD4747">batchUpdate</span>(roleTypeList);
     * </pre>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<RoleType> roleTypeList) {
        return doBatchUpdate(roleTypeList, null);
    }

    /**
     * Batch-update the entity list specified-only. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * roleTypeBhv.<span style="color: #DD4747">batchUpdate</span>(roleTypeList, new SpecifyQuery<RoleTypeCB>() {
     *     public void specify(RoleTypeCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * roleTypeBhv.<span style="color: #DD4747">batchUpdate</span>(roleTypeList, new SpecifyQuery<RoleTypeCB>() {
     *     public void specify(RoleTypeCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<RoleType> roleTypeList,
            final SpecifyQuery<RoleTypeCB> updateColumnSpec) {
        return doBatchUpdate(roleTypeList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 140%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     RoleType roleType = new RoleType();
     *     roleType.setFooName("foo");
     *     if (...) {
     *         roleType.setFooPrice(123);
     *     } else {
     *         roleType.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//roleType.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     roleTypeList.add(roleType);
     * }
     * roleTypeBhv.<span style="color: #DD4747">batchUpdate</span>(roleTypeList);
     * </pre>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<RoleType> roleTypeList) {
        return doBatchUpdateNonstrict(roleTypeList, null);
    }

    /**
     * Batch-update the entity list non-strictly specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * roleTypeBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(roleTypeList, new SpecifyQuery<RoleTypeCB>() {
     *     public void specify(RoleTypeCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * roleTypeBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(roleTypeList, new SpecifyQuery<RoleTypeCB>() {
     *     public void specify(RoleTypeCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<RoleType> roleTypeList,
            final SpecifyQuery<RoleTypeCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(roleTypeList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-delete the entity list. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<RoleType> roleTypeList) {
        return doBatchDelete(roleTypeList, null);
    }

    /**
     * Batch-delete the entity list non-strictly. {NonExclusiveControl} <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param roleTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(final List<RoleType> roleTypeList) {
        return doBatchDeleteNonstrict(roleTypeList, null);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * roleTypeBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;RoleType, RoleTypeCB&gt;() {
     *     public ConditionBean setup(RoleType entity, RoleTypeCB intoCB) {
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
     * @param setupper The set-upper of query-insert. (NotNull)
     * @return The inserted count.
     */
    public int queryInsert(
            final QueryInsertSetupper<RoleType, RoleTypeCB> setupper) {
        return doQueryInsert(setupper, null);
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
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * roleTypeBhv.<span style="color: #DD4747">queryUpdate</span>(roleType, cb);
     * </pre>
     * @param roleType The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final RoleType roleType, final RoleTypeCB cb) {
        return doQueryUpdate(roleType, cb, null);
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * roleTypeBhv.<span style="color: #DD4747">queryDelete</span>(roleType, cb);
     * </pre>
     * @param cb The condition-bean of RoleType. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final RoleTypeCB cb) {
        return doQueryDelete(cb, null);
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
     * roleTypeBhv.<span style="color: #DD4747">varyingInsert</span>(roleType, option);
     * ... = roleType.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param roleType The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * roleType.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;RoleTypeCB&gt; option = new UpdateOption&lt;RoleTypeCB&gt;();
     *     option.self(new SpecifyQuery&lt;RoleTypeCB&gt;() {
     *         public void specify(RoleTypeCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     roleTypeBhv.<span style="color: #DD4747">varyingUpdate</span>(roleType, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param roleType The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * UpdateOption&lt;RoleTypeCB&gt; option = new UpdateOption&lt;RoleTypeCB&gt;();
     * option.self(new SpecifyQuery&lt;RoleTypeCB&gt;() {
     *     public void specify(RoleTypeCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * roleTypeBhv.<span style="color: #DD4747">varyingUpdateNonstrict</span>(roleType, option);
     * </pre>
     * @param roleType The entity of update. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(final RoleType roleType,
            final UpdateOption<RoleTypeCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(roleType, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param roleType The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final RoleType roleType,
            final InsertOption<RoleTypeCB> insertOption,
            final UpdateOption<RoleTypeCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdate(roleType, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param roleType The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(final RoleType roleType,
            final InsertOption<RoleTypeCB> insertOption,
            final UpdateOption<RoleTypeCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdateNonstrict(roleType, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param roleType The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
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
     * @param roleType The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
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
     * @param setupper The set-upper of query-insert. (NotNull)
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
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//roleType.setVersionNo(value);</span>
     * RoleTypeCB cb = new RoleTypeCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;RoleTypeCB&gt; option = new UpdateOption&lt;RoleTypeCB&gt;();
     * option.self(new SpecifyQuery&lt;RoleTypeCB&gt;() {
     *     public void specify(RoleTypeCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * roleTypeBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(roleType, cb, option);
     * </pre>
     * @param roleType The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of RoleType. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
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
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
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
    //                                                                Optimistic Lock Info
    //                                                                ====================
    @Override
    protected boolean hasVersionNoValue(final Entity et) {
        return downcast(et).getVersionNo() != null;
    }

    // ===================================================================================
    //                                                                         Type Helper
    //                                                                         ===========
    @Override
    protected Class<? extends RoleType> typeOfSelectedEntity() {
        return RoleType.class;
    }

    @Override
    protected Class<RoleType> typeOfHandlingEntity() {
        return RoleType.class;
    }

    @Override
    protected Class<RoleTypeCB> typeOfHandlingConditionBean() {
        return RoleTypeCB.class;
    }
}
