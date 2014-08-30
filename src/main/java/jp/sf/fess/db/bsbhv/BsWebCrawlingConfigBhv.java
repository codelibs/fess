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

import jp.sf.fess.db.bsbhv.loader.LoaderOfWebCrawlingConfig;
import jp.sf.fess.db.bsentity.dbmeta.WebCrawlingConfigDbm;
import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.cbean.WebAuthenticationCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.exbhv.WebCrawlingConfigBhv;
import jp.sf.fess.db.exentity.RequestHeader;
import jp.sf.fess.db.exentity.WebAuthentication;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.AbstractBehaviorWritable;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.InsertOption;
import org.seasar.dbflute.bhv.LoadReferrerOption;
import org.seasar.dbflute.bhv.NestedReferrerListGateway;
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
import org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException;
import org.seasar.dbflute.exception.DangerousResultSizeException;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.dbflute.exception.EntityDuplicatedException;
import org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException;
import org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException;
import org.seasar.dbflute.exception.SelectEntityConditionNotFoundException;
import org.seasar.dbflute.optional.OptionalEntity;
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
 *     REQUEST_HEADER, WEB_AUTHENTICATION, WEB_CONFIG_TO_LABEL_TYPE_MAPPING, WEB_CONFIG_TO_ROLE_TYPE_MAPPING
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     requestHeaderList, webAuthenticationList, webConfigToLabelTypeMappingList, webConfigToRoleTypeMappingList
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
    /** {@inheritDoc} */
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
    public WebCrawlingConfig newEntity() {
        return new WebCrawlingConfig();
    }

    /** {@inheritDoc} */
    @Override
    public WebCrawlingConfigCB newConditionBean() {
        return new WebCrawlingConfigCB();
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
     * int count = webCrawlingConfigBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final WebCrawlingConfigCB cb) {
        return facadeSelectCount(cb);
    }

    protected int facadeSelectCount(final WebCrawlingConfigCB cb) {
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * WebCrawlingConfig webCrawlingConfig = webCrawlingConfigBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (webCrawlingConfig != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = webCrawlingConfig.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public WebCrawlingConfig selectEntity(final WebCrawlingConfigCB cb) {
        return facadeSelectEntity(cb);
    }

    protected WebCrawlingConfig facadeSelectEntity(final WebCrawlingConfigCB cb) {
        return doSelectEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebCrawlingConfig> ENTITY doSelectEntity(
            final WebCrawlingConfigCB cb, final Class<ENTITY> tp) {
        return helpSelectEntityInternally(cb, tp);
    }

    protected <ENTITY extends WebCrawlingConfig> OptionalEntity<ENTITY> doSelectOptionalEntity(
            final WebCrawlingConfigCB cb, final Class<ENTITY> tp) {
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * WebCrawlingConfig webCrawlingConfig = webCrawlingConfigBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = webCrawlingConfig.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public WebCrawlingConfig selectEntityWithDeletedCheck(
            final WebCrawlingConfigCB cb) {
        return facadeSelectEntityWithDeletedCheck(cb);
    }

    protected WebCrawlingConfig facadeSelectEntityWithDeletedCheck(
            final WebCrawlingConfigCB cb) {
        return doSelectEntityWithDeletedCheck(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebCrawlingConfig> ENTITY doSelectEntityWithDeletedCheck(
            final WebCrawlingConfigCB cb, final Class<ENTITY> tp) {
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
    public WebCrawlingConfig selectByPKValue(final Long id) {
        return facadeSelectByPKValue(id);
    }

    protected WebCrawlingConfig facadeSelectByPKValue(final Long id) {
        return doSelectByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebCrawlingConfig> ENTITY doSelectByPK(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected <ENTITY extends WebCrawlingConfig> OptionalEntity<ENTITY> doSelectOptionalByPK(
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
    public WebCrawlingConfig selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKWithDeletedCheck(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebCrawlingConfig> ENTITY doSelectByPKWithDeletedCheck(
            final Long id, final Class<ENTITY> tp) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), tp);
    }

    protected WebCrawlingConfigCB xprepareCBAsPK(final Long id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
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
     * ListResultBean&lt;WebCrawlingConfig&gt; webCrawlingConfigList = webCrawlingConfigBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.get...();
     * }
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<WebCrawlingConfig> selectList(
            final WebCrawlingConfigCB cb) {
        return facadeSelectList(cb);
    }

    protected ListResultBean<WebCrawlingConfig> facadeSelectList(
            final WebCrawlingConfigCB cb) {
        return doSelectList(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebCrawlingConfig> ListResultBean<ENTITY> doSelectList(
            final WebCrawlingConfigCB cb, final Class<ENTITY> tp) {
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;WebCrawlingConfig&gt; page = webCrawlingConfigBhv.<span style="color: #DD4747">selectPage</span>(cb);
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
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<WebCrawlingConfig> selectPage(
            final WebCrawlingConfigCB cb) {
        return facadeSelectPage(cb);
    }

    protected PagingResultBean<WebCrawlingConfig> facadeSelectPage(
            final WebCrawlingConfigCB cb) {
        return doSelectPage(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebCrawlingConfig> PagingResultBean<ENTITY> doSelectPage(
            final WebCrawlingConfigCB cb, final Class<ENTITY> tp) {
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * webCrawlingConfigBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;WebCrawlingConfig&gt;() {
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
        facadeSelectCursor(cb, entityRowHandler);
    }

    protected void facadeSelectCursor(final WebCrawlingConfigCB cb,
            final EntityRowHandler<WebCrawlingConfig> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebCrawlingConfig> void doSelectCursor(
            final WebCrawlingConfigCB cb,
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
     * webCrawlingConfigBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(WebCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> HpSLSFunction<WebCrawlingConfigCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return facadeScalarSelect(resultType);
    }

    protected <RESULT> HpSLSFunction<WebCrawlingConfigCB, RESULT> facadeScalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newConditionBean());
    }

    protected <RESULT, CB extends WebCrawlingConfigCB> HpSLSFunction<CB, RESULT> doScalarSelect(
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
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final List<WebCrawlingConfig> webCrawlingConfigList,
            final ReferrerLoaderHandler<LoaderOfWebCrawlingConfig> handler) {
        xassLRArg(webCrawlingConfigList, handler);
        handler.handle(new LoaderOfWebCrawlingConfig().ready(
                webCrawlingConfigList, _behaviorSelector));
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
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param handler The callback to handle the referrer loader for actually loading referrer. (NotNull)
     */
    public void load(final WebCrawlingConfig webCrawlingConfig,
            final ReferrerLoaderHandler<LoaderOfWebCrawlingConfig> handler) {
        xassLRArg(webCrawlingConfig, handler);
        handler.handle(new LoaderOfWebCrawlingConfig().ready(
                xnewLRAryLs(webCrawlingConfig), _behaviorSelector));
    }

    /**
     * Load referrer of requestHeaderList by the set-upper of referrer. <br />
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">loadRequestHeaderList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;RequestHeaderCB&gt;() {
     *     public void setup(RequestHeaderCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #DD4747">getRequestHeaderList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setWebCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebCrawlingConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<RequestHeader> loadRequestHeaderList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<RequestHeaderCB> setupper) {
        xassLRArg(webCrawlingConfigList, setupper);
        return doLoadRequestHeaderList(webCrawlingConfigList,
                new LoadReferrerOption<RequestHeaderCB, RequestHeader>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of requestHeaderList by the set-upper of referrer. <br />
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">loadRequestHeaderList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;RequestHeaderCB&gt;() {
     *     public void setup(RequestHeaderCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = webCrawlingConfig.<span style="color: #DD4747">getRequestHeaderList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setWebCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebCrawlingConfigId_Asc();
     * </pre>
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<RequestHeader> loadRequestHeaderList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<RequestHeaderCB> setupper) {
        xassLRArg(webCrawlingConfig, setupper);
        return doLoadRequestHeaderList(xnewLRLs(webCrawlingConfig),
                new LoadReferrerOption<RequestHeaderCB, RequestHeader>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<RequestHeader> loadRequestHeaderList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<RequestHeaderCB, RequestHeader> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        return loadRequestHeaderList(xnewLRLs(webCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<RequestHeader> loadRequestHeaderList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<RequestHeaderCB, RequestHeader> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return (NestedReferrerListGateway<RequestHeader>) EMPTY_NREF_LGWAY;
        }
        return doLoadRequestHeaderList(webCrawlingConfigList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<RequestHeader> doLoadRequestHeaderList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<RequestHeaderCB, RequestHeader> option) {
        return helpLoadReferrerInternally(webCrawlingConfigList, option,
                "requestHeaderList");
    }

    /**
     * Load referrer of webAuthenticationList by the set-upper of referrer. <br />
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">loadWebAuthenticationList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebAuthenticationCB&gt;() {
     *     public void setup(WebAuthenticationCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #DD4747">getWebAuthenticationList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setWebCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebCrawlingConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebAuthentication> loadWebAuthenticationList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<WebAuthenticationCB> setupper) {
        xassLRArg(webCrawlingConfigList, setupper);
        return doLoadWebAuthenticationList(
                webCrawlingConfigList,
                new LoadReferrerOption<WebAuthenticationCB, WebAuthentication>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of webAuthenticationList by the set-upper of referrer. <br />
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">loadWebAuthenticationList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebAuthenticationCB&gt;() {
     *     public void setup(WebAuthenticationCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = webCrawlingConfig.<span style="color: #DD4747">getWebAuthenticationList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setWebCrawlingConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebCrawlingConfigId_Asc();
     * </pre>
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebAuthentication> loadWebAuthenticationList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<WebAuthenticationCB> setupper) {
        xassLRArg(webCrawlingConfig, setupper);
        return doLoadWebAuthenticationList(
                xnewLRLs(webCrawlingConfig),
                new LoadReferrerOption<WebAuthenticationCB, WebAuthentication>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebAuthentication> loadWebAuthenticationList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<WebAuthenticationCB, WebAuthentication> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        return loadWebAuthenticationList(xnewLRLs(webCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<WebAuthentication> loadWebAuthenticationList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebAuthenticationCB, WebAuthentication> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return (NestedReferrerListGateway<WebAuthentication>) EMPTY_NREF_LGWAY;
        }
        return doLoadWebAuthenticationList(webCrawlingConfigList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<WebAuthentication> doLoadWebAuthenticationList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebAuthenticationCB, WebAuthentication> option) {
        return helpLoadReferrerInternally(webCrawlingConfigList, option,
                "webAuthenticationList");
    }

    /**
     * Load referrer of webConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">loadWebConfigToLabelTypeMappingList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(WebConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #DD4747">getWebConfigToLabelTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setWebConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(webCrawlingConfigList, setupper);
        return doLoadWebConfigToLabelTypeMappingList(
                webCrawlingConfigList,
                new LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of webConfigToLabelTypeMappingList by the set-upper of referrer. <br />
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">loadWebConfigToLabelTypeMappingList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void setup(WebConfigToLabelTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = webCrawlingConfig.<span style="color: #DD4747">getWebConfigToLabelTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setWebConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebConfigId_Asc();
     * </pre>
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> setupper) {
        xassLRArg(webCrawlingConfig, setupper);
        return doLoadWebConfigToLabelTypeMappingList(
                xnewLRLs(webCrawlingConfig),
                new LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        return loadWebConfigToLabelTypeMappingList(xnewLRLs(webCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<WebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return (NestedReferrerListGateway<WebConfigToLabelTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadWebConfigToLabelTypeMappingList(webCrawlingConfigList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<WebConfigToLabelTypeMapping> doLoadWebConfigToLabelTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebConfigToLabelTypeMappingCB, WebConfigToLabelTypeMapping> option) {
        return helpLoadReferrerInternally(webCrawlingConfigList, option,
                "webConfigToLabelTypeMappingList");
    }

    /**
     * Load referrer of webConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">loadWebConfigToRoleTypeMappingList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(WebConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
     *     ... = webCrawlingConfig.<span style="color: #DD4747">getWebConfigToRoleTypeMappingList()</span>;
     * }
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setWebConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebConfigId_Asc();
     * </pre>
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(webCrawlingConfigList, setupper);
        return doLoadWebConfigToRoleTypeMappingList(
                webCrawlingConfigList,
                new LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * Load referrer of webConfigToRoleTypeMappingList by the set-upper of referrer. <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingList'.
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">loadWebConfigToRoleTypeMappingList</span>(webCrawlingConfigList, new ConditionBeanSetupper&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void setup(WebConfigToRoleTypeMappingCB cb) {
     *         cb.setupSelect...();
     *         cb.query().setFoo...(value);
     *         cb.query().addOrderBy_Bar...();
     *     }
     * }); <span style="color: #3F7E5E">// you can load nested referrer from here</span>
     * <span style="color: #3F7E5E">//}).withNestedList(referrerList -&gt {</span>
     * <span style="color: #3F7E5E">//    ...</span>
     * <span style="color: #3F7E5E">//});</span>
     * ... = webCrawlingConfig.<span style="color: #DD4747">getWebConfigToRoleTypeMappingList()</span>;
     * </pre>
     * About internal policy, the value of primary key (and others too) is treated as case-insensitive. <br />
     * The condition-bean, which the set-upper provides, has settings before callback as follows:
     * <pre>
     * cb.query().setWebConfigId_InScope(pkList);
     * cb.query().addOrderBy_WebConfigId_Asc();
     * </pre>
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param setupper The callback to set up referrer condition-bean for loading referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> setupper) {
        xassLRArg(webCrawlingConfig, setupper);
        return doLoadWebConfigToRoleTypeMappingList(
                xnewLRLs(webCrawlingConfig),
                new LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping>()
                        .xinit(setupper));
    }

    /**
     * {Refer to overload method that has an argument of the list of entity.} #beforejava8
     * @param webCrawlingConfig The entity of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    public NestedReferrerListGateway<WebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final WebCrawlingConfig webCrawlingConfig,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfig, loadReferrerOption);
        return loadWebConfigToRoleTypeMappingList(xnewLRLs(webCrawlingConfig),
                loadReferrerOption);
    }

    /**
     * {Refer to overload method that has an argument of condition-bean setupper.} #beforejava8
     * @param webCrawlingConfigList The entity list of webCrawlingConfig. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     * @return The callback interface which you can load nested referrer by calling withNestedReferrer(). (NotNull)
     */
    @SuppressWarnings("unchecked")
    public NestedReferrerListGateway<WebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> loadReferrerOption) {
        xassLRArg(webCrawlingConfigList, loadReferrerOption);
        if (webCrawlingConfigList.isEmpty()) {
            return (NestedReferrerListGateway<WebConfigToRoleTypeMapping>) EMPTY_NREF_LGWAY;
        }
        return doLoadWebConfigToRoleTypeMappingList(webCrawlingConfigList,
                loadReferrerOption);
    }

    protected NestedReferrerListGateway<WebConfigToRoleTypeMapping> doLoadWebConfigToRoleTypeMappingList(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final LoadReferrerOption<WebConfigToRoleTypeMappingCB, WebConfigToRoleTypeMapping> option) {
        return helpLoadReferrerInternally(webCrawlingConfigList, option,
                "webConfigToRoleTypeMappingList");
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
        return helpExtractListInternally(webCrawlingConfigList, "id");
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * webCrawlingConfig.setFoo...(value);
     * webCrawlingConfig.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.set...;</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">insert</span>(webCrawlingConfig);
     * ... = webCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param webCrawlingConfig The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(final WebCrawlingConfig webCrawlingConfig) {
        doInsert(webCrawlingConfig, null);
    }

    protected void doInsert(final WebCrawlingConfig et,
            final InsertOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfig", et);
        prepareInsertOption(op);
        delegateInsert(et, op);
    }

    protected void prepareInsertOption(
            final InsertOption<WebCrawlingConfigCB> op) {
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
     * Update the entity modified-only. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * webCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * webCrawlingConfig.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     webCrawlingConfigBhv.<span style="color: #DD4747">update</span>(webCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param webCrawlingConfig The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final WebCrawlingConfig webCrawlingConfig) {
        doUpdate(webCrawlingConfig, null);
    }

    protected void doUpdate(final WebCrawlingConfig et,
            final UpdateOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfig", et);
        prepareUpdateOption(op);
        helpUpdateInternally(et, op);
    }

    protected void prepareUpdateOption(
            final UpdateOption<WebCrawlingConfigCB> op) {
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

    protected WebCrawlingConfigCB createCBForVaryingUpdate() {
        final WebCrawlingConfigCB cb = newConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected WebCrawlingConfigCB createCBForSpecifiedUpdate() {
        final WebCrawlingConfigCB cb = newConditionBean();
        cb.xsetupForSpecifiedUpdate();
        return cb;
    }

    @Override
    protected void doModify(final Entity et,
            final UpdateOption<? extends ConditionBean> op) {
        doUpdate(downcast(et), downcast(op));
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
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">updateNonstrict</span>(webCrawlingConfig);
     * </pre>
     * @param webCrawlingConfig The entity of update. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void updateNonstrict(final WebCrawlingConfig webCrawlingConfig) {
        doUpdateNonstrict(webCrawlingConfig, null);
    }

    protected void doUpdateNonstrict(final WebCrawlingConfig et,
            final UpdateOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfig", et);
        prepareUpdateOption(op);
        helpUpdateNonstrictInternally(et, op);
    }

    @Override
    protected void doModifyNonstrict(final Entity et,
            final UpdateOption<? extends ConditionBean> op) {
        doUpdateNonstrict(downcast(et), downcast(op));
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, ExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param webCrawlingConfig The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(final WebCrawlingConfig webCrawlingConfig) {
        doInsertOrUpdate(webCrawlingConfig, null, null);
    }

    protected void doInsertOrUpdate(final WebCrawlingConfig et,
            final InsertOption<WebCrawlingConfigCB> iop,
            final UpdateOption<WebCrawlingConfigCB> uop) {
        assertObjectNotNull("webCrawlingConfig", et);
        helpInsertOrUpdateInternally(et, iop, uop);
    }

    @Override
    protected void doCreateOrModify(final Entity et,
            final InsertOption<? extends ConditionBean> iop,
            final UpdateOption<? extends ConditionBean> uop) {
        doInsertOrUpdate(downcast(et), downcast(iop), downcast(uop));
    }

    /**
     * Insert or update the entity non-strictly modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() }
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param webCrawlingConfig The entity of insert or update. (NotNull, ...depends on insert or update)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdateNonstrict(
            final WebCrawlingConfig webCrawlingConfig) {
        doInsertOrUpdateNonstrict(webCrawlingConfig, null, null);
    }

    protected void doInsertOrUpdateNonstrict(final WebCrawlingConfig et,
            final InsertOption<WebCrawlingConfigCB> iop,
            final UpdateOption<WebCrawlingConfigCB> uop) {
        assertObjectNotNull("webCrawlingConfig", et);
        helpInsertOrUpdateNonstrictInternally(et, iop, uop);
    }

    @Override
    protected void doCreateOrModifyNonstrict(final Entity et,
            final InsertOption<? extends ConditionBean> iop,
            final UpdateOption<? extends ConditionBean> uop) {
        doInsertOrUpdateNonstrict(downcast(et), downcast(iop), downcast(uop));
    }

    /**
     * Delete the entity. (ZeroUpdateException, ExclusiveControl)
     * <pre>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * webCrawlingConfig.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     webCrawlingConfigBhv.<span style="color: #DD4747">delete</span>(webCrawlingConfig);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param webCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(final WebCrawlingConfig webCrawlingConfig) {
        doDelete(webCrawlingConfig, null);
    }

    protected void doDelete(final WebCrawlingConfig et,
            final DeleteOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfig", et);
        prepareDeleteOption(op);
        helpDeleteInternally(et, op);
    }

    protected void prepareDeleteOption(
            final DeleteOption<WebCrawlingConfigCB> op) {
        if (op != null) {
            assertDeleteOptionStatus(op);
        }
    }

    @Override
    protected void doRemove(final Entity et,
            final DeleteOption<? extends ConditionBean> op) {
        doDelete(downcast(et), downcast(op));
    }

    /**
     * Delete the entity non-strictly. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">deleteNonstrict</span>(webCrawlingConfig);
     * </pre>
     * @param webCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(final WebCrawlingConfig webCrawlingConfig) {
        doDeleteNonstrict(webCrawlingConfig, null);
    }

    protected void doDeleteNonstrict(final WebCrawlingConfig et,
            final DeleteOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfig", et);
        prepareDeleteOption(op);
        helpDeleteNonstrictInternally(et, op);
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {ZeroUpdateException, NonExclusiveControl}
     * <pre>
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * webCrawlingConfig.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">deleteNonstrictIgnoreDeleted</span>(webCrawlingConfig);
     * <span style="color: #3F7E5E">// if the target entity doesn't exist, no exception</span>
     * </pre>
     * @param webCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(
            final WebCrawlingConfig webCrawlingConfig) {
        doDeleteNonstrictIgnoreDeleted(webCrawlingConfig, null);
    }

    protected void doDeleteNonstrictIgnoreDeleted(final WebCrawlingConfig et,
            final DeleteOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfig", et);
        prepareDeleteOption(op);
        helpDeleteNonstrictIgnoreDeletedInternally(et, op);
    }

    @Override
    protected void doRemoveNonstrict(final Entity et,
            final DeleteOption<? extends ConditionBean> op) {
        doDeleteNonstrict(downcast(et), downcast(op));
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
     *     WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     *     webCrawlingConfig.setFooName("foo");
     *     if (...) {
     *         webCrawlingConfig.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     webCrawlingConfigList.add(webCrawlingConfig);
     * }
     * webCrawlingConfigBhv.<span style="color: #DD4747">batchInsert</span>(webCrawlingConfigList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchInsert(webCrawlingConfigList, null);
    }

    protected int[] doBatchInsert(final List<WebCrawlingConfig> ls,
            final InsertOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfigList", ls);
        InsertOption<WebCrawlingConfigCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainInsertOption();
        }
        prepareBatchInsertOption(ls, rlop); // required
        return delegateBatchInsert(ls, rlop);
    }

    protected void prepareBatchInsertOption(final List<WebCrawlingConfig> ls,
            final InsertOption<WebCrawlingConfigCB> op) {
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
     * Batch-update the entity list modified-only of same-set columns. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     *     webCrawlingConfig.setFooName("foo");
     *     if (...) {
     *         webCrawlingConfig.setFooPrice(123);
     *     } else {
     *         webCrawlingConfig.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//webCrawlingConfig.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     webCrawlingConfigList.add(webCrawlingConfig);
     * }
     * webCrawlingConfigBhv.<span style="color: #DD4747">batchUpdate</span>(webCrawlingConfigList);
     * </pre>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchUpdate(webCrawlingConfigList, null);
    }

    protected int[] doBatchUpdate(final List<WebCrawlingConfig> ls,
            final UpdateOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfigList", ls);
        UpdateOption<WebCrawlingConfigCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainUpdateOption();
        }
        prepareBatchUpdateOption(ls, rlop); // required
        return delegateBatchUpdate(ls, rlop);
    }

    protected void prepareBatchUpdateOption(final List<WebCrawlingConfig> ls,
            final UpdateOption<WebCrawlingConfigCB> op) {
        op.xacceptUpdateColumnModifiedPropertiesIfNeeds(ls);
        prepareUpdateOption(op);
    }

    @Override
    protected int[] doLumpModify(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> op) {
        return doBatchUpdate(downcast(ls), downcast(op));
    }

    /**
     * Batch-update the entity list specified-only. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">batchUpdate</span>(webCrawlingConfigList, new SpecifyQuery<WebCrawlingConfigCB>() {
     *     public void specify(WebCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">batchUpdate</span>(webCrawlingConfigList, new SpecifyQuery<WebCrawlingConfigCB>() {
     *     public void specify(WebCrawlingConfigCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchUpdate(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final SpecifyQuery<WebCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdate(webCrawlingConfigList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    /**
     * Batch-update the entity list non-strictly modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 140%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     *     webCrawlingConfig.setFooName("foo");
     *     if (...) {
     *         webCrawlingConfig.setFooPrice(123);
     *     } else {
     *         webCrawlingConfig.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//webCrawlingConfig.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     webCrawlingConfigList.add(webCrawlingConfig);
     * }
     * webCrawlingConfigBhv.<span style="color: #DD4747">batchUpdate</span>(webCrawlingConfigList);
     * </pre>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchUpdateNonstrict(webCrawlingConfigList, null);
    }

    protected int[] doBatchUpdateNonstrict(final List<WebCrawlingConfig> ls,
            final UpdateOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfigList", ls);
        UpdateOption<WebCrawlingConfigCB> rlop;
        if (op != null) {
            rlop = op;
        } else {
            rlop = createPlainUpdateOption();
        }
        prepareBatchUpdateOption(ls, rlop);
        return delegateBatchUpdateNonstrict(ls, rlop);
    }

    /**
     * Batch-update the entity list non-strictly specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(webCrawlingConfigList, new SpecifyQuery<WebCrawlingConfigCB>() {
     *     public void specify(WebCrawlingConfigCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">batchUpdateNonstrict</span>(webCrawlingConfigList, new SpecifyQuery<WebCrawlingConfigCB>() {
     *     public void specify(WebCrawlingConfigCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).</p>
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdateNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList,
            final SpecifyQuery<WebCrawlingConfigCB> updateColumnSpec) {
        return doBatchUpdateNonstrict(webCrawlingConfigList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> op) {
        return doBatchUpdateNonstrict(downcast(ls), downcast(op));
    }

    /**
     * Batch-delete the entity list. (ExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends EntityAlreadyUpdatedException.
     */
    public int[] batchDelete(final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchDelete(webCrawlingConfigList, null);
    }

    protected int[] doBatchDelete(final List<WebCrawlingConfig> ls,
            final DeleteOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfigList", ls);
        prepareDeleteOption(op);
        return delegateBatchDelete(ls, op);
    }

    @Override
    protected int[] doLumpRemove(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> op) {
        return doBatchDelete(downcast(ls), downcast(op));
    }

    /**
     * Batch-delete the entity list non-strictly. {NonExclusiveControl} <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param webCrawlingConfigList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDeleteNonstrict(
            final List<WebCrawlingConfig> webCrawlingConfigList) {
        return doBatchDeleteNonstrict(webCrawlingConfigList, null);
    }

    protected int[] doBatchDeleteNonstrict(final List<WebCrawlingConfig> ls,
            final DeleteOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfigList", ls);
        prepareDeleteOption(op);
        return delegateBatchDeleteNonstrict(ls, op);
    }

    @Override
    protected int[] doLumpRemoveNonstrict(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> op) {
        return doBatchDeleteNonstrict(downcast(ls), downcast(op));
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * webCrawlingConfigBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;WebCrawlingConfig, WebCrawlingConfigCB&gt;() {
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
            final QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB> sp,
            final InsertOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("setupper", sp);
        prepareInsertOption(op);
        final WebCrawlingConfig et = newEntity();
        final WebCrawlingConfigCB cb = createCBForQueryInsert();
        return delegateQueryInsert(et, cb, sp.setup(et, cb), op);
    }

    protected WebCrawlingConfigCB createCBForQueryInsert() {
        final WebCrawlingConfigCB cb = newConditionBean();
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setPK...(value);</span>
     * webCrawlingConfig.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * webCrawlingConfigBhv.<span style="color: #DD4747">queryUpdate</span>(webCrawlingConfig, cb);
     * </pre>
     * @param webCrawlingConfig The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(final WebCrawlingConfig webCrawlingConfig,
            final WebCrawlingConfigCB cb) {
        return doQueryUpdate(webCrawlingConfig, cb, null);
    }

    protected int doQueryUpdate(final WebCrawlingConfig et,
            final WebCrawlingConfigCB cb,
            final UpdateOption<WebCrawlingConfigCB> op) {
        assertObjectNotNull("webCrawlingConfig", et);
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
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * webCrawlingConfigBhv.<span style="color: #DD4747">queryDelete</span>(webCrawlingConfig, cb);
     * </pre>
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(final WebCrawlingConfigCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final WebCrawlingConfigCB cb,
            final DeleteOption<WebCrawlingConfigCB> op) {
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
     * WebCrawlingConfig webCrawlingConfig = new WebCrawlingConfig();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * webCrawlingConfig.setFoo...(value);
     * webCrawlingConfig.setBar...(value);
     * InsertOption<WebCrawlingConfigCB> option = new InsertOption<WebCrawlingConfigCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * webCrawlingConfigBhv.<span style="color: #DD4747">varyingInsert</span>(webCrawlingConfig, option);
     * ... = webCrawlingConfig.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param webCrawlingConfig The entity of insert. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * <span style="color: #3F7E5E">// if exclusive control, the value of concurrency column is required</span>
     * webCrawlingConfig.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;WebCrawlingConfigCB&gt; option = new UpdateOption&lt;WebCrawlingConfigCB&gt;();
     *     option.self(new SpecifyQuery&lt;WebCrawlingConfigCB&gt;() {
     *         public void specify(WebCrawlingConfigCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     webCrawlingConfigBhv.<span style="color: #DD4747">varyingUpdate</span>(webCrawlingConfig, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param webCrawlingConfig The entity of update. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * UpdateOption&lt;WebCrawlingConfigCB&gt; option = new UpdateOption&lt;WebCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void specify(WebCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">varyingUpdateNonstrict</span>(webCrawlingConfig, option);
     * </pre>
     * @param webCrawlingConfig The entity of update. (NotNull, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
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
     * @param webCrawlingConfig The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(
            final WebCrawlingConfig webCrawlingConfig,
            final InsertOption<WebCrawlingConfigCB> insertOption,
            final UpdateOption<WebCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdate(webCrawlingConfig, insertOption, updateOption);
    }

    /**
     * Insert or update the entity with varying requests non-strictly. (NonExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdateNonstrict(entity).
     * @param webCrawlingConfig The entity of insert or update. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdateNonstrict(
            final WebCrawlingConfig webCrawlingConfig,
            final InsertOption<WebCrawlingConfigCB> insertOption,
            final UpdateOption<WebCrawlingConfigCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInsertOrUpdateNonstrict(webCrawlingConfig, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, ExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param webCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception EntityDuplicatedException When the entity has been duplicated.
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
     * @param webCrawlingConfig The entity of delete. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
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
     * <span style="color: #3F7E5E">// you don't need to set a value of concurrency column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//webCrawlingConfig.setVersionNo(value);</span>
     * WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;WebCrawlingConfigCB&gt; option = new UpdateOption&lt;WebCrawlingConfigCB&gt;();
     * option.self(new SpecifyQuery&lt;WebCrawlingConfigCB&gt;() {
     *     public void specify(WebCrawlingConfigCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * webCrawlingConfigBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(webCrawlingConfig, cb, option);
     * </pre>
     * @param webCrawlingConfig The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of WebCrawlingConfig. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
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
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
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
    //                                                                Optimistic Lock Info
    //                                                                ====================
    @Override
    protected boolean hasVersionNoValue(final Entity et) {
        return downcast(et).getVersionNo() != null;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected Class<WebCrawlingConfig> typeOfSelectedEntity() {
        return WebCrawlingConfig.class;
    }

    protected WebCrawlingConfig downcast(final Entity et) {
        return helpEntityDowncastInternally(et, WebCrawlingConfig.class);
    }

    protected WebCrawlingConfigCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb,
                WebCrawlingConfigCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<WebCrawlingConfig> downcast(final List<? extends Entity> ls) {
        return (List<WebCrawlingConfig>) ls;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<WebCrawlingConfigCB> downcast(
            final InsertOption<? extends ConditionBean> op) {
        return (InsertOption<WebCrawlingConfigCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<WebCrawlingConfigCB> downcast(
            final UpdateOption<? extends ConditionBean> op) {
        return (UpdateOption<WebCrawlingConfigCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<WebCrawlingConfigCB> downcast(
            final DeleteOption<? extends ConditionBean> op) {
        return (DeleteOption<WebCrawlingConfigCB>) op;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> sp) {
        return (QueryInsertSetupper<WebCrawlingConfig, WebCrawlingConfigCB>) sp;
    }
}
