/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.bsbhv.loader;

import java.util.List;

import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.cbean.WebAuthenticationCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.WebCrawlingConfigBhv;
import jp.sf.fess.db.exentity.RequestHeader;
import jp.sf.fess.db.exentity.WebAuthentication;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.NestedReferrerLoaderGateway;
import org.seasar.dbflute.bhv.ReferrerListHandler;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;

/**
 * The referrer loader of WEB_CRAWLING_CONFIG as TABLE. <br />
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
public class LoaderOfWebCrawlingConfig {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<WebCrawlingConfig> _selectedList;

    protected BehaviorSelector _selector;

    protected WebCrawlingConfigBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfWebCrawlingConfig ready(
            final List<WebCrawlingConfig> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected WebCrawlingConfigBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(WebCrawlingConfigBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    protected List<RequestHeader> _referrerRequestHeaderList;

    public NestedReferrerLoaderGateway<LoaderOfRequestHeader> loadRequestHeaderList(
            final ConditionBeanSetupper<RequestHeaderCB> setupper) {
        myBhv().loadRequestHeaderList(_selectedList, setupper)
                .withNestedReferrer(new ReferrerListHandler<RequestHeader>() {
                    @Override
                    public void handle(final List<RequestHeader> referrerList) {
                        _referrerRequestHeaderList = referrerList;
                    }
                });
        return new NestedReferrerLoaderGateway<LoaderOfRequestHeader>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfRequestHeader> handler) {
                handler.handle(new LoaderOfRequestHeader().ready(
                        _referrerRequestHeaderList, _selector));
            }
        };
    }

    protected List<WebAuthentication> _referrerWebAuthenticationList;

    public NestedReferrerLoaderGateway<LoaderOfWebAuthentication> loadWebAuthenticationList(
            final ConditionBeanSetupper<WebAuthenticationCB> setupper) {
        myBhv().loadWebAuthenticationList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<WebAuthentication>() {
                            @Override
                            public void handle(
                                    final List<WebAuthentication> referrerList) {
                                _referrerWebAuthenticationList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfWebAuthentication>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfWebAuthentication> handler) {
                handler.handle(new LoaderOfWebAuthentication().ready(
                        _referrerWebAuthenticationList, _selector));
            }
        };
    }

    protected List<WebConfigToLabelTypeMapping> _referrerWebConfigToLabelTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfWebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> setupper) {
        myBhv().loadWebConfigToLabelTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<WebConfigToLabelTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<WebConfigToLabelTypeMapping> referrerList) {
                                _referrerWebConfigToLabelTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfWebConfigToLabelTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfWebConfigToLabelTypeMapping> handler) {
                handler.handle(new LoaderOfWebConfigToLabelTypeMapping().ready(
                        _referrerWebConfigToLabelTypeMappingList, _selector));
            }
        };
    }

    protected List<WebConfigToRoleTypeMapping> _referrerWebConfigToRoleTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfWebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> setupper) {
        myBhv().loadWebConfigToRoleTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<WebConfigToRoleTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<WebConfigToRoleTypeMapping> referrerList) {
                                _referrerWebConfigToRoleTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfWebConfigToRoleTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfWebConfigToRoleTypeMapping> handler) {
                handler.handle(new LoaderOfWebConfigToRoleTypeMapping().ready(
                        _referrerWebConfigToRoleTypeMappingList, _selector));
            }
        };
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<WebCrawlingConfig> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
