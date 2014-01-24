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

import jp.sf.fess.db.bsentity.dbmeta.FileCrawlingConfigDbm;
import jp.sf.fess.db.cbean.FileAuthenticationCB;
import jp.sf.fess.db.cbean.FileConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.exbhv.FileAuthenticationBhv;
import jp.sf.fess.db.exbhv.FileConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileCrawlingConfigBhv;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.db.exentity.FileConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.AbstractBehaviorWritable;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.InsertOption;
import org.seasar.dbflute.bhv.LoadReferrerOption;
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
 *     FILE_AUTHENTICATION, FILE_CONFIG_TO_BROWSER_TYPE_MAPPING, FILE_CONFIG_TO_LABEL_TYPE_MAPPING, FILE_CONFIG_TO_ROLE_TYPE_MAPPING
 * 
 * [foreign property]
 *     
 * 
 * [referrer property]
 *     fileAuthenticationList, fileConfigToBrowserTypeMappingList, fileConfigToLabelTypeMappingList, fileConfigToRoleTypeMappingList
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
    /** @return The instance of DBMeta. (NotNull) */
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
    public Entity newEntity() {
        return newMyEntity();
    }

    /** {@inheritDoc} */
    @Override
    public ConditionBean newConditionBean() {
        return newMyConditionBean();
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
     * int count = fileCrawlingConfigBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final FileCrawlingConfigCB cb) {
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
        return selectCount(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * <pre>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (fileCrawlingConfig != null) {
     *     ... = fileCrawlingConfig.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileCrawlingConfig selectEntity(final FileCrawlingConfigCB cb) {
        return doSelectEntity(cb, FileCrawlingConfig.class);
    }

    protected <ENTITY extends FileCrawlingConfig> ENTITY doSelectEntity(
            final FileCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, FileCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileCrawlingConfigCB cb,
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
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = fileCrawlingConfig.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public FileCrawlingConfig selectEntityWithDeletedCheck(
            final FileCrawlingConfigCB cb) {
        return doSelectEntityWithDeletedCheck(cb, FileCrawlingConfig.class);
    }

    protected <ENTITY extends FileCrawlingConfig> ENTITY doSelectEntityWithDeletedCheck(
            final FileCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, FileCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileCrawlingConfigCB cb,
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
    public FileCrawlingConfig selectByPKValue(final Long id) {
        return doSelectByPKValue(id, FileCrawlingConfig.class);
    }

    protected <ENTITY extends FileCrawlingConfig> ENTITY doSelectByPKValue(
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
    public FileCrawlingConfig selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, FileCrawlingConfig.class);
    }

    protected <ENTITY extends FileCrawlingConfig> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private FileCrawlingConfigCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final FileCrawlingConfigCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
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
     * ListResultBean&lt;FileCrawlingConfig&gt; fileCrawlingConfigList = fileCrawlingConfigBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.get...();
     * }
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<FileCrawlingConfig> selectList(
            final FileCrawlingConfigCB cb) {
        return doSelectList(cb, FileCrawlingConfig.class);
    }

    protected <ENTITY extends FileCrawlingConfig> ListResultBean<ENTITY> doSelectList(
            final FileCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, FileCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileCrawlingConfigCB cb,
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
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;FileCrawlingConfig&gt; page = fileCrawlingConfigBhv.<span style="color: #FD4747">selectPage</span>(cb);
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
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<FileCrawlingConfig> selectPage(
            final FileCrawlingConfigCB cb) {
        return doSelectPage(cb, FileCrawlingConfig.class);
    }

    protected <ENTITY extends FileCrawlingConfig> PagingResultBean<ENTITY> doSelectPage(
            final FileCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, FileCrawlingConfigCB>() {
                    @Override
                    public int callbackSelectCount(final FileCrawlingConfigCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileCrawlingConfigCB cb,
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
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * fileCrawlingConfigBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;FileCrawlingConfig&gt;() {
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
        doSelectCursor(cb, entityRowHandler, FileCrawlingConfig.class);
    }

    protected <ENTITY extends FileCrawlingConfig> void doSelectCursor(
            final FileCrawlingConfigCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<FileCrawlingConfig>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, FileCrawlingConfigCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final FileCrawlingConfigCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final FileCrawlingConfigCB cb,
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
     * fileCrawlingConfigBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(FileCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<FileCrawlingConfigCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends FileCrawlingConfigCB> SLFunction<CB, RESULT> doScalarSelect(
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
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileAuthenticationList(
            final FileCrawlingConfig fileCrawlingConfig,
            final ConditionBeanSetupper<FileAuthenticationCB> conditionBeanSetupper) {
        xassLRArg(fileCrawlingConfig, conditionBeanSetupper);
        loadFileAuthenticationList(xnewLRLs(fileCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of fileAuthenticationList with the set-upper for condition-bean of referrer. <br />
     * FILE_AUTHENTICATION by FILE_CRAWLING_CONFIG_ID, named 'fileAuthenticationList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">loadFileAuthenticationList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileAuthenticationCB&gt;() {
     *     public void setup(FileAuthenticationCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.<span style="color: #FD4747">getFileAuthenticationList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setFileCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileCrawlingConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileAuthenticationList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final ConditionBeanSetupper<FileAuthenticationCB> conditionBeanSetupper) {
        xassLRArg(fileCrawlingConfigList, conditionBeanSetupper);
        loadFileAuthenticationList(
                fileCrawlingConfigList,
                new LoadReferrerOption<FileAuthenticationCB, FileAuthentication>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileAuthenticationList(
            final FileCrawlingConfig fileCrawlingConfig,
            final LoadReferrerOption<FileAuthenticationCB, FileAuthentication> loadReferrerOption) {
        xassLRArg(fileCrawlingConfig, loadReferrerOption);
        loadFileAuthenticationList(xnewLRLs(fileCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileAuthenticationList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileAuthenticationCB, FileAuthentication> loadReferrerOption) {
        xassLRArg(fileCrawlingConfigList, loadReferrerOption);
        if (fileCrawlingConfigList.isEmpty()) {
            return;
        }
        final FileAuthenticationBhv referrerBhv = xgetBSFLR().select(
                FileAuthenticationBhv.class);
        helpLoadReferrerInternally(
                fileCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<FileCrawlingConfig, Long, FileAuthenticationCB, FileAuthentication>() {
                    @Override
                    public Long getPKVal(final FileCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final FileCrawlingConfig e,
                            final List<FileAuthentication> ls) {
                        e.setFileAuthenticationList(ls);
                    }

                    @Override
                    public FileAuthenticationCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final FileAuthenticationCB cb,
                            final List<Long> ls) {
                        cb.query().setFileCrawlingConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final FileAuthenticationCB cb) {
                        cb.query().addOrderBy_FileCrawlingConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final FileAuthenticationCB cb) {
                        cb.specify().columnFileCrawlingConfigId();
                    }

                    @Override
                    public List<FileAuthentication> selRfLs(
                            final FileAuthenticationCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final FileAuthentication e) {
                        return e.getFileCrawlingConfigId();
                    }

                    @Override
                    public void setlcEt(final FileAuthentication re,
                            final FileCrawlingConfig le) {
                        re.setFileCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "fileAuthenticationList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToBrowserTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final ConditionBeanSetupper<FileConfigToBrowserTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(fileCrawlingConfig, conditionBeanSetupper);
        loadFileConfigToBrowserTypeMappingList(xnewLRLs(fileCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of fileConfigToBrowserTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * FILE_CONFIG_TO_BROWSER_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToBrowserTypeMappingList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">loadFileConfigToBrowserTypeMappingList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *     public void setup(FileConfigToBrowserTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.<span style="color: #FD4747">getFileConfigToBrowserTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setFileConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToBrowserTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final ConditionBeanSetupper<FileConfigToBrowserTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(fileCrawlingConfigList, conditionBeanSetupper);
        loadFileConfigToBrowserTypeMappingList(
                fileCrawlingConfigList,
                new LoadReferrerOption<FileConfigToBrowserTypeMappingCB, FileConfigToBrowserTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToBrowserTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final LoadReferrerOption<FileConfigToBrowserTypeMappingCB, FileConfigToBrowserTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfig, loadReferrerOption);
        loadFileConfigToBrowserTypeMappingList(xnewLRLs(fileCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToBrowserTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileConfigToBrowserTypeMappingCB, FileConfigToBrowserTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfigList, loadReferrerOption);
        if (fileCrawlingConfigList.isEmpty()) {
            return;
        }
        final FileConfigToBrowserTypeMappingBhv referrerBhv = xgetBSFLR()
                .select(FileConfigToBrowserTypeMappingBhv.class);
        helpLoadReferrerInternally(
                fileCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<FileCrawlingConfig, Long, FileConfigToBrowserTypeMappingCB, FileConfigToBrowserTypeMapping>() {
                    @Override
                    public Long getPKVal(final FileCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final FileCrawlingConfig e,
                            final List<FileConfigToBrowserTypeMapping> ls) {
                        e.setFileConfigToBrowserTypeMappingList(ls);
                    }

                    @Override
                    public FileConfigToBrowserTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(
                            final FileConfigToBrowserTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setFileConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(
                            final FileConfigToBrowserTypeMappingCB cb) {
                        cb.query().addOrderBy_FileConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(
                            final FileConfigToBrowserTypeMappingCB cb) {
                        cb.specify().columnFileConfigId();
                    }

                    @Override
                    public List<FileConfigToBrowserTypeMapping> selRfLs(
                            final FileConfigToBrowserTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final FileConfigToBrowserTypeMapping e) {
                        return e.getFileConfigId();
                    }

                    @Override
                    public void setlcEt(
                            final FileConfigToBrowserTypeMapping re,
                            final FileCrawlingConfig le) {
                        re.setFileCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "fileConfigToBrowserTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToLabelTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(fileCrawlingConfig, conditionBeanSetupper);
        loadFileConfigToLabelTypeMappingList(xnewLRLs(fileCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of fileConfigToLabelTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToLabelTypeMappingList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">loadFileConfigToLabelTypeMappingList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(FileConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.<span style="color: #FD4747">getFileConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setFileConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToLabelTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(fileCrawlingConfigList, conditionBeanSetupper);
        loadFileConfigToLabelTypeMappingList(
                fileCrawlingConfigList,
                new LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToLabelTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfig, loadReferrerOption);
        loadFileConfigToLabelTypeMappingList(xnewLRLs(fileCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToLabelTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfigList, loadReferrerOption);
        if (fileCrawlingConfigList.isEmpty()) {
            return;
        }
        final FileConfigToLabelTypeMappingBhv referrerBhv = xgetBSFLR().select(
                FileConfigToLabelTypeMappingBhv.class);
        helpLoadReferrerInternally(
                fileCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<FileCrawlingConfig, Long, FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping>() {
                    @Override
                    public Long getPKVal(final FileCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final FileCrawlingConfig e,
                            final List<FileConfigToLabelTypeMapping> ls) {
                        e.setFileConfigToLabelTypeMappingList(ls);
                    }

                    @Override
                    public FileConfigToLabelTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final FileConfigToLabelTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setFileConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(
                            final FileConfigToLabelTypeMappingCB cb) {
                        cb.query().addOrderBy_FileConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final FileConfigToLabelTypeMappingCB cb) {
                        cb.specify().columnFileConfigId();
                    }

                    @Override
                    public List<FileConfigToLabelTypeMapping> selRfLs(
                            final FileConfigToLabelTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final FileConfigToLabelTypeMapping e) {
                        return e.getFileConfigId();
                    }

                    @Override
                    public void setlcEt(final FileConfigToLabelTypeMapping re,
                            final FileCrawlingConfig le) {
                        re.setFileCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "fileConfigToLabelTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToRoleTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(fileCrawlingConfig, conditionBeanSetupper);
        loadFileConfigToRoleTypeMappingList(xnewLRLs(fileCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of fileConfigToRoleTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by FILE_CONFIG_ID, named 'fileConfigToRoleTypeMappingList'.
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">loadFileConfigToRoleTypeMappingList</span>(fileCrawlingConfigList, new ConditionBeanSetupper&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(FileConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
     *     ... = fileCrawlingConfig.<span style="color: #FD4747">getFileConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setFileConfigId_InScope(pkList);
     * cb.query().addOrderBy_FileConfigId_Asc();
     * </pre>
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToRoleTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(fileCrawlingConfigList, conditionBeanSetupper);
        loadFileConfigToRoleTypeMappingList(
                fileCrawlingConfigList,
                new LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param fileCrawlingConfig The entity of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToRoleTypeMappingList(
            final FileCrawlingConfig fileCrawlingConfig,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfig, loadReferrerOption);
        loadFileConfigToRoleTypeMappingList(xnewLRLs(fileCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param fileCrawlingConfigList The entity list of fileCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToRoleTypeMappingList(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final LoadReferrerOption<FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(fileCrawlingConfigList, loadReferrerOption);
        if (fileCrawlingConfigList.isEmpty()) {
            return;
        }
        final FileConfigToRoleTypeMappingBhv referrerBhv = xgetBSFLR().select(
                FileConfigToRoleTypeMappingBhv.class);
        helpLoadReferrerInternally(
                fileCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<FileCrawlingConfig, Long, FileConfigToRoleTypeMappingCB, FileConfigToRoleTypeMapping>() {
                    @Override
                    public Long getPKVal(final FileCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final FileCrawlingConfig e,
                            final List<FileConfigToRoleTypeMapping> ls) {
                        e.setFileConfigToRoleTypeMappingList(ls);
                    }

                    @Override
                    public FileConfigToRoleTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final FileConfigToRoleTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setFileConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final FileConfigToRoleTypeMappingCB cb) {
                        cb.query().addOrderBy_FileConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final FileConfigToRoleTypeMappingCB cb) {
                        cb.specify().columnFileConfigId();
                    }

                    @Override
                    public List<FileConfigToRoleTypeMapping> selRfLs(
                            final FileConfigToRoleTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final FileConfigToRoleTypeMapping e) {
                        return e.getFileConfigId();
                    }

                    @Override
                    public void setlcEt(final FileConfigToRoleTypeMapping re,
                            final FileCrawlingConfig le) {
                        re.setFileCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "fileConfigToRoleTypeMappingList";
                    }
                });
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
        return helpExtractListInternally(fileCrawlingConfigList,
                new InternalExtractCallback<FileCrawlingConfig, Long>() {
                    @Override
                    public Long getCV(final FileCrawlingConfig e) {
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
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileCrawlingConfig.setFoo...(value);
     * fileCrawlingConfig.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.set...;</span>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">insert</span>(fileCrawlingConfig);
     * ... = fileCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileCrawlingConfig The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final FileCrawlingConfig fileCrawlingConfig) {
        doInsert(fileCrawlingConfig, null);
    }

    protected void doInsert(final FileCrawlingConfig fileCrawlingConfig,
            final InsertOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfig", fileCrawlingConfig);
        prepareInsertOption(option);
        delegateInsert(fileCrawlingConfig, option);
    }

    protected void prepareInsertOption(
            final InsertOption<FileCrawlingConfigCB> option) {
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
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     fileCrawlingConfigBhv.<span style="color: #FD4747">update</span>(fileCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param fileCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final FileCrawlingConfig fileCrawlingConfig) {
        doUpdate(fileCrawlingConfig, null);
    }

    protected void doUpdate(final FileCrawlingConfig fileCrawlingConfig,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfig", fileCrawlingConfig);
        prepareUpdateOption(option);
        helpUpdateInternally(fileCrawlingConfig,
                new InternalUpdateCallback<FileCrawlingConfig>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final FileCrawlingConfig entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<FileCrawlingConfigCB> option) {
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

    protected FileCrawlingConfigCB createCBForVaryingUpdate() {
        final FileCrawlingConfigCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected FileCrawlingConfigCB createCBForSpecifiedUpdate() {
        final FileCrawlingConfigCB cb = newMyConditionBean();
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
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * fileCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">updateNonstrict</span>(fileCrawlingConfig);
     * </pre>
     * @param fileCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final FileCrawlingConfig fileCrawlingConfig) {
        doUpdateNonstrict(fileCrawlingConfig, null);
    }

    protected void doUpdateNonstrict(
            final FileCrawlingConfig fileCrawlingConfig,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfig", fileCrawlingConfig);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(fileCrawlingConfig,
                new InternalUpdateNonstrictCallback<FileCrawlingConfig>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final FileCrawlingConfig entity) {
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
     * @param fileCrawlingConfig The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final FileCrawlingConfig fileCrawlingConfig) {
        doInesrtOrUpdate(fileCrawlingConfig, null, null);
    }

    protected void doInesrtOrUpdate(
            final FileCrawlingConfig fileCrawlingConfig,
            final InsertOption<FileCrawlingConfigCB> insertOption,
            final UpdateOption<FileCrawlingConfigCB> updateOption) {
        helpInsertOrUpdateInternally(
                fileCrawlingConfig,
                new InternalInsertOrUpdateCallback<FileCrawlingConfig, FileCrawlingConfigCB>() {
                    @Override
                    public void callbackInsert(final FileCrawlingConfig entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final FileCrawlingConfig entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public FileCrawlingConfigCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final FileCrawlingConfigCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<FileCrawlingConfigCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<FileCrawlingConfigCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param fileCrawlingConfig The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(
            final FileCrawlingConfig fileCrawlingConfig) {
        doInesrtOrUpdateNonstrict(fileCrawlingConfig, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(
            final FileCrawlingConfig fileCrawlingConfig,
            final InsertOption<FileCrawlingConfigCB> insertOption,
            final UpdateOption<FileCrawlingConfigCB> updateOption) {
        helpInsertOrUpdateInternally(
                fileCrawlingConfig,
                new InternalInsertOrUpdateNonstrictCallback<FileCrawlingConfig>() {
                    @Override
                    public void callbackInsert(final FileCrawlingConfig entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(
                            final FileCrawlingConfig entity) {
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
            insertOption = insertOption == null ? new InsertOption<FileCrawlingConfigCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<FileCrawlingConfigCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     fileCrawlingConfigBhv.<span style="color: #FD4747">delete</span>(fileCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param fileCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final FileCrawlingConfig fileCrawlingConfig) {
        doDelete(fileCrawlingConfig, null);
    }

    protected void doDelete(final FileCrawlingConfig fileCrawlingConfig,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfig", fileCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteInternally(fileCrawlingConfig,
                new InternalDeleteCallback<FileCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDelete(
                            final FileCrawlingConfig entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<FileCrawlingConfigCB> option) {
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
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">deleteNonstrict</span>(fileCrawlingConfig);
     * </pre>
     * @param fileCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final FileCrawlingConfig fileCrawlingConfig) {
        doDeleteNonstrict(fileCrawlingConfig, null);
    }

    protected void doDeleteNonstrict(
            final FileCrawlingConfig fileCrawlingConfig,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfig", fileCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(fileCrawlingConfig,
                new InternalDeleteNonstrictCallback<FileCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final FileCrawlingConfig entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * fileCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(fileCrawlingConfig);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param fileCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(
            final FileCrawlingConfig fileCrawlingConfig) {
        doDeleteNonstrictIgnoreDeleted(fileCrawlingConfig, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(
            final FileCrawlingConfig fileCrawlingConfig,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfig", fileCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(
                fileCrawlingConfig,
                new InternalDeleteNonstrictIgnoreDeletedCallback<FileCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final FileCrawlingConfig entity) {
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
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchInsert(fileCrawlingConfigList, null);
    }

    protected int[] doBatchInsert(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final InsertOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfigList", fileCrawlingConfigList);
        prepareInsertOption(option);
        return delegateBatchInsert(fileCrawlingConfigList, option);
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
     * fileCrawlingConfigBhv.<span style="color: #FD4747">batchUpdate</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchUpdate(fileCrawlingConfigList, null);
    }

    protected int[] doBatchUpdate(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfigList", fileCrawlingConfigList);
        prepareBatchUpdateOption(fileCrawlingConfigList, option);
        return delegateBatchUpdate(fileCrawlingConfigList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final UpdateOption<FileCrawlingConfigCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(fileCrawlingConfigList);
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
     * fileCrawlingConfigBhv.<span style="color: #FD4747">batchUpdate</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * fileCrawlingConfigBhv.<span style="color: #FD4747">batchUpdate</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final SpecifyQuery<FileCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdate(fileCrawlingConfigList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdateNonstrict() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchUpdateNonstrict(fileCrawlingConfigList, null);
    }

    protected int[] doBatchUpdateNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfigList", fileCrawlingConfigList);
        prepareBatchUpdateOption(fileCrawlingConfigList, option);
        return delegateBatchUpdateNonstrict(fileCrawlingConfigList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span> 
     * fileCrawlingConfigBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * fileCrawlingConfigBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(fileCrawlingConfigList, new SpecifyQuery<FileCrawlingConfigCB>() {
     *     public void specify(FileCrawlingConfigCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
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
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final SpecifyQuery<FileCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(fileCrawlingConfigList,
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
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchDelete(fileCrawlingConfigList, null);
    }

    protected int[] doBatchDelete(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfigList", fileCrawlingConfigList);
        prepareDeleteOption(option);
        return delegateBatchDelete(fileCrawlingConfigList, option);
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
     * @param fileCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList) {
        return doBatchDeleteNonstrict(fileCrawlingConfigList, null);
    }

    protected int[] doBatchDeleteNonstrict(
            final List<FileCrawlingConfig> fileCrawlingConfigList,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfigList", fileCrawlingConfigList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(fileCrawlingConfigList, option);
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
     * fileCrawlingConfigBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;FileCrawlingConfig, FileCrawlingConfigCB&gt;() {
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
            final QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB> setupper,
            final InsertOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final FileCrawlingConfig entity = new FileCrawlingConfig();
        final FileCrawlingConfigCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected FileCrawlingConfigCB createCBForQueryInsert() {
        final FileCrawlingConfigCB cb = newMyConditionBean();
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
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setPK...(value);</span>
     * fileCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * fileCrawlingConfigBhv.<span style="color: #FD4747">queryUpdate</span>(fileCrawlingConfig, cb);
     * </pre>
     * @param fileCrawlingConfig The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final FileCrawlingConfig fileCrawlingConfig,
            final FileCrawlingConfigCB cb) {
        return doQueryUpdate(fileCrawlingConfig, cb, null);
    }

    protected int doQueryUpdate(final FileCrawlingConfig fileCrawlingConfig,
            final FileCrawlingConfigCB cb,
            final UpdateOption<FileCrawlingConfigCB> option) {
        assertObjectNotNull("fileCrawlingConfig", fileCrawlingConfig);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                fileCrawlingConfig, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (FileCrawlingConfigCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (FileCrawlingConfigCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * fileCrawlingConfigBhv.<span style="color: #FD4747">queryDelete</span>(fileCrawlingConfig, cb);
     * </pre>
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final FileCrawlingConfigCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final FileCrawlingConfigCB cb,
            final DeleteOption<FileCrawlingConfigCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((FileCrawlingConfigCB) cb);
        } else {
            return varyingQueryDelete((FileCrawlingConfigCB) cb,
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
     * FileCrawlingConfig fileCrawlingConfig = new FileCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * fileCrawlingConfig.setFoo...(value);
     * fileCrawlingConfig.setBar...(value);
     * InsertOption<FileCrawlingConfigCB> option = new InsertOption<FileCrawlingConfigCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * fileCrawlingConfigBhv.<span style="color: #FD4747">varyingInsert</span>(fileCrawlingConfig, option);
     * ... = fileCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param fileCrawlingConfig The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * fileCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;FileCrawlingConfigCB&gt; option = new UpdateOption&lt;FileCrawlingConfigCB&gt;();
     *     option.self(new SpecifyQuery&lt;FileCrawlingConfigCB&gt;() {
     *         public void specify(FileCrawlingConfigCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     fileCrawlingConfigBhv.<span style="color: #FD4747">varyingUpdate</span>(fileCrawlingConfig, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param fileCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * UpdateOption&lt;FileCrawlingConfigCB&gt; option = new UpdateOption&lt;FileCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void specify(FileCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(fileCrawlingConfig, option);
     * </pre>
     * @param fileCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * @param fileCrawlingConfig The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final FileCrawlingConfig fileCrawlingConfig,
            final InsertOption<FileCrawlingConfigCB> insertOption,
            final UpdateOption<FileCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(fileCrawlingConfig, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param fileCrawlingConfig The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final FileCrawlingConfig fileCrawlingConfig,
            final InsertOption<FileCrawlingConfigCB> insertOption,
            final UpdateOption<FileCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(fileCrawlingConfig, insertOption,
                updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param fileCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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
     * @param fileCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//fileCrawlingConfig.setVersionNo(value);</span>
     * FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;FileCrawlingConfigCB&gt; option = new UpdateOption&lt;FileCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;FileCrawlingConfigCB&gt;() {
     *     public void specify(FileCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * fileCrawlingConfigBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(fileCrawlingConfig, cb, option);
     * </pre>
     * @param fileCrawlingConfig The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of FileCrawlingConfig. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
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
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
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
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final FileCrawlingConfigCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final FileCrawlingConfigCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends FileCrawlingConfig> void delegateSelectCursor(
            final FileCrawlingConfigCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends FileCrawlingConfig> List<ENTITY> delegateSelectList(
            final FileCrawlingConfigCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final FileCrawlingConfig e,
            final InsertOption<FileCrawlingConfigCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final FileCrawlingConfig e,
            final UpdateOption<FileCrawlingConfigCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final FileCrawlingConfig e,
            final UpdateOption<FileCrawlingConfigCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final FileCrawlingConfig e,
            final DeleteOption<FileCrawlingConfigCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final FileCrawlingConfig e,
            final DeleteOption<FileCrawlingConfigCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<FileCrawlingConfig> ls,
            final InsertOption<FileCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<FileCrawlingConfig> ls,
            final UpdateOption<FileCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<FileCrawlingConfig> ls,
            final UpdateOption<FileCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<FileCrawlingConfig> ls,
            final DeleteOption<FileCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<FileCrawlingConfig> ls,
            final DeleteOption<FileCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final FileCrawlingConfig e,
            final FileCrawlingConfigCB inCB, final ConditionBean resCB,
            final InsertOption<FileCrawlingConfigCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final FileCrawlingConfig e,
            final FileCrawlingConfigCB cb,
            final UpdateOption<FileCrawlingConfigCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final FileCrawlingConfigCB cb,
            final DeleteOption<FileCrawlingConfigCB> op) {
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
    protected FileCrawlingConfig downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, FileCrawlingConfig.class);
    }

    protected FileCrawlingConfigCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                FileCrawlingConfigCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<FileCrawlingConfig> downcast(
            final List<? extends Entity> entityList) {
        return (List<FileCrawlingConfig>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<FileCrawlingConfigCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<FileCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<FileCrawlingConfigCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<FileCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<FileCrawlingConfigCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<FileCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<FileCrawlingConfig, FileCrawlingConfigCB>) option;
    }
}
