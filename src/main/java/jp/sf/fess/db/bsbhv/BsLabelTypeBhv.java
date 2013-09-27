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

import jp.sf.fess.db.bsentity.dbmeta.LabelTypeDbm;
import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.LabelTypeCB;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.exbhv.DataConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.LabelTypeBhv;
import jp.sf.fess.db.exbhv.LabelTypeToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;

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
 * The behavior of LABEL_TYPE as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, NAME, VALUE, INCLUDED_PATHS, EXCLUDED_PATHS, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     DATA_CONFIG_TO_LABEL_TYPE_MAPPING, FILE_CONFIG_TO_LABEL_TYPE_MAPPING, LABEL_TYPE_TO_ROLE_TYPE_MAPPING, WEB_CONFIG_TO_LABEL_TYPE_MAPPING
 * 
 * [foreign property]
 *     
 * 
 * [referrer property]
 *     dataConfigToLabelTypeMappingList, fileConfigToLabelTypeMappingList, labelTypeToRoleTypeMappingList, webConfigToLabelTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsLabelTypeBhv extends AbstractBehaviorWritable {

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
        return "LABEL_TYPE";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return LabelTypeDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public LabelTypeDbm getMyDBMeta() {
        return LabelTypeDbm.getInstance();
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
    public LabelType newMyEntity() {
        return new LabelType();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public LabelTypeCB newMyConditionBean() {
        return new LabelTypeCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * int count = labelTypeBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final LabelTypeCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final LabelTypeCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final LabelTypeCB cb) { // called by selectPage(cb)
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
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * LabelType labelType = labelTypeBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (labelType != null) {
     *     ... = labelType.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public LabelType selectEntity(final LabelTypeCB cb) {
        return doSelectEntity(cb, LabelType.class);
    }

    protected <ENTITY extends LabelType> ENTITY doSelectEntity(
            final LabelTypeCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(cb, entityType,
                new InternalSelectEntityCallback<ENTITY, LabelTypeCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeCB cb, final Class<ENTITY> entityType) {
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
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * LabelType labelType = labelTypeBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = labelType.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public LabelType selectEntityWithDeletedCheck(final LabelTypeCB cb) {
        return doSelectEntityWithDeletedCheck(cb, LabelType.class);
    }

    protected <ENTITY extends LabelType> ENTITY doSelectEntityWithDeletedCheck(
            final LabelTypeCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, LabelTypeCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeCB cb, final Class<ENTITY> entityType) {
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
    public LabelType selectByPKValue(final Long id) {
        return doSelectByPKValue(id, LabelType.class);
    }

    protected <ENTITY extends LabelType> ENTITY doSelectByPKValue(
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
    public LabelType selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, LabelType.class);
    }

    protected <ENTITY extends LabelType> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private LabelTypeCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final LabelTypeCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;LabelType&gt; labelTypeList = labelTypeBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.get...();
     * }
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<LabelType> selectList(final LabelTypeCB cb) {
        return doSelectList(cb, LabelType.class);
    }

    protected <ENTITY extends LabelType> ListResultBean<ENTITY> doSelectList(
            final LabelTypeCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, LabelTypeCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeCB cb, final Class<ENTITY> entityType) {
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
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;LabelType&gt; page = labelTypeBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (LabelType labelType : page) {
     *     ... = labelType.get...();
     * }
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<LabelType> selectPage(final LabelTypeCB cb) {
        return doSelectPage(cb, LabelType.class);
    }

    protected <ENTITY extends LabelType> PagingResultBean<ENTITY> doSelectPage(
            final LabelTypeCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, LabelTypeCB>() {
                    @Override
                    public int callbackSelectCount(final LabelTypeCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeCB cb, final Class<ENTITY> entityType) {
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
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * labelTypeBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;LabelType&gt;() {
     *     public void handle(LabelType entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @param entityRowHandler The handler of entity row of LabelType. (NotNull)
     */
    public void selectCursor(final LabelTypeCB cb,
            final EntityRowHandler<LabelType> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, LabelType.class);
    }

    protected <ENTITY extends LabelType> void doSelectCursor(
            final LabelTypeCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<LabelType>", entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(cb, entityRowHandler, entityType,
                new InternalSelectCursorCallback<ENTITY, LabelTypeCB>() {
                    @Override
                    public void callbackSelectCursor(final LabelTypeCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final LabelTypeCB cb, final Class<ENTITY> entityType) {
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
     * labelTypeBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(LabelTypeCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<LabelTypeCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends LabelTypeCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param labelType The entity of labelType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadDataConfigToLabelTypeMappingList(
            final LabelType labelType,
            final ConditionBeanSetupper<DataConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(labelType, conditionBeanSetupper);
        loadDataConfigToLabelTypeMappingList(xnewLRLs(labelType),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of dataConfigToLabelTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'dataConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #FD4747">loadDataConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(DataConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.<span style="color: #FD4747">getDataConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadDataConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final ConditionBeanSetupper<DataConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(labelTypeList, conditionBeanSetupper);
        loadDataConfigToLabelTypeMappingList(
                labelTypeList,
                new LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param labelType The entity of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadDataConfigToLabelTypeMappingList(
            final LabelType labelType,
            final LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelType, loadReferrerOption);
        loadDataConfigToLabelTypeMappingList(xnewLRLs(labelType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadDataConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelTypeList, loadReferrerOption);
        if (labelTypeList.isEmpty()) {
            return;
        }
        final DataConfigToLabelTypeMappingBhv referrerBhv = xgetBSFLR().select(
                DataConfigToLabelTypeMappingBhv.class);
        helpLoadReferrerInternally(
                labelTypeList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<LabelType, Long, DataConfigToLabelTypeMappingCB, DataConfigToLabelTypeMapping>() {
                    @Override
                    public Long getPKVal(final LabelType e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final LabelType e,
                            final List<DataConfigToLabelTypeMapping> ls) {
                        e.setDataConfigToLabelTypeMappingList(ls);
                    }

                    @Override
                    public DataConfigToLabelTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final DataConfigToLabelTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setLabelTypeId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(
                            final DataConfigToLabelTypeMappingCB cb) {
                        cb.query().addOrderBy_LabelTypeId_Asc();
                    }

                    @Override
                    public void spFKCol(final DataConfigToLabelTypeMappingCB cb) {
                        cb.specify().columnLabelTypeId();
                    }

                    @Override
                    public List<DataConfigToLabelTypeMapping> selRfLs(
                            final DataConfigToLabelTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final DataConfigToLabelTypeMapping e) {
                        return e.getLabelTypeId();
                    }

                    @Override
                    public void setlcEt(final DataConfigToLabelTypeMapping re,
                            final LabelType le) {
                        re.setLabelType(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "dataConfigToLabelTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param labelType The entity of labelType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToLabelTypeMappingList(
            final LabelType labelType,
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(labelType, conditionBeanSetupper);
        loadFileConfigToLabelTypeMappingList(xnewLRLs(labelType),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of fileConfigToLabelTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'fileConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #FD4747">loadFileConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;FileConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(FileConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.<span style="color: #FD4747">getFileConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFileConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(labelTypeList, conditionBeanSetupper);
        loadFileConfigToLabelTypeMappingList(
                labelTypeList,
                new LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param labelType The entity of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToLabelTypeMappingList(
            final LabelType labelType,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelType, loadReferrerOption);
        loadFileConfigToLabelTypeMappingList(xnewLRLs(labelType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFileConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelTypeList, loadReferrerOption);
        if (labelTypeList.isEmpty()) {
            return;
        }
        final FileConfigToLabelTypeMappingBhv referrerBhv = xgetBSFLR().select(
                FileConfigToLabelTypeMappingBhv.class);
        helpLoadReferrerInternally(
                labelTypeList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<LabelType, Long, FileConfigToLabelTypeMappingCB, FileConfigToLabelTypeMapping>() {
                    @Override
                    public Long getPKVal(final LabelType e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final LabelType e,
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
                        cb.query().setLabelTypeId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(
                            final FileConfigToLabelTypeMappingCB cb) {
                        cb.query().addOrderBy_LabelTypeId_Asc();
                    }

                    @Override
                    public void spFKCol(final FileConfigToLabelTypeMappingCB cb) {
                        cb.specify().columnLabelTypeId();
                    }

                    @Override
                    public List<FileConfigToLabelTypeMapping> selRfLs(
                            final FileConfigToLabelTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final FileConfigToLabelTypeMapping e) {
                        return e.getLabelTypeId();
                    }

                    @Override
                    public void setlcEt(final FileConfigToLabelTypeMapping re,
                            final LabelType le) {
                        re.setLabelType(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "fileConfigToLabelTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param labelType The entity of labelType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadLabelTypeToRoleTypeMappingList(
            final LabelType labelType,
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(labelType, conditionBeanSetupper);
        loadLabelTypeToRoleTypeMappingList(xnewLRLs(labelType),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of labelTypeToRoleTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by LABEL_TYPE_ID, named 'labelTypeToRoleTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #FD4747">loadLabelTypeToRoleTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void setup(LabelTypeToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.<span style="color: #FD4747">getLabelTypeToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadLabelTypeToRoleTypeMappingList(
            final List<LabelType> labelTypeList,
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(labelTypeList, conditionBeanSetupper);
        loadLabelTypeToRoleTypeMappingList(
                labelTypeList,
                new LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param labelType The entity of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadLabelTypeToRoleTypeMappingList(
            final LabelType labelType,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(labelType, loadReferrerOption);
        loadLabelTypeToRoleTypeMappingList(xnewLRLs(labelType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadLabelTypeToRoleTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(labelTypeList, loadReferrerOption);
        if (labelTypeList.isEmpty()) {
            return;
        }
        final LabelTypeToRoleTypeMappingBhv referrerBhv = xgetBSFLR().select(
                LabelTypeToRoleTypeMappingBhv.class);
        helpLoadReferrerInternally(
                labelTypeList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<LabelType, Long, LabelTypeToRoleTypeMappingCB, LabelTypeToRoleTypeMapping>() {
                    @Override
                    public Long getPKVal(final LabelType e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final LabelType e,
                            final List<LabelTypeToRoleTypeMapping> ls) {
                        e.setLabelTypeToRoleTypeMappingList(ls);
                    }

                    @Override
                    public LabelTypeToRoleTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final LabelTypeToRoleTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setLabelTypeId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final LabelTypeToRoleTypeMappingCB cb) {
                        cb.query().addOrderBy_LabelTypeId_Asc();
                    }

                    @Override
                    public void spFKCol(final LabelTypeToRoleTypeMappingCB cb) {
                        cb.specify().columnLabelTypeId();
                    }

                    @Override
                    public List<LabelTypeToRoleTypeMapping> selRfLs(
                            final LabelTypeToRoleTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final LabelTypeToRoleTypeMapping e) {
                        return e.getLabelTypeId();
                    }

                    @Override
                    public void setlcEt(final LabelTypeToRoleTypeMapping re,
                            final LabelType le) {
                        re.setLabelType(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "labelTypeToRoleTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param labelType The entity of labelType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToLabelTypeMappingList(
            final LabelType labelType,
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(labelType, conditionBeanSetupper);
        loadWebConfigToLabelTypeMappingList(xnewLRLs(labelType),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of webConfigToLabelTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'webConfigToLabelTypeMappingList'.
     * <pre>
     * labelTypeBhv.<span style="color: #FD4747">loadWebConfigToLabelTypeMappingList</span>(labelTypeList, new ConditionBeanSetupper&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(WebConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (LabelType labelType : labelTypeList) {
     *     ... = labelType.<span style="color: #FD4747">getWebConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setLabelTypeId_InScope(pkList);
     * cb.query().addOrderBy_LabelTypeId_Asc();
     * </pre>
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(labelTypeList, conditionBeanSetupper);
        loadWebConfigToLabelTypeMappingList(
                labelTypeList,
                new LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param labelType The entity of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToLabelTypeMappingList(
            final LabelType labelType,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelType, loadReferrerOption);
        loadWebConfigToLabelTypeMappingList(xnewLRLs(labelType),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param labelTypeList The entity list of labelType. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToLabelTypeMappingList(
            final List<LabelType> labelTypeList,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(labelTypeList, loadReferrerOption);
        if (labelTypeList.isEmpty()) {
            return;
        }
        final WebConfigToLabelTypeMappingBhv referrerBhv = xgetBSFLR().select(
                WebConfigToLabelTypeMappingBhv.class);
        helpLoadReferrerInternally(
                labelTypeList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<LabelType, Long, WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping>() {
                    @Override
                    public Long getPKVal(final LabelType e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final LabelType e,
                            final List<WebConfigToLabelTypeMapping> ls) {
                        e.setWebConfigToLabelTypeMappingList(ls);
                    }

                    @Override
                    public WebConfigToLabelTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final WebConfigToLabelTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setLabelTypeId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final WebConfigToLabelTypeMappingCB cb) {
                        cb.query().addOrderBy_LabelTypeId_Asc();
                    }

                    @Override
                    public void spFKCol(final WebConfigToLabelTypeMappingCB cb) {
                        cb.specify().columnLabelTypeId();
                    }

                    @Override
                    public List<WebConfigToLabelTypeMapping> selRfLs(
                            final WebConfigToLabelTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final WebConfigToLabelTypeMapping e) {
                        return e.getLabelTypeId();
                    }

                    @Override
                    public void setlcEt(final WebConfigToLabelTypeMapping re,
                            final LabelType le) {
                        re.setLabelType(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "webConfigToLabelTypeMappingList";
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
     * @param labelTypeList The list of labelType. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(final List<LabelType> labelTypeList) {
        return helpExtractListInternally(labelTypeList,
                new InternalExtractCallback<LabelType, Long>() {
                    @Override
                    public Long getCV(final LabelType e) {
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
     * LabelType labelType = new LabelType();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * labelType.setFoo...(value);
     * labelType.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelType.set...;</span>
     * labelTypeBhv.<span style="color: #FD4747">insert</span>(labelType);
     * ... = labelType.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param labelType The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final LabelType labelType) {
        doInsert(labelType, null);
    }

    protected void doInsert(final LabelType labelType,
            final InsertOption<LabelTypeCB> option) {
        assertObjectNotNull("labelType", labelType);
        prepareInsertOption(option);
        delegateInsert(labelType, option);
    }

    protected void prepareInsertOption(final InsertOption<LabelTypeCB> option) {
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
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelType.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * labelType.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     labelTypeBhv.<span style="color: #FD4747">update</span>(labelType);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param labelType The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final LabelType labelType) {
        doUpdate(labelType, null);
    }

    protected void doUpdate(final LabelType labelType,
            final UpdateOption<LabelTypeCB> option) {
        assertObjectNotNull("labelType", labelType);
        prepareUpdateOption(option);
        helpUpdateInternally(labelType,
                new InternalUpdateCallback<LabelType>() {
                    @Override
                    public int callbackDelegateUpdate(final LabelType entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(final UpdateOption<LabelTypeCB> option) {
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

    protected LabelTypeCB createCBForVaryingUpdate() {
        final LabelTypeCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected LabelTypeCB createCBForSpecifiedUpdate() {
        final LabelTypeCB cb = newMyConditionBean();
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
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelType.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * labelTypeBhv.<span style="color: #FD4747">updateNonstrict</span>(labelType);
     * </pre>
     * @param labelType The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final LabelType labelType) {
        doUpdateNonstrict(labelType, null);
    }

    protected void doUpdateNonstrict(final LabelType labelType,
            final UpdateOption<LabelTypeCB> option) {
        assertObjectNotNull("labelType", labelType);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(labelType,
                new InternalUpdateNonstrictCallback<LabelType>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final LabelType entity) {
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
     * @param labelType The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final LabelType labelType) {
        doInesrtOrUpdate(labelType, null, null);
    }

    protected void doInesrtOrUpdate(final LabelType labelType,
            final InsertOption<LabelTypeCB> insertOption,
            final UpdateOption<LabelTypeCB> updateOption) {
        helpInsertOrUpdateInternally(labelType,
                new InternalInsertOrUpdateCallback<LabelType, LabelTypeCB>() {
                    @Override
                    public void callbackInsert(final LabelType entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final LabelType entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public LabelTypeCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final LabelTypeCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<LabelTypeCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<LabelTypeCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #FD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param labelType The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(final LabelType labelType) {
        doInesrtOrUpdateNonstrict(labelType, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(final LabelType labelType,
            final InsertOption<LabelTypeCB> insertOption,
            final UpdateOption<LabelTypeCB> updateOption) {
        helpInsertOrUpdateInternally(labelType,
                new InternalInsertOrUpdateNonstrictCallback<LabelType>() {
                    @Override
                    public void callbackInsert(final LabelType entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(final LabelType entity) {
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
            insertOption = insertOption == null ? new InsertOption<LabelTypeCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<LabelTypeCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * labelType.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     labelTypeBhv.<span style="color: #FD4747">delete</span>(labelType);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param labelType The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final LabelType labelType) {
        doDelete(labelType, null);
    }

    protected void doDelete(final LabelType labelType,
            final DeleteOption<LabelTypeCB> option) {
        assertObjectNotNull("labelType", labelType);
        prepareDeleteOption(option);
        helpDeleteInternally(labelType,
                new InternalDeleteCallback<LabelType>() {
                    @Override
                    public int callbackDelegateDelete(final LabelType entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(final DeleteOption<LabelTypeCB> option) {
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
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * labelTypeBhv.<span style="color: #FD4747">deleteNonstrict</span>(labelType);
     * </pre>
     * @param labelType The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final LabelType labelType) {
        doDeleteNonstrict(labelType, null);
    }

    protected void doDeleteNonstrict(final LabelType labelType,
            final DeleteOption<LabelTypeCB> option) {
        assertObjectNotNull("labelType", labelType);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(labelType,
                new InternalDeleteNonstrictCallback<LabelType>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final LabelType entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * labelTypeBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(labelType);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param labelType The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(final LabelType labelType) {
        doDeleteNonstrictIgnoreDeleted(labelType, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(final LabelType labelType,
            final DeleteOption<LabelTypeCB> option) {
        assertObjectNotNull("labelType", labelType);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(labelType,
                new InternalDeleteNonstrictIgnoreDeletedCallback<LabelType>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final LabelType entity) {
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
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<LabelType> labelTypeList) {
        return doBatchInsert(labelTypeList, null);
    }

    protected int[] doBatchInsert(final List<LabelType> labelTypeList,
            final InsertOption<LabelTypeCB> option) {
        assertObjectNotNull("labelTypeList", labelTypeList);
        prepareInsertOption(option);
        return delegateBatchInsert(labelTypeList, option);
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
     * labelTypeBhv.<span style="color: #FD4747">batchUpdate</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<LabelType> labelTypeList) {
        return doBatchUpdate(labelTypeList, null);
    }

    protected int[] doBatchUpdate(final List<LabelType> labelTypeList,
            final UpdateOption<LabelTypeCB> option) {
        assertObjectNotNull("labelTypeList", labelTypeList);
        prepareBatchUpdateOption(labelTypeList, option);
        return delegateBatchUpdate(labelTypeList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<LabelType> labelTypeList,
            final UpdateOption<LabelTypeCB> option) {
        if (option == null) {
            return;
        }
        prepareUpdateOption(option);
        // under review
        //if (option.hasSpecifiedUpdateColumn()) {
        //    option.xgatherUpdateColumnModifiedProperties(labelTypeList);
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
     * labelTypeBhv.<span style="color: #FD4747">batchUpdate</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * labelTypeBhv.<span style="color: #FD4747">batchUpdate</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<LabelType> labelTypeList,
            final SpecifyQuery<LabelTypeCB> updateColumnSpec) {
        return doBatchUpdate(labelTypeList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdateNonstrict() (overload) method for performace,
     * which you can specify update columns like this:
     * <pre>
     * labelTypeBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>;
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>;
     *     }
     * });
     * </pre>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<LabelType> labelTypeList) {
        return doBatchUpdateNonstrict(labelTypeList, null);
    }

    protected int[] doBatchUpdateNonstrict(final List<LabelType> labelTypeList,
            final UpdateOption<LabelTypeCB> option) {
        assertObjectNotNull("labelTypeList", labelTypeList);
        prepareBatchUpdateOption(labelTypeList, option);
        return delegateBatchUpdateNonstrict(labelTypeList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span> 
     * labelTypeBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * labelTypeBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(labelTypeList, new SpecifyQuery<LabelTypeCB>() {
     *     public void specify(LabelTypeCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(final List<LabelType> labelTypeList,
            final SpecifyQuery<LabelTypeCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(labelTypeList,
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
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<LabelType> labelTypeList) {
        return doBatchDelete(labelTypeList, null);
    }

    protected int[] doBatchDelete(final List<LabelType> labelTypeList,
            final DeleteOption<LabelTypeCB> option) {
        assertObjectNotNull("labelTypeList", labelTypeList);
        prepareDeleteOption(option);
        return delegateBatchDelete(labelTypeList, option);
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
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(final List<LabelType> labelTypeList) {
        return doBatchDeleteNonstrict(labelTypeList, null);
    }

    protected int[] doBatchDeleteNonstrict(final List<LabelType> labelTypeList,
            final DeleteOption<LabelTypeCB> option) {
        assertObjectNotNull("labelTypeList", labelTypeList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(labelTypeList, option);
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
     * labelTypeBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;LabelType, LabelTypeCB&gt;() {
     *     public ConditionBean setup(labelType entity, LabelTypeCB intoCB) {
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
            final QueryInsertSetupper<LabelType, LabelTypeCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<LabelType, LabelTypeCB> setupper,
            final InsertOption<LabelTypeCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final LabelType entity = new LabelType();
        final LabelTypeCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected LabelTypeCB createCBForQueryInsert() {
        final LabelTypeCB cb = newMyConditionBean();
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
     * LabelType labelType = new LabelType();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//labelType.setPK...(value);</span>
     * labelType.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//labelType.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//labelType.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * labelTypeBhv.<span style="color: #FD4747">queryUpdate</span>(labelType, cb);
     * </pre>
     * @param labelType The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final LabelType labelType, final LabelTypeCB cb) {
        return doQueryUpdate(labelType, cb, null);
    }

    protected int doQueryUpdate(final LabelType labelType,
            final LabelTypeCB cb, final UpdateOption<LabelTypeCB> option) {
        assertObjectNotNull("labelType", labelType);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                labelType, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (LabelTypeCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity), (LabelTypeCB) cb,
                    downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * labelTypeBhv.<span style="color: #FD4747">queryDelete</span>(labelType, cb);
     * </pre>
     * @param cb The condition-bean of LabelType. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final LabelTypeCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final LabelTypeCB cb,
            final DeleteOption<LabelTypeCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((LabelTypeCB) cb);
        } else {
            return varyingQueryDelete((LabelTypeCB) cb, downcast(option));
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
     * LabelType labelType = new LabelType();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * labelType.setFoo...(value);
     * labelType.setBar...(value);
     * InsertOption<LabelTypeCB> option = new InsertOption<LabelTypeCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * labelTypeBhv.<span style="color: #FD4747">varyingInsert</span>(labelType, option);
     * ... = labelType.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param labelType The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final LabelType labelType,
            final InsertOption<LabelTypeCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(labelType, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * labelType.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;LabelTypeCB&gt; option = new UpdateOption&lt;LabelTypeCB&gt;();
     *     option.self(new SpecifyQuery&lt;LabelTypeCB&gt;() {
     *         public void specify(LabelTypeCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     labelTypeBhv.<span style="color: #FD4747">varyingUpdate</span>(labelType, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param labelType The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final LabelType labelType,
            final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(labelType, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * LabelType labelType = new LabelType();
     * labelType.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * labelType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * UpdateOption&lt;LabelTypeCB&gt; option = new UpdateOption&lt;LabelTypeCB&gt;();
     * option.self(new SpecifyQuery&lt;LabelTypeCB&gt;() {
     *     public void specify(LabelTypeCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * labelTypeBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(labelType, option);
     * </pre>
     * @param labelType The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(final LabelType labelType,
            final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(labelType, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param labelType The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(final LabelType labelType,
            final InsertOption<LabelTypeCB> insertOption,
            final UpdateOption<LabelTypeCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(labelType, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param labelType The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(final LabelType labelType,
            final InsertOption<LabelTypeCB> insertOption,
            final UpdateOption<LabelTypeCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(labelType, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param labelType The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final LabelType labelType,
            final DeleteOption<LabelTypeCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(labelType, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param labelType The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(final LabelType labelType,
            final DeleteOption<LabelTypeCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(labelType, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(final List<LabelType> labelTypeList,
            final InsertOption<LabelTypeCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(labelTypeList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(final List<LabelType> labelTypeList,
            final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(labelTypeList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<LabelType> labelTypeList,
            final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(labelTypeList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(final List<LabelType> labelTypeList,
            final DeleteOption<LabelTypeCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(labelTypeList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param labelTypeList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<LabelType> labelTypeList,
            final DeleteOption<LabelTypeCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(labelTypeList, option);
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
            final QueryInsertSetupper<LabelType, LabelTypeCB> setupper,
            final InsertOption<LabelTypeCB> option) {
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
     * LabelType labelType = new LabelType();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//labelType.setPK...(value);</span>
     * labelType.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//labelType.setVersionNo(value);</span>
     * LabelTypeCB cb = new LabelTypeCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;LabelTypeCB&gt; option = new UpdateOption&lt;LabelTypeCB&gt;();
     * option.self(new SpecifyQuery&lt;LabelTypeCB&gt;() {
     *     public void specify(LabelTypeCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * labelTypeBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(labelType, cb, option);
     * </pre>
     * @param labelType The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of LabelType. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final LabelType labelType,
            final LabelTypeCB cb, final UpdateOption<LabelTypeCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(labelType, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of LabelType. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final LabelTypeCB cb,
            final DeleteOption<LabelTypeCB> option) {
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
    public OutsideSqlBasicExecutor<LabelTypeBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final LabelTypeCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final LabelTypeCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends LabelType> void delegateSelectCursor(
            final LabelTypeCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends LabelType> List<ENTITY> delegateSelectList(
            final LabelTypeCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final LabelType e,
            final InsertOption<LabelTypeCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final LabelType e,
            final UpdateOption<LabelTypeCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final LabelType e,
            final UpdateOption<LabelTypeCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final LabelType e,
            final DeleteOption<LabelTypeCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final LabelType e,
            final DeleteOption<LabelTypeCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<LabelType> ls,
            final InsertOption<LabelTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<LabelType> ls,
            final UpdateOption<LabelTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(final List<LabelType> ls,
            final UpdateOption<LabelTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<LabelType> ls,
            final DeleteOption<LabelTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(final List<LabelType> ls,
            final DeleteOption<LabelTypeCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final LabelType e,
            final LabelTypeCB inCB, final ConditionBean resCB,
            final InsertOption<LabelTypeCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final LabelType e, final LabelTypeCB cb,
            final UpdateOption<LabelTypeCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final LabelTypeCB cb,
            final DeleteOption<LabelTypeCB> op) {
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
    protected LabelType downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, LabelType.class);
    }

    protected LabelTypeCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, LabelTypeCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<LabelType> downcast(final List<? extends Entity> entityList) {
        return (List<LabelType>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<LabelTypeCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<LabelTypeCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<LabelTypeCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<LabelTypeCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<LabelTypeCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<LabelTypeCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<LabelType, LabelTypeCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<LabelType, LabelTypeCB>) option;
    }
}
