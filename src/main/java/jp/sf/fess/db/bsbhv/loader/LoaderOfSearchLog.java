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

import jp.sf.fess.db.cbean.ClickLogCB;
import jp.sf.fess.db.cbean.SearchFieldLogCB;
import jp.sf.fess.db.exbhv.SearchLogBhv;
import jp.sf.fess.db.exentity.ClickLog;
import jp.sf.fess.db.exentity.SearchFieldLog;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.NestedReferrerLoaderGateway;
import org.seasar.dbflute.bhv.ReferrerListHandler;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;

/**
 * The referrer loader of SEARCH_LOG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SEARCH_WORD, REQUESTED_TIME, RESPONSE_TIME, HIT_COUNT, QUERY_OFFSET, QUERY_PAGE_SIZE, USER_AGENT, REFERER, CLIENT_IP, USER_SESSION_ID, ACCESS_TYPE, USER_ID
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
 *     USER_INFO
 *
 * [referrer table]
 *     CLICK_LOG, SEARCH_FIELD_LOG
 *
 * [foreign property]
 *     userInfo
 *
 * [referrer property]
 *     clickLogList, searchFieldLogList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfSearchLog {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<SearchLog> _selectedList;

    protected BehaviorSelector _selector;

    protected SearchLogBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfSearchLog ready(final List<SearchLog> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected SearchLogBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(SearchLogBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    protected List<ClickLog> _referrerClickLogList;

    public NestedReferrerLoaderGateway<LoaderOfClickLog> loadClickLogList(
            final ConditionBeanSetupper<ClickLogCB> setupper) {
        myBhv().loadClickLogList(_selectedList, setupper).withNestedReferrer(
                new ReferrerListHandler<ClickLog>() {
                    @Override
                    public void handle(final List<ClickLog> referrerList) {
                        _referrerClickLogList = referrerList;
                    }
                });
        return new NestedReferrerLoaderGateway<LoaderOfClickLog>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfClickLog> handler) {
                handler.handle(new LoaderOfClickLog().ready(
                        _referrerClickLogList, _selector));
            }
        };
    }

    protected List<SearchFieldLog> _referrerSearchFieldLogList;

    public NestedReferrerLoaderGateway<LoaderOfSearchFieldLog> loadSearchFieldLogList(
            final ConditionBeanSetupper<SearchFieldLogCB> setupper) {
        myBhv().loadSearchFieldLogList(_selectedList, setupper)
                .withNestedReferrer(new ReferrerListHandler<SearchFieldLog>() {
                    @Override
                    public void handle(final List<SearchFieldLog> referrerList) {
                        _referrerSearchFieldLogList = referrerList;
                    }
                });
        return new NestedReferrerLoaderGateway<LoaderOfSearchFieldLog>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfSearchFieldLog> handler) {
                handler.handle(new LoaderOfSearchFieldLog().ready(
                        _referrerSearchFieldLogList, _selector));
            }
        };
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfUserInfo _foreignUserInfoLoader;

    public LoaderOfUserInfo pulloutUserInfo() {
        if (_foreignUserInfoLoader != null) {
            return _foreignUserInfoLoader;
        }
        final List<UserInfo> pulledList = myBhv()
                .pulloutUserInfo(_selectedList);
        _foreignUserInfoLoader = new LoaderOfUserInfo().ready(pulledList,
                _selector);
        return _foreignUserInfoLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<SearchLog> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
