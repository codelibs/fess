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

import jp.sf.fess.db.cbean.CrawlingSessionInfoCB;
import jp.sf.fess.db.exbhv.CrawlingSessionBhv;
import jp.sf.fess.db.exentity.CrawlingSession;
import jp.sf.fess.db.exentity.CrawlingSessionInfo;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.NestedReferrerLoaderGateway;
import org.seasar.dbflute.bhv.ReferrerListHandler;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;

/**
 * The referrer loader of CRAWLING_SESSION as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SESSION_ID, NAME, EXPIRED_TIME, CREATED_TIME
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
 *     CRAWLING_SESSION_INFO
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     crawlingSessionInfoList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfCrawlingSession {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<CrawlingSession> _selectedList;

    protected BehaviorSelector _selector;

    protected CrawlingSessionBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfCrawlingSession ready(
            final List<CrawlingSession> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected CrawlingSessionBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(CrawlingSessionBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    protected List<CrawlingSessionInfo> _referrerCrawlingSessionInfoList;

    public NestedReferrerLoaderGateway<LoaderOfCrawlingSessionInfo> loadCrawlingSessionInfoList(
            final ConditionBeanSetupper<CrawlingSessionInfoCB> setupper) {
        myBhv().loadCrawlingSessionInfoList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<CrawlingSessionInfo>() {
                            @Override
                            public void handle(
                                    final List<CrawlingSessionInfo> referrerList) {
                                _referrerCrawlingSessionInfoList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfCrawlingSessionInfo>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfCrawlingSessionInfo> handler) {
                handler.handle(new LoaderOfCrawlingSessionInfo().ready(
                        _referrerCrawlingSessionInfoList, _selector));
            }
        };
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<CrawlingSession> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
