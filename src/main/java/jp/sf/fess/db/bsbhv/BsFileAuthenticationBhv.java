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

import jp.sf.fess.db.bsentity.dbmeta.FileAuthenticationDbm;
import jp.sf.fess.db.cbean.FileAuthenticationCB;
import jp.sf.fess.db.exbhv.FileAuthenticationBhv;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.db.exentity.FileCrawlingConfig;

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
 * The behavior of FILE_AUTHENTICATION as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, HOSTNAME, PORT, PROTOCOL_SCHEME, USERNAME, PASSWORD, PARAMETERS, FILE_CRAWLING_CONFIG_ID, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     FILE_CRAWLING_CONFIG
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     fileCrawlingConfig
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsFileAuthenticationBhv extends AbstractBehaviorWritable {

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
        return "FILE_AUTHENTICATION";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return FileAuthenticationDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public FileAuthenticationDbm getMyDBMeta() {
        return FileAuthenticationDbm.getInstance();
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
    public FileAuthentication newMyEntity() {
        return new FileAuthentication();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public FileAuthenticationCB newMyConditionBean() {
        return new FileAuthenticationCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * int count = fileAuthenticationBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final FileAuthenticationCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final FileAuthenticationCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final FileAuthenticationCB cb) { // called by selectPage(cb)
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
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * FileAuthentication fileAuthentication = fileAuthenticationBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (fileAuthentication != null) {
     *     ... = fileAuthentication.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileAuthentication selectEntity(final FileAuthenticationCB cb) {
        return doSelectEntity(cb, FileAuthentication.class);
    }

    protected <ENTITY extends FileAuthentication> ENTITY doSelectEntity(
            final FileAuthenticationCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, FileAuthenticationCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileAuthenticationCB cb,
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
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * FileAuthentication fileAuthentication = fileAuthenticationBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = fileAuthentication.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileAuthentication selectEntityWithDeletedCheck(
            final FileAuthenticationCB cb) {
        return doSelectEntityWithDeletedCheck(cb, FileAuthentication.class);
    }

    protected <ENTITY extends FileAuthentication> ENTITY doSelectEntityWithDeletedCheck(
            final FileAuthenticationCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, FileAuthenticationCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileAuthenticationCB cb,
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
    public FileAuthentication selectByPKValue(final Long id) {
        return doSelectByPKValue(id, FileAuthentication.class);
    }

    protected <ENTITY extends FileAuthentication> ENTITY doSelectByPKValue(
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
    public FileAuthentication selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, FileAuthentication.class);
    }

    protected <ENTITY extends FileAuthentication> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private FileAuthenticationCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final FileAuthenticationCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;FileAuthentication&gt; fileAuthenticationList = fileAuthenticationBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (FileAuthentication fileAuthentication : fileAuthenticationList) {
     *     ... = fileAuthentication.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<FileAuthentication> selectList(
            final FileAuthenticationCB cb) {
        return doSelectList(cb, FileAuthentication.class);
    }

    protected <ENTITY extends FileAuthentication> ListResultBean<ENTITY> doSelectList(
            final FileAuthenticationCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, FileAuthenticationCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileAuthenticationCB cb,
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
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;FileAuthentication&gt; page = fileAuthenticationBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (FileAuthentication fileAuthentication : page) {
     *     ... = fileAuthentication.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<FileAuthentication> selectPage(
            final FileAuthenticationCB cb) {
        return doSelectPage(cb, FileAuthentication.class);
    }

    protected <ENTITY extends FileAuthentication> PagingResultBean<ENTITY> doSelectPage(
            final FileAuthenticationCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, FileAuthenticationCB>() {
                    @Override
                    public int callbackSelectCount(final FileAuthenticationCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileAuthenticationCB cb,
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
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * fileAuthenticationBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;FileAuthentication&gt;() {
     *     public void handle(FileAuthentication entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @param entityRowHandler The handler of entity row of FileAuthentication. (NotNull)
     */
    public void selectCursor(final FileAuthenticationCB cb,
            final EntityRowHandler<FileAuthentication> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, FileAuthentication.class);
    }

    protected <ENTITY extends FileAuthentication> void doSelectCursor(
            final FileAuthenticationCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<FileAuthentication>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, FileAuthenticationCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final FileAuthenticationCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileAuthenticationCB cb,
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
     * fileAuthenticationBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(FileAuthenticationCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<FileAuthenticationCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends FileAuthenticationCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * Pull out the list of foreign table 'FileCrawlingConfig'.
     * @param fileAuthenticationList The list of fileAuthentication. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<FileCrawlingConfig> pulloutFileCrawlingConfig(
            final List<FileAuthentication> fileAuthenticationList) {
        return helpPulloutInternally(
                fileAuthenticationList,
                new InternalPulloutCallback<FileAuthentication, FileCrawlingConfig>() {
                    @Override
                    public FileCrawlingConfig getFr(final FileAuthentication e) {
                        return e.getFileCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final FileCrawlingConfig e,
                            final List<FileAuthentication> ls) {
                        e.setFileAuthenticationList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param fileAuthenticationList The list of fileAuthentication. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<FileAuthentication> fileAuthenticationList) {
        return helpExtractListInternally(fileAuthenticationList,
                new InternalExtractCallback<FileAuthentication, Long>() {
                    @Override
                    public Long getCV(final FileAuthentication e) {
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
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileAuthentication.setFoo...(value);
     * fileAuthentication.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileAuthentication.set...;</span>
     * fileAuthenticationBhv.<span style="color: #FD4747">insert</span>(fileAuthentication);
     * ... = fileAuthentication.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileAuthentication The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final FileAuthentication fileAuthentication) {
        doInsert(fileAuthentication, null);
    }

    protected void doInsert(final FileAuthentication fileAuthentication,
            final InsertOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthentication", fileAuthentication);
        prepareInsertOption(option);
        delegateInsert(fileAuthentication, option);
    }

    protected void prepareInsertOption(
            final InsertOption<FileAuthenticationCB> option) {
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
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * fileAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileAuthentication.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileAuthentication.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileAuthentication.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     fileAuthenticationBhv.<span style="color: #FD4747">update</span>(fileAuthentication);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param fileAuthentication The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final FileAuthentication fileAuthentication) {
        doUpdate(fileAuthentication, null);
    }

    protected void doUpdate(final FileAuthentication fileAuthentication,
            final UpdateOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthentication", fileAuthentication);
        prepareUpdateOption(option);
        helpUpdateInternally(fileAuthentication,
                new InternalUpdateCallback<FileAuthentication>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final FileAuthentication entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<FileAuthenticationCB> option) {
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

    protected FileAuthenticationCB createCBForVaryingUpdate() {
        final FileAuthenticationCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected FileAuthenticationCB createCBForSpecifiedUpdate() {
        final FileAuthenticationCB cb = newMyConditionBean();
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
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * fileAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileAuthentication.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileAuthentication.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setVersionNo(value);</span>
     * fileAuthenticationBhv.<span style="color: #FD4747">updateNonstrict</span>(fileAuthentication);
     * </pre>
     * @param fileAuthentication The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final FileAuthentication fileAuthentication) {
        doUpdateNonstrict(fileAuthentication, null);
    }

    protected void doUpdateNonstrict(
            final FileAuthentication fileAuthentication,
            final UpdateOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthentication", fileAuthentication);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(fileAuthentication,
                new InternalUpdateNonstrictCallback<FileAuthentication>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final FileAuthentication entity) {
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
     * @param fileAuthentication The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final FileAuthentication fileAuthentication) {
        doInesrtOrUpdate(fileAuthentication, null, null);
    }

    protected void doInesrtOrUpdate(
            final FileAuthentication fileAuthentication,
            final InsertOption<FileAuthenticationCB> insertOption,
            final UpdateOption<FileAuthenticationCB> updateOption) {
        helpInsertOrUpdateInternally(
                fileAuthentication,
                new InternalInsertOrUpdateCallback<FileAuthentication, FileAuthenticationCB>() {
                    @Override
                    public void callbackInsert(final FileAuthentication entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final FileAuthentication entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public FileAuthenticationCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final FileAuthenticationCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<FileAuthenticationCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<FileAuthenticationCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param fileAuthentication The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(
            final FileAuthentication fileAuthentication) {
        doInesrtOrUpdateNonstrict(fileAuthentication, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(
            final FileAuthentication fileAuthentication,
            final InsertOption<FileAuthenticationCB> insertOption,
            final UpdateOption<FileAuthenticationCB> updateOption) {
        helpInsertOrUpdateInternally(
                fileAuthentication,
                new InternalInsertOrUpdateNonstrictCallback<FileAuthentication>() {
                    @Override
                    public void callbackInsert(final FileAuthentication entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(
                            final FileAuthentication entity) {
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
            insertOption = insertOption == null ? new InsertOption<FileAuthenticationCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<FileAuthenticationCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * fileAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileAuthentication.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     fileAuthenticationBhv.<span style="color: #FD4747">delete</span>(fileAuthentication);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param fileAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final FileAuthentication fileAuthentication) {
        doDelete(fileAuthentication, null);
    }

    protected void doDelete(final FileAuthentication fileAuthentication,
            final DeleteOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthentication", fileAuthentication);
        prepareDeleteOption(option);
        helpDeleteInternally(fileAuthentication,
                new InternalDeleteCallback<FileAuthentication>() {
                    @Override
                    public int callbackDelegateDelete(
                            final FileAuthentication entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<FileAuthenticationCB> option) {
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
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * fileAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setVersionNo(value);</span>
     * fileAuthenticationBhv.<span style="color: #FD4747">deleteNonstrict</span>(fileAuthentication);
     * </pre>
     * @param fileAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final FileAuthentication fileAuthentication) {
        doDeleteNonstrict(fileAuthentication, null);
    }

    protected void doDeleteNonstrict(
            final FileAuthentication fileAuthentication,
            final DeleteOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthentication", fileAuthentication);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(fileAuthentication,
                new InternalDeleteNonstrictCallback<FileAuthentication>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final FileAuthentication entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * fileAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setVersionNo(value);</span>
     * fileAuthenticationBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(fileAuthentication);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param fileAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(
            final FileAuthentication fileAuthentication) {
        doDeleteNonstrictIgnoreDeleted(fileAuthentication, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(
            final FileAuthentication fileAuthentication,
            final DeleteOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthentication", fileAuthentication);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(
                fileAuthentication,
                new InternalDeleteNonstrictIgnoreDeletedCallback<FileAuthentication>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final FileAuthentication entity) {
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
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<FileAuthentication> fileAuthenticationList) {
        return doBatchInsert(fileAuthenticationList, null);
    }

    protected int[] doBatchInsert(
            final List<FileAuthentication> fileAuthenticationList,
            final InsertOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthenticationList", fileAuthenticationList);
        prepareInsertOption(option);
        return delegateBatchInsert(fileAuthenticationList, option);
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
     * fileAuthenticationBhv.<span style="color: #FD4747">batchUpdate</span>(fileAuthenticationList, new SpecifyQuery<FileAuthenticationCB>() {
     *     public void specify(FileAuthenticationCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<FileAuthentication> fileAuthenticationList) {
        return doBatchUpdate(fileAuthenticationList, null);
    }

    protected int[] doBatchUpdate(
            final List<FileAuthentication> fileAuthenticationList,
            final UpdateOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthenticationList", fileAuthenticationList);
        prepareBatchUpdateOption(fileAuthenticationList, option);
        return delegateBatchUpdate(fileAuthenticationList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<FileAuthentication> fileAuthenticationList,
            final UpdateOption<FileAuthenticationCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(fileAuthenticationList);
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
     * fileAuthenticationBhv.<span style="color: #FD4747">batchUpdate</span>(fileAuthenticationList, new SpecifyQuery<FileAuthenticationCB>() {
     *     public void specify(FileAuthenticationCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * fileAuthenticationBhv.<span style="color: #FD4747">batchUpdate</span>(fileAuthenticationList, new SpecifyQuery<FileAuthenticationCB>() {
     *     public void specify(FileAuthenticationCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<FileAuthentication> fileAuthenticationList,
            final SpecifyQuery<FileAuthenticationCB> updateColumnSpec) {
        return doBatchUpdate(fileAuthenticationList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdateNonstrict() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * fileAuthenticationBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(fileAuthenticationList, new SpecifyQuery<FileAuthenticationCB>() {
     *     public void specify(FileAuthenticationCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<FileAuthentication> fileAuthenticationList) {
        return doBatchUpdateNonstrict(fileAuthenticationList, null);
    }

    protected int[] doBatchUpdateNonstrict(
            final List<FileAuthentication> fileAuthenticationList,
            final UpdateOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthenticationList", fileAuthenticationList);
        prepareBatchUpdateOption(fileAuthenticationList, option);
        return delegateBatchUpdateNonstrict(fileAuthenticationList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span> 
     * fileAuthenticationBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(fileAuthenticationList, new SpecifyQuery<FileAuthenticationCB>() {
     *     public void specify(FileAuthenticationCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * fileAuthenticationBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(fileAuthenticationList, new SpecifyQuery<FileAuthenticationCB>() {
     *     public void specify(FileAuthenticationCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<FileAuthentication> fileAuthenticationList,
            final SpecifyQuery<FileAuthenticationCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(fileAuthenticationList,
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
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(
            final List<FileAuthentication> fileAuthenticationList) {
        return doBatchDelete(fileAuthenticationList, null);
    }

    protected int[] doBatchDelete(
            final List<FileAuthentication> fileAuthenticationList,
            final DeleteOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthenticationList", fileAuthenticationList);
        prepareDeleteOption(option);
        return delegateBatchDelete(fileAuthenticationList, option);
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
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<FileAuthentication> fileAuthenticationList) {
        return doBatchDeleteNonstrict(fileAuthenticationList, null);
    }

    protected int[] doBatchDeleteNonstrict(
            final List<FileAuthentication> fileAuthenticationList,
            final DeleteOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthenticationList", fileAuthenticationList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(fileAuthenticationList, option);
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
     * fileAuthenticationBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;FileAuthentication, FileAuthenticationCB&gt;() {
     *     public ConditionBean setup(fileAuthentication entity, FileAuthenticationCB intoCB) {
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
            final QueryInsertSetupper<FileAuthentication, FileAuthenticationCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<FileAuthentication, FileAuthenticationCB> setupper,
            final InsertOption<FileAuthenticationCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final FileAuthentication entity = new FileAuthentication();
        final FileAuthenticationCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected FileAuthenticationCB createCBForQueryInsert() {
        final FileAuthenticationCB cb = newMyConditionBean();
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
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setPK...(value);</span>
     * fileAuthentication.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileAuthentication.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setVersionNo(value);</span>
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * fileAuthenticationBhv.<span style="color: #FD4747">queryUpdate</span>(fileAuthentication, cb);
     * </pre>
     * @param fileAuthentication The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final FileAuthentication fileAuthentication,
            final FileAuthenticationCB cb) {
        return doQueryUpdate(fileAuthentication, cb, null);
    }

    protected int doQueryUpdate(final FileAuthentication fileAuthentication,
            final FileAuthenticationCB cb,
            final UpdateOption<FileAuthenticationCB> option) {
        assertObjectNotNull("fileAuthentication", fileAuthentication);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                fileAuthentication, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (FileAuthenticationCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (FileAuthenticationCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * fileAuthenticationBhv.<span style="color: #FD4747">queryDelete</span>(fileAuthentication, cb);
     * </pre>
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final FileAuthenticationCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final FileAuthenticationCB cb,
            final DeleteOption<FileAuthenticationCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((FileAuthenticationCB) cb);
        } else {
            return varyingQueryDelete((FileAuthenticationCB) cb,
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
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileAuthentication.setFoo...(value);
     * fileAuthentication.setBar...(value);
     * InsertOption<FileAuthenticationCB> option = new InsertOption<FileAuthenticationCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * fileAuthenticationBhv.<span style="color: #FD4747">varyingInsert</span>(fileAuthentication, option);
     * ... = fileAuthentication.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileAuthentication The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final FileAuthentication fileAuthentication,
            final InsertOption<FileAuthenticationCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(fileAuthentication, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * fileAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileAuthentication.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileAuthentication.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;FileAuthenticationCB&gt; option = new UpdateOption&lt;FileAuthenticationCB&gt;();
     *     option.self(new SpecifyQuery&lt;FileAuthenticationCB&gt;() {
     *         public void specify(FileAuthenticationCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     fileAuthenticationBhv.<span style="color: #FD4747">varyingUpdate</span>(fileAuthentication, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileAuthentication The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final FileAuthentication fileAuthentication,
            final UpdateOption<FileAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(fileAuthentication, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * fileAuthentication.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileAuthentication.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setVersionNo(value);</span>
     * UpdateOption&lt;FileAuthenticationCB&gt; option = new UpdateOption&lt;FileAuthenticationCB&gt;();
     * option.self(new SpecifyQuery&lt;FileAuthenticationCB&gt;() {
     *     public void specify(FileAuthenticationCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileAuthenticationBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(fileAuthentication, option);
     * </pre>
     * @param fileAuthentication The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(
            final FileAuthentication fileAuthentication,
            final UpdateOption<FileAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(fileAuthentication, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param fileAuthentication The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final FileAuthentication fileAuthentication,
            final InsertOption<FileAuthenticationCB> insertOption,
            final UpdateOption<FileAuthenticationCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(fileAuthentication, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param fileAuthentication The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final FileAuthentication fileAuthentication,
            final InsertOption<FileAuthenticationCB> insertOption,
            final UpdateOption<FileAuthenticationCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(fileAuthentication, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param fileAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final FileAuthentication fileAuthentication,
            final DeleteOption<FileAuthenticationCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(fileAuthentication, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param fileAuthentication The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(
            final FileAuthentication fileAuthentication,
            final DeleteOption<FileAuthenticationCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(fileAuthentication, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<FileAuthentication> fileAuthenticationList,
            final InsertOption<FileAuthenticationCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(fileAuthenticationList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<FileAuthentication> fileAuthenticationList,
            final UpdateOption<FileAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(fileAuthenticationList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<FileAuthentication> fileAuthenticationList,
            final UpdateOption<FileAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(fileAuthenticationList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<FileAuthentication> fileAuthenticationList,
            final DeleteOption<FileAuthenticationCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(fileAuthenticationList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param fileAuthenticationList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<FileAuthentication> fileAuthenticationList,
            final DeleteOption<FileAuthenticationCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(fileAuthenticationList, option);
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
            final QueryInsertSetupper<FileAuthentication, FileAuthenticationCB> setupper,
            final InsertOption<FileAuthenticationCB> option) {
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
     * FileAuthentication fileAuthentication = new FileAuthentication();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setPK...(value);</span>
     * fileAuthentication.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileAuthentication.setVersionNo(value);</span>
     * FileAuthenticationCB cb = new FileAuthenticationCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;FileAuthenticationCB&gt; option = new UpdateOption&lt;FileAuthenticationCB&gt;();
     * option.self(new SpecifyQuery&lt;FileAuthenticationCB&gt;() {
     *     public void specify(FileAuthenticationCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileAuthenticationBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(fileAuthentication, cb, option);
     * </pre>
     * @param fileAuthentication The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final FileAuthentication fileAuthentication,
            final FileAuthenticationCB cb,
            final UpdateOption<FileAuthenticationCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(fileAuthentication, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of FileAuthentication. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final FileAuthenticationCB cb,
            final DeleteOption<FileAuthenticationCB> option) {
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
    public OutsideSqlBasicExecutor<FileAuthenticationBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final FileAuthenticationCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final FileAuthenticationCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends FileAuthentication> void delegateSelectCursor(
            final FileAuthenticationCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends FileAuthentication> List<ENTITY> delegateSelectList(
            final FileAuthenticationCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final FileAuthentication e,
            final InsertOption<FileAuthenticationCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final FileAuthentication e,
            final UpdateOption<FileAuthenticationCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final FileAuthentication e,
            final UpdateOption<FileAuthenticationCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final FileAuthentication e,
            final DeleteOption<FileAuthenticationCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final FileAuthentication e,
            final DeleteOption<FileAuthenticationCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<FileAuthentication> ls,
            final InsertOption<FileAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<FileAuthentication> ls,
            final UpdateOption<FileAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<FileAuthentication> ls,
            final UpdateOption<FileAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<FileAuthentication> ls,
            final DeleteOption<FileAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<FileAuthentication> ls,
            final DeleteOption<FileAuthenticationCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final FileAuthentication e,
            final FileAuthenticationCB inCB, final ConditionBean resCB,
            final InsertOption<FileAuthenticationCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final FileAuthentication e,
            final FileAuthenticationCB cb,
            final UpdateOption<FileAuthenticationCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final FileAuthenticationCB cb,
            final DeleteOption<FileAuthenticationCB> op) {
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
    protected FileAuthentication downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, FileAuthentication.class);
    }

    protected FileAuthenticationCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                FileAuthenticationCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<FileAuthentication> downcast(
            final List<? extends Entity> entityList) {
        return (List<FileAuthentication>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<FileAuthenticationCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<FileAuthenticationCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<FileAuthenticationCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<FileAuthenticationCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<FileAuthenticationCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<FileAuthenticationCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<FileAuthentication, FileAuthenticationCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<FileAuthentication, FileAuthenticationCB>) option;
    }
}
