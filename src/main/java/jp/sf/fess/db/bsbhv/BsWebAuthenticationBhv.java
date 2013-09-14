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

import jp.sf.fess.db.bsentity.dbmeta.WebAuthenticationDbm;
import jp.sf.fess.db.cbean.WebAuthenticationCB;
import jp.sf.fess.db.exbhv.WebAuthenticationBhv;
import jp.sf.fess.db.exentity.WebAuthentication;
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
 * The behavior of WEB_AUTHENTICATION as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, HOSTNAME, PORT, AUTH_REALM, PROTOCOL_SCHEME, USERNAME, PASSWORD, PARAMETERS, WEB_CRAWLING_CONFIG_ID, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     WEB_CRAWLING_CONFIG
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     webCrawlingConfig
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsWebAuthenticationBhv extends AbstractBehaviorWritable {

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
        return "WEB_AUTHENTICATION";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return WebAuthenticationDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public WebAuthenticationDbm getMyDBMeta() {
        return WebAuthenticationDbm.getInstance();
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
    public WebAuthentication newMyEntity() {
        return new WebAuthentication();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public WebAuthenticationCB newMyConditionBean() {
        return new WebAuthenticationCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * int count = webAuthenticationBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final WebAuthenticationCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final WebAuthenticationCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final WebAuthenticationCB cb) { // called by selectPage(cb)
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
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * WebAuthentication webAuthentication = webAuthenticationBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (webAuthentication != null) {
     *     ... = webAuthentication.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public WebAuthentication selectEntity(final WebAuthenticationCB cb) {
        return doSelectEntity(cb, WebAuthentication.class);
    }

    protected <ENTITY extends WebAuthentication> ENTITY doSelectEntity(
            final WebAuthenticationCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, WebAuthenticationCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebAuthenticationCB cb,
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
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * WebAuthentication webAuthentication = webAuthenticationBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = webAuthentication.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public WebAuthentication selectEntityWithDeletedCheck(
            final WebAuthenticationCB cb) {
        return doSelectEntityWithDeletedCheck(cb, WebAuthentication.class);
    }

    protected <ENTITY extends WebAuthentication> ENTITY doSelectEntityWithDeletedCheck(
            final WebAuthenticationCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, WebAuthenticationCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebAuthenticationCB cb,
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
    public WebAuthentication selectByPKValue(final Long id) {
        return doSelectByPKValue(id, WebAuthentication.class);
    }

    protected <ENTITY extends WebAuthentication> ENTITY doSelectByPKValue(
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
    public WebAuthentication selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, WebAuthentication.class);
    }

    protected <ENTITY extends WebAuthentication> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private WebAuthenticationCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final WebAuthenticationCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;WebAuthentication&gt; webAuthenticationList = webAuthenticationBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (WebAuthentication webAuthentication : webAuthenticationList) {
     *     ... = webAuthentication.get...();
     * }
     * </pre>
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<WebAuthentication> selectList(
            final WebAuthenticationCB cb) {
        return doSelectList(cb, WebAuthentication.class);
    }

    protected <ENTITY extends WebAuthentication> ListResultBean<ENTITY> doSelectList(
            final WebAuthenticationCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, WebAuthenticationCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebAuthenticationCB cb,
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
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;WebAuthentication&gt; page = webAuthenticationBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (WebAuthentication webAuthentication : page) {
     *     ... = webAuthentication.get...();
     * }
     * </pre>
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<WebAuthentication> selectPage(
            final WebAuthenticationCB cb) {
        return doSelectPage(cb, WebAuthentication.class);
    }

    protected <ENTITY extends WebAuthentication> PagingResultBean<ENTITY> doSelectPage(
            final WebAuthenticationCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, WebAuthenticationCB>() {
                    @Override
                    public int callbackSelectCount(final WebAuthenticationCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebAuthenticationCB cb,
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
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * webAuthenticationBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;WebAuthentication&gt;() {
     *     public void handle(WebAuthentication entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @param entityRowHandler The handler of entity row of WebAuthentication. (NotNull)
     */
    public void selectCursor(final WebAuthenticationCB cb,
            final EntityRowHandler<WebAuthentication> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, WebAuthentication.class);
    }

    protected <ENTITY extends WebAuthentication> void doSelectCursor(
            final WebAuthenticationCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<WebAuthentication>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, WebAuthenticationCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final WebAuthenticationCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebAuthenticationCB cb,
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
     * webAuthenticationBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(WebAuthenticationCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<WebAuthenticationCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends WebAuthenticationCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param webAuthenticationList The list of webAuthentication. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<WebCrawlingConfig> pulloutWebCrawlingConfig(
            final List<WebAuthentication> webAuthenticationList) {
        return helpPulloutInternally(
                webAuthenticationList,
                new InternalPulloutCallback<WebAuthentication, WebCrawlingConfig>() {
                    @Override
                    public WebCrawlingConfig getFr(final WebAuthentication e) {
                        return e.getWebCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
                            final List<WebAuthentication> ls) {
                        e.setWebAuthenticationList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param webAuthenticationList The list of webAuthentication. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<WebAuthentication> webAuthenticationList) {
        return helpExtractListInternally(webAuthenticationList,
                new InternalExtractCallback<WebAuthentication, Long>() {
                    @Override
                    public Long getCV(final WebAuthentication e) {
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
     * WebAuthentication webAuthentication = new WebAuthentication();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * webAuthentication.setFoo...(value);
     * webAuthentication.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webAuthentication.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webAuthentication.set...;</span>
     * webAuthenticationBhv.<span style="color: #FD4747">insert</span>(webAuthentication);
     * ... = webAuthentication.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param webAuthentication The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final WebAuthentication webAuthentication) {
        doInsert(webAuthentication, null);
    }

    protected void doInsert(final WebAuthentication webAuthentication,
            final InsertOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthentication", webAuthentication);
        prepareInsertOption(option);
        delegateInsert(webAuthentication, option);
    }

    protected void prepareInsertOption(
            final InsertOption<WebAuthenticationCB> option) {
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
     * WebAuthentication webAuthentication = new WebAuthentication();
     * webAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webAuthentication.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webAuthentication.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webAuthentication.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webAuthentication.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     webAuthenticationBhv.<span style="color: #FD4747">update</span>(webAuthentication);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param webAuthentication The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final WebAuthentication webAuthentication) {
        doUpdate(webAuthentication, null);
    }

    protected void doUpdate(final WebAuthentication webAuthentication,
            final UpdateOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthentication", webAuthentication);
        prepareUpdateOption(option);
        helpUpdateInternally(webAuthentication,
                new InternalUpdateCallback<WebAuthentication>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final WebAuthentication entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<WebAuthenticationCB> option) {
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

    protected WebAuthenticationCB createCBForVaryingUpdate() {
        final WebAuthenticationCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected WebAuthenticationCB createCBForSpecifiedUpdate() {
        final WebAuthenticationCB cb = newMyConditionBean();
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
     * WebAuthentication webAuthentication = new WebAuthentication();
     * webAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webAuthentication.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webAuthentication.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webAuthentication.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webAuthentication.setVersionNo(value);</span>
     * webAuthenticationBhv.<span style="color: #FD4747">updateNonstrict</span>(webAuthentication);
     * </pre>
     * @param webAuthentication The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final WebAuthentication webAuthentication) {
        doUpdateNonstrict(webAuthentication, null);
    }

    protected void doUpdateNonstrict(final WebAuthentication webAuthentication,
            final UpdateOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthentication", webAuthentication);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(webAuthentication,
                new InternalUpdateNonstrictCallback<WebAuthentication>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final WebAuthentication entity) {
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
     * @param webAuthentication The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final WebAuthentication webAuthentication) {
        doInesrtOrUpdate(webAuthentication, null, null);
    }

    protected void doInesrtOrUpdate(final WebAuthentication webAuthentication,
            final InsertOption<WebAuthenticationCB> insertOption,
            final UpdateOption<WebAuthenticationCB> updateOption) {
        helpInsertOrUpdateInternally(
                webAuthentication,
                new InternalInsertOrUpdateCallback<WebAuthentication, WebAuthenticationCB>() {
                    @Override
                    public void callbackInsert(final WebAuthentication entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final WebAuthentication entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public WebAuthenticationCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final WebAuthenticationCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<WebAuthenticationCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<WebAuthenticationCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl)
     * @param webAuthentication The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(
            final WebAuthentication webAuthentication) {
        doInesrtOrUpdateNonstrict(webAuthentication, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(
            final WebAuthentication webAuthentication,
            final InsertOption<WebAuthenticationCB> insertOption,
            final UpdateOption<WebAuthenticationCB> updateOption) {
        helpInsertOrUpdateInternally(
                webAuthentication,
                new InternalInsertOrUpdateNonstrictCallback<WebAuthentication>() {
                    @Override
                    public void callbackInsert(final WebAuthentication entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(
                            final WebAuthentication entity) {
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
            insertOption = insertOption == null ? new InsertOption<WebAuthenticationCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<WebAuthenticationCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * WebAuthentication webAuthentication = new WebAuthentication();
     * webAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webAuthentication.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     webAuthenticationBhv.<span style="color: #FD4747">delete</span>(webAuthentication);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param webAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final WebAuthentication webAuthentication) {
        doDelete(webAuthentication, null);
    }

    protected void doDelete(final WebAuthentication webAuthentication,
            final DeleteOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthentication", webAuthentication);
        prepareDeleteOption(option);
        helpDeleteInternally(webAuthentication,
                new InternalDeleteCallback<WebAuthentication>() {
                    @Override
                    public int callbackDelegateDelete(
                            final WebAuthentication entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<WebAuthenticationCB> option) {
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
     * WebAuthentication webAuthentication = new WebAuthentication();
     * webAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webAuthentication.setVersionNo(value);</span>
     * webAuthenticationBhv.<span style="color: #FD4747">deleteNonstrict</span>(webAuthentication);
     * </pre>
     * @param webAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final WebAuthentication webAuthentication) {
        doDeleteNonstrict(webAuthentication, null);
    }

    protected void doDeleteNonstrict(final WebAuthentication webAuthentication,
            final DeleteOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthentication", webAuthentication);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(webAuthentication,
                new InternalDeleteNonstrictCallback<WebAuthentication>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final WebAuthentication entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * WebAuthentication webAuthentication = new WebAuthentication();
     * webAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webAuthentication.setVersionNo(value);</span>
     * webAuthenticationBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(webAuthentication);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param webAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(
            final WebAuthentication webAuthentication) {
        doDeleteNonstrictIgnoreDeleted(webAuthentication, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(
            final WebAuthentication webAuthentication,
            final DeleteOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthentication", webAuthentication);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(
                webAuthentication,
                new InternalDeleteNonstrictIgnoreDeletedCallback<WebAuthentication>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final WebAuthentication entity) {
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
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<WebAuthentication> webAuthenticationList) {
        return doBatchInsert(webAuthenticationList, null);
    }

    protected int[] doBatchInsert(
            final List<WebAuthentication> webAuthenticationList,
            final InsertOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthenticationList", webAuthenticationList);
        prepareInsertOption(option);
        return delegateBatchInsert(webAuthenticationList, option);
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
     * webAuthenticationBhv.<span style="color: #FD4747">batchUpdate</span>(webAuthenticationList, new SpecifyQuery<WebAuthenticationCB>() {
     *     public void specify(WebAuthenticationCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<WebAuthentication> webAuthenticationList) {
        return doBatchUpdate(webAuthenticationList, null);
    }

    protected int[] doBatchUpdate(
            final List<WebAuthentication> webAuthenticationList,
            final UpdateOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthenticationList", webAuthenticationList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(webAuthenticationList, option);
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
     * webAuthenticationBhv.<span style="color: #FD4747">batchUpdate</span>(webAuthenticationList, new SpecifyQuery<WebAuthenticationCB>() {
     *     public void specify(WebAuthenticationCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<WebAuthentication> webAuthenticationList,
            final SpecifyQuery<WebAuthenticationCB> updateColumnSpec) {
        return doBatchUpdate(webAuthenticationList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdate() method, which you can specify update columns like this:
     * <pre>
     * webAuthenticationBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(webAuthenticationList, new SpecifyQuery<WebAuthenticationCB>() {
     *     public void specify(WebAuthenticationCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<WebAuthentication> webAuthenticationList) {
        return doBatchUpdateNonstrict(webAuthenticationList, null);
    }

    protected int[] doBatchUpdateNonstrict(
            final List<WebAuthentication> webAuthenticationList,
            final UpdateOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthenticationList", webAuthenticationList);
        prepareUpdateOption(option);
        return delegateBatchUpdateNonstrict(webAuthenticationList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistick lock column because they are specified implicitly.
     * <pre>
     * webAuthenticationBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(webAuthenticationList, new SpecifyQuery<WebAuthenticationCB>() {
     *     public void specify(WebAuthenticationCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<WebAuthentication> webAuthenticationList,
            final SpecifyQuery<WebAuthenticationCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(webAuthenticationList,
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
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<WebAuthentication> webAuthenticationList) {
        return doBatchDelete(webAuthenticationList, null);
    }

    protected int[] doBatchDelete(
            final List<WebAuthentication> webAuthenticationList,
            final DeleteOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthenticationList", webAuthenticationList);
        prepareDeleteOption(option);
        return delegateBatchDelete(webAuthenticationList, option);
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
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<WebAuthentication> webAuthenticationList) {
        return doBatchDeleteNonstrict(webAuthenticationList, null);
    }

    protected int[] doBatchDeleteNonstrict(
            final List<WebAuthentication> webAuthenticationList,
            final DeleteOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthenticationList", webAuthenticationList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(webAuthenticationList, option);
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
     * webAuthenticationBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;WebAuthentication, WebAuthenticationCB&gt;() {
     *     public ConditionBean setup(webAuthentication entity, WebAuthenticationCB intoCB) {
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
            final QueryInsertSetupper<WebAuthentication, WebAuthenticationCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<WebAuthentication, WebAuthenticationCB> setupper,
            final InsertOption<WebAuthenticationCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final WebAuthentication entity = new WebAuthentication();
        final WebAuthenticationCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected WebAuthenticationCB createCBForQueryInsert() {
        final WebAuthenticationCB cb = newMyConditionBean();
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
     * WebAuthentication webAuthentication = new WebAuthentication();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//webAuthentication.setPK...(value);</span>
     * webAuthentication.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webAuthentication.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webAuthentication.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webAuthentication.setVersionNo(value);</span>
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * webAuthenticationBhv.<span style="color: #FD4747">queryUpdate</span>(webAuthentication, cb);
     * </pre>
     * @param webAuthentication The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final WebAuthentication webAuthentication,
            final WebAuthenticationCB cb) {
        return doQueryUpdate(webAuthentication, cb, null);
    }

    protected int doQueryUpdate(final WebAuthentication webAuthentication,
            final WebAuthenticationCB cb,
            final UpdateOption<WebAuthenticationCB> option) {
        assertObjectNotNull("webAuthentication", webAuthentication);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                webAuthentication, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (WebAuthenticationCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (WebAuthenticationCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * webAuthenticationBhv.<span style="color: #FD4747">queryDelete</span>(webAuthentication, cb);
     * </pre>
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final WebAuthenticationCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final WebAuthenticationCB cb,
            final DeleteOption<WebAuthenticationCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((WebAuthenticationCB) cb);
        } else {
            return varyingQueryDelete((WebAuthenticationCB) cb,
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
     * WebAuthentication webAuthentication = new WebAuthentication();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * webAuthentication.setFoo...(value);
     * webAuthentication.setBar...(value);
     * InsertOption<WebAuthenticationCB> option = new InsertOption<WebAuthenticationCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * webAuthenticationBhv.<span style="color: #FD4747">varyingInsert</span>(webAuthentication, option);
     * ... = webAuthentication.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param webAuthentication The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final WebAuthentication webAuthentication,
            final InsertOption<WebAuthenticationCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(webAuthentication, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * WebAuthentication webAuthentication = new WebAuthentication();
     * webAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webAuthentication.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webAuthentication.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;WebAuthenticationCB&gt; option = new UpdateOption&lt;WebAuthenticationCB&gt;();
     *     option.self(new SpecifyQuery&lt;WebAuthenticationCB&gt;() {
     *         public void specify(WebAuthenticationCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     webAuthenticationBhv.<span style="color: #FD4747">varyingUpdate</span>(webAuthentication, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param webAuthentication The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final WebAuthentication webAuthentication,
            final UpdateOption<WebAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(webAuthentication, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * WebAuthentication webAuthentication = new WebAuthentication();
     * webAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webAuthentication.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webAuthentication.setVersionNo(value);</span>
     * UpdateOption&lt;WebAuthenticationCB&gt; option = new UpdateOption&lt;WebAuthenticationCB&gt;();
     * option.self(new SpecifyQuery&lt;WebAuthenticationCB&gt;() {
     *     public void specify(WebAuthenticationCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * webAuthenticationBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(webAuthentication, option);
     * </pre>
     * @param webAuthentication The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(
            final WebAuthentication webAuthentication,
            final UpdateOption<WebAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(webAuthentication, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param webAuthentication The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final WebAuthentication webAuthentication,
            final InsertOption<WebAuthenticationCB> insertOption,
            final UpdateOption<WebAuthenticationCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(webAuthentication, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param webAuthentication The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final WebAuthentication webAuthentication,
            final InsertOption<WebAuthenticationCB> insertOption,
            final UpdateOption<WebAuthenticationCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(webAuthentication, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param webAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final WebAuthentication webAuthentication,
            final DeleteOption<WebAuthenticationCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(webAuthentication, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param webAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(
            final WebAuthentication webAuthentication,
            final DeleteOption<WebAuthenticationCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(webAuthentication, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<WebAuthentication> webAuthenticationList,
            final InsertOption<WebAuthenticationCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(webAuthenticationList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<WebAuthentication> webAuthenticationList,
            final UpdateOption<WebAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(webAuthenticationList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<WebAuthentication> webAuthenticationList,
            final UpdateOption<WebAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(webAuthenticationList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<WebAuthentication> webAuthenticationList,
            final DeleteOption<WebAuthenticationCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(webAuthenticationList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param webAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<WebAuthentication> webAuthenticationList,
            final DeleteOption<WebAuthenticationCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(webAuthenticationList, option);
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
            final QueryInsertSetupper<WebAuthentication, WebAuthenticationCB> setupper,
            final InsertOption<WebAuthenticationCB> option) {
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
     * WebAuthentication webAuthentication = new WebAuthentication();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//webAuthentication.setPK...(value);</span>
     * webAuthentication.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webAuthentication.setVersionNo(value);</span>
     * WebAuthenticationCB cb = new WebAuthenticationCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;WebAuthenticationCB&gt; option = new UpdateOption&lt;WebAuthenticationCB&gt;();
     * option.self(new SpecifyQuery&lt;WebAuthenticationCB&gt;() {
     *     public void specify(WebAuthenticationCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * webAuthenticationBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(webAuthentication, cb, option);
     * </pre>
     * @param webAuthentication The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final WebAuthentication webAuthentication,
            final WebAuthenticationCB cb,
            final UpdateOption<WebAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(webAuthentication, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of WebAuthentication. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final WebAuthenticationCB cb,
            final DeleteOption<WebAuthenticationCB> option) {
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
    public OutsideSqlBasicExecutor<WebAuthenticationBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final WebAuthenticationCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final WebAuthenticationCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends WebAuthentication> void delegateSelectCursor(
            final WebAuthenticationCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends WebAuthentication> List<ENTITY> delegateSelectList(
            final WebAuthenticationCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final WebAuthentication e,
            final InsertOption<WebAuthenticationCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final WebAuthentication e,
            final UpdateOption<WebAuthenticationCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final WebAuthentication e,
            final UpdateOption<WebAuthenticationCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final WebAuthentication e,
            final DeleteOption<WebAuthenticationCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final WebAuthentication e,
            final DeleteOption<WebAuthenticationCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<WebAuthentication> ls,
            final InsertOption<WebAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<WebAuthentication> ls,
            final UpdateOption<WebAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<WebAuthentication> ls,
            final UpdateOption<WebAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<WebAuthentication> ls,
            final DeleteOption<WebAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<WebAuthentication> ls,
            final DeleteOption<WebAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final WebAuthentication e,
            final WebAuthenticationCB inCB, final ConditionBean resCB,
            final InsertOption<WebAuthenticationCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final WebAuthentication e,
            final WebAuthenticationCB cb,
            final UpdateOption<WebAuthenticationCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final WebAuthenticationCB cb,
            final DeleteOption<WebAuthenticationCB> op) {
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
    protected WebAuthentication downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, WebAuthentication.class);
    }

    protected WebAuthenticationCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                WebAuthenticationCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<WebAuthentication> downcast(
            final List<? extends Entity> entityList) {
        return (List<WebAuthentication>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<WebAuthenticationCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<WebAuthenticationCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<WebAuthenticationCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<WebAuthenticationCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<WebAuthenticationCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<WebAuthenticationCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<WebAuthentication, WebAuthenticationCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<WebAuthentication, WebAuthenticationCB>) option;
    }
}
