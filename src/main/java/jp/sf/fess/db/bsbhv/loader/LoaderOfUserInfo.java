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

import jp.sf.fess.db.cbean.FavoriteLogCB;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.exbhv.UserInfoBhv;
import jp.sf.fess.db.exentity.FavoriteLog;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.NestedReferrerLoaderGateway;
import org.seasar.dbflute.bhv.ReferrerListHandler;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;

/**
 * The referrer loader of USER_INFO as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, CODE, CREATED_TIME, UPDATED_TIME
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
 *
 *
 * [referrer table]
 *     FAVORITE_LOG, SEARCH_LOG
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     favoriteLogList, searchLogList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfUserInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<UserInfo> _selectedList;

    protected BehaviorSelector _selector;

    protected UserInfoBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfUserInfo ready(final List<UserInfo> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected UserInfoBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(UserInfoBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    protected List<FavoriteLog> _referrerFavoriteLogList;

    public NestedReferrerLoaderGateway<LoaderOfFavoriteLog> loadFavoriteLogList(
            final ConditionBeanSetupper<FavoriteLogCB> setupper) {
        myBhv().loadFavoriteLogList(_selectedList, setupper)
                .withNestedReferrer(new ReferrerListHandler<FavoriteLog>() {
                    @Override
                    public void handle(final List<FavoriteLog> referrerList) {
                        _referrerFavoriteLogList = referrerList;
                    }
                });
        return new NestedReferrerLoaderGateway<LoaderOfFavoriteLog>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfFavoriteLog> handler) {
                handler.handle(new LoaderOfFavoriteLog().ready(
                        _referrerFavoriteLogList, _selector));
            }
        };
    }

    protected List<SearchLog> _referrerSearchLogList;

    public NestedReferrerLoaderGateway<LoaderOfSearchLog> loadSearchLogList(
            final ConditionBeanSetupper<SearchLogCB> setupper) {
        myBhv().loadSearchLogList(_selectedList, setupper).withNestedReferrer(
                new ReferrerListHandler<SearchLog>() {
                    @Override
                    public void handle(final List<SearchLog> referrerList) {
                        _referrerSearchLogList = referrerList;
                    }
                });
        return new NestedReferrerLoaderGateway<LoaderOfSearchLog>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfSearchLog> handler) {
                handler.handle(new LoaderOfSearchLog().ready(
                        _referrerSearchLogList, _selector));
            }
        };
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<UserInfo> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
