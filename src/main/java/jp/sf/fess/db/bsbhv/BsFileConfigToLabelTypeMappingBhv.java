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

import jp.sf.fess.db.bsbhv.loader.LoaderOfFileConfigToLabelTypeMapping;
import jp.sf.fess.db.bsentity.dbmeta.FileConfigToLabelTypeMappingDbm;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.exbhv.FileConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.db.exentity.LabelType;

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
import org.seasar.dbflute.cbean.chelper.HpSLSExecutor;
import org.seasar.dbflute.cbean.chelper.HpSLSFunction;
import org.seasar.dbflute.dbmeta.DBMeta;
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
 * The behavior of FILE_CONFIG_TO_LABEL_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, FILE_CONFIG_ID, LABEL_TYPE_ID
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
 *     FILE_CRAWLING_CONFIG, LABEL_TYPE
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     fileCrawlingConfig, labelType
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsFileConfigToLabelTypeMappingBhv extends
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
        return "FILE_CONFIG_TO_LABEL_TYPE_MAPPING";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** {@inheritDoc} */
    @Override
    public DBMeta getDBMeta() {
        return FileConfigToLabelTypeMappingDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public FileConfigToLabelTypeMappingDbm getMyDBMeta() {
        return FileConfigToLabelTypeMappingDbm.getInstance();
    }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    @Override
    public FileConfigToLabelTypeMapping newEntity() {
        return new FileConfigToLabelTypeMapping();
    }

    /** {@inheritDoc} */
    @Override
    public FileConfigToLabelTypeMappingCB newConditionBean() {
        return new FileConfigToLabelTypeMappingCB();
    }

    /** @return The instance of new entity as my table type. (NotNull) */
    public FileConfigToLabelTypeMapping newMyEntity() {
        return new FileConfigToLabelTypeMapping();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public FileConfigToLabelTypeMappingCB newMyConditionBean() {
        return new FileConfigToLabelTypeMappingCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * int count = fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final FileConfigToLabelTypeMappingCB cb) {
        return facadeSelectCount(cb);
    }

    protected int facadeSelectCount(final FileConfigToLabelTypeMappingCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final FileConfigToLabelTypeMappingCB cb) { // called by selectCount(cb)
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final FileConfigToLabelTypeMappingCB cb) { // called by selectPage(cb)
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
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (fileConfigToLabelTypeMapping != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = fileConfigToLabelTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToLabelTypeMapping selectEntity(
            final FileConfigToLabelTypeMappingCB cb) {
        return facadeSelectEntity(cb);
    }

    protected FileConfigToLabelTypeMapping facadeSelectEntity(
            final FileConfigToLabelTypeMappingCB cb) {
        return doSelectEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ENTITY doSelectEntity(
            final FileConfigToLabelTypeMappingCB cb, final Class<ENTITY> tp) {
        return helpSelectEntityInternally(cb, tp);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> OptionalEntity<ENTITY> doSelectOptionalEntity(
            final FileConfigToLabelTypeMappingCB cb, final Class<ENTITY> tp) {
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
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = fileConfigToLabelTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToLabelTypeMapping selectEntityWithDeletedCheck(
            final FileConfigToLabelTypeMappingCB cb) {
        return facadeSelectEntityWithDeletedCheck(cb);
    }

    protected FileConfigToLabelTypeMapping facadeSelectEntityWithDeletedCheck(
            final FileConfigToLabelTypeMappingCB cb) {
        return doSelectEntityWithDeletedCheck(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ENTITY doSelectEntityWithDeletedCheck(
            final FileConfigToLabelTypeMappingCB cb, final Class<ENTITY> tp) {
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
    public FileConfigToLabelTypeMapping selectByPKValue(final Long id) {
        return facadeSelectByPKValue(id);
    }

    protected FileConfigToLabelTypeMapping facadeSelectByPKValue(final Long id) {
        return doSelectByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ENTITY doSelectByPK(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> OptionalEntity<ENTITY> doSelectOptionalByPK(
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
    public FileConfigToLabelTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKWithDeletedCheck(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ENTITY doSelectByPKWithDeletedCheck(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), tp);
    }

    protected FileConfigToLabelTypeMappingCB xprepareCBAsPK(final Long id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;FileConfigToLabelTypeMapping&gt; fileConfigToLabelTypeMappingList = fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping : fileConfigToLabelTypeMappingList) {
     *     ... = fileConfigToLabelTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<FileConfigToLabelTypeMapping> selectList(
            final FileConfigToLabelTypeMappingCB cb) {
        return facadeSelectList(cb);
    }

    protected ListResultBean<FileConfigToLabelTypeMapping> facadeSelectList(
            final FileConfigToLabelTypeMappingCB cb) {
        return doSelectList(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ListResultBean<ENTITY> doSelectList(
            final FileConfigToLabelTypeMappingCB cb, final Class<ENTITY> tp) {
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
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;FileConfigToLabelTypeMapping&gt; page = fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping : page) {
     *     ... = fileConfigToLabelTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<FileConfigToLabelTypeMapping> selectPage(
            final FileConfigToLabelTypeMappingCB cb) {
        return facadeSelectPage(cb);
    }

    protected PagingResultBean<FileConfigToLabelTypeMapping> facadeSelectPage(
            final FileConfigToLabelTypeMappingCB cb) {
        return doSelectPage(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> PagingResultBean<ENTITY> doSelectPage(
            final FileConfigToLabelTypeMappingCB cb, final Class<ENTITY> tp) {
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
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;FileConfigToLabelTypeMapping&gt;() {
     *     public void handle(FileConfigToLabelTypeMapping entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @param entityRowHandler The handler of entity row of FileConfigToLabelTypeMapping. (NotNull)
     */
    public void selectCursor(
            final FileConfigToLabelTypeMappingCB cb,
            final EntityRowHandler<FileConfigToLabelTypeMapping> entityRowHandler) {
        facadeSelectCursor(cb, entityRowHandler);
    }

    protected void facadeSelectCursor(
            final FileConfigToLabelTypeMappingCB cb,
            final EntityRowHandler<FileConfigToLabelTypeMapping> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> void doSelectCursor(
            final FileConfigToLabelTypeMappingCB cb,
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
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(FileConfigToLabelTypeMappingCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> HpSLSFunction<FileConfigToLabelTypeMappingCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return facadeScalarSelect(resultType);
    }

    protected <RESULT> HpSLSFunction<FileConfigToLabelTypeMappingCB, RESULT> facadeScalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newConditionBean());
    }

    protected <RESULT, CB extends FileConfigToLabelTypeMappingCB> HpSLSFunction<CB, RESULT> doScalarSelect(
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
     * @param fileConfigToLabelTypeMappingList The entity list of fileConfigToLabelTypeMapping. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final ReferrerLoaderHandler<LoaderOfFileConfigToLabelTypeMapping> handler) {
        xassLRArg(fileConfigToLabelTypeMappingList, handler);
        handler.handle(new LoaderOfFileConfigToLabelTypeMapping().ready(
                fileConfigToLabelTypeMappingList, _behaviorSelector));
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
     * @param fileConfigToLabelTypeMapping The entity of fileConfigToLabelTypeMapping. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final ReferrerLoaderHandler<LoaderOfFileConfigToLabelTypeMapping> handler) {
        xassLRArg(fileConfigToLabelTypeMapping, handler);
        handler.handle(new LoaderOfFileConfigToLabelTypeMapping().ready(
                xnewLRAryLs(fileConfigToLabelTypeMapping), _behaviorSelector));
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================
    /**
     * Pull out the list of foreign table 'FileCrawlingConfig'.
     * @param fileConfigToLabelTypeMappingList The list of fileConfigToLabelTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<FileCrawlingConfig> pulloutFileCrawlingConfig(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return helpPulloutInternally(fileConfigToLabelTypeMappingList,
                "fileCrawlingConfig");
    }

    /**
     * Pull out the list of foreign table 'LabelType'.
     * @param fileConfigToLabelTypeMappingList The list of fileConfigToLabelTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<LabelType> pulloutLabelType(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return helpPulloutInternally(fileConfigToLabelTypeMappingList,
                "labelType");
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param fileConfigToLabelTypeMappingList The list of fileConfigToLabelTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return helpExtractListInternally(fileConfigToLabelTypeMappingList, "id");
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileConfigToLabelTypeMapping.setFoo...(value);
     * fileConfigToLabelTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.set...;</span>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">insert</span>(fileConfigToLabelTypeMapping);
     * ... = fileConfigToLabelTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param fileConfigToLabelTypeMapping The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping) {
        doInsert(fileConfigToLabelTypeMapping, null);
    }

    protected void doInsert(final FileConfigToLabelTypeMapping et,
            final InsertOption<FileConfigToLabelTypeMappingCB> op) {
        assertObjectNotNull("fileConfigToLabelTypeMapping", et);
        prepareInsertOption(op);
        delegateInsert(et, op);
    }

    protected void prepareInsertOption(
            final InsertOption<FileConfigToLabelTypeMappingCB> op) {
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
     * Update the entity modified-only. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * fileConfigToLabelTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileConfigToLabelTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileConfigToLabelTypeMapping.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">update</span>(fileConfigToLabelTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of update. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping) {
        doUpdate(fileConfigToLabelTypeMapping, null);
    }

    protected void doUpdate(final FileConfigToLabelTypeMapping et,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        assertObjectNotNull("fileConfigToLabelTypeMapping", et);
        prepareUpdateOption(op);
        helpUpdateInternally(et, op);
    }

    protected void prepareUpdateOption(
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
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

    protected FileConfigToLabelTypeMappingCB createCBForVaryingUpdate() {
        final FileConfigToLabelTypeMappingCB cb = newConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected FileConfigToLabelTypeMappingCB createCBForSpecifiedUpdate() {
        final FileConfigToLabelTypeMappingCB cb = newConditionBean();
        cb.xsetupForSpecifiedUpdate();
        return cb;
    }

    @Override
    protected void doModify(final Entity et,
            final UpdateOption<? extends ConditionBean> op) {
        doUpdate(downcast(et), downcast(op));
    }

    @Override
    protected void doModifyNonstrict(final Entity et,
            final UpdateOption<? extends ConditionBean> op) {
        doModify(et, op);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param fileConfigToLabelTypeMapping The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping) {
        doInsertOrUpdate(fileConfigToLabelTypeMapping, null, null);
    }

    protected void doInsertOrUpdate(final FileConfigToLabelTypeMapping et,
            final InsertOption<FileConfigToLabelTypeMappingCB> iop,
            final UpdateOption<FileConfigToLabelTypeMappingCB> uop) {
        assertObjectNotNull("fileConfigToLabelTypeMapping", et);
        helpInsertOrUpdateInternally(et, iop, uop);
    }

    @Override
    protected void doCreateOrModify(final Entity et,
            final InsertOption<? extends ConditionBean> iop,
            final UpdateOption<? extends ConditionBean> uop) {
        doInsertOrUpdate(downcast(et), downcast(iop), downcast(uop));
    }

    @Override
    protected void doCreateOrModifyNonstrict(final Entity et,
            final InsertOption<? extends ConditionBean> iop,
            final UpdateOption<? extends ConditionBean> uop) {
        doCreateOrModify(et, iop, uop);
    }

    /**
     * Delete the entity. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * fileConfigToLabelTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileConfigToLabelTypeMapping.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">delete</span>(fileConfigToLabelTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping) {
        doDelete(fileConfigToLabelTypeMapping, null);
    }

    protected void doDelete(final FileConfigToLabelTypeMapping et,
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
        assertObjectNotNull("fileConfigToLabelTypeMapping", et);
        prepareDeleteOption(op);
        helpDeleteInternally(et, op);
    }

    protected void prepareDeleteOption(
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
        if (op != null) {
            assertDeleteOptionStatus(op);
        }
    }

    @Override
    protected void doRemove(final Entity et,
            final DeleteOption<? extends ConditionBean> op) {
        doDelete(downcast(et), downcast(op));
    }

    @Override
    protected void doRemoveNonstrict(final Entity et,
            final DeleteOption<? extends ConditionBean> op) {
        doRemove(et, op);
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
     *     FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     *     fileConfigToLabelTypeMapping.setFooName("foo");
     *     if (...) {
     *         fileConfigToLabelTypeMapping.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     fileConfigToLabelTypeMappingList.add(fileConfigToLabelTypeMapping);
     * }
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">batchInsert</span>(fileConfigToLabelTypeMappingList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return doBatchInsert(fileConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchInsert(final List<FileConfigToLabelTypeMapping> ls,
            final InsertOption<FileConfigToLabelTypeMappingCB> op) {
        assertObjectNotNull("fileConfigToLabelTypeMappingList", ls);
        InsertOption<FileConfigToLabelTypeMappingCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainInsertOption();
        }
        prepareBatchInsertOption(ls, rlop); // required
        return delegateBatchInsert(ls, rlop);
    }

    protected void prepareBatchInsertOption(
            final List<FileConfigToLabelTypeMapping> ls,
            final InsertOption<FileConfigToLabelTypeMappingCB> op) {
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
     * Batch-update the entity list modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     *     fileConfigToLabelTypeMapping.setFooName("foo");
     *     if (...) {
     *         fileConfigToLabelTypeMapping.setFooPrice(123);
     *     } else {
     *         fileConfigToLabelTypeMapping.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     fileConfigToLabelTypeMappingList.add(fileConfigToLabelTypeMapping);
     * }
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">batchUpdate</span>(fileConfigToLabelTypeMappingList);
     * </pre>
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return doBatchUpdate(fileConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchUpdate(final List<FileConfigToLabelTypeMapping> ls,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        assertObjectNotNull("fileConfigToLabelTypeMappingList", ls);
        UpdateOption<FileConfigToLabelTypeMappingCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainUpdateOption();
        }
        prepareBatchUpdateOption(ls, rlop); // required
        return delegateBatchUpdate(ls, rlop);
    }

    protected void prepareBatchUpdateOption(
            final List<FileConfigToLabelTypeMapping> ls,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        op.xacceptUpdateColumnModifiedPropertiesIfNeeds(ls);
        prepareUpdateOption(op);
    }

    @Override
    protected int[] doLumpModify(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> op) {
        return doBatchUpdate(downcast(ls), downcast(op));
    }

    /**
     * Batch-update the entity list specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">batchUpdate</span>(fileConfigToLabelTypeMappingList, new SpecifyQuery<FileConfigToLabelTypeMappingCB>() {
     *     public void specify(FileConfigToLabelTypeMappingCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">batchUpdate</span>(fileConfigToLabelTypeMappingList, new SpecifyQuery<FileConfigToLabelTypeMappingCB>() {
     *     public void specify(FileConfigToLabelTypeMappingCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final SpecifyQuery<FileConfigToLabelTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(fileConfigToLabelTypeMappingList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> op) {
        return doLumpModify(ls, op);
    }

    /**
     * Batch-delete the entity list. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return doBatchDelete(fileConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchDelete(final List<FileConfigToLabelTypeMapping> ls,
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
        assertObjectNotNull("fileConfigToLabelTypeMappingList", ls);
        prepareDeleteOption(op);
        return delegateBatchDelete(ls, op);
    }

    @Override
    protected int[] doLumpRemove(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> op) {
        return doBatchDelete(downcast(ls), downcast(op));
    }

    @Override
    protected int[] doLumpRemoveNonstrict(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> op) {
        return doLumpRemove(ls, op);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB&gt;() {
     *     public ConditionBean setup(fileConfigToLabelTypeMapping entity, FileConfigToLabelTypeMappingCB intoCB) {
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
            final QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB> sp,
            final InsertOption<FileConfigToLabelTypeMappingCB> op) {
        assertObjectNotNull("setupper", sp);
        prepareInsertOption(op);
        final FileConfigToLabelTypeMapping et = newEntity();
        final FileConfigToLabelTypeMappingCB cb = createCBForQueryInsert();
        return delegateQueryInsert(et, cb, sp.setup(et, cb), op);
    }

    protected FileConfigToLabelTypeMappingCB createCBForQueryInsert() {
        final FileConfigToLabelTypeMappingCB cb = newConditionBean();
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
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setPK...(value);</span>
     * fileConfigToLabelTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setVersionNo(value);</span>
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">queryUpdate</span>(fileConfigToLabelTypeMapping, cb);
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final FileConfigToLabelTypeMappingCB cb) {
        return doQueryUpdate(fileConfigToLabelTypeMapping, cb, null);
    }

    protected int doQueryUpdate(final FileConfigToLabelTypeMapping et,
            final FileConfigToLabelTypeMappingCB cb,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        assertObjectNotNull("fileConfigToLabelTypeMapping", et);
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
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">queryDelete</span>(fileConfigToLabelTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final FileConfigToLabelTypeMappingCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final FileConfigToLabelTypeMappingCB cb,
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
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
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileConfigToLabelTypeMapping.setFoo...(value);
     * fileConfigToLabelTypeMapping.setBar...(value);
     * InsertOption<FileConfigToLabelTypeMappingCB> option = new InsertOption<FileConfigToLabelTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">varyingInsert</span>(fileConfigToLabelTypeMapping, option);
     * ... = fileConfigToLabelTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final InsertOption<FileConfigToLabelTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(fileConfigToLabelTypeMapping, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * fileConfigToLabelTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileConfigToLabelTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * fileConfigToLabelTypeMapping.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;FileConfigToLabelTypeMappingCB&gt; option = new UpdateOption&lt;FileConfigToLabelTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *         public void specify(FileConfigToLabelTypeMappingCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">varyingUpdate</span>(fileConfigToLabelTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of update. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final UpdateOption<FileConfigToLabelTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(fileConfigToLabelTypeMapping, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param fileConfigToLabelTypeMapping The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final InsertOption<FileConfigToLabelTypeMappingCB> insertOption,
            final UpdateOption<FileConfigToLabelTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdate(fileConfigToLabelTypeMapping, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param fileConfigToLabelTypeMapping The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final DeleteOption<FileConfigToLabelTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(fileConfigToLabelTypeMapping, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final InsertOption<FileConfigToLabelTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(fileConfigToLabelTypeMappingList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final UpdateOption<FileConfigToLabelTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(fileConfigToLabelTypeMappingList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final DeleteOption<FileConfigToLabelTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(fileConfigToLabelTypeMappingList, option);
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
            final QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB> setupper,
            final InsertOption<FileConfigToLabelTypeMappingCB> option) {
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
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setPK...(value);</span>
     * fileConfigToLabelTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setVersionNo(value);</span>
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;FileConfigToLabelTypeMappingCB&gt; option = new UpdateOption&lt;FileConfigToLabelTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void specify(FileConfigToLabelTypeMappingCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(fileConfigToLabelTypeMapping, cb, option);
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final FileConfigToLabelTypeMappingCB cb,
            final UpdateOption<FileConfigToLabelTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(fileConfigToLabelTypeMapping, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final FileConfigToLabelTypeMappingCB cb,
            final DeleteOption<FileConfigToLabelTypeMappingCB> option) {
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
    public OutsideSqlBasicExecutor<FileConfigToLabelTypeMappingBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected Class<FileConfigToLabelTypeMapping> typeOfSelectedEntity() {
        return FileConfigToLabelTypeMapping.class;
    }

    protected FileConfigToLabelTypeMapping downcast(final Entity et) {
        return helpEntityDowncastInternally(et,
                FileConfigToLabelTypeMapping.class);
    }

    protected FileConfigToLabelTypeMappingCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                FileConfigToLabelTypeMappingCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<FileConfigToLabelTypeMapping> downcast(
            final List<? extends Entity> ls) {
        return (List<FileConfigToLabelTypeMapping>) ls;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<FileConfigToLabelTypeMappingCB> downcast(
            final InsertOption<? extends ConditionBean> op) {
        return (InsertOption<FileConfigToLabelTypeMappingCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<FileConfigToLabelTypeMappingCB> downcast(
            final UpdateOption<? extends ConditionBean> op) {
        return (UpdateOption<FileConfigToLabelTypeMappingCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<FileConfigToLabelTypeMappingCB> downcast(
            final DeleteOption<? extends ConditionBean> op) {
        return (DeleteOption<FileConfigToLabelTypeMappingCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> sp) {
        return (QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB>) sp;
    }
}
