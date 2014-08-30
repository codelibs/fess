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

package jp.sf.fess.db.bsbhv.loader;

import java.util.List;

import jp.sf.fess.db.exbhv.CrawlingSessionInfoBhv;
import jp.sf.fess.db.exentity.CrawlingSession;
import jp.sf.fess.db.exentity.CrawlingSessionInfo;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of CRAWLING_SESSION_INFO as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, CRAWLING_SESSION_ID, KEY, VALUE, CREATED_TIME
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
 *     CRAWLING_SESSION
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     crawlingSession
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfCrawlingSessionInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<CrawlingSessionInfo> _selectedList;

    protected BehaviorSelector _selector;

    protected CrawlingSessionInfoBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfCrawlingSessionInfo ready(
            final List<CrawlingSessionInfo> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected CrawlingSessionInfoBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(CrawlingSessionInfoBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfCrawlingSession _foreignCrawlingSessionLoader;

    public LoaderOfCrawlingSession pulloutCrawlingSession() {
        if (_foreignCrawlingSessionLoader != null) {
            return _foreignCrawlingSessionLoader;
        }
        final List<CrawlingSession> pulledList = myBhv()
                .pulloutCrawlingSession(_selectedList);
        _foreignCrawlingSessionLoader = new LoaderOfCrawlingSession().ready(
                pulledList, _selector);
        return _foreignCrawlingSessionLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<CrawlingSessionInfo> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
