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

import jp.sf.fess.db.bsentity.dbmeta.RequestHeaderDbm;
import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.exbhv.RequestHeaderBhv;
import jp.sf.fess.db.exentity.RequestHeader;
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
 * The behavior of REQUEST_HEADER as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, NAME, VALUE, WEB_CRAWLING_CONFIG_ID, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
public abstract class BsRequestHeaderBhv extends AbstractBehaviorWritable {

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
        return "REQUEST_HEADER";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return RequestHeaderDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public RequestHeaderDbm getMyDBMeta() {
        return RequestHeaderDbm.getInstance();
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
    public RequestHeader newMyEntity() {
        return new RequestHeader();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public RequestHeaderCB newMyConditionBean() {
        return new RequestHeaderCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * int count = requestHeaderBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final RequestHeaderCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final RequestHeaderCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final RequestHeaderCB cb) { // called by selectPage(cb)
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
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * RequestHeader requestHeader = requestHeaderBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (requestHeader != null) {
     *     ... = requestHeader.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public RequestHeader selectEntity(final RequestHeaderCB cb) {
        return doSelectEntity(cb, RequestHeader.class);
    }

    protected <ENTITY extends RequestHeader> ENTITY doSelectEntity(
            final RequestHeaderCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(cb, entityType,
                new InternalSelectEntityCallback<ENTITY, RequestHeaderCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final RequestHeaderCB cb,
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
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * RequestHeader requestHeader = requestHeaderBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = requestHeader.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public RequestHeader selectEntityWithDeletedCheck(final RequestHeaderCB cb) {
        return doSelectEntityWithDeletedCheck(cb, RequestHeader.class);
    }

    protected <ENTITY extends RequestHeader> ENTITY doSelectEntityWithDeletedCheck(
            final RequestHeaderCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, RequestHeaderCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final RequestHeaderCB cb,
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
    public RequestHeader selectByPKValue(final Long id) {
        return doSelectByPKValue(id, RequestHeader.class);
    }

    protected <ENTITY extends RequestHeader> ENTITY doSelectByPKValue(
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
    public RequestHeader selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, RequestHeader.class);
    }

    protected <ENTITY extends RequestHeader> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private RequestHeaderCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final RequestHeaderCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;RequestHeader&gt; requestHeaderList = requestHeaderBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (RequestHeader requestHeader : requestHeaderList) {
     *     ... = requestHeader.get...();
     * }
     * </pre>
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<RequestHeader> selectList(final RequestHeaderCB cb) {
        return doSelectList(cb, RequestHeader.class);
    }

    protected <ENTITY extends RequestHeader> ListResultBean<ENTITY> doSelectList(
            final RequestHeaderCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, RequestHeaderCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final RequestHeaderCB cb,
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
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;RequestHeader&gt; page = requestHeaderBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (RequestHeader requestHeader : page) {
     *     ... = requestHeader.get...();
     * }
     * </pre>
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<RequestHeader> selectPage(final RequestHeaderCB cb) {
        return doSelectPage(cb, RequestHeader.class);
    }

    protected <ENTITY extends RequestHeader> PagingResultBean<ENTITY> doSelectPage(
            final RequestHeaderCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, RequestHeaderCB>() {
                    @Override
                    public int callbackSelectCount(final RequestHeaderCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final RequestHeaderCB cb,
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
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * requestHeaderBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;RequestHeader&gt;() {
     *     public void handle(RequestHeader entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @param entityRowHandler The handler of entity row of RequestHeader. (NotNull)
     */
    public void selectCursor(final RequestHeaderCB cb,
            final EntityRowHandler<RequestHeader> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, RequestHeader.class);
    }

    protected <ENTITY extends RequestHeader> void doSelectCursor(
            final RequestHeaderCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<RequestHeader>", entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(cb, entityRowHandler, entityType,
                new InternalSelectCursorCallback<ENTITY, RequestHeaderCB>() {
                    @Override
                    public void callbackSelectCursor(final RequestHeaderCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final RequestHeaderCB cb,
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
     * requestHeaderBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(RequestHeaderCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<RequestHeaderCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends RequestHeaderCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param requestHeaderList The list of requestHeader. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<WebCrawlingConfig> pulloutWebCrawlingConfig(
            final List<RequestHeader> requestHeaderList) {
        return helpPulloutInternally(
                requestHeaderList,
                new InternalPulloutCallback<RequestHeader, WebCrawlingConfig>() {
                    @Override
                    public WebCrawlingConfig getFr(final RequestHeader e) {
                        return e.getWebCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
                            final List<RequestHeader> ls) {
                        e.setRequestHeaderList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param requestHeaderList The list of requestHeader. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(final List<RequestHeader> requestHeaderList) {
        return helpExtractListInternally(requestHeaderList,
                new InternalExtractCallback<RequestHeader, Long>() {
                    @Override
                    public Long getCV(final RequestHeader e) {
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
     * RequestHeader requestHeader = new RequestHeader();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * requestHeader.setFoo...(value);
     * requestHeader.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//requestHeader.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//requestHeader.set...;</span>
     * requestHeaderBhv.<span style="color: #FD4747">insert</span>(requestHeader);
     * ... = requestHeader.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param requestHeader The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final RequestHeader requestHeader) {
        doInsert(requestHeader, null);
    }

    protected void doInsert(final RequestHeader requestHeader,
            final InsertOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeader", requestHeader);
        prepareInsertOption(option);
        delegateInsert(requestHeader, option);
    }

    protected void prepareInsertOption(
            final InsertOption<RequestHeaderCB> option) {
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
     * RequestHeader requestHeader = new RequestHeader();
     * requestHeader.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * requestHeader.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//requestHeader.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//requestHeader.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * requestHeader.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     requestHeaderBhv.<span style="color: #FD4747">update</span>(requestHeader);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param requestHeader The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final RequestHeader requestHeader) {
        doUpdate(requestHeader, null);
    }

    protected void doUpdate(final RequestHeader requestHeader,
            final UpdateOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeader", requestHeader);
        prepareUpdateOption(option);
        helpUpdateInternally(requestHeader,
                new InternalUpdateCallback<RequestHeader>() {
                    @Override
                    public int callbackDelegateUpdate(final RequestHeader entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<RequestHeaderCB> option) {
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

    protected RequestHeaderCB createCBForVaryingUpdate() {
        final RequestHeaderCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected RequestHeaderCB createCBForSpecifiedUpdate() {
        final RequestHeaderCB cb = newMyConditionBean();
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
     * RequestHeader requestHeader = new RequestHeader();
     * requestHeader.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * requestHeader.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//requestHeader.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//requestHeader.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//requestHeader.setVersionNo(value);</span>
     * requestHeaderBhv.<span style="color: #FD4747">updateNonstrict</span>(requestHeader);
     * </pre>
     * @param requestHeader The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final RequestHeader requestHeader) {
        doUpdateNonstrict(requestHeader, null);
    }

    protected void doUpdateNonstrict(final RequestHeader requestHeader,
            final UpdateOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeader", requestHeader);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(requestHeader,
                new InternalUpdateNonstrictCallback<RequestHeader>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final RequestHeader entity) {
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
     * @param requestHeader The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final RequestHeader requestHeader) {
        doInesrtOrUpdate(requestHeader, null, null);
    }

    protected void doInesrtOrUpdate(final RequestHeader requestHeader,
            final InsertOption<RequestHeaderCB> insertOption,
            final UpdateOption<RequestHeaderCB> updateOption) {
        helpInsertOrUpdateInternally(
                requestHeader,
                new InternalInsertOrUpdateCallback<RequestHeader, RequestHeaderCB>() {
                    @Override
                    public void callbackInsert(final RequestHeader entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final RequestHeader entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public RequestHeaderCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final RequestHeaderCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<RequestHeaderCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<RequestHeaderCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param requestHeader The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(final RequestHeader requestHeader) {
        doInesrtOrUpdateNonstrict(requestHeader, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(final RequestHeader requestHeader,
            final InsertOption<RequestHeaderCB> insertOption,
            final UpdateOption<RequestHeaderCB> updateOption) {
        helpInsertOrUpdateInternally(requestHeader,
                new InternalInsertOrUpdateNonstrictCallback<RequestHeader>() {
                    @Override
                    public void callbackInsert(final RequestHeader entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(
                            final RequestHeader entity) {
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
            insertOption = insertOption == null ? new InsertOption<RequestHeaderCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<RequestHeaderCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * RequestHeader requestHeader = new RequestHeader();
     * requestHeader.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * requestHeader.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     requestHeaderBhv.<span style="color: #FD4747">delete</span>(requestHeader);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param requestHeader The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final RequestHeader requestHeader) {
        doDelete(requestHeader, null);
    }

    protected void doDelete(final RequestHeader requestHeader,
            final DeleteOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeader", requestHeader);
        prepareDeleteOption(option);
        helpDeleteInternally(requestHeader,
                new InternalDeleteCallback<RequestHeader>() {
                    @Override
                    public int callbackDelegateDelete(final RequestHeader entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<RequestHeaderCB> option) {
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
     * RequestHeader requestHeader = new RequestHeader();
     * requestHeader.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//requestHeader.setVersionNo(value);</span>
     * requestHeaderBhv.<span style="color: #FD4747">deleteNonstrict</span>(requestHeader);
     * </pre>
     * @param requestHeader The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final RequestHeader requestHeader) {
        doDeleteNonstrict(requestHeader, null);
    }

    protected void doDeleteNonstrict(final RequestHeader requestHeader,
            final DeleteOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeader", requestHeader);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(requestHeader,
                new InternalDeleteNonstrictCallback<RequestHeader>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final RequestHeader entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * RequestHeader requestHeader = new RequestHeader();
     * requestHeader.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//requestHeader.setVersionNo(value);</span>
     * requestHeaderBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(requestHeader);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param requestHeader The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(final RequestHeader requestHeader) {
        doDeleteNonstrictIgnoreDeleted(requestHeader, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(
            final RequestHeader requestHeader,
            final DeleteOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeader", requestHeader);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(
                requestHeader,
                new InternalDeleteNonstrictIgnoreDeletedCallback<RequestHeader>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final RequestHeader entity) {
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
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<RequestHeader> requestHeaderList) {
        return doBatchInsert(requestHeaderList, null);
    }

    protected int[] doBatchInsert(final List<RequestHeader> requestHeaderList,
            final InsertOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeaderList", requestHeaderList);
        prepareInsertOption(option);
        return delegateBatchInsert(requestHeaderList, option);
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
     * requestHeaderBhv.<span style="color: #FD4747">batchUpdate</span>(requestHeaderList, new SpecifyQuery<RequestHeaderCB>() {
     *     public void specify(RequestHeaderCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<RequestHeader> requestHeaderList) {
        return doBatchUpdate(requestHeaderList, null);
    }

    protected int[] doBatchUpdate(final List<RequestHeader> requestHeaderList,
            final UpdateOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeaderList", requestHeaderList);
        prepareBatchUpdateOption(requestHeaderList, option);
        return delegateBatchUpdate(requestHeaderList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<RequestHeader> requestHeaderList,
            final UpdateOption<RequestHeaderCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(requestHeaderList);
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
     * requestHeaderBhv.<span style="color: #FD4747">batchUpdate</span>(requestHeaderList, new SpecifyQuery<RequestHeaderCB>() {
     *     public void specify(RequestHeaderCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * requestHeaderBhv.<span style="color: #FD4747">batchUpdate</span>(requestHeaderList, new SpecifyQuery<RequestHeaderCB>() {
     *     public void specify(RequestHeaderCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<RequestHeader> requestHeaderList,
            final SpecifyQuery<RequestHeaderCB> updateColumnSpec) {
        return doBatchUpdate(requestHeaderList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdateNonstrict() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * requestHeaderBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(requestHeaderList, new SpecifyQuery<RequestHeaderCB>() {
     *     public void specify(RequestHeaderCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<RequestHeader> requestHeaderList) {
        return doBatchUpdateNonstrict(requestHeaderList, null);
    }

    protected int[] doBatchUpdateNonstrict(
            final List<RequestHeader> requestHeaderList,
            final UpdateOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeaderList", requestHeaderList);
        prepareBatchUpdateOption(requestHeaderList, option);
        return delegateBatchUpdateNonstrict(requestHeaderList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span> 
     * requestHeaderBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(requestHeaderList, new SpecifyQuery<RequestHeaderCB>() {
     *     public void specify(RequestHeaderCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * requestHeaderBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(requestHeaderList, new SpecifyQuery<RequestHeaderCB>() {
     *     public void specify(RequestHeaderCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<RequestHeader> requestHeaderList,
            final SpecifyQuery<RequestHeaderCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(requestHeaderList,
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
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<RequestHeader> requestHeaderList) {
        return doBatchDelete(requestHeaderList, null);
    }

    protected int[] doBatchDelete(final List<RequestHeader> requestHeaderList,
            final DeleteOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeaderList", requestHeaderList);
        prepareDeleteOption(option);
        return delegateBatchDelete(requestHeaderList, option);
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
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<RequestHeader> requestHeaderList) {
        return doBatchDeleteNonstrict(requestHeaderList, null);
    }

    protected int[] doBatchDeleteNonstrict(
            final List<RequestHeader> requestHeaderList,
            final DeleteOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeaderList", requestHeaderList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(requestHeaderList, option);
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
     * requestHeaderBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;RequestHeader, RequestHeaderCB&gt;() {
     *     public ConditionBean setup(requestHeader entity, RequestHeaderCB intoCB) {
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
            final QueryInsertSetupper<RequestHeader, RequestHeaderCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<RequestHeader, RequestHeaderCB> setupper,
            final InsertOption<RequestHeaderCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final RequestHeader entity = new RequestHeader();
        final RequestHeaderCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected RequestHeaderCB createCBForQueryInsert() {
        final RequestHeaderCB cb = newMyConditionBean();
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
     * RequestHeader requestHeader = new RequestHeader();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//requestHeader.setPK...(value);</span>
     * requestHeader.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//requestHeader.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//requestHeader.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//requestHeader.setVersionNo(value);</span>
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * requestHeaderBhv.<span style="color: #FD4747">queryUpdate</span>(requestHeader, cb);
     * </pre>
     * @param requestHeader The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final RequestHeader requestHeader,
            final RequestHeaderCB cb) {
        return doQueryUpdate(requestHeader, cb, null);
    }

    protected int doQueryUpdate(final RequestHeader requestHeader,
            final RequestHeaderCB cb, final UpdateOption<RequestHeaderCB> option) {
        assertObjectNotNull("requestHeader", requestHeader);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                requestHeader, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (RequestHeaderCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity), (RequestHeaderCB) cb,
                    downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * requestHeaderBhv.<span style="color: #FD4747">queryDelete</span>(requestHeader, cb);
     * </pre>
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final RequestHeaderCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final RequestHeaderCB cb,
            final DeleteOption<RequestHeaderCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((RequestHeaderCB) cb);
        } else {
            return varyingQueryDelete((RequestHeaderCB) cb, downcast(option));
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
     * RequestHeader requestHeader = new RequestHeader();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * requestHeader.setFoo...(value);
     * requestHeader.setBar...(value);
     * InsertOption<RequestHeaderCB> option = new InsertOption<RequestHeaderCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * requestHeaderBhv.<span style="color: #FD4747">varyingInsert</span>(requestHeader, option);
     * ... = requestHeader.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param requestHeader The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final RequestHeader requestHeader,
            final InsertOption<RequestHeaderCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(requestHeader, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * RequestHeader requestHeader = new RequestHeader();
     * requestHeader.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * requestHeader.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * requestHeader.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;RequestHeaderCB&gt; option = new UpdateOption&lt;RequestHeaderCB&gt;();
     *     option.self(new SpecifyQuery&lt;RequestHeaderCB&gt;() {
     *         public void specify(RequestHeaderCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     requestHeaderBhv.<span style="color: #FD4747">varyingUpdate</span>(requestHeader, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param requestHeader The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final RequestHeader requestHeader,
            final UpdateOption<RequestHeaderCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(requestHeader, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * RequestHeader requestHeader = new RequestHeader();
     * requestHeader.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * requestHeader.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//requestHeader.setVersionNo(value);</span>
     * UpdateOption&lt;RequestHeaderCB&gt; option = new UpdateOption&lt;RequestHeaderCB&gt;();
     * option.self(new SpecifyQuery&lt;RequestHeaderCB&gt;() {
     *     public void specify(RequestHeaderCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * requestHeaderBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(requestHeader, option);
     * </pre>
     * @param requestHeader The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(final RequestHeader requestHeader,
            final UpdateOption<RequestHeaderCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(requestHeader, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param requestHeader The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final RequestHeader requestHeader,
            final InsertOption<RequestHeaderCB> insertOption,
            final UpdateOption<RequestHeaderCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(requestHeader, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param requestHeader The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final RequestHeader requestHeader,
            final InsertOption<RequestHeaderCB> insertOption,
            final UpdateOption<RequestHeaderCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(requestHeader, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param requestHeader The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final RequestHeader requestHeader,
            final DeleteOption<RequestHeaderCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(requestHeader, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param requestHeader The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(final RequestHeader requestHeader,
            final DeleteOption<RequestHeaderCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(requestHeader, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<RequestHeader> requestHeaderList,
            final InsertOption<RequestHeaderCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(requestHeaderList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<RequestHeader> requestHeaderList,
            final UpdateOption<RequestHeaderCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(requestHeaderList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<RequestHeader> requestHeaderList,
            final UpdateOption<RequestHeaderCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(requestHeaderList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<RequestHeader> requestHeaderList,
            final DeleteOption<RequestHeaderCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(requestHeaderList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param requestHeaderList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<RequestHeader> requestHeaderList,
            final DeleteOption<RequestHeaderCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(requestHeaderList, option);
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
            final QueryInsertSetupper<RequestHeader, RequestHeaderCB> setupper,
            final InsertOption<RequestHeaderCB> option) {
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
     * RequestHeader requestHeader = new RequestHeader();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//requestHeader.setPK...(value);</span>
     * requestHeader.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//requestHeader.setVersionNo(value);</span>
     * RequestHeaderCB cb = new RequestHeaderCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;RequestHeaderCB&gt; option = new UpdateOption&lt;RequestHeaderCB&gt;();
     * option.self(new SpecifyQuery&lt;RequestHeaderCB&gt;() {
     *     public void specify(RequestHeaderCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * requestHeaderBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(requestHeader, cb, option);
     * </pre>
     * @param requestHeader The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final RequestHeader requestHeader,
            final RequestHeaderCB cb, final UpdateOption<RequestHeaderCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(requestHeader, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of RequestHeader. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final RequestHeaderCB cb,
            final DeleteOption<RequestHeaderCB> option) {
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
    public OutsideSqlBasicExecutor<RequestHeaderBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final RequestHeaderCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final RequestHeaderCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends RequestHeader> void delegateSelectCursor(
            final RequestHeaderCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends RequestHeader> List<ENTITY> delegateSelectList(
            final RequestHeaderCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final RequestHeader e,
            final InsertOption<RequestHeaderCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final RequestHeader e,
            final UpdateOption<RequestHeaderCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final RequestHeader e,
            final UpdateOption<RequestHeaderCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final RequestHeader e,
            final DeleteOption<RequestHeaderCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final RequestHeader e,
            final DeleteOption<RequestHeaderCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<RequestHeader> ls,
            final InsertOption<RequestHeaderCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<RequestHeader> ls,
            final UpdateOption<RequestHeaderCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(final List<RequestHeader> ls,
            final UpdateOption<RequestHeaderCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<RequestHeader> ls,
            final DeleteOption<RequestHeaderCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(final List<RequestHeader> ls,
            final DeleteOption<RequestHeaderCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final RequestHeader e,
            final RequestHeaderCB inCB, final ConditionBean resCB,
            final InsertOption<RequestHeaderCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final RequestHeader e,
            final RequestHeaderCB cb, final UpdateOption<RequestHeaderCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final RequestHeaderCB cb,
            final DeleteOption<RequestHeaderCB> op) {
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
    protected RequestHeader downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, RequestHeader.class);
    }

    protected RequestHeaderCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, RequestHeaderCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<RequestHeader> downcast(
            final List<? extends Entity> entityList) {
        return (List<RequestHeader>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<RequestHeaderCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<RequestHeaderCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<RequestHeaderCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<RequestHeaderCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<RequestHeaderCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<RequestHeaderCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<RequestHeader, RequestHeaderCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<RequestHeader, RequestHeaderCB>) option;
    }
}
