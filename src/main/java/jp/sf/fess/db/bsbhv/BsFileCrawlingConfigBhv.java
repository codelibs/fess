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

import jp.sf.fess.db.bsbhv.loader.LoaderOfFileCrawlingConfig;
import jp.sf.fess.db.bsentity.dbmeta.FileCrawlingConfigDbm;
import jp.sf.fess.db.cbean.FileAuthenticationCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.exbhv.FileCrawlingConfigBhv;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;

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
import org.seasar.dbflute.cbean.chelper.HpSLSExecutor;
import org.seasar.dbflute.cbean.chelper.HpSLSFunction;
import org.seasar.dbflute.dbmeta.DBMeta;
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
 * The behavior of FILE_CRAWLING_CONFIG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, NAME, PATHS, INCLUDED_PATHS, EXCLUDED_PATHS, INCLUDED_DOC_PATHS, EXCLUDED_DOC_PATHS, CONFIG_PARAMETER, DEPTH, MAX_ACCESS_COUNT, NUM_OF_THREAD, INTERVAL_TIME, BOOST, AVAILABLE, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     FILE_AUTHENTICATION, FILE_CONFIG_TO_LABEL_TYPE_MAPPING, FILE_CONFIG_TO_ROLE_TYPE_MAPPING
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     fileAuthenticationList, fileConfigToLabelTypeMappingList, fileConfigToRoleTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsFileCrawlingConfigBhv extends AbstractBehaviorWritable {

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
        return "FILE_CRAWLING_CONFIG";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** {@inheritDoc} */
    @Override
    public DBMeta getDBMeta() {
        return FileCrawlingConfigDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public FileCrawlingConfigDbm getMyDBMeta() {
        return FileCrawlingConfigDbm.getInstance();
    }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    @Override
    public FileCrawlingConfig newEntity() {
        return new FileCrawlingConfig();
    }

    /** {@inheritDoc} */
    @Override
    public FileCrawlingConfigCB newConditionBean() {
        return new FileCrawlingConfigCB();
    }

    /** @return The instance of new entity as my table type. (NotNull) */
    public FileCrawlingConfig newMyEntity() {
        return new FileCrawlingConfig();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public FileCrawlingConfigCB newMyConditionBean() {
        return new FileCrawlingConfigCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * int count = fileCrawlingConfigBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final FileCrawlingConfigCB cb) {
        return facadeSelectCount(cb);
    }

    protected int facadeSelectCount(final FileCrawlingConfigCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final FileCrawlingConfigCB cb) { // called by selectCount(cb)
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final FileCrawlingConfigCB cb) { // called by selectPage(cb)
        assertCBStateValid(cb);
        return delegateSelectCountPlainly(cb);
    }

    @Override
    protected int doReadCount(final ConditionBean cb) {
        return facadeSelectCount(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean. #beforejava8 <br />
     * <span style="color: #AD4747; font-size: 120%">The return might be null if no data, so you should have null check.</span> <br />
     * <span style="color: #AD4747; font-size: 120%">If the data always exists as your business rule, use selectEntityWithDeletedCheck().</span>
     * <pre>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (fileCrawlingConfig != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = fileCrawlingConfig.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileCrawlingConfig selectEntity(final FileCrawlingConfigCB cb) {
        return facadeSelectEntity(cb);
    }

    protected FileCrawlingConfig facadeSelectEntity(
            final FileCrawlingConfigCB cb) {
        return doSelectEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileCrawlingConfig> ENTITY doSelectEntity(
            final FileCrawlingConfigCB cb, final Class<ENTITY> tp) {
        return helpSelectEntityInternally(cb, tp);
    }

    protected <ENTITY extends FileCrawlingConfig> OptionalEntity<ENTITY> doSelectOptionalEntity(
            final FileCrawlingConfigCB cb, final Class<ENTITY> tp) {
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
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = fileCrawlingConfig.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileCrawlingConfig selectEntityWithDeletedCheck(
            final FileCrawlingConfigCB cb) {
        return facadeSelectEntityWithDeletedCheck(cb);
    }

    protected FileCrawlingConfig facadeSelectEntityWithDeletedCheck(
            final FileCrawlingConfigCB cb) {
        return doSelectEntityWithDeletedCheck(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileCrawlingConfig> ENTITY doSelectEntityWithDeletedCheck(
            final FileCrawlingConfigCB cb, final Class<ENTITY> tp) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", tp);
        return helpSelectEntityWithDeletedCheckInternally(cb, tp);
    }

    @Override
    protected Entity doReadEntityWithDeletedCheck(final ConditionBean cb) {
        return facadeSelectEntityWithDeletedCheck(downcast(cb));
    }

    /**
     * Select the entity by the primary-key value.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileCrawlingConfig selectByPKValue(final Long id) {
        return facadeSelectByPKValue(id);
    }

    protected FileCrawlingConfig facadeSelectByPKValue(final Long id) {
        return doSelectByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileCrawlingConfig> ENTITY doSelectByPK(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected <ENTITY extends FileCrawlingConfig> OptionalEntity<ENTITY> doSelectOptionalByPK(
            final Long id, final Class<ENTITY> tp) {
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
    public FileCrawlingConfig selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKWithDeletedCheck(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileCrawlingConfig> ENTITY doSelectByPKWithDeletedCheck(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), tp);
    }

    protected FileCrawlingConfigCB xprepareCBAsPK(final Long id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;FileCrawlingConfig&gt; fileCrawlingConfigList = fileCrawlingConfigBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<FileCrawlingConfig> selectList(
            final FileCrawlingConfigCB cb) {
        return facadeSelectList(cb);
    }

    protected ListResultBean<FileCrawlingConfig> facadeSelectList(
            final FileCrawlingConfigCB cb) {
        return doSelectList(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileCrawlingConfig> ListResultBean<ENTITY> doSelectList(
            final FileCrawlingConfigCB cb, final Class<ENTITY> tp) {
        return helpSelectListInternally(cb, tp);
    }

    @Override
    protected ListResultBean<? extends Entity> doReadList(final ConditionBean cb) {
        return facadeSelectList(downcast(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean. <br />
     * (both count-select and paging-select are executed)
     * <pre>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;FileCrawlingConfig&gt; page = fileCrawlingConfigBhv.<span style="color: #DD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (FileCrawlingConfig fileCrawlingConfig : page) {
     *     ... = fileCrawlingConfig.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<FileCrawlingConfig> selectPage(
            final FileCrawlingConfigCB cb) {
        return facadeSelectPage(cb);
    }

    protected PagingResultBean<FileCrawlingConfig> facadeSelectPage(
            final FileCrawlingConfigCB cb) {
        return doSelectPage(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileCrawlingConfig> PagingResultBean<ENTITY> doSelectPage(
            final FileCrawlingConfigCB cb, final Class<ENTITY> tp) {
        return helpSelectPageInternally(cb, tp);
    }

    @Override
    protected PagingResultBean<? extends Entity> doReadPage(
            final ConditionBean cb) {
        return facadeSelectPage(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * fileCrawlingConfigBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;FileCrawlingConfig&gt;() {
     *     public void handle(FileCrawlingConfig entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @param entityRowHandler The handler of entity row of FileCrawlingConfig. (NotNull)
     */
    public void selectCursor(final FileCrawlingConfigCB cb,
            final EntityRowHandler<FileCrawlingConfig> entityRowHandler) {
        facadeSelectCursor(cb, entityRowHandler);
    }

    protected void facadeSelectCursor(final FileCrawlingConfigCB cb,
            final EntityRowHandler<FileCrawlingConfig> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileCrawlingConfig> void doSelectCursor(
            final FileCrawlingConfigCB cb,
            final EntityRowHandler<ENTITY> handler, final Class<ENTITY> tp) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler", handler);
        assertObjectNotNull("entityType", tp);
        assertSpecifyDerivedReferrerEntityProperty(cb, tp);
        helpSelectCursorInternally(cb, handler, tp);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(FileCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> HpSLSFunction<FileCrawlingConfigCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return facadeScalarSelect(resultType);
    }

    protected <RESULT> HpSLSFunction<FileCrawlingConfigCB, RESULT> facadeScalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newConditionBean());
    }

    protected <RESULT, CB extends FileCrawlingConfigCB> HpSLSFunction<CB, RESULT> doScalarSelect(
            final Class<RESULT> tp, final CB cb) {
        assertObjectNotNull("resultType", tp);
        assertCBStateValid(cb);
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        final HpSLSExecutor<CB, RESULT> executor = createHpSLSExecutor(); // variable to resolve generic
        return createSLSFunction(cb, tp, executor);
    }

    @Override
    protected <RESULT> HpSLSFunction<? extends ConditionBean, RESULT> doReadScalar(
            final Class<RESULT> tp) {
        return facadeScalarSelect(tp);
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
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final List<FileCrawlingConfig> fileCrawlingConfigList,
            final ReferrerLoaderHandler<LoaderOfFileCrawlingConfig> handler) {
        xassLRArg(fileCrawlingConfigList, handler);
        handler.handle(new LoaderOfFileCrawlingConfig().ready(
                fileCrawlingConfigList, _behaviorSelector));
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
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final FileCrawlingConfig fileCrawlingConfig,
            final ReferrerLoaderHandler<LoaderOfFileCrawlingConfig> handler) {
        xassLRArg(fileCrawlingConfig, handler);
        handler.handle(new LoaderOfFileCrawlingConfig().ready(
                xnewLRAryLs(fileCrawlingConfig), _behaviorSelector));
    }

    /**
     * Load referrer of fileAuthenticationList by the set-upper of referrer. <br />
     * FILE_AUTHENTICATION by FILE_CRAWLING_CONFIG_ID, named 'fileAuthenticationList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">loadFileAuthenticationList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileAuthenticationCB&gt;() {
     *     public void setup(FileAuthenticationCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.<span style="color: #DD4747">getFileAuthenticationList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setFileCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileCrawlingConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileAuthentication> loadFileAuthenticationList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final ConditionBeanSetupper<FileAuthenticationCB> setupper) {
        xassLRArg(fileCrawlingConfigList, setupper);
        return doLoadFileAuthenticationList(
                fileCrawlingConfigList,
                new LoadReferrerOption<FileAuthenticationCB, FileAuthentication>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of fileAuthenticationList by the set-upper of referrer. <br />
     * FILE_AUTHENTICATION by FILE_CRAWLING_CONFIG_ID, named 'fileAuthenticationList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">loadFileAuthenticationList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileAuthenticationCB&gt;() {
     *     public void setup(FileAuthenticationCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = fileCrawlingConfig.<span style="color: #DD4747">getFileAuthenticationList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setFileCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileCrawlingConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileAuthentication> loadFileAuthenticationList(
            final FileCrawlingConfig fileCrawlingConfig,
            final ConditionBeanSetupper<FileAuthenticationCB> setupper) {
        xassLRArg(fileCrawlingConfig, setupper);
        return doLoadFileAuthenticationList(
                xnewLRLs(fileCrawlingConfig),
                new LoadReferrerOption<FileAuthenticationCB, FileAuthentication>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileAuthentication> loadFileAuthenticationList(
            final FileCrawlingConfig fileCrawlingConfig,
            final LoadReferrerOption<FileAuthenticationCB, FileAuthentication> loadReferrerOption) {
        xassLRArg(fileCrawlingConfig, loadReferrerOption);
        return loadFileAuthenticationList(xnewLRLs(fileCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<FileAuthentication> loadFileAuthenticationList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileAuthenticationCB, FileAuthentication> loadReferrerOption) {
        xassLRArg(fileCrawlingConfigList, loadReferrerOption);
        if (fileCrawlingConfigList.isEmpty()) {
            return (NestedReferrerListGateway<FileAuthentication>) EMPTY_NREF_LGWAY;
        }
        return doLoadFileAuthenticationList(fileCrawlingConfigList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<FileAuthentication> doLoadFileAuthenticationList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileAuthenticationCB, FileAuthentication> option) {
        return helpLoadReferrerInternally(fileCrawlingConfigList, option,
                "fileAuthenticationList");
    }

    /**
     * Load referrer of fileConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToLabelTypeMappingList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">loadFileConfigToLabelTypeMappingList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(FileConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.<span style="color: #DD4747">getFileConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setFileConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(fileCrawlingConfigList, setupper);
        return doLoadFileConfigToLabelTypeMappingList(
                fileCrawlingConfigList,
                new LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of fileConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToLabelTypeMappingList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">loadFileConfigToLabelTypeMappingList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(FileConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = fileCrawlingConfig.<span style="color: #DD4747">getFileConfigToLabelTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setFileConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(fileCrawlingConfig, setupper);
        return doLoadFileConfigToLabelTypeMappingList(
                xnewLRLs(fileCrawlingConfig),
                new LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfig, loadReferrerOption);
        return loadFileConfigToLabelTypeMappingList(
                xnewLRLs(fileCrawlingConfig), loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<FileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfigList, loadReferrerOption);
        if (fileCrawlingConfigList.isEmpty()) {
            return (NestedReferrerListGateway<FileConfigToLabelTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadFileConfigToLabelTypeMappingList(fileCrawlingConfigList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<FileConfigToLabelTypeMapping> doLoadFileConfigToLabelTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> option) {
        return helpLoadReferrerInternally(fileCrawlingConfigList, option,
                "fileConfigToLabelTypeMappingList");
    }

    /**
     * Load referrer of fileConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToRoleTypeMappingList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">loadFileConfigToRoleTypeMappingList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(FileConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.<span style="color: #DD4747">getFileConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setFileConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(fileCrawlingConfigList, setupper);
        return doLoadFileConfigToRoleTypeMappingList(
                fileCrawlingConfigList,
                new LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of fileConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToRoleTypeMappingList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">loadFileConfigToRoleTypeMappingList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(FileConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = fileCrawlingConfig.<span style="color: #DD4747">getFileConfigToRoleTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setFileConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(fileCrawlingConfig, setupper);
        return doLoadFileConfigToRoleTypeMappingList(
                xnewLRLs(fileCrawlingConfig),
                new LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<FileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfig, loadReferrerOption);
        return loadFileConfigToRoleTypeMappingList(
                xnewLRLs(fileCrawlingConfig), loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<FileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfigList, loadReferrerOption);
        if (fileCrawlingConfigList.isEmpty()) {
            return (NestedReferrerListGateway<FileConfigToRoleTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadFileConfigToRoleTypeMappingList(fileCrawlingConfigList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<FileConfigToRoleTypeMapping> doLoadFileConfigToRoleTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> option) {
        return helpLoadReferrerInternally(fileCrawlingConfigList, option,
                "fileConfigToRoleTypeMappingList");
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================
    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param fileCrawlingConfigList The list of fileCrawlingConfig. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return helpExtractListInternally(fileCrawlingConfigList, "id");
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileCrawlingConfig.setFoo...(value);
     * fileCrawlingConfig.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.set...;</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">insert</span>(fileCrawlingConfig);
     * ... = fileCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param fileCrawlingConfig The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final FileCrawlingConfig fileCrawlingConfig) {
        doInsert(fileCrawlingConfig, null);
    }

    protected void doInsert(final FileCrawlingConfig et,
            final InsertOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfig", et);
        prepareInsertOption(op);
        delegateInsert(et, op);
    }

    protected void prepareInsertOption(
            final InsertOption<FileCrawlingConfigCB> op) {
        if (op == null) {
            return;
        }
        assertInsertOptionStatus(op);
        if (op.hasSpecifiedInsertColumn()) {
            op.resolveInsertColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    @Override
    protected void doCreate(final Entity et,
            final InsertOption<? extends ConditionBean> op) {
        doInsert(downcast(et), downcast(op));
    }

    /**
     * Update the entity modified-only. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileCrawlingConfig.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     fileCrawlingConfigBhv.<span style="color: #DD4747">update</span>(fileCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileCrawlingConfig The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final FileCrawlingConfig fileCrawlingConfig) {
        doUpdate(fileCrawlingConfig, null);
    }

    protected void doUpdate(final FileCrawlingConfig et,
            final UpdateOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfig", et);
        prepareUpdateOption(op);
        helpUpdateInternally(et, op);
    }

    protected void prepareUpdateOption(
            final UpdateOption<FileCrawlingConfigCB> op) {
        if (op == null) {
            return;
        }
        assertUpdateOptionStatus(op);
        if (op.hasSelfSpecification()) {
            op.resolveSelfSpecification(createCBForVaryingUpdate());
        }
        if (op.hasSpecifiedUpdateColumn()) {
            op.resolveUpdateColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    protected FileCrawlingConfigCB createCBForVaryingUpdate() {
        final FileCrawlingConfigCB cb = newConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected FileCrawlingConfigCB createCBForSpecifiedUpdate() {
        final FileCrawlingConfigCB cb = newConditionBean();
        cb.xsetupForSpecifiedUpdate();
        return cb;
    }

    @Override
    protected void doModify(final Entity et,
            final UpdateOption<? extends ConditionBean> op) {
        doUpdate(downcast(et), downcast(op));
    }

    /**
     * Update the entity non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">updateNonstrict</span>(fileCrawlingConfig);
     * </pre>
     * @param fileCrawlingConfig The entity of update. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final FileCrawlingConfig fileCrawlingConfig) {
        doUpdateNonstrict(fileCrawlingConfig, null);
    }

    protected void doUpdateNonstrict(final FileCrawlingConfig et,
            final UpdateOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfig", et);
        prepareUpdateOption(op);
        helpUpdateNonstrictInternally(et, op);
    }

    @Override
    protected void doModifyNonstrict(final Entity et,
            final UpdateOption<? extends ConditionBean> op) {
        doUpdateNonstrict(downcast(et), downcast(op));
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, ExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param fileCrawlingConfig The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final FileCrawlingConfig fileCrawlingConfig) {
        doInsertOrUpdate(fileCrawlingConfig, null, null);
    }

    protected void doInsertOrUpdate(final FileCrawlingConfig et,
            final InsertOption<FileCrawlingConfigCB> iop,
            final UpdateOption<FileCrawlingConfigCB> uop) {
        assertObjectNotNull("fileCrawlingConfig", et);
        helpInsertOrUpdateInternally(et, iop, uop);
    }

    @Override
    protected void doCreateOrModify(final Entity et,
            final InsertOption<? extends ConditionBean> iop,
            final UpdateOption<? extends ConditionBean> uop) {
        doInsertOrUpdate(downcast(et), downcast(iop), downcast(uop));
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param fileCrawlingConfig The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(
            final FileCrawlingConfig fileCrawlingConfig) {
        doInsertOrUpdateNonstrict(fileCrawlingConfig, null, null);
    }

    protected void doInsertOrUpdateNonstrict(final FileCrawlingConfig et,
            final InsertOption<FileCrawlingConfigCB> iop,
            final UpdateOption<FileCrawlingConfigCB> uop) {
        assertObjectNotNull("fileCrawlingConfig", et);
        helpInsertOrUpdateNonstrictInternally(et, iop, uop);
    }

    @Override
    protected void doCreateOrModifyNonstrict(final Entity et,
            final InsertOption<? extends ConditionBean> iop,
            final UpdateOption<? extends ConditionBean> uop) {
        doInsertOrUpdateNonstrict(downcast(et), downcast(iop), downcast(uop));
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileCrawlingConfig.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     fileCrawlingConfigBhv.<span style="color: #DD4747">delete</span>(fileCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final FileCrawlingConfig fileCrawlingConfig) {
        doDelete(fileCrawlingConfig, null);
    }

    protected void doDelete(final FileCrawlingConfig et,
            final DeleteOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfig", et);
        prepareDeleteOption(op);
        helpDeleteInternally(et, op);
    }

    protected void prepareDeleteOption(
            final DeleteOption<FileCrawlingConfigCB> op) {
        if (op != null) {
            assertDeleteOptionStatus(op);
        }
    }

    @Override
    protected void doRemove(final Entity et,
            final DeleteOption<? extends ConditionBean> op) {
        doDelete(downcast(et), downcast(op));
    }

    /**
     * Delete the entity non-strictly. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">deleteNonstrict</span>(fileCrawlingConfig);
     * </pre>
     * @param fileCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final FileCrawlingConfig fileCrawlingConfig) {
        doDeleteNonstrict(fileCrawlingConfig, null);
    }

    protected void doDeleteNonstrict(final FileCrawlingConfig et,
            final DeleteOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfig", et);
        prepareDeleteOption(op);
        helpDeleteNonstrictInternally(et, op);
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">deleteNonstrictIgnoreDeleted</span>(fileCrawlingConfig);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param fileCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(
            final FileCrawlingConfig fileCrawlingConfig) {
        doDeleteNonstrictIgnoreDeleted(fileCrawlingConfig, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(final FileCrawlingConfig et,
            final DeleteOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfig", et);
        prepareDeleteOption(op);
        helpDeleteNonstrictIgnoreDeletedInternally(et, op);
    }

    @Override
    protected void doRemoveNonstrict(final Entity et,
            final DeleteOption<? extends ConditionBean> op) {
        doDeleteNonstrict(downcast(et), downcast(op));
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
     *     FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     *     fileCrawlingConfig.setFooName("foo");
     *     if (...) {
     *         fileCrawlingConfig.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     fileCrawlingConfigList.add(fileCrawlingConfig);
     * }
     * fileCrawlingConfigBhv.<span style="color: #DD4747">batchInsert</span>(fileCrawlingConfigList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchInsert(fileCrawlingConfigList, null);
    }

    protected int[] doBatchInsert(final List<FileCrawlingConfig> ls,
            final InsertOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfigList", ls);
        InsertOption<FileCrawlingConfigCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainInsertOption();
        }
        prepareBatchInsertOption(ls, rlop); // required
        return delegateBatchInsert(ls, rlop);
    }

    protected void prepareBatchInsertOption(final List<FileCrawlingConfig> ls,
            final InsertOption<FileCrawlingConfigCB> op) {
        op.xallowInsertColumnModifiedPropertiesFragmented();
        op.xacceptInsertColumnModifiedPropertiesIfNeeds(ls);
        prepareInsertOption(op);
    }

    @Override
    protected int[] doLumpCreate(final List<Entity> ls,
            final InsertOption<? extends ConditionBean> op) {
        return doBatchInsert(downcast(ls), downcast(op));
    }

    /**
     * Batch-update the entity list modified-only of same-set columns. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     *     fileCrawlingConfig.setFooName("foo");
     *     if (...) {
     *         fileCrawlingConfig.setFooPrice(123);
     *     } else {
     *         fileCrawlingConfig.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//fileCrawlingConfig.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     fileCrawlingConfigList.add(fileCrawlingConfig);
     * }
     * fileCrawlingConfigBhv.<span style="color: #DD4747">batchUpdate</span>(fileCrawlingConfigList);
     * </pre>
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchUpdate(fileCrawlingConfigList, null);
    }

    protected int[] doBatchUpdate(final List<FileCrawlingConfig> ls,
            final UpdateOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfigList", ls);
        UpdateOption<FileCrawlingConfigCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainUpdateOption();
        }
        prepareBatchUpdateOption(ls, rlop); // required
        return delegateBatchUpdate(ls, rlop);
    }

    protected void prepareBatchUpdateOption(final List<FileCrawlingConfig> ls,
            final UpdateOption<FileCrawlingConfigCB> op) {
        op.xacceptUpdateColumnModifiedPropertiesIfNeeds(ls);
        prepareUpdateOption(op);
    }

    @Override
    protected int[] doLumpModify(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> op) {
        return doBatchUpdate(downcast(ls), downcast(op));
    }

    /**
     * Batch-update the entity list specified-only. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">batchUpdate</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">batchUpdate</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final SpecifyQuery<FileCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdate(fileCrawlingConfigList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 140%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     *     fileCrawlingConfig.setFooName("foo");
     *     if (...) {
     *         fileCrawlingConfig.setFooPrice(123);
     *     } else {
     *         fileCrawlingConfig.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//fileCrawlingConfig.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     fileCrawlingConfigList.add(fileCrawlingConfig);
     * }
     * fileCrawlingConfigBhv.<span style="color: #DD4747">batchUpdate</span>(fileCrawlingConfigList);
     * </pre>
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchUpdateNonstrict(fileCrawlingConfigList, null);
    }

    protected int[] doBatchUpdateNonstrict(final List<FileCrawlingConfig> ls,
            final UpdateOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfigList", ls);
        UpdateOption<FileCrawlingConfigCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainUpdateOption();
        }
        prepareBatchUpdateOption(ls, rlop);
        return delegateBatchUpdateNonstrict(ls, rlop);
    }

    /**
     * Batch-update the entity list non-strictly specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final SpecifyQuery<FileCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(fileCrawlingConfigList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> op) {
        return doBatchUpdateNonstrict(downcast(ls), downcast(op));
    }

    /**
     * Batch-delete the entity list. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchDelete(fileCrawlingConfigList, null);
    }

    protected int[] doBatchDelete(final List<FileCrawlingConfig> ls,
            final DeleteOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfigList", ls);
        prepareDeleteOption(op);
        return delegateBatchDelete(ls, op);
    }

    @Override
    protected int[] doLumpRemove(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> op) {
        return doBatchDelete(downcast(ls), downcast(op));
    }

    /**
     * Batch-delete the entity list non-strictly. {NonExclusiveControl} <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchDeleteNonstrict(fileCrawlingConfigList, null);
    }

    protected int[] doBatchDeleteNonstrict(final List<FileCrawlingConfig> ls,
            final DeleteOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfigList", ls);
        prepareDeleteOption(op);
        return delegateBatchDeleteNonstrict(ls, op);
    }

    @Override
    protected int[] doLumpRemoveNonstrict(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> op) {
        return doBatchDeleteNonstrict(downcast(ls), downcast(op));
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;FileCrawlingConfig, FileCrawlingConfigCB&gt;() {
     *     public ConditionBean setup(fileCrawlingConfig entity, FileCrawlingConfigCB intoCB) {
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
     * @param setupper The setup-per of query-insert. (NotNull)
     * @return The inserted count.
     */
    public int queryInsert(
            final QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB> sp,
            final InsertOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("setupper", sp);
        prepareInsertOption(op);
        final FileCrawlingConfig et = newEntity();
        final FileCrawlingConfigCB cb = createCBForQueryInsert();
        return delegateQueryInsert(et, cb, sp.setup(et, cb), op);
    }

    protected FileCrawlingConfigCB createCBForQueryInsert() {
        final FileCrawlingConfigCB cb = newConditionBean();
        cb.xsetupForQueryInsert();
        return cb;
    }

    @Override
    protected int doRangeCreate(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            final InsertOption<? extends ConditionBean> op) {
        return doQueryInsert(downcast(setupper), downcast(op));
    }

    /**
     * Update the several entities by query non-strictly modified-only. (NonExclusiveControl)
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setPK...(value);</span>
     * fileCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * fileCrawlingConfigBhv.<span style="color: #DD4747">queryUpdate</span>(fileCrawlingConfig, cb);
     * </pre>
     * @param fileCrawlingConfig The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final FileCrawlingConfig fileCrawlingConfig,
            final FileCrawlingConfigCB cb) {
        return doQueryUpdate(fileCrawlingConfig, cb, null);
    }

    protected int doQueryUpdate(final FileCrawlingConfig et,
            final FileCrawlingConfigCB cb,
            final UpdateOption<FileCrawlingConfigCB> op) {
        assertObjectNotNull("fileCrawlingConfig", et);
        assertCBStateValid(cb);
        prepareUpdateOption(op);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(et,
                cb, op) : 0;
    }

    @Override
    protected int doRangeModify(final Entity et, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> op) {
        return doQueryUpdate(downcast(et), downcast(cb), downcast(op));
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * fileCrawlingConfigBhv.<span style="color: #DD4747">queryDelete</span>(fileCrawlingConfig, cb);
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final FileCrawlingConfigCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final FileCrawlingConfigCB cb,
            final DeleteOption<FileCrawlingConfigCB> op) {
        assertCBStateValid(cb);
        prepareDeleteOption(op);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                op) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> op) {
        return doQueryDelete(downcast(cb), downcast(op));
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
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileCrawlingConfig.setFoo...(value);
     * fileCrawlingConfig.setBar...(value);
     * InsertOption<FileCrawlingConfigCB> option = new InsertOption<FileCrawlingConfigCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * fileCrawlingConfigBhv.<span style="color: #DD4747">varyingInsert</span>(fileCrawlingConfig, option);
     * ... = fileCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileCrawlingConfig The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final FileCrawlingConfig fileCrawlingConfig,
            final InsertOption<FileCrawlingConfigCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(fileCrawlingConfig, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileCrawlingConfig.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;FileCrawlingConfigCB&gt; option = new UpdateOption&lt;FileCrawlingConfigCB&gt;();
     *     option.self(new SpecifyQuery&lt;FileCrawlingConfigCB&gt;() {
     *         public void specify(FileCrawlingConfigCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     fileCrawlingConfigBhv.<span style="color: #DD4747">varyingUpdate</span>(fileCrawlingConfig, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileCrawlingConfig The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final FileCrawlingConfig fileCrawlingConfig,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(fileCrawlingConfig, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * UpdateOption&lt;FileCrawlingConfigCB&gt; option = new UpdateOption&lt;FileCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void specify(FileCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">varyingUpdateNonstrict</span>(fileCrawlingConfig, option);
     * </pre>
     * @param fileCrawlingConfig The entity of update. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(
            final FileCrawlingConfig fileCrawlingConfig,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(fileCrawlingConfig, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param fileCrawlingConfig The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final FileCrawlingConfig fileCrawlingConfig,
            final InsertOption<FileCrawlingConfigCB> insertOption,
            final UpdateOption<FileCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdate(fileCrawlingConfig, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param fileCrawlingConfig The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final FileCrawlingConfig fileCrawlingConfig,
            final InsertOption<FileCrawlingConfigCB> insertOption,
            final UpdateOption<FileCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdateNonstrict(fileCrawlingConfig, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param fileCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final FileCrawlingConfig fileCrawlingConfig,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(fileCrawlingConfig, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param fileCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(
            final FileCrawlingConfig fileCrawlingConfig,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(fileCrawlingConfig, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final InsertOption<FileCrawlingConfigCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(fileCrawlingConfigList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(fileCrawlingConfigList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(fileCrawlingConfigList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(fileCrawlingConfigList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(fileCrawlingConfigList, option);
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
            final QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB> setupper,
            final InsertOption<FileCrawlingConfigCB> option) {
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
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setPK...(value);</span>
     * fileCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;FileCrawlingConfigCB&gt; option = new UpdateOption&lt;FileCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void specify(FileCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileCrawlingConfigBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(fileCrawlingConfig, cb, option);
     * </pre>
     * @param fileCrawlingConfig The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final FileCrawlingConfig fileCrawlingConfig,
            final FileCrawlingConfigCB cb,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(fileCrawlingConfig, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final FileCrawlingConfigCB cb,
            final DeleteOption<FileCrawlingConfigCB> option) {
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
    public OutsideSqlBasicExecutor<FileCrawlingConfigBhv> outsideSql() {
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
    //                                                                       Assist Helper
    //                                                                       =============
    protected Class<FileCrawlingConfig> typeOfSelectedEntity() {
        return FileCrawlingConfig.class;
    }

    protected FileCrawlingConfig downcast(final Entity et) {
        return helpEntityDowncastInternally(et, FileCrawlingConfig.class);
    }

    protected FileCrawlingConfigCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                FileCrawlingConfigCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<FileCrawlingConfig> downcast(final List<? extends Entity> ls) {
        return (List<FileCrawlingConfig>) ls;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<FileCrawlingConfigCB> downcast(
            final InsertOption<? extends ConditionBean> op) {
        return (InsertOption<FileCrawlingConfigCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<FileCrawlingConfigCB> downcast(
            final UpdateOption<? extends ConditionBean> op) {
        return (UpdateOption<FileCrawlingConfigCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<FileCrawlingConfigCB> downcast(
            final DeleteOption<? extends ConditionBean> op) {
        return (DeleteOption<FileCrawlingConfigCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> sp) {
        return (QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB>) sp;
    }
}
