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

import jp.sf.fess.db.exbhv.WebAuthenticationBhv;
import jp.sf.fess.db.exentity.WebAuthentication;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of WEB_AUTHENTICATION as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, HOSTNAME, PORT, AUTH_REALM, PROTOCOL_SCHEME, USERNAME, PASSWORD, PARAMETERS, WEB_CRAWLING_CONFIG_ID, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     WEB_CRAWLING_CONFIG
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     webCrawlingConfig
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfWebAuthentication {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<WebAuthentication> _selectedList;

    protected BehaviorSelector _selector;

    protected WebAuthenticationBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfWebAuthentication ready(
            final List<WebAuthentication> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected WebAuthenticationBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(WebAuthenticationBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfWebCrawlingConfig _foreignWebCrawlingConfigLoader;

    public LoaderOfWebCrawlingConfig pulloutWebCrawlingConfig() {
        if (_foreignWebCrawlingConfigLoader != null) {
            return _foreignWebCrawlingConfigLoader;
        }
        final List<WebCrawlingConfig> pulledList = myBhv()
                .pulloutWebCrawlingConfig(_selectedList);
        _foreignWebCrawlingConfigLoader = new LoaderOfWebCrawlingConfig()
                .ready(pulledList, _selector);
        return _foreignWebCrawlingConfigLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<WebAuthentication> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
