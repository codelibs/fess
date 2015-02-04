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

import jp.sf.fess.db.exbhv.ClickLogBhv;
import jp.sf.fess.db.exentity.ClickLog;
import jp.sf.fess.db.exentity.SearchLog;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of CLICK_LOG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SEARCH_ID, URL, REQUESTED_TIME
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
 *     SEARCH_LOG
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     searchLog
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfClickLog {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<ClickLog> _selectedList;

    protected BehaviorSelector _selector;

    protected ClickLogBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfClickLog ready(final List<ClickLog> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected ClickLogBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(ClickLogBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfSearchLog _foreignSearchLogLoader;

    public LoaderOfSearchLog pulloutSearchLog() {
        if (_foreignSearchLogLoader != null) {
            return _foreignSearchLogLoader;
        }
        final List<SearchLog> pulledList = myBhv().pulloutSearchLog(
                _selectedList);
        _foreignSearchLogLoader = new LoaderOfSearchLog().ready(pulledList,
                _selector);
        return _foreignSearchLogLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<ClickLog> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
