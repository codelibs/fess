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

import jp.sf.fess.db.bsbhv.loader.LoaderOfFileConfigToRoleTypeMapping;
import jp.sf.fess.db.bsentity.dbmeta.FileConfigToRoleTypeMappingDbm;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.FileConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.db.exentity.RoleType;

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
import org.seasar.dbflute.exception.DangerousResultSizeException;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.dbflute.exception.EntityDuplicatedException;
import org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException;
import org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException;
import org.seasar.dbflute.exception.SelectEntityConditionNotFoundException;
import org.seasar.dbflute.optional.OptionalEntity;
import org.seasar.dbflute.outsidesql.executor.OutsideSqlBasicExecutor;

/**
 * The behavior of FILE_CONFIG_TO_ROLE_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, FILE_CONFIG_ID, ROLE_TYPE_ID
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
 *     FILE_CRAWLING_CONFIG, ROLE_TYPE
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     fileCrawlingConfig, roleType
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsFileConfigToRoleTypeMappingBhv
        extends
        AbstractBehaviorWritable<FileConfigToRoleTypeMapping, FileConfigToRoleTypeMappingCB> {

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
    public FileConfigToRoleTypeMappingDbm getDBMeta() {
        return FileConfigToRoleTypeMappingDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public FileConfigToRoleTypeMappingDbm getMyDBMeta() {
        return FileConfigToRoleTypeMappingDbm.getInstance();
    }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    @Override
    public FileConfigToRoleTypeMappingCB newConditionBean() {
        return new FileConfigToRoleTypeMappingCB();
    }

    /** @return The instance of new entity as my table type. (NotNull) */
    public FileConfigToRoleTypeMapping newMyEntity() {
        return new FileConfigToRoleTypeMapping();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public FileConfigToRoleTypeMappingCB newMyConditionBean() {
        return new FileConfigToRoleTypeMappingCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * int count = fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final FileConfigToRoleTypeMappingCB cb) {
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
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (fileConfigToRoleTypeMapping != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = fileConfigToRoleTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToRoleTypeMapping selectEntity(
            final FileConfigToRoleTypeMappingCB cb) {
        return facadeSelectEntity(cb);
    }

    protected FileConfigToRoleTypeMapping facadeSelectEntity(
            final FileConfigToRoleTypeMappingCB cb) {
        return doSelectEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToRoleTypeMapping> OptionalEntity<ENTITY> doSelectOptionalEntity(
            final FileConfigToRoleTypeMappingCB cb,
            final Class<? extends ENTITY> tp) {
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
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = fileConfigToRoleTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToRoleTypeMapping selectEntityWithDeletedCheck(
            final FileConfigToRoleTypeMappingCB cb) {
        return facadeSelectEntityWithDeletedCheck(cb);
    }

    /**
     * Select the entity by the primary-key value.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToRoleTypeMapping selectByPKValue(final Long id) {
        return facadeSelectByPKValue(id);
    }

    protected FileConfigToRoleTypeMapping facadeSelectByPKValue(final Long id) {
        return doSelectByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToRoleTypeMapping> ENTITY doSelectByPK(
            final Long id, final Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected <ENTITY extends FileConfigToRoleTypeMapping> OptionalEntity<ENTITY> doSelectOptionalByPK(
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
    public FileConfigToRoleTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKWithDeletedCheck(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToRoleTypeMapping> ENTITY doSelectByPKWithDeletedCheck(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), tp);
    }

    protected FileConfigToRoleTypeMappingCB xprepareCBAsPK(final Long id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;FileConfigToRoleTypeMapping&gt; fileConfigToRoleTypeMappingList = fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping : fileConfigToRoleTypeMappingList) {
     *     ... = fileConfigToRoleTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<FileConfigToRoleTypeMapping> selectList(
            final FileConfigToRoleTypeMappingCB cb) {
        return facadeSelectList(cb);
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean. <br />
     * (both count-select and paging-select are executed)
     * <pre>
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;FileConfigToRoleTypeMapping&gt; page = fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping : page) {
     *     ... = fileConfigToRoleTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<FileConfigToRoleTypeMapping> selectPage(
            final FileConfigToRoleTypeMappingCB cb) {
        return facadeSelectPage(cb);
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;FileConfigToRoleTypeMapping&gt;() {
     *     public void handle(FileConfigToRoleTypeMapping entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @param entityRowHandler The handler of entity row of FileConfigToRoleTypeMapping. (NotNull)
     */
    public void selectCursor(final FileConfigToRoleTypeMappingCB cb,
            final EntityRowHandler<FileConfigToRoleTypeMapping> entityRowHandler) {
        facadeSelectCursor(cb, entityRowHandler);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(FileConfigToRoleTypeMappingCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> HpSLSFunction<FileConfigToRoleTypeMappingCB, RESULT> scalarSelect(
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
     * @param fileConfigToRoleTypeMappingList The entity list of fileConfigToRoleTypeMapping. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList,
            final ReferrerLoaderHandler<LoaderOfFileConfigToRoleTypeMapping> handler) {
        xassLRArg(fileConfigToRoleTypeMappingList, handler);
        handler.handle(new LoaderOfFileConfigToRoleTypeMapping().ready(
                fileConfigToRoleTypeMappingList, _behaviorSelector));
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
     * @param fileConfigToRoleTypeMapping The entity of fileConfigToRoleTypeMapping. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping,
            final ReferrerLoaderHandler<LoaderOfFileConfigToRoleTypeMapping> handler) {
        xassLRArg(fileConfigToRoleTypeMapping, handler);
        handler.handle(new LoaderOfFileConfigToRoleTypeMapping().ready(
                xnewLRAryLs(fileConfigToRoleTypeMapping), _behaviorSelector));
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================
    /**
     * Pull out the list of foreign table 'FileCrawlingConfig'.
     * @param fileConfigToRoleTypeMappingList The list of fileConfigToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<FileCrawlingConfig> pulloutFileCrawlingConfig(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList) {
        return helpPulloutInternally(fileConfigToRoleTypeMappingList,
                "fileCrawlingConfig");
    }

    /**
     * Pull out the list of foreign table 'RoleType'.
     * @param fileConfigToRoleTypeMappingList The list of fileConfigToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<RoleType> pulloutRoleType(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList) {
        return helpPulloutInternally(fileConfigToRoleTypeMappingList,
                "roleType");
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param fileConfigToRoleTypeMappingList The list of fileConfigToRoleTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList) {
        return helpExtractListInternally(fileConfigToRoleTypeMappingList, "id");
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileConfigToRoleTypeMapping.setFoo...(value);
     * fileConfigToRoleTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.set...;</span>
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">insert</span>(fileConfigToRoleTypeMapping);
     * ... = fileConfigToRoleTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param fileConfigToRoleTypeMapping The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping) {
        doInsert(fileConfigToRoleTypeMapping, null);
    }

    /**
     * Update the entity modified-only. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     * fileConfigToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileConfigToRoleTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileConfigToRoleTypeMapping.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">update</span>(fileConfigToRoleTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileConfigToRoleTypeMapping The entity of update. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping) {
        doUpdate(fileConfigToRoleTypeMapping, null);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param fileConfigToRoleTypeMapping The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping) {
        doInsertOrUpdate(fileConfigToRoleTypeMapping, null, null);
    }

    /**
     * Delete the entity. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     * fileConfigToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileConfigToRoleTypeMapping.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">delete</span>(fileConfigToRoleTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileConfigToRoleTypeMapping The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping) {
        doDelete(fileConfigToRoleTypeMapping, null);
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
     *     FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     *     fileConfigToRoleTypeMapping.setFooName("foo");
     *     if (...) {
     *         fileConfigToRoleTypeMapping.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     fileConfigToRoleTypeMappingList.add(fileConfigToRoleTypeMapping);
     * }
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">batchInsert</span>(fileConfigToRoleTypeMappingList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param fileConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList) {
        return doBatchInsert(fileConfigToRoleTypeMappingList, null);
    }

    /**
     * Batch-update the entity list modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     *     fileConfigToRoleTypeMapping.setFooName("foo");
     *     if (...) {
     *         fileConfigToRoleTypeMapping.setFooPrice(123);
     *     } else {
     *         fileConfigToRoleTypeMapping.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     fileConfigToRoleTypeMappingList.add(fileConfigToRoleTypeMapping);
     * }
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">batchUpdate</span>(fileConfigToRoleTypeMappingList);
     * </pre>
     * @param fileConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList) {
        return doBatchUpdate(fileConfigToRoleTypeMappingList, null);
    }

    /**
     * Batch-update the entity list specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">batchUpdate</span>(fileConfigToRoleTypeMappingList, new SpecifyQuery<FileConfigToRoleTypeMappingCB>() {
     *     public void specify(FileConfigToRoleTypeMappingCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">batchUpdate</span>(fileConfigToRoleTypeMappingList, new SpecifyQuery<FileConfigToRoleTypeMappingCB>() {
     *     public void specify(FileConfigToRoleTypeMappingCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param fileConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList,
            final SpecifyQuery<FileConfigToRoleTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(fileConfigToRoleTypeMappingList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-delete the entity list. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param fileConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList) {
        return doBatchDelete(fileConfigToRoleTypeMappingList, null);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;FileConfigToRoleTypeMapping, FileConfigToRoleTypeMappingCB&gt;() {
     *     public ConditionBean setup(FileConfigToRoleTypeMapping entity, FileConfigToRoleTypeMappingCB intoCB) {
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
            final QueryInsertSetupper<FileConfigToRoleTypeMapping, FileConfigToRoleTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    /**
     * Update the several entities by query non-strictly modified-only. (NonExclusiveControl)
     * <pre>
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.setPK...(value);</span>
     * fileConfigToRoleTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.setVersionNo(value);</span>
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">queryUpdate</span>(fileConfigToRoleTypeMapping, cb);
     * </pre>
     * @param fileConfigToRoleTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping,
            final FileConfigToRoleTypeMappingCB cb) {
        return doQueryUpdate(fileConfigToRoleTypeMapping, cb, null);
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">queryDelete</span>(fileConfigToRoleTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final FileConfigToRoleTypeMappingCB cb) {
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
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileConfigToRoleTypeMapping.setFoo...(value);
     * fileConfigToRoleTypeMapping.setBar...(value);
     * InsertOption<FileConfigToRoleTypeMappingCB> option = new InsertOption<FileConfigToRoleTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">varyingInsert</span>(fileConfigToRoleTypeMapping, option);
     * ... = fileConfigToRoleTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileConfigToRoleTypeMapping The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping,
            final InsertOption<FileConfigToRoleTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(fileConfigToRoleTypeMapping, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     * fileConfigToRoleTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileConfigToRoleTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileConfigToRoleTypeMapping.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;FileConfigToRoleTypeMappingCB&gt; option = new UpdateOption&lt;FileConfigToRoleTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *         public void specify(FileConfigToRoleTypeMappingCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">varyingUpdate</span>(fileConfigToRoleTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileConfigToRoleTypeMapping The entity of update. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping,
            final UpdateOption<FileConfigToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(fileConfigToRoleTypeMapping, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param fileConfigToRoleTypeMapping The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping,
            final InsertOption<FileConfigToRoleTypeMappingCB> insertOption,
            final UpdateOption<FileConfigToRoleTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdate(fileConfigToRoleTypeMapping, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param fileConfigToRoleTypeMapping The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping,
            final DeleteOption<FileConfigToRoleTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(fileConfigToRoleTypeMapping, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param fileConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList,
            final InsertOption<FileConfigToRoleTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(fileConfigToRoleTypeMappingList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param fileConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList,
            final UpdateOption<FileConfigToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(fileConfigToRoleTypeMappingList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param fileConfigToRoleTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList,
            final DeleteOption<FileConfigToRoleTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(fileConfigToRoleTypeMappingList, option);
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
            final QueryInsertSetupper<FileConfigToRoleTypeMapping, FileConfigToRoleTypeMappingCB> setupper,
            final InsertOption<FileConfigToRoleTypeMappingCB> option) {
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
     * FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.setPK...(value);</span>
     * fileConfigToRoleTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileConfigToRoleTypeMapping.setVersionNo(value);</span>
     * FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;FileConfigToRoleTypeMappingCB&gt; option = new UpdateOption&lt;FileConfigToRoleTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void specify(FileConfigToRoleTypeMappingCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileConfigToRoleTypeMappingBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(fileConfigToRoleTypeMapping, cb, option);
     * </pre>
     * @param fileConfigToRoleTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(
            final FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping,
            final FileConfigToRoleTypeMappingCB cb,
            final UpdateOption<FileConfigToRoleTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(fileConfigToRoleTypeMapping, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of FileConfigToRoleTypeMapping. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final FileConfigToRoleTypeMappingCB cb,
            final DeleteOption<FileConfigToRoleTypeMappingCB> option) {
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
    public OutsideSqlBasicExecutor<FileConfigToRoleTypeMappingBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                         Type Helper
    //                                                                         ===========
    @Override
    protected Class<? extends FileConfigToRoleTypeMapping> typeOfSelectedEntity() {
        return FileConfigToRoleTypeMapping.class;
    }

    @Override
    protected Class<FileConfigToRoleTypeMapping> typeOfHandlingEntity() {
        return FileConfigToRoleTypeMapping.class;
    }

    @Override
    protected Class<FileConfigToRoleTypeMappingCB> typeOfHandlingConditionBean() {
        return FileConfigToRoleTypeMappingCB.class;
    }
}
