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

import jp.sf.fess.db.bsbhv.loader.LoaderOfLabelType;
import jp.sf.fess.db.bsentity.dbmeta.LabelTypeDbm;
import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.LabelTypeCB;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.exbhv.LabelTypeBhv;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;

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
 * The behavior of LABEL_TYPE as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, NAME, VALUE, INCLUDED_PATHS, EXCLUDED_PATHS, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     DATA_CONFIG_TO_LABEL_TYPE_MAPPING, FILE_CONFIG_TO_LABEL_TYPE_MAPPING, LABEL_TYPE_TO_ROLE_TYPE_MAPPING, WEB_CONFIG_TO_LABEL_TYPE_MAPPING
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     dataConfigToLabelTypeMappingList, fileConfigToLabelTypeMappingList, labelTypeToRoleTypeMappingList, webConfigToLabelTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsLabelTypeBhv extends
        AbstractBehaviorWritable<LabelType, LabelTypeCB> {

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
    public LabelTypeDbm getDBMeta() {
        return LabelTypeDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public LabelTypeDbm getMyDBMeta() {
        return LabelTypeDbm.getInstance();
    }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    @Override
    public LabelTypeCB newConditionBean() {
        return new LabelTypeCB();
    }

    /** @return The instance of new entity as my table type. (NotNull) */
    public LabelType newMyEntity() {
        return new LabelType();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public LabelTypeCB newMyConditionBean() {
        return new LabelTypeCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * int count = labelTypeBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final LabelTypeCB cb) {
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
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * LabelType labelType = labelTypeBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (labelType != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = labelType.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public LabelType selectEntity(final LabelTypeCB cb) {
        return facadeSelectEntity(cb);
    }

    protected LabelType facadeSelectEntity(final LabelTypeCB cb) {
        return doSelectEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> OptionalEntity<ENTITY> doSelectOptionalEntity(
            final LabelTypeCB cb, final Class<? extends ENTITY> tp) {
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
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * LabelType labelType = labelTypeBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = labelType.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public LabelType selectEntityWithDeletedCheck(final LabelTypeCB cb) {
        return facadeSelectEntityWithDeletedCheck(cb);
    }

    /**
     * Select the entity by the primary-key value.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public LabelType selectByPKValue(final Long id) {
        return facadeSelectByPKValue(id);
    }

    protected LabelType facadeSelectByPKValue(final Long id) {
        return doSelectByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> ENTITY doSelectByPK(final Long id,
            final Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected <ENTITY extends LabelType> OptionalEntity<ENTITY> doSelectOptionalByPK(
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
    public LabelType selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKWithDeletedCheck(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> ENTITY doSelectByPKWithDeletedCheck(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), tp);
    }

    protected LabelTypeCB xprepareCBAsPK(final Long id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;LabelType&gt; labelTypeList = labelTypeBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.get...();
     * }
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<LabelType> selectList(final LabelTypeCB cb) {
        return facadeSelectList(cb);
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean. <br />
     * (both count-select and paging-select are executed)
     * <pre>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;LabelType&gt; page = labelTypeBhv.<span style="color: #DD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (LabelType labelType : page) {
     *     ... = labelType.get...();
     * }
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<LabelType> selectPage(final LabelTypeCB cb) {
        return facadeSelectPage(cb);
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * labelTypeBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;LabelType&gt;() {
     *     public void handle(LabelType entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @param entityRowHandler The handler of entity row of LabelType. (NotNull)
     */
    public void selectCursor(final LabelTypeCB cb,
            final EntityRowHandler<LabelType> entityRowHandler) {
        facadeSelectCursor(cb, entityRowHandler);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(LabelTypeCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> HpSLSFunction<LabelTypeCB, RESULT> scalarSelect(
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
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final List<LabelType> labelTypeList,
            final ReferrerLoaderHandler<LoaderOfLabelType> handler) {
        xassLRArg(labelTypeList, handler);
        handler.handle(new LoaderOfLabelType().ready(labelTypeList,
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
     * @param labelType The entity of labelType. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final LabelType labelType,
            final ReferrerLoaderHandler<LoaderOfLabelType> handler) {
        xassLRArg(labelType, handler);
        handler.handle(new LoaderOfLabelType().ready(xnewLRAryLs(labelType),
                _behaviorSelector));
    }

    /**
     * Load referrer of dataConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'dataConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">loadDataConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(DataConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.<span style="color: #DD4747">getDataConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<DataConfigToLabelTypeMapping> loadDataConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final ConditionBeanSetupper<DataConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(labelTypeList, setupper);
        return doLoadDataConfigToLabelTypeMappingList(
                labelTypeList,
                new LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of dataConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'dataConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">loadDataConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(DataConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = labelType.<span style="color: #DD4747">getDataConfigToLabelTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelType The entity of labelType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<DataConfigToLabelTypeMapping> loadDataConfigToLabelTypeMappingList(
            final LabelType labelType,
            final ConditionBeanSetupper<DataConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(labelType, setupper);
        return doLoadDataConfigToLabelTypeMappingList(
                xnewLRLs(labelType),
                new LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param labelType The entity of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<DataConfigToLabelTypeMapping> loadDataConfigToLabelTypeMappingList(
            final LabelType labelType,
            final LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelType, loadReferrerOption);
        return loadDataConfigToLabelTypeMappingList(xnewLRLs(labelType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean set-upper} #beforejava8
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<DataConfigToLabelTypeMapping> loadDataConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelTypeList, loadReferrerOption);
        if (labelTypeList.isEmpty()) {
            return (NestedReferrerListGateway<DataConfigToLabelTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadDataConfigToLabelTypeMappingList(labelTypeList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<DataConfigToLabelTypeMapping> doLoadDataConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping> option) {
        return helpLoadReferrerInternally(labelTypeList, option,
                "dataConfigToLabelTypeMappingList");
    }

    /**
     * Load referrer of fileConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'fileConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">loadFileConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(FileConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.<span style="color: #DD4747">getFileConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(labelTypeList, setupper);
        return doLoadFileConfigToLabelTypeMappingList(
                labelTypeList,
                new LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of fileConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'fileConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">loadFileConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(FileConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = labelType.<span style="color: #DD4747">getFileConfigToLabelTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelType The entity of labelType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final LabelType labelType,
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(labelType, setupper);
        return doLoadFileConfigToLabelTypeMappingList(
                xnewLRLs(labelType),
                new LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param labelType The entity of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final LabelType labelType,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelType, loadReferrerOption);
        return loadFileConfigToLabelTypeMappingList(xnewLRLs(labelType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean set-upper} #beforejava8
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<FileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelTypeList, loadReferrerOption);
        if (labelTypeList.isEmpty()) {
            return (NestedReferrerListGateway<FileConfigToLabelTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadFileConfigToLabelTypeMappingList(labelTypeList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<FileConfigToLabelTypeMapping> doLoadFileConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> option) {
        return helpLoadReferrerInternally(labelTypeList, option,
                "fileConfigToLabelTypeMappingList");
    }

    /**
     * Load referrer of labelTypeToRoleTypeMappingList by the set-upper of referrer. <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by LABEL_TYPE_ID, named 'labelTypeToRoleTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">loadLabelTypeToRoleTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void setup(LabelTypeToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.<span style="color: #DD4747">getLabelTypeToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<LabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final List<LabelType> labelTypeList,
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> setupper) {
        xassLRArg(labelTypeList, setupper);
        return doLoadLabelTypeToRoleTypeMappingList(
                labelTypeList,
                new LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of labelTypeToRoleTypeMappingList by the set-upper of referrer. <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by LABEL_TYPE_ID, named 'labelTypeToRoleTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">loadLabelTypeToRoleTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void setup(LabelTypeToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = labelType.<span style="color: #DD4747">getLabelTypeToRoleTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelType The entity of labelType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<LabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final LabelType labelType,
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> setupper) {
        xassLRArg(labelType, setupper);
        return doLoadLabelTypeToRoleTypeMappingList(
                xnewLRLs(labelType),
                new LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param labelType The entity of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<LabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final LabelType labelType,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(labelType, loadReferrerOption);
        return loadLabelTypeToRoleTypeMappingList(xnewLRLs(labelType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean set-upper} #beforejava8
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<LabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(labelTypeList, loadReferrerOption);
        if (labelTypeList.isEmpty()) {
            return (NestedReferrerListGateway<LabelTypeToRoleTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadLabelTypeToRoleTypeMappingList(labelTypeList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<LabelTypeToRoleTypeMapping> doLoadLabelTypeToRoleTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> option) {
        return helpLoadReferrerInternally(labelTypeList, option,
                "labelTypeToRoleTypeMappingList");
    }

    /**
     * Load referrer of webConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'webConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">loadWebConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(WebConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.<span style="color: #DD4747">getWebConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(labelTypeList, setupper);
        return doLoadWebConfigToLabelTypeMappingList(
                labelTypeList,
                new LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of webConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'webConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">loadWebConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(WebConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = labelType.<span style="color: #DD4747">getWebConfigToLabelTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelType The entity of labelType. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final LabelType labelType,
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(labelType, setupper);
        return doLoadWebConfigToLabelTypeMappingList(
                xnewLRLs(labelType),
                new LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param labelType The entity of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final LabelType labelType,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelType, loadReferrerOption);
        return loadWebConfigToLabelTypeMappingList(xnewLRLs(labelType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean set-upper} #beforejava8
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<WebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelTypeList, loadReferrerOption);
        if (labelTypeList.isEmpty()) {
            return (NestedReferrerListGateway<WebConfigToLabelTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadWebConfigToLabelTypeMappingList(labelTypeList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<WebConfigToLabelTypeMapping> doLoadWebConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> option) {
        return helpLoadReferrerInternally(labelTypeList, option,
                "webConfigToLabelTypeMappingList");
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================
    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param labelTypeList The list of labelType. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(final List<LabelType> labelTypeList) {
        return helpExtractListInternally(labelTypeList, "id");
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * LabelType labelType = new LabelType();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * labelType.setFoo...(value);
     * labelType.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelType.set...;</span>
     * labelTypeBhv.<span style="color: #DD4747">insert</span>(labelType);
     * ... = labelType.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param labelType The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final LabelType labelType) {
        doInsert(labelType, null);
    }

    /**
     * Update the entity modified-only. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelType.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * labelType.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     labelTypeBhv.<span style="color: #DD4747">update</span>(labelType);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param labelType The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final LabelType labelType) {
        doUpdate(labelType, null);
    }

    /**
     * Update the entity non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelType.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * labelTypeBhv.<span style="color: #DD4747">updateNonstrict</span>(labelType);
     * </pre>
     * @param labelType The entity of update. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final LabelType labelType) {
        doUpdateNonstrict(labelType, null);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, ExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param labelType The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final LabelType labelType) {
        doInsertOrUpdate(labelType, null, null);
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param labelType The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(final LabelType labelType) {
        doInsertOrUpdateNonstrict(labelType, null, null);
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * labelType.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     labelTypeBhv.<span style="color: #DD4747">delete</span>(labelType);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param labelType The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final LabelType labelType) {
        doDelete(labelType, null);
    }

    /**
     * Delete the entity non-strictly. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * labelTypeBhv.<span style="color: #DD4747">deleteNonstrict</span>(labelType);
     * </pre>
     * @param labelType The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final LabelType labelType) {
        doDeleteNonstrict(labelType, null);
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * labelTypeBhv.<span style="color: #DD4747">deleteNonstrictIgnoreDeleted</span>(labelType);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param labelType The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(final LabelType labelType) {
        doDeleteNonstrictIgnoreDeleted(labelType, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(final LabelType et,
            final DeleteOption<LabelTypeCB> op) {
        assertObjectNotNull("labelType", et);
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
     *     LabelType labelType = new LabelType();
     *     labelType.setFooName("foo");
     *     if (...) {
     *         labelType.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     labelTypeList.add(labelType);
     * }
     * labelTypeBhv.<span style="color: #DD4747">batchInsert</span>(labelTypeList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<LabelType> labelTypeList) {
        return doBatchInsert(labelTypeList, null);
    }

    /**
     * Batch-update the entity list modified-only of same-set columns. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     LabelType labelType = new LabelType();
     *     labelType.setFooName("foo");
     *     if (...) {
     *         labelType.setFooPrice(123);
     *     } else {
     *         labelType.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//labelType.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     labelTypeList.add(labelType);
     * }
     * labelTypeBhv.<span style="color: #DD4747">batchUpdate</span>(labelTypeList);
     * </pre>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<LabelType> labelTypeList) {
        return doBatchUpdate(labelTypeList, null);
    }

    /**
     * Batch-update the entity list specified-only. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * labelTypeBhv.<span style="color: #DD4747">batchUpdate</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * labelTypeBhv.<span style="color: #DD4747">batchUpdate</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<LabelType> labelTypeList,
            final SpecifyQuery<LabelTypeCB> updateColumnSpec) {
        return doBatchUpdate(labelTypeList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 140%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     LabelType labelType = new LabelType();
     *     labelType.setFooName("foo");
     *     if (...) {
     *         labelType.setFooPrice(123);
     *     } else {
     *         labelType.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//labelType.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     labelTypeList.add(labelType);
     * }
     * labelTypeBhv.<span style="color: #DD4747">batchUpdate</span>(labelTypeList);
     * </pre>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<LabelType> labelTypeList) {
        return doBatchUpdateNonstrict(labelTypeList, null);
    }

    /**
     * Batch-update the entity list non-strictly specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * labelTypeBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * labelTypeBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<LabelType> labelTypeList,
            final SpecifyQuery<LabelTypeCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(labelTypeList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-delete the entity list. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<LabelType> labelTypeList) {
        return doBatchDelete(labelTypeList, null);
    }

    /**
     * Batch-delete the entity list non-strictly. {NonExclusiveControl} <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(final List<LabelType> labelTypeList) {
        return doBatchDeleteNonstrict(labelTypeList, null);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * labelTypeBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;LabelType, LabelTypeCB&gt;() {
     *     public ConditionBean setup(LabelType entity, LabelTypeCB intoCB) {
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
            final QueryInsertSetupper<LabelType, LabelTypeCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    /**
     * Update the several entities by query non-strictly modified-only. (NonExclusiveControl)
     * <pre>
     * LabelType labelType = new LabelType();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//labelType.setPK...(value);</span>
     * labelType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelType.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * labelTypeBhv.<span style="color: #DD4747">queryUpdate</span>(labelType, cb);
     * </pre>
     * @param labelType The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final LabelType labelType, final LabelTypeCB cb) {
        return doQueryUpdate(labelType, cb, null);
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * labelTypeBhv.<span style="color: #DD4747">queryDelete</span>(labelType, cb);
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final LabelTypeCB cb) {
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
     * LabelType labelType = new LabelType();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * labelType.setFoo...(value);
     * labelType.setBar...(value);
     * InsertOption<LabelTypeCB> option = new InsertOption<LabelTypeCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * labelTypeBhv.<span style="color: #DD4747">varyingInsert</span>(labelType, option);
     * ... = labelType.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param labelType The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final LabelType labelType,
            final InsertOption<LabelTypeCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(labelType, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * labelType.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;LabelTypeCB&gt; option = new UpdateOption&lt;LabelTypeCB&gt;();
     *     option.self(new SpecifyQuery&lt;LabelTypeCB&gt;() {
     *         public void specify(LabelTypeCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     labelTypeBhv.<span style="color: #DD4747">varyingUpdate</span>(labelType, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param labelType The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final LabelType labelType,
            final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(labelType, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * UpdateOption&lt;LabelTypeCB&gt; option = new UpdateOption&lt;LabelTypeCB&gt;();
     * option.self(new SpecifyQuery&lt;LabelTypeCB&gt;() {
     *     public void specify(LabelTypeCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * labelTypeBhv.<span style="color: #DD4747">varyingUpdateNonstrict</span>(labelType, option);
     * </pre>
     * @param labelType The entity of update. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(final LabelType labelType,
            final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(labelType, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param labelType The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final LabelType labelType,
            final InsertOption<LabelTypeCB> insertOption,
            final UpdateOption<LabelTypeCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdate(labelType, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param labelType The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(final LabelType labelType,
            final InsertOption<LabelTypeCB> insertOption,
            final UpdateOption<LabelTypeCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdateNonstrict(labelType, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param labelType The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final LabelType labelType,
            final DeleteOption<LabelTypeCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(labelType, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param labelType The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(final LabelType labelType,
            final DeleteOption<LabelTypeCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(labelType, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(final List<LabelType> labelTypeList,
            final InsertOption<LabelTypeCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(labelTypeList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(final List<LabelType> labelTypeList,
            final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(labelTypeList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<LabelType> labelTypeList,
            final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(labelTypeList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(final List<LabelType> labelTypeList,
            final DeleteOption<LabelTypeCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(labelTypeList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<LabelType> labelTypeList,
            final DeleteOption<LabelTypeCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(labelTypeList, option);
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
            final QueryInsertSetupper<LabelType, LabelTypeCB> setupper,
            final InsertOption<LabelTypeCB> option) {
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
     * LabelType labelType = new LabelType();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//labelType.setPK...(value);</span>
     * labelType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;LabelTypeCB&gt; option = new UpdateOption&lt;LabelTypeCB&gt;();
     * option.self(new SpecifyQuery&lt;LabelTypeCB&gt;() {
     *     public void specify(LabelTypeCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * labelTypeBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(labelType, cb, option);
     * </pre>
     * @param labelType The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of LabelType. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final LabelType labelType,
            final LabelTypeCB cb, final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(labelType, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of LabelType. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final LabelTypeCB cb,
            final DeleteOption<LabelTypeCB> option) {
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
    public OutsideSqlBasicExecutor<LabelTypeBhv> outsideSql() {
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
    protected Class<? extends LabelType> typeOfSelectedEntity() {
        return LabelType.class;
    }

    @Override
    protected Class<LabelType> typeOfHandlingEntity() {
        return LabelType.class;
    }

    @Override
    protected Class<LabelTypeCB> typeOfHandlingConditionBean() {
        return LabelTypeCB.class;
    }
}
