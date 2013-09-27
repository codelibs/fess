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

import jp.sf.fess.db.bsentity.dbmeta.FileConfigToBrowserTypeMappingDbm;
import jp.sf.fess.db.cbean.FileConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.exbhv.FileConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exentity.BrowserType;
import jp.sf.fess.db.exentity.FileConfigToBrowserTypeMapping;
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
 * The behavior of FILE_CONFIG_TO_BROWSER_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, FILE_CONFIG_ID, BROWSER_TYPE_ID
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
 *     FILE_CRAWLING_CONFIG, BROWSER_TYPE
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     fileCrawlingConfig, browserType
 * 
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsFileConfigToBrowserTypeMappingBhv extends
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
        return "FILE_CONFIG_TO_BROWSER_TYPE_MAPPING";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return FileConfigToBrowserTypeMappingDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public FileConfigToBrowserTypeMappingDbm getMyDBMeta() {
        return FileConfigToBrowserTypeMappingDbm.getInstance();
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
    public FileConfigToBrowserTypeMapping newMyEntity() {
        return new FileConfigToBrowserTypeMapping();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public FileConfigToBrowserTypeMappingCB newMyConditionBean() {
        return new FileConfigToBrowserTypeMappingCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * int count = fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final FileConfigToBrowserTypeMappingCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(
            final FileConfigToBrowserTypeMappingCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final FileConfigToBrowserTypeMappingCB cb) { // called by selectPage(cb)
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
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (fileConfigToBrowserTypeMapping != null) {
     *     ... = fileConfigToBrowserTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToBrowserTypeMapping selectEntity(
            final FileConfigToBrowserTypeMappingCB cb) {
        return doSelectEntity(cb, FileConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> ENTITY doSelectEntity(
            final FileConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, FileConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToBrowserTypeMappingCB cb,
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
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = fileConfigToBrowserTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToBrowserTypeMapping selectEntityWithDeletedCheck(
            final FileConfigToBrowserTypeMappingCB cb) {
        return doSelectEntityWithDeletedCheck(cb,
                FileConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> ENTITY doSelectEntityWithDeletedCheck(
            final FileConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, FileConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToBrowserTypeMappingCB cb,
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
    public FileConfigToBrowserTypeMapping selectByPKValue(final Long id) {
        return doSelectByPKValue(id, FileConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> ENTITY doSelectByPKValue(
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
    public FileConfigToBrowserTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKValueWithDeletedCheck(id,
                FileConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private FileConfigToBrowserTypeMappingCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final FileConfigToBrowserTypeMappingCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;FileConfigToBrowserTypeMapping&gt; fileConfigToBrowserTypeMappingList = fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping : fileConfigToBrowserTypeMappingList) {
     *     ... = fileConfigToBrowserTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<FileConfigToBrowserTypeMapping> selectList(
            final FileConfigToBrowserTypeMappingCB cb) {
        return doSelectList(cb, FileConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> ListResultBean<ENTITY> doSelectList(
            final FileConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
                cb,
                entityType,
                new InternalSelectListCallback<ENTITY, FileConfigToBrowserTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToBrowserTypeMappingCB cb,
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
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;FileConfigToBrowserTypeMapping&gt; page = fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping : page) {
     *     ... = fileConfigToBrowserTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<FileConfigToBrowserTypeMapping> selectPage(
            final FileConfigToBrowserTypeMappingCB cb) {
        return doSelectPage(cb, FileConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> PagingResultBean<ENTITY> doSelectPage(
            final FileConfigToBrowserTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
                cb,
                entityType,
                new InternalSelectPageCallback<ENTITY, FileConfigToBrowserTypeMappingCB>() {
                    @Override
                    public int callbackSelectCount(
                            final FileConfigToBrowserTypeMappingCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToBrowserTypeMappingCB cb,
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
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;FileConfigToBrowserTypeMapping&gt;() {
     *     public void handle(FileConfigToBrowserTypeMapping entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @param entityRowHandler The handler of entity row of FileConfigToBrowserTypeMapping. (NotNull)
     */
    public void selectCursor(
            final FileConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<FileConfigToBrowserTypeMapping> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler,
                FileConfigToBrowserTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> void doSelectCursor(
            final FileConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<FileConfigToBrowserTypeMapping>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, FileConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final FileConfigToBrowserTypeMappingCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToBrowserTypeMappingCB cb,
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
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(FileConfigToBrowserTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<FileConfigToBrowserTypeMappingCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends FileConfigToBrowserTypeMappingCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param fileConfigToBrowserTypeMappingList The list of fileConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<FileCrawlingConfig> pulloutFileCrawlingConfig(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList) {
        return helpPulloutInternally(
                fileConfigToBrowserTypeMappingList,
                new InternalPulloutCallback<FileConfigToBrowserTypeMapping, FileCrawlingConfig>() {
                    @Override
                    public FileCrawlingConfig getFr(
                            final FileConfigToBrowserTypeMapping e) {
                        return e.getFileCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final FileCrawlingConfig e,
                            final List<FileConfigToBrowserTypeMapping> ls) {
                        e.setFileConfigToBrowserTypeMappingList(ls);
                    }
                });
    }

    /**
     * Pull out the list of foreign table 'BrowserType'.
     * @param fileConfigToBrowserTypeMappingList The list of fileConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<BrowserType> pulloutBrowserType(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList) {
        return helpPulloutInternally(
                fileConfigToBrowserTypeMappingList,
                new InternalPulloutCallback<FileConfigToBrowserTypeMapping, BrowserType>() {
                    @Override
                    public BrowserType getFr(
                            final FileConfigToBrowserTypeMapping e) {
                        return e.getBrowserType();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final BrowserType e,
                            final List<FileConfigToBrowserTypeMapping> ls) {
                        e.setFileConfigToBrowserTypeMappingList(ls);
                    }
                });
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param fileConfigToBrowserTypeMappingList The list of fileConfigToBrowserTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList) {
        return helpExtractListInternally(
                fileConfigToBrowserTypeMappingList,
                new InternalExtractCallback<FileConfigToBrowserTypeMapping, Long>() {
                    @Override
                    public Long getCV(final FileConfigToBrowserTypeMapping e) {
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
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = new FileConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileConfigToBrowserTypeMapping.setFoo...(value);
     * fileConfigToBrowserTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.set...;</span>
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">insert</span>(fileConfigToBrowserTypeMapping);
     * ... = fileConfigToBrowserTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileConfigToBrowserTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping) {
        doInsert(fileConfigToBrowserTypeMapping, null);
    }

    protected void doInsert(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final InsertOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToBrowserTypeMapping",
                fileConfigToBrowserTypeMapping);
        prepareInsertOption(option);
        delegateInsert(fileConfigToBrowserTypeMapping, option);
    }

    protected void prepareInsertOption(
            final InsertOption<FileConfigToBrowserTypeMappingCB> option) {
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
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = new FileConfigToBrowserTypeMapping();
     * fileConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileConfigToBrowserTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">update</span>(fileConfigToBrowserTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param fileConfigToBrowserTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping) {
        doUpdate(fileConfigToBrowserTypeMapping, null);
    }

    protected void doUpdate(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToBrowserTypeMapping",
                fileConfigToBrowserTypeMapping);
        prepareUpdateOption(option);
        helpUpdateInternally(fileConfigToBrowserTypeMapping,
                new InternalUpdateCallback<FileConfigToBrowserTypeMapping>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final FileConfigToBrowserTypeMapping entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<FileConfigToBrowserTypeMappingCB> option) {
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

    protected FileConfigToBrowserTypeMappingCB createCBForVaryingUpdate() {
        final FileConfigToBrowserTypeMappingCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected FileConfigToBrowserTypeMappingCB createCBForSpecifiedUpdate() {
        final FileConfigToBrowserTypeMappingCB cb = newMyConditionBean();
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
     * @param fileConfigToBrowserTypeMapping The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping) {
        doInesrtOrUpdate(fileConfigToBrowserTypeMapping, null, null);
    }

    protected void doInesrtOrUpdate(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final InsertOption<FileConfigToBrowserTypeMappingCB> insertOption,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> updateOption) {
        helpInsertOrUpdateInternally(
                fileConfigToBrowserTypeMapping,
                new InternalInsertOrUpdateCallback<FileConfigToBrowserTypeMapping, FileConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void callbackInsert(
                            final FileConfigToBrowserTypeMapping entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(
                            final FileConfigToBrowserTypeMapping entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public FileConfigToBrowserTypeMappingCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(
                            final FileConfigToBrowserTypeMappingCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<FileConfigToBrowserTypeMappingCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<FileConfigToBrowserTypeMappingCB>()
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
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = new FileConfigToBrowserTypeMapping();
     * fileConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">delete</span>(fileConfigToBrowserTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param fileConfigToBrowserTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping) {
        doDelete(fileConfigToBrowserTypeMapping, null);
    }

    protected void doDelete(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToBrowserTypeMapping",
                fileConfigToBrowserTypeMapping);
        prepareDeleteOption(option);
        helpDeleteInternally(fileConfigToBrowserTypeMapping,
                new InternalDeleteCallback<FileConfigToBrowserTypeMapping>() {
                    @Override
                    public int callbackDelegateDelete(
                            final FileConfigToBrowserTypeMapping entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<FileConfigToBrowserTypeMappingCB> option) {
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
     * @param fileConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList) {
        return doBatchInsert(fileConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchInsert(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList,
            final InsertOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToBrowserTypeMappingList",
                fileConfigToBrowserTypeMappingList);
        prepareInsertOption(option);
        return delegateBatchInsert(fileConfigToBrowserTypeMappingList, option);
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
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(fileConfigToBrowserTypeMappingList, new SpecifyQuery<FileConfigToBrowserTypeMappingCB>() {
     *     public void specify(FileConfigToBrowserTypeMappingCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param fileConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList) {
        return doBatchUpdate(fileConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchUpdate(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToBrowserTypeMappingList",
                fileConfigToBrowserTypeMappingList);
        prepareBatchUpdateOption(fileConfigToBrowserTypeMappingList, option);
        return delegateBatchUpdate(fileConfigToBrowserTypeMappingList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(fileConfigToBrowserTypeMappingList);
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
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(fileConfigToBrowserTypeMappingList, new SpecifyQuery<FileConfigToBrowserTypeMappingCB>() {
     *     public void specify(FileConfigToBrowserTypeMappingCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(fileConfigToBrowserTypeMappingList, new SpecifyQuery<FileConfigToBrowserTypeMappingCB>() {
     *     public void specify(FileConfigToBrowserTypeMappingCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param fileConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList,
            final SpecifyQuery<FileConfigToBrowserTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(fileConfigToBrowserTypeMappingList,
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
     * @param fileConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList) {
        return doBatchDelete(fileConfigToBrowserTypeMappingList, null);
    }

    protected int[] doBatchDelete(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToBrowserTypeMappingList",
                fileConfigToBrowserTypeMappingList);
        prepareDeleteOption(option);
        return delegateBatchDelete(fileConfigToBrowserTypeMappingList, option);
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
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;FileConfigToBrowserTypeMapping, FileConfigToBrowserTypeMappingCB&gt;() {
     *     public ConditionBean setup(fileConfigToBrowserTypeMapping entity, FileConfigToBrowserTypeMappingCB intoCB) {
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
            final QueryInsertSetupper<FileConfigToBrowserTypeMapping, FileConfigToBrowserTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<FileConfigToBrowserTypeMapping, FileConfigToBrowserTypeMappingCB> setupper,
            final InsertOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final FileConfigToBrowserTypeMapping entity = new FileConfigToBrowserTypeMapping();
        final FileConfigToBrowserTypeMappingCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected FileConfigToBrowserTypeMappingCB createCBForQueryInsert() {
        final FileConfigToBrowserTypeMappingCB cb = newMyConditionBean();
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
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = new FileConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.setPK...(value);</span>
     * fileConfigToBrowserTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.setVersionNo(value);</span>
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryUpdate</span>(fileConfigToBrowserTypeMapping, cb);
     * </pre>
     * @param fileConfigToBrowserTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final FileConfigToBrowserTypeMappingCB cb) {
        return doQueryUpdate(fileConfigToBrowserTypeMapping, cb, null);
    }

    protected int doQueryUpdate(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final FileConfigToBrowserTypeMappingCB cb,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToBrowserTypeMapping",
                fileConfigToBrowserTypeMapping);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                fileConfigToBrowserTypeMapping, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity),
                    (FileConfigToBrowserTypeMappingCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (FileConfigToBrowserTypeMappingCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">queryDelete</span>(fileConfigToBrowserTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final FileConfigToBrowserTypeMappingCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final FileConfigToBrowserTypeMappingCB cb,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((FileConfigToBrowserTypeMappingCB) cb);
        } else {
            return varyingQueryDelete((FileConfigToBrowserTypeMappingCB) cb,
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
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = new FileConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileConfigToBrowserTypeMapping.setFoo...(value);
     * fileConfigToBrowserTypeMapping.setBar...(value);
     * InsertOption<FileConfigToBrowserTypeMappingCB> option = new InsertOption<FileConfigToBrowserTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingInsert</span>(fileConfigToBrowserTypeMapping, option);
     * ... = fileConfigToBrowserTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileConfigToBrowserTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final InsertOption<FileConfigToBrowserTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(fileConfigToBrowserTypeMapping, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = new FileConfigToBrowserTypeMapping();
     * fileConfigToBrowserTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileConfigToBrowserTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileConfigToBrowserTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;FileConfigToBrowserTypeMappingCB&gt; option = new UpdateOption&lt;FileConfigToBrowserTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *         public void specify(FileConfigToBrowserTypeMappingCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingUpdate</span>(fileConfigToBrowserTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileConfigToBrowserTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(fileConfigToBrowserTypeMapping, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param fileConfigToBrowserTypeMapping The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final InsertOption<FileConfigToBrowserTypeMappingCB> insertOption,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(fileConfigToBrowserTypeMapping, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param fileConfigToBrowserTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(fileConfigToBrowserTypeMapping, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param fileConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList,
            final InsertOption<FileConfigToBrowserTypeMappingCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(fileConfigToBrowserTypeMappingList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param fileConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(fileConfigToBrowserTypeMappingList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param fileConfigToBrowserTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(fileConfigToBrowserTypeMappingList, option);
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
            final QueryInsertSetupper<FileConfigToBrowserTypeMapping, FileConfigToBrowserTypeMappingCB> setupper,
            final InsertOption<FileConfigToBrowserTypeMappingCB> option) {
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
     * FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping = new FileConfigToBrowserTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.setPK...(value);</span>
     * fileConfigToBrowserTypeMapping.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileConfigToBrowserTypeMapping.setVersionNo(value);</span>
     * FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;FileConfigToBrowserTypeMappingCB&gt; option = new UpdateOption&lt;FileConfigToBrowserTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *     public void specify(FileConfigToBrowserTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileConfigToBrowserTypeMappingBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(fileConfigToBrowserTypeMapping, cb, option);
     * </pre>
     * @param fileConfigToBrowserTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(
            final FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping,
            final FileConfigToBrowserTypeMappingCB cb,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(fileConfigToBrowserTypeMapping, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of FileConfigToBrowserTypeMapping. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final FileConfigToBrowserTypeMappingCB cb,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> option) {
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
    public OutsideSqlBasicExecutor<FileConfigToBrowserTypeMappingBhv> outsideSql() {
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
            final FileConfigToBrowserTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(
            final FileConfigToBrowserTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> void delegateSelectCursor(
            final FileConfigToBrowserTypeMappingCB cb,
            final EntityRowHandler<ENTITY> erh, final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends FileConfigToBrowserTypeMapping> List<ENTITY> delegateSelectList(
            final FileConfigToBrowserTypeMappingCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final FileConfigToBrowserTypeMapping e,
            final InsertOption<FileConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final FileConfigToBrowserTypeMapping e,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(
            final FileConfigToBrowserTypeMapping e,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final FileConfigToBrowserTypeMapping e,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(
            final FileConfigToBrowserTypeMapping e,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(
            final List<FileConfigToBrowserTypeMapping> ls,
            final InsertOption<FileConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(
            final List<FileConfigToBrowserTypeMapping> ls,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<FileConfigToBrowserTypeMapping> ls,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(
            final List<FileConfigToBrowserTypeMapping> ls,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<FileConfigToBrowserTypeMapping> ls,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final FileConfigToBrowserTypeMapping e,
            final FileConfigToBrowserTypeMappingCB inCB,
            final ConditionBean resCB,
            final InsertOption<FileConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final FileConfigToBrowserTypeMapping e,
            final FileConfigToBrowserTypeMappingCB cb,
            final UpdateOption<FileConfigToBrowserTypeMappingCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(
            final FileConfigToBrowserTypeMappingCB cb,
            final DeleteOption<FileConfigToBrowserTypeMappingCB> op) {
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
    protected FileConfigToBrowserTypeMapping downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity,
                FileConfigToBrowserTypeMapping.class);
    }

    protected FileConfigToBrowserTypeMappingCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                FileConfigToBrowserTypeMappingCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<FileConfigToBrowserTypeMapping> downcast(
            final List<? extends Entity> entityList) {
        return (List<FileConfigToBrowserTypeMapping>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<FileConfigToBrowserTypeMappingCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<FileConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<FileConfigToBrowserTypeMappingCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<FileConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<FileConfigToBrowserTypeMappingCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<FileConfigToBrowserTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<FileConfigToBrowserTypeMapping, FileConfigToBrowserTypeMappingCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<FileConfigToBrowserTypeMapping, FileConfigToBrowserTypeMappingCB>) option;
    }
}
