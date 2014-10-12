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

import jp.sf.fess.db.bsbhv.loader.LoaderOfOverlappingHost;
import jp.sf.fess.db.bsentity.dbmeta.OverlappingHostDbm;
import jp.sf.fess.db.cbean.OverlappingHostCB;
import jp.sf.fess.db.exbhv.OverlappingHostBhv;
import jp.sf.fess.db.exentity.OverlappingHost;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.AbstractBehaviorWritable;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.InsertOption;
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
 * The behavior of OVERLAPPING_HOST as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, REGULAR_NAME, OVERLAPPING_NAME, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
public abstract class BsOverlappingHostBhv extends
        AbstractBehaviorWritable<OverlappingHost, OverlappingHostCB> {

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
    public OverlappingHostDbm getDBMeta() {
        return OverlappingHostDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public OverlappingHostDbm getMyDBMeta() {
        return OverlappingHostDbm.getInstance();
    }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    @Override
    public OverlappingHostCB newConditionBean() {
        return new OverlappingHostCB();
    }

    /** @return The instance of new entity as my table type. (NotNull) */
    public OverlappingHost newMyEntity() {
        return new OverlappingHost();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public OverlappingHostCB newMyConditionBean() {
        return new OverlappingHostCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * int count = overlappingHostBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final OverlappingHostCB cb) {
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
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * OverlappingHost overlappingHost = overlappingHostBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (overlappingHost != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = overlappingHost.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public OverlappingHost selectEntity(final OverlappingHostCB cb) {
        return facadeSelectEntity(cb);
    }

    protected OverlappingHost facadeSelectEntity(final OverlappingHostCB cb) {
        return doSelectEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends OverlappingHost> OptionalEntity<ENTITY> doSelectOptionalEntity(
            final OverlappingHostCB cb, final Class<? extends ENTITY> tp) {
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
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * OverlappingHost overlappingHost = overlappingHostBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = overlappingHost.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public OverlappingHost selectEntityWithDeletedCheck(
            final OverlappingHostCB cb) {
        return facadeSelectEntityWithDeletedCheck(cb);
    }

    /**
     * Select the entity by the primary-key value.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public OverlappingHost selectByPKValue(final Long id) {
        return facadeSelectByPKValue(id);
    }

    protected OverlappingHost facadeSelectByPKValue(final Long id) {
        return doSelectByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends OverlappingHost> ENTITY doSelectByPK(
            final Long id, final Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected <ENTITY extends OverlappingHost> OptionalEntity<ENTITY> doSelectOptionalByPK(
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
    public OverlappingHost selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKWithDeletedCheck(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends OverlappingHost> ENTITY doSelectByPKWithDeletedCheck(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), tp);
    }

    protected OverlappingHostCB xprepareCBAsPK(final Long id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;OverlappingHost&gt; overlappingHostList = overlappingHostBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (OverlappingHost overlappingHost : overlappingHostList) {
     *     ... = overlappingHost.get...();
     * }
     * </pre>
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<OverlappingHost> selectList(final OverlappingHostCB cb) {
        return facadeSelectList(cb);
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean. <br />
     * (both count-select and paging-select are executed)
     * <pre>
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;OverlappingHost&gt; page = overlappingHostBhv.<span style="color: #DD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (OverlappingHost overlappingHost : page) {
     *     ... = overlappingHost.get...();
     * }
     * </pre>
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<OverlappingHost> selectPage(
            final OverlappingHostCB cb) {
        return facadeSelectPage(cb);
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * overlappingHostBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;OverlappingHost&gt;() {
     *     public void handle(OverlappingHost entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @param entityRowHandler The handler of entity row of OverlappingHost. (NotNull)
     */
    public void selectCursor(final OverlappingHostCB cb,
            final EntityRowHandler<OverlappingHost> entityRowHandler) {
        facadeSelectCursor(cb, entityRowHandler);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * overlappingHostBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(OverlappingHostCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> HpSLSFunction<OverlappingHostCB, RESULT> scalarSelect(
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
     * @param overlappingHostList The entity list of overlappingHost. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final List<OverlappingHost> overlappingHostList,
            final ReferrerLoaderHandler<LoaderOfOverlappingHost> handler) {
        xassLRArg(overlappingHostList, handler);
        handler.handle(new LoaderOfOverlappingHost().ready(overlappingHostList,
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
     * @param overlappingHost The entity of overlappingHost. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final OverlappingHost overlappingHost,
            final ReferrerLoaderHandler<LoaderOfOverlappingHost> handler) {
        xassLRArg(overlappingHost, handler);
        handler.handle(new LoaderOfOverlappingHost().ready(
                xnewLRAryLs(overlappingHost), _behaviorSelector));
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================
    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param overlappingHostList The list of overlappingHost. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<OverlappingHost> overlappingHostList) {
        return helpExtractListInternally(overlappingHostList, "id");
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * overlappingHost.setFoo...(value);
     * overlappingHost.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//overlappingHost.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//overlappingHost.set...;</span>
     * overlappingHostBhv.<span style="color: #DD4747">insert</span>(overlappingHost);
     * ... = overlappingHost.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param overlappingHost The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final OverlappingHost overlappingHost) {
        doInsert(overlappingHost, null);
    }

    /**
     * Update the entity modified-only. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * overlappingHost.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * overlappingHost.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//overlappingHost.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//overlappingHost.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * overlappingHost.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     overlappingHostBhv.<span style="color: #DD4747">update</span>(overlappingHost);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param overlappingHost The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final OverlappingHost overlappingHost) {
        doUpdate(overlappingHost, null);
    }

    /**
     * Update the entity non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * overlappingHost.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * overlappingHost.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//overlappingHost.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//overlappingHost.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//overlappingHost.setVersionNo(value);</span>
     * overlappingHostBhv.<span style="color: #DD4747">updateNonstrict</span>(overlappingHost);
     * </pre>
     * @param overlappingHost The entity of update. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final OverlappingHost overlappingHost) {
        doUpdateNonstrict(overlappingHost, null);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, ExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param overlappingHost The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final OverlappingHost overlappingHost) {
        doInsertOrUpdate(overlappingHost, null, null);
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param overlappingHost The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(final OverlappingHost overlappingHost) {
        doInsertOrUpdateNonstrict(overlappingHost, null, null);
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * overlappingHost.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * overlappingHost.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     overlappingHostBhv.<span style="color: #DD4747">delete</span>(overlappingHost);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param overlappingHost The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final OverlappingHost overlappingHost) {
        doDelete(overlappingHost, null);
    }

    /**
     * Delete the entity non-strictly. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * overlappingHost.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//overlappingHost.setVersionNo(value);</span>
     * overlappingHostBhv.<span style="color: #DD4747">deleteNonstrict</span>(overlappingHost);
     * </pre>
     * @param overlappingHost The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final OverlappingHost overlappingHost) {
        doDeleteNonstrict(overlappingHost, null);
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * overlappingHost.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//overlappingHost.setVersionNo(value);</span>
     * overlappingHostBhv.<span style="color: #DD4747">deleteNonstrictIgnoreDeleted</span>(overlappingHost);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param overlappingHost The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(
            final OverlappingHost overlappingHost) {
        doDeleteNonstrictIgnoreDeleted(overlappingHost, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(final OverlappingHost et,
            final DeleteOption<OverlappingHostCB> op) {
        assertObjectNotNull("overlappingHost", et);
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
     *     OverlappingHost overlappingHost = new OverlappingHost();
     *     overlappingHost.setFooName("foo");
     *     if (...) {
     *         overlappingHost.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     overlappingHostList.add(overlappingHost);
     * }
     * overlappingHostBhv.<span style="color: #DD4747">batchInsert</span>(overlappingHostList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<OverlappingHost> overlappingHostList) {
        return doBatchInsert(overlappingHostList, null);
    }

    /**
     * Batch-update the entity list modified-only of same-set columns. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     OverlappingHost overlappingHost = new OverlappingHost();
     *     overlappingHost.setFooName("foo");
     *     if (...) {
     *         overlappingHost.setFooPrice(123);
     *     } else {
     *         overlappingHost.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//overlappingHost.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     overlappingHostList.add(overlappingHost);
     * }
     * overlappingHostBhv.<span style="color: #DD4747">batchUpdate</span>(overlappingHostList);
     * </pre>
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<OverlappingHost> overlappingHostList) {
        return doBatchUpdate(overlappingHostList, null);
    }

    /**
     * Batch-update the entity list specified-only. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * overlappingHostBhv.<span style="color: #DD4747">batchUpdate</span>(overlappingHostList, new SpecifyQuery<OverlappingHostCB>() {
     *     public void specify(OverlappingHostCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * overlappingHostBhv.<span style="color: #DD4747">batchUpdate</span>(overlappingHostList, new SpecifyQuery<OverlappingHostCB>() {
     *     public void specify(OverlappingHostCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<OverlappingHost> overlappingHostList,
            final SpecifyQuery<OverlappingHostCB> updateColumnSpec) {
        return doBatchUpdate(overlappingHostList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 140%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     OverlappingHost overlappingHost = new OverlappingHost();
     *     overlappingHost.setFooName("foo");
     *     if (...) {
     *         overlappingHost.setFooPrice(123);
     *     } else {
     *         overlappingHost.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//overlappingHost.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     overlappingHostList.add(overlappingHost);
     * }
     * overlappingHostBhv.<span style="color: #DD4747">batchUpdate</span>(overlappingHostList);
     * </pre>
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<OverlappingHost> overlappingHostList) {
        return doBatchUpdateNonstrict(overlappingHostList, null);
    }

    /**
     * Batch-update the entity list non-strictly specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * overlappingHostBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(overlappingHostList, new SpecifyQuery<OverlappingHostCB>() {
     *     public void specify(OverlappingHostCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * overlappingHostBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(overlappingHostList, new SpecifyQuery<OverlappingHostCB>() {
     *     public void specify(OverlappingHostCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<OverlappingHost> overlappingHostList,
            final SpecifyQuery<OverlappingHostCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(overlappingHostList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-delete the entity list. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<OverlappingHost> overlappingHostList) {
        return doBatchDelete(overlappingHostList, null);
    }

    /**
     * Batch-delete the entity list non-strictly. {NonExclusiveControl} <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<OverlappingHost> overlappingHostList) {
        return doBatchDeleteNonstrict(overlappingHostList, null);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * overlappingHostBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;OverlappingHost, OverlappingHostCB&gt;() {
     *     public ConditionBean setup(OverlappingHost entity, OverlappingHostCB intoCB) {
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
            final QueryInsertSetupper<OverlappingHost, OverlappingHostCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    /**
     * Update the several entities by query non-strictly modified-only. (NonExclusiveControl)
     * <pre>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//overlappingHost.setPK...(value);</span>
     * overlappingHost.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//overlappingHost.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//overlappingHost.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//overlappingHost.setVersionNo(value);</span>
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * overlappingHostBhv.<span style="color: #DD4747">queryUpdate</span>(overlappingHost, cb);
     * </pre>
     * @param overlappingHost The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final OverlappingHost overlappingHost,
            final OverlappingHostCB cb) {
        return doQueryUpdate(overlappingHost, cb, null);
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * overlappingHostBhv.<span style="color: #DD4747">queryDelete</span>(overlappingHost, cb);
     * </pre>
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final OverlappingHostCB cb) {
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
     * OverlappingHost overlappingHost = new OverlappingHost();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * overlappingHost.setFoo...(value);
     * overlappingHost.setBar...(value);
     * InsertOption<OverlappingHostCB> option = new InsertOption<OverlappingHostCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * overlappingHostBhv.<span style="color: #DD4747">varyingInsert</span>(overlappingHost, option);
     * ... = overlappingHost.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param overlappingHost The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final OverlappingHost overlappingHost,
            final InsertOption<OverlappingHostCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(overlappingHost, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * overlappingHost.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * overlappingHost.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * overlappingHost.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;OverlappingHostCB&gt; option = new UpdateOption&lt;OverlappingHostCB&gt;();
     *     option.self(new SpecifyQuery&lt;OverlappingHostCB&gt;() {
     *         public void specify(OverlappingHostCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     overlappingHostBhv.<span style="color: #DD4747">varyingUpdate</span>(overlappingHost, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param overlappingHost The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final OverlappingHost overlappingHost,
            final UpdateOption<OverlappingHostCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(overlappingHost, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * OverlappingHost overlappingHost = new OverlappingHost();
     * overlappingHost.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * overlappingHost.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//overlappingHost.setVersionNo(value);</span>
     * UpdateOption&lt;OverlappingHostCB&gt; option = new UpdateOption&lt;OverlappingHostCB&gt;();
     * option.self(new SpecifyQuery&lt;OverlappingHostCB&gt;() {
     *     public void specify(OverlappingHostCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * overlappingHostBhv.<span style="color: #DD4747">varyingUpdateNonstrict</span>(overlappingHost, option);
     * </pre>
     * @param overlappingHost The entity of update. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(final OverlappingHost overlappingHost,
            final UpdateOption<OverlappingHostCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(overlappingHost, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param overlappingHost The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final OverlappingHost overlappingHost,
            final InsertOption<OverlappingHostCB> insertOption,
            final UpdateOption<OverlappingHostCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdate(overlappingHost, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param overlappingHost The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final OverlappingHost overlappingHost,
            final InsertOption<OverlappingHostCB> insertOption,
            final UpdateOption<OverlappingHostCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdateNonstrict(overlappingHost, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param overlappingHost The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final OverlappingHost overlappingHost,
            final DeleteOption<OverlappingHostCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(overlappingHost, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param overlappingHost The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(final OverlappingHost overlappingHost,
            final DeleteOption<OverlappingHostCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(overlappingHost, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<OverlappingHost> overlappingHostList,
            final InsertOption<OverlappingHostCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(overlappingHostList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<OverlappingHost> overlappingHostList,
            final UpdateOption<OverlappingHostCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(overlappingHostList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<OverlappingHost> overlappingHostList,
            final UpdateOption<OverlappingHostCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(overlappingHostList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<OverlappingHost> overlappingHostList,
            final DeleteOption<OverlappingHostCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(overlappingHostList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param overlappingHostList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<OverlappingHost> overlappingHostList,
            final DeleteOption<OverlappingHostCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(overlappingHostList, option);
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
            final QueryInsertSetupper<OverlappingHost, OverlappingHostCB> setupper,
            final InsertOption<OverlappingHostCB> option) {
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
     * OverlappingHost overlappingHost = new OverlappingHost();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//overlappingHost.setPK...(value);</span>
     * overlappingHost.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//overlappingHost.setVersionNo(value);</span>
     * OverlappingHostCB cb = new OverlappingHostCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;OverlappingHostCB&gt; option = new UpdateOption&lt;OverlappingHostCB&gt;();
     * option.self(new SpecifyQuery&lt;OverlappingHostCB&gt;() {
     *     public void specify(OverlappingHostCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * overlappingHostBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(overlappingHost, cb, option);
     * </pre>
     * @param overlappingHost The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final OverlappingHost overlappingHost,
            final OverlappingHostCB cb,
            final UpdateOption<OverlappingHostCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(overlappingHost, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of OverlappingHost. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final OverlappingHostCB cb,
            final DeleteOption<OverlappingHostCB> option) {
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
    public OutsideSqlBasicExecutor<OverlappingHostBhv> outsideSql() {
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
    protected Class<? extends OverlappingHost> typeOfSelectedEntity() {
        return OverlappingHost.class;
    }

    @Override
    protected Class<OverlappingHost> typeOfHandlingEntity() {
        return OverlappingHost.class;
    }

    @Override
    protected Class<OverlappingHostCB> typeOfHandlingConditionBean() {
        return OverlappingHostCB.class;
    }
}
