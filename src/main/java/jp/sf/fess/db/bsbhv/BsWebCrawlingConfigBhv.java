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

import jp.sf.fess.db.bsentity.dbmeta.WebCrawlingConfigDbm;
import jp.sf.fess.db.cbean.FailureUrlCB;
import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.cbean.WebAuthenticationCB;
import jp.sf.fess.db.cbean.WebConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.exbhv.FailureUrlBhv;
import jp.sf.fess.db.exbhv.RequestHeaderBhv;
import jp.sf.fess.db.exbhv.WebAuthenticationBhv;
import jp.sf.fess.db.exbhv.WebConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebCrawlingConfigBhv;
import jp.sf.fess.db.exentity.FailureUrl;
import jp.sf.fess.db.exentity.RequestHeader;
import jp.sf.fess.db.exentity.WebAuthentication;
import jp.sf.fess.db.exentity.WebConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

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
 * The behavior of WEB_CRAWLING_CONFIG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, NAME, URLS, INCLUDED_URLS, EXCLUDED_URLS, INCLUDED_DOC_URLS, EXCLUDED_DOC_URLS, CONFIG_PARAMETER, DEPTH, MAX_ACCESS_COUNT, USER_AGENT, NUM_OF_THREAD, INTERVAL_TIME, BOOST, AVAILABLE, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     FAILURE_URL, REQUEST_HEADER, WEB_AUTHENTICATION, WEB_CONFIG_TO_BROWSER_TYPE_MAPPING, WEB_CONFIG_TO_LABEL_TYPE_MAPPING, WEB_CONFIG_TO_ROLE_TYPE_MAPPING
 * 
 * [foreign property]
 *     
 * 
 * [referrer property]
 *     failureUrlList, requestHeaderList, webAuthenticationList, webConfigToBrowserTypeMappingList, webConfigToLabelTypeMappingList, webConfigToRoleTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsWebCrawlingConfigBhv extends AbstractBehaviorWritable {

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
        return "WEB_CRAWLING_CONFIG";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return WebCrawlingConfigDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public WebCrawlingConfigDbm getMyDBMeta() {
        return WebCrawlingConfigDbm.getInstance();
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
    public WebCrawlingConfig newMyEntity() {
        return new WebCrawlingConfig();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public WebCrawlingConfigCB newMyConditionBean() {
        return new WebCrawlingConfigCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * int count = webCrawlingConfigBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final WebCrawlingConfigCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final WebCrawlingConfigCB cb) { // called by selectCount(cb) 
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final WebCrawlingConfigCB cb) { // called by selectPage(cb)
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * WebCrawlingConfig webCrawlingConfig = webCrawlingConfigBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (webCrawlingConfig != null) {
     *     ... = webCrawlingConfig.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public WebCrawlingConfig selectEntity(final WebCrawlingConfigCB cb) {
        return doSelectEntity(cb, WebCrawlingConfig.class);
    }

    protected <ENTITY extends WebCrawlingConfig> ENTITY doSelectEntity(
            final WebCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
                cb,
                entityType,
                new InternalSelectEntityCallback<ENTITY, WebCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebCrawlingConfigCB cb,
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * WebCrawlingConfig webCrawlingConfig = webCrawlingConfigBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = webCrawlingConfig.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public WebCrawlingConfig selectEntityWithDeletedCheck(
            final WebCrawlingConfigCB cb) {
        return doSelectEntityWithDeletedCheck(cb, WebCrawlingConfig.class);
    }

    protected <ENTITY extends WebCrawlingConfig> ENTITY doSelectEntityWithDeletedCheck(
            final WebCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                entityType,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, WebCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebCrawlingConfigCB cb,
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
    public WebCrawlingConfig selectByPKValue(final Long id) {
        return doSelectByPKValue(id, WebCrawlingConfig.class);
    }

    protected <ENTITY extends WebCrawlingConfig> ENTITY doSelectByPKValue(
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
    public WebCrawlingConfig selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, WebCrawlingConfig.class);
    }

    protected <ENTITY extends WebCrawlingConfig> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private WebCrawlingConfigCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final WebCrawlingConfigCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;WebCrawlingConfig&gt; webCrawlingConfigList = webCrawlingConfigBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.get...();
     * }
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<WebCrawlingConfig> selectList(
            final WebCrawlingConfigCB cb) {
        return doSelectList(cb, WebCrawlingConfig.class);
    }

    protected <ENTITY extends WebCrawlingConfig> ListResultBean<ENTITY> doSelectList(
            final WebCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, WebCrawlingConfigCB>() {
                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebCrawlingConfigCB cb,
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;WebCrawlingConfig&gt; page = webCrawlingConfigBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (WebCrawlingConfig webCrawlingConfig : page) {
     *     ... = webCrawlingConfig.get...();
     * }
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<WebCrawlingConfig> selectPage(
            final WebCrawlingConfigCB cb) {
        return doSelectPage(cb, WebCrawlingConfig.class);
    }

    protected <ENTITY extends WebCrawlingConfig> PagingResultBean<ENTITY> doSelectPage(
            final WebCrawlingConfigCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, WebCrawlingConfigCB>() {
                    @Override
                    public int callbackSelectCount(final WebCrawlingConfigCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebCrawlingConfigCB cb,
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * webCrawlingConfigBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;WebCrawlingConfig&gt;() {
     *     public void handle(WebCrawlingConfig entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @param entityRowHandler The handler of entity row of WebCrawlingConfig. (NotNull)
     */
    public void selectCursor(final WebCrawlingConfigCB cb,
            final EntityRowHandler<WebCrawlingConfig> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, WebCrawlingConfig.class);
    }

    protected <ENTITY extends WebCrawlingConfig> void doSelectCursor(
            final WebCrawlingConfigCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<WebCrawlingConfig>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
                cb,
                entityRowHandler,
                entityType,
                new InternalSelectCursorCallback<ENTITY, WebCrawlingConfigCB>() {
                    @Override
                    public void callbackSelectCursor(
                            final WebCrawlingConfigCB cb,
                            final EntityRowHandler<ENTITY> entityRowHandler,
                            final Class<ENTITY> entityType) {
                        delegateSelectCursor(cb, entityRowHandler, entityType);
                    }

                    @Override
                    public List<ENTITY> callbackSelectList(
                            final WebCrawlingConfigCB cb,
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
     * webCrawlingConfigBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(WebCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<WebCrawlingConfigCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends WebCrawlingConfigCB> SLFunction<CB, RESULT> doScalarSelect(
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
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFailureUrlList(final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<FailureUrlCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfig, conditionBeanSetupper);
        loadFailureUrlList(xnewLRLs(webCrawlingConfig), conditionBeanSetupper);
    }

    /**
     * Load referrer of failureUrlList with the set-upper for condition-bean of referrer. <br />
     * FAILURE_URL by WEB_CONFIG_ID, named 'failureUrlList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #FD4747">loadFailureUrlList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;FailureUrlCB&gt;() {
     *     public void setup(FailureUrlCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #FD4747">getFailureUrlList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setWebConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadFailureUrlList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<FailureUrlCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfigList, conditionBeanSetupper);
        loadFailureUrlList(webCrawlingConfigList,
                new LoadReferrerOption<FailureUrlCB, FailureUrl>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFailureUrlList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<FailureUrlCB, FailureUrl> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        loadFailureUrlList(xnewLRLs(webCrawlingConfig), loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadFailureUrlList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<FailureUrlCB, FailureUrl> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return;
        }
        final FailureUrlBhv referrerBhv = xgetBSFLR().select(
                FailureUrlBhv.class);
        helpLoadReferrerInternally(
                webCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<WebCrawlingConfig, Long, FailureUrlCB, FailureUrl>() {
                    @Override
                    public Long getPKVal(final WebCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
                            final List<FailureUrl> ls) {
                        e.setFailureUrlList(ls);
                    }

                    @Override
                    public FailureUrlCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final FailureUrlCB cb,
                            final List<Long> ls) {
                        cb.query().setWebConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final FailureUrlCB cb) {
                        cb.query().addOrderBy_WebConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final FailureUrlCB cb) {
                        cb.specify().columnWebConfigId();
                    }

                    @Override
                    public List<FailureUrl> selRfLs(final FailureUrlCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final FailureUrl e) {
                        return e.getWebConfigId();
                    }

                    @Override
                    public void setlcEt(final FailureUrl re,
                            final WebCrawlingConfig le) {
                        re.setWebCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "failureUrlList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadRequestHeaderList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<RequestHeaderCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfig, conditionBeanSetupper);
        loadRequestHeaderList(xnewLRLs(webCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of requestHeaderList with the set-upper for condition-bean of referrer. <br />
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #FD4747">loadRequestHeaderList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;RequestHeaderCB&gt;() {
     *     public void setup(RequestHeaderCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #FD4747">getRequestHeaderList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setWebCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebCrawlingConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadRequestHeaderList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<RequestHeaderCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfigList, conditionBeanSetupper);
        loadRequestHeaderList(webCrawlingConfigList,
                new LoadReferrerOption<RequestHeaderCB, RequestHeader>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadRequestHeaderList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<RequestHeaderCB, RequestHeader> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        loadRequestHeaderList(xnewLRLs(webCrawlingConfig), loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadRequestHeaderList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<RequestHeaderCB, RequestHeader> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return;
        }
        final RequestHeaderBhv referrerBhv = xgetBSFLR().select(
                RequestHeaderBhv.class);
        helpLoadReferrerInternally(
                webCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<WebCrawlingConfig, Long, RequestHeaderCB, RequestHeader>() {
                    @Override
                    public Long getPKVal(final WebCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
                            final List<RequestHeader> ls) {
                        e.setRequestHeaderList(ls);
                    }

                    @Override
                    public RequestHeaderCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final RequestHeaderCB cb,
                            final List<Long> ls) {
                        cb.query().setWebCrawlingConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final RequestHeaderCB cb) {
                        cb.query().addOrderBy_WebCrawlingConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final RequestHeaderCB cb) {
                        cb.specify().columnWebCrawlingConfigId();
                    }

                    @Override
                    public List<RequestHeader> selRfLs(final RequestHeaderCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final RequestHeader e) {
                        return e.getWebCrawlingConfigId();
                    }

                    @Override
                    public void setlcEt(final RequestHeader re,
                            final WebCrawlingConfig le) {
                        re.setWebCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "requestHeaderList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebAuthenticationList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<WebAuthenticationCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfig, conditionBeanSetupper);
        loadWebAuthenticationList(xnewLRLs(webCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of webAuthenticationList with the set-upper for condition-bean of referrer. <br />
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #FD4747">loadWebAuthenticationList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebAuthenticationCB&gt;() {
     *     public void setup(WebAuthenticationCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #FD4747">getWebAuthenticationList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setWebCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebCrawlingConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebAuthenticationList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<WebAuthenticationCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfigList, conditionBeanSetupper);
        loadWebAuthenticationList(
                webCrawlingConfigList,
                new LoadReferrerOption<WebAuthenticationCB, WebAuthentication>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebAuthenticationList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<WebAuthenticationCB, WebAuthentication> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        loadWebAuthenticationList(xnewLRLs(webCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebAuthenticationList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebAuthenticationCB, WebAuthentication> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return;
        }
        final WebAuthenticationBhv referrerBhv = xgetBSFLR().select(
                WebAuthenticationBhv.class);
        helpLoadReferrerInternally(
                webCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<WebCrawlingConfig, Long, WebAuthenticationCB, WebAuthentication>() {
                    @Override
                    public Long getPKVal(final WebCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
                            final List<WebAuthentication> ls) {
                        e.setWebAuthenticationList(ls);
                    }

                    @Override
                    public WebAuthenticationCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final WebAuthenticationCB cb,
                            final List<Long> ls) {
                        cb.query().setWebCrawlingConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final WebAuthenticationCB cb) {
                        cb.query().addOrderBy_WebCrawlingConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final WebAuthenticationCB cb) {
                        cb.specify().columnWebCrawlingConfigId();
                    }

                    @Override
                    public List<WebAuthentication> selRfLs(
                            final WebAuthenticationCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final WebAuthentication e) {
                        return e.getWebCrawlingConfigId();
                    }

                    @Override
                    public void setlcEt(final WebAuthentication re,
                            final WebCrawlingConfig le) {
                        re.setWebCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "webAuthenticationList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToBrowserTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<WebConfigToBrowserTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfig, conditionBeanSetupper);
        loadWebConfigToBrowserTypeMappingList(xnewLRLs(webCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of webConfigToBrowserTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * WEB_CONFIG_TO_BROWSER_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToBrowserTypeMappingList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #FD4747">loadWebConfigToBrowserTypeMappingList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebConfigToBrowserTypeMappingCB&gt;() {
     *     public void setup(WebConfigToBrowserTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #FD4747">getWebConfigToBrowserTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setWebConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToBrowserTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<WebConfigToBrowserTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfigList, conditionBeanSetupper);
        loadWebConfigToBrowserTypeMappingList(
                webCrawlingConfigList,
                new LoadReferrerOption<WebConfigToBrowserTypeMappingCB, WebConfigToBrowserTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToBrowserTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<WebConfigToBrowserTypeMappingCB, WebConfigToBrowserTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        loadWebConfigToBrowserTypeMappingList(xnewLRLs(webCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToBrowserTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebConfigToBrowserTypeMappingCB, WebConfigToBrowserTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return;
        }
        final WebConfigToBrowserTypeMappingBhv referrerBhv = xgetBSFLR()
                .select(WebConfigToBrowserTypeMappingBhv.class);
        helpLoadReferrerInternally(
                webCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<WebCrawlingConfig, Long, WebConfigToBrowserTypeMappingCB, WebConfigToBrowserTypeMapping>() {
                    @Override
                    public Long getPKVal(final WebCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
                            final List<WebConfigToBrowserTypeMapping> ls) {
                        e.setWebConfigToBrowserTypeMappingList(ls);
                    }

                    @Override
                    public WebConfigToBrowserTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(
                            final WebConfigToBrowserTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setWebConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(
                            final WebConfigToBrowserTypeMappingCB cb) {
                        cb.query().addOrderBy_WebConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final WebConfigToBrowserTypeMappingCB cb) {
                        cb.specify().columnWebConfigId();
                    }

                    @Override
                    public List<WebConfigToBrowserTypeMapping> selRfLs(
                            final WebConfigToBrowserTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final WebConfigToBrowserTypeMapping e) {
                        return e.getWebConfigId();
                    }

                    @Override
                    public void setlcEt(final WebConfigToBrowserTypeMapping re,
                            final WebCrawlingConfig le) {
                        re.setWebCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "webConfigToBrowserTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToLabelTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfig, conditionBeanSetupper);
        loadWebConfigToLabelTypeMappingList(xnewLRLs(webCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of webConfigToLabelTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #FD4747">loadWebConfigToLabelTypeMappingList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(WebConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #FD4747">getWebConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setWebConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToLabelTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfigList, conditionBeanSetupper);
        loadWebConfigToLabelTypeMappingList(
                webCrawlingConfigList,
                new LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToLabelTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        loadWebConfigToLabelTypeMappingList(xnewLRLs(webCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToLabelTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return;
        }
        final WebConfigToLabelTypeMappingBhv referrerBhv = xgetBSFLR().select(
                WebConfigToLabelTypeMappingBhv.class);
        helpLoadReferrerInternally(
                webCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<WebCrawlingConfig, Long, WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping>() {
                    @Override
                    public Long getPKVal(final WebCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
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
                        cb.query().setWebConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final WebConfigToLabelTypeMappingCB cb) {
                        cb.query().addOrderBy_WebConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final WebConfigToLabelTypeMappingCB cb) {
                        cb.specify().columnWebConfigId();
                    }

                    @Override
                    public List<WebConfigToLabelTypeMapping> selRfLs(
                            final WebConfigToLabelTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final WebConfigToLabelTypeMapping e) {
                        return e.getWebConfigId();
                    }

                    @Override
                    public void setlcEt(final WebConfigToLabelTypeMapping re,
                            final WebCrawlingConfig le) {
                        re.setWebCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "webConfigToLabelTypeMappingList";
                    }
                });
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToRoleTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfig, conditionBeanSetupper);
        loadWebConfigToRoleTypeMappingList(xnewLRLs(webCrawlingConfig),
                conditionBeanSetupper);
    }

    /**
     * Load referrer of webConfigToRoleTypeMappingList with the set-upper for condition-bean of referrer. <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #FD4747">loadWebConfigToRoleTypeMappingList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(WebConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...(); <span style="color: #3F7E5E">// basically you should order referrer list</span>
     *     }
     * });
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #FD4747">getWebConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the set-upper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setWebConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean set-upper for registering referrer condition. (NotNull)
     */
    public void loadWebConfigToRoleTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> conditionBeanSetupper) {
        xassLRArg(webCrawlingConfigList, conditionBeanSetupper);
        loadWebConfigToRoleTypeMappingList(
                webCrawlingConfigList,
                new LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping>()
                        .xinit(conditionBeanSetupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.}
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToRoleTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        loadWebConfigToRoleTypeMappingList(xnewLRLs(webCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadWebConfigToRoleTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return;
        }
        final WebConfigToRoleTypeMappingBhv referrerBhv = xgetBSFLR().select(
                WebConfigToRoleTypeMappingBhv.class);
        helpLoadReferrerInternally(
                webCrawlingConfigList,
                loadReferrerOption,
                new InternalLoadReferrerCallback<WebCrawlingConfig, Long, WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping>() {
                    @Override
                    public Long getPKVal(final WebCrawlingConfig e) {
                        return e.getId();
                    }

                    @Override
                    public void setRfLs(final WebCrawlingConfig e,
                            final List<WebConfigToRoleTypeMapping> ls) {
                        e.setWebConfigToRoleTypeMappingList(ls);
                    }

                    @Override
                    public WebConfigToRoleTypeMappingCB newMyCB() {
                        return referrerBhv.newMyConditionBean();
                    }

                    @Override
                    public void qyFKIn(final WebConfigToRoleTypeMappingCB cb,
                            final List<Long> ls) {
                        cb.query().setWebConfigId_InScope(ls);
                    }

                    @Override
                    public void qyOdFKAsc(final WebConfigToRoleTypeMappingCB cb) {
                        cb.query().addOrderBy_WebConfigId_Asc();
                    }

                    @Override
                    public void spFKCol(final WebConfigToRoleTypeMappingCB cb) {
                        cb.specify().columnWebConfigId();
                    }

                    @Override
                    public List<WebConfigToRoleTypeMapping> selRfLs(
                            final WebConfigToRoleTypeMappingCB cb) {
                        return referrerBhv.selectList(cb);
                    }

                    @Override
                    public Long getFKVal(final WebConfigToRoleTypeMapping e) {
                        return e.getWebConfigId();
                    }

                    @Override
                    public void setlcEt(final WebConfigToRoleTypeMapping re,
                            final WebCrawlingConfig le) {
                        re.setWebCrawlingConfig(le);
                    }

                    @Override
                    public String getRfPrNm() {
                        return "webConfigToRoleTypeMappingList";
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
     * @param webCrawlingConfigList The list of webCrawlingConfig. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(
            final List<WebCrawlingConfig> webCrawlingConfigList) {
        return helpExtractListInternally(webCrawlingConfigList,
                new InternalExtractCallback<WebCrawlingConfig, Long>() {
                    @Override
                    public Long getCV(final WebCrawlingConfig e) {
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * webCrawlingConfig.setFoo...(value);
     * webCrawlingConfig.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.set...;</span>
     * webCrawlingConfigBhv.<span style="color: #FD4747">insert</span>(webCrawlingConfig);
     * ... = webCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param webCrawlingConfig The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final WebCrawlingConfig webCrawlingConfig) {
        doInsert(webCrawlingConfig, null);
    }

    protected void doInsert(final WebCrawlingConfig webCrawlingConfig,
            final InsertOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfig", webCrawlingConfig);
        prepareInsertOption(option);
        delegateInsert(webCrawlingConfig, option);
    }

    protected void prepareInsertOption(
            final InsertOption<WebCrawlingConfigCB> option) {
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     webCrawlingConfigBhv.<span style="color: #FD4747">update</span>(webCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param webCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final WebCrawlingConfig webCrawlingConfig) {
        doUpdate(webCrawlingConfig, null);
    }

    protected void doUpdate(final WebCrawlingConfig webCrawlingConfig,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfig", webCrawlingConfig);
        prepareUpdateOption(option);
        helpUpdateInternally(webCrawlingConfig,
                new InternalUpdateCallback<WebCrawlingConfig>() {
                    @Override
                    public int callbackDelegateUpdate(
                            final WebCrawlingConfig entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(
            final UpdateOption<WebCrawlingConfigCB> option) {
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

    protected WebCrawlingConfigCB createCBForVaryingUpdate() {
        final WebCrawlingConfigCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected WebCrawlingConfigCB createCBForSpecifiedUpdate() {
        final WebCrawlingConfigCB cb = newMyConditionBean();
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * webCrawlingConfigBhv.<span style="color: #FD4747">updateNonstrict</span>(webCrawlingConfig);
     * </pre>
     * @param webCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final WebCrawlingConfig webCrawlingConfig) {
        doUpdateNonstrict(webCrawlingConfig, null);
    }

    protected void doUpdateNonstrict(final WebCrawlingConfig webCrawlingConfig,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfig", webCrawlingConfig);
        prepareUpdateOption(option);
        helpUpdateNonstrictInternally(webCrawlingConfig,
                new InternalUpdateNonstrictCallback<WebCrawlingConfig>() {
                    @Override
                    public int callbackDelegateUpdateNonstrict(
                            final WebCrawlingConfig entity) {
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
     * @param webCrawlingConfig The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final WebCrawlingConfig webCrawlingConfig) {
        doInesrtOrUpdate(webCrawlingConfig, null, null);
    }

    protected void doInesrtOrUpdate(final WebCrawlingConfig webCrawlingConfig,
            final InsertOption<WebCrawlingConfigCB> insertOption,
            final UpdateOption<WebCrawlingConfigCB> updateOption) {
        helpInsertOrUpdateInternally(
                webCrawlingConfig,
                new InternalInsertOrUpdateCallback<WebCrawlingConfig, WebCrawlingConfigCB>() {
                    @Override
                    public void callbackInsert(final WebCrawlingConfig entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdate(final WebCrawlingConfig entity) {
                        doUpdate(entity, updateOption);
                    }

                    @Override
                    public WebCrawlingConfigCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    @Override
                    public int callbackSelectCount(final WebCrawlingConfigCB cb) {
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
            insertOption = insertOption == null ? new InsertOption<WebCrawlingConfigCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<WebCrawlingConfigCB>()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity), downcast(insertOption),
                    downcast(updateOption));
        }
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl)
     * @param webCrawlingConfig The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(
            final WebCrawlingConfig webCrawlingConfig) {
        doInesrtOrUpdateNonstrict(webCrawlingConfig, null, null);
    }

    protected void doInesrtOrUpdateNonstrict(
            final WebCrawlingConfig webCrawlingConfig,
            final InsertOption<WebCrawlingConfigCB> insertOption,
            final UpdateOption<WebCrawlingConfigCB> updateOption) {
        helpInsertOrUpdateInternally(
                webCrawlingConfig,
                new InternalInsertOrUpdateNonstrictCallback<WebCrawlingConfig>() {
                    @Override
                    public void callbackInsert(final WebCrawlingConfig entity) {
                        doInsert(entity, insertOption);
                    }

                    @Override
                    public void callbackUpdateNonstrict(
                            final WebCrawlingConfig entity) {
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
            insertOption = insertOption == null ? new InsertOption<WebCrawlingConfigCB>()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption<WebCrawlingConfigCB>()
                    : updateOption;
            varyingInsertOrUpdateNonstrict(downcast(entity),
                    downcast(insertOption), downcast(updateOption));
        }
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     webCrawlingConfigBhv.<span style="color: #FD4747">delete</span>(webCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param webCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final WebCrawlingConfig webCrawlingConfig) {
        doDelete(webCrawlingConfig, null);
    }

    protected void doDelete(final WebCrawlingConfig webCrawlingConfig,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfig", webCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteInternally(webCrawlingConfig,
                new InternalDeleteCallback<WebCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDelete(
                            final WebCrawlingConfig entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(
            final DeleteOption<WebCrawlingConfigCB> option) {
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * webCrawlingConfigBhv.<span style="color: #FD4747">deleteNonstrict</span>(webCrawlingConfig);
     * </pre>
     * @param webCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final WebCrawlingConfig webCrawlingConfig) {
        doDeleteNonstrict(webCrawlingConfig, null);
    }

    protected void doDeleteNonstrict(final WebCrawlingConfig webCrawlingConfig,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfig", webCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteNonstrictInternally(webCrawlingConfig,
                new InternalDeleteNonstrictCallback<WebCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final WebCrawlingConfig entity) {
                        return delegateDeleteNonstrict(entity, option);
                    }
                });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * webCrawlingConfigBhv.<span style="color: #FD4747">deleteNonstrictIgnoreDeleted</span>(webCrawlingConfig);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param webCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(
            final WebCrawlingConfig webCrawlingConfig) {
        doDeleteNonstrictIgnoreDeleted(webCrawlingConfig, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(
            final WebCrawlingConfig webCrawlingConfig,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfig", webCrawlingConfig);
        prepareDeleteOption(option);
        helpDeleteNonstrictIgnoreDeletedInternally(
                webCrawlingConfig,
                new InternalDeleteNonstrictIgnoreDeletedCallback<WebCrawlingConfig>() {
                    @Override
                    public int callbackDelegateDeleteNonstrict(
                            final WebCrawlingConfig entity) {
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
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchInsert(webCrawlingConfigList, null);
    }

    protected int[] doBatchInsert(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final InsertOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfigList", webCrawlingConfigList);
        prepareInsertOption(option);
        return delegateBatchInsert(webCrawlingConfigList, option);
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
     * webCrawlingConfigBhv.<span style="color: #FD4747">batchUpdate</span>(webCrawlingConfigList, new SpecifyQuery<WebCrawlingConfigCB>() {
     *     public void specify(WebCrawlingConfigCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchUpdate(webCrawlingConfigList, null);
    }

    protected int[] doBatchUpdate(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfigList", webCrawlingConfigList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(webCrawlingConfigList, option);
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
     * webCrawlingConfigBhv.<span style="color: #FD4747">batchUpdate</span>(webCrawlingConfigList, new SpecifyQuery<WebCrawlingConfigCB>() {
     *     public void specify(WebCrawlingConfigCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final SpecifyQuery<WebCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdate(webCrawlingConfigList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly. (AllColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747">All columns are update target. {NOT modified only}</span>
     * So you should the other batchUpdate() method, which you can specify update columns like this:
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(webCrawlingConfigList, new SpecifyQuery<WebCrawlingConfigCB>() {
     *     public void specify(WebCrawlingConfigCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchUpdateNonstrict(webCrawlingConfigList, null);
    }

    protected int[] doBatchUpdateNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfigList", webCrawlingConfigList);
        prepareUpdateOption(option);
        return delegateBatchUpdateNonstrict(webCrawlingConfigList, option);
    }

    /**
     * Batch-update the entity list non-strictly. (SpecifiedColumnsUpdated, NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistick lock column because they are specified implicitly.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #FD4747">batchUpdateNonstrict</span>(webCrawlingConfigList, new SpecifyQuery<WebCrawlingConfigCB>() {
     *     public void specify(WebCrawlingConfigCB cb) { <span style="color: #3F7E5E">// FOO_STATUS_CODE, BAR_DATE only updated</span>
     *         cb.specify().columnFooStatusCode();
     *         cb.specify().columnBarDate();
     *     }
     * });
     * </pre>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final SpecifyQuery<WebCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(webCrawlingConfigList,
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
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchDelete(webCrawlingConfigList, null);
    }

    protected int[] doBatchDelete(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfigList", webCrawlingConfigList);
        prepareDeleteOption(option);
        return delegateBatchDelete(webCrawlingConfigList, option);
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
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchDeleteNonstrict(webCrawlingConfigList, null);
    }

    protected int[] doBatchDeleteNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfigList", webCrawlingConfigList);
        prepareDeleteOption(option);
        return delegateBatchDeleteNonstrict(webCrawlingConfigList, option);
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
     * webCrawlingConfigBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;WebCrawlingConfig, WebCrawlingConfigCB&gt;() {
     *     public ConditionBean setup(webCrawlingConfig entity, WebCrawlingConfigCB intoCB) {
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
            final QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB> setupper,
            final InsertOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final WebCrawlingConfig entity = new WebCrawlingConfig();
        final WebCrawlingConfigCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected WebCrawlingConfigCB createCBForQueryInsert() {
        final WebCrawlingConfigCB cb = newMyConditionBean();
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setPK...(value);</span>
     * webCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * webCrawlingConfigBhv.<span style="color: #FD4747">queryUpdate</span>(webCrawlingConfig, cb);
     * </pre>
     * @param webCrawlingConfig The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final WebCrawlingConfig webCrawlingConfig,
            final WebCrawlingConfigCB cb) {
        return doQueryUpdate(webCrawlingConfig, cb, null);
    }

    protected int doQueryUpdate(final WebCrawlingConfig webCrawlingConfig,
            final WebCrawlingConfigCB cb,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertObjectNotNull("webCrawlingConfig", webCrawlingConfig);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
                webCrawlingConfig, cb, option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (WebCrawlingConfigCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (WebCrawlingConfigCB) cb, downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * webCrawlingConfigBhv.<span style="color: #FD4747">queryDelete</span>(webCrawlingConfig, cb);
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final WebCrawlingConfigCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final WebCrawlingConfigCB cb,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb,
                option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((WebCrawlingConfigCB) cb);
        } else {
            return varyingQueryDelete((WebCrawlingConfigCB) cb,
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * webCrawlingConfig.setFoo...(value);
     * webCrawlingConfig.setBar...(value);
     * InsertOption<WebCrawlingConfigCB> option = new InsertOption<WebCrawlingConfigCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * webCrawlingConfigBhv.<span style="color: #FD4747">varyingInsert</span>(webCrawlingConfig, option);
     * ... = webCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param webCrawlingConfig The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(final WebCrawlingConfig webCrawlingConfig,
            final InsertOption<WebCrawlingConfigCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(webCrawlingConfig, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, ExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * webCrawlingConfig.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;WebCrawlingConfigCB&gt; option = new UpdateOption&lt;WebCrawlingConfigCB&gt;();
     *     option.self(new SpecifyQuery&lt;WebCrawlingConfigCB&gt;() {
     *         public void specify(WebCrawlingConfigCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     webCrawlingConfigBhv.<span style="color: #FD4747">varyingUpdate</span>(webCrawlingConfig, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param webCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(final WebCrawlingConfig webCrawlingConfig,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(webCrawlingConfig, option);
    }

    /**
     * Update the entity with varying requests non-strictly modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as updateNonstrict(entity).
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * UpdateOption&lt;WebCrawlingConfigCB&gt; option = new UpdateOption&lt;WebCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void specify(WebCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * webCrawlingConfigBhv.<span style="color: #FD4747">varyingUpdateNonstrict</span>(webCrawlingConfig, option);
     * </pre>
     * @param webCrawlingConfig The entity of update target. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdateNonstrict(
            final WebCrawlingConfig webCrawlingConfig,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdateNonstrict(webCrawlingConfig, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param webCrawlingConfig The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final WebCrawlingConfig webCrawlingConfig,
            final InsertOption<WebCrawlingConfigCB> insertOption,
            final UpdateOption<WebCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(webCrawlingConfig, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param webCrawlingConfig The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final WebCrawlingConfig webCrawlingConfig,
            final InsertOption<WebCrawlingConfigCB> insertOption,
            final UpdateOption<WebCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdateNonstrict(webCrawlingConfig, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param webCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(final WebCrawlingConfig webCrawlingConfig,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(webCrawlingConfig, option);
    }

    /**
     * Delete the entity with varying requests non-strictly. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as deleteNonstrict(entity).
     * @param webCrawlingConfig The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDeleteNonstrict(
            final WebCrawlingConfig webCrawlingConfig,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        doDeleteNonstrict(webCrawlingConfig, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final InsertOption<WebCrawlingConfigCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(webCrawlingConfigList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(webCrawlingConfigList, option);
    }

    /**
     * Batch-update the list with varying requests non-strictly. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdateNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdateNonstrict(webCrawlingConfigList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(webCrawlingConfigList, option);
    }

    /**
     * Batch-delete the list with varying requests non-strictly. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDeleteNonstrict(entityList).
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDeleteNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final DeleteOption<WebCrawlingConfigCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDeleteNonstrict(webCrawlingConfigList, option);
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
            final QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB> setupper,
            final InsertOption<WebCrawlingConfigCB> option) {
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setPK...(value);</span>
     * webCrawlingConfig.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;WebCrawlingConfigCB&gt; option = new UpdateOption&lt;WebCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void specify(WebCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * webCrawlingConfigBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(webCrawlingConfig, cb, option);
     * </pre>
     * @param webCrawlingConfig The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final WebCrawlingConfig webCrawlingConfig,
            final WebCrawlingConfigCB cb,
            final UpdateOption<WebCrawlingConfigCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(webCrawlingConfig, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final WebCrawlingConfigCB cb,
            final DeleteOption<WebCrawlingConfigCB> option) {
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
    public OutsideSqlBasicExecutor<WebCrawlingConfigBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(final WebCrawlingConfigCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final WebCrawlingConfigCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends WebCrawlingConfig> void delegateSelectCursor(
            final WebCrawlingConfigCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends WebCrawlingConfig> List<ENTITY> delegateSelectList(
            final WebCrawlingConfigCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(final WebCrawlingConfig e,
            final InsertOption<WebCrawlingConfigCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final WebCrawlingConfig e,
            final UpdateOption<WebCrawlingConfigCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateEntityCommand(e, op));
    }

    protected int delegateUpdateNonstrict(final WebCrawlingConfig e,
            final UpdateOption<WebCrawlingConfigCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final WebCrawlingConfig e,
            final DeleteOption<WebCrawlingConfigCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteEntityCommand(e, op));
    }

    protected int delegateDeleteNonstrict(final WebCrawlingConfig e,
            final DeleteOption<WebCrawlingConfigCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<WebCrawlingConfig> ls,
            final InsertOption<WebCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(final List<WebCrawlingConfig> ls,
            final UpdateOption<WebCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchUpdateNonstrict(
            final List<WebCrawlingConfig> ls,
            final UpdateOption<WebCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(final List<WebCrawlingConfig> ls,
            final DeleteOption<WebCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteCommand(
                processBatchInternally(ls, op, false), op));
    }

    protected int[] delegateBatchDeleteNonstrict(
            final List<WebCrawlingConfig> ls,
            final DeleteOption<WebCrawlingConfigCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(final WebCrawlingConfig e,
            final WebCrawlingConfigCB inCB, final ConditionBean resCB,
            final InsertOption<WebCrawlingConfigCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final WebCrawlingConfig e,
            final WebCrawlingConfigCB cb,
            final UpdateOption<WebCrawlingConfigCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final WebCrawlingConfigCB cb,
            final DeleteOption<WebCrawlingConfigCB> op) {
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
    protected WebCrawlingConfig downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, WebCrawlingConfig.class);
    }

    protected WebCrawlingConfigCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                WebCrawlingConfigCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<WebCrawlingConfig> downcast(
            final List<? extends Entity> entityList) {
        return (List<WebCrawlingConfig>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<WebCrawlingConfigCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<WebCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<WebCrawlingConfigCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<WebCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<WebCrawlingConfigCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<WebCrawlingConfigCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB>) option;
    }
}
