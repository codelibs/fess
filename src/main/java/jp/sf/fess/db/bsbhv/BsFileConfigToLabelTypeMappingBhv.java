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
import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.dbmeta.DBMeta;
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
 *     LABEL_TYPE, FILE_CRAWLING_CONFIG
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     labelType, fileCrawlingConfig
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
    /** @return The instance of DBMeta. (NotNull) */
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
    public Entity newEntity() {
        return newMyEntity();
    }

    /** {@inheritDoc} */
    @Override
    public ConditionBean newConditionBean() {
        return newMyConditionBean();
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
     * int count = fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final FileConfigToLabelTypeMappingCB cb) {
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
        return selectCount(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * <pre>
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (fileConfigToLabelTypeMapping != null) {
     *     ... = fileConfigToLabelTypeMapping.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToLabelTypeMapping selectEntity(
            final FileConfigToLabelTypeMappingCB cb) {
        return doSelectEntity(cb, FileConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ENTITY doSelectEntity(
            final FileConfigToLabelTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, FileConfigToLabelTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToLabelTypeMappingCB cb,
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
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = fileConfigToLabelTypeMapping.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileConfigToLabelTypeMapping selectEntityWithDeletedCheck(
            final FileConfigToLabelTypeMappingCB cb) {
        return doSelectEntityWithDeletedCheck(cb,
                FileConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ENTITY doSelectEntityWithDeletedCheck(
            final FileConfigToLabelTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, FileConfigToLabelTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToLabelTypeMappingCB cb,
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
    public FileConfigToLabelTypeMapping selectByPKValue(final Long id) {
        return doSelectByPKValue(id, FileConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ENTITY doSelectByPKValue(
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
    public FileConfigToLabelTypeMapping selectByPKValueWithDeletedCheck(
            final Long id) {
        return doSelectByPKValueWithDeletedCheck(id,
                FileConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private FileConfigToLabelTypeMappingCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final FileConfigToLabelTypeMappingCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
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
     * ListResultBean&lt;FileConfigToLabelTypeMapping&gt; fileConfigToLabelTypeMappingList = fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping : fileConfigToLabelTypeMappingList) {
     *     ... = fileConfigToLabelTypeMapping.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<FileConfigToLabelTypeMapping> selectList(
            final FileConfigToLabelTypeMappingCB cb) {
        return doSelectList(cb, FileConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> ListResultBean<ENTITY> doSelectList(
            final FileConfigToLabelTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
                cb,
                entityType,
                new InternalSelectListCallback<ENTITY, FileConfigToLabelTypeMappingCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToLabelTypeMappingCB cb,
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
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;FileConfigToLabelTypeMapping&gt; page = fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectPage</span>(cb);
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
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<FileConfigToLabelTypeMapping> selectPage(
            final FileConfigToLabelTypeMappingCB cb) {
        return doSelectPage(cb, FileConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> PagingResultBean<ENTITY> doSelectPage(
            final FileConfigToLabelTypeMappingCB cb,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
                cb,
                entityType,
                new InternalSelectPageCallback<ENTITY, FileConfigToLabelTypeMappingCB>() {
                    @Override
                    public int callbackSelectCount(
                            final FileConfigToLabelTypeMappingCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToLabelTypeMappingCB cb,
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
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;FileConfigToLabelTypeMapping&gt;() {
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
        doSelectCursor(cb, entityRowHandler, FileConfigToLabelTypeMapping.class);
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> void doSelectCursor(
            final FileConfigToLabelTypeMappingCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<FileConfigToLabelTypeMapping>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, FileConfigToLabelTypeMappingCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final FileConfigToLabelTypeMappingCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileConfigToLabelTypeMappingCB cb,
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
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(FileConfigToLabelTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<FileConfigToLabelTypeMappingCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends FileConfigToLabelTypeMappingCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * Pull out the list of foreign table 'LabelType'.
     * @param fileConfigToLabelTypeMappingList The list of fileConfigToLabelTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<LabelType> pulloutLabelType(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return helpPulloutInternally(
                fileConfigToLabelTypeMappingList,
                new InternalPulloutCallback<FileConfigToLabelTypeMapping, LabelType>() {
                    @Override
                    public LabelType getFr(final FileConfigToLabelTypeMapping e) {
                        return e.getLabelType();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final LabelType e,
                            final List<FileConfigToLabelTypeMapping> ls) {
                        e.setFileConfigToLabelTypeMappingList(ls);
                    }
                });
    }

    /**
     * Pull out the list of foreign table 'FileCrawlingConfig'.
     * @param fileConfigToLabelTypeMappingList The list of fileConfigToLabelTypeMapping. (NotNull, EmptyAllowed)
     * @return The list of foreign table. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<FileCrawlingConfig> pulloutFileCrawlingConfig(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return helpPulloutInternally(
                fileConfigToLabelTypeMappingList,
                new InternalPulloutCallback<FileConfigToLabelTypeMapping, FileCrawlingConfig>() {
                    @Override
                    public FileCrawlingConfig getFr(
                            final FileConfigToLabelTypeMapping e) {
                        return e.getFileCrawlingConfig();
                    }

                    @Override
                    public boolean hasRf() {
                        return true;
                    }

                    @Override
                    public void setRfLs(final FileCrawlingConfig e,
                            final List<FileConfigToLabelTypeMapping> ls) {
                        e.setFileConfigToLabelTypeMappingList(ls);
                    }
                });
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
        return helpExtractListInternally(
                fileConfigToLabelTypeMappingList,
                new InternalExtractCallback<FileConfigToLabelTypeMapping, Long>() {
                    @Override
                    public Long getCV(final FileConfigToLabelTypeMapping e) {
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
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileConfigToLabelTypeMapping.setFoo...(value);
     * fileConfigToLabelTypeMapping.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.set...;</span>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">insert</span>(fileConfigToLabelTypeMapping);
     * ... = fileConfigToLabelTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping) {
        doInsert(fileConfigToLabelTypeMapping, null);
    }

    protected void doInsert(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final InsertOption<FileConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToLabelTypeMapping",
                fileConfigToLabelTypeMapping);
        prepareInsertOption(option);
        delegateInsert(fileConfigToLabelTypeMapping, option);
    }

    protected void prepareInsertOption(
            final InsertOption<FileConfigToLabelTypeMappingCB> option) {
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
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * fileConfigToLabelTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileConfigToLabelTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileConfigToLabelTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">update</span>(fileConfigToLabelTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping) {
        doUpdate(fileConfigToLabelTypeMapping, null);
    }

    protected void doUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final UpdateOption<FileConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToLabelTypeMapping",
                fileConfigToLabelTypeMapping);
        prepareUpdateOption(option);
        helpUpdateInternally(fileConfigToLabelTypeMapping,
                new InternalUpdateCallback<FileConfigToLabelTypeMapping>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final FileConfigToLabelTypeMapping entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<FileConfigToLabelTypeMappingCB> option) {
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

    protected FileConfigToLabelTypeMappingCB createCBForVaryingUpdate() {
        final FileConfigToLabelTypeMappingCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected FileConfigToLabelTypeMappingCB createCBForSpecifiedUpdate() {
        final FileConfigToLabelTypeMappingCB cb = newMyConditionBean();
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
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, NonExclusiveControl)
     * @param fileConfigToLabelTypeMapping The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping) {
        doInesrtOrUpdate(fileConfigToLabelTypeMapping, null, null);
    }

    protected void doInesrtOrUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final InsertOption<FileConfigToLabelTypeMappingCB> insertOption,
            final UpdateOption<FileConfigToLabelTypeMappingCB> updateOption) {
        helpInsertOrUpdateInternally(
                fileConfigToLabelTypeMapping,
                new InternalInsertOrUpdateCallback<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB>() {
                    @Override
                    public void callbackInsert(
                            final FileConfigToLabelTypeMapping entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(
                            final FileConfigToLabelTypeMapping entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public FileConfigToLabelTypeMappingCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(
                            final FileConfigToLabelTypeMappingCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<FileConfigToLabelTypeMappingCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<FileConfigToLabelTypeMappingCB>()
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
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * fileConfigToLabelTypeMapping.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileConfigToLabelTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">delete</span>(fileConfigToLabelTypeMapping);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping) {
        doDelete(fileConfigToLabelTypeMapping, null);
    }

    protected void doDelete(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final DeleteOption<FileConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToLabelTypeMapping",
                fileConfigToLabelTypeMapping);
        prepareDeleteOption(option);
        helpDeleteInternally(fileConfigToLabelTypeMapping,
                new InternalDeleteCallback<FileConfigToLabelTypeMapping>() {
                    @Override
                    public int callbackDelegateDelete(
                            final FileConfigToLabelTypeMapping entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<FileConfigToLabelTypeMappingCB> option) {
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
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are insert target. (so default constraints are not available in this method)</span> <br />
     * And if the table has an identity, entities after the process don't have incremented values.
     * When you use the (normal) insert(), an entity after the process has an incremented value.
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return doBatchInsert(fileConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchInsert(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final InsertOption<FileConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToLabelTypeMappingList",
                fileConfigToLabelTypeMappingList);
        prepareInsertOption(option);
        return delegateBatchInsert(fileConfigToLabelTypeMappingList, option);
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
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span> <br />
     * So you should the other batchUpdate() method, which you can specify update columns like this:
     * <pre>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(fileConfigToLabelTypeMappingList, new SpecifyQuery<FileConfigToLabelTypeMappingCB>() {
     *     public void specify(FileConfigToLabelTypeMappingCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return doBatchUpdate(fileConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchUpdate(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final UpdateOption<FileConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToLabelTypeMappingList",
                fileConfigToLabelTypeMappingList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(fileConfigToLabelTypeMappingList, option);
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
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistick lock column because they are specified implicitly.
     * <pre>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">batchUpdate</span>(fileConfigToLabelTypeMappingList, new SpecifyQuery<FileConfigToLabelTypeMappingCB>() {
     *     public void specify(FileConfigToLabelTypeMappingCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final SpecifyQuery<FileConfigToLabelTypeMappingCB> updateColumnSpec) {
        return doBatchUpdate(fileConfigToLabelTypeMappingList,
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
     * @param fileConfigToLabelTypeMappingList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList) {
        return doBatchDelete(fileConfigToLabelTypeMappingList, null);
    }

    protected int[] doBatchDelete(
            final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList,
            final DeleteOption<FileConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToLabelTypeMappingList",
                fileConfigToLabelTypeMappingList);
        prepareDeleteOption(option);
        return delegateBatchDelete(fileConfigToLabelTypeMappingList, option);
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
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB&gt;() {
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
            final QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB> setupper,
            final InsertOption<FileConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final FileConfigToLabelTypeMapping entity = new FileConfigToLabelTypeMapping();
        final FileConfigToLabelTypeMappingCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected FileConfigToLabelTypeMappingCB createCBForQueryInsert() {
        final FileConfigToLabelTypeMappingCB cb = newMyConditionBean();
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
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setPK...(value);</span>
     * fileConfigToLabelTypeMapping.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setVersionNo(value);</span>
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">queryUpdate</span>(fileConfigToLabelTypeMapping, cb);
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final FileConfigToLabelTypeMappingCB cb) {
        return doQueryUpdate(fileConfigToLabelTypeMapping, cb, null);
    }

    protected int doQueryUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final FileConfigToLabelTypeMappingCB cb,
            final UpdateOption<FileConfigToLabelTypeMappingCB> option) {
        assertObjectNotNull("fileConfigToLabelTypeMapping",
                fileConfigToLabelTypeMapping);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                fileConfigToLabelTypeMapping, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity),
                    (FileConfigToLabelTypeMappingCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (FileConfigToLabelTypeMappingCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">queryDelete</span>(fileConfigToLabelTypeMapping, cb);
     * </pre>
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final FileConfigToLabelTypeMappingCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final FileConfigToLabelTypeMappingCB cb,
            final DeleteOption<FileConfigToLabelTypeMappingCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((FileConfigToLabelTypeMappingCB) cb);
        } else {
            return varyingQueryDelete((FileConfigToLabelTypeMappingCB) cb,
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
     * FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileConfigToLabelTypeMapping.setFoo...(value);
     * fileConfigToLabelTypeMapping.setBar...(value);
     * InsertOption<FileConfigToLabelTypeMappingCB> option = new InsertOption<FileConfigToLabelTypeMappingCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">varyingInsert</span>(fileConfigToLabelTypeMapping, option);
     * ... = fileConfigToLabelTypeMapping.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileConfigToLabelTypeMapping.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;FileConfigToLabelTypeMappingCB&gt; option = new UpdateOption&lt;FileConfigToLabelTypeMappingCB&gt;();
     *     option.self(new SpecifyQuery&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *         public void specify(FileConfigToLabelTypeMappingCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">varyingUpdate</span>(fileConfigToLabelTypeMapping, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * @param fileConfigToLabelTypeMapping The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping,
            final InsertOption<FileConfigToLabelTypeMappingCB> insertOption,
            final UpdateOption<FileConfigToLabelTypeMappingCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(fileConfigToLabelTypeMapping, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param fileConfigToLabelTypeMapping The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileConfigToLabelTypeMapping.setVersionNo(value);</span>
     * FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;FileConfigToLabelTypeMappingCB&gt; option = new UpdateOption&lt;FileConfigToLabelTypeMappingCB&gt;();
     * option.self(new SpecifyQuery&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void specify(FileConfigToLabelTypeMappingCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileConfigToLabelTypeMappingBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(fileConfigToLabelTypeMapping, cb, option);
     * </pre>
     * @param fileConfigToLabelTypeMapping The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of FileConfigToLabelTypeMapping. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
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
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
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
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(
            final FileConfigToLabelTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(
            final FileConfigToLabelTypeMappingCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> void delegateSelectCursor(
            final FileConfigToLabelTypeMappingCB cb,
            final EntityRowHandler<ENTITY> erh, final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends FileConfigToLabelTypeMapping> List<ENTITY> delegateSelectList(
            final FileConfigToLabelTypeMappingCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final FileConfigToLabelTypeMapping e,
            final InsertOption<FileConfigToLabelTypeMappingCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final FileConfigToLabelTypeMapping e,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final FileConfigToLabelTypeMapping e,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final FileConfigToLabelTypeMapping e,
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final FileConfigToLabelTypeMapping e,
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(
            final List<FileConfigToLabelTypeMapping> ls,
            final InsertOption<FileConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(
            final List<FileConfigToLabelTypeMapping> ls,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<FileConfigToLabelTypeMapping> ls,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(
            final List<FileConfigToLabelTypeMapping> ls,
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<FileConfigToLabelTypeMapping> ls,
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final FileConfigToLabelTypeMapping e,
            final FileConfigToLabelTypeMappingCB inCB,
            final ConditionBean resCB,
            final InsertOption<FileConfigToLabelTypeMappingCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final FileConfigToLabelTypeMapping e,
            final FileConfigToLabelTypeMappingCB cb,
            final UpdateOption<FileConfigToLabelTypeMappingCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final FileConfigToLabelTypeMappingCB cb,
            final DeleteOption<FileConfigToLabelTypeMappingCB> op) {
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
    protected FileConfigToLabelTypeMapping downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity,
                FileConfigToLabelTypeMapping.class);
    }

    protected FileConfigToLabelTypeMappingCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                FileConfigToLabelTypeMappingCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<FileConfigToLabelTypeMapping> downcast(
            final List<? extends Entity> entityList) {
        return (List<FileConfigToLabelTypeMapping>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<FileConfigToLabelTypeMappingCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<FileConfigToLabelTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<FileConfigToLabelTypeMappingCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<FileConfigToLabelTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<FileConfigToLabelTypeMappingCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<FileConfigToLabelTypeMappingCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<FileConfigToLabelTypeMapping, FileConfigToLabelTypeMappingCB>) option;
    }
}
