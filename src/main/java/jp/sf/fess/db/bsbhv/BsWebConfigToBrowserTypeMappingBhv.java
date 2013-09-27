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

import jp.sf.fess.db.bsentity.dbmeta.WebConfigToBrowserTypeMappingDbm;
import jp.sf.fess.db.cbean.WebConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.exbhv.WebConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exentity.BrowserType;
import jp.sf.fess.db.exentity.WebConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

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
 * The behavior of WEB_CONFIG_TO_BROWSER_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, WEB_CONFIG_ID, BROWSER_TYPE_ID
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
 *     WEB_CRAWLING_CONFIG, BROWSER_TYPE
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     webCrawlingConfig, browserType
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsWebConfigToBrowserTypeMappingBhv extends
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
        return "WEB_CONFIG_TO_BROWSER_TYPE_MAPPING";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return WebConfigToBrowserTypeMappingDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public WebConfigToBrowserTypeMappingDbm getMyDBMeta() {
        return WebConfigToBrowserTypeMappingDbm.getInstance();
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
    public WebConfigToBrowserTypeMapping newMyEntity() {
        return new WebConfigToBrowserTypeMapping();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public WebConfigToBrowserTypeMappingCB newMyConditionBean() {
        return new WebConfigToBrowserTypeMappingCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * int count = webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final WebConfigToBrowserTypeMappingCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final WebConfigToBrowserTypeMappingCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final WebConfigToBrowserTypeMappingCB cb) { // called by selectPage(cb)
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
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (webConfigToBrowserTypeMapping != null) {
     *     ... = webConfigToBrowserTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public WebConfigToBrowserTypeMapping selectEntity(
            final WebConfigToBrowserTypeMappingCB cb) {
        return doSelectEntity(cb, WebConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> ENTITY doSelectEntity(
            final WebConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, WebConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebConfigToBrowserTypeMappingCB cb,
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
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = webConfigToBrowserTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public WebConfigToBrowserTypeMapping selectEntityWithDeletedCheck(
            final WebConfigToBrowserTypeMappingCB cb) {
        return doSelectEntityWithDeletedCheck(cb,
                WebConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> ENTITY doSelectEntityWithDeletedCheck(
            final WebConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, WebConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebConfigToBrowserTypeMappingCB cb,
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
    public WebConfigToBrowserTypeMapping selectByPKValue(final Long id) {
        return doSelectByPKValue(id, WebConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> ENTITY doSelectByPKValue(
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
    public WebConfigToBrowserTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKValueWithDeletedCheck(id,
                WebConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private WebConfigToBrowserTypeMappingCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final WebConfigToBrowserTypeMappingCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;WebConfigToBrowserTypeMapping&gt; webConfigToBrowserTypeMappingList = webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping : webConfigToBrowserTypeMappingList) {
     *     ... = webConfigToBrowserTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<WebConfigToBrowserTypeMapping> selectList(
            final WebConfigToBrowserTypeMappingCB cb) {
        return doSelectList(cb, WebConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> ListResultBean<ENTITY> doSelectList(
            final WebConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
                cb,
                entityType,
                new InternalSelectListCallback<ENTITY, WebConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebConfigToBrowserTypeMappingCB cb,
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
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;WebConfigToBrowserTypeMapping&gt; page = webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping : page) {
     *     ... = webConfigToBrowserTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<WebConfigToBrowserTypeMapping> selectPage(
            final WebConfigToBrowserTypeMappingCB cb) {
        return doSelectPage(cb, WebConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> PagingResultBean<ENTITY> doSelectPage(
            final WebConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
                cb,
                entityType,
                new InternalSelectPageCallback<ENTITY, WebConfigToBrowserTypeMappingCB>() {
                    @Override
                    public int callbackSelectCount(
                            final WebConfigToBrowserTypeMappingCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebConfigToBrowserTypeMappingCB cb,
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
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;WebConfigToBrowserTypeMapping&gt;() {
     *     public void handle(WebConfigToBrowserTypeMapping entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @param entityRowHandler The handler of entity row of WebConfigToBrowserTypeMapping. (NotNull)
     */
    public void selectCursor(
            final WebConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<WebConfigToBrowserTypeMapping> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler,
                WebConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> void doSelectCursor(
            final WebConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<WebConfigToBrowserTypeMapping>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, WebConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final WebConfigToBrowserTypeMappingCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebConfigToBrowserTypeMappingCB cb,
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
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(WebConfigToBrowserTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<WebConfigToBrowserTypeMappingCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends WebConfigToBrowserTypeMappingCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * Pull out the list of foreign table 'WebCrawlingConfig'.
     * @param webConfigToBrowserTypeMappingList The list of webConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<WebCrawlingConfig> pulloutWebCrawlingConfig(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList) {
        return helpPulloutInternally(
                webConfigToBrowserTypeMappingList,
                new InternalPulloutCallback<WebConfigToBrowserTypeMapping, WebCrawlingConfig>() {
                    @Override
                    public WebCrawlingConfig getFr(
                            final WebConfigToBrowserTypeMapping e) {
                        return e.getWebCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
                            final List<WebConfigToBrowserTypeMapping> ls) {
                        e.setWebConfigToBrowserTypeMappingList(ls);
                    }
                });
    }

    /**
     * Pull out the list of foreign table 'BrowserType'.
     * @param webConfigToBrowserTypeMappingList The list of webConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<BrowserType> pulloutBrowserType(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList) {
        return helpPulloutInternally(
                webConfigToBrowserTypeMappingList,
                new InternalPulloutCallback<WebConfigToBrowserTypeMapping, BrowserType>() {
                    @Override
                    public BrowserType getFr(
                            final WebConfigToBrowserTypeMapping e) {
                        return e.getBrowserType();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final BrowserType e,
                            final List<WebConfigToBrowserTypeMapping> ls) {
                        e.setWebConfigToBrowserTypeMappingList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param webConfigToBrowserTypeMappingList The list of webConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList) {
        return helpExtractListInternally(
                webConfigToBrowserTypeMappingList,
                new InternalExtractCallback<WebConfigToBrowserTypeMapping, Long>() {
                    @Override
                    public Long getCV(final WebConfigToBrowserTypeMapping e) {
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
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = new WebConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * webConfigToBrowserTypeMapping.setFoo...(value);
     * webConfigToBrowserTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.set...;</span>
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">insert</span>(webConfigToBrowserTypeMapping);
     * ... = webConfigToBrowserTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param webConfigToBrowserTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping) {
        doInsert(webConfigToBrowserTypeMapping, null);
    }

    protected void doInsert(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final InsertOption<WebConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("webConfigToBrowserTypeMapping",
                webConfigToBrowserTypeMapping);
        prepareInsertOption(option);
        delegateInsert(webConfigToBrowserTypeMapping, option);
    }

    protected void prepareInsertOption(
            final InsertOption<WebConfigToBrowserTypeMappingCB> option) {
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
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = new WebConfigToBrowserTypeMapping();
     * webConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webConfigToBrowserTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">update</span>(webConfigToBrowserTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param webConfigToBrowserTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping) {
        doUpdate(webConfigToBrowserTypeMapping, null);
    }

    protected void doUpdate(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("webConfigToBrowserTypeMapping",
                webConfigToBrowserTypeMapping);
        prepareUpdateOption(option);
        helpUpdateInternally(webConfigToBrowserTypeMapping,
                new InternalUpdateCallback<WebConfigToBrowserTypeMapping>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final WebConfigToBrowserTypeMapping entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<WebConfigToBrowserTypeMappingCB> option) {
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

    protected WebConfigToBrowserTypeMappingCB createCBForVaryingUpdate() {
        final WebConfigToBrowserTypeMappingCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected WebConfigToBrowserTypeMappingCB createCBForSpecifiedUpdate() {
        final WebConfigToBrowserTypeMappingCB cb = newMyConditionBean();
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
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param webConfigToBrowserTypeMapping The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping) {
        doInesrtOrUpdate(webConfigToBrowserTypeMapping, null, null);
    }

    protected void doInesrtOrUpdate(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final InsertOption<WebConfigToBrowserTypeMappingCB> insertOption,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> updateOption) {
        helpInsertOrUpdateInternally(
                webConfigToBrowserTypeMapping,
                new InternalInsertOrUpdateCallback<WebConfigToBrowserTypeMapping, WebConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void callbackInsert(
                            final WebConfigToBrowserTypeMapping entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(
                            final WebConfigToBrowserTypeMapping entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public WebConfigToBrowserTypeMappingCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(
                            final WebConfigToBrowserTypeMappingCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<WebConfigToBrowserTypeMappingCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<WebConfigToBrowserTypeMappingCB>()
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
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = new WebConfigToBrowserTypeMapping();
     * webConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">delete</span>(webConfigToBrowserTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param webConfigToBrowserTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping) {
        doDelete(webConfigToBrowserTypeMapping, null);
    }

    protected void doDelete(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("webConfigToBrowserTypeMapping",
                webConfigToBrowserTypeMapping);
        prepareDeleteOption(option);
        helpDeleteInternally(webConfigToBrowserTypeMapping,
                new InternalDeleteCallback<WebConfigToBrowserTypeMapping>() {
                    @Override
                    public int callbackDelegateDelete(
                            final WebConfigToBrowserTypeMapping entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<WebConfigToBrowserTypeMappingCB> option) {
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
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <p><span style="color: #FD4747; font-size: 120%">Attention, all columns are insert target. (so default constraints are not available)</span></p>
     * And if the table has an identity, entities after the process don't have incremented values.
     * When you use the (normal) insert(), an entity after the process has an incremented value.
     * @param webConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList) {
        return doBatchInsert(webConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchInsert(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList,
            final InsertOption<WebConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("webConfigToBrowserTypeMappingList",
                webConfigToBrowserTypeMappingList);
        prepareInsertOption(option);
        return delegateBatchInsert(webConfigToBrowserTypeMappingList, option);
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
     * <span style="color: #FD4747; font-size: 140%">Attention, all columns are update target. {NOT modified only}</span> <br />
     * So you should the other batchUpdate() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(webConfigToBrowserTypeMappingList, new SpecifyQuery<WebConfigToBrowserTypeMappingCB>() {
     *     public void specify(WebConfigToBrowserTypeMappingCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param webConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList) {
        return doBatchUpdate(webConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchUpdate(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("webConfigToBrowserTypeMappingList",
                webConfigToBrowserTypeMappingList);
        prepareBatchUpdateOption(webConfigToBrowserTypeMappingList, option);
        return delegateBatchUpdate(webConfigToBrowserTypeMappingList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(webConfigToBrowserTypeMappingList);
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
     * Batch-update the entity list. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span> 
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(webConfigToBrowserTypeMappingList, new SpecifyQuery<WebConfigToBrowserTypeMappingCB>() {
     *     public void specify(WebConfigToBrowserTypeMappingCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(webConfigToBrowserTypeMappingList, new SpecifyQuery<WebConfigToBrowserTypeMappingCB>() {
     *     public void specify(WebConfigToBrowserTypeMappingCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param webConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList,
            final SpecifyQuery<WebConfigToBrowserTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(webConfigToBrowserTypeMappingList,
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
     * @param webConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList) {
        return doBatchDelete(webConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchDelete(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("webConfigToBrowserTypeMappingList",
                webConfigToBrowserTypeMappingList);
        prepareDeleteOption(option);
        return delegateBatchDelete(webConfigToBrowserTypeMappingList, option);
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
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;WebConfigToBrowserTypeMapping, WebConfigToBrowserTypeMappingCB&gt;() {
     *     public ConditionBean setup(webConfigToBrowserTypeMapping entity, WebConfigToBrowserTypeMappingCB intoCB) {
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
            final QueryInsertSetupper<WebConfigToBrowserTypeMapping, WebConfigToBrowserTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<WebConfigToBrowserTypeMapping, WebConfigToBrowserTypeMappingCB> setupper,
            final InsertOption<WebConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final WebConfigToBrowserTypeMapping entity = new WebConfigToBrowserTypeMapping();
        final WebConfigToBrowserTypeMappingCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected WebConfigToBrowserTypeMappingCB createCBForQueryInsert() {
        final WebConfigToBrowserTypeMappingCB cb = newMyConditionBean();
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
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = new WebConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.setPK...(value);</span>
     * webConfigToBrowserTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.setVersionNo(value);</span>
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryUpdate</span>(webConfigToBrowserTypeMapping, cb);
     * </pre>
     * @param webConfigToBrowserTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final WebConfigToBrowserTypeMappingCB cb) {
        return doQueryUpdate(webConfigToBrowserTypeMapping, cb, null);
    }

    protected int doQueryUpdate(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final WebConfigToBrowserTypeMappingCB cb,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("webConfigToBrowserTypeMapping",
                webConfigToBrowserTypeMapping);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                webConfigToBrowserTypeMapping, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity),
                    (WebConfigToBrowserTypeMappingCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (WebConfigToBrowserTypeMappingCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryDelete</span>(webConfigToBrowserTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final WebConfigToBrowserTypeMappingCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final WebConfigToBrowserTypeMappingCB cb,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((WebConfigToBrowserTypeMappingCB) cb);
        } else {
            return varyingQueryDelete((WebConfigToBrowserTypeMappingCB) cb,
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
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = new WebConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * webConfigToBrowserTypeMapping.setFoo...(value);
     * webConfigToBrowserTypeMapping.setBar...(value);
     * InsertOption<WebConfigToBrowserTypeMappingCB> option = new InsertOption<WebConfigToBrowserTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingInsert</span>(webConfigToBrowserTypeMapping, option);
     * ... = webConfigToBrowserTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param webConfigToBrowserTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final InsertOption<WebConfigToBrowserTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(webConfigToBrowserTypeMapping, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = new WebConfigToBrowserTypeMapping();
     * webConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webConfigToBrowserTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;WebConfigToBrowserTypeMappingCB&gt; option = new UpdateOption&lt;WebConfigToBrowserTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;WebConfigToBrowserTypeMappingCB&gt;() {
     *         public void specify(WebConfigToBrowserTypeMappingCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingUpdate</span>(webConfigToBrowserTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param webConfigToBrowserTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(webConfigToBrowserTypeMapping, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param webConfigToBrowserTypeMapping The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final InsertOption<WebConfigToBrowserTypeMappingCB> insertOption,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(webConfigToBrowserTypeMapping, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param webConfigToBrowserTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(webConfigToBrowserTypeMapping, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param webConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList,
            final InsertOption<WebConfigToBrowserTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(webConfigToBrowserTypeMappingList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param webConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(webConfigToBrowserTypeMappingList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param webConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(webConfigToBrowserTypeMappingList, option);
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
            final QueryInsertSetupper<WebConfigToBrowserTypeMapping, WebConfigToBrowserTypeMappingCB> setupper,
            final InsertOption<WebConfigToBrowserTypeMappingCB> option) {
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
     * WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping = new WebConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.setPK...(value);</span>
     * webConfigToBrowserTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webConfigToBrowserTypeMapping.setVersionNo(value);</span>
     * WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;WebConfigToBrowserTypeMappingCB&gt; option = new UpdateOption&lt;WebConfigToBrowserTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;WebConfigToBrowserTypeMappingCB&gt;() {
     *     public void specify(WebConfigToBrowserTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * webConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(webConfigToBrowserTypeMapping, cb, option);
     * </pre>
     * @param webConfigToBrowserTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(
            final WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping,
            final WebConfigToBrowserTypeMappingCB cb,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(webConfigToBrowserTypeMapping, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of WebConfigToBrowserTypeMapping. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final WebConfigToBrowserTypeMappingCB cb,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> option) {
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
    public OutsideSqlBasicExecutor<WebConfigToBrowserTypeMappingBhv> outsideSql() {
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
            final WebConfigToBrowserTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(
            final WebConfigToBrowserTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> void delegateSelectCursor(
            final WebConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<ENTITY> erh, final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends WebConfigToBrowserTypeMapping> List<ENTITY> delegateSelectList(
            final WebConfigToBrowserTypeMappingCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final WebConfigToBrowserTypeMapping e,
            final InsertOption<WebConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final WebConfigToBrowserTypeMapping e,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(
            final WebConfigToBrowserTypeMapping e,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final WebConfigToBrowserTypeMapping e,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(
            final WebConfigToBrowserTypeMapping e,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(
            final List<WebConfigToBrowserTypeMapping> ls,
            final InsertOption<WebConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(
            final List<WebConfigToBrowserTypeMapping> ls,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<WebConfigToBrowserTypeMapping> ls,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(
            final List<WebConfigToBrowserTypeMapping> ls,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<WebConfigToBrowserTypeMapping> ls,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final WebConfigToBrowserTypeMapping e,
            final WebConfigToBrowserTypeMappingCB inCB,
            final ConditionBean resCB,
            final InsertOption<WebConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final WebConfigToBrowserTypeMapping e,
            final WebConfigToBrowserTypeMappingCB cb,
            final UpdateOption<WebConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final WebConfigToBrowserTypeMappingCB cb,
            final DeleteOption<WebConfigToBrowserTypeMappingCB> op) {
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
    protected WebConfigToBrowserTypeMapping downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity,
                WebConfigToBrowserTypeMapping.class);
    }

    protected WebConfigToBrowserTypeMappingCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                WebConfigToBrowserTypeMappingCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<WebConfigToBrowserTypeMapping> downcast(
            final List<? extends Entity> entityList) {
        return (List<WebConfigToBrowserTypeMapping>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<WebConfigToBrowserTypeMappingCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<WebConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<WebConfigToBrowserTypeMappingCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<WebConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<WebConfigToBrowserTypeMappingCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<WebConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<WebConfigToBrowserTypeMapping, WebConfigToBrowserTypeMappingCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<WebConfigToBrowserTypeMapping, WebConfigToBrowserTypeMappingCB>) option;
    }
}
